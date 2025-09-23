document.addEventListener('DOMContentLoaded', () => {
  const notificationList = document.getElementById('notification-list');
  const paginationDiv = document.getElementById('pagination');

  /**
   * 특정 페이지의 전체 알림 목록을 서버에서 불러와 화면에 렌더링합니다.
   * @param {number} page - 불러올 페이지 번호
   */
  const loadNotifications = async (page = 1) => {
    try {
      const response = await fetch(`/api/notification/all?page=${page}`);
      if (!response.ok) throw new Error('Failed to fetch notifications');

      const result = await response.json();
      const pageInfo = result.data.notifications;

      renderNotifications(pageInfo.list);
      renderPagination(pageInfo);

    } catch (error) {
      console.error(error);
      notificationList.innerHTML = '<li class="notification-empty">알림을 불러오는데 실패했습니다.</li>';
    }
  };

  /**
   * 알림 목록 데이터를 받아 HTML 요소를 생성합니다.
   * @param {Array} notifications - 알림 객체 배열
   */
  const renderNotifications = (notifications) => {
    notificationList.innerHTML = '';
    if (!notifications || notifications.length === 0) {
      notificationList.innerHTML = '<li class="notification-empty">표시할 알림이 없습니다.</li>';
      return;
    }

    notifications.forEach(noti => {
      const item = document.createElement('li');
      item.className = 'notification-item';
      // DTO의 'notificationIsRead' 필드명에 맞춰 수정 (Lombok getter는 isNotificationIsRead가 아닌 getNotificationIsRead로 생성될 수 있음)
      if (!noti.notificationIsRead) {
        item.classList.add('unread');
      }
      item.onclick = () => {
        // TODO: 알림 클릭 시 읽음 처리하는 API 호출 로직 추가
        location.href = noti.notificationUrn;
      };
      item.innerHTML = `
                <div class="notification-content">
                    <p>${noti.notificationContents}</p>
                    <div class="timestamp">${new Date(noti.notificationCreatedAt).toLocaleString()}</div>
                </div>
            `;
      notificationList.appendChild(item);
    });
  };

  /**
   * 페이지네이션 UI를 렌더링합니다.
   * @param {object} pageInfo - PageInfo 객체
   */
  const renderPagination = (pageInfo) => {
    paginationDiv.innerHTML = '';
    if (!pageInfo || !pageInfo.navigatepageNums) return;

    // 이전 페이지 버튼
    const prevBtn = document.createElement('button');
    prevBtn.className = 'page-btn';
    prevBtn.textContent = '<';
    prevBtn.disabled = !pageInfo.hasPreviousPage;
    prevBtn.addEventListener('click', () => loadNotifications(pageInfo.prePage));
    paginationDiv.appendChild(prevBtn);

    // 페이지 번호 버튼
    pageInfo.navigatepageNums.forEach(pageNum => {
      const pageBtn = document.createElement('button');
      pageBtn.className = 'page-btn';
      pageBtn.textContent = pageNum;
      if (pageNum === pageInfo.pageNum) {
        pageBtn.disabled = true;
      }
      pageBtn.addEventListener('click', () => loadNotifications(pageNum));
      paginationDiv.appendChild(pageBtn);
    });

    // 다음 페이지 버튼
    const nextBtn = document.createElement('button');
    nextBtn.className = 'page-btn';
    nextBtn.textContent = '>';
    nextBtn.disabled = !pageInfo.hasNextPage;
    nextBtn.addEventListener('click', () => loadNotifications(pageInfo.nextPage));
    paginationDiv.appendChild(nextBtn);
  };

  // 첫 페이지 로드
  loadNotifications(1);
});