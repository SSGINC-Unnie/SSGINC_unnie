document.addEventListener('DOMContentLoaded', () => {
    // --- 상태 관리 객체 ---
    const state = {
        currentStep: 1,
        shopId: null,
        // memberId는 이제 백엔드에서 처리하므로 삭제합니다.
        // memberName은 API로 받아오므로 초기값만 설정합니다.
        memberName: '',
        bookingData: {
            service: null,
            designer: null,
            date: null,
            time: null
        },
        calendarDate: new Date() // 오늘 날짜 기준으로 달력 생성
    };

    // --- DOM 요소 (기존과 동일) ---
    const stepIndicators = document.querySelectorAll('.step');
    const progressFill = document.getElementById('progress-fill');
    const stepPanels = document.querySelectorAll('.step-panel');
    const servicesGrid = document.getElementById('services-grid');
    const designersGrid = document.getElementById('designers-grid');
    const step1NextBtn = document.getElementById('step1-next-btn');
    const calendarTitle = document.getElementById('calendar-title');
    const calendarDates = document.getElementById('calendar-dates');
    const prevMonthBtn = document.getElementById('prev-month-btn');
    const nextMonthBtn = document.getElementById('next-month-btn');
    const timeContainer = document.getElementById('time-container');
    const timeSectionTitle = document.getElementById('time-section-title');
    const timeSlots = document.getElementById('time-slots');
    const step2PrevBtn = document.getElementById('step2-prev-btn');
    const step2NextBtn = document.getElementById('step2-next-btn');
    const bookingSummaryContent = document.getElementById('booking-summary-content');
    const step3PrevBtn = document.getElementById('step3-prev-btn');
    const confirmBookingBtn = document.getElementById('confirm-booking-btn');
    const successPanel = document.getElementById('success-panel');
    const finalSummaryContent = document.getElementById('final-summary-content');


    // --- 데이터 로딩 함수 (기존과 동일) ---
    const fetchData = async (url) => {
        try {
            const response = await fetch(url);
            if (response.status === 401) { // 401 Unauthorized 에러 처리
                alert('로그인이 필요합니다.');
                window.location.href = '/login'; // 로그인 페이지로 리디렉션
                return null;
            }
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            const result = await response.json();
            return result.data;
        } catch (error) {
            console.error(`Fetch error for ${url}:`, error);
            servicesGrid.innerHTML = '<p>데이터를 불러오는 데 실패했습니다.</p>';
            designersGrid.innerHTML = '';
            return null;
        }
    };

    // --- UI 업데이트 함수 (기존과 동일) ---
    const showStep = (stepNumber) => {
        stepPanels.forEach(panel => panel.classList.remove('active'));
        const currentPanel = document.getElementById(`step-${stepNumber}`);
        if (currentPanel) {
            currentPanel.classList.add('active');
        } else if (stepNumber === 4) {
            successPanel.classList.add('active');
            document.querySelector('.step-progress').style.display = 'none';
        }

        stepIndicators.forEach((indicator, index) => {
            const stepNum = index + 1;
            indicator.classList.remove('active', 'completed');
            if (stepNum === stepNumber) {
                indicator.classList.add('active');
            } else if (stepNum < stepNumber) {
                indicator.classList.add('completed');
            }
        });

        const progressPercentage = stepNumber === 1 ? 0 : ((stepNumber - 1) / 2) * 100;
        progressFill.style.width = `${progressPercentage}%`;
        state.currentStep = stepNumber;
    };

    // --- Step 1, 2 관련 함수들 (기존과 동일) ---
    const renderServices = (services) => {
        servicesGrid.innerHTML = '';
        if (!services) return;
        services.forEach(service => {
            const serviceCard = document.createElement('div');
            serviceCard.className = 'selection-card';
            serviceCard.dataset.serviceId = service.procedureId;
            const thumbnailUrl = service.procedureThumbnail || '/img/karina.png';

            serviceCard.innerHTML = `
                <div class="card-image">
                    <img src="${thumbnailUrl}" alt="${service.procedureName}" onerror="this.src='/img/karina.png';">
                </div>
                <div class="card-info">
                    <h4>${service.procedureName}</h4>
                    <p class="price">${service.procedurePrice.toLocaleString()}원</p>
                </div>`;

            serviceCard.addEventListener('click', () => selectService(service, serviceCard));
            servicesGrid.appendChild(serviceCard);
        });
    };

    const renderDesigners = (designers) => {
        designersGrid.innerHTML = '';
        if (!designers) return;
        designers.forEach(designer => {
            const designerCard = document.createElement('div');
            designerCard.className = 'selection-card';
            designerCard.dataset.designerId = designer.designerId;
            const thumbnailUrl = designer.designerThumbnail || '/img/karina.png';

            designerCard.innerHTML = `
                <div class="card-image designer-image">
                    <img src="${thumbnailUrl}" alt="${designer.designerName}" onerror="this.src='/img/karina.png';">
                </div>
                <div class="card-info">
                    <h4>${designer.designerName}</h4>
                    <p class="specialty">${designer.designerIntroduction || '전문 디자이너'}</p>
                </div>`;

            designerCard.addEventListener('click', () => selectDesigner(designer, designerCard));
            designersGrid.appendChild(designerCard);
        });
    };

    const selectService = (service, cardElement) => {
        state.bookingData.service = service;
        servicesGrid.querySelectorAll('.selection-card').forEach(card => card.classList.remove('selected'));
        cardElement.classList.add('selected');
        updateStep1NextButton();
    };

    const selectDesigner = (designer, cardElement) => {
        state.bookingData.designer = designer;
        designersGrid.querySelectorAll('.selection-card').forEach(card => card.classList.remove('selected'));
        cardElement.classList.add('selected');
        updateStep1NextButton();
    };

    const updateStep1NextButton = () => {
        step1NextBtn.disabled = !(state.bookingData.service && state.bookingData.designer);
    };

    const renderCalendar = () => {
        const year = state.calendarDate.getFullYear();
        const month = state.calendarDate.getMonth();
        calendarTitle.textContent = `${year}년 ${month + 1}월`;
        const firstDayOfMonth = new Date(year, month, 1).getDay();
        const daysInMonth = new Date(year, month + 1, 0).getDate();
        const today = new Date(); today.setHours(0, 0, 0, 0);
        calendarDates.innerHTML = '';

        for (let i = 0; i < firstDayOfMonth; i++) {
            calendarDates.insertAdjacentHTML('beforeend', '<div class="date-cell empty"></div>');
        }

        for (let day = 1; day <= daysInMonth; day++) {
            const dateCell = document.createElement('div');
            dateCell.className = 'date-cell';
            dateCell.textContent = day;
            const currentDate = new Date(year, month, day);

            if (currentDate < today) {
                dateCell.classList.add('disabled');
            } else {
                if (currentDate.getTime() === today.getTime()) dateCell.classList.add('today');
                if (state.bookingData.date === formatDateForComparison(currentDate)) dateCell.classList.add('selected');
                dateCell.addEventListener('click', () => selectDate(currentDate));
            }
            calendarDates.appendChild(dateCell);
        }
    };

    const formatDateForComparison = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');

        return `${year}-${month}-${day}`;
    };

    const selectDate = async (dateObj) => {
        state.bookingData.date = formatDateForComparison(dateObj);
        state.bookingData.time = null;
        renderCalendar();

        const designerId = state.bookingData.designer.designerId;
        const date = state.bookingData.date;
        const bookedTimes = await fetchData(`/api/reservation/booked-times?designerId=${designerId}&date=${date}`);

        renderTimeSlots(dateObj, bookedTimes || []); // 조회된 목록을 renderTimeSlots에 전달

        timeContainer.style.display = 'block';
        timeSectionTitle.textContent = `${dateObj.toLocaleDateString('ko-KR')} 시간 선택`;
        updateStep2NextButton();
    };

    const renderTimeSlots = (selectedDate, bookedTimes) => { // bookedTimes 파라미터 추가
        timeSlots.innerHTML = '';
        const isToday = selectedDate.toDateString() === new Date().toDateString();
        const currentHour = new Date().getHours();
        const availableTimeSlots = ['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00', '18:00'];

        availableTimeSlots.forEach(time => {
            const timeSlot = document.createElement('button');
            timeSlot.className = 'time-slot';
            timeSlot.textContent = time;

            const hour = parseInt(time.split(':')[0]);
            const isPast = isToday && hour <= currentHour;
            const isBooked = bookedTimes.includes(time); // [추가] 예약 마감 여부 확인

            if (isPast || isBooked) {
                timeSlot.classList.add('disabled');
                timeSlot.disabled = true;
                if(isBooked) {
                    timeSlot.textContent = "예약마감"; // 마감된 슬롯에 텍스트 표시
                }
            } else {
                timeSlot.addEventListener('click', () => selectTime(time, timeSlot));
            }
            timeSlots.appendChild(timeSlot);
        });
    };

    const selectTime = (time, slotElement) => {
        state.bookingData.time = time;
        timeSlots.querySelectorAll('.time-slot').forEach(slot => slot.classList.remove('selected'));
        slotElement.classList.add('selected');
        updateStep2NextButton();
    };

    const updateStep2NextButton = () => {
        step2NextBtn.disabled = !(state.bookingData.date && state.bookingData.time);
    };


    const renderBookingSummary = (targetElement) => {
        const { service, designer, date, time } = state.bookingData;

        targetElement.innerHTML = `
            <div class="summary-item">
                <span class="label">예약자명</span>
                <span class="value">${state.memberName}</span>
            </div>
            <div class="summary-item">
                <span class="label">서비스</span>
                <span class="value">${service.procedureName}</span>
            </div>
            <div class="summary-item">
                <span class="label">디자이너</span>
                <span class="value">${designer.designerName}</span>
            </div>
            <div class="summary-item">
                <span class="label">예약일시</span>
                <span class="value">${new Date(date).toLocaleDateString('ko-KR')} ${time}</span>
            </div>
            <div class="summary-item total">
                <span class="label">총 결제금액</span>
                <span class="value">${service.procedurePrice.toLocaleString()}원</span>
            </div>`;
    };

    const confirmBooking = async () => {
        confirmBookingBtn.textContent = '결제 정보 생성 중...';
        confirmBookingBtn.disabled = true;

        try {
            const { service, designer, date, time } = state.bookingData;
            const startTime = `${date}T${time}:00`;

            // --- 1. 우리 서버에 'HOLD' 상태로 예약 생성 요청 ---
            const holdResponse = await fetch('/api/reservation/hold', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    shopId: state.shopId,
                    designerId: designer.designerId,
                    procedureId: service.procedureId,
                    startTime: startTime
                })
            });

            if (!holdResponse.ok) {
                throw new Error('예약 생성에 실패했습니다. 잠시 후 다시 시도해주세요.');
            }

            const holdData = await holdResponse.json();
            const reservationId = holdData.data.reservationId;
            const orderId = 'unnie-' + new Date().getTime(); // 고유한 주문 ID 생성

            const sessionResponse = await fetch('/api/payments/toss/widget-session', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    reservationId: reservationId,
                    shopId: state.shopId,
                    orderId: orderId
                })
            });

            if (!sessionResponse.ok) {
                throw new Error('결제 정보를 만드는 데 실패했습니다.');
            }

            const sessionData = (await sessionResponse.json()).data;

            const tossPayments = TossPayments(sessionData.clientKey);
            tossPayments.requestPayment('카드', {
                amount: sessionData.amount,
                orderId: sessionData.orderId,
                orderName: sessionData.orderName,
                customerName: state.memberName,
                successUrl: sessionData.successUrl,
                failUrl: sessionData.failUrl,
            });

        } catch (error) {
            alert(error.message);
        } finally {
            confirmBookingBtn.textContent = '결제하기';
            confirmBookingBtn.disabled = false;
        }
    };

    step1NextBtn.addEventListener('click', () => { showStep(2); renderCalendar(); });
    step2PrevBtn.addEventListener('click', () => showStep(1));
    step2NextBtn.addEventListener('click', () => { showStep(3); renderBookingSummary(bookingSummaryContent); });
    step3PrevBtn.addEventListener('click', () => showStep(2));
    confirmBookingBtn.addEventListener('click', confirmBooking);
    prevMonthBtn.addEventListener('click', () => { state.calendarDate.setMonth(state.calendarDate.getMonth() - 1); renderCalendar(); });
    nextMonthBtn.addEventListener('click', () => { state.calendarDate.setMonth(state.calendarDate.getMonth() + 1); renderCalendar(); });

    // --- 초기화 함수 (수정됨) ---
    const init = async () => {
        state.shopId = new URLSearchParams(window.location.search).get('shopId');
        if (!state.shopId) {
            document.body.innerHTML = '<h1>잘못된 접근입니다.</h1>';
            return;
        }

        try {
            const memberInfo = await fetchData('/api/reservation/member-info');
            if (memberInfo && memberInfo.memberName) {
                state.memberName = memberInfo.memberName;
            }
        } catch(e) {
            console.error("사용자 정보 로딩 중 오류 발생", e);
        }

        const proceduresData = await fetchData(`/api/shop/shopdetails/procedure/${state.shopId}`);
        const designersData = await fetchData(`/api/shop/shopdetails/designer/${state.shopId}`);

        if (proceduresData?.procedures) renderServices(proceduresData.procedures);
        if (designersData?.designers) renderDesigners(designersData.designers);

        showStep(1);
        updateStep1NextButton();
    };

    init();
});