import { register, deactivateUser, updateUserRole } from './apiService.js';

document.addEventListener("DOMContentLoaded", () => {
  const createUserForm = document.getElementById("createUserForm");
  const alertCreate = document.getElementById("createUserAlert");
  if (alertCreate) alertCreate.classList.add("d-none");
  if (createUserForm) {
    createUserForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const fullName = document.getElementById("newUserName").value.trim();
      const email = document.getElementById("newUserEmail").value.trim();
      const [firstName, ...rest] = fullName.split(" ");
      const lastName = rest.join(" ") || "";

      const data = {
        firstName,
        lastName,
        email
      };

      try {
        const result = await register(data);
        if (alertCreate) {
          alertCreate.classList.remove("d-none");
          alertCreate.classList.add("alert-success");
          alertCreate.classList.remove("alert-danger");
          alertCreate.innerHTML = `
            <strong>User created successfully!</strong><br>
            Username: <code>${result.username}</code><br>
            Generated Password: <code>${result.generatedPassword}</code>
          `;
          setTimeout(() => {
            alertCreate.classList.add("d-none");
            alertCreate.innerHTML = "";
          }, 30000);
        }
        createUserForm.reset();
      } catch (err) {
        if (alertCreate) {
          alertCreate.classList.remove("d-none");
          alertCreate.classList.add("alert-danger");
          alertCreate.classList.remove("alert-success");
          alertCreate.textContent = "Error creating user: " + err.message;
          setTimeout(() => {
            alertCreate.classList.add("d-none");
            alertCreate.innerHTML = "";
          }, 30000);
        }
      }
    });
  }

  const editRoleForm = document.getElementById("editRoleForm");
  const alertEditRole = document.getElementById("editRoleAlert");
  if (alertEditRole) alertEditRole.classList.add("d-none");
  if (editRoleForm) {
    editRoleForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const userId = document.getElementById("userIdToEdit").value;
      const role = document.getElementById("newRole").value;

      try {
        await updateUserRole(userId, role);
        if (alertEditRole) {
          alertEditRole.classList.remove("d-none");
          alertEditRole.classList.add("alert-success");
          alertEditRole.classList.remove("alert-danger");
          alertEditRole.textContent = "User role updated!";
          setTimeout(() => {
            alertEditRole.classList.add("d-none");
            alertEditRole.textContent = "";
          }, 30000);
        }
        editRoleForm.reset();
      } catch (err) {
        if (alertEditRole) {
          alertEditRole.classList.remove("d-none");
          alertEditRole.classList.add("alert-danger");
          alertEditRole.classList.remove("alert-success");
          alertEditRole.textContent = "Error updating user role: " + err.message;
          setTimeout(() => {
            alertEditRole.classList.add("d-none");
            alertEditRole.textContent = "";
          }, 30000);
        }
      }
    });
  }

  const deactivateUserForm = document.getElementById("deactivateUserForm");
  const alertDeactivateUser = document.getElementById("deactivateUserAlert");
  if (alertDeactivateUser) alertDeactivateUser.classList.add("d-none");
  if (deactivateUserForm) {
    deactivateUserForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const userId = document.getElementById("userIdToDeactivate").value;

      try {
        await deactivateUser(userId);
        if (alertDeactivateUser) {
          alertDeactivateUser.classList.remove("d-none");
          alertDeactivateUser.classList.add("alert-success");
          alertDeactivateUser.classList.remove("alert-danger");
          alertDeactivateUser.textContent = "User deactivated.";
          setTimeout(() => {
            alertDeactivateUser.classList.add("d-none");
            alertDeactivateUser.textContent = "";
          }, 30000);
        }
        deactivateUserForm.reset();
      } catch (err) {
        if (alertDeactivateUser) {
          alertDeactivateUser.classList.remove("d-none");
          alertDeactivateUser.classList.add("alert-danger");
          alertDeactivateUser.classList.remove("alert-success");
          alertDeactivateUser.textContent = "Error deactivating user: " + err.message;
          setTimeout(() => {
            alertDeactivateUser.classList.add("d-none");
            alertDeactivateUser.textContent = "";
          }, 30000);
        }
      }
    });
  }
});