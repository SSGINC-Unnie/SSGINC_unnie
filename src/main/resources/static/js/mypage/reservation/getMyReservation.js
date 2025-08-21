document.addEventListener('DOMContentLoaded', () => {

    const reservationListContainer = document.getElementById('reservation-list-container');
    const modal = document.getElementById('change-modal');

    const modalState = {
        reservationId: null,
        designerId: null,
        calendarDate: new Date(),
        selectedDate: null,
        selectedTime: null,
        newStartTime: null
    };

    const fetchMyReservations = async () => {
        try {
            const response = await fetch('/api/mypage/reservations');
            const result = await response.json();
            renderReservations(result.data);
        } catch (error) {
            console.error(error);
            reservationListContainer.innerHTML = `<div class="empty-list">${error.message}</div>`;
        }
    };

    const renderReservations = (reservations) => {
        if (!reservations || reservations.length === 0) {
            reservationListContainer.innerHTML = `
            <div class="empty-state-container">
                <div class="empty-state-icon">🗓️</div>
                <h3>예약 내역이 없습니다.</h3>
                <p>마음에 드는 샵을 찾아 첫 예약을 진행해보세요!</p>
                <a href="/map/map" class="btn btn-primary">내주변 샵 찾기</a>
            </div>
        `;
            return;
        }

        reservationListContainer.innerHTML = '';

        reservations.forEach(res => {
            const card = document.createElement('div');
            card.className = 'reservation-card';
            card.dataset.reservationId = res.reservationId;

            const startTime = new Date(res.reservationTime);
            const formattedTime = startTime.toLocaleString('ko-KR', {
                year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
            });

            const statusClass = res.status.toLowerCase();
            const statusText = {
                'CONFIRMED': '예약확정',
                'CANCELLED': '예약취소',
                'HOLD': '결제대기'
            }[res.status] || res.status;

            const isChangeable = res.status === 'CONFIRMED' && (startTime.getTime() - new Date().getTime()) > 24 * 60 * 60 * 1000;
            const isCancellable = isChangeable;

            card.innerHTML = `
            <div class="reservation-info">
                <h3>${res.shopName}</h3>
                <p>${res.procedureName} / ${res.designerName}</p>
                <p class="reservation-time-display">${formattedTime}</p>
            </div>
            <div class="reservation-details">
                <div class="status ${statusClass}">${statusText}</div>
                <div class="price">${res.price.toLocaleString()}원</div>
            </div>
            
            ${isChangeable || isCancellable ? `
                <div class="card-footer">
                    ${isChangeable ? `<button class="btn btn-primary change-reservation-btn"
                        data-reservation-id="${res.reservationId}"
                        data-designer-id="${res.designerId}"
                        data-start-time="${res.reservationTime}">
                        예약 변경
                    </button>` : ''}
                    ${isCancellable ? `<button class="btn btn-ghost cancel-reservation-btn" data-reservation-id="${res.reservationId}">예약 취소</button>` : ''}
                </div>
            ` : ''}
        `;
            reservationListContainer.appendChild(card);
        });
    };

    // --- 모달 관련 DOM 요소 ---
    const modalCurrentTime = document.getElementById('modal-current-time');
    const calendarMonthYear = document.getElementById('modal-current-month-year');
    const calendarDates = document.getElementById('modal-calendar-dates');
    const timeSlotsContainer = document.getElementById('modal-time-slots-container');
    const timeSlotList = document.getElementById('modal-time-slot-list');
    const confirmBtn = document.getElementById('modal-confirm-btn');
    const cancelBtn = document.getElementById('modal-cancel-btn');
    const prevMonthBtn = document.getElementById('modal-prev-month-btn');
    const nextMonthBtn = document.getElementById('modal-next-month-btn');

    // '예약 변경' 버튼 클릭 시 모달 열기 (이벤트 위임)
    reservationListContainer.addEventListener('click', async(e) => {
        if (e.target.classList.contains('change-reservation-btn')) {
            const button = e.target;
            modalState.reservationId = button.dataset.reservationId;
            modalState.designerId = button.dataset.designerId;
            modalCurrentTime.textContent = new Date(button.dataset.startTime).toLocaleString('ko-KR');
            openModal();
        }
        if (e.target.classList.contains('cancel-reservation-btn')) {
            const button = e.target;
            const reservationId = button.dataset.reservationId;

            if (confirm('정말로 예약을 취소하시겠습니까?')) {
                try {
                    const response = await fetch(`/api/mypage/reservations/${reservationId}`, {
                        method: 'DELETE'
                    });

                    if (response.ok) {
                        alert('예약이 취소되었습니다.');
                        const card = button.closest('.reservation-card');
                        if (card) {
                            card.querySelector('.status').textContent = '예약취소';
                            card.querySelector('.status').className = 'status cancelled';
                            card.querySelector('.card-footer').innerHTML = '';
                        }
                    } else {
                        const errorData = await response.json();
                        alert(`취소 실패: ${errorData.message}`);
                    }
                } catch (error) {
                    console.error('Reservation cancellation error:', error);
                    alert('예약 취소 중 오류가 발생했습니다.');
                }
            }
        }
    });

    function openModal() {
        Object.assign(modalState, {
            calendarDate: new Date(),
            selectedDate: null,
            selectedTime: null,
            newStartTime: null
        });
        timeSlotsContainer.style.display = 'none';
        confirmBtn.disabled = true;
        renderCalendar();
        modal.style.display = 'flex';
    }

    function closeModal() { modal.style.display = 'none'; }
    cancelBtn.addEventListener('click', closeModal);
    modal.addEventListener('click', e => e.target === modal && closeModal());

    // 달력 월 이동
    prevMonthBtn.addEventListener('click', () => { modalState.calendarDate.setMonth(modalState.calendarDate.getMonth() - 1); renderCalendar(); });
    nextMonthBtn.addEventListener('click', () => { modalState.calendarDate.setMonth(modalState.calendarDate.getMonth() + 1); renderCalendar(); });

    // '변경 완료' 클릭
    confirmBtn.addEventListener('click', submitReservationChange);

    function renderCalendar() { /* 이전 답변과 동일 */
        const year = modalState.calendarDate.getFullYear();
        const month = modalState.calendarDate.getMonth();
        calendarMonthYear.textContent = `${year}년 ${month + 1}월`;
        const firstDay = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        const today = new Date(); today.setHours(0,0,0,0);
        calendarDates.innerHTML = '';
        for (let i = 0; i < firstDay; i++) calendarDates.innerHTML += '<div></div>';
        for (let day = 1; day <= daysInMonth; day++) {
            const dateCell = document.createElement('div');
            dateCell.className = 'date-cell';
            dateCell.textContent = day;
            const currentDate = new Date(year, month, day);
            if (currentDate < today) {
                dateCell.classList.add('disabled');
            } else {
                const dateString = `${year}-${String(month+1).padStart(2,'0')}-${String(day).padStart(2,'0')}`;
                if (modalState.selectedDate === dateString) {
                    dateCell.classList.add('selected');
                }
                dateCell.addEventListener('click', () => handleDateSelect(currentDate));
            }
            calendarDates.appendChild(dateCell);
        }
    }

    async function handleDateSelect(date) {
        modalState.selectedDate = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
        modalState.selectedTime = null;
        confirmBtn.disabled = true;
        renderCalendar();

        timeSlotsContainer.style.display = 'block';
        timeSlotList.innerHTML = `<div class="loader"></div>`;

        const response = await fetch(`/api/reservation/booked-times?designerId=${modalState.designerId}&date=${modalState.selectedDate}`);
        const result = await response.json();
        const bookedTimes = result.data || [];
        renderTimeSlots(date, bookedTimes);
    }

    function renderTimeSlots(selectedDate, bookedTimes) {
        timeSlotList.innerHTML = '';
        const isToday = selectedDate.toDateString() === new Date().toDateString();
        const currentHour = new Date().getHours();
        ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00'].forEach(time => {
            const slot = document.createElement('button');
            slot.className = 'time-slot';
            slot.textContent = time;
            const hour = parseInt(time.split(':')[0]);
            const isPast = isToday && hour <= currentHour;
            const isBooked = bookedTimes.includes(time);
            if (isPast || isBooked) {
                slot.classList.add('disabled');
                slot.disabled = true;
                if(isBooked) slot.textContent = '예약마감';
            } else {
                slot.addEventListener('click', () => handleTimeSelect(time));
            }
            if (modalState.selectedTime === time) slot.classList.add('selected');
            timeSlotList.appendChild(slot);
        });
    }

    function handleTimeSelect(time) {
        modalState.selectedTime = time;
        modalState.newStartTime = `${modalState.selectedDate}T${time}:00`;
        confirmBtn.disabled = false;
        renderTimeSlots(new Date(modalState.selectedDate), []);
    }

    async function submitReservationChange() {
        confirmBtn.disabled = true;
        confirmBtn.textContent = '변경 중...';
        try {
            const response = await fetch(`/api/mypage/reservations/${modalState.reservationId}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ newStartTime: modalState.newStartTime })
            });
            if (response.ok) {
                alert('예약이 성공적으로 변경되었습니다.');
                closeModal();
                const updatedCard = document.querySelector(`.reservation-card[data-reservation-id="${modalState.reservationId}"]`);
                if (updatedCard) {
                    const timeDisplay = updatedCard.querySelector('.reservation-time-display');
                    const newTime = new Date(modalState.newStartTime);
                    const formattedNewTime = newTime.toLocaleString('ko-KR', {
                        year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
                    });
                    timeDisplay.textContent = formattedNewTime;
                }
            } else {
                const errorData = await response.json();
                alert(`예약 변경 실패: ${errorData.message}`);
            }
        } catch (error) {
            alert('예약 변경 중 오류가 발생했습니다.');
        } finally {
            confirmBtn.disabled = false;
            confirmBtn.textContent = '변경 완료';
        }
    }

    fetchMyReservations();
});