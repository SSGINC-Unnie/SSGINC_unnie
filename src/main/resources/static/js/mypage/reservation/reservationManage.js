document.addEventListener('DOMContentLoaded', () => {
    const shopSelector = document.getElementById('shop-selector');
    const datePicker = document.getElementById('date-picker');
    const timelineHeader = document.querySelector('.timeline-header');
    const timelineBody = document.getElementById('timeline-body');

    const state = {
        selectedShopId: null,
        selectedDate: new Date().toISOString().split('T')[0]
    };

    const init = async () => {
        datePicker.value = state.selectedDate;
        renderTimeHeader();
        await loadShops();
        shopSelector.addEventListener('change', handleControlChange);
        datePicker.addEventListener('change', handleControlChange);
    };

    const loadShops = async () => {
        try {
            const response = await fetch('/api/mypage/shops/manager');
            const result = await response.json();
            if (!response.ok) throw new Error('매장 목록 로딩 실패');
            shopSelector.innerHTML = '';
            result.data.forEach(shop => {
                shopSelector.add(new Option(shop.shopName, shop.shopId));
            });
            if (result.data.length > 0) {
                state.selectedShopId = shopSelector.value;
                loadDashboard();
            } else {
                shopSelector.innerHTML = '<option>등록된 매장이 없습니다</option>';
            }
        } catch (error) {
            console.error(error);
        }
    };

    const handleControlChange = () => {
        state.selectedShopId = shopSelector.value;
        state.selectedDate = datePicker.value;
        loadDashboard();
    };

    const loadDashboard = async () => {
        if (!state.selectedShopId || !state.selectedDate) return;
        timelineBody.innerHTML = '<div class="loader">로딩 중...</div>';
        try {
            const response = await fetch(`/api/mypage/manager/reservations/dashboard?shopId=${state.selectedShopId}&date=${state.selectedDate}`);
            const result = await response.json();
            if (!response.ok) throw new Error(result.message);
            renderTimeline(result.data);
        } catch (error) {
            timelineBody.innerHTML = `<p>${error.message}</p>`;
        }
    };

    const renderTimeline = (schedules) => {
        timelineBody.innerHTML = '';
        if (!schedules || schedules.length === 0) {
            timelineBody.innerHTML = '<p style="text-align:center; padding: 20px;">해당 날짜에 예약 현황이 없습니다.</p>';
            return;
        }
        schedules.forEach(schedule => {
            const designerRow = document.createElement('div');
            designerRow.className = 'designer-row';
            designerRow.innerHTML = `<div class="designer-name">${schedule.designerName}</div>`;
            const track = document.createElement('div');
            track.className = 'schedule-track';
            schedule.reservations.forEach(res => {
                track.appendChild(createReservationBlock(res));
            });
            designerRow.appendChild(track);
            timelineBody.appendChild(designerRow);
        });
    };

    const createReservationBlock = (reservation) => {
        const block = document.createElement('div');
        block.className = 'reservation-block';
        block.innerHTML = `<strong>${reservation.memberName}</strong><br>${reservation.procedureName}`;
        const startTime = new Date(reservation.startTime);
        const endTime = new Date(reservation.endTime);
        const totalMinutes = 600; // 9시 ~ 19시 (10시간)
        const startOffset = (startTime.getHours() * 60 + startTime.getMinutes()) - (9 * 60);
        const duration = (endTime.getTime() - startTime.getTime()) / (1000 * 60);
        if (startOffset < 0 || startOffset > totalMinutes) return document.createDocumentFragment();
        block.style.left = `${(startOffset / totalMinutes) * 100}%`;
        block.style.width = `${(duration / totalMinutes) * 100}%`;
        return block;
    };

    const renderTimeHeader = () => {
        let headerHTML = '<div class="designer-name">디자이너</div>';
        for (let i = 9; i < 19; i++) {
            headerHTML += `<div class="hour-label">${i}:00</div>`;
        }
        timelineHeader.innerHTML = headerHTML;
    };

    init();
});