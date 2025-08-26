document.addEventListener('DOMContentLoaded', () => {
    const postTitle = document.getElementById('post-title');
    const authorProfile = document.getElementById('author-profile');
    const authorNickname = document.getElementById('author-nickname');
    const postCreatedAt = document.getElementById('post-created-at');
    const postContent = document.getElementById('post-content');
    const authorActions = document.getElementById('author-actions');
    const editButton = document.getElementById('edit-button');
    const deleteButton = document.getElementById('delete-button');

    const boardId = window.location.pathname.split('/').pop();

    const fetchBoardDetail = async () => {
        try {
            const response = await fetch(`/api/community/board/${boardId}`);
            const result = await response.json();

            if (response.ok && result.code === 200) {
                const board = result.data.board;
                postTitle.textContent = board.boardTitle;
                authorProfile.src = `/img/${board.authorProfile || 'karina.png'}`;
                authorNickname.textContent = board.authorNickname;
                postCreatedAt.textContent = new Date(board.boardCreatedAt).toLocaleString();
                postContent.innerHTML = board.boardContents; // HTML 콘텐츠를 그대로 렌더링

                // 4. 작성자일 경우 수정/삭제 버튼 표시
                if (board.isOwner) {
                    authorActions.style.display = 'block';
                }

            } else {
                alert(result.message || '게시글을 불러올 수 없습니다.');
                window.location.href = '/community/board'; // 목록으로 이동
            }
        } catch (error) {
            console.error('Error fetching board detail:', error);
        }
    };

    // 5. 삭제 버튼 이벤트 리스너
    deleteButton.addEventListener('click', async () => {
        if (confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            try {
                const response = await fetch(`/api/community/board/${boardId}`, {
                    method: 'PATCH'
                });
                const result = await response.json();

                if (response.ok && result.code === 200) {
                    alert('게시글이 삭제되었습니다.');
                    window.location.href = '/community/board'; // 목록으로 이동
                } else {
                    alert(result.message || '삭제에 실패했습니다.');
                }
            } catch (error) {
                console.error('Error deleting board:', error);
            }
        }
    });

    // 6. 수정 버튼 이벤트 리스너
    editButton.addEventListener('click', () => {
        // 수정 페이지로 이동
        window.location.href = `/community/board/${boardId}/edit`;
    });


    fetchBoardDetail();
});