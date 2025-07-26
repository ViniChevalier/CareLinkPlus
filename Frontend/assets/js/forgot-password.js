import { requestPasswordReset } from './apiService.js';

const submitBtn = document.querySelector('#forgotPasswordForm button[type="submit"]');

document.getElementById("forgotPasswordForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const username = document.getElementById("username").value;

  submitBtn.disabled = true;

  requestPasswordReset({ username: username })
    .then(() => {
      showToast("Reset link sent! Please check your email.", "success");
    })
    .catch(error => {
      showToast(error.message || "An error occurred", "danger");
    })
    .finally(() => {
      submitBtn.disabled = false;
    });
});

function showToast(message, type = "info") {
  let toastContainer = document.getElementById("toast-top-right");
  if (!toastContainer) {
    toastContainer = document.createElement("div");
    toastContainer.id = "toast-top-right";
    toastContainer.className = "toast-container position-fixed top-0 end-0 p-3";
    document.body.appendChild(toastContainer);
  }

  const toastElement = document.createElement("div");
  toastElement.className = `toast align-items-center text-white bg-${type} border-0 mb-2 animate__animated animate__fadeInDown`;
  toastElement.setAttribute("role", "alert");
  toastElement.setAttribute("aria-live", "assertive");
  toastElement.setAttribute("aria-atomic", "true");

  toastElement.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${message}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  `;

  toastContainer.appendChild(toastElement);
  const bsToast = new bootstrap.Toast(toastElement);
  bsToast.show();

  setTimeout(() => {
    toastElement.remove();
  }, 8000);
}