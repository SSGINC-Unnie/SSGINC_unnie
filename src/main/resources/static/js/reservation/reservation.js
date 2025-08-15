document.addEventListener('DOMContentLoaded', () => {
    // --- 상태 관리 객체 ---
    const state = {
        currentStep: 1,
        shopId: null,
        memberId: 1, // 실제로는 로그인 세션에서 가져와야 합니다.
        procedure: null,
        designer: null,
        selectedDate: null,
        selectedTime: null,
        calendarDate: new Date()
    };

    // --- DOM 요소 ---
    const steps = document.querySelectorAll('.step-card');
    const nextBtn = document.getElementById('next-btn');
    const submitBtn = document.getElementById('submit-btn');

    // --- STEP 1: 서비스 & 디자이너 ---
    const procedureList = document.getElementById('procedure-list');
    const designerList = document.getElementById('designer-list');

    // --- STEP 2: 달력 & 시간 ---
    const calendarMonthYear = document.getElementById('current-month-year');
    const calendarDates = document.getElementById('calendar-dates');
    const timeSlotsContainer = document.getElementById('time-slots-container');
    const timeSlotList = document.getElementById('time-slot-list');
    const selectedDateTitle = document.getElementById('selected-date-title');

    // --- STEP 3: 요약 ---
    const summaryDiv = document.getElementById('reservation-summary');
    const successModal = document.getElementById('success-modal');

    // --- 데이터 로딩 함수 ---
    const fetchData = async (url) => {
        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const result = await response.json();
            return result.data;
        } catch (error) {
            console.error(`Fetch error for ${url}:`, error);
            return null;
        }
    };

    // --- STEP 1 로직 ---
    const initStep1 = async () => {
        const procedures = await fetchData(`/api/shop/shopdetails/procedure/${state.shopId}`);
        const designers = await fetchData(`/api/shop/shopdetails/designer/${state.shopId}`);

        renderCards(procedureList, procedures?.procedures, 'procedure');
        renderCards(designerList, designers?.designers, 'designer');
    };

// reservation.js 파일에서 이 함수를 찾아 교체하세요.

    const renderCards = (listElement, items, type) => {
        listElement.innerHTML = '';
        if (!items || items.length === 0) {
            listElement.innerHTML = `<p>${type === 'procedure' ? '서비스' : '디자이너'} 정보가 없습니다.</p>`;
            return;
        }
        items.forEach(item => {
            const card = document.createElement('div');
            card.className = 'card';

            // 썸네일 경로를 가져옵니다. 없으면 기본 이미지 경로를 사용합니다.
            const thumbnailUrl = item.procedureThumbnail || item.designerThumbnail || '/img/karina.png';

            card.innerHTML = `
            <div class="card-image-placeholder">
                <img src="${thumbnailUrl}" 
                     alt="${item.procedureName || item.designerName}" 
                     style="width:100%; height:100%; object-fit:cover;"
                     onerror="this.onerror=null; this.src='/img/common/karina.png';">
            </div>
            <div class="card-info">
                <h4>${item.procedureName || item.designerName}</h4>
                ${item.procedurePrice ? `<p>${item.procedurePrice.toLocaleString()}원</p>` : ''}
            </div>`;

            card.addEventListener('click', () => {
                state[type] = item;
                listElement.querySelectorAll('.card').forEach(c => c.classList.remove('selected'));
                card.classList.add('selected');
                updateNextButtonState();
            });
            listElement.appendChild(card);
        });
    };

    // --- STEP 2 로직 (달력) ---
    const renderCalendar = () => {
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const date = state.calendarDate;
        const year = date.getFullYear();
        const month = date.getMonth();

        calendarMonthYear.textContent = `${year}년 ${month + 1}월`;

        const firstDayOfMonth = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();

        calendarDates.innerHTML = '';

        for (let i = 0; i < firstDayOfMonth; i++) {
            calendarDates.appendChild(document.createElement('div'));
        }

        for (let i = 1; i <= daysInMonth; i++) {
            const dayCell = document.createElement('div');
            dayCell.className = 'date-cell';
            dayCell.textContent = i;

            const currentDate = new Date(year, month, i);

            if (currentDate < today) {
                dayCell.classList.add('disabled');
            } else {
                if (currentDate.getTime() === today.getTime()) dayCell.classList.add('today');
                dayCell.addEventListener('click', () => {
                    state.selectedDate = currentDate;
                    state.selectedTime = null;
                    calendarDates.querySelectorAll('.date-cell').forEach(c => c.classList.remove('selected'));
                    dayCell.classList.add('selected');
                    renderTimeSlots();
                    updateNextButtonState();
                });
            }
            calendarDates.appendChild(dayCell);
        }
    };

    const renderTimeSlots = () => {
        const isToday = state.selectedDate.toDateString() === new Date().toDateString();
        const currentHour = new Date().getHours();

        selectedDateTitle.textContent = `${state.selectedDate.toLocaleDateString('ko-KR')} 시간 선택`;
        timeSlotsContainer.style.display = 'block';
        timeSlotList.innerHTML = '';

        for (let hour = 9; hour <= 18; hour++) {
            const slot = document.createElement('div');
            slot.className = 'time-slot';
            slot.textContent = `${String(hour).padStart(2, '0')}:00`;

            if (isToday && hour <= currentHour) {
                slot.classList.add('disabled');
            } else {
                slot.addEventListener('click', () => {
                    state.selectedTime = `${String(hour).padStart(2, '0')}:00`;
                    timeSlotList.querySelectorAll('.time-slot').forEach(s => s.classList.remove('selected'));
                    slot.classList.add('selected');
                    updateNextButtonState();
                });
            }
            timeSlotList.appendChild(slot);
        }
    };

    // --- STEP 3 로직 ---
    const renderSummary = () => {
        const { procedure, designer, selectedDate, selectedTime } = state;
        const formattedDate = selectedDate.toLocaleDateString('ko-KR');
        summaryDiv.innerHTML = `
            <p><strong>서비스</strong> <span>${procedure.procedureName}</span></p>
            <p><strong>디자이너</strong> <span>${designer.designerName}</span></p>
            <p><strong>예약일시</strong> <span>${formattedDate} ${selectedTime}</span></p>
            <p class="total"><strong>총 결제금액</strong> <span>${procedure.procedurePrice.toLocaleString()}원</span></p>
        `;
    };

    // --- 상태 및 흐름 제어 ---
    const updateNextButtonState = () => {
        let isEnabled = false;
        if (state.currentStep === 1) {
            isEnabled = state.procedure && state.designer;
        } else if (state.currentStep === 2) {
            isEnabled = state.selectedDate && state.selectedTime;
        }
        nextBtn.disabled = !isEnabled;
    };

    const handleNext = () => {
        const currentActiveStep = document.querySelector('.step-card.active');
        if (currentActiveStep) {
            currentActiveStep.classList.remove('active');
        }

        if (state.currentStep === 1) {
            state.currentStep = 2;
            renderCalendar();
        } else if (state.currentStep === 2) {
            state.currentStep = 3;
            renderSummary();
            nextBtn.style.display = 'none';
            submitBtn.style.display = 'block';
        }

        const nextStep = document.getElementById(`step-${state.currentStep}`);
        if (nextStep) {
            nextStep.classList.add('active');
        }

        updateNextButtonState();
    };

    const handleSubmit = async () => {
        const [year, month, day] = [state.selectedDate.getFullYear(), String(state.selectedDate.getMonth() + 1).padStart(2, '0'), String(state.selectedDate.getDate()).padStart(2, '0')];
        const startTime = `${year}-${month}-${day}T${state.selectedTime}:00`;

        const response = await fetch('/api/reservation', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                memberId: state.memberId,
                shopId: state.shopId,
                designerId: state.designer.designerId,
                procedureId: state.procedure.procedureId,
                startTime: startTime
            })
        });

        if (response.ok) {
            successModal.style.display = 'flex';
        } else {
            alert('예약 생성에 실패했습니다. 다시 시도해주세요.');
        }
    };

    // --- 초기화 ---
    const init = () => {
        state.shopId = new URLSearchParams(window.location.search).get('shopId');
        if (!state.shopId) {
            document.body.innerHTML = '<h1>잘못된 접근입니다.</h1>';
            return;
        }

        initStep1();

        nextBtn.addEventListener('click', handleNext);
        submitBtn.addEventListener('click', handleSubmit);

        document.getElementById('prev-month-btn').addEventListener('click', () => {
            state.calendarDate.setMonth(state.calendarDate.getMonth() - 1);
            renderCalendar();
        });
        document.getElementById('next-month-btn').addEventListener('click', () => {
            state.calendarDate.setMonth(state.calendarDate.getMonth() + 1);
            renderCalendar();
        });
    };

    init();
});