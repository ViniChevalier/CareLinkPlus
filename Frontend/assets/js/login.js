import { login, getProfile } from './apiService.js';

const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const username = document.getElementById('username').value;
  const password = document.getElementById('password').value;
  const message = document.getElementById('message');
  message.textContent = 'Logging in...';

  try {
    const data = await login({ username, password });

    localStorage.setItem('token', data.token);

    const profile = await getProfile();

    localStorage.setItem('userId', profile.userId);
    localStorage.setItem('role', profile.role);
    localStorage.setItem('name', profile.firstName);
    console.log('Profile received:', profile);
    console.log('Saved userId:', localStorage.getItem('userId'));

    message.textContent = 'Login successful! Redirecting...';
    message.style.color = 'green';

    setTimeout(() => {
      if (profile.role === 'DOCTOR') {
        window.location.href = 'dashboard_doctor.html';
      } else if (profile.role === 'PATIENT') {
        window.location.href = 'dashboard_patient.html';
      } else if (profile.role === 'RECEPTIONIST') {
        window.location.href = 'dashboard_receptionist.html';
      } else if (profile.role === 'ADMIN') {
        window.location.href = 'dashboard_admin.html';
      } else {
        message.textContent = 'Unknown user role. Access denied.';
        message.style.color = 'red';
      }
    }, 1000);
  } catch (error) {
    if (error.response && error.response.status === 403) {
      message.textContent = 'Invalid username or password. Please try again.';
    } else {
      message.textContent = error.message || 'An unexpected error occurred. Please try again later.';
    }
    message.style.color = 'red';
  }
});