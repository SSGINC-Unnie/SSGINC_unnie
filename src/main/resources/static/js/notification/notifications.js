document.addEventListener('DOMContentLoaded', () => {
  // --- DOM 요소 가져오기 ---
  const notificationList = document.getElementById('notification-list');
  const paginationDiv = document.getElementById('pagination');
  const selectAllCheckbox = document.getElementById('select-all-checkbox');
  const deleteSelectedBtn = document.getElementById('delete-selected-btn');
  const deleteAllBtn = document.getElementById('delete-all-btn');

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
      document.querySelector('.notification-actions').style.display = 'none';
      return;
    }

    // 알림이 있으면 삭제 버튼 보이기
    document.querySelector('.notification-actions').style.display = 'flex';

    notifications.forEach(noti => {
      const item = document.createElement('li');
      item.className = 'notification-item';
      if (!noti.notificationIsRead) {
        item.classList.add('unread');
      }

      // 체크박스와 삭제 버튼을 위한 HTML 구조
      item.innerHTML = `
                <input type="checkbox" class="notification-item-checkbox" data-id="${noti.notificationId}">
                <div class="notification-content" data-url="${noti.notificationUrn}">
                    <p>${noti.notificationContents}</p>
                    <div class="timestamp">${new Date(noti.notificationCreatedAt).toLocaleString()}</div>
                </div>
            `;

      // 내용 클릭 시 페이지 이동 (읽음 처리 로직 포함)
      item.querySelector('.notification-content').addEventListener('click', async function() {
        // 읽지 않은 알림일 경우에만 '읽음' 처리 API 호출
        if (item.classList.contains('unread')) {
          try {
            await fetch(`/api/notification/${noti.notificationId}/read`, { method: 'PATCH' });
          } catch (e) {
            console.error("읽음 처리 API 호출 실패", e);
          }
        }
        location.href = this.dataset.url;
      });

      notificationList.appendChild(item);
    });
    selectAllCheckbox.checked = false;
  };

  /**
   * 페이지네이션 UI를 렌더링합니다.
   * @param {object} pageInfo - PageInfo 객체
   */
  const renderPagination = (pageInfo) => {
    paginationDiv.innerHTML = '';
    if (!pageInfo || !pageInfo.navigatepageNums) return;

    const prevBtn = document.createElement('button');
    prevBtn.className = 'page-btn';
    prevBtn.textContent = '<';
    prevBtn.disabled = !pageInfo.hasPreviousPage;
    prevBtn.addEventListener('click', () => loadNotifications(pageInfo.prePage));
    paginationDiv.appendChild(prevBtn);

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

    const nextBtn = document.createElement('button');
    nextBtn.className = 'page-btn';
    nextBtn.textContent = '>';
    nextBtn.disabled = !pageInfo.hasNextPage;
    nextBtn.addEventListener('click', () => loadNotifications(pageInfo.nextPage));
    paginationDiv.appendChild(nextBtn);
  };

  // --- 이벤트 리스너 ---

  // '전체 선택' 체크박스
  selectAllCheckbox.addEventListener('change', () => {
    const checkboxes = document.querySelectorAll('.notification-item-checkbox');
    checkboxes.forEach(checkbox => {
      checkbox.checked = selectAllCheckbox.checked;
    });
  });

  // '선택 삭제' 버튼
  deleteSelectedBtn.addEventListener('click', async () => {
    const checkedBoxes = document.querySelectorAll('.notification-item-checkbox:checked');
    if (checkedBoxes.length === 0) {
      alert('삭제할 알림을 선택해주세요.');
      return;
    }

    if (confirm(`${checkedBoxes.length}개의 알림을 정말 삭제하시겠습니까?`)) {
      const idsToDelete = Array.from(checkedBoxes).map(cb => parseInt(cb.dataset.id));

      try {
        const response = await fetch('/api/notification', {
          method: 'DELETE',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ ids: idsToDelete })
        });

        if (response.ok) {
          loadNotifications(1); // 성공 시 목록을 새로고침
        } else {
          alert('삭제에 실패했습니다.');
        }
      } catch (error) {
        console.error("선택 삭제 실패:", error);
        alert('삭제 중 오류가 발생했습니다.');
      }
    }
  });

  // '전체 삭제' 버튼
  deleteAllBtn.addEventListener('click', async () => {
    if (confirm('모든 알림을 정말 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
      try {
        const response = await fetch('/api/notification', { method: 'DELETE' });

        if (response.ok) {
          loadNotifications(1); // 성공 시 목록을 새로고침
        } else {
          alert('전체 삭제에 실패했습니다.');
        }
      } catch (error) {
        console.error("전체 삭제 실패:", error);
        alert('삭제 중 오류가 발생했습니다.');
      }
    }
  });

  // 첫 페이지 로드
  loadNotifications(1);
});