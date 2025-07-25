
document.addEventListener("DOMContentLoaded", function () {
  const nav = document.getElementById("nav");
  const role = localStorage.getItem("role");

  if (!nav || !role) return;

  let items = `
    <li class="nav-item"><a class="nav-link" href="update-profile.html">Profile</a></li>
    <li class="nav-item"><a class="nav-link" href="change-Password.html">Change Password</a></li>
    <li class="nav-item"><a class="nav-link" href="#" id="logout-link">Logout</a></li>
  `;

  switch (role.toUpperCase()) {
    case 'ADMIN':
      items = `
        <li class="nav-item"><a class="nav-link" href="admin-Dashboard.html">Admin Dashboard</a></li>
        <li class="nav-item"><a class="nav-link" href="user-management.html">User Management</a></li>
      ` + items;
      break;
    case 'RECEPTIONIST':
      items = `
        <li class="nav-item"><a class="nav-link" href="receptionist-dashboard.html">Dashboard</a></li>
        <li class="nav-item"><a class="nav-link" href="manage-patients.html">Patients</a></li>
        <li class="nav-item"><a class="nav-link" href="view-appointments.html">Appointments</a></li>
      ` + items;
      break;
    case 'DOCTOR':
      items = `
        <li class="nav-item"><a class="nav-link" href="doctor-dashboard.html">Doctor Dashboard</a></li>
        <li class="nav-item"><a class="nav-link" href="appointments.html">My Appointments</a></li>
      ` + items;
      break;
    case 'PATIENT':
      items = `
        <li class="nav-item"><a class="nav-link" href="patient-dashboard.html">Dashboard</a></li>
        <li class="nav-item"><a class="nav-link" href="my-appointments.html">My Appointments</a></li>
      ` + items;
      break;
  }

  nav.innerHTML = items;
});