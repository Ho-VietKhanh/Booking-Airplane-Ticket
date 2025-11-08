// Minimal seat map JS: handle selecting seats and showing booked info
function selectSeat(seatId, el) {
    // toggle selected class
    document.querySelectorAll('.admin-seat-map .seat').forEach(function(s){ s.classList.remove('selected'); });
    el.classList.add('selected');
    // store selection in hidden input (create if not exists)
    let input = document.getElementById('selectedSeatId');
    if(!input) {
        input = document.createElement('input');
        input.type = 'hidden';
        input.id = 'selectedSeatId';
        input.name = 'selectedSeatId';
        document.querySelector('form')?.appendChild(input);
    }
    input.value = seatId;
}

function showBookedInfo(ticketId) {
    if(!ticketId || ticketId.trim() === '') {
        alert('This seat is booked by another passenger.');
        return;
    }
    fetch('/admin/api/tickets/' + encodeURIComponent(ticketId))
        .then(function(res){
            if(!res.ok) throw new Error('Ticket not found');
            return res.json();
        })
        .then(function(data){
            // populate modal fields
            document.getElementById('md-ticketId').innerText = data.ticketId || '';
            document.getElementById('md-passenger').innerText = ((data.firstName||'') + ' ' + (data.lastName||'')).trim();
            document.getElementById('md-email').innerText = data.email || '';
            document.getElementById('md-phone').innerText = data.phone || '';
            document.getElementById('md-cccd').innerText = data.cccd || '';
            document.getElementById('md-seat').innerText = data.seatNumber || data.seatId || '';
            document.getElementById('md-booking').innerText = data.bookingId || '';
            document.getElementById('md-status').innerText = data.status || '';
            document.getElementById('md-price').innerText = data.price || '';

            // show bootstrap modal
            var modalEl = document.getElementById('ticketDetailModal');
            if(modalEl) {
                var modal = new bootstrap.Modal(modalEl);
                modal.show();
            } else {
                alert('Ticket: ' + ticketId + '\nPassenger: ' + ((data.firstName||'') + ' ' + (data.lastName||'')));
            }
        })
        .catch(function(err){
            console.error(err);
            alert('Unable to load ticket details');
        });
}

// For non-admin main seat-selection page, update passenger selection UI
function selectPassenger(index) {
    document.querySelectorAll('.passenger-tab').forEach(function(el){ el.classList.remove('active'); });
    const el = document.getElementById('passengerTab' + index);
    if(el) el.classList.add('active');
    const name = el?.querySelector('strong')?.innerText || ('Hành khách ' + (index+1));
    document.getElementById('currentPassengerName').innerText = name;
}

function updateSeatSelection(radio) {
    const passengerIndex = document.querySelector('.passenger-tab.active')?.getAttribute('data-passenger-index') || 0;
    const infoEl = document.getElementById('seatInfo' + passengerIndex);
    if(infoEl) infoEl.innerText = 'Ghế: ' + radio.getAttribute('data-seat-number');
    const cur = document.getElementById('currentSeatSelection');
    if(cur) cur.innerText = 'Đã chọn: ' + radio.getAttribute('data-seat-number');
}
