
document.addEventListener("DOMContentLoaded", function () {
  const nav = document.getElementById("nav");
  const role = localStorage.getItem("role");

  if (!nav || !role) return;

  let items = `
    <li class="nav-item"><a class="nav-link" href="profile_update.html">Profile</a></li>
    <li class="nav-item"><a class="nav-link" href="pwd_change.html">Change Password</a></li>
    <li class="nav-item"><a class="nav-link" href="#" id="logout-link">Logout</a></li>
  `;

  switch (role.toUpperCase()) {
    case 'ADMIN':
      items = `
        <li class="nav-item"><a class="nav-link" href="dashboard_admin.html">Dashboard</a></li>
      ` + items;
      break;
    case 'RECEPTIONIST':
      items = `
        <li class="nav-item"><a class="nav-link" href="dashboard_receptionist.html">Dashboard</a></li>
        <li class="nav-item"><a class="nav-link" href="appointment_schedule_receptionist.html">New Appointment</a></li>
        <li class="nav-item"><a class="nav-link" href="appointment_view.html">Appointments</a></li>
      ` + items;
      break;
    case 'DOCTOR':
      items = `
        <li class="nav-item"><a class="nav-link" href="dashboard_doctor.html">Dashboard</a></li>
        <li class="nav-item"><a class="nav-link" href="appointment_list.html">Agenda</a></li>
      ` + items;
      break;
    case 'PATIENT':
      items = `
        <li class="nav-item"><a class="nav-link" href="dashboard_patient.html">Dashboard</a></li>
        <li class="nav-item"><a class="nav-link" href="appointment_schedule_patient.html">New Appointment</a></li>
      ` + items;
      break;
  }

  nav.innerHTML = items;
});