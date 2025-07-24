import { getAllAppointments, cancelAppointment, updateAppointment, getAllAvailability, createAppointment } from "./apiService.js";

let originalAppointments = [];

document.addEventListener("DOMContentLoaded", async () => {
  try {
    originalAppointments = await getAllAppointments();
    originalAppointments = originalAppointments
      .filter(app => app.status !== "Completed" && app.status !== "Cancelled")
      .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));
    renderAppointments(originalAppointments);
  } catch (err) {
    const tableBody = document.getElementById("appointmentsTableBody");
    tableBody.innerHTML = `<tr><td colspan='5' class="text-danger">${err.message}</td></tr>`;
  }
  ["filterPatient", "filterDoctor", "filterDate"].forEach(id => {
    document.getElementById(id)?.addEventListener("input", applyFilters);
  });
  document.getElementById("clearFilters")?.addEventListener("click", () => {
    document.getElementById("filterPatient").value = "";
    document.getElementById("filterDoctor").value = "";
    document.getElementById("filterDate").value = "";
    renderAppointments(originalAppointments);
  });
});

function renderAppointments(appointments) {
  const tableBody = document.getElementById("appointmentsTableBody");
  if (!appointments.length) {
    tableBody.innerHTML = "<tr><td colspan='5'>No appointments found.</td></tr>";
    return;
  }
  tableBody.innerHTML = "";
  appointments.forEach(app => {
    const row = document.createElement("tr");
    row.innerHTML = `
      <td>${new Date(app.dateTime).toLocaleString()}</td>
      <td>${app.patientName || app.patient?.fullName || "N/A"}</td>
      <td>${app.doctorName || app.doctor?.fullName || "N/A"}</td>
      <td>${app.status}</td>
      <td>
        <button class="btn btn-sm btn-warning me-2" data-id="${app.id}" data-patient-id="${app.patientId}" data-action="reschedule">Reschedule</button>
        <button class="btn btn-sm btn-danger" data-id="${app.id}" data-action="cancel">Cancel</button>
      </td>
    `;
    tableBody.appendChild(row);
  });
}

function applyFilters() {
  const patient = document.getElementById("filterPatient").value.toLowerCase();
  const doctor = document.getElementById("filterDoctor").value.toLowerCase();
  const date = document.getElementById("filterDate").value;
  const filtered = originalAppointments.filter(app => {
    const patientMatch = (app.patientName || app.patient?.fullName || "").toLowerCase().includes(patient);
    const doctorMatch = (app.doctorName || app.doctor?.fullName || "").toLowerCase().includes(doctor);
    const dateMatch = !date || new Date(app.dateTime).toISOString().slice(0, 10) === date;
    return patientMatch && doctorMatch && dateMatch;
  });
  renderAppointments(filtered);
}

document.addEventListener("click", async (e) => {
  if (e.target.dataset.action === "cancel") {
    e.target.disabled = true;
    const id = e.target.dataset.id;
    if (!confirm("Are you sure you want to cancel this appointment?")) {
      e.target.disabled = false;
      return;
    }
    try {
      await cancelAppointment(id);
      alert("Appointment canceled successfully.");
      originalAppointments = await getAllAppointments();
      originalAppointments = originalAppointments
        .filter(app => app.status !== "Completed" && app.status !== "Cancelled")
        .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));
      renderAppointments(originalAppointments);
    } catch (err) {
      alert("Failed to cancel appointment: " + err.message);
    } finally {
      e.target.disabled = false;
    }
  }

  if (e.target.dataset.action === "reschedule") {
    e.preventDefault();
    e.stopPropagation();
    e.target.disabled = true;

    const appointmentId = e.target.dataset.id;
    const patientId = e.target.dataset.patientId;

    try {
      const allAppointments = await getAllAppointments();
      const appointment = allAppointments.find(a => a.id === parseInt(appointmentId));
      if (!appointment) {
        alert("Appointment not found.");
        return;
      }

      document.getElementById("rescheduleAppointmentId").dataset.appointmentId = appointmentId;
      document.getElementById("rescheduleAppointmentId").dataset.patientId = patientId;
      document.getElementById("doctorSelect").innerHTML = `<option>Loading...</option>`;
      document.getElementById("slotSelect").innerHTML = `<option>Select a doctor first</option>`;
      document.getElementById("slotSelect").disabled = true;

      const doctors = await getAllAvailability();
      const doctorSelect = document.getElementById("doctorSelect");
      doctorSelect.innerHTML = `<option value="">Select a doctor</option>`;
      const uniqueDoctors = [...new Map(doctors.map(d => [d.doctorId, d])).values()];
      uniqueDoctors.forEach(doc => {
        const opt = document.createElement("option");
        opt.value = doc.doctorId;
        opt.textContent = doc.doctorName;
        doctorSelect.appendChild(opt);
      });

      doctorSelect.addEventListener("change", () => {
        const selectedDoctorId = parseInt(doctorSelect.value);
        const slots = doctors.filter(a => a.doctorId === selectedDoctorId && a.status === "AVAILABLE");
        const slotSelect = document.getElementById("slotSelect");
        slotSelect.innerHTML = slots.length
          ? slots.map(slot => {
              const date = new Date(`${slot.availableDate}T${slot.startTime}`);
              const formattedDate = date.toLocaleDateString("en-IE", { weekday: 'short', year: 'numeric', month: 'short', day: 'numeric' });
              const formattedTime = `${slot.startTime.slice(0,5)} - ${slot.endTime.slice(0,5)}`;
              return `<option value="${slot.availabilityId}">${formattedDate} (${formattedTime})</option>`;
            }).join("")
          : `<option>No available slots</option>`;
        slotSelect.disabled = !slots.length;
      });

      const modalElement = document.getElementById("rescheduleModal");
      const modal = bootstrap.Modal.getOrCreateInstance(modalElement);
      setTimeout(() => modal.show(), 100);

      const confirmBtn = document.getElementById("confirmReschedule");
      const newBtn = confirmBtn.cloneNode(true);
      confirmBtn.parentNode.replaceChild(newBtn, confirmBtn);

      newBtn.addEventListener("click", async (event) => {
        event.preventDefault();
        const appointmentId = document.getElementById("rescheduleAppointmentId").dataset.appointmentId;
        const patientId = document.getElementById("rescheduleAppointmentId").dataset.patientId;
        const selectedDoctorId = parseInt(doctorSelect.value);
        const selectedSlotId = parseInt(document.getElementById("slotSelect").value);

        if (!selectedDoctorId || !selectedSlotId) {
          alert("Please select both doctor and slot.");
          return;
        }

        const receptionistName = localStorage.getItem("name") || "Unknown";
        const receptionistId = localStorage.getItem("userId") || "Unknown";

        try {
          await cancelAppointment(appointmentId);
          await createAppointment({
            patientId: parseInt(patientId),
            doctorId: selectedDoctorId,
            availabilityId: selectedSlotId,
            reason: `Rescheduled by receptionist ${receptionistName} (${receptionistId})`,
            notes: "Rescheduled via UI"
          });
          alert("Appointment rescheduled successfully.");
          setTimeout(async () => {
            bootstrap.Modal.getInstance(document.getElementById("rescheduleModal"))?.hide();
            originalAppointments = await getAllAppointments();
            originalAppointments = originalAppointments
              .filter(app => app.status !== "Completed" && app.status !== "Cancelled")
              .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));
            renderAppointments(originalAppointments);
          }, 100);

        } catch (err) {
          alert("Failed to reschedule appointment: " + err.message);
        }
      });

    } catch (err) {
      alert("Error loading appointment data: " + err.message);
    } finally {
      e.target.disabled = false;
    }
  }
});

