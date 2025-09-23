document.addEventListener('DOMContentLoaded', function () {

    // --- DOM 요소 가져오기 ---
    const memberId = document.body.dataset.memberId;
    const notificationIcon = document.querySelector('.notification-icon');
    const notificationPopup = document.getElementById('notification-popup');
    const notificationList = document.getElementById('notification-list');
    const notificationBadge = document.querySelector('.notification-badge');

    // --- 알림 목록 불러오기 함수 ---
    const fetchNotifications = async () => {
        if (!memberId || memberId === '0') return;

        try {
            const response = await fetch('/api/notification');
            if (!response.ok) throw new Error('알림 목록을 불러오는데 실패했습니다.');

            const result = await response.json();
            const notifications = result.data.notifications;

            renderNotifications(notifications);

        } catch (error) {
            console.error(error);
            notificationList.innerHTML = '<li class="notification-empty">알림을 불러올 수 없습니다.</li>';
        }
    };

    // --- 알림 목록을 화면에 그리는 함수 ---
    const renderNotifications = (notifications) => {
        notificationList.innerHTML = ''; // 기존 목록 비우기

        if (!notifications || notifications.length === 0) {
            notificationList.innerHTML = '<li class="notification-empty">새로운 알림이 없습니다.</li>';
            return;
        }

        notifications.forEach(noti => {
            const item = document.createElement('li');
            item.className = 'notification-item unread'; // unread 클래스 추가
            item.onclick = () => {
                // TODO: 알림 읽음 처리 API 호출
                location.href = noti.notificationUrn;
            };

            // 알림 타입에 따라 아이콘 등을 다르게 할 수 있음 (추후 확장)

            item.innerHTML = `
                <div class="notification-content">
                    <p>${noti.notificationContents}</p>
                    <div class="timestamp">${/* new Date(noti.notificationCreatedAt).toLocaleString() */'방금 전'}</div>
                </div>
            `;
            notificationList.appendChild(item);
        });
    };

    // --- 이벤트 리스너 설정 ---

    // 알림 아이콘 클릭 시 팝업 토글 및 데이터 로드
    if (notificationIcon) {
        notificationIcon.addEventListener('click', (e) => {
            e.preventDefault(); // a 태그의 기본 동작(페이지 이동) 막기
            e.stopPropagation(); // 이벤트 버블링 방지

            const isHidden = notificationPopup.classList.contains('hidden');
            if (isHidden) {
                // 팝업 열기 & 알림 목록 새로고침
                notificationPopup.classList.remove('hidden');
                fetchNotifications();
                // 뱃지 숨기기 (팝업을 열면 확인한 것으로 간주)
                notificationBadge.classList.add('hidden');
                notificationBadge.textContent = '0';
            } else {
                // 팝업 닫기
                notificationPopup.classList.add('hidden');
            }
        });
    }

    // 팝업 내부 클릭 시 닫히지 않도록
    if (notificationPopup) {
        notificationPopup.addEventListener('click', (e) => {
            e.stopPropagation();
        });
    }

    // 화면의 다른 곳을 클릭하면 팝업 닫기
    window.addEventListener('click', () => {
        if (notificationPopup && !notificationPopup.classList.contains('hidden')) {
            notificationPopup.classList.add('hidden');
        }
    });

    // --- SSE 연결 로직 (기존 코드와 통합) ---
    if (memberId && memberId !== '0') {
        const eventSource = new EventSource(`/api/notification/subscribe/${memberId}`);

        eventSource.addEventListener('sse', function (event) {
            console.log("새로운 SSE 알림 수신:", event.data);

            // 뱃지 숫자 업데이트
            let currentCount = parseInt(notificationBadge.textContent) || 0;
            const newCount = currentCount + 1;
            notificationBadge.textContent = newCount;
            notificationBadge.classList.remove('hidden');

            // 팝업이 열려있다면, 목록 맨 위에 새 알림 추가
            if (!notificationPopup.classList.contains('hidden')) {
                fetchNotifications(); // 간단하게 목록 전체를 새로고침
            }

            // 브라우저 팝업 알림 표시
            try {
                const data = JSON.parse(event.data);
                if (data.notificationContents) {
                    new Notification('언니어때 새 알림', { body: data.notificationContents });
                }
            } catch (e) {
                console.error("알림 데이터 파싱 실패", e);
            }
        });

        eventSource.onerror = function(error) {
            console.error('EventSource 에러 발생:', error);
            eventSource.close();
        };
    }
});