// Admin seat map JS (moved from template)
// Assumes `passengerCount` global is set by a small inline template script

let currentPassengerIndex = 0;
let passengerSeats = new Array(typeof passengerCount !== 'undefined' ? passengerCount : 1).fill(null);
let selectedSeatIds = new Set();

function selectPassenger(index) {
    const currentRadio = document.querySelector('input[name="currentPassengerSeat"]:checked');
    if (currentRadio && currentPassengerIndex >= 0 && currentPassengerIndex < passengerCount) {
        passengerSeats[currentPassengerIndex] = {
            seatId: currentRadio.value,
            seatNumber: currentRadio.dataset.seatNumber
        };
        selectedSeatIds.add(currentRadio.value);
    }

    currentPassengerIndex = index;

    document.querySelectorAll('.passenger-tab').forEach((tab, i) => {
        tab.classList.remove('active');
        tab.classList.remove('completed');
        if (i === index) tab.classList.add('active');
        if (passengerSeats[i] !== null) tab.classList.add('completed');
    });

    const passengerTab = document.getElementById('passengerTab' + index);
    if (passengerTab) {
        document.getElementById('currentPassengerName').textContent = passengerTab.querySelector('strong').textContent + ': ' + passengerTab.querySelector('span').textContent;
    }

    document.querySelectorAll('input[name="currentPassengerSeat"]').forEach(radio => {
        radio.checked = false;
        const originalDisabled = radio.hasAttribute('data-original-disabled');
        const seatId = radio.value;
        const isSelectedByOther = selectedSeatIds.has(seatId) && (passengerSeats[index] === null || passengerSeats[index].seatId !== seatId);
        radio.disabled = originalDisabled || isSelectedByOther;
        radio.parentElement.classList.remove('occupied');
        radio.nextElementSibling.classList.remove('disabled');
        if (isSelectedByOther || originalDisabled) {
            radio.parentElement.classList.add('occupied');
            radio.nextElementSibling.classList.add('disabled');
        }
    });

    if (passengerSeats[index] !== null) {
        const radio = document.querySelector(`input[value="${passengerSeats[index].seatId}"]`);
        if (radio) radio.checked = true;
        document.getElementById('currentSeatSelection').textContent = 'Đã chọn: Ghế ' + passengerSeats[index].seatNumber;
    } else {
        document.getElementById('currentSeatSelection').textContent = 'Vui lòng chọn ghế bên dưới';
    }

    checkAllSeatsSelected();
}

function updateSeatSelection(radio) {
    const seatNumber = radio.dataset.seatNumber;
    const seatId = radio.value;

    if (passengerSeats[currentPassengerIndex] !== null && passengerSeats[currentPassengerIndex].seatId === seatId) {
        radio.checked = false;
        selectedSeatIds.delete(seatId);
        passengerSeats[currentPassengerIndex] = null;
        document.getElementById('currentSeatSelection').textContent = 'Vui lòng chọn ghế bên dưới';
        document.getElementById('seatInfo' + currentPassengerIndex).textContent = 'Chưa chọn ghế';
        document.getElementById('passengerTab' + currentPassengerIndex).classList.remove('completed');
        checkAllSeatsSelected();
        return;
    }

    if (passengerSeats[currentPassengerIndex] !== null) {
        const oldSeatId = passengerSeats[currentPassengerIndex].seatId;
        selectedSeatIds.delete(oldSeatId);
        const oldRadio = document.querySelector(`input[value="${oldSeatId}"]`);
        if (oldRadio && !oldRadio.hasAttribute('data-original-disabled')) {
            oldRadio.disabled = false;
            oldRadio.parentElement.classList.remove('occupied');
            oldRadio.nextElementSibling.classList.remove('disabled');
        }
    }

    passengerSeats[currentPassengerIndex] = { seatId: seatId, seatNumber: seatNumber };
    selectedSeatIds.add(seatId);

    document.getElementById('currentSeatSelection').textContent = 'Đã chọn: Ghế ' + seatNumber;
    document.getElementById('seatInfo' + currentPassengerIndex).textContent = 'Ghế: ' + seatNumber;
    document.getElementById('passengerTab' + currentPassengerIndex).classList.add('completed');

    document.querySelectorAll(`input[value="${seatId}"]`).forEach(r => {
        if (r !== radio) {
            r.disabled = true; r.parentElement.classList.add('occupied'); r.nextElementSibling.classList.add('disabled');
        }
    });

    checkAllSeatsSelected();

    if (currentPassengerIndex < passengerCount - 1 && passengerSeats[currentPassengerIndex + 1] === null) {
        setTimeout(() => selectPassenger(currentPassengerIndex + 1), 400);
    }
}

function checkAllSeatsSelected() {
    const allSelected = passengerSeats.every(seat => seat !== null);
    const submitBtn = document.getElementById('submitBtn');
    if (submitBtn) submitBtn.disabled = !allSelected;
}

// DOM init
document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('input[name="currentPassengerSeat"]').forEach(radio => {
        if (radio.disabled) radio.setAttribute('data-original-disabled','true');
        let wasChecked = false;
        radio.addEventListener('mousedown', function(){ wasChecked = this.checked; });
        radio.addEventListener('click', function(e){ if (wasChecked && passengerSeats[currentPassengerIndex] !== null && passengerSeats[currentPassengerIndex].seatId === this.value){ this.checked=false; updateSeatSelection(this); e.preventDefault(); e.stopPropagation(); } });
    });

    const form = document.getElementById('seatForm');
    if (form) {
        form.addEventListener('submit', function(e){
            e.preventDefault();
            const currentRadio = document.querySelector('input[name="currentPassengerSeat"]:checked');
            if (currentRadio && currentPassengerIndex >=0 && currentPassengerIndex < passengerCount && passengerSeats[currentPassengerIndex] === null) {
                passengerSeats[currentPassengerIndex] = { seatId: currentRadio.value, seatNumber: currentRadio.dataset.seatNumber };
            }
            if (!passengerSeats.every(s => s !== null)) { alert('Vui lòng chọn ghế cho tất cả hành khách!'); return false; }
            const container = document.getElementById('hiddenInputsContainer'); container.innerHTML='';
            for (let i=0;i<passengerCount;i++){
                const input = document.createElement('input'); input.type='hidden'; input.name='selectedSeats'; input.value=passengerSeats[i].seatId; container.appendChild(input);
            }
            form.submit();
        });
    }

    selectPassenger(0);

    // admin seat click handlers
    document.querySelectorAll('.seat').forEach(el => {
        el.addEventListener('click', function(e){
            const status = this.getAttribute('data-status');
            if (status === 'BOOKED') {
                e.preventDefault(); e.stopPropagation();
                const ticketId = this.getAttribute('data-ticket-id');
                document.getElementById('md-ticketId').textContent = ticketId || '';
                document.getElementById('md-passenger').textContent = this.getAttribute('data-passenger') || '';
                document.getElementById('md-email').textContent = this.getAttribute('data-email') || '';
                document.getElementById('md-phone').textContent = this.getAttribute('data-phone') || '';
                document.getElementById('md-cccd').textContent = this.getAttribute('data-cccd') || '';
                document.getElementById('md-seat').textContent = this.querySelector('.seat-label') ? this.querySelector('.seat-label').textContent : '';
                document.getElementById('md-booking').textContent = this.getAttribute('data-booking') || '';
                document.getElementById('md-status').textContent = status;
                document.getElementById('md-price').textContent = this.getAttribute('data-price') || '';
                const modalEl = document.getElementById('ticketDetailModal');
                if (modalEl && typeof bootstrap !== 'undefined') {
                    const modal = new bootstrap.Modal(modalEl);
                    modal.show();
                }
            } else if (status === 'HELD') {
                e.preventDefault(); e.stopPropagation();
                alert('Ghế đang được giữ.');
            }
        });
    });
});

