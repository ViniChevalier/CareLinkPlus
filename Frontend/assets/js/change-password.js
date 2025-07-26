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
  const messageDiv = document.getElementById('message');
  const mismatchWarning = document.getElementById('passwordMismatch');
  const criteriaBox = document.getElementById("passwordCriteriaBox");
  const lengthCheck = document.getElementById("lengthCheck");
  const numberCheck = document.getElementById("numberCheck");
  const symbolCheck = document.getElementById("symbolCheck");

  function showMessage(content, type = 'success') {
    messageDiv.classList.remove('d-none', 'alert-success', 'alert-danger', 'alert-warning');
    messageDiv.classList.add('alert');

    if (type === 'success') {
      messageDiv.classList.add('alert-success');
    } else if (type === 'error') {
      messageDiv.classList.add('alert-danger');
    } else if (type === 'warning') {
      messageDiv.classList.add('alert-warning');
    }

    messageDiv.textContent = content;
  }

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
      showMessage('Please fill in all fields.', 'warning');
      return;
    }

    if (!validatePassword(newPassword.value)) {
      showMessage('Password must be at least 8 characters, include a number and a symbol.', 'warning');
      return;
    }

    if (newPassword.value !== confirmPassword.value) {
      mismatchWarning.classList.remove("d-none");
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      showMessage('You are not logged in.', 'error');
      return;
    }

    try {
      await updatePassword({
        oldPassword: currentPassword.value,
        newPassword: newPassword.value
      });

      showMessage('Password changed successfully! Redirecting...', 'success');
      form.reset();

      setTimeout(() => {
        history.back();
      }, 2000);
    } catch (error) {
      showMessage(`${error.message || 'Error changing password.'}`, 'error');
    }
  });
});