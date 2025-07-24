import { getAllAvailability, getAllPatients, get, post } from './apiService.js';

document.addEventListener("DOMContentLoaded", () => {
  const patientSearch = document.getElementById("patientSearchInput");
  const patientNameConfirm = document.getElementById("patientNameConfirm");

  const patientOptions = document.getElementById("patientOptions");

  const doctorSelect = document.getElementById("doctorSelect");
  const slotSelect = document.getElementById("slotSelect");
  const form = document.getElementById("scheduleAppointmentForm");
  const messageDiv = document.getElementById("message");


  let selectedPatientId = null;
  const patientMap = new Map();

  getAllPatients()
    .then(patients => {
      console.log(patients);
      patients.forEach(patient => {
        const option = document.createElement("option");
        const patientLabel = `${patient.firstName} ${patient.lastName} (${patient.email}${patient.phoneNumber ? ' - ' + patient.phoneNumber : ''})`;
        option.value = patientLabel;
        patientMap.set(patientLabel, patient.userId);
        console.log("Patient Option:", option.value);
        patientOptions.appendChild(option);
      });
    })
    .catch(error => {
      console.error("Error loading patients:", error);
    });

  patientSearch.addEventListener("input", () => {
    const val = patientSearch.value.toLowerCase();
    const options = Array.from(document.getElementById("patientOptions").options);

    const matchedOption = options.find(option =>
      option.value.toLowerCase().includes(val)
    );

    if (matchedOption) {
      selectedPatientId = patientMap.get(patientSearch.value);
      patientNameConfirm.value = matchedOption.value;
      patientNameConfirm.classList.remove("is-invalid");
      patientNameConfirm.classList.add("is-valid");
    } else {
      selectedPatientId = null;
      patientNameConfirm.value = "";
      patientNameConfirm.classList.remove("is-valid");
      patientNameConfirm.classList.add("is-invalid");
    }
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
    if (!doctorId) {
      slotSelect.disabled = true;
      slotSelect.innerHTML = `<option value="">Please select a doctor first</option>`;
      return;
    }

    get(`/api/availability/doctor/${doctorId}`)
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

    post("/api/appointments", payload)
      .then(() => {
        showMessage("Appointment scheduled successfully!", "success");
        form.reset();
        doctorSelect.value = "";
        slotSelect.disabled = true;
        slotSelect.innerHTML = `<option value="">Please select a doctor first</option>`;
        selectedPatientId = null;
        patientNameConfirm.value = "";
        patientNameConfirm.classList.remove("is-valid", "is-invalid");
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

  function showMessage(msg, type) {
    messageDiv.className = `alert alert-${type} text-center`;
    messageDiv.textContent = msg;
    messageDiv.classList.remove("d-none");
  }
});