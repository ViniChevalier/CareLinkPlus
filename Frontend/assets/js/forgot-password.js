import { requestPasswordReset } from './apiService.js';

document.getElementById("forgotPasswordForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const username = document.getElementById("username").value;
  const messageDiv = document.getElementById("message");

  requestPasswordReset({ username: username })
    .then(() => {
      messageDiv.innerHTML = `<span class="text-success">Reset link sent! Please check your email.</span>`;
    })
    .catch(error => {
      messageDiv.innerHTML = `<span class="text-danger">${error.message}</span>`;
    });
});