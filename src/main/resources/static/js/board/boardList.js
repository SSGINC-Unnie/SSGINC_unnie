// DOM이 모두 로드되었을 때 스크립트 실행
document.addEventListener('DOMContentLoaded', () => {

    // 필요한 DOM 요소들을 미리 가져오기
    const contentsDiv = document.getElementById('contents');
    const paginationDiv = document.getElementById('pagination');
    const categoryTabs = document.querySelectorAll('.category-tab');
    const sortDropdown = document.getElementById('sort-dropdown');
    const searchTypeDropdown = document.getElementById('search-type-dropdown');
    const searchInput = document.getElementById('search-input');
    const searchButton = document.getElementById('search-button');

    // 현재 페이지 및 검색 조건을 관리하는 상태 객체
    const state = {
        page: 1,
        category: '공지 있어!', // 기본값
        sort: 'LATEST',     // 기본값
        searchType: 'TITLE',  // 기본값
        search: ''
    };

    // 1. API를 호출하여 게시글 데이터를 가져오는 함수
    const fetchBoards = async () => {
        contentsDiv.innerHTML = '<div class="loader"></div>'; // 로딩 시작

        const endpoint = '/api/community/board';

        const params = new URLSearchParams({
            page: state.page,
            category: state.category,
            sort: state.sort,
            searchType: state.searchType,
            search: state.search.trim()
        });

        try {
            const response = await fetch(`${endpoint}?${params.toString()}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const result = await response.json();

            if (result.code === 200) {
                renderBoards(result.data.boards.list);
                renderPagination(result.data.boards);
            } else {
                contentsDiv.innerHTML = `<p>${result.message}</p>`;
            }
        } catch (error) {
            console.error('Fetch error:', error);
            contentsDiv.innerHTML = '<p>게시글을 불러오는 데 실패했습니다.</p>';
        }
    };

    const renderBoards = (boards) => {
        contentsDiv.innerHTML = ''; // 기존 내용을 비웁니다.
        if (!boards || boards.length === 0) {
            contentsDiv.innerHTML = '<p>게시글이 없습니다.</p>';
            return;
        }

        boards.forEach(board => {
            const postHTML = `
                <div class="post" data-id="${board.boardId}" onclick="location.href='/community/board/${board.boardId}'">
                    <div class="address">
                        <div class="post-author">
                            <img src="/img/${board.memberProfile}" alt="프로필 이미지">
                            <span>${board.memberNickname}</span>
                        </div>
                        <span class="post-title">${board.boardTitle}</span>
                    </div>
                    <div class="img-wrapper">
                        <img src="/img/${board.boardThumbnail}" alt="post image" class="post-image">
                    </div>
                    <div class="boards-info flex-row">
                        <div class="boards-like">
                            <svg width="14" height="12" viewBox="0 0 14 12"><path d="M12.6109 3.97494C12.8109 6.44494 11.1509 8.82994 7.1509 10.8999C7.11829 10.9162 7.08235 10.9247 7.0459 10.9247C7.00945 10.9247 6.97351 10.9162 6.9409 10.8999C2.9409 8.82994 1.2809 6.44994 1.4809 3.96494C1.7709 1.23994 5.1409 0.149939 7.0509 2.34994C8.9509 0.149939 12.3259 1.23994 12.6159 3.97494H12.6109Z" stroke="#808080"/></svg>
                            <span class="like-count">${board.likeCount}</span>
                        </div>
                        <div class="boards-comment">
                           <svg width="12" height="12" viewBox="0 0 12 12"><path fill-rule="evenodd" clip-rule="evenodd" d="M7.52195 9.1572C10.0986 8.66318 12 6.94442 12 4.9C12 2.46995 9.31371 0.5 6 0.5C2.68629 0.5 0 2.46995 0 4.9C0 7.1094 2.22061 8.93847 5.11441 9.25241L6.29321 11.5L7.52195 9.1572Z" fill="#808080"/></svg>
                            <span class="comment-count">${board.commentCount}</span>
                        </div>
                    </div>
                    <p class="post-description">${board.boardTitle}</p>
                </div>
            `;
            contentsDiv.insertAdjacentHTML('beforeend', postHTML);
        });
    };

    const renderPagination = (pageInfo) => {
        paginationDiv.innerHTML = '';
        for (let i = pageInfo.startPage; i <= pageInfo.endPage; i++) {
            const pageBtn = document.createElement('button');
            pageBtn.className = 'page-btn';
            pageBtn.textContent = i;
            if (i === pageInfo.pageNum) {
                pageBtn.disabled = true;
            }
            pageBtn.addEventListener('click', () => {
                state.page = i;
                fetchBoards();
            });
            paginationDiv.appendChild(pageBtn);
        }
    };


    // 카테고리 탭 클릭 이벤트
    categoryTabs.forEach(tab => {
        tab.addEventListener('click', (e) => {
            // 모든 탭에서 'active' 클래스 제거
            categoryTabs.forEach(t => t.classList.remove('active'));
            // 클릭된 탭에 'active' 클래스 추가
            e.currentTarget.classList.add('active');

            state.category = e.currentTarget.dataset.category;
            state.page = 1;
            fetchBoards();
        });
    });

    // 정렬, 검색 유형 드롭다운 변경 이벤트
    [sortDropdown, searchTypeDropdown].forEach(dropdown => {
        dropdown.addEventListener('change', (e) => {
            if (e.currentTarget.id === 'sort-dropdown') {
                state.sort = e.currentTarget.value;
            } else {
                state.searchType = e.currentTarget.value;
            }
            state.page = 1;
            fetchBoards();
        });
    });

    // 검색 버튼 클릭 이벤트
    searchButton.addEventListener('click', () => {
        state.search = searchInput.value;
        state.page = 1;
        fetchBoards();
    });

    // 검색창에서 Enter 키 입력 이벤트
    searchInput.addEventListener('keyup', (e) => {
        if (e.key === 'Enter') {
            searchButton.click();
        }
    });

    // 페이지 최초 진입 시, 기본값으로 게시글 목록을 불러옵니다.
    fetchBoards();
});