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
  const passwordCriteria = document.getElementById('passwordCriteria');

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
        showMessage('New passwords match!', 'success');
      } else {
        showMessage('New passwords do not match!', 'error');
      }
    } else {
      messageDiv.classList.add('d-none');
      messageDiv.classList.remove('alert', 'alert-success', 'alert-danger', 'alert-warning');
    }
  }

  function togglePasswordCriteria(show) {
    if (show) {
      passwordCriteria.classList.remove('d-none');
      passwordCriteria.classList.add('show');
    } else {
      passwordCriteria.classList.add('d-none');
      passwordCriteria.classList.remove('show');
    }
  }

  newPassword.addEventListener('input', checkPasswordsMatch);
  confirmPassword.addEventListener('input', checkPasswordsMatch);

  newPassword.addEventListener('focus', () => togglePasswordCriteria(true));
  newPassword.addEventListener('blur', () => togglePasswordCriteria(false));

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
      showMessage('New passwords do not match!', 'error');
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