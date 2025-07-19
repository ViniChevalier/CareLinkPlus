function createAppointmentCard(appt) {
  const date = new Date(appt.appointmentDateTime);
  const startTime = date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  const endTime = new Date(date.getTime() + 30 * 60000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  const dateStr = `${date.getDate().toString().padStart(2, '0')}/${(date.getMonth() + 1).toString().padStart(2, '0')}/${date.getFullYear()}`;
  const patientName = appt.patientFirstName
    ? `${appt.patientFirstName} ${appt.patientLastName}`
    : 'Anonymous Patient';

  return `
    <div class="card mb-4 shadow-lg border rounded-3">
      <div class="card-body">
        <h5 class="card-title text-primary mb-2"><i class="lni lni-user"></i> ${patientName}</h5>
        <p class="card-text mb-1"><i class="lni lni-calendar"></i> <strong>${startTime} - ${endTime}</strong></p>
        <div class="text-end mt-3">
        </div>
      </div>
    </div>`;
}

import { getAppointmentsByDoctor, updateAppointment } from './apiService.js';

const weekdays = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
const appointmentsByDay = {
  Sunday: [], Monday: [], Tuesday: [], Wednesday: [], Thursday: [], Friday: [], Saturday: []
};

export async function fetchAppointments() {
  try {
    const doctorId = localStorage.getItem('userId');
    return await getAppointmentsByDoctor(doctorId);
  } catch (error) {
    console.error('Error fetching appointments:', error);
    return [];
  }
}

export function groupAppointmentsByDay(appointments) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const endDate = new Date();
  endDate.setHours(23, 59, 59, 999);
  endDate.setDate(today.getDate() + 6);

  weekdays.forEach(day => appointmentsByDay[day] = []);
  appointments.forEach(appt => {
    const date = new Date(appt.appointmentDateTime);
    date.setHours(0, 0, 0, 0);
    if (date >= today && date <= endDate) {
      const dayName = weekdays[date.getDay()];
      appointmentsByDay[dayName].push(appt);
    }
  });
}

export function renderAppointments() {
  const container = document.getElementById('weeklyAppointments');
  if (!container) return;

  container.innerHTML = `
    <div class="accordion" id="appointmentsAccordion"></div>
  `;
  const accordion = document.getElementById('appointmentsAccordion');

  weekdays.forEach((day, index) => {
    const appointments = appointmentsByDay[day];

    const collapseId = `collapse-${index}`;
    const headingId = `heading-${index}`;

    const content = appointments.length > 0
      ? appointments.map(createAppointmentCard).join('')
      : `<div class="text-muted text-center py-3">No appointments</div>`;

    const card = `
      <div class="accordion-item">
        <h2 class="accordion-header" id="${headingId}">
          <button class="accordion-button ${index !== 0 ? 'collapsed' : ''}" type="button" data-bs-toggle="collapse" data-bs-target="#${collapseId}" aria-expanded="${index === 0}" aria-controls="${collapseId}">
            ${appointments.length > 0
              ? `${day} - ${new Date(appointments[0].appointmentDateTime).toLocaleDateString()} (${appointments.length} ${appointments.length === 1 ? 'appointment' : 'appointments'})`
              : `${day} - No appointments`}
          </button>
        </h2>
        <div id="${collapseId}" class="accordion-collapse collapse ${index === 0 ? 'show' : ''}" aria-labelledby="${headingId}" data-bs-parent="#appointmentsAccordion">
          <div class="accordion-body">
            ${content}
          </div>
        </div>
      </div>
    `;

    accordion.insertAdjacentHTML('beforeend', card);
  });
}

fetchAppointments().then(data => {
  groupAppointmentsByDay(data);
  renderAppointments();
});