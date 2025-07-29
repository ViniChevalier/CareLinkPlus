import {
  getProfileById,
  getMedicalHistoryByPatient,
  getAppointmentsByPatient,
  getPatientHistoriesByPatient,
  createPatientHistory,
  createMedicalRecord
} from './apiService.js';

const STATUS_COLORS = {
  AVAILABLE: "bg-warning",
  BOOKED: "bg-warning",
  EXPIRED: "bg-secondary",
  CANCELLED: "bg-danger",
  SCHEDULED: "bg-warning",
  COMPLETED: "bg-success",
  NO_SHOW: "bg-danger",
  PENDING: "bg-warning",
  CONFIRMED: "bg-success",
  ATTENDED: "bg-info"
};

const STATUS_LABELS = {
  AVAILABLE: "Available",
  BOOKED: "Booked",
  EXPIRED: "Expired",
  CANCELLED: "Cancelled",
  SCHEDULED: "Scheduled",
  COMPLETED: "Completed",
  NO_SHOW: "No Show",
  PENDING: "Pending",
  CONFIRMED: "Confirmed",
  ATTENDED: "Attended"
};


async function loadMedicalHistory(patientId) {
  const spinner = document.getElementById("loadingSpinner");
  if (spinner) spinner.style.display = "block";
  console.log("==> Calling loadMedicalHistory for patientId:", patientId);

  try {
    const historyList = await getPatientHistoriesByPatient(patientId);
    console.log("Received history list:", historyList);
    if (!Array.isArray(historyList) || historyList.length === 0) {
      console.log("No history entries available.");
      return;
    }

    const latest = historyList.reduce((latest, current) => {
      const currentDate = new Date(current.diagnosisDate || current.createdAt || 0);
      const latestDate = new Date(latest.diagnosisDate || latest.createdAt || 0);
      return currentDate > latestDate ? current : latest;
    });

    console.log("Using latest history entry:", latest);

    if (latest.diagnosis && latest.diagnosis.trim().startsWith("{")) {
      try {
        const diag = JSON.parse(latest.diagnosis);
        const pe = document.getElementById("viewPreExistingConditions");
        const su = document.getElementById("viewPastSurgeries");
        const al = document.getElementById("viewAllergies");
        const fh = document.getElementById("viewFamilyHistory");

        console.log("Parsed Diagnosis:", diag);

        if (pe) {
          pe.textContent = diag.preExisting || '--';
          console.log("Updated preExisting:", pe.textContent);
        }
        if (su) {
          su.textContent = diag.surgeries || '--';
          console.log("Updated surgeries:", su.textContent);
        }
        if (al) {
          al.textContent = diag.allergies || '--';
          console.log("Updated allergies:", al.textContent);
        }
        if (fh) {
          fh.textContent = diag.familyHistory || '--';
          console.log("Updated familyHistory:", fh.textContent);
        }
      } catch (e) {
        console.warn("Invalid JSON in diagnosis:", latest.diagnosis);
      }
    } else {
      const pe = document.getElementById("viewPreExistingConditions");
      if (pe) pe.textContent = latest.diagnosis || '--';
    }

    const desc = document.getElementById("viewDiagnosisDescription");
    if (desc) desc.textContent = latest.description || 'No description.';
    const date = document.getElementById("viewDiagnosisDate");
    if (date) date.textContent = latest.diagnosisDate ? new Date(latest.diagnosisDate).toLocaleDateString() : 'N/A';
    const att = document.getElementById("viewDiagnosisAttachment");
    if (att) {
      if (latest.attachmentUrl) {
        att.innerHTML = `<a href="${latest.attachmentUrl}" target="_blank">View Attachment</a>`;
      } else {
        att.textContent = '';
      }
    }
    const status = document.getElementById("viewDiagnosisStatus");
    if (status) status.textContent = latest.status || 'Active';

    const doctorSpan = document.querySelector(".doctor-name[data-doctor-id]");
    if (doctorSpan) {
      const id = doctorSpan.getAttribute("data-doctor-id") || latest.doctorId;
      console.log(`Fetching doctor profile for ID: "${id}"`);
      if (!id || id === "undefined") {
        console.warn(`Skipped fetching profile for invalid ID: "${id}"`);
        doctorSpan.textContent = "Doctor: N/A";
      } else {
        try {
          getProfileById(id).then(doctor => {
            const fullName = `${doctor.firstName || ''} ${doctor.lastName || ''}`.trim();
            doctorSpan.textContent = fullName ? `Doctor: ${fullName}` : "Doctor: N/A";
          }).catch(error => {
            console.error(`Failed to load doctor with ID ${id}:`, error);
            doctorSpan.textContent = "Doctor: N/A";
          });
        } catch (error) {
          console.error(`Failed to load doctor with ID ${id}:`, error);
          doctorSpan.textContent = "Doctor: N/A";
        }
      }
    }
    if (spinner) spinner.style.display = "none";
  } catch (err) {
    console.error("Error loading medical history:", err);
    if (spinner) spinner.style.display = "none";
  }
}

async function loadUpcomingAppointments(patientId) {
  const spinner = document.getElementById("loadingSpinner");
  if (spinner) spinner.style.display = "block";
  try {
    const appointments = await getAppointmentsByPatient(patientId);
    const list = document.getElementById("upcomingAppointmentsList");
    if (!list) return;
    list.innerHTML = '';

    const upcoming = appointments
      .filter(a => new Date(a.dateTime) >= new Date())
      .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime))
      .slice(0, 5);

    if (upcoming.length === 0) {
      list.innerHTML = '<li class="list-group-item text-muted">No future appointments.</li>';
      return;
    }

    upcoming.forEach(app => {
      const li = document.createElement("li");
      li.className = "list-group-item";
      const statusBadgeColor = STATUS_COLORS[app.status] || "bg-secondary";
      li.innerHTML = `
        <div class="d-flex justify-content-between align-items-start">
          <div>
            <strong>${new Date(app.dateTime).toLocaleString('en-IE')}</strong><br/>
            <small>Doctor: ${app.doctorName || 'Unknown Doctor'}</small><br/>
            <span>Motivo: ${app.type || 'General Consultation'}</span>
          </div>
          <span class="badge ${statusBadgeColor}">${app.status || 'Scheduled'}</span>
        </div>
      `;
      list.appendChild(li);
    });

    if (appointments.length > 5) {
      const showMoreBtn = document.createElement("li");
      showMoreBtn.className = "list-group-item text-center";
      showMoreBtn.innerHTML = `<button class="btn btn-link">Show All Appointments</button>`;
      showMoreBtn.querySelector("button").addEventListener("click", () => {
        const modalWrapper = document.createElement("div");
        modalWrapper.className = "modal fade";
        modalWrapper.innerHTML = `
          <div class="modal-dialog modal-lg"><div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">All Future Appointments</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <ul class="list-group">
                ${appointments
            .filter(a => new Date(a.dateTime) >= new Date())
            .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime))
            .map(app => {
              const statusBadgeColor = STATUS_COLORS[app.status] || "bg-secondary";
              return `
                      <li class="list-group-item">
                        <div class="d-flex justify-content-between align-items-start">
                          <div>
                            <strong>${new Date(app.dateTime).toLocaleString('en-IE')}</strong><br/>
                            <small>Doctor: ${app.doctorName || 'Unknown Doctor'}</small><br/>
                            <span>Motivo: ${app.type || 'General Consultation'}</span>
                          </div>
                          <span class="badge ${statusBadgeColor}">${app.status || 'Scheduled'}</span>
                        </div>
                      </li>
                    `;
            }).join('')}
              </ul>
            </div>
          </div></div>
        `;
        document.body.appendChild(modalWrapper);
        const modal = new bootstrap.Modal(modalWrapper);
        modal.show();
      });
      list.appendChild(showMoreBtn);
    }
    if (spinner) spinner.style.display = "none";
  } catch (err) {
    console.error("Error loading appointments:", err);
    if (spinner) spinner.style.display = "none";
  }
}

async function loadPrescriptions(patientId) {
  const spinner = document.getElementById("loadingSpinner");
  if (spinner) spinner.style.display = "block";
  try {
    const prescriptions = await getMedicalHistoryByPatient(patientId);
    console.log("Received prescriptions:", prescriptions);
    const list = document.getElementById("prescriptionsList");
    if (!list) return;
    list.innerHTML = '';

    if (!prescriptions || prescriptions.length === 0) {
      list.innerHTML = '<li class="list-group-item text-muted">No active prescriptions.</li>';
      return;
    }

    prescriptions.sort((a, b) => {
      let dateA = new Date(JSON.parse(a.prescriptions || '{}').startDate || 0);
      let dateB = new Date(JSON.parse(b.prescriptions || '{}').startDate || 0);
      return dateB - dateA;
    });

    const latestPrescriptions = prescriptions.slice(0, 5);
    latestPrescriptions.forEach(p => {
      const li = document.createElement("li");
      li.className = "list-group-item list-group-item-action animate__animated animate__fadeInUp";
      let medInfo = {};
      try {
        medInfo = JSON.parse(p.prescriptions || '{}');
      } catch (err) {
        console.warn("Invalid JSON in prescriptions:", p.prescriptions);
      }

      li.innerHTML = `
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <strong>${medInfo.medication || 'Unnamed Medication'}</strong> - ${medInfo.dosage || 'N/A'} (${medInfo.frequency || 'N/A'})<br/>
            <small>Prescribed on: ${medInfo.startDate ? new Date(medInfo.startDate).toLocaleDateString() : 'Unknown date'}</small>
          </div>
        </div>
      `;
      list.appendChild(li);

      li.style.cursor = "pointer";
      li.style.transition = "background-color 0.3s ease";
      li.addEventListener("mouseenter", () => li.style.backgroundColor = "#f8f9fa");
      li.addEventListener("mouseleave", () => li.style.backgroundColor = "");

      li.addEventListener("click", () => {
        const modalWrapper = document.createElement("div");
        modalWrapper.className = "modal fade";

        let medInfo = {};
        try {
          medInfo = JSON.parse(p.prescriptions || '{}');
        } catch (err) {
          console.warn("Invalid JSON in prescriptions:", p.prescriptions);
        }

        modalWrapper.innerHTML = `
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title">Prescription Details</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
              </div>
              <div class="modal-body">
                <p><strong>Medication:</strong> ${medInfo.medication || 'N/A'}</p>
                <p><strong>Dosage:</strong> ${medInfo.dosage || 'N/A'}</p>
                <p><strong>Frequency:</strong> ${medInfo.frequency || 'N/A'}</p>
                <p><strong>Start Date:</strong> ${medInfo.startDate ? new Date(medInfo.startDate).toLocaleDateString() : 'N/A'}</p>
                <p><strong>Expiration Date:</strong> ${medInfo.expirationDate ? new Date(medInfo.expirationDate).toLocaleDateString() : 'N/A'}</p>
                <p><strong>Notes:</strong> ${p.notes || 'None'}</p>
                ${p.attachmentUrl ? `<p><strong>Attachment:</strong> <a href="${p.attachmentUrl}" target="_blank">Download</a></p>` : ''}
              </div>
            </div>
          </div>
        `;
        document.body.appendChild(modalWrapper);
        const modal = new bootstrap.Modal(modalWrapper);
        modal.show();
      });
    });

    if (prescriptions.length > 5) {
      const showMoreBtn = document.createElement("li");
      showMoreBtn.className = "list-group-item text-center";
      showMoreBtn.innerHTML = `
        <button class="btn btn-outline-primary d-flex align-items-center gap-2 px-4 py-2 animate__animated animate__fadeInUp">
          <i class="lni lni-folder"></i> Show All Prescriptions
        </button>`;
      showMoreBtn.querySelector("button").addEventListener("click", () => {
        const modalWrapper = document.createElement("div");
        modalWrapper.className = "modal fade";
        modalWrapper.innerHTML = `
          <div class="modal-dialog modal-lg"><div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">All Prescriptions</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <ul class="list-group">${prescriptions.map(p => {
          let medInfo = {};
          try {
            medInfo = JSON.parse(p.prescriptions || '{}');
          } catch { }
          return `
                  <li class="list-group-item list-group-item-action animate__animated animate__fadeInUp">
                    <strong>${medInfo.medication || 'Unnamed Medication'}</strong> - ${medInfo.dosage || 'N/A'} (${medInfo.frequency || 'N/A'})<br/>
                    <small>Prescribed on: ${medInfo.startDate ? new Date(medInfo.startDate).toLocaleDateString() : 'Unknown date'}</small>
                  </li>
                `;
        }).join('')}</ul>
            </div>
          </div></div>
        `;
        document.body.appendChild(modalWrapper);
        const modal = new bootstrap.Modal(modalWrapper);
        modal.show();
      });
      list.appendChild(showMoreBtn);
    }
    if (spinner) spinner.style.display = "none";
  } catch (err) {
    console.error("Error loading prescriptions:", err);
    if (spinner) spinner.style.display = "none";
  }
}

async function loadConsultationHistory(patientId) {
  const spinner = document.getElementById("loadingSpinner");
  if (spinner) spinner.style.display = "block";
  try {
    const appointments = await getAppointmentsByPatient(patientId);
    const list = document.getElementById("consultationHistoryList");
    if (!list) return;
    list.innerHTML = '';

    if (!appointments || appointments.length === 0) {
      list.innerHTML = '<li class="list-group-item text-muted">No past consultations found.</li>';
      return;
    }

    const pastAppointments = appointments
      .filter(app => new Date(app.dateTime) < new Date())
      .sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime))
      .slice(0, 5);

    pastAppointments.forEach(app => {
      const li = document.createElement("li");
      li.className = "list-group-item";
      const statusKey = (app.status || "COMPLETED").toUpperCase();
      const statusBadgeColor = STATUS_COLORS[statusKey] || "bg-secondary";
      const statusLabel = STATUS_LABELS[statusKey] || app.status;
      li.innerHTML = `
        <div class="d-flex justify-content-between align-items-start">
          <div>
            <strong>${new Date(app.dateTime).toLocaleString('en-IE')}</strong><br/>
            <small>Doctor: ${app.doctorName || 'Unknown Doctor'}</small><br/>
            <span>Motivo: ${app.type || 'General Consultation'}</span>
          </div>
          <span class="badge ${statusBadgeColor}">${statusLabel}</span>
        </div>
      `;
      list.appendChild(li);
    });

    if (appointments.length > 5) {
      const showMoreBtn = document.createElement("li");
      showMoreBtn.className = "list-group-item text-center";
      showMoreBtn.innerHTML = `<button class="btn btn-link">Show All Past Consultations</button>`;
      showMoreBtn.querySelector("button").addEventListener("click", () => {
        const modalWrapper = document.createElement("div");
        modalWrapper.className = "modal fade";
        modalWrapper.innerHTML = `
          <div class="modal-dialog modal-lg"><div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">All Past Consultations</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <ul class="list-group">
                ${appointments
            .filter(app => new Date(app.dateTime) < new Date())
            .sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime))
            .map(app => {
              const statusKey = (app.status || "COMPLETED").toUpperCase();
              const statusBadgeColor = STATUS_COLORS[statusKey] || "bg-secondary";
              const statusLabel = STATUS_LABELS[statusKey] || app.status;
              return `
                      <li class="list-group-item">
                        <div class="d-flex justify-content-between align-items-start">
                          <div>
                            <strong>${new Date(app.dateTime).toLocaleString('en-IE')}</strong><br/>
                            <small>Doctor: ${app.doctorName || 'Unknown Doctor'}</small><br/>
                            <span>Motivo: ${app.type || 'General Consultation'}</span>
                          </div>
                          <span class="badge ${statusBadgeColor}">${statusLabel}</span>
                        </div>
                      </li>
                    `;
            }).join('')}
              </ul>
            </div>
          </div></div>
        `;
        document.body.appendChild(modalWrapper);
        const modal = new bootstrap.Modal(modalWrapper);
        modal.show();
      });
      list.appendChild(showMoreBtn);
    }
    if (spinner) spinner.style.display = "none";
  } catch (err) {
    console.error("Error loading consultation history:", err);
    if (spinner) spinner.style.display = "none";
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  const patientId = localStorage.getItem("selectedPatientId");

  document.getElementById("patientId").textContent = patientId;

  try {
    const profile = await getProfileById(patientId);
    const fullName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
    document.getElementById("patientFullName").textContent = fullName;
    document.getElementById("patientDetails").textContent = profile.email || '';
    document.getElementById("dob").textContent = profile.dateOfBirth || '--';
    document.getElementById("gender").textContent = profile.gender || '--';
    document.getElementById("contact").textContent = profile.phoneNumber || '--';
  } catch (err) {
    console.error("Error fetching patient profile:", err);
    document.getElementById("patientFullName").textContent = "Unable to load patient data";
    document.getElementById("patientDetails").textContent = "An error occurred.";
  }

  loadMedicalHistory(patientId);
  loadUpcomingAppointments(patientId);
  loadPrescriptions(patientId);
  loadConsultationHistory(patientId);

  const btnAddHistory = document.getElementById("btnAddMedicalHistory");
  const btnAddPrescription = document.getElementById("btnAddPrescription");

  if (btnAddHistory) {
    btnAddHistory.addEventListener("click", () => {
      const wrapper = document.createElement("div");
      wrapper.className = "modal fade";
      wrapper.innerHTML = `
        <div class="modal-dialog modal-lg">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">Add Medical History</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <form id="historyForm" class="p-3">
                <div class="mb-2">
                  <label class="form-label">Diagnosis</label>
                  <input type="text" class="form-control" name="diagnosis" required>
                </div>
                <div class="mb-2">
                  <label class="form-label">Description</label>
                  <textarea class="form-control" name="description" rows="3"></textarea>
                </div>
                <div class="mb-2">
                  <label class="form-label">Pre-existing Conditions</label>
                  <textarea class="form-control" name="preExisting" rows="2" required></textarea>
                </div>
                <div class="mb-2">
                  <label class="form-label">Past Surgeries</label>
                  <textarea class="form-control" name="surgeries" rows="2" required></textarea>
                </div>
                <div class="mb-2">
                  <label class="form-label">Allergies</label>
                  <textarea class="form-control" name="allergies" rows="2" required></textarea>
                </div>
                <div class="mb-2">
                  <label class="form-label">Family History</label>
                  <textarea class="form-control" name="familyHistory" rows="2" required></textarea>
                </div>
                <div class="mb-2">
                  <label class="form-label">Diagnosis Date</label>
                  <input type="date" class="form-control" name="diagnosisDate">
                </div>
                <div class="mb-2">
                  <label class="form-label">Attachment (optional)</label>
                  <input type="file" class="form-control" name="file">
                </div>
                <button type="submit" class="btn btn-primary mt-2">Submit</button>
              </form>
            </div>
          </div>
        </div>
      `;
      document.body.appendChild(wrapper);
      const bsModal = new bootstrap.Modal(wrapper);
      bsModal.show();

      wrapper.querySelector("#historyForm").addEventListener("submit", async e => {
        e.preventDefault();
        const form = e.target;

        if (
          !form.preExisting.value.trim() ||
          !form.surgeries.value.trim() ||
          !form.allergies.value.trim() ||
          !form.familyHistory.value.trim()
        ) {
          toast("All clinical fields must be filled.", "danger");
          return;
        }

        const patientId = localStorage.getItem("selectedPatientId");
        const doctorId = localStorage.getItem("userId");

        const diagnosis = {
          preExisting: form.preExisting.value.trim(),
          surgeries: form.surgeries.value.trim(),
          allergies: form.allergies.value.trim(),
          familyHistory: form.familyHistory.value.trim()
        };

        const formData = new FormData();
        formData.append("patientId", patientId);
        formData.append("doctorId", doctorId);
        formData.append("diagnosis", JSON.stringify(diagnosis));
        formData.append("description", form.description.value || "");
        formData.append("diagnosisDate", form.diagnosisDate.value || "");
        formData.append("status", "Active");
        formData.append("historyId", "");
        if (form.file && form.file.files.length > 0) {
          formData.append("attachment", form.file.files[0]);
        }

        let modalFeedback = form.querySelector("#modalHistoryFeedback");
        if (!modalFeedback) {
          modalFeedback = document.createElement("div");
          modalFeedback.id = "modalHistoryFeedback";
          modalFeedback.className = "alert alert-danger d-none";
          form.querySelector("button[type='submit']").insertAdjacentElement("afterend", modalFeedback);
        }
        function showModalFeedback(message) {
          modalFeedback.textContent = message;
          modalFeedback.classList.remove("d-none");
        }

        try {
          const doctorId = localStorage.getItem("userId");
          const formObject = {
            patientId: parseInt(patientId),
            doctorId: parseInt(doctorId),
            diagnosis: JSON.stringify(diagnosis),
            description: form.description.value || "",
            status: "Active",
            diagnosisDate: form.diagnosisDate.value || "",
            file: form.file && form.file.files.length > 0 ? form.file.files[0] : null
          };
          console.log("Sending formObject to createPatientHistory:", formObject);
          await createPatientHistory(
            formObject.patientId,
            formObject.doctorId,
            formObject.diagnosis,
            formObject.description,
            formObject.status,
            formObject.diagnosisDate,
            formObject.file
          );
          toast("Medical history successfully saved.", "success");
          bsModal.hide();
          loadMedicalHistory(patientId);
        } catch (error) {
          console.error("Failed to create medical history:", error);
          const message = error?.message || "Failed to save history. Please check the data and try again.";
          toast(message, "danger");
        }
      });
    });
  }

  if (btnAddPrescription) {
    btnAddPrescription.addEventListener("click", () => {
      const formHtml = `
        <form id="prescriptionForm" class="p-3">
          <div class="mb-2">
            <label class="form-label">Medication</label>
            <input type="text" class="form-control" name="medication" required>
          </div>
          <div class="mb-2">
            <label class="form-label">Dosage</label>
            <input type="text" class="form-control" name="dosage" required>
          </div>
          <div class="mb-2">
            <label class="form-label">Frequency</label>
            <input type="text" class="form-control" name="frequency" required>
          </div>
          <div class="mb-2">
            <label class="form-label">Start Date</label>
            <input type="date" class="form-control" name="startDate">
          </div>
          <div class="mb-2">
            <label class="form-label">Notes</label>
            <textarea class="form-control" name="notes" rows="2"></textarea>
          </div>
          <div class="mb-2">
            <label class="form-label">Attachment (optional)</label>
            <input type="file" name="file" class="form-control">
          </div>
          <div id="modalPrescriptionFeedback" class="alert alert-danger d-none mt-2"></div>
          <button type="submit" class="btn btn-success mt-2">Submit</button>
        </form>
      `;

      const container = document.createElement("div");
      container.innerHTML = formHtml;

      const wrapper = document.createElement("div");
      wrapper.className = "modal fade";
      wrapper.innerHTML = `
        <div class="modal-dialog modal-lg"><div class="modal-content">
          <div class="modal-header"><h5 class="modal-title">Add Prescription</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
          </div>
          <div class="modal-body">${container.innerHTML}</div>
        </div></div>
      `;
      document.body.appendChild(wrapper);
      const bsModal = new bootstrap.Modal(wrapper);
      bsModal.show();

      wrapper.querySelector("form").addEventListener("submit", async e => {
        e.preventDefault();
        const form = e.target;
        if (!form.medication.value.trim() || !form.dosage.value.trim() || !form.frequency.value.trim()) {
          toast("Please fill out all required fields.", "danger");
          return;
        }
        const patientId = localStorage.getItem("selectedPatientId");
        const doctorId = localStorage.getItem("userId");

        const prescriptionPayload = {
          patientId: parseInt(patientId),
          doctorId: parseInt(doctorId),
          historyId: null,
          notes: form.notes ? form.notes.value.trim() : '',
          prescriptions: JSON.stringify({
            medication: form.medication.value.trim(),
            dosage: form.dosage.value.trim(),
            frequency: form.frequency.value.trim(),
            startDate: form.startDate.value
          })
        };

        const prescriptionFormData = new FormData();
        prescriptionFormData.append("patientId", prescriptionPayload.patientId);
        prescriptionFormData.append("doctorId", prescriptionPayload.doctorId);
        prescriptionFormData.append("notes", prescriptionPayload.notes);
        prescriptionFormData.append("prescriptions", prescriptionPayload.prescriptions);
        prescriptionFormData.append("historyId", "");
        if (form.file && form.file.files.length > 0) {
          prescriptionFormData.append("attachment", form.file.files[0]);
        }

        try {
          await createMedicalRecord(
            prescriptionPayload.patientId,
            prescriptionPayload.doctorId,
            prescriptionPayload.notes,
            prescriptionPayload.prescriptions,
            null,
            prescriptionPayload.historyId,
            form.file && form.file.files.length > 0 ? form.file.files[0] : null
          );
          toast("Prescription successfully registered.", "success");
          bsModal.hide();
          loadPrescriptions(patientId);
        } catch (err) {
          console.error("Error posting prescription:", err);
          const modalFeedback = wrapper.querySelector("#modalPrescriptionFeedback");
          if (modalFeedback) {
            modalFeedback.textContent = err?.message || "Failed to add prescription. Please check the data.";
            modalFeedback.classList.remove("d-none");
          } else {
            toast("Failed to add prescription.", "danger");
          }
        }
      });
    });
  }
});
async function loadMedicalHistoryModal() {
  const patientId = localStorage.getItem("selectedPatientId");
  const spinner = document.getElementById("medicalHistoryLoading");
  const tableBody = document.getElementById("medicalHistoryTableBody");

  if (!patientId || !tableBody) return;

  const theadRow = document.getElementById("medicalHistoryTableBody").previousElementSibling.querySelector("thead tr");
  if (theadRow) {
    theadRow.innerHTML = `
      <th>Diagnosis Date</th>
      <th>Diagnosis</th>
      <th>Description</th>
      <th>Status</th>
      <th>Pre-existing Conditions</th>
      <th>Past Surgeries</th>
      <th>Allergies</th>
      <th>Family History</th>
      <th>Attachment</th>
      <th>Doctor Name</th>
    `;
  }

  if (spinner) spinner.classList.remove("d-none");
  tableBody.innerHTML = '';

  try {
    const historyList = await getPatientHistoriesByPatient(patientId);
    if (!historyList || historyList.length === 0) {
      tableBody.innerHTML = '<tr><td colspan="10" class="text-center text-muted">No records available.</td></tr>';
    } else {
      historyList.forEach(history => {
        let preExisting = '-', surgeries = '-', allergies = '-', familyHistory = '-';
        if (history.diagnosis && typeof history.diagnosis === 'string' && history.diagnosis.startsWith("{")) {
          try {
            const diagObj = JSON.parse(history.diagnosis);
            preExisting = diagObj.preExisting || '-';
            surgeries = diagObj.surgeries || '-';
            allergies = diagObj.allergies || '-';
            familyHistory = diagObj.familyHistory || '-';
          } catch (err) {
            console.warn("Failed to parse diagnosis JSON:", history.diagnosis);
          }
        }
        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${history.diagnosisDate ? new Date(history.diagnosisDate).toLocaleDateString() : '-'}</td>
          <td>${history.diagnosis ? (typeof history.diagnosis === 'string' && history.diagnosis.startsWith("{") ? 'Detailed' : history.diagnosis) : '-'}</td>
          <td>${history.description || '-'}</td>
          <td>${history.status || '-'}</td>
          <td>${preExisting}</td>
          <td>${surgeries}</td>
          <td>${allergies}</td>
          <td>${familyHistory}</td>
          <td>${history.attachmentUrl ? `<a href="${history.attachmentUrl}" target="_blank">View</a>` : '-'}</td>
          <td data-doctor-id="${history.doctorId}">Loading...</td>
        `;
        tableBody.appendChild(row);
      });
      const doctorCells = tableBody.querySelectorAll("td[data-doctor-id]");
      doctorCells.forEach(async cell => {
        const doctorId = cell.getAttribute("data-doctor-id");
        try {
          const profile = await getProfileById(doctorId);
          const fullName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
          cell.textContent = fullName || 'Unknown Doctor';
        } catch (err) {
          console.warn("Could not fetch doctor profile:", err);
          cell.textContent = 'Unknown Doctor';
        }
      });
    }
  } catch (error) {
    console.error("Failed to load history for modal:", error);
    tableBody.innerHTML = '<tr><td colspan="10" class="text-center text-danger">Failed to load data.</td></tr>';
  } finally {
    if (spinner) spinner.classList.add("d-none");
  }
}
async function loadPrescriptionHistory() {
  const patientId = localStorage.getItem("selectedPatientId");
  const spinner = document.getElementById("prescriptionHistoryLoading");
  const tableBody = document.getElementById("prescriptionHistoryTableBody");

  if (!patientId || !tableBody) return;

  if (spinner) spinner.classList.remove("d-none");
  tableBody.innerHTML = '';

  try {
    const prescriptions = await getMedicalHistoryByPatient(patientId);
    const theadRow = document.getElementById("prescriptionHistoryTableBody").previousElementSibling.querySelector("thead tr");
    if (theadRow) {
      theadRow.innerHTML = `
        <th>Date</th>
        <th>Medication</th>
        <th>Dosage</th>
        <th>Instructions</th>
        <th>Start Date</th>
        <th>Notes</th>
        <th>Attachment</th>
        <th>Doctor Name</th>
      `;
    }
    if (!prescriptions || prescriptions.length === 0) {
      tableBody.innerHTML = '<tr><td colspan="8" class="text-center text-muted">No prescriptions found.</td></tr>';
    } else {
      prescriptions.forEach(p => {
        let medInfo = {};
        try {
          medInfo = JSON.parse(p.prescriptions || '{}');
        } catch (err) {
          console.warn("Invalid JSON in prescriptions:", p.prescriptions);
        }

        const row = document.createElement("tr");
        row.innerHTML = `
          <td>${medInfo.startDate ? new Date(medInfo.startDate).toLocaleDateString() : '-'}</td>
          <td>${medInfo.medication || '-'}</td>
          <td>${medInfo.dosage || '-'}</td>
          <td>${medInfo.frequency || '-'}</td>
          <td>${medInfo.startDate ? new Date(medInfo.startDate).toLocaleDateString() : '-'}</td>
          <td>${p.notes || '-'}</td>
          <td>${p.attachmentUrl ? `<a href="${p.attachmentUrl}" target="_blank">Download</a>` : '-'}</td>
          <td data-doctor-id="${p.doctorId}">Loading...</td>
        `;
        tableBody.appendChild(row);
      });
      const doctorCells = tableBody.querySelectorAll("td[data-doctor-id]");
      doctorCells.forEach(async cell => {
        const doctorId = cell.getAttribute("data-doctor-id");
        try {
          const profile = await getProfileById(doctorId);
          const fullName = `${profile.firstName || ''} ${profile.lastName || ''}`.trim();
          cell.textContent = fullName || 'Unknown Doctor';
        } catch (err) {
          console.warn("Could not fetch doctor profile:", err);
          cell.textContent = 'Unknown Doctor';
        }
      });
    }
  } catch (error) {
    console.error("Failed to load prescription history:", error);
    tableBody.innerHTML = '<tr><td colspan="8" class="text-center text-danger">Failed to load data.</td></tr>';
  } finally {
    if (spinner) spinner.classList.add("d-none");
  }
}

window.loadMedicalHistoryModal = loadMedicalHistoryModal;
window.loadPrescriptionHistory = loadPrescriptionHistory;

function toast(message, type = "info") {
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
