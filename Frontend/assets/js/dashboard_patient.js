import { getAppointmentsByPatient, getMedicalHistoryByPatient, getPatientHistoriesByPatient, getProfileById } from './apiService.js';
import { loadPatientName } from './userProfile.js';

document.addEventListener("DOMContentLoaded", function () {
  loadPatientName();

  const patientId = localStorage.getItem("userId");
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
        el.innerHTML = data.map(app => {
          const date = new Date(app.appointmentDateTime);
          const formattedDate = date.toLocaleString('en-IE', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
          });

          return `
            <div class="mb-2">
              <strong>${formattedDate}</strong><br>
              Reason: ${app.reason}<br>
              Status: <span class="badge bg-info">${app.appointmentStatus}</span>
            </div>
          `;
        }).join("");
      }
    })
    .catch(error => {
      document.getElementById("appointments").innerHTML = "<p>Error loading appointments.</p>";
    });

  getMedicalHistoryByPatient(patientId)
    .then(async data => {
      console.log("Prescription API called successfully. Data:", data);
      const el = document.getElementById("prescriptions");

      if (!data || data.length === 0) {
        el.innerHTML = "<p>No prescriptions found.</p>";
      } else {
        const lastRecord = data[data.length - 1];

        let doctorName = "N/A";
        if (lastRecord.doctorId) {
          try {
            const profileData = await getProfileById(lastRecord.doctorId);
            doctorName = `${profileData.firstName || ""} ${profileData.lastName || ""}`.trim();
          } catch (error) {
            console.error("Error fetching doctor profile:", error);
          }
        }

        el.innerHTML = `
          <p>Below you can find the details of your latest prescription provided by your doctor:</p>
          <div class="border rounded p-2 mb-2">
            <span class="badge bg-primary mb-1">Latest Prescription</span><br>
            <strong>Doctor:</strong> ${doctorName}<br>
            <strong>Prescription:</strong> ${lastRecord.prescriptions}<br>
            <strong>Notes:</strong> ${lastRecord.notes}<br>
            ${lastRecord.attachmentUrl ? `<a href="${lastRecord.attachmentUrl}" target="_blank">View Attachment</a><br>` : ""}
          </div>
        `;
      }
    })
    .catch(error => {
      console.error("Error calling prescriptions API:", error);
      document.getElementById("prescriptions").innerHTML = "<p>Error loading prescriptions.</p>";
    });

 getPatientHistoriesByPatient(patientId)
    .then(data => {
      const el = document.getElementById("medicalHistory");

      if (!data || data.length === 0) {
        el.innerHTML = "<p>No medical history found.</p>";
      } else {
        // Filter for active status
        const activeHistories = data.filter(record => record.status === "Active" || record.status === "active");
        if (activeHistories.length === 0) {
          el.innerHTML = "<p>No active medical history found.</p>";
        } else {
          // Get the last active record
          const lastRecord = activeHistories[activeHistories.length - 1];
          
          const createdAt = lastRecord.createdAt
            ? new Date(lastRecord.createdAt).toLocaleString('en-IE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
              })
            : "N/A";

          const lastUpdated = lastRecord.lastUpdated
            ? new Date(lastRecord.lastUpdated).toLocaleString('en-IE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
              })
            : "N/A";

          el.innerHTML = `
            <p>Here is the most recent active medical history record for your reference:</p>
            <div class="border rounded p-2 mb-2">
              <span class="badge bg-primary mb-1">Latest Active Medical History</span><br>
              <strong>Description:</strong> ${lastRecord.description}<br>
              <strong>Diagnosis:</strong> ${lastRecord.diagnosis}<br>
              <strong>Created At:</strong> ${createdAt}<br>
              <strong>Last Updated:</strong> ${lastUpdated}<br>
            </div>
          `;
        }
      }
    })
    .catch(error => {
      console.error("Error fetching medical history:", error);
      document.getElementById("medicalHistory").innerHTML = "<p>Error loading medical history.</p>";
    });
});

import { logout } from './logout.js';

document.getElementById("logout-link").addEventListener("click", function (e) {
  e.preventDefault();
  logout();
});