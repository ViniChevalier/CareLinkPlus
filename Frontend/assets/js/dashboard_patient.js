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
        const appointmentElements = data.map(app => {
          const date = new Date(app.dateTime);
          const formattedDate = date.toLocaleString('en-IE', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
          });

          let doctorName = app.doctorName || "N/A";

          const statusColorMap = {
            BOOKED: 'bg-primary',
            CANCELLED: 'bg-danger',
            SCHEDULED: 'bg-secondary',
            COMPLETED: 'bg-success',
            NO_SHOW: 'bg-danger',
            PENDING: 'bg-light text-dark',
            CONFIRMED: 'bg-info'
          };

          const statusClass = statusColorMap[app.status?.toUpperCase()] || 'bg-secondary';

          return `
            <div class="mb-2">
              <strong>${formattedDate}</strong><br>
              <strong>Doctor:</strong> ${doctorName}<br>
              Type: ${app.type}<br>
              Status: <span class="badge ${statusClass}">${app.status}</span>
            </div>
          `;
        });

        el.innerHTML = appointmentElements.join("");
      }
    })
    .catch(error => {
      document.getElementById("appointments").innerHTML = "<p>Error loading appointments.</p>";
    });

  getMedicalHistoryByPatient(patientId)
    .then(async data => {
      const el = document.getElementById("prescriptions");

      if (!data || data.length === 0) {
        el.innerHTML = "<p>No prescriptions found.</p>";
      } else {
        const lastRecord = data[data.length - 1];

        let doctorName = "N/A";
        if (lastRecord.doctorId) {
          try {
            const profile = await getProfileById(lastRecord.doctorId);
            doctorName = `${profile.firstName || ""} ${profile.lastName || ""}`.trim();
          } catch (error) {
            console.error("Error fetching doctor profile for prescription:", error);
          }
        }

        let prescriptionDetails = {};
        try {
          prescriptionDetails = JSON.parse(lastRecord.prescriptions);
        } catch (e) {
          console.warn("Prescription is not in JSON format, displaying as plain text.");
          prescriptionDetails = { raw: lastRecord.prescriptions };
        }

        el.innerHTML = `
          <p>Below you can find the details of your latest prescription provided by your doctor:</p>
          <div class="border rounded p-2 mb-2">
            <span class="badge bg-primary mb-1">Latest Prescription</span><br>
            <strong>Doctor:</strong> ${doctorName}<br>
            ${prescriptionDetails.medication ? `<strong>Medication:</strong> ${prescriptionDetails.medication}<br>` : ""}
            ${prescriptionDetails.dosage ? `<strong>Dosage:</strong> ${prescriptionDetails.dosage}<br>` : ""}
            ${prescriptionDetails.frequency ? `<strong>Frequency:</strong> ${prescriptionDetails.frequency}<br>` : ""}
            ${prescriptionDetails.startDate ? `<strong>Start Date:</strong> ${new Date(prescriptionDetails.startDate).toLocaleDateString('en-IE', { day: '2-digit', month: '2-digit', year: 'numeric' })}<br>` : ""}
            ${prescriptionDetails.raw ? `<strong>Prescription:</strong> ${prescriptionDetails.raw}<br>` : ""}
            <strong>Notes:</strong> ${lastRecord.notes}<br>
            ${lastRecord.attachmentUrl ? `<a href="${lastRecord.attachmentUrl}" target="_blank">View Attachment</a><br>` : ""}
          </div>
        `;
      }
    })
    .catch(error => {
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
              <strong>Diagnosis:</strong><br>
              ${(() => {
                try {
                  const d = JSON.parse(lastRecord.diagnosis);
                  return `
                    ${d.preExisting ? `<strong>Pre-Existing:</strong> ${d.preExisting}<br>` : ""}
                    ${d.surgeries ? `<strong>Surgeries:</strong> ${d.surgeries}<br>` : ""}
                    ${d.allergies ? `<strong>Allergies:</strong> ${d.allergies}<br>` : ""}
                    ${d.familyHistory ? `<strong>Family History:</strong> ${d.familyHistory}<br>` : ""}
                  `;
                } catch {
                  return `${lastRecord.diagnosis}<br>`;
                }
              })()}
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
  const btn = e.target;
  const originalText = btn.innerHTML;
  btn.disabled = true;
  btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Loading...';

  const modal = new bootstrap.Modal(document.getElementById("prescriptionsModal"));
  const cardsContainer = document.getElementById("prescriptionsCards");
  cardsContainer.innerHTML = "<p>Loading prescriptions...</p>";

  const patientId = localStorage.getItem("userId");
  if (!patientId) {
    cardsContainer.innerHTML = "<p>Patient ID not found.</p>";
    modal.show();
    btn.innerHTML = originalText;
    btn.disabled = false;
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
            const profile = await getProfileById(record.doctorId);
            doctorName = `${profile.firstName || ""} ${profile.lastName || ""}`.trim();
          } catch (error) {
            console.error("Error fetching doctor profile for prescription modal card:", error);
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
        let prescriptionDetails = {};
        try {
          prescriptionDetails = JSON.parse(record.prescriptions);
        } catch (e) {
          console.warn("Prescription is not in JSON format, displaying as plain text.");
          prescriptionDetails = { raw: record.prescriptions };
        }

        card.innerHTML = `
          <div class="card shadow-sm h-100">
            <div class="card-body">
              <h5 class="card-title">Prescription #${record.recordId}</h5>
              ${createdAt ? `<p class="card-text"><strong>Created At:</strong> ${createdAt}</p>` : ""}
              <p class="card-text"><strong>Doctor:</strong> ${doctorName}</p>
              <p class="card-text"><strong>Notes:</strong> ${record.notes || 'N/A'}</p>
              ${prescriptionDetails.medication ? `<p class="card-text"><strong>Medication:</strong> ${prescriptionDetails.medication}</p>` : ""}
              ${prescriptionDetails.dosage ? `<p class="card-text"><strong>Dosage:</strong> ${prescriptionDetails.dosage}</p>` : ""}
              ${prescriptionDetails.frequency ? `<p class="card-text"><strong>Frequency:</strong> ${prescriptionDetails.frequency}</p>` : ""}
              ${prescriptionDetails.startDate ? `<p class="card-text"><strong>Start Date:</strong> ${new Date(prescriptionDetails.startDate).toLocaleDateString('en-IE', { day: '2-digit', month: '2-digit', year: 'numeric' })}</p>` : ""}
              ${prescriptionDetails.raw ? `<p class="card-text"><strong>Prescription:</strong> ${prescriptionDetails.raw}</p>` : ""}
              ${lastUpdated ? `<p class="card-text"><strong>Last Updated:</strong> ${lastUpdated}</p>` : ""}
              ${record.attachmentUrl ? `<p class="card-text"><a href="${record.attachmentUrl}" target="_blank">View Attachment</a></p>` : ""}
            </div>
          </div>
        `;
        cardsContainer.appendChild(card);
      }
    }
    modal.show();
    btn.innerHTML = originalText;
    btn.disabled = false;
  } catch (error) {
    console.error("Error loading prescriptions:", error);
    cardsContainer.innerHTML = "<p>Error loading prescriptions.</p>";
    btn.innerHTML = originalText;
    btn.disabled = false;
  }
});

document.getElementById("viewFullHistoryBtn").addEventListener("click", async function (e) {
  e.preventDefault();
  const btn = e.target;
  const originalText = btn.innerHTML;
  btn.disabled = true;
  btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Loading...';

  const modal = new bootstrap.Modal(document.getElementById("historyModal"));
  const cardsContainer = document.getElementById("historyCards");
  cardsContainer.innerHTML = "<p>Loading medical history...</p>";

  const patientId = localStorage.getItem("userId");
  if (!patientId) {
    cardsContainer.innerHTML = "<p>Patient ID not found.</p>";
    modal.show();
    btn.innerHTML = originalText;
    btn.disabled = false;
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
              doctorName = record.updatedBy;
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
                <p class="card-text"><strong>Diagnosis:</strong><br>
                ${
                  (() => {
                    try {
                      const d = JSON.parse(record.diagnosis);
                      return `
                        ${d.preExisting ? `<strong>Pre-Existing:</strong> ${d.preExisting}<br>` : ""}
                        ${d.surgeries ? `<strong>Surgeries:</strong> ${d.surgeries}<br>` : ""}
                        ${d.allergies ? `<strong>Allergies:</strong> ${d.allergies}<br>` : ""}
                        ${d.familyHistory ? `<strong>Family History:</strong> ${d.familyHistory}<br>` : ""}
                      `;
                    } catch {
                      return `${record.diagnosis || 'N/A'}<br>`;
                    }
                  })()
                }
                </p>
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
    modal.show();
    btn.innerHTML = originalText;
    btn.disabled = false;
  } catch (error) {
    console.error("Error loading medical history:", error);
    cardsContainer.innerHTML = "<p>Error loading medical history.</p>";
    btn.innerHTML = originalText;
    btn.disabled = false;
  }
});