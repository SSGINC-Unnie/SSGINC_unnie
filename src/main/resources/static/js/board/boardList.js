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

            if (result.status === 200) {
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
        contentsDiv.innerHTML = '';
        if (!boards || boards.length === 0) {
            contentsDiv.innerHTML = '<p>게시글이 없습니다.</p>';
            return;
        }

        boards.forEach(board => {
            const postHTML = `
            <div class="post" data-id="${board.boardId}" onclick="location.href='/community/board/${board.boardId}'">
                <div class="img-wrapper">
                    <img src="${board.boardThumbnail}" alt="게시글 썸네일" class="post-image">
                </div>
                <div class="post-info-area">
                    <div class="post-author">
                        <img src="${board.memberProfile}" alt="작성자 프로필">
                        <span>${board.memberNickname}</span>
                    </div>
                    <p class="post-title">${board.boardTitle}</p>
                    <div class="boards-info">
                        <div class="boards-like">
                            <svg viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"></path></svg>
                            <span>${board.likeCount}</span>
                        </div>
                        <div class="boards-comment">
                            <svg viewBox="0 0 24 24"><path d="M21.99 4c0-1.1-.89-2-1.99-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18z"></path></svg>
                            <span>${board.commentCount}</span>
                        </div>
                    </div>
                </div>
            </div>
        `;
            contentsDiv.insertAdjacentHTML('beforeend', postHTML);
        });
    };

    const renderPagination = (pageInfo) => {
        console.log("페이지네이션 데이터:", pageInfo);

        paginationDiv.innerHTML = '';

        if (pageInfo && pageInfo.navigatepageNums) {

            pageInfo.navigatepageNums.forEach(pageNum => {
                const pageBtn = document.createElement('button');
                pageBtn.className = 'page-btn';
                pageBtn.textContent = pageNum;

                if (pageNum === pageInfo.pageNum) {
                    pageBtn.disabled = true;
                }

                pageBtn.addEventListener('click', () => {
                    state.page = pageNum;
                    fetchBoards();
                });

                paginationDiv.appendChild(pageBtn);
            });
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