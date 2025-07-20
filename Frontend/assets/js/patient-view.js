import {
  getProfileById,
  getMedicalHistoryByPatient,
  getAppointmentsByPatient,
  getPatientHistoriesByPatient,
  createPatientHistoryWithForm,
  createMedicalRecord
} from './apiService.js';


async function loadMedicalHistory(patientId) {
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

    // Only populate the individual fields
    if (latest.diagnosis && latest.diagnosis.trim().startsWith("{")) {
      try {
        const diag = JSON.parse(latest.diagnosis);
        // Fill fields individually
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

    // Optionally, populate other fields if needed (e.g., description, date, attachment, status)
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

    // Load doctor name and append it to the .doctor-name span, if present
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
  } catch (err) {
    console.error("Error loading medical history:", err);
  }
}

async function loadUpcomingAppointments(patientId) {
  try {
    const appointments = await getAppointmentsByPatient(patientId);
    const list = document.getElementById("upcomingAppointmentsList");
    if (!list) return;
    list.innerHTML = '';

    const upcoming = appointments
      .filter(a => new Date(a.dateTime) >= new Date())
      .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));

    if (upcoming.length === 0) {
      list.innerHTML = '<li class="list-group-item text-muted">No upcoming appointments.</li>';
      return;
    }

    upcoming.forEach(app => {
      const li = document.createElement("li");
      li.className = "list-group-item";
      li.innerHTML = `
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <strong>${new Date(app.dateTime).toLocaleString()}</strong><br/>
            <small>${app.type || 'General Consultation'}</small>
          </div>
          <span class="badge bg-primary">${app.status || 'Scheduled'}</span>
        </div>
      `;
      list.appendChild(li);
    });
  } catch (err) {
    console.error("Error loading appointments:", err);
  }
}

async function loadPrescriptions(patientId) {
  try {
    // Fetch prescriptions from the correct endpoint
    const prescriptions = await getMedicalHistoryByPatient(patientId);
    console.log("Received prescriptions:", prescriptions);
    const list = document.getElementById("prescriptionsList");
    if (!list) return;
    list.innerHTML = '';

    if (!prescriptions || prescriptions.length === 0) {
      list.innerHTML = '<li class="list-group-item text-muted">No active prescriptions.</li>';
      return;
    }

    prescriptions.forEach(p => {
      const li = document.createElement("li");
      li.className = "list-group-item";
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
          <span class="badge bg-success">${p.status || 'Active'}</span>
        </div>
      `;
      list.appendChild(li);
    });
  } catch (err) {
    console.error("Error loading prescriptions:", err);
  }
}

async function loadConsultationHistory(patientId) {
  try {
    const appointments = await getAppointmentsByPatient(patientId);
    const list = document.getElementById("consultationHistoryList");
    if (!list) return;
    list.innerHTML = '';

    if (!appointments || appointments.length === 0) {
      list.innerHTML = '<li class="list-group-item text-muted">No past consultations found.</li>';
      return;
    }

    appointments
      .sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime))
      .forEach(app => {
        const li = document.createElement("li");
        li.className = "list-group-item";
        li.innerHTML = `
          <div class="d-flex justify-content-between align-items-start">
            <div>
              <strong>${new Date(app.dateTime).toLocaleDateString()}</strong><br/>
              <small>${app.type || 'Consultation'}</small><br/>
              <span>${app.notes || 'No notes provided.'}</span>
            </div>
            <span class="badge bg-secondary">${app.status || 'Completed'}</span>
          </div>
        `;
        list.appendChild(li);
      });
  } catch (err) {
    console.error("Error loading consultation history:", err);
  }
}

document.addEventListener("DOMContentLoaded", async () => {
  const patientId = localStorage.getItem("selectedPatientId"); // ou sessionStorage
  if (!patientId) return;

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

  // Após carregar o perfil, carregue histórico médico, próximos agendamentos e prescrições
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
                  <label class="form-label">Status</label>
                  <select name="status" class="form-select">
                    <option value="Active">Active</option>
                    <option value="Resolved">Resolved</option>
                  </select>
                </div>
                <div class="mb-2">
                  <label class="form-label">Date</label>
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
          !form.diagnosis.value.trim() ||
          !form.preExisting.value.trim() ||
          !form.surgeries.value.trim() ||
          !form.allergies.value.trim() ||
          !form.familyHistory.value.trim()
        ) {
          alert("All clinical fields must be filled.");
          return;
        }

        const formData = new FormData(form);
        const clinicalData = {
          preExisting: form.preExisting.value.trim(),
          surgeries: form.surgeries.value.trim(),
          allergies: form.allergies.value.trim(),
          familyHistory: form.familyHistory.value.trim()
        };
        formData.set("diagnosis", JSON.stringify(clinicalData));
        formData.delete("preExisting");
        formData.delete("surgeries");
        formData.delete("allergies");
        formData.delete("familyHistory");
        const patientId = localStorage.getItem("selectedPatientId");
        const doctorId = localStorage.getItem("userId");
        if (!patientId || !doctorId) {
          console.error("Missing patientId or doctorId", { patientId, doctorId });
          alert("Erro: paciente ou médico não identificado.");
          return;
        }

        // Use append with string conversion for patientId, doctorId, updatedBy
        formData.append("patientId", String(patientId));
        formData.append("doctorId", String(doctorId));
        formData.append("updatedBy", String(doctorId));

        // Enhanced debug logging for FormData
        console.log("=== Submitting Medical History Form ===");
        for (let [key, value] of formData.entries()) {
          if (value instanceof File) {
            console.log(`[FormData] ${key}:`, value.name);
          } else {
            console.log(`[FormData] ${key}: "${value}"`);
          }
        }
        console.log("=== End of FormData ===");

        try {
          await createPatientHistoryWithForm(formData);
          alert("History added successfully!");
          bsModal.hide();
          loadMedicalHistory(patientId);
        } catch (error) {
          console.error("Failed to create patient history:", error);
          alert("Failed to add history.");
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
          alert("Please fill out all required fields.");
          return;
        }
        const patientId = localStorage.getItem("selectedPatientId");
        const doctorId = localStorage.getItem("userId");

        // Build the payload as specified
        const prescriptionPayload = {
          patientId: parseInt(patientId),
          doctorId: parseInt(doctorId),
          updatedBy: null,
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
        prescriptionFormData.append("updatedBy", "");
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
            form.file && form.file.files.length > 0 ? form.file.files[0] : null
          );
          alert("Prescription added successfully!");
          bsModal.hide();
          loadPrescriptions(patientId);
        } catch (err) {
          console.error("Error posting prescription:", err);
          alert("Failed to add prescription.");
        }
      });
    });
  }
});