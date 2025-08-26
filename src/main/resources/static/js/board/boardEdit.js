document.addEventListener('DOMContentLoaded', () => {
    const boardForm = document.getElementById('board-form');
    const categorySelect = document.getElementById('category');
    const titleInput = document.getElementById('title');
    const contentTextarea = document.getElementById('content');
    const cancelButton = document.getElementById('cancel-button');

    const boardId = window.location.pathname.split('/')[3]; // /community/board/{id}/edit

    // 1. 기존 게시글 데이터 불러오기
    const fetchExistingData = async () => {
        try {
            const response = await fetch(`/api/community/board/${boardId}`);
            const result = await response.json();

            if (response.ok) {
                const board = result.data.board;
                // 2. 폼에 기존 데이터 채우기
                categorySelect.value = board.boardCategory;
                titleInput.value = board.boardTitle;
                contentTextarea.value = board.boardContents;
            } else {
                alert('게시글 정보를 불러오는데 실패했습니다.');
                window.location.href = '/community/board';
            }
        } catch (error) {
            console.error('Error fetching board data for edit:', error);
        }
    };

    // 3. 폼 제출(수정) 이벤트
    boardForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(boardForm);
        formData.append('boardId', boardId); // 수정할 게시글 ID 추가

        try {
            const response = await fetch('/api/community/board', { // PUT 요청
                method: 'PUT',
                body: formData
            });
            const result = await response.json();

            if (response.ok) {
                alert('게시글이 성공적으로 수정되었습니다.');
                window.location.href = `/community/board/${boardId}`; // 수정된 글로 이동
            } else {
                alert(result.message || '게시글 수정에 실패했습니다.');
            }
        } catch(error) {
            console.error('Error updating board:', error);
        }
    });

    cancelButton.addEventListener('click', () => {
        if(confirm('수정을 취소하시겠습니까? 변경사항이 저장되지 않습니다.')) {
            window.history.back();
        }
    });

    fetchExistingData();
});