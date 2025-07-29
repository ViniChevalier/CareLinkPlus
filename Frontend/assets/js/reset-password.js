import { changePassword } from './apiService.js';

function validatePassword(password) {
  const lengthValid = password.length >= 8;
  const hasNumber = /\d/.test(password);
  const hasSymbol = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  return lengthValid && hasNumber && hasSymbol;
}

const submitBtn = document.querySelector('#resetPasswordForm button[type="submit"]');

document.getElementById("resetPasswordForm").addEventListener("submit", function (e) {
  e.preventDefault();
  submitBtn.disabled = true;

  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  const urlParams = new URLSearchParams(window.location.search);
  const token = urlParams.get("token");

  if (!token) {
    showToast("Invalid or missing token.", "danger");
    submitBtn.disabled = false;
    return;
  }

  if (newPassword !== confirmPassword) {
    showToast("Passwords do not match.", "danger");
    submitBtn.disabled = false;
    return;
  }

  if (!validatePassword(newPassword)) {
    showToast("Password must be at least 8 characters, include a number and a symbol.", "danger");
    submitBtn.disabled = false;
    return;
  }

  changePassword({
    token: token,
    newPassword: newPassword
  })
    .then(() => {
      showToast("Password reset successfully! You can now log in.", "success");
    })
    .catch(error => {
      showToast(error.message, "danger");
    })
    .finally(() => {
      submitBtn.disabled = false;
    });
});

// Toast function
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

const passwordInput = document.getElementById("newPassword");
const criteriaText = document.getElementById("passwordCriteria");

if (passwordInput && criteriaText) {
  passwordInput.addEventListener("focus", () => {
    criteriaText.classList.remove("hide");
  });
  passwordInput.addEventListener("blur", () => {
    criteriaText.classList.add("hide");
  });
  passwordInput.addEventListener("input", () => {
    if (validatePassword(passwordInput.value)) {
      criteriaText.classList.add("text-success");
      criteriaText.classList.remove("text-muted", "text-danger");
    } else {
      criteriaText.classList.add("text-danger");
      criteriaText.classList.remove("text-muted", "text-success");
    }
  });
}