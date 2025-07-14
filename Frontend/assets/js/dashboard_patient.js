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
    .then(async data => {
      const el = document.getElementById("appointments");
      if (data.length === 0) {
        el.innerHTML = "<p>No upcoming appointments.</p>";
      } else {
        const appointmentElements = await Promise.all(data.map(async app => {
          const date = new Date(app.appointmentDateTime);
          const formattedDate = date.toLocaleString('en-IE', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
          });

          let doctorName = "N/A";
          if (app.doctorId) {
            try {
              const profileData = await getProfileById(app.doctorId);
              doctorName = `${profileData.firstName || ""} ${profileData.lastName || ""}`.trim();
            } catch (error) {
              console.error("Error fetching doctor profile:", error);
            }
          }

          return `
            <div class="mb-2">
              <strong>${formattedDate}</strong><br>
              <strong>Doctor:</strong> ${doctorName}<br>
              Reason: ${app.reason}<br>
              Status: <span class="badge bg-info">${app.appointmentStatus}</span>
            </div>
          `;
        }));

        el.innerHTML = appointmentElements.join("");
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

document.getElementById("logout-link").addEventListener("click", function (e) {
  e.preventDefault();
  logout();
});

// Prescriptions modal and cards display logic
document.getElementById("viewPrescriptionsBtn").addEventListener("click", async function (e) {
  e.preventDefault();

  const modal = new bootstrap.Modal(document.getElementById("prescriptionsModal"));
  const cardsContainer = document.getElementById("prescriptionsCards");
  cardsContainer.innerHTML = "<p>Loading prescriptions...</p>";

  const patientId = localStorage.getItem("userId");
  if (!patientId) {
    cardsContainer.innerHTML = "<p>Patient ID not found.</p>";
    modal.show();
    return;
  }

  try {
    const data = await getMedicalHistoryByPatient(patientId);

    cardsContainer.innerHTML = "";

    if (!data || data.length === 0) {
      cardsContainer.innerHTML = "<p>No prescriptions found.</p>";
    } else {
      for (const record of data) {
        let doctorName = "N/A";
        if (record.doctorId) {
          try {
            const profileData = await getProfileById(record.doctorId);
            doctorName = `${profileData.firstName || ""} ${profileData.lastName || ""}`.trim();
          } catch (error) {
            console.error("Error fetching doctor profile:", error);
          }
        }

        const lastUpdated = record.lastUpdated
          ? new Date(record.lastUpdated).toLocaleString('en-IE', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
          })
          : null;

        const createdAt = record.createdAt
          ? new Date(record.createdAt).toLocaleString('en-IE', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
          })
          : null;

        const card = document.createElement("div");
        card.className = "col-md-6";
        card.innerHTML = `
          <div class="card shadow-sm h-100">
            <div class="card-body">
              <h5 class="card-title">Prescription #${record.recordId}</h5>
              ${createdAt ? `<p class="card-text"><strong>Created At:</strong> ${createdAt}</p>` : ""}
              <p class="card-text"><strong>Doctor:</strong> ${doctorName}</p>
              <p class="card-text"><strong>Notes:</strong> ${record.notes || 'N/A'}</p>
              <p class="card-text"><strong>Prescription:</strong> ${record.prescriptions || 'N/A'}</p>
              ${lastUpdated ? `<p class="card-text"><strong>Last Updated:</strong> ${lastUpdated}</p>` : ""}
              ${record.attachmentUrl ? `<p class="card-text"><a href="${record.attachmentUrl}" target="_blank">View Attachment</a></p>` : ""}


            </div>
          </div>
        `;
        cardsContainer.appendChild(card);
      }
    }
  } catch (error) {
    console.error("Error loading prescriptions:", error);
    cardsContainer.innerHTML = "<p>Error loading prescriptions.</p>";
  }

  modal.show();
});

document.getElementById("viewFullHistoryBtn").addEventListener("click", async function (e) {
  e.preventDefault();

  const modal = new bootstrap.Modal(document.getElementById("historyModal"));
  const cardsContainer = document.getElementById("historyCards");
  cardsContainer.innerHTML = "<p>Loading medical history...</p>";

  const patientId = localStorage.getItem("userId");
  if (!patientId) {
    cardsContainer.innerHTML = "<p>Patient ID not found.</p>";
    modal.show();
    return;
  }

  try {
    const data = await getPatientHistoriesByPatient(patientId);

    cardsContainer.innerHTML = "";

    if (!data || data.length === 0) {
      cardsContainer.innerHTML = "<p>No medical history found.</p>";
    } else {
      // Filter active records
      const activeHistories = data.filter(record => record.status === "Active" || record.status === "active");

      if (activeHistories.length === 0) {
        cardsContainer.innerHTML = "<p>No active medical history found.</p>";
      } else {
        for (const record of activeHistories) {
          let doctorName = "N/A";
          if (record.updatedBy) {
            try {
              const profileData = await getProfileById(record.updatedBy);
              doctorName = `${profileData.firstName || ""} ${profileData.lastName || ""}`.trim();
            } catch (error) {
              console.error("Error fetching doctor profile:", error);
              doctorName = record.updatedBy; // fallback to raw value
            }
          }

          let doctorProfileName = "N/A";
          if (record.doctorId) {
            try {
              const doctorProfile = await getProfileById(record.doctorId);
              doctorProfileName = `${doctorProfile.firstName || ""} ${doctorProfile.lastName || ""}`.trim();
            } catch (error) {
              console.error("Error fetching doctor profile:", error);
            }
          }

          const createdAt = record.createdAt
            ? new Date(record.createdAt).toLocaleString('en-IE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
              })
            : null;

          const lastUpdated = record.lastUpdated
            ? new Date(record.lastUpdated).toLocaleString('en-IE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
              })
            : null;

          const diagnosisDate = record.diagnosisDate
            ? new Date(record.diagnosisDate).toLocaleDateString('en-IE', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric'
              })
            : "N/A";

          const card = document.createElement("div");
          card.className = "col-md-6";
          card.innerHTML = `
            <div class="card shadow-sm h-100">
              <div class="card-body">
                <h5 class="card-title">History #${record.historyId}</h5>
                ${createdAt ? `<p class="card-text"><strong>Created At:</strong> ${createdAt}</p>` : ""}
                <p class="card-text"><strong>Doctor:</strong> ${doctorProfileName}</p>
                <p class="card-text"><strong>Diagnosis Date:</strong> ${diagnosisDate}</p>
                <p class="card-text"><strong>Diagnosis:</strong> ${record.diagnosis || 'N/A'}</p>
                <p class="card-text"><strong>Description:</strong> ${record.description || 'N/A'}</p>           
                ${lastUpdated ? `<p class="card-text"><strong>Last Updated:</strong> ${lastUpdated}</p>` : ""}
                <p class="card-text"><strong>Updated By:</strong> ${doctorName}</p>
                ${record.attachmentUrl ? `<p class="card-text"><a href="${record.attachmentUrl}" target="_blank" download>Download Attachment</a></p>` : ""}
              </div>
            </div>
          `;
          cardsContainer.appendChild(card);
        }
      }
    }
  } catch (error) {
    console.error("Error loading medical history:", error);
    cardsContainer.innerHTML = "<p>Error loading medical history.</p>";
  }

  modal.show();
});