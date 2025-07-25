import { get, put, post, getAllAvailability, getAppointmentsByPatient, cancelAppointment, createAppointment } from './apiService.js';

document.addEventListener("DOMContentLoaded", () => {
  const appointmentSelect = document.getElementById("appointmentSelect");
  const doctorSelect = document.getElementById("doctorSelect");
  const slotSelect = document.getElementById("slotSelect");
  const form = document.getElementById("rescheduleAppointmentForm");
  const messageDiv = document.getElementById("message");
  const doctorGroup = document.getElementById("doctorGroup");
  const slotGroup = document.getElementById("slotGroup");
  const submitBtn = document.getElementById("submitBtn");

  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");

  appointmentSelect.innerHTML = `<option disabled selected>Loading appointments...</option>`;
  appointmentSelect.disabled = true;

  getAppointmentsByPatient(userId)
    .then((appointments) => {
      const bookedAppointments = appointments
        .filter(app => app.status === "Scheduled" || app.status === "Confirmed")
        .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));
      if (bookedAppointments.length === 0) {
        appointmentSelect.innerHTML = `<option value="">No appointments found</option>`;
        appointmentSelect.disabled = false;
        return;
      }

      appointmentSelect.innerHTML = `<option value="">Select an appointment</option>`;

      (async () => {
        for (const app of bookedAppointments) {
          const option = document.createElement("option");
          option.value = app.id;

          let doctorName = "Unknown";
          let slotInfo = "No slot info";

          doctorName = `Dr. ${app.doctorName}`;

          if (!app.availabilityId || app.availabilityId === 0 || !app.availableDate || !app.startTime || !app.endTime) {
            console.warn(`Appointment ${app.id} missing availability data. Skipping.`);
            continue;
          }

          const date = new Date(`${app.availableDate.split("/").reverse().join("-")}T${app.startTime}`);

          const formattedDate = date.toLocaleDateString("en-IE", {
            weekday: "long",
            day: "numeric",
            month: "long",
            year: "numeric"
          });

          const formattedStartTime = date.toLocaleTimeString("en-IE", {
            hour: "2-digit",
            minute: "2-digit",
            hour12: false
          });

          const formattedEndTime = new Date(`${app.availableDate.split("/").reverse().join("-")}T${app.endTime}`).toLocaleTimeString("en-IE", {
            hour: "2-digit",
            minute: "2-digit",
            hour12: false
          });

          slotInfo = `${formattedDate} — ${formattedStartTime} to ${formattedEndTime}`;

          option.textContent = `${slotInfo} with ${doctorName}`;
          appointmentSelect.appendChild(option);
        }
        appointmentSelect.disabled = false;
      })();
    })
    .catch(error => {
      console.error("Error loading appointments:", error);
      appointmentSelect.innerHTML = `<option value="">Error loading appointments</option>`;
      appointmentSelect.disabled = false;
    });

  appointmentSelect.addEventListener("change", () => {
    if (!appointmentSelect.value) {
      doctorGroup.classList.add("d-none");
      slotGroup.classList.add("d-none");
      submitBtn.classList.add("d-none");
      return;
    }

    doctorGroup.classList.remove("d-none");

    getAllAvailability()
      .then(async (availabilities) => {
        const activeAvailabilities = availabilities;
        const doctorMap = new Map();

        for (const slot of activeAvailabilities) {
          if (!doctorMap.has(slot.doctorId) && slot.doctorName) {
            doctorMap.set(slot.doctorId, slot.doctorName);
          }
        }

        doctorSelect.innerHTML = ``;
        doctorSelect.innerHTML = `<option value="">Select a doctor</option>`;
        doctorMap.forEach((name, id) => {
          const option = document.createElement("option");
          option.value = id;
          option.textContent = name;
          doctorSelect.appendChild(option);
        });

        if (doctorMap.size > 0) {
          doctorGroup.classList.remove("d-none");
        } else {
          doctorSelect.innerHTML = `<option value="">No doctors available</option>`;
        }
      })
      .catch(error => {
        console.error("Error loading doctors:", error);
        doctorSelect.innerHTML = `<option value="">Error loading doctors</option>`;
      });
  });

  doctorSelect.addEventListener("change", () => {
    const doctorId = doctorSelect.value;
    if (!doctorId) {
      slotGroup.classList.add("d-none");
      submitBtn.classList.add("d-none");
      return;
    }

    slotSelect.innerHTML = `<option>Loading...</option>`;
    slotSelect.disabled = true;

    getDoctorAvailability(doctorId)
      .then((slots) => {
        const now = new Date();
        const availableSlots = slots.filter(slot => {
          const start = new Date(`${slot.availableDate}T${slot.startTime}`);
          return !slot.isBooked && slot.status === "AVAILABLE" && start >= now;
        });
        availableSlots.sort((a, b) => {
          const dateA = new Date(`${a.availableDate}T${a.startTime}`);
          const dateB = new Date(`${b.availableDate}T${b.startTime}`);
          return dateA - dateB;
        });
        slotSelect.innerHTML = `<option value="">Select a slot</option>`;
        availableSlots.forEach(slot => {
          const option = document.createElement("option");
          option.value = slot.availabilityId;

          const date = new Date(`${slot.availableDate}T${slot.startTime}`);
          const formattedDate = date.toLocaleDateString("en-IE", {
            weekday: "long",
            day: "numeric",
            month: "long",
            year: "numeric"
          });

          const formattedStartTime = date.toLocaleTimeString("en-IE", {
            hour: "2-digit",
            minute: "2-digit",
            hour12: false
          });

          const formattedEndTime = new Date(`${slot.availableDate}T${slot.endTime}`).toLocaleTimeString("en-IE", {
            hour: "2-digit",
            minute: "2-digit",
            hour12: false
          });

          option.textContent = `${formattedDate} — ${formattedStartTime} to ${formattedEndTime}`;
          slotSelect.appendChild(option);
        });

        if (availableSlots.length > 0) {
          slotGroup.classList.remove("d-none");
          submitBtn.classList.remove("d-none");
        } else {
          slotSelect.innerHTML = `<option value="">No slots available</option>`;
          slotGroup.classList.remove("d-none");
          submitBtn.classList.add("d-none");
        }
        slotSelect.disabled = false;
      })
      .catch(error => {
        console.error("Error loading slots:", error);
        slotSelect.innerHTML = `<option value="">Error loading slots</option>`;
        slotSelect.disabled = false;
      });
  });

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    submitBtn.disabled = true;
    submitBtn.textContent = "Rescheduling...";

    const appointmentId = appointmentSelect.value;
    const doctorId = doctorSelect.value;
    const slotId = slotSelect.value;

    if (!appointmentId || !doctorId || !slotId || slotId === "undefined") {
      showMessage("Please fill all fields correctly.", "danger");
      submitBtn.disabled = false;
      submitBtn.textContent = "Reschedule";
      return;
    }

    rescheduleAppointment(appointmentId, doctorId, slotId);
  });

});

function showMessage(msg, type) {
  const messageDiv = document.getElementById("message");
  messageDiv.className = `alert alert-${type} text-center`;
  messageDiv.textContent = msg;
  messageDiv.classList.remove("d-none");
}

function rescheduleAppointment(currentAppointmentId, doctorId, slotId) {
  const userId = localStorage.getItem("userId");
  if (
    !currentAppointmentId ||
    !doctorId ||
    !slotId ||
    isNaN(parseInt(doctorId)) ||
    isNaN(parseInt(slotId))
  ) {
    showMessage("Error: Invalid data for rescheduling.", "danger");
    console.warn("Invalid data:", {
      currentAppointmentId,
      doctorId,
      slotId
    });
    submitBtn.disabled = false;
    submitBtn.textContent = "Reschedule";
    return;
  }

  cancelAppointment(currentAppointmentId)
    .then(() => {
      const availabilityId = parseInt(slotId);
      const payload = {
        doctorId: parseInt(doctorId),
        availabilityId: availabilityId,
        patientId: parseInt(userId),
        reason: "Rescheduled by patient"
      };

      return createAppointment(payload);
    })
    .then(() => {
      showMessage("Appointment successfully rescheduled!", "success");
      submitBtn.disabled = false;
      submitBtn.textContent = "Reschedule";
    })
    .catch((error) => {
      console.error("Error during rescheduling:", error);
      showMessage("Failed to reschedule appointment. Please try again later.", "danger");
      submitBtn.disabled = false;
      submitBtn.textContent = "Reschedule";
    });
}