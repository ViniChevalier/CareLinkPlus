import { getAllAvailability, getAllPatients, get, post, getDoctorAvailability, createAppointment } from './apiService.js';

document.addEventListener("DOMContentLoaded", () => {
  const patientSearch = document.getElementById("patientSearchInput");

  const doctorSelect = document.getElementById("doctorSelect");
  const slotSelect = document.getElementById("slotSelect");
  const form = document.getElementById("scheduleAppointmentForm");
  const messageDiv = document.getElementById("message");

  doctorSelect.disabled = true;

  let selectedPatientId = null;
  const patientMap = new Map();

  getAllPatients()
    .then(patients => {
      console.log(patients);
      patients.forEach(patient => {
        const patientLabel = `${patient.firstName} ${patient.lastName} (${patient.email}${patient.phoneNumber ? ' - ' + patient.phoneNumber : ''})`;
        patientMap.set(patientLabel, patient.userId);
      });

      const autocompleteResults = document.getElementById("autocomplete-results");

      patientSearch.addEventListener("input", () => {
        const val = patientSearch.value.toLowerCase();
        autocompleteResults.innerHTML = "";
        autocompleteResults.classList.add("d-none");

        if (val === "") return;

        const matched = Array.from(patientMap.entries()).filter(([label]) =>
          label.toLowerCase().includes(val)
        );

        if (matched.length > 0) {
          matched.forEach(([label, userId]) => {
            const li = document.createElement("li");
            li.className = "list-group-item list-group-item-action";
            li.textContent = label;
            li.addEventListener("click", () => {
              patientSearch.value = label;
              selectedPatientId = userId;
              autocompleteResults.classList.add("d-none");
              doctorSelect.disabled = false;
            });
            autocompleteResults.appendChild(li);
          });
          autocompleteResults.classList.remove("d-none");
        }
      });
    })
    .catch(error => {
      console.error("Error loading patients:", error);
    });

  getAllAvailability()
    .then(async (availabilities) => {
      const activeAvailabilities = availabilities.filter(slot =>
        slot.status === "AVAILABLE"
      );

      const doctorMap = new Map();
      activeAvailabilities.forEach(slot => {
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

      if (doctorMap.size === 0) {
        doctorSelect.innerHTML = `<option value="">No doctors available</option>`;
      }
    })
    .catch(error => {
      console.error("Error loading doctors:", error);
      doctorSelect.innerHTML = `<option value="">Error loading doctors</option>`;
    });

  doctorSelect.addEventListener("change", () => {
    const doctorId = doctorSelect.value;
    slotSelect.disabled = true;
    slotSelect.innerHTML = `<option value="">Loading slots...</option>`;

    if (!selectedPatientId) {
      slotSelect.disabled = true;
      slotSelect.innerHTML = `<option value="">Please select a patient first</option>`;
      showMessage("Please select a patient before choosing a doctor.", "warning");
      doctorSelect.value = "";
      return;
    }

    if (!doctorId) {
      slotSelect.disabled = true;
      slotSelect.innerHTML = `<option value="">Please select a doctor first</option>`;
      return;
    }

    getDoctorAvailability(doctorId)
      .then((slots) => {
        const activeSlots = slots.filter(slot =>
          slot.status === "AVAILABLE"
        );
        activeSlots.sort((a, b) => {
          const dateA = new Date(`${a.availableDate}T${a.startTime}`);
          const dateB = new Date(`${b.availableDate}T${b.startTime}`);
          return dateA - dateB;
        });
        slotSelect.innerHTML = `<option value="">Select a slot</option>`;
        activeSlots.forEach(slot => {
          const option = document.createElement("option");
          option.value = slot.availabilityId;

          const date = new Date(`${slot.availableDate}T${slot.startTime}`);
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

          const formattedEndTime = new Date(`${slot.availableDate}T${slot.endTime}`).toLocaleTimeString("en-IE", {
            hour: "2-digit",
            minute: "2-digit",
            hour12: false
          });
          option.textContent = `${formattedDate} — ${formattedStartTime} to ${formattedEndTime}`;
          slotSelect.appendChild(option);
        });
        slotSelect.disabled = false;
      })
      .catch(error => {
        console.error("Error loading slots:", error);
        slotSelect.disabled = true;
        slotSelect.innerHTML = `<option value="">Error loading slots</option>`;
      });
  });

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const submitBtn = form.querySelector("button[type='submit']");
    submitBtn.disabled = true;
    submitBtn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Scheduling...`;

    const availabilityId = slotSelect.value;
    const reason = document.getElementById("reason").value;

    if (!selectedPatientId || !availabilityId || !reason) {
      showMessage("Please fill all required fields.", "danger");
      submitBtn.disabled = false;
      submitBtn.textContent = "Schedule Appointment";
      return;
    }

    console.log("Selected patient ID:", selectedPatientId);

    const payload = {
      patientId: parseInt(selectedPatientId),
      availabilityId,
      reason,
    };

    createAppointment(payload)
      .then(() => {
        showMessage("Appointment scheduled successfully!", "success");
        form.reset();
        doctorSelect.value = "";
        slotSelect.disabled = true;
        slotSelect.innerHTML = `<option value="">Please select a doctor first</option>`;
        selectedPatientId = null;
        submitBtn.disabled = false;
        submitBtn.textContent = "Schedule Appointment";
      })
      .catch(error => {
        console.error("Error scheduling appointment:", error);
        showMessage("Error scheduling appointment. Please try again.", "danger");
        submitBtn.disabled = false;
        submitBtn.textContent = "Schedule Appointment";
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