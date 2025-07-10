import { getAppointmentsByPatient } from './apiService.js';
import { loadPatientName } from './userProfile.js';

document.addEventListener("DOMContentLoaded", function () {
  loadPatientName();

  const patientId = localStorage.getItem("patientId");
  if (!patientId) {
    document.getElementById("appointments").innerHTML = "<p>Patient ID not found.</p>";
    return;
  }

  getAppointmentsByPatient(patientId)
    .then(data => {
      const el = document.getElementById("appointments");
      if (data.length === 0) {
        el.innerHTML = "<p>No upcoming appointments.</p>";
      } else {
        el.innerHTML = data.map(app => `
          <div class="mb-2">
            <strong>${app.appointmentDateTime}</strong><br>
            Reason: ${app.reason}
          </div>
        `).join("");
      }
    })
    .catch(error => {
      document.getElementById("appointments").innerHTML = "<p>Error loading appointments.</p>";
    });

  // Placeholder for medications
  // You will need to implement getMedications() in apiService.js
  /*
  getMedications()
    .then(data => {
      const el = document.getElementById("medications");
      if (data.length === 0) {
        el.innerHTML = "<p>No current medications.</p>";
      } else {
        el.innerHTML = data.map(med => `
          <div>
            ${med.name} - ${med.dosage}
          </div>
        `).join("");
      }
    })
    .catch(error => {
      document.getElementById("medications").innerHTML = "<p>Error loading medications.</p>";
    });
  */
});