// DOM이 모두 로드되었을 때 스크립트 실행
document.addEventListener('DOMContentLoaded', () => {

    // 필요한 DOM 요소들을 미리 가져오기
    const contentsDiv = document.getElementById('contents');
    const paginationDiv = document.getElementById('pagination');
    const sortDropdown = document.getElementById('sort-dropdown');
    const categoryWrapper = document.getElementById('category-dropdown-wrapper');
    const currentCategoryName = document.getElementById('current-category-name');
    const categoryDropdownList = document.getElementById('category-dropdown-list')

    // 현재 페이지 및 검색 조건을 관리하는 상태 객체
    const state = {
        page: 1,
        category: '공지 있어!', // 기본값
        sort: 'LATEST',     // 기본값
        searchType: 'TITLE', // 기본값

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
            <div class="post" data-id="${board.id}" onclick="location.href='/community/board/${board.id}'">
                
                <div class="post-header">
                    <div class="post-author">
                        <img src="${board.memberProfile}" alt="작성자 프로필">
                        <span>${board.memberNickname}</span>
                    </div>
                    <p class="post-description">${board.boardTitle}</p>
                </div>

                <div class="img-wrapper">
                    <img src="${board.boardThumbnail}" alt="게시글 썸네일" class="post-image">
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


    // 1. 카테고리 표시 영역을 클릭하면 드롭다운 목록을 열고 닫음
    categoryWrapper.addEventListener('click', (e) => {
        e.stopPropagation(); // 이벤트가 부모로 전파되는 것을 막음
        categoryDropdownList.classList.toggle('hidden');
    });

    // 2. 드롭다운 목록에서 특정 카테고리를 클릭했을 때의 동작
    categoryDropdownList.addEventListener('click', (e) => {
        if (e.target.classList.contains('category-item')) {
            const selectedCategory = e.target.dataset.category;

            state.category = selectedCategory;
            state.page = 1;

            currentCategoryName.textContent = selectedCategory; // 화면 표시 업데이트
            fetchBoards(); // 새 카테고리로 게시글 다시 불러오기
        }
    });

    // 3. 페이지의 다른 곳을 클릭하면 드롭다운 목록을 닫음
    window.addEventListener('click', () => {
        if (!categoryDropdownList.classList.contains('hidden')) {
            categoryDropdownList.classList.add('hidden');
        }
    });

    // --- 다른 이벤트 리스너 ---

    // 정렬 드롭다운 변경 시
    sortDropdown.addEventListener('change', (e) => {
        state.sort = e.target.value;
        state.page = 1;
        fetchBoards();
    });

    // --- 페이지 초기화 ---
    const init = () => {
        currentCategoryName.textContent = state.category; // 초기 카테고리 이름 설정
        fetchBoards();
    };

    init();
});