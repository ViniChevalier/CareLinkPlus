import { get, put, getAllAvailability, getAppointmentsByPatient } from './apiService.js';

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

  // Load user's existing appointments
  getAppointmentsByPatient(userId)
    .then((appointments) => {
      const bookedAppointments = appointments.filter(app => app.status === "BOOKED");
      if (bookedAppointments.length === 0) {
        appointmentSelect.innerHTML = `<option value="">No appointments found</option>`;
        return;
      }

      appointmentSelect.innerHTML = `<option value="">Select an appointment</option>`;

      (async () => {
        for (const app of bookedAppointments) {
          const option = document.createElement("option");
          option.value = app.appointmentId;

          let doctorName = "Unknown";
          let slotInfo = "No slot info";

          try {
            const profile = await get(`/api/account/profile/${app.doctorId}`);
            doctorName = `Dr. ${profile.firstName} ${profile.lastName}`;
          } catch (error) {
            console.error(`Error fetching doctor profile for ID ${app.doctorId}:`, error);
          }

          try {
            if (!app.availabilityId || app.availabilityId === 0) {
              console.warn(`Appointment ${app.appointmentId} has no availabilityId. Skipping.`);
              continue; 
            }

            const availability = await get(`/api/availability/${app.availabilityId}`);
            const date = new Date(`${availability.availableDate}T${availability.startTime}`);

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

            const formattedEndTime = new Date(`${availability.availableDate}T${availability.endTime}`).toLocaleTimeString("en-IE", {
              hour: "2-digit",
              minute: "2-digit",
              hour12: false
            });

            slotInfo = `${formattedDate} — ${formattedStartTime} to ${formattedEndTime}`;
          } catch (error) {
            console.error(`Error fetching availability for ID ${app.availabilityId}:`, error);
          }

          option.textContent = `${slotInfo} with ${doctorName}`;
          appointmentSelect.appendChild(option);
        }
      })();
    })
    .catch(error => {
      console.error("Error loading appointments:", error);
      appointmentSelect.innerHTML = `<option value="">Error loading appointments</option>`;
    });

  // When appointment is selected, show doctor select
  appointmentSelect.addEventListener("change", () => {
    if (!appointmentSelect.value) {
      doctorGroup.classList.add("d-none");
      slotGroup.classList.add("d-none");
      submitBtn.classList.add("d-none");
      return;
    }

    // Load doctors using available slots logic
    getAllAvailability()
      .then(async (availabilities) => {
        const activeAvailabilities = availabilities.filter(slot => !slot.isBooked && slot.status === "AVAILABLE");
        const doctorMap = new Map();

        for (const slot of activeAvailabilities) {
          if (!doctorMap.has(slot.doctorId)) {
            try {
              const profile = await get(`/api/account/profile/${slot.doctorId}`);
              const fullName = `${profile.firstName} ${profile.lastName}`;
              doctorMap.set(slot.doctorId, fullName);
            } catch (error) {
              console.error(`Error fetching profile for doctor ID ${slot.doctorId}:`, error);
            }
          }
        }

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

  // Load slots for selected doctor
  doctorSelect.addEventListener("change", () => {
    const doctorId = doctorSelect.value;
    if (!doctorId) {
      slotGroup.classList.add("d-none");
      submitBtn.classList.add("d-none");
      return;
    }

    get(`/api/availability/doctor/${doctorId}`)
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
          option.value = slot.id;

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
      })
      .catch(error => {
        console.error("Error loading slots:", error);
        slotSelect.innerHTML = `<option value="">Error loading slots</option>`;
      });
  });

  // Handle form submission
  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const appointmentId = appointmentSelect.value;
    const doctorId = doctorSelect.value;
    const slotId = slotSelect.value;

    if (!appointmentId || !doctorId || !slotId) {
      showMessage("Please fill all fields.", "danger");
      return;
    }

    const payload = {
      doctorId: parseInt(doctorId),
      availabilityId: parseInt(slotId),
      reason: "Rescheduled by patient"
    };

    put(`/api/appointments/${appointmentId}`, payload)
      .then(() => {
        showMessage("Appointment rescheduled successfully!", "success");
        form.reset();
        doctorGroup.classList.add("d-none");
        slotGroup.classList.add("d-none");
        submitBtn.classList.add("d-none");
      })
      .catch(error => {
        console.error("Error rescheduling appointment:", error);
        showMessage("Error rescheduling. Please try again.", "danger");
      });
  });

  function showMessage(msg, type) {
    messageDiv.className = `alert alert-${type} text-center`;
    messageDiv.textContent = msg;
    messageDiv.classList.remove("d-none");
  }
});