document.addEventListener('DOMContentLoaded', () => {
    const reservationListContainer = document.getElementById('reservation-list-container');

    const fetchMyReservations = async () => {
        try {
            const response = await fetch('/api/mypage/reservations');
            if (!response.ok) {
                throw new Error('예약 목록을 불러오는데 실패했습니다.');
            }
            const result = await response.json();
            renderReservations(result.data);
        } catch (error) {
            console.error(error);
            reservationListContainer.innerHTML = `<div class="empty-list">${error.message}</div>`;
        }
    };

    const renderReservations = (reservations) => {
        if (!reservations || reservations.length === 0) {
            reservationListContainer.innerHTML = '<div class="empty-list">예약 내역이 없습니다.</div>';
            return;
        }

        reservationListContainer.innerHTML = ''; // 로더 제거

        reservations.forEach(res => {
            const card = document.createElement('div');
            card.className = 'reservation-card';

            const formattedTime = new Date(res.reservationTime).toLocaleString('ko-KR', {
                year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit'
            });

            const statusClass = res.status.toLowerCase();
            const statusText = {
                'CONFIRMED': '예약확정',
                'CANCELLED': '예약취소',
                'HOLD': '결제대기'
            }[res.status] || res.status;

            card.innerHTML = `
                <div class="reservation-info">
                    <h3>${res.shopName}</h3>
                    <p>${res.procedureName} / ${res.designerName}</p>
                    <p>${formattedTime}</p>
                </div>
                <div class="reservation-details">
                    <div class="status ${statusClass}">${statusText}</div>
                    <div class="price">${res.price.toLocaleString()}원</div>
                </div>
            `;
            reservationListContainer.appendChild(card);
        });
    };

    fetchMyReservations();
});