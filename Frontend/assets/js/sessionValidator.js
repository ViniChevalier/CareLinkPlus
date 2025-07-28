function parseJwt(token) {
  try {
    const payload = token.split('.')[1];
    const decodedPayload = atob(payload);
    return JSON.parse(decodedPayload);
  } catch (e) {
    return null;
  }
}

function isJwtExpired(token) {
  const decoded = parseJwt(token);

  if (!decoded || !decoded.exp) {
    return true;
  }

  const now = Math.floor(Date.now() / 1000);

  const isExpired = decoded.exp < now;
  return isExpired;
}

function isSessionValid() {
  const token = localStorage.getItem("token");

  if (!token) return false;
  if (isJwtExpired(token)) return false;

  return true;
}

function validateSessionOrRedirect() {
  if (!isSessionValid()) {
    localStorage.clear();
    showSessionExpiredToast();
    setTimeout(() => {
      window.location.href = "login.html";
    }, 4000);
  }
}

function showSessionExpiredToast() {
  const toast = document.createElement("div");
  toast.innerText = "Your session has expired. Please log in again.";
  toast.style.position = "fixed";
  toast.style.top = "20px";
  toast.style.right = "20px";
  toast.style.padding = "12px 20px";
  toast.style.backgroundColor = "#dc3545";
  toast.style.color = "#fff";
  toast.style.borderRadius = "4px";
  toast.style.boxShadow = "0 0 10px rgba(0, 0, 0, 0.2)";
  toast.style.zIndex = "9999";
  toast.style.fontSize = "14px";
  toast.style.transition = "opacity 0.5s ease-in-out";

  document.body.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    setTimeout(() => document.body.removeChild(toast), 500);
  }, 3000);
}

function handleUnauthorizedResponse(status) {
  if (status === 401) {
    localStorage.clear();
    showSessionExpiredToast();
    setTimeout(() => {
      window.location.href = "login.html";
    }, 4000);
  }
}

document.addEventListener("DOMContentLoaded", validateSessionOrRedirect);