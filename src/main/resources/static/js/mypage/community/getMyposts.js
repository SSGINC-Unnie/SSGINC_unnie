document.addEventListener('DOMContentLoaded', () => {
    // 필요한 DOM 요소들을 미리 가져오기
    const contentsDiv = document.getElementById('contents');
    const paginationDiv = document.getElementById('pagination');

    // 현재 페이지 상태 관리
    const state = {
        page: 1,
    };

    /**
     * '나의 게시글' 목록을 서버에서 불러와 새로운 UI로 렌더링합니다.
     * @param {number} page - 불러올 페이지 번호
     */
    const loadBoards = async (page = 1) => {
        state.page = page;
        contentsDiv.innerHTML = ''; // 로딩 시작 전 기존 내용 비우기

        try {
            const response = await fetch(`/api/mypage/community/board?page=${state.page}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const result = await response.json();

            if (result.status === 200) {
                const boards = result.data.boards.list;
                const pageInfo = result.data.boards;

                // ▼▼▼ 이 부분이 새로운 UI를 그리는 핵심입니다 ▼▼▼
                if (!boards || boards.length === 0) {
                    contentsDiv.innerHTML = '<p style="text-align: center; color: #888; grid-column: 1 / -1;">작성한 게시글이 없습니다.</p>';
                    renderPagination(pageInfo); // 페이지가 0개여도 페이지네이션은 렌더링 (컨테이너 비우기)
                    return;
                }

                boards.forEach(board => {
                    const postHTML = `
                        <div class="post" data-id="${board.boardId}" onclick="location.href='/community/board/${board.boardId}'">
                            
                            <div class="post-header">
                                <p class="post-title">${board.boardTitle}</p>
                            </div>

                            <div class="img-wrapper">
                                <img src="${board.boardThumbnail || '/img/default_thumbnail.png'}" alt="게시글 썸네일" class="post-image">
                            </div>

                            <div class="post-footer">
                                <div class="boards-info">
                                    <div class="boards-like">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"></path></svg>
                                        <span>${board.likeCount}</span>
                                    </div>
                                    <div class="boards-comment">
                                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"></path></svg>
                                        <span>${board.commentCount}</span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    `;
                    contentsDiv.insertAdjacentHTML('beforeend', postHTML);
                });

                renderPagination(pageInfo);
            } else {
                contentsDiv.innerHTML = `<p>${result.message}</p>`;
            }
        } catch (error) {
            console.error('Fetch error:', error);
            contentsDiv.innerHTML = '<p>게시글을 불러오는 데 실패했습니다.</p>';
        }
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
        prevBtn.addEventListener('click', () => loadBoards(pageInfo.prePage));
        paginationDiv.appendChild(prevBtn);

        // 페이지 번호 버튼
        pageInfo.navigatepageNums.forEach(pageNum => {
            const pageBtn = document.createElement('button');
            pageBtn.className = 'page-btn';
            pageBtn.textContent = pageNum;
            if (pageNum === pageInfo.pageNum) {
                pageBtn.disabled = true;
            }
            pageBtn.addEventListener('click', () => loadBoards(pageNum));
            paginationDiv.appendChild(pageBtn);
        });

        // 다음 페이지 버튼
        const nextBtn = document.createElement('button');
        nextBtn.className = 'page-btn';
        nextBtn.textContent = '>';
        nextBtn.disabled = !pageInfo.hasNextPage;
        nextBtn.addEventListener('click', () => loadBoards(pageInfo.nextPage));
        paginationDiv.appendChild(nextBtn);
    };

    // 페이지 초기화
    loadBoards(1);
});