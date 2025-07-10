import { changePassword } from './apiService.js';

document.getElementById("resetPasswordForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;
  const messageDiv = document.getElementById("message");

  // Pega token da URL
  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get("token");

  if (!token) {
    messageDiv.innerHTML = `<span class="text-danger">Invalid or missing token.</span>`;
    return;
  }

  if (newPassword !== confirmPassword) {
    messageDiv.innerHTML = `<span class="text-danger">Passwords do not match.</span>`;
    return;
  }

  changePassword({
    token: token,
    newPassword: newPassword
  })
    .then(() => {
      messageDiv.innerHTML = `<span class="text-success">Password reset successfully! You can now log in.</span>`;
    })
    .catch(error => {
      messageDiv.innerHTML = `<span class="text-danger">${error.message}</span>`;
    });
});