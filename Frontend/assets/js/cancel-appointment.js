import { get, cancelAppointment, getAppointmentsByPatient } from './apiService.js';

document.addEventListener("DOMContentLoaded", () => {
  const appointmentSelect = document.getElementById("appointmentSelect");
  const form = document.getElementById("cancelAppointmentForm");
  const messageDiv = document.getElementById("message");

  const token = localStorage.getItem("token");

  // Carregar appointments do usuário
  const patientId = localStorage.getItem("userId");
  if (!patientId) {
    console.error("Patient ID is missing in localStorage");
    appointmentSelect.innerHTML = `<option value="">Patient not found</option>`;
    return;
  }

  appointmentSelect.innerHTML = `<option disabled selected>Loading appointments...</option>`;
  appointmentSelect.disabled = true;

  get(`/api/appointments/patient/${patientId}`)
    .then(async (appointments) => {
      const bookedAppointments = appointments.filter(app => app.status === "Scheduled" || app.status === "Confirmed");

      bookedAppointments.sort((a, b) => {
        const dateA = new Date(a.dateTime);
        const dateB = new Date(b.dateTime);
        return dateA - dateB;
      });

      if (bookedAppointments.length === 0) {
        appointmentSelect.innerHTML = `<option value="">No active appointments found</option>`;
        appointmentSelect.disabled = false;
        return;
      }

      appointmentSelect.innerHTML = `<option value="">Select an appointment</option>`;

      for (const app of bookedAppointments) {
        const option = document.createElement("option");
        option.value = app.id;

        const date = new Date(app.dateTime);

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

        const endDateTime = new Date(app.dateTime);
        endDateTime.setHours(...app.endTime.split(':').map(Number));
        const formattedEndTime = endDateTime.toLocaleTimeString("en-IE", {
          hour: "2-digit",
          minute: "2-digit",
          hour12: false
        });

        const slotInfo = `${formattedDate} — ${formattedStartTime} to ${formattedEndTime}`;
        option.textContent = `${slotInfo} with ${app.doctorName}`;
        appointmentSelect.appendChild(option);
      }
      appointmentSelect.disabled = false;
    })
    .catch(error => {
      console.error("Error loading appointments:", error);
      appointmentSelect.innerHTML = `<option value="">Error loading appointments</option>`;
      appointmentSelect.disabled = false;
    });

  // Submeter o cancelamento
  form.addEventListener("submit", (e) => {
    e.preventDefault();
    const appointmentId = appointmentSelect.value;

    if (!appointmentId) {
      showMessage("Please select an appointment to cancel.", "danger");
      return;
    }

    cancelAppointment(appointmentId)
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