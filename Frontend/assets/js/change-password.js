import { updatePassword } from './apiService.js';

document.addEventListener('DOMContentLoaded', () => {
  const form = document.getElementById('changePasswordForm');
  const currentPassword = document.getElementById('currentPassword');
  const newPassword = document.getElementById('newPassword');
  const confirmPassword = document.getElementById('confirmPassword');
  const messageDiv = document.getElementById('message');

  function showMessage(content, type = 'success') {
    messageDiv.classList.remove('d-none', 'alert-success', 'alert-danger', 'alert-warning');
    if (type === 'success') {
      messageDiv.classList.add('alert', 'alert-success');
    } else if (type === 'error') {
      messageDiv.classList.add('alert', 'alert-danger');
    } else if (type === 'warning') {
      messageDiv.classList.add('alert', 'alert-warning');
    }
    messageDiv.innerHTML = content;
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
    }
  }

  newPassword.addEventListener('input', checkPasswordsMatch);
  confirmPassword.addEventListener('input', checkPasswordsMatch);

  form.addEventListener('submit', async (e) => {
    e.preventDefault();

    if (!currentPassword.value || !newPassword.value || !confirmPassword.value) {
      showMessage('Please fill in all fields.', 'warning');
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