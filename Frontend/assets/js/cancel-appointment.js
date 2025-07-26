import { get, cancelAppointment, getAppointmentsByPatient } from './apiService.js';

document.addEventListener("DOMContentLoaded", () => {
  const appointmentSelect = document.getElementById("appointmentSelect");
  const form = document.getElementById("cancelAppointmentForm");
  const messageDiv = document.getElementById("message");
  const submitBtn = form.querySelector("button[type='submit']");

  const token = localStorage.getItem("token");

  const patientId = localStorage.getItem("userId");
  if (!patientId) {
    console.error("Patient ID is missing in localStorage");
    appointmentSelect.innerHTML = `<option value="">Patient not found</option>`;
    return;
  }

  appointmentSelect.innerHTML = `<option disabled selected>Loading appointments...</option>`;
  appointmentSelect.disabled = true;

  getAppointmentsByPatient(patientId)
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

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    submitBtn.disabled = true;
    submitBtn.textContent = "Cancelling...";
    const appointmentId = appointmentSelect.value;

    if (!appointmentId) {
      showMessage("Please select an appointment to cancel.", "danger");
      submitBtn.disabled = false;
      submitBtn.textContent = "Cancel Appointment";
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
        submitBtn.disabled = false;
        submitBtn.textContent = "Cancel Appointment";
      })
      .catch(error => {
        console.error("Error cancelling appointment:", error);
        showMessage("Error cancelling appointment. Please try again.", "danger");
        submitBtn.disabled = false;
        submitBtn.textContent = "Cancel Appointment";
      });
  });

  function showMessage(message, type = "info") {
    let toastContainer = document.getElementById("toast-top-right");
    if (!toastContainer) {
      toastContainer = document.createElement("div");
      toastContainer.id = "toast-top-right";
      toastContainer.className = "toast-container position-fixed top-0 end-0 p-3";
      document.body.appendChild(toastContainer);
    }

    const toastElement = document.createElement("div");
    toastElement.className = `toast align-items-center text-white bg-${type} border-0 mb-2 animate__animated animate__fadeInDown`;
    toastElement.setAttribute("role", "alert");
    toastElement.setAttribute("aria-live", "assertive");
    toastElement.setAttribute("aria-atomic", "true");

    toastElement.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">${message}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    `;

    toastContainer.appendChild(toastElement);
    const bsToast = new bootstrap.Toast(toastElement);
    bsToast.show();

    setTimeout(() => {
      toastElement.remove();
    }, 5000);
  }
});