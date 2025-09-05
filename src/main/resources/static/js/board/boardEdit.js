$(document).ready(function() {
    // --- DOM 요소 ---
    const boardForm = $('#board-form');
    const categorySelect = $('#category');
    const titleInput = $('#title');
    const summernoteContent = $('#summernote-content');
    const attachmentList = $('#attachment-list');

    // --- 상태 변수 ---
    const boardId = window.location.pathname.split('/')[3];

    // Summernote 초기화
    summernoteContent.summernote({
        height: 300,
        lang: "ko-KR",
        placeholder: '내용을 입력해주세요',
        dialogsInBody: true,
        callbacks: {
            onImageUpload: function(files) {
                for (let i = 0; i < files.length; i++) {
                    sendFile(files[i], this);
                }
            }
        },
        toolbar: [
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
            ['style', ['bold', 'italic', 'underline','strikethrough', 'clear']],
            ['color', ['forecolor','color']],
            ['table', ['table']],
            ['para', ['ul', 'ol', 'paragraph']],
            ['height', ['height']],
            ['picture', ['picture']],
            ['insert',['link','video']],
            ['view', ['fullscreen', 'help']]
        ],
        fontNames: ['Arial', 'Arial Black', 'Comic Sans MS', 'Courier New','맑은 고딕','궁서','굴림체','돋움체','바탕체'],
        fontSizes: ['8','9','10','11','12','14','16','18','20','22','24','28','30','36','50','72']
    });

    // 1. 기존 게시글 데이터를 불러오는 함수
    const fetchAndPopulateData = async () => {
        try {
            const response = await fetch(`/api/community/board/${boardId}`);
            const result = await response.json();
            if (response.ok) {
                const board = result.data.board;
                categorySelect.val(board.boardCategory);
                titleInput.val(board.boardTitle);
                summernoteContent.summernote('code', board.boardContents);

                // 본문에서 이미지들을 파싱하여 첨부 파일 목록에 추가
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = board.boardContents;
                const images = tempDiv.querySelectorAll('img');
                images.forEach(img => {
                    if (img.src.includes('/upload/')) { // 업로드된 이미지만 처리
                        const fileName = img.src.split('/').pop();
                        addAttachmentPreview(fileName, img.src);
                    }
                });
            } else {
                alert('게시글 정보를 불러오는 데 실패했습니다.');
                window.location.href = '/community/board';
            }
        } catch (error) { console.error('Error fetching data:', error); }
    };

    // 2. Summernote에 이미지 업로드
    function sendFile(file, editor) {
        const formData = new FormData();
        formData.append('file', file);
        $.ajax({
            url: '/api/media/upload',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            success: function(response) {
                if (response.status === 201 || response.status === 200) {
                    const fileUrn = response.data.fileUrn;
                    $(editor).summernote('insertImage', fileUrn);
                    addAttachmentPreview(file.name, fileUrn);
                } else {
                    alert(response.message);
                }
            },
            error: function() {
                alert('이미지 업로드에 실패했습니다.');
            }
        });
    }

    // 3. 첨부 파일 미리보기 추가
    function addAttachmentPreview(fileName, fileUrn) {
        const attachmentItemHTML = `
            <div class="attachment-item" data-urn="${fileUrn}">
                <img src="${fileUrn}" alt="${fileName}" class="attachment-thumbnail">
                <span class="attachment-name">${fileName}</span>
                <button type="button" class="attachment-delete-btn" title="첨부 삭제">&times;</button>
            </div>`;
        attachmentList.append(attachmentItemHTML);
    }

    // 4. 첨부 파일 삭제 이벤트
    attachmentList.on('click', '.attachment-delete-btn', function() {
        const item = $(this).closest('.attachment-item');
        const fileUrnToDelete = item.data('urn');

        if (confirm("이 첨부파일을 삭제하시겠습니까? 에디터 본문에서도 이미지가 삭제됩니다.")) {
            const currentContent = summernoteContent.summernote('code');
            const imgTagRegex = new RegExp(`<img[^>]+src\\s*=\\s*['"]${fileUrnToDelete}['\"][^>]*>`, 'g');
            const updatedContent = currentContent.replace(imgTagRegex, '');
            summernoteContent.summernote('code', updatedContent);
            item.remove();
        }
    });

    // 5. 폼 제출(수정) 이벤트
    boardForm.on('submit', async function(e) {
        e.preventDefault();

        const content = summernoteContent.summernote('code');
        if (!content.includes('<img')) {
            alert('최소 1장의 이미지를 첨부해야 합니다.');
            return;
        }

        const formData = new FormData(this);
        formData.set('boardContents', content);
        formData.append('boardId', boardId);

        try {
            const response = await fetch('/api/community/board', {
                method: 'PUT',
                body: formData
            });
            const result = await response.json();
            if (response.ok) {
                alert('게시글이 성공적으로 수정되었습니다.');
                window.location.href = `/community/board/${boardId}`;
            } else {
                alert(result.message || '게시글 수정에 실패했습니다.');
            }
        } catch(error) { console.error('Error updating board:', error); }
    });

    // 취소 버튼
    $('#cancel-button').on('click', () => {
        if(confirm('수정을 취소하시겠습니까? 변경사항은 저장되지 않습니다.')) {
            window.history.back();
        }
    });

    // 페이지 로드 시 기존 데이터 불러오기 실행
    fetchAndPopulateData();
});