import { getAllAppointments, getProfile } from './apiService.js';

document.addEventListener('DOMContentLoaded', async () => {
  try {
    const appointments = await getAllAppointments();
    const now = new Date();

    const upcoming = appointments
      .filter(app => {
        const appDate = new Date(app.dateTime);
        return appDate.getFullYear() === now.getFullYear()
          && appDate.getMonth() === now.getMonth()
          && appDate.getDate() === now.getDate();
      })
      .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));

    const appointmentList = document.getElementById('upcomingAppointmentsList').querySelector('ul');
    if (upcoming.length === 0) {
      appointmentList.innerHTML = '<li class="list-group-item text-muted">No appointments scheduled for today.</li>';
    } else {
      appointmentList.innerHTML = upcoming.slice(0, 5).map(app => `
        <li class="list-group-item">
          <strong>${new Date(app.dateTime).toLocaleString()}</strong><br>
          Patient: ${app.patientName || 'N/A'}<br>
          Doctor: ${app.doctorName || 'N/A'}
        </li>
      `).join('');
    }

    const excludedStatuses = ['available', 'expired', 'cancelled', 'completed', 'no_show'];
    const patientAppointments = upcoming
      .filter(app => app.status && !excludedStatuses.includes(app.status.toLowerCase()))
      .slice(0, 5);

    const patientList = document.getElementById('nextPatientsList').querySelector('ul');
    if (patientAppointments.length === 0) {
      patientList.innerHTML = '<li class="list-group-item text-muted">No patients expected for check-in today.</li>';
    } else {
      patientList.innerHTML = patientAppointments.map(app => `
        <li class="list-group-item">
          ${app.patientName || 'Unnamed'} - ${new Date(app.dateTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
        </li>
      `).join('');
    }

  } catch (error) {
    console.error('Failed to load dashboard data:', error);
  }
});