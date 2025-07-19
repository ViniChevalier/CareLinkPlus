import { getAppointmentsByDoctor } from './apiService.js';

document.addEventListener('DOMContentLoaded', () => {
    const filterForm = document.getElementById('filterForm');
    const container = document.getElementById('filteredAppointmentsContainer');

    let filteredAppointments = [];

    async function fetchAndFilterAppointments(doctorId, start, end) {
        try {
            const allAppointments = await getAppointmentsByDoctor(doctorId);
            return allAppointments.filter(app => {
                const date = new Date(app.appointmentDateTime);
                return date >= start && date <= end;
            }).sort((a, b) => new Date(a.appointmentDateTime) - new Date(b.appointmentDateTime));
        } catch (err) {
            console.error('Failed to fetch appointments:', err.message);
            return [];
        }
    }

    function renderAppointments(appointments) {
        if (!appointments.length) {
            container.innerHTML = '<p class="text-center mt-3">No appointments found in this range.</p>';
            return;
        }

        container.innerHTML = `
      <table class="table table-bordered mt-3">
        <thead>
          <tr>
            <th>Appointment ID</th>
            <th>Date &amp; Time</th>
            <th>Status</th>
            <th>Reason for Visit</th>
            <th>Created On</th>
            <th>Patient Name</th>
          </tr>
        </thead>
        <tbody>
          ${appointments.map(app => `
            <tr>
              <td>${app.appointmentId}</td>
              <td>${new Date(app.appointmentDateTime).toLocaleString()}</td>
              <td>${app.appointmentStatus}</td>
              <td>${app.reason}</td>
              <td>${new Date(app.createdAt).toLocaleString()}</td>
              <td>${app.patientFirstName} ${app.patientLastName}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    `;
    }

    filterForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const start = new Date(document.getElementById('startDate').value);
        const end = new Date(document.getElementById('endDate').value);
        const doctorId = localStorage.getItem('userId');

        if (!doctorId) {
            alert('Doctor ID not found in local storage.');
            return;
        }

        filteredAppointments = await fetchAndFilterAppointments(doctorId, start, end);
        renderAppointments(filteredAppointments);
    });
});