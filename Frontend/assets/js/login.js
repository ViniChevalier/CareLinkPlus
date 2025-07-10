import { login } from './apiService.js';

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
    message.textContent = 'Login successful! Redirecting...';
    message.style.color = 'green';

    setTimeout(() => {
      window.location.href = 'patient-Dashboard.html';
    }, 1500);
  } catch (error) {
    message.textContent = error.message || 'Invalid credentials';
    message.style.color = 'red';
  }
});