document.addEventListener('DOMContentLoaded', function () {

    // --- DOM 요소 가져오기 ---
    const memberId = document.body.dataset.memberId;
    const notificationIcon = document.querySelector('.notification-icon');
    const notificationPopup = document.getElementById('header-notification-popup');
    const headerNotificationList = document.getElementById('header-notification-list');
    const notificationBadge = document.querySelector('.notification-badge');
    const clearAllBtn = document.getElementById('clear-all-notifications-btn');


    const initializeNotificationCount = async () => {
        if (!memberId || memberId === '0') return;
        try {
            const response = await fetch('/api/notification/count');
            if (!response.ok) return;

            const result = await response.json();
            const count = result.data.count;

            if (count > 0) {
                notificationBadge.textContent = count;
                notificationBadge.classList.remove('hidden');
            } else {
                notificationBadge.classList.add('hidden');
            }
        } catch (error) {
            console.error("알림 수 초기화 실패:", error);
        }
    };

    // --- 알림 목록 불러오기 함수 ---
    const fetchNotifications = async () => {
        if (!memberId || memberId === '0') return;
        try {
            const response = await fetch('/api/notification');
            if (!response.ok) throw new Error('알림 목록을 불러오는데 실패했습니다.');
            const result = await response.json();
            renderPopupNotifications(result.data.notifications);
        } catch (error) {
            console.error(error);
            if(headerNotificationList) headerNotificationList.innerHTML = '<li class="notification-empty">알림을 불러올 수 없습니다.</li>';
        }
    };

    // --- 알림 목록을 화면에 그리는 함수 ---
    const renderPopupNotifications = (notifications) => {
        if (!headerNotificationList) return;
        headerNotificationList.innerHTML = '';
        if (!notifications || notifications.length === 0) {
            headerNotificationList.innerHTML = '<li class="notification-empty">새로운 알림이 없습니다.</li>';
            return;
        }

        notifications.forEach(noti => {
            const item = document.createElement('li');
            item.className = 'notification-item unread';

            item.onclick = async () => {
                try {
                    const response = await fetch(`/api/notification/${noti.notificationId}/read`, {
                        method: 'PATCH'
                    });

                    if (response.ok) {
                        // 성공 시 화면에서 즉시 제거
                        item.remove();
                        // 만약 목록이 비게 되면 "없음" 메시지 표시
                        if (headerNotificationList.children.length === 0) {
                            headerNotificationList.innerHTML = '<li class="notification-empty">새로운 알림이 없습니다.</li>';
                        }
                    }
                } catch (error) {
                    console.error("알림 읽음 처리 실패:", error);
                } finally {
                    // 페이지 이동은 항상 마지막에 실행
                    location.href = noti.notificationUrn;
                }
            };

            item.innerHTML = `
                <div class="notification-content">
                    <p>${noti.notificationContents}</p>
                    <div class="timestamp">방금 전</div>
                </div>
            `;
            headerNotificationList.appendChild(item);
        });
    };

    // --- 이벤트 리스너 설정 ---

    // 알림 아이콘 클릭 시
    if (notificationIcon) {
        notificationIcon.addEventListener('click', (e) => {
            e.preventDefault();
            e.stopPropagation();
            const isHidden = notificationPopup.classList.contains('hidden');
            if (isHidden) {
                notificationPopup.classList.remove('hidden');
                fetchNotifications();
                notificationBadge.classList.add('hidden');
                notificationBadge.textContent = '0';
            } else {
                notificationPopup.classList.add('hidden');
            }
        });
    }

    if (clearAllBtn) {
        clearAllBtn.addEventListener('click', async (e) => {
            e.stopPropagation();
            try {
                const response = await fetch('/api/notification/read-all', {
                    method: 'PATCH'
                });
                if (response.ok) {
                    // 성공 시 화면의 목록을 비우고 "없음" 메시지 표시
                    if(headerNotificationList) headerNotificationList.innerHTML = '<li class="notification-empty">새로운 알림이 없습니다.</li>';
                }
            } catch (error) {
                console.error("모든 알림 읽음 처리 실패:", error);
            }
        });
    }

    if (notificationPopup) {
        notificationPopup.addEventListener('click', (e) => e.stopPropagation());
    }

    // 화면의 다른 곳을 클릭하면 팝업 닫기
    window.addEventListener('click', () => {
        if (notificationPopup && !notificationPopup.classList.contains('hidden')) {
            notificationPopup.classList.add('hidden');
        }
    });

    // --- SSE 연결 로직 ---
    if (memberId && memberId !== '0') {

        initializeNotificationCount();

        const eventSource = new EventSource(`/api/notification/subscribe/${memberId}`);
        eventSource.addEventListener('sse', function (event) {
            console.log("새로운 SSE 알림 수신:", event.data);

            let currentCount = parseInt(notificationBadge.textContent) || 0;
            const newCount = currentCount + 1;
            notificationBadge.textContent = newCount;
            notificationBadge.classList.remove('hidden');


            if (!notificationPopup.classList.contains('hidden')) {
                fetchNotifications();
            }

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