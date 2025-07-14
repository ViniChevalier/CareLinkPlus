import { get, del } from './apiService.js';

document.addEventListener("DOMContentLoaded", () => {
  const appointmentSelect = document.getElementById("appointmentSelect");
  const form = document.getElementById("cancelAppointmentForm");
  const messageDiv = document.getElementById("message");

  const userId = localStorage.getItem("userId");
  const token = localStorage.getItem("token");

  // Carregar appointments do usuário
  get(`/api/appointments?userId=${userId}`)
    .then(async (appointments) => {
      if (appointments.length === 0) {
        appointmentSelect.innerHTML = `<option value="">No appointments found</option>`;
        return;
      }

      appointmentSelect.innerHTML = `<option value="">Select an appointment</option>`;

      for (const app of appointments) {
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
    })
    .catch(error => {
      console.error("Error loading appointments:", error);
      appointmentSelect.innerHTML = `<option value="">Error loading appointments</option>`;
    });

  // Submeter o cancelamento
  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const appointmentId = appointmentSelect.value;

    if (!appointmentId) {
      showMessage("Please select an appointment to cancel.", "danger");
      return;
    }

    del(`/api/appointments/${appointmentId}`)
      .then(() => {
        showMessage("Appointment cancelled successfully!", "success");

        const optionToRemove = appointmentSelect.querySelector(`option[value="${appointmentId}"]`);
        if (optionToRemove) {
          optionToRemove.remove();
        }

        appointmentSelect.value = "";
      })
      .catch(error => {
        console.error("Error cancelling appointment:", error);
        showMessage("Error cancelling appointment. Please try again.", "danger");
      });
  });

  function showMessage(msg, type) {
    messageDiv.className = `alert alert-${type} text-center`;
    messageDiv.textContent = msg;
    messageDiv.classList.remove("d-none");
    setTimeout(() => {
      messageDiv.classList.add("d-none");
    }, 3000);
  }
});