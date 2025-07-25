import {
  getAllPatients,
  getAppointmentsByPatient,
  getProfileById,
  updateUserProfile,
  cancelAppointment,
  deactivateUser,
  getAppointmentById,
  getAllAvailability,
  createAppointment,
  getGoogleMapsApiKey
} from './apiService.js';

function loadGoogleMaps(callback) {
  getGoogleMapsApiKey()
    .then(key => {
      const script = document.createElement("script");
      script.src = `https://maps.googleapis.com/maps/api/js?key=${key}&libraries=places`;
      script.defer = true;
      script.onload = callback;
      document.head.appendChild(script);
    })
    .catch(err => console.error("Failed to load Google Maps key:", err));
}

let currentPatientId = null;

document.addEventListener("DOMContentLoaded", () => {
  const searchInput = document.getElementById("patient-search");
  // --- Google Maps Autocomplete for Address ---
  loadGoogleMaps(() => {
    const addressInput = document.getElementById("patient-address");
    const autocomplete = new google.maps.places.Autocomplete(addressInput, {
      types: ["address"],
      componentRestrictions: { country: ["ie"] },
      fields: ["address_components", "formatted_address"],
    });

    autocomplete.addListener("place_changed", function () {
      const place = autocomplete.getPlace();
      if (place.formatted_address) {
        addressInput.value = place.formatted_address;
      }

      let city = "";
      let country = "";

      if (place.address_components) {
        place.address_components.forEach((component) => {
          if (component.types.includes("locality") || component.types.includes("postal_town")) {
            city = component.long_name;
          }
          if (component.types.includes("country")) {
            country = component.long_name;
          }
        });
      }

      document.getElementById("patient-city").value = city;
      document.getElementById("patient-country").value = country;
    });
  });

  if (window.intlTelInput) {
    const phoneInput = document.getElementById("patient-phone");
    window.iti = window.intlTelInput(phoneInput, {
      initialCountry: "ie",
      separateDialCode: true,
      utilsScript: "https://cdnjs.cloudflare.com/ajax/libs/intl-tel-input/17.0.8/js/utils.js",
    });
  } else {
    console.error("intlTelInput not loaded");
  }
  const autocompleteList = document.getElementById("autocomplete-results");

  const nameInput = document.getElementById("patient-name");
  const emailInput = document.getElementById("patient-email");
  const phoneInput = document.getElementById("patient-phone");

  const appointmentsTable = document.querySelector("#appointments-table tbody");
  const editBtn = document.getElementById("edit-profile-btn");
  const saveBtn = document.getElementById("save-profile-btn");
  const cancelBtn = document.getElementById("cancel-edit-btn");

  let patients = [];
  let patientMap = new Map();
  let originalProfile = {};

  getAllPatients().then(data => {
    patients = data;
    patients.forEach(p => {
      const label = `${p.firstName} ${p.lastName} (${p.email}${p.phoneNumber ? ' - ' + p.phoneNumber : ''})`;
      patientMap.set(label, p.userId);
    });
  });

  searchInput.addEventListener("input", () => {
    const val = searchInput.value.toLowerCase();
    autocompleteList.innerHTML = '';
    if (!val) {
      autocompleteList.classList.add("d-none");
      return;
    }

    const matches = Array.from(patientMap.keys()).filter(label =>
      label.toLowerCase().includes(val)
    );

    matches.forEach(label => {
      const li = document.createElement("li");
      li.textContent = label;
      li.className = "list-group-item list-group-item-action";
      li.addEventListener("click", () => {
        searchInput.value = label;
        autocompleteList.classList.add("d-none");
        currentPatientId = patientMap.get(label);
        loadPatientProfile(currentPatientId);
        loadAppointments(currentPatientId);
      });
      autocompleteList.appendChild(li);
    });

    autocompleteList.classList.toggle("d-none", matches.length === 0);
  });

  function loadPatientProfile(patientId) {
    getProfileById(patientId).then(profile => {
      nameInput.value = `${profile.firstName} ${profile.lastName}`;
      emailInput.value = profile.email || '';
      phoneInput.value = profile.phoneNumber || '';
      // Populate new fields
      document.getElementById("patient-dob").value = profile.dateOfBirth || '';
      document.getElementById("patient-gender").value = profile.gender || '';
      document.getElementById("patient-address").value = profile.address || '';
      document.getElementById("patient-city").value = profile.city || '';
      document.getElementById("patient-country").value = profile.country || '';
      document.getElementById("patient-notification").value = profile.notificationPreference || '';
      originalProfile = profile;
    }).catch(err => {
      console.error("Error loading profile:", err);
    });
  }

  editBtn.addEventListener("click", () => {
    nameInput.disabled = false;
    emailInput.disabled = false;
    phoneInput.disabled = false;
    // Enable new fields
    document.getElementById("patient-dob").disabled = false;
    document.getElementById("patient-gender").disabled = false;
    document.getElementById("patient-address").disabled = false;
    document.getElementById("patient-city").disabled = false;
    document.getElementById("patient-country").disabled = false;
    document.getElementById("patient-notification").disabled = false;
    editBtn.classList.add("d-none");
    saveBtn.classList.remove("d-none");
    cancelBtn.classList.remove("d-none");
  });

  cancelBtn.addEventListener("click", () => {
    nameInput.value = `${originalProfile.firstName} ${originalProfile.lastName}`;
    emailInput.value = originalProfile.email || '';
    phoneInput.value = originalProfile.phoneNumber || '';
    // Reset and disable new fields
    document.getElementById("patient-dob").value = originalProfile.dateOfBirth || '';
    document.getElementById("patient-gender").value = originalProfile.gender || '';
    document.getElementById("patient-address").value = originalProfile.address || '';
    document.getElementById("patient-city").value = originalProfile.city || '';
    document.getElementById("patient-country").value = originalProfile.country || '';
    document.getElementById("patient-notification").value = originalProfile.notificationPreference || '';
    document.getElementById("patient-dob").disabled = true;
    document.getElementById("patient-gender").disabled = true;
    document.getElementById("patient-address").disabled = true;
    document.getElementById("patient-city").disabled = true;
    document.getElementById("patient-country").disabled = true;
    document.getElementById("patient-notification").disabled = true;
    nameInput.disabled = true;
    emailInput.disabled = true;
    phoneInput.disabled = true;
    editBtn.classList.remove("d-none");
    saveBtn.classList.add("d-none");
    cancelBtn.classList.add("d-none");
  });

  saveBtn.addEventListener("click", (e) => {
    e.preventDefault();
    if (!currentPatientId) return;

    const [firstName, ...lastParts] = nameInput.value.trim().split(" ");
    const lastName = lastParts.join(" ");
    const updated = {
      userId: currentPatientId,
      firstName: firstName || "",
      lastName: lastParts.join(" ") || "",
      email: emailInput.value.trim(),
      phoneNumber: phoneInput.value.trim()
    };
    // Add new fields to payload
    updated.dateOfBirth = document.getElementById("patient-dob").value;
    updated.gender = document.getElementById("patient-gender").value;
    updated.address = document.getElementById("patient-address").value;
    updated.city = document.getElementById("patient-city").value;
    updated.country = document.getElementById("patient-country").value;
    updated.notificationPreference = document.getElementById("patient-notification").value;

    updateUserProfile(updated).then(() => {
      nameInput.disabled = true;
      emailInput.disabled = true;
      phoneInput.disabled = true;
      // Disable new fields after save
      document.getElementById("patient-dob").disabled = true;
      document.getElementById("patient-gender").disabled = true;
      document.getElementById("patient-address").disabled = true;
      document.getElementById("patient-city").disabled = true;
      document.getElementById("patient-country").disabled = true;
      document.getElementById("patient-notification").disabled = true;
      editBtn.classList.remove("d-none");
      saveBtn.classList.add("d-none");
      cancelBtn.classList.add("d-none");
      showToast("Profile updated successfully.", "success");
    }).catch(err => {
      showToast("Failed to update profile. Please try again.", "danger");
    });
  });
  // --- Deactivate User Button Logic ---
  const deactivateBtn = document.getElementById("deactivate-user-btn");
  if (deactivateBtn) {
    deactivateBtn.addEventListener("click", () => {
      if (!currentPatientId) return;

      const confirmed = confirm("Are you sure you want to deactivate this user?\nThis action can only be undone by a system administrator.");
      if (!confirmed) return;

      deactivateUser(currentPatientId)
        .then(() => {
          showToast("User deactivated successfully.", "success");
          loadPatientProfile(currentPatientId);
        })
        .catch(() => {
          showToast("Failed to deactivate user. Please try again.", "danger");
        });
    });
  }

  // --- Bootstrap Toast utility ---
  function showToast(message, type = 'success') {
    const toastContainerId = 'toast-container';
    let container = document.getElementById(toastContainerId);
    if (!container) {
      container = document.createElement('div');
      container.id = toastContainerId;
      container.className = 'toast-container position-fixed top-0 end-0 p-3';
      document.body.appendChild(container);
    }

    const toastEl = document.createElement('div');
    toastEl.className = `toast align-items-center text-white bg-${type} border-0 mb-2`;
    toastEl.setAttribute('role', 'alert');
    toastEl.setAttribute('aria-live', 'assertive');
    toastEl.setAttribute('aria-atomic', 'true');
    toastEl.innerHTML = `
      <div class="d-flex">
        <div class="toast-body">${message}</div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    `;
    container.appendChild(toastEl);
    const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
    toast.show();
  }
});

// --- RESCHEDULE MODAL LOGIC ---

let currentAppointmentToReschedule = null;

// Cria modal de reagendamento
const modalHTML = `
<div class="modal fade" id="rescheduleModal" tabindex="-1" aria-labelledby="rescheduleModalLabel" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="rescheduleModalLabel">Reschedule Appointment</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body">
        <div id="reschedule-error" class="alert alert-danger d-none"></div>
        <div class="mb-3">
          <label for="rescheduleDoctorSelect" class="form-label">Select a doctor</label>
          <select class="form-select" id="rescheduleDoctorSelect">
            <option value="">Loading doctors...</option>
          </select>
        </div>
        <div class="mb-3">
          <label for="rescheduleSlotSelect" class="form-label">Select a new slot</label>
          <select class="form-select" id="rescheduleSlotSelect">
            <option value="">Select a doctor first</option>
          </select>
        </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn btn-primary" id="confirmRescheduleBtn">Confirm</button>
      </div>
    </div>
  </div>
</div>
`;
document.body.insertAdjacentHTML("beforeend", modalHTML);
const rescheduleModal = new bootstrap.Modal(document.getElementById("rescheduleModal"));
const slotSelect = document.getElementById("rescheduleSlotSelect");
const doctorSelect = document.getElementById("rescheduleDoctorSelect");
const errorDiv = document.getElementById("reschedule-error");

// Substitui alert de reagendamento pela lógica de modal
const appointmentsTable = document.querySelector("#appointments-table tbody");
appointmentsTable.addEventListener("click", (e) => {
  const btn = e.target.closest("button[data-action='reschedule']");
  if (!btn) return;

  const appointmentId = btn.dataset.id;
  getAppointmentById(appointmentId).then(appointment => {
    currentAppointmentToReschedule = appointment;
    loadAvailableDoctorsAndSlots();
    rescheduleModal.show();
  }).catch(() => {
    alert("Erro ao buscar detalhes do agendamento.");
  });
});

function loadAvailableDoctorsAndSlots() {
  getAllAvailability().then(availabilities => {
    const availableSlots = availabilities.filter(s => s.status === "AVAILABLE");
    const doctorMap = new Map();

    availableSlots.forEach(slot => {
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

    slotSelect.innerHTML = `<option value="">Select a doctor first</option>`;

    doctorSelect.onchange = () => {
      const selectedDoctorId = doctorSelect.value;
      if (!selectedDoctorId) {
        slotSelect.innerHTML = `<option value="">Select a doctor first</option>`;
        return;
      }

      const doctorSlots = availableSlots
        .filter(s => s.doctorId == selectedDoctorId)
        .sort((a, b) => new Date(`${a.availableDate}T${a.startTime}`) - new Date(`${b.availableDate}T${b.startTime}`));

      slotSelect.innerHTML = `<option value="">Select a slot</option>`;
      doctorSlots.forEach(slot => {
        const option = document.createElement("option");
        option.value = slot.availabilityId;
        const start = new Date(`${slot.availableDate}T${slot.startTime}`);
        const end = new Date(`${slot.availableDate}T${slot.endTime}`);
        const formatted = start.toLocaleString("en-IE", {
          weekday: "short",
          day: "2-digit",
          month: "short",
          year: "numeric",
          hour: "2-digit",
          minute: "2-digit"
        });
        const formattedEnd = end.toLocaleTimeString("en-IE", {
          hour: "2-digit",
          minute: "2-digit"
        });
        option.textContent = `${formatted} - ${formattedEnd}`;
        slotSelect.appendChild(option);
      });
    };
  }).catch(() => {
    doctorSelect.innerHTML = `<option value="">Error loading doctors</option>`;
    slotSelect.innerHTML = `<option value="">Error loading slots</option>`;
  });
}

document.getElementById("confirmRescheduleBtn").addEventListener("click", () => {
  const newSlotId = slotSelect.value;
  const confirmBtn = document.getElementById("confirmRescheduleBtn");

  if (!newSlotId || !currentAppointmentToReschedule) {
    errorDiv.textContent = "Please select a new slot.";
    errorDiv.classList.remove("d-none");
    return;
  }

  errorDiv.classList.add("d-none");
  confirmBtn.disabled = true;
  const originalHTML = confirmBtn.innerHTML;
  confirmBtn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Rescheduling...`;

  cancelAppointment(currentAppointmentToReschedule.id).then(() => {
    const payload = {
      patientId: currentAppointmentToReschedule.patientId,
      availabilityId: newSlotId,
      reason: currentAppointmentToReschedule.type
    };
    return createAppointment(payload);
  }).then(() => {
    rescheduleModal.hide();
    // Força atualização da lista após fechar o modal, com leve atraso para garantir DOM livre
    setTimeout(() => {
      if (currentPatientId) {
        loadAppointments(currentPatientId);
      } else {
        console.warn("currentPatientId não definido. A lista de agendamentos não pôde ser recarregada.");
      }
    }, 300);
  }).catch(() => {
    errorDiv.textContent = "Failed to reschedule. Please try again.";
    errorDiv.classList.remove("d-none");
  }).finally(() => {
    confirmBtn.disabled = false;
    confirmBtn.innerHTML = originalHTML;
  });
});

function loadAppointments(patientId) {
  getAppointmentsByPatient(patientId).then(appointments => {
    appointments.sort((a, b) => new Date(b.dateTime) - new Date(a.dateTime));
    const appointmentsTable = document.querySelector("#appointments-table tbody");
    appointmentsTable.innerHTML = '';

    if (!appointments.length) {
      appointmentsTable.innerHTML = '<tr><td colspan="6" class="text-center">No appointments found.</td></tr>';
      return;
    }

    appointments.forEach(app => {
      const tr = document.createElement("tr");
      const date = new Date(app.dateTime);
      const dateStr = date.toLocaleDateString("en-IE");
      const timeStr = date.toLocaleTimeString("en-IE", { hour: "2-digit", minute: "2-digit" });

      const validStatuses = ["BOOKED", "SCHEDULED", "PENDING", "CONFIRMED"];
      const actionBtns = (validStatuses.includes(app.status?.toUpperCase())) ? `
        <button class="btn btn-sm btn-warning me-1" data-id="${app.id}" data-action="reschedule">Reschedule</button>
        <button class="btn btn-sm btn-danger" data-id="${app.id}" data-action="cancel">Cancel</button>
      ` : `<button class="btn btn-sm btn-outline-secondary" disabled>N/A</button>`;

      tr.innerHTML = `
        <td>${dateStr}</td>
        <td>${timeStr}</td>
        <td>${app.doctorName || '-'}</td>
        <td><span class="badge bg-${getStatusColor(app.status)}">${app.status}</span></td>
        <td>${app.type || '-'}</td>
        <td>${actionBtns}</td>
      `;
      appointmentsTable.appendChild(tr);
    });

    // Attach event listeners
    appointmentsTable.querySelectorAll("button[data-action]").forEach(btn => {
      btn.addEventListener("click", (e) => {
        const id = btn.dataset.id;
        const action = btn.dataset.action;

        if (action === "cancel") {
          // Visual confirmation alert before cancelling
          if (!confirm("Are you sure you want to cancel this appointment?")) {
            return;
          }
          btn.disabled = true;
          const originalText = btn.innerHTML;
          btn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Cancelling...`;

          cancelAppointment(id)
            .then(() => {
              loadAppointments(currentPatientId);
              showToast("Appointment cancelled successfully.", "success");
            })
            .catch(err => {
              showToast("Failed to cancel the appointment. Please try again.", "danger");
            })
            .finally(() => {
              btn.disabled = false;
              btn.innerHTML = originalText;
            });
        }

        // Rescheduling logic moved to global event listener below
      });
    });
  }).catch(err => {
    console.error("Error loading appointments:", err);
  });
}

function getStatusColor(status) {
  switch (status?.toUpperCase()) {
    case "BOOKED": return "info";
    case "COMPLETED": return "success";
    case "CANCELLED": return "danger";
    case "NO_SHOW": return "danger";
    case "SCHEDULED": return "info";
    case "CONFIRMED": return "success";
    case "PENDING": return "warning";
    case "ATTENDED": return "success";
    default: return "light";
  }
}
