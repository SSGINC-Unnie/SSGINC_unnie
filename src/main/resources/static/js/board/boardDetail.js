document.addEventListener('DOMContentLoaded', () => {
    // --- DOM 요소 가져오기 ---
    const authorProfile = document.getElementById('author-profile');
    const authorNickname = document.getElementById('author-nickname');
    const authorActions = document.getElementById('author-actions');
    const postTitle = document.getElementById('post-title');
    const postCreatedAt = document.getElementById('post-created-at');
    const postViews = document.getElementById('post-views');
    const postThumbnail = document.getElementById('post-thumbnail');
    const postBody = document.getElementById('post-body');
    const likeButton = document.getElementById('like-button');
    const likeCount = document.getElementById('like-count');
    const commentCountSpan = document.getElementById('comment-count');
    const commentInput = document.getElementById('comment-input');
    const commentSubmitBtn = document.getElementById('comment-submit-btn');
    const commentListDiv = document.getElementById('comment-list');
    const loginModal = document.getElementById('login-required-modal');
    const closeModalBtn = document.getElementById('modal-close-btn');
    const deleteButton = document.getElementById('delete-button');
    const editButton = document.getElementById('edit-button');
    const reportPostBtn = document.getElementById('report-post-btn');
    const reportModal = document.getElementById('report-modal');
    const reportForm = document.getElementById('report-form');
    const cancelReportBtn = document.getElementById('cancel-report-btn');


    // --- 상태 변수 ---
    const boardId = window.location.pathname.split('/').pop();
    let currentLikeId = 0;
    let isLoggedIn = false;

    // --- 함수 정의 ---

    // 게시글 좋아요 버튼 상태 업데이트
    const updateLikeButton = (isLiked) => {
        if (isLiked) {
            likeButton.classList.add('liked');
        } else {
            likeButton.classList.remove('liked');
        }
    };

    // 1. 게시글 상세 정보 불러오기
    const fetchBoardDetail = async () => {
        try {
            const response = await fetch(`/api/community/board/${boardId}`);
            const result = await response.json();
            if (response.ok) {
                const board = result.data.board;
                isLoggedIn = result.data.isLoggedIn;

                authorProfile.src = board.authorProfile;
                authorNickname.textContent = board.authorNickname;
                postTitle.textContent = board.boardTitle;
                postCreatedAt.textContent = new Date(board.boardCreatedAt).toLocaleString();
                likeCount.textContent = board.likeCount;
                currentLikeId = board.likeId;
                postViews.textContent = board.boardViews;


                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = board.boardContents;
                const firstImg = tempDiv.querySelector('img');
                if (firstImg) {
                    postThumbnail.src = firstImg.src;
                    postThumbnail.style.display = 'block';
                } else {
                    document.getElementById('post-image-container').style.display = 'none';
                }

                const contentWithoutImages = board.boardContents.replace(/<img[^>]*>/g, "");
                postBody.innerHTML = contentWithoutImages;

                if (board.owner) {
                    authorActions.style.display = 'block';
                }
                updateLikeButton(board.liked);
            } else {
                alert(result.message || '게시글을 불러올 수 없습니다.');
            }
        } catch (error) { console.error('Error fetching board:', error); }
    };

    // 2. 댓글 불러오기
    const fetchComments = async () => {
        try {
            const response = await fetch(`/api/community/comment?boardId=${boardId}&page=1`);
            const result = await response.json();
            if (response.ok) {
                const pageInfo = result.data.pageInfo;
                const rootCommentCount = result.data.rootCommentCount;

                commentCountSpan.textContent = rootCommentCount;
                renderComments(pageInfo.list);
            }
        } catch (error) { console.error('Error fetching comments:', error); }
    };

    // 3. 댓글 화면에 그리기
    const renderComments = (commentList) => {
        commentListDiv.innerHTML = '';
        const commentMap = new Map();
        const rootComments = [];
        commentList.forEach(comment => {
            comment.children = [];
            commentMap.set(comment.commentId, comment);
            if (comment.commentParentId === 0) rootComments.push(comment);
        });
        commentList.forEach(comment => {
            if (comment.commentParentId !== 0) {
                const parent = commentMap.get(comment.commentParentId);
                if (parent) parent.children.push(comment);
            }
        });
        rootComments.forEach(comment => {
            const commentWrapper = document.createElement('div');
            commentWrapper.className = 'comment-wrapper';
            commentWrapper.appendChild(createCommentElement(comment));
            const repliesContainer = document.createElement('div');
            repliesContainer.className = 'replies-container';
            if (comment.children.length > 0) {
                comment.children.forEach(reply => {
                    repliesContainer.appendChild(createCommentElement(reply, true));
                });
            }
            commentWrapper.appendChild(repliesContainer);
            commentListDiv.appendChild(commentWrapper);
        });
    };

    // 4. 개별 댓글 HTML 요소 생성
    const createCommentElement = (comment, isReply = false) => {
        const div = document.createElement('div');
        div.className = `comment-item ${isReply ? 'reply' : ''}`;
        div.setAttribute('data-comment-id', comment.commentId);
        const userLikeId = comment.likeId || 0;
        const ownerButtons = comment.owner ? `
            <div class="comment-actions">
                <button class="edit-comment-btn">수정</button>
                <button class="delete-comment-btn">삭제</button>
            </div>` : '';
        div.innerHTML = `
            <img src="${comment.memberProfile}" alt="프로필" class="comment-author-profile">
            <div class="comment-body">
                <span class="comment-author-name">${comment.memberNickname}</span>
                <p class="comment-content">${comment.commentContents}</p>
                <div class="comment-meta">
                    <span>${new Date(comment.commentCreatedAt).toLocaleString()}</span>
                    <button class="like-btn ${comment.liked ? 'liked' : ''}" data-like-id="${userLikeId}">
                        좋아요 <span class="comment-like-count">${comment.likeCount}</span>
                    </button>
                    ${!isReply ? `<button class="reply-btn">답글달기</button>` : ''}
                    ${ownerButtons}
                </div>
            </div>`;
        return div;
    };

    // 5. 대댓글 작성 폼 생성
    const createReplyForm = (parentId) => {
        const formWrapper = document.createElement('div');
        formWrapper.className = 'comment-form-wrapper reply-form-wrapper';
        formWrapper.innerHTML = `
            <textarea placeholder="대댓글을 입력하세요..."></textarea>
            <div class="form-actions">
                <button class="cancel-reply-btn">취소</button>
                <button class="submit-reply-btn">등록</button>
            </div>`;
        formWrapper.querySelector('.cancel-reply-btn').addEventListener('click', () => formWrapper.remove());
        formWrapper.querySelector('.submit-reply-btn').addEventListener('click', async () => {
            const content = formWrapper.querySelector('textarea').value.trim();
            if (!content) {
                alert('대댓글 내용을 입력해주세요.');
                return;
            }
            try {
                const formData = new FormData();
                formData.append('commentBoardId', boardId);
                formData.append('commentParentId', parentId);
                formData.append('commentContents', content);
                const response = await fetch('/api/community/comment/reply', { method: 'POST', body: formData });
                if (response.ok) fetchComments();
                else alert('대댓글 등록에 실패했습니다.');
            } catch (error) { console.error('Error creating reply:', error); }
        });
        return formWrapper;
    };

    // --- 이벤트 리스너 ---

    editButton.addEventListener('click', () => {
        window.location.href = `/community/board/${boardId}/edit`;
    });

    reportPostBtn.addEventListener('click', () => {
        if (!isLoggedIn) {
            loginModal.classList.remove('hidden');
            return;
        }
        reportModal.classList.remove('hidden');
    });

    cancelReportBtn.addEventListener('click', () => {
        reportModal.classList.add('hidden');
        reportForm.reset();
    });

    reportModal.addEventListener('click', (e) => {
        if (e.target === reportModal) {
            reportModal.classList.add('hidden');
            reportForm.reset();
        }
    });

    reportForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const selectedReason = reportForm.querySelector('input[name="reason"]:checked');
        if (!selectedReason) {
            alert('신고 사유를 선택해주세요.');
            return;
        }

        const reportData = {
            targetType: 1, // 1: 게시글
            targetId: boardId,
            reason: selectedReason.value,
            reasonDetailed: document.getElementById('report-reason-detailed').value
        };

        try {
            const response = await fetch('/api/report', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(reportData)
            });

            if (response.ok) {
                alert('신고가 정상적으로 접수되었습니다.');
            } else {
                const result = await response.json();
                alert(result.message || '신고 접수에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error submitting report:', error);
            alert('오류가 발생하여 신고에 실패했습니다.');
        } finally {
            reportModal.classList.add('hidden');
            reportForm.reset();
        }
    });

    //게시글 삭제
    deleteButton.addEventListener('click', async () => {
        if (confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
            try {
                const response = await fetch(`/api/community/board/${boardId}`, {
                    method: 'PATCH'
                });
                const result = await response.json();

                if (response.ok) {
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

    // 새 댓글 등록
    commentSubmitBtn.addEventListener('click', async () => {
        const content = commentInput.value.trim();
        if (!isLoggedIn) {
            loginModal.classList.remove('hidden');
            return;
        }
        if (!content) {
            alert('댓글 내용을 입력해주세요.');
            return;
        }
        try {
            const formData = new FormData();
            formData.append('commentBoardId', boardId);
            formData.append('commentContents', content);
            const response = await fetch('/api/community/comment', { method: 'POST', body: formData });
            if (response.ok) {
                commentInput.value = '';
                fetchComments();
            } else {
                alert('댓글 등록에 실패했습니다.');
            }
        } catch (error) { console.error('Error creating comment:', error); }
    });

    // 게시글 좋아요
    likeButton.addEventListener('click', async () => {
        if (!isLoggedIn) {
            loginModal.classList.remove('hidden');
            return;
        }
        const isLiked = likeButton.classList.contains('liked');
        if (isLiked) {
            try {
                const response = await fetch(`/api/like?likeId=${currentLikeId}`, { method: 'DELETE' });
                if (response.ok) {
                    updateLikeButton(false);
                    likeCount.textContent = parseInt(likeCount.textContent) - 1;
                } else { alert('좋아요 취소에 실패했습니다.'); }
            } catch (error) { console.error('Error deleting like:', error); }
        } else {
            try {
                const formData = new FormData();
                formData.append('likeTargetType', 'BOARD');
                formData.append('likeTargetId', boardId);
                const response = await fetch('/api/like', { method: 'POST', body: formData });
                const result = await response.json();
                if (response.ok) {
                    updateLikeButton(true);
                    likeCount.textContent = parseInt(likeCount.textContent) + 1;
                    currentLikeId = result.data.like;
                } else { alert('좋아요 추가에 실패했습니다.'); }
            } catch (error) { console.error('Error creating like:', error); }
        }
    });

    // 댓글 목록 이벤트 위임 (좋아요, 답글, 수정, 삭제)
    commentListDiv.addEventListener('click', async (e) => {
        const target = e.target;

        const likeBtn = target.closest('.like-btn');
        const replyBtn = target.closest('.reply-btn');
        const deleteBtn = target.closest('.delete-comment-btn');
        const editBtn = target.closest('.edit-comment-btn');

        if (likeBtn) {
            if (!isLoggedIn) {
                loginModal.classList.remove('hidden');
                return;
            }
            const commentItem = likeBtn.closest('.comment-item');
            const commentId = commentItem.dataset.commentId;
            const likeCountSpan = likeBtn.querySelector('.comment-like-count');
            let currentCommentLikeId = likeBtn.dataset.likeId;

            if (likeBtn.classList.contains('liked')) {
                try {
                    const response = await fetch(`/api/like?likeId=${currentCommentLikeId}`, { method: 'DELETE' });
                    if (response.ok) {
                        likeBtn.classList.remove('liked');
                        likeCountSpan.textContent = parseInt(likeCountSpan.textContent) - 1;
                        likeBtn.dataset.likeId = 0;
                    }
                } catch (error) { console.error('Error deleting comment like:', error); }
            } else {
                try {
                    const formData = new FormData();
                    formData.append('likeTargetType', 'COMMENT');
                    formData.append('likeTargetId', commentId);
                    const response = await fetch('/api/like', { method: 'POST', body: formData });
                    const result = await response.json();
                    if (response.ok) {
                        likeBtn.classList.add('liked');
                        likeCountSpan.textContent = parseInt(likeCountSpan.textContent) + 1;
                        likeBtn.dataset.likeId = result.data.like;
                    }
                } catch (error) { console.error('Error creating comment like:', error); }
            }
        } else if (replyBtn) {
            if (!isLoggedIn) {
                loginModal.classList.remove('hidden');
                return;
            }
            const existingForms = document.querySelectorAll('.reply-form-wrapper');
            existingForms.forEach(form => form.remove());

            const commentWrapper = replyBtn.closest('.comment-wrapper');
            const parentId = commentWrapper.querySelector('.comment-item').dataset.commentId;
            const repliesContainer = commentWrapper.querySelector('.replies-container');

            const replyForm = createReplyForm(parentId);
            repliesContainer.appendChild(replyForm);
            replyForm.querySelector('textarea').focus();
        } else if (deleteBtn) {
            if (!confirm('정말로 이 댓글을 삭제하시겠습니까?')) return;
            const commentItem = deleteBtn.closest('.comment-item');
            const commentId = commentItem.dataset.commentId;
            try {
                const response = await fetch(`/api/community/comment/${commentId}`, { method: 'PATCH' });
                if (response.ok) {
                    fetchComments();
                } else {
                    alert('댓글 삭제에 실패했습니다.');
                }
            } catch (error) { console.error('Error deleting comment:', error); }
        } else if (editBtn) {
            const commentItem = editBtn.closest('.comment-item');
            const commentBody = commentItem.querySelector('.comment-body');
            const contentP = commentBody.querySelector('.comment-content');

            if (commentBody.querySelector('.comment-edit-form')) return;

            const originalContent = contentP.textContent;
            const editForm = document.createElement('div');
            editForm.className = 'comment-edit-form';
            editForm.innerHTML = `
                <textarea>${originalContent}</textarea>
                <div class="form-actions">
                    <button class="cancel-edit-btn">취소</button>
                    <button class="submit-edit-btn">저장</button>
                </div>`;

            contentP.style.display = 'none';
            commentBody.insertBefore(editForm, commentBody.querySelector('.comment-meta'));

            editForm.querySelector('.cancel-edit-btn').addEventListener('click', () => {
                editForm.remove();
                contentP.style.display = 'block';
            });

            editForm.querySelector('.submit-edit-btn').addEventListener('click', async () => {
                const newContent = editForm.querySelector('textarea').value;
                const commentId = commentItem.dataset.commentId;
                try {
                    const formData = new FormData();
                    formData.append('commentId', commentId);
                    formData.append('commentContents', newContent);
                    formData.append('commentBoardId', boardId);

                    const response = await fetch('/api/community/comment', { method: 'PUT', body: formData });
                    if (response.ok) {
                        fetchComments();
                    } else {
                        alert('댓글 수정에 실패했습니다.');
                    }
                } catch (error) { console.error('Error updating comment:', error); }
            });
        }
    });

    // 모달 닫기
    if (closeModalBtn) {
        closeModalBtn.addEventListener('click', () => {
            loginModal.classList.add('hidden');
        });
    }

    // --- 페이지 초기화 ---
    const init = async () => {
        await fetchBoardDetail();
        await fetchComments();
    };

    init();
});