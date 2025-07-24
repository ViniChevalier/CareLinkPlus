import { changePassword } from './apiService.js';

function validatePassword(password) {
  const lengthValid = password.length >= 8;
  const hasNumber = /\d/.test(password);
  const hasSymbol = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  return lengthValid && hasNumber && hasSymbol;
}

document.getElementById("resetPasswordForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;
  const messageDiv = document.getElementById("message");

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

  if (!validatePassword(newPassword)) {
    messageDiv.innerHTML = `<span class="text-danger">Password must be at least 8 characters, include a number and a symbol.</span>`;
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