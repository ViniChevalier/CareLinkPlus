import { login, getProfile } from './apiService.js';

const loginForm = document.getElementById('loginForm');
loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const usernameInput = document.getElementById('username');
  const passwordInput = document.getElementById('password');
  const loginBtn = loginForm.querySelector("button[type='submit']");
  const message = document.getElementById('message');

  usernameInput.classList.remove("is-invalid");
  passwordInput.classList.remove("is-invalid");
  message.textContent = '';
  message.classList.remove('text-success', 'text-danger');

  loginBtn.disabled = true;
  loginBtn.textContent = 'Logging in...';

  try {
    const data = await login({
      username: usernameInput.value,
      password: passwordInput.value,
    });

    localStorage.setItem('token', data.token);

    const profile = await getProfile();

    localStorage.setItem('userId', profile.userId);
    localStorage.setItem('role', profile.role);
    localStorage.setItem('name', profile.firstName);
    console.log('Profile received:', profile);

    message.textContent = 'Login successful! Redirecting...';
    message.classList.add('text-success');

    setTimeout(() => {
      const roleRedirects = {
        DOCTOR: 'dashboard_doctor.html',
        PATIENT: 'dashboard_patient.html',
        RECEPTIONIST: 'dashboard_receptionist.html',
        ADMIN: 'dashboard_admin.html',
      };
      const redirectUrl = roleRedirects[profile.role];
      if (redirectUrl) {
        window.location.href = redirectUrl;
      } else {
        message.textContent = 'Unknown user role. Access denied.';
        message.classList.remove('text-success');
        message.classList.add('text-danger');
      }
    }, 1000);
  } catch (error) {
    usernameInput.classList.add("is-invalid");
    passwordInput.classList.add("is-invalid");

    if (error.response && error.response.status === 403) {
      message.textContent = 'Invalid username or password. Please try again.';
    } else {
      message.textContent = error.message || 'An unexpected error occurred.';
    }
    message.classList.add('text-danger');
  } finally {
    loginBtn.disabled = false;
    loginBtn.textContent = 'Login';
  }
});