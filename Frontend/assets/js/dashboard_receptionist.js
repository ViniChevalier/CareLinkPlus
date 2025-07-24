import { getAllAppointments, getProfile } from './apiService.js';

function getStatusBadge(status) {
  const s = status.toLowerCase();
  if (s === 'confirmed') return 'bg-success';
  if (s === 'scheduled') return 'bg-success';
  if (s === 'pending') return 'bg-warning text-dark';
  if (s === 'cancelled') return 'bg-danger';
  if (s === 'completed') return 'bg-success';
  if (s === 'no_show') return 'bg-danger';
  return 'bg-light text-dark';
}

document.addEventListener('DOMContentLoaded', async () => {
  try {
    const appointments = await getAllAppointments();
    const now = new Date();

    const excludedStatuses = ['available', 'expired', 'cancelled', 'completed'];
    const upcoming = appointments
      .filter(app => {
        const appDate = new Date(app.dateTime);
        return appDate.getFullYear() === now.getFullYear()
          && appDate.getMonth() === now.getMonth()
          && appDate.getDate() === now.getDate()
          && (!app.status || !excludedStatuses.includes(app.status.toLowerCase()));
      })
      .sort((a, b) => new Date(a.dateTime) - new Date(b.dateTime));

    const appointmentList = document.getElementById('upcomingAppointmentsList').querySelector('ul');
    if (upcoming.length === 0) {
      appointmentList.innerHTML = '<li class="list-group-item text-muted">No appointments scheduled for today.</li>';
    } else {
      appointmentList.innerHTML = upcoming.map(app => `
        <li class="list-group-item d-flex justify-content-between align-items-start">
          <div>
            <span class="badge ${getStatusBadge(app.status)} mb-2 px-3 py-2 fs-6 d-inline-block">${app.status}</span><br>
            <strong>${new Date(app.dateTime).toLocaleString()}</strong><br>
            Patient: ${app.patientName || 'N/A'}<br>
            Doctor: ${app.doctorName || 'N/A'}
          </div>
          <div class="ms-4 text-end">
            <button class="btn btn-sm btn-success me-1 btn-checkin" data-id="${app.id}">Check-in</button>
            <button class="btn btn-sm btn-danger btn-noshow" data-id="${app.id}">No-Show</button>
          </div>
        </li>
      `).join('');
    }

    const patientAppointments = upcoming
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