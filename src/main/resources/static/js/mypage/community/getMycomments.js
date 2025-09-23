document.addEventListener('DOMContentLoaded', () => {
    // 페이지가 로드되면 첫 번째 페이지의 댓글 목록을 불러옵니다.
    loadComments(1);
});

const heartIcon = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"></path></svg>`;
const commentIcon = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"></path></svg>`;


async function loadComments(page = 1) {
    try {
        const response = await fetch(`/api/mypage/community/comment?page=${page}`);
        if (!response.ok) throw new Error('데이터를 불러오는 데 실패했습니다.');

        const result = await response.json();
        const data = result.data.comments;

        const commentList = document.getElementById('comment-list');
        commentList.innerHTML = '';

        if (!data.list || data.list.length === 0) {
            commentList.innerHTML = `<div class="empty-state"><p>작성한 댓글이 없습니다.</p></div>`;
            document.getElementById('comment-pagination').innerHTML = '';
            return;
        }

        data.list.forEach(comment => {
            const li = document.createElement('li');
            li.className = 'comment-card';
            li.onclick = () => { location.href = `/community/board/${comment.boardId}#comment-${comment.commentId}`; };

            li.innerHTML = `
                <img src="${comment.boardThumbnail || '/img/default_thumbnail.png'}" alt="게시글 썸네일" class="comment-thumbnail">
                <div class="comment-details">
                    <p class="my-comment-content">${comment.commentContents}</p>
                    <div class="original-post-title" title="${comment.boardTitle}">
                        원문: ${comment.boardTitle}
                    </div>
                    <div class="comment-meta">
                        <span>${comment.commentCreatedAt.split(' ')[0]}</span>
                        <span class="meta-item">
                            ${heartIcon}
                            <span>${comment.likeCount}</span>
                        </span>
                        <span class="meta-item">
                            ${commentIcon}
                            <span>${comment.boardCommentCount}</span>
                        </span>
                    </div>
                </div>
            `;
            commentList.appendChild(li);
        });
        // 페이지네이션 UI를 렌더링합니다. (PageInfo 객체 전체를 전달)
        renderPagination(
            'comment-pagination',
            data.pages,
            data.pageNum,
            loadComments
        );
    } catch (error) {
        console.error(error);
        document.getElementById('comment-list').innerHTML = `<div class="empty-state"><p>오류가 발생했습니다. 다시 시도해주세요.</p></div>`;
    }
}

/**
 * 페이지네이션 UI를 동적으로 생성하고 이벤트 리스너를 추가합니다.
 * @param {string} containerId - 페이지네이션을 추가할 HTML 요소의 ID
 * @param {number} totalPages - 전체 페이지 수
 * @param {number} currentPage - 현재 활성화된 페이지 번호
 * @param {function} loadFn - 페이지 번호 클릭 시 호출될 데이터 로딩 함수 (e.g., loadComments)
 */
function renderPagination(containerId, totalPages, currentPage, loadFn) {
    const paginationContainer = document.getElementById(containerId);
    if (!paginationContainer) return;

    paginationContainer.innerHTML = ''; // 컨테이너 초기화
    if (totalPages <= 1) return; // 페이지가 하나 이하면 렌더링 안함

    // '이전' 버튼 생성
    const prevButton = document.createElement('a');
    prevButton.href = '#';
    prevButton.innerHTML = '&laquo;'; // «
    prevButton.className = 'page-link';
    if (currentPage === 1) {
        prevButton.classList.add('disabled'); // 첫 페이지일 경우 비활성화
    }
    prevButton.addEventListener('click', (e) => {
        e.preventDefault();
        if (currentPage > 1) {
            loadFn(currentPage - 1);
        }
    });
    paginationContainer.appendChild(prevButton);

    // 페이지 번호 버튼 생성
    for (let i = 1; i <= totalPages; i++) {
        const pageButton = document.createElement('a');
        pageButton.href = '#';
        pageButton.textContent = i;
        pageButton.className = 'page-link';
        if (i === currentPage) {
            pageButton.classList.add('active'); // 현재 페이지는 'active' 스타일 적용
        }
        pageButton.addEventListener('click', (e) => {
            e.preventDefault();
            if (i !== currentPage) {
                loadFn(i);
            }
        });
        paginationContainer.appendChild(pageButton);
    }

    // '다음' 버튼 생성
    const nextButton = document.createElement('a');
    nextButton.href = '#';
    nextButton.innerHTML = '&raquo;'; // »
    nextButton.className = 'page-link';
    if (currentPage === totalPages) {
        nextButton.classList.add('disabled'); // 마지막 페이지일 경우 비활성화
    }
    nextButton.addEventListener('click', (e) => {
        e.preventDefault();
        if (currentPage < totalPages) {
            loadFn(currentPage + 1);
        }
    });
    paginationContainer.appendChild(nextButton);
}