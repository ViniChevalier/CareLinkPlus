import {
  addDoctorAvailability,
  getDoctorAvailability,
  deleteAvailability} from './apiService.js';

const doctorId = localStorage.getItem('userId');

document.querySelectorAll('.btn-duration').forEach(btn => {
  btn.addEventListener('click', async () => {
    const date = document.getElementById('date').value;
    const time = document.getElementById('time').value;
    const duration = parseInt(btn.dataset.duration);

    if (!date || !time || !duration) return toast('Please fill in all fields.', 'danger');

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Adding...';

    const start = new Date(`${date}T${time}`);
    const end = new Date(start.getTime() + duration * 60000);

    try {
      await addDoctorAvailability({
        doctorId,
        availableSlots: [
          {
            availableDate: date,
            startTime: formatTimeString(start),
            endTime: formatTimeString(end)
          }
        ]
      });
      toast(`Availability added for ${duration === 60 ? '1 hour' : duration + ' minutes'}.`, 'success');
      if (document.getElementById('viewDate').value === date) {
        await displayAvailability(date);
      }
    } catch (err) {
      toast('Error adding availability: ' + err.message, 'danger');
    } finally {
      btn.disabled = false;
      btn.innerHTML = '<i class="lni lni-plus"></i> Add 1-Hour Slot';
    }
  });
});

document.getElementById('viewDate').addEventListener('change', async (e) => {
  const selectedDate = e.target.value;
  await displayAvailability(selectedDate);
});

async function displayAvailability(date) {
  const list = document.getElementById('availabilityList');
  list.innerHTML = '<li class="list-group-item">Loading...</li>';
  try {
    const all = await getDoctorAvailability(doctorId);
    const filtered = all.filter(slot => slot.availableDate === date && slot.status === 'AVAILABLE');

    if (filtered.length === 0) {
      list.innerHTML = '<li class="list-group-item text-muted">No available slots for this date.</li>';
      return;
    }

    list.innerHTML = '';
    filtered.forEach(slot => {
      const li = document.createElement('li');
      li.className = 'list-group-item d-flex justify-content-between align-items-center';
      li.innerHTML = `
        ${formatTime(slot.startTime)} - ${formatTime(slot.endTime)}
        <button class="btn btn-sm btn-danger" data-id="${slot.id}">Cancel</button>
      `;
      list.appendChild(li);
    });

    list.querySelectorAll('button').forEach(btn => {
      btn.addEventListener('click', async () => {
        const id = btn.dataset.id;
        try {
          await deleteAvailability(id);
          btn.closest('li').remove();
        } catch (err) {
          toast('Failed to delete: ' + err.message, 'danger');
        }
      });
    });

  } catch (err) {
    list.innerHTML = `<li class="list-group-item text-danger">${err.message}</li>`;
  }
}

function formatTime(timeStr) {
  const [hour, minute] = timeStr.split(':');
  return `${hour}:${minute}`;
}

function formatTimeString(date) {
  return date.toTimeString().slice(0, 8); 
}


document.querySelectorAll('.auto-populate-btn').forEach(btn => {
  btn.addEventListener('click', async () => {
    const date = document.getElementById('populateDate').value;
    const workStart = document.getElementById('workStart').value;
    const workEnd = document.getElementById('workEnd').value;
    const breakStart = document.getElementById('breakStart').value;
    const breakEnd = document.getElementById('breakEnd').value;
    const slotSize = parseInt(btn.dataset.slot);

    if (!date || !workStart || !workEnd || !slotSize) {
      toast("Please fill in all required fields.", "danger");
      return;
    }

    btn.disabled = true;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Populating...';

    const slots = [];
    const start = new Date(`${date}T${workStart}`);
    const end = new Date(`${date}T${workEnd}`);
    const breakStartTime = breakStart ? new Date(`${date}T${breakStart}`) : null;
    const breakEndTime = breakEnd ? new Date(`${date}T${breakEnd}`) : null;

    for (let t = new Date(start); t < end; t.setMinutes(t.getMinutes() + slotSize)) {
      const next = new Date(t.getTime() + slotSize * 60000);

      if (breakStartTime && breakEndTime && t >= breakStartTime && next <= breakEndTime) continue;

      if (next > end) break;

      slots.push({
        availableDate: date,
        startTime: formatTimeString(t),
        endTime: formatTimeString(next)
      });
    }

    if (slots.length === 0) {
      toast("No valid slots generated for the selected configuration.", "warning");
      btn.disabled = false;
      btn.innerHTML = '<i class="lni lni-plus"></i> Populate 1-Hour Slots';
      return;
    }

    try {
      await addDoctorAvailability({ doctorId, availableSlots: slots });
      toast(`${slots.length} slots added for ${date}.`, "success");
      if (document.getElementById('viewDate').value === date) {
        await displayAvailability(date);
      }
    } catch (err) {
      toast('Error adding slots: ' + err.message, 'danger');
    } finally {
      btn.disabled = false;
      btn.innerHTML = '<i class="lni lni-plus"></i> Populate 1-Hour Slots';
    }
  });
});

function toast(message, type = "info") {
  let topRightContainer = document.getElementById("toast-top-right");
  if (!topRightContainer) {
    topRightContainer = document.createElement("div");
    topRightContainer.id = "toast-top-right";
    topRightContainer.className = "toast-container position-fixed top-0 end-0 p-3";
    document.body.appendChild(topRightContainer);
  }

  const toastElement = document.createElement("div");
  toastElement.className = `toast align-items-center text-white bg-${type} border-0 m-2 animate__animated animate__fadeInDown`;
  toastElement.setAttribute("role", "alert");
  toastElement.setAttribute("aria-live", "assertive");
  toastElement.setAttribute("aria-atomic", "true");

  toastElement.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${message}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
    </div>
  `;

  topRightContainer.appendChild(toastElement);
  const bsToast = new bootstrap.Toast(toastElement);
  bsToast.show();

  setTimeout(() => {
    toastElement.remove();
  }, 4000);
}