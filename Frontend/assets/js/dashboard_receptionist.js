

document.addEventListener('DOMContentLoaded', () => {
  loadTodaysAppointments();
  loadPatientOverview();
});

async function loadTodaysAppointments() {
  const container = document.getElementById('todaysAppointments');
  try {
    const response = await fetch('/api/appointments/today');
    if (!response.ok) throw new Error('Failed to fetch appointments');
    const appointments = await response.json();

    if (appointments.length === 0) {
      container.innerHTML = '<p>No appointments scheduled for today.</p>';
      return;
    }

    const list = document.createElement('ul');
    list.classList.add('list-group');
    appointments.forEach(app => {
      const item = document.createElement('li');
      item.classList.add('list-group-item');
      item.textContent = `${app.time} - ${app.patientName}`;
      list.appendChild(item);
    });
    container.innerHTML = '';
    container.appendChild(list);
  } catch (error) {
    container.innerHTML = `<p class="text-danger">Error loading appointments: ${error.message}</p>`;
  }
}

async function loadPatientOverview() {
  const container = document.getElementById('patientOverview');
  try {
    const response = await fetch('/api/patients/overview');
    if (!response.ok) throw new Error('Failed to fetch patient data');
    const patients = await response.json();

    if (patients.length === 0) {
      container.innerHTML = '<p>No patients registered yet.</p>';
      return;
    }

    const list = document.createElement('ul');
    list.classList.add('list-group');
    patients.slice(0, 5).forEach(p => {
      const item = document.createElement('li');
      item.classList.add('list-group-item');
      item.textContent = `${p.name} (${p.id})`;
      list.appendChild(item);
    });
    container.innerHTML = '';
    container.appendChild(list);
  } catch (error) {
    container.innerHTML = `<p class="text-danger">Error loading patients: ${error.message}</p>`;
  }
}