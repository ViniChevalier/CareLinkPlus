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
        <h5 class="card-title text-primary mb-2"><i class="fas fa-user"></i> ${patientName}</h5>
        <p class="card-text mb-1"><i class="fas fa-calendar-alt"></i> <strong>${startTime} - ${endTime}</strong></p>
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

fetchAppointments().then(appointments => {
  const calendarEl = document.getElementById('calendarContainer');
  if (!calendarEl) return;

  const calendar = new FullCalendar.Calendar(calendarEl, {
    locale: 'en-gb',
    initialView: 'timeGridWeek',
    slotMinTime: '07:00:00',
    slotMaxTime: '20:00:00',
    nowIndicator: true,
    height: 'auto', 
    contentHeight: 'auto',
    aspectRatio: window.innerWidth < 576 ? 0.9 : window.innerWidth < 768 ? 1.2 : 1.8,
    expandRows: true,
    themeSystem: 'bootstrap',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'timeGridDay,timeGridWeek,dayGridMonth'
    },
    events: appointments
      .filter(app => ["BOOKED", "SCHEDULED", "COMPLETED", "NO_SHOW", "PENDING", "CONFIRMED", "ATTENDED"].includes(app.appointmentStatus?.toUpperCase()))
      .map(app => {
      const status = app.appointmentStatus?.toUpperCase();
      let color = '#0d6efd'; // Default: Scheduled
      if (["ATTENDED", "COMPLETED"].includes(status)) color = "#198754";
      else if (["BOOKED", "SCHEDULED", "PENDING", "CONFIRMED"].includes(status)) color = "#0d6efd";
      else if (status === "NO_SHOW") color = "#dc3545";

      return {
        title: app.patientFirstName ? `${app.patientFirstName} ${app.patientLastName}` : "Appointment",
        start: app.appointmentDateTime,
        allDay: false,
        backgroundColor: color,
        borderColor: color,
        extendedProps: {
          notes: app.notes || '',
          doctor: app.doctorName || '',
          id: app.id
        }
      };
    }),
    eventClick: function(info) {
      const event = info.event;
      const title = event.title;
      const date = new Date(event.start).toLocaleString();
      const notes = event.extendedProps.notes;
      const doctor = event.extendedProps.doctor;

      const detailHtml = `
        <div class="modal fade" id="eventDetailModal" tabindex="-1" aria-labelledby="eventDetailModalLabel" aria-hidden="true">
          <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title" id="eventDetailModalLabel">${title}</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
              </div>
              <div class="modal-body">
                <p><strong>Date & Time:</strong> ${date}</p>
                <p><strong>Reason:</strong> ${notes || 'No reason provided'}</p>
              </div>
            </div>
          </div>
        </div>
      `;

      const existingModal = document.getElementById('eventDetailModal');
      if (existingModal) existingModal.remove();
      document.body.insertAdjacentHTML('beforeend', detailHtml);
      new bootstrap.Modal(document.getElementById('eventDetailModal')).show();
    }
  });

  calendar.render();
  // Aplicar estilo Bootstrap moderno aos botões do calendário
  setTimeout(() => {
    const fcButtons = calendarEl.querySelectorAll('.fc-button');
    fcButtons.forEach(btn => {
      btn.classList.remove('fc-button', 'fc-button-primary');
      btn.classList.add(
        'btn',
        'btn-outline-primary',
        'd-flex',
        'align-items-center',
        'justify-content-center',
        'gap-2',
        'animate__animated',
        'animate__fadeInUp'
      );
      btn.style.minWidth = '110px';
      // Adiciona ícones Font Awesome aos botões de navegação e visualização
      if (btn.getAttribute("aria-label") === "prev") {
        btn.innerHTML = '<i class="fas fa-chevron-left"></i> Prev';
      }
      if (btn.getAttribute("aria-label") === "next") {
        btn.innerHTML = 'Next <i class="fas fa-chevron-right"></i>';
      }
      if (btn.getAttribute("aria-label") === "today") {
        btn.innerHTML = '<i class="fas fa-calendar-alt"></i> Today';
      }
      if (btn.innerText.toLowerCase() === "day") {
        btn.innerHTML = '<i class="fas fa-calendar-day"></i> Day';
      }
      if (btn.innerText.toLowerCase() === "week") {
        btn.innerHTML = '<i class="fas fa-calendar-week"></i> Week';
      }
      if (btn.innerText.toLowerCase() === "month") {
        btn.innerHTML = '<i class="fas fa-calendar"></i> Month';
      }
    });

    const buttonGroups = calendarEl.querySelectorAll('.fc-header-toolbar');
    buttonGroups.forEach(group => {
      group.classList.add('d-flex', 'justify-content-between', 'align-items-center', 'mb-3');
    });

    const titles = calendarEl.querySelectorAll('.fc-toolbar-title');
    titles.forEach(title => {
      title.classList.add('h5', 'text-dark', 'fw-semibold');
    });
  }, 0);
  calendarEl.classList.add("p-3", "bg-white", "rounded", "shadow-sm", "border");
  calendarEl.style.minHeight = "700px";
  calendarEl.style.width = "100%";
  calendarEl.classList.add("w-100");

  const legendHtml = `
    <div class="d-flex justify-content-start align-items-center gap-3 mt-3 flex-wrap">
      <div class="d-flex align-items-center gap-2">
        <span class="badge rounded-pill" style="background-color: #0d6efd;">&nbsp;</span>
        <small>Scheduled / Booked / Pending / Confirmed</small>
      </div>
      <div class="d-flex align-items-center gap-2">
        <span class="badge rounded-pill" style="background-color: #198754;">&nbsp;</span>
        <small>Completed / Attended</small>
      </div>
      <div class="d-flex align-items-center gap-2">
        <span class="badge rounded-pill" style="background-color: #dc3545;">&nbsp;</span>
        <small>No Show</small>
      </div>
    </div>
  `;

  calendarEl.insertAdjacentHTML('beforebegin', legendHtml);
});