import { getAllAppointments, checkInAppointment, markNoShowAppointment, undoCheckIn, undoNoShow } from './apiService.js';
import { loadReceptionistName } from './userProfile.js';

document.addEventListener("DOMContentLoaded", () => {
  loadReceptionistName();

  const receptionistId = localStorage.getItem("userId");
  if (!receptionistId) {
    const container = document.getElementById("dashboard");
    if (container) {
      container.innerHTML = `
        <div class="alert alert-warning text-center" role="alert">
          Receptionist ID not found. Please log in again.
        </div>
      `;
    }
    return;
  }
});

function getStatusBadge(status) {
  const s = status.toLowerCase();
  if (s === 'confirmed') return 'bg-success';
  if (s === 'attended') return 'bg-success';
  if (s === 'scheduled') return 'bg-warning text-dark';
  if (s === 'pending') return 'bg-warning text-dark';
  if (s === 'cancelled') return 'bg-danger';
  if (s === 'completed') return 'bg-success';
  if (s === 'no_show') return 'bg-danger';
  return 'bg-light text-dark';
}

function getDisplayStatus(status) {
  const s = status.toLowerCase();
  if (s === 'no_show') return 'No-Show';
  if (s === 'attended') return 'Attended';
  if (s === 'scheduled') return 'Scheduled';
  if (s === 'confirmed') return 'Confirmed';
  if (s === 'pending') return 'Pending';
  if (s === 'cancelled') return 'Cancelled';
  if (s === 'completed') return 'Completed';
  return status;
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
      appointmentList.innerHTML = upcoming.map(app => {
        const statusBadge = `<span class="badge ${getStatusBadge(app.status)} mb-2 px-3 py-2 fs-6 d-inline-block">${getDisplayStatus(app.status)}</span><br>`;
        const infoBlock = `<strong>${new Date(app.dateTime).toLocaleString()}</strong><br>Patient: ${app.patientName || 'N/A'}<br>Doctor: ${app.doctorName || 'N/A'}`;
        
        let actionButtons = '';
        if (app.status?.toLowerCase() === 'attended') {
          actionButtons = `<button class="btn btn-sm btn-outline-secondary btn-undo-checkin" data-id="${app.id}">Undo Check-in</button>`;
        } else if (app.status?.toLowerCase() === 'no_show') {
          actionButtons = `<button class="btn btn-sm btn-outline-secondary btn-undo-noshow" data-id="${app.id}">Undo No-Show</button>`;
        } else {
          actionButtons = `
            <button class="btn btn-sm btn-success me-1 btn-checkin" data-id="${app.id}">Check-in</button>
            <button class="btn btn-sm btn-danger btn-noshow" data-id="${app.id}">No-Show</button>
          `;
        }
        return `
          <li class="list-group-item d-flex justify-content-between align-items-start">
            <div>
              ${statusBadge}
              ${infoBlock}
            </div>
            <div class="ms-4 text-end">${actionButtons}</div>
          </li>`;
      }).join('');
      // Add event listeners for check-in and no-show buttons
      function attachCheckInAndNoShowListeners() {
        document.querySelectorAll('.btn-checkin').forEach(button => {
          button.addEventListener('click', async () => {
            const id = button.getAttribute('data-id');
            const item = button.closest('li');
            const badge = item.querySelector('.badge');
            const buttons = button.parentElement;

            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Checking in...';

            try {
              await checkInAppointment(id);
              badge.className = 'badge bg-success mb-2 px-3 py-2 fs-6 d-inline-block';
              badge.innerText = getDisplayStatus('attended');
              buttons.innerHTML = `<button class="btn btn-sm btn-outline-secondary btn-undo-checkin" data-id="${id}">Undo Check-in</button>`;
              document.querySelector(`.btn-undo-checkin[data-id="${id}"]`).addEventListener('click', async () => {
                const badge = item.querySelector('.badge');
                buttons.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Undoing...';
                try {
                  await undoCheckIn(id);
                  badge.className = 'badge bg-success mb-2 px-3 py-2 fs-6 d-inline-block';
                  badge.innerText = getDisplayStatus('scheduled');
                  buttons.innerHTML = `
                    <button class="btn btn-sm btn-success me-1 btn-checkin" data-id="${id}">Check-in</button>
                    <button class="btn btn-sm btn-danger btn-noshow" data-id="${id}">No-Show</button>
                  `;
                  attachCheckInAndNoShowListeners();
                } catch (error) {
                  alert('Failed to undo check-in.');
                }
              });
            } catch (error) {
              console.error('Check-in failed:', error);
              alert('Failed to check-in appointment.');
              button.disabled = false;
              button.innerText = 'Check-in';
            }
          });
        });

        document.querySelectorAll('.btn-noshow').forEach(button => {
          button.addEventListener('click', async () => {
            const id = button.getAttribute('data-id');
            const item = button.closest('li');
            const badge = item.querySelector('.badge');
            const buttons = button.parentElement;

            button.disabled = true;
            button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Marking...';

            try {
              await markNoShowAppointment(id);
              badge.className = 'badge bg-danger mb-2 px-3 py-2 fs-6 d-inline-block';
              badge.innerText = getDisplayStatus('no_show');
              buttons.innerHTML = `<button class="btn btn-sm btn-outline-secondary btn-undo-noshow" data-id="${id}">Undo No-Show</button>`;
              document.querySelector(`.btn-undo-noshow[data-id="${id}"]`).addEventListener('click', async () => {
                const badge = item.querySelector('.badge');
                buttons.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Undoing...';
                try {
                  await undoNoShow(id);
                  badge.className = 'badge bg-success mb-2 px-3 py-2 fs-6 d-inline-block';
                  badge.innerText = getDisplayStatus('scheduled');
                  buttons.innerHTML = `
                    <button class="btn btn-sm btn-success me-1 btn-checkin" data-id="${id}">Check-in</button>
                    <button class="btn btn-sm btn-danger btn-noshow" data-id="${id}">No-Show</button>
                  `;
                  attachCheckInAndNoShowListeners();
                } catch (error) {
                  alert('Failed to undo no-show.');
                }
              });
            } catch (error) {
              console.error('Mark No-Show failed:', error);
              alert('Failed to mark appointment as no-show.');
              button.disabled = false;
              button.innerText = 'No-Show';
            }
          });
        });
      }
      attachCheckInAndNoShowListeners();
      // Add event listeners for undo buttons
      document.querySelectorAll('.btn-undo-checkin').forEach(button => {
        button.addEventListener('click', async () => {
          const id = button.getAttribute('data-id');
          const item = button.closest('li');
          const badge = item.querySelector('.badge');
          const buttonContainer = button.parentElement;

          button.disabled = true;
          button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Undoing...';

          try {
            await undoCheckIn(id);
            badge.className = 'badge bg-success mb-2 px-3 py-2 fs-6 d-inline-block';
            badge.innerText = getDisplayStatus('scheduled');
            buttonContainer.innerHTML = `
              <button class="btn btn-sm btn-success me-1 btn-checkin" data-id="${id}">Check-in</button>
              <button class="btn btn-sm btn-danger btn-noshow" data-id="${id}">No-Show</button>
            `;
            attachCheckInAndNoShowListeners();
          } catch (error) {
            console.error('Undo Check-in failed:', error);
            alert('Failed to undo check-in.');
            button.disabled = false;
            button.innerText = 'Undo Check-in';
          }
        });
      });
      document.querySelectorAll('.btn-undo-noshow').forEach(button => {
        button.addEventListener('click', async () => {
          const id = button.getAttribute('data-id');
          const item = button.closest('li');
          const badge = item.querySelector('.badge');
          const buttonContainer = button.parentElement;

          button.disabled = true;
          button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Undoing...';

          try {
            await undoNoShow(id);
            badge.className = 'badge bg-success mb-2 px-3 py-2 fs-6 d-inline-block';
            badge.innerText = getDisplayStatus('scheduled');
            buttonContainer.innerHTML = `
              <button class="btn btn-sm btn-success me-1 btn-checkin" data-id="${id}">Check-in</button>
              <button class="btn btn-sm btn-danger btn-noshow" data-id="${id}">No-Show</button>
            `;
            attachCheckInAndNoShowListeners();
          } catch (error) {
            console.error('Undo No-Show failed:', error);
            alert('Failed to undo no-show.');
            button.disabled = false;
            button.innerText = 'Undo No-Show';
          }
        });
      });
    }


  } catch (error) {
    console.error('Failed to load dashboard data:', error);
  }
});