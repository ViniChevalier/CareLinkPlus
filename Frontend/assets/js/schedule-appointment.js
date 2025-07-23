import {
  getAllAvailability,
  createAppointment
} from './apiService.js';

document.addEventListener("DOMContentLoaded", () => {
  const doctorSelect = document.getElementById("doctorSelect");
  const slotSelect = document.getElementById("slotSelect");
  const form = document.getElementById("scheduleAppointmentForm");
  const messageDiv = document.getElementById("message");

  const patientId = localStorage.getItem("userId");

  // Load doctors with active availability
  getAllAvailability()
    .then((availabilities) => {
      const doctorMap = new Map();
      availabilities.forEach(slot => {
        if (!doctorMap.has(slot.doctorId)) {
          doctorMap.set(slot.doctorId, slot.doctorName);
        }
      });

      doctorSelect.innerHTML = `<option value="">Select a doctor</option>`;
      doctorMap.forEach((name, id) => {
        const option = document.createElement("option");
        option.value = id;
        option.textContent = name;
        doctorSelect.appendChild(option);
      });

      if (doctorMap.size === 0) {
        doctorSelect.innerHTML = `<option value="">No doctors available</option>`;
      }
    })
    .catch(error => {
      console.error("Error loading doctors:", error);
      doctorSelect.innerHTML = `<option value="">Error loading doctors</option>`;
    });

  // Load available slots when doctor is selected
  doctorSelect.addEventListener("change", () => {
    const doctorId = doctorSelect.value;
    if (!doctorId) {
      slotSelect.disabled = true;
      slotSelect.innerHTML = `<option value="">Please select a doctor first</option>`;
      return;
    }

    getAllAvailability()
      .then((availabilities) => {
        const activeSlots = availabilities.filter(slot =>
          slot.doctorId == doctorId &&
          slot.status === "AVAILABLE"
        );
        activeSlots.sort((a, b) => {
          const dateA = new Date(`${a.availableDate}T${a.startTime}`);
          const dateB = new Date(`${b.availableDate}T${b.startTime}`);
          return dateA - dateB;
        });
        slotSelect.innerHTML = `<option value="">Select a slot</option>`;
        activeSlots.forEach(slot => {
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
        slotSelect.disabled = false;
      })
      .catch(error => {
        console.error("Error loading slots:", error);
        slotSelect.innerHTML = `<option value="">Error loading slots</option>`;
        slotSelect.disabled = true;
      });
  });

  // Handle form submission
  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const availabilityId = slotSelect.value;
    const reason = document.getElementById("reason").value;

    if (!patientId || !availabilityId || !reason) {
      showMessage("Please fill all required fields.", "danger");
      return;
    }

    const payload = {
      patientId,
      availabilityId,
      reason,
    };

    createAppointment(payload)
      .then(() => {
        showMessage("Appointment scheduled successfully!", "success");
        form.reset();
        doctorSelect.value = "";
        slotSelect.disabled = true;
        slotSelect.innerHTML = `<option value="">Please select a doctor first</option>`;
      })
      .catch(error => {
        console.error("Error scheduling appointment:", error);
        showMessage("Error scheduling appointment. Please try again.", "danger");
      });
  });

  function showMessage(msg, type) {
    messageDiv.className = `alert alert-${type} text-center`;
    messageDiv.textContent = msg;
    messageDiv.classList.remove("d-none");
  }
});