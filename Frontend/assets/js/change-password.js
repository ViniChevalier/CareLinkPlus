import { updatePassword } from './apiService.js';

document.addEventListener('DOMContentLoaded', () => {
  function validatePassword(password) {
    const lengthValid = password.length >= 8;
    const hasNumber = /\d/.test(password);
    const hasSymbol = /[!@#$%^&*(),.?":{}|<>]/.test(password);
    return lengthValid && hasNumber && hasSymbol;
  }
  const form = document.getElementById('changePasswordForm');
  const currentPassword = document.getElementById('currentPassword');
  const newPassword = document.getElementById('newPassword');
  const confirmPassword = document.getElementById('confirmPassword');
  const mismatchWarning = document.getElementById('passwordMismatch');
  const criteriaBox = document.getElementById("passwordCriteriaBox");
  const lengthCheck = document.getElementById("lengthCheck");
  const numberCheck = document.getElementById("numberCheck");
  const symbolCheck = document.getElementById("symbolCheck");


  function checkPasswordsMatch() {
    if (newPassword.value && confirmPassword.value) {
      if (newPassword.value === confirmPassword.value) {
        mismatchWarning.classList.add("d-none");
      } else {
        mismatchWarning.classList.remove("d-none");
      }
    } else {
      mismatchWarning.classList.add("d-none");
    }
  }

  function toggleCriteriaBox(show) {
    if (show) {
      criteriaBox.classList.remove("d-none");
    } else {
      criteriaBox.classList.add("d-none");
    }
  }

  function updateCriteriaFeedback(value) {
    toggleCheck(lengthCheck, value.length >= 8);
    toggleCheck(numberCheck, /\d/.test(value));
    toggleCheck(symbolCheck, /[!@#$%^&*(),.?":{}|<>]/.test(value));
  }

  function toggleCheck(element, condition) {
    element.classList.remove("text-success", "text-danger");
    element.classList.add(condition ? "text-success" : "text-danger");

    const icon = element.querySelector("i");
    if (icon) {
      icon.className = condition ? "lni lni-checkmark-circle" : "lni lni-close";
    }
  }

  newPassword.addEventListener('focus', () => toggleCriteriaBox(true));
  newPassword.addEventListener('blur', () => setTimeout(() => toggleCriteriaBox(false), 200));
  newPassword.addEventListener('input', () => {
    checkPasswordsMatch();
    updateCriteriaFeedback(newPassword.value);
  });
  confirmPassword.addEventListener('input', checkPasswordsMatch);

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!currentPassword.value || !newPassword.value || !confirmPassword.value) {
      showToast('Please fill in all fields.', 'warning');
      return;
    }

    if (!validatePassword(newPassword.value)) {
      showToast('Password must be at least 8 characters, include a number and a symbol.', 'warning');
      return;
    }

    if (newPassword.value !== confirmPassword.value) {
      mismatchWarning.classList.remove("d-none");
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      showToast('You are not logged in.', 'danger');
      return;
    }

    try {
      await updatePassword({
        oldPassword: currentPassword.value,
        newPassword: newPassword.value
      });

      showToast('Password changed successfully!', 'success');
      form.reset();

      setTimeout(() => {
        history.back();
      }, 2000);
    } catch (error) {
      showToast(`${error.message || 'Error changing password.'}`, 'danger');
    }
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