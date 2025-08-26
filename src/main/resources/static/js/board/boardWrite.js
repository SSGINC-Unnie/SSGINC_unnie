$(document).ready(function() {

    const attachmentList = $('#attachment-list');

    // 1. Summernote 에디터 초기화
    $('#summernote-content').summernote({
        height: 300,
        minHeight: null,
        maxHeight: null,
        focus: true,
        lang: "ko-KR",
        placeholder: '내용을 입력해주세요',
        dialogsInBody: true,
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
        fontSizes: ['8','9','10','11','12','14','16','18','20','22','24','28','30','36','50','72'],
        callbacks: {
            onImageUpload: function(files) {
                for (let i = 0; i < files.length; i++) {
                    sendFile(files[i], this);
                }
            }
        }
    });

    /**
     * 이미지를 서버로 전송하고, 반환된 경로를 에디터와 첨부 파일 목록에 추가하는 함수
     */
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
                // ✅ 서버 응답 코드가 201(Created)이 아닌 200(OK)일 수 있으므로 함께 확인
                if (response.code === 201 || response.code === 200) {
                    const fileUrn = response.data.fileUrn;
                    $(editor).summernote('insertImage', fileUrn);
                    addAttachmentPreview(file.name, fileUrn);
                } else {
                    alert(response.message || '이미지 업로드 중 오류가 발생했습니다.');
                }
            },
            error: function() {
                alert('이미지 업로드에 실패했습니다.');
            }
        });
    }

    /**
     * 하단 첨부 파일 목록에 미리보기 아이템을 생성하고 추가하는 함수
     */
    function addAttachmentPreview(fileName, fileUrn) {
        const attachmentItemHTML = `
            <div class="attachment-item" data-urn="${fileUrn}">
                <img src="${fileUrn}" alt="${fileName}" class="attachment-thumbnail">
                <span class="attachment-name">${fileName}</span>
                <button type="button" class="attachment-delete-btn" title="첨부 삭제">&times;</button>
            </div>
        `;
        attachmentList.append(attachmentItemHTML);
    }

    /**
     * 첨부 파일 삭제 버튼 클릭 이벤트
     */
    attachmentList.on('click', '.attachment-delete-btn', function() {
        const item = $(this).closest('.attachment-item');
        const fileUrnToDelete = item.data('urn');

        if (confirm("이 첨부파일을 삭제하시겠습니까? 에디터 본문에서도 이미지가 삭제됩니다.")) {
            const currentContent = $('#summernote-content').summernote('code');
            const imgTagRegex = new RegExp(`<img[^>]+src\\s*=\\s*['"]${fileUrnToDelete}['\"][^>]*>`, 'g');
            const updatedContent = currentContent.replace(imgTagRegex, '');
            $('#summernote-content').summernote('code', updatedContent);
            item.remove();
        }
    });

    /**
     * 폼 제출 이벤트 핸들러 (기존의 간단한 방식으로 수정)
     */
    $('#board-form').on('submit', async function (e) {
        e.preventDefault();

        const formData = new FormData(this);
        const content = $('#summernote-content').summernote('code');

        // 본문에 이미지가 있는지 간단히 프론트에서 확인 (선택 사항)
        if (!content.includes('<img')) {
            alert('최소 1장의 이미지를 첨부해야 합니다.');
            return;
        }

        formData.set('boardContents', content);

        try {
            const response = await fetch('/api/community/board', {
                method: 'POST',
                body: formData
            });
            const result = await response.json();

            if (response.ok && (result.code === 201 || result.code === 200)) {
                alert('게시글이 성공적으로 작성되었습니다.');
                const newBoardId = result.data.boardId;
                window.location.href = `/community/board/${newBoardId}`;
            } else {
                alert(result.message || '게시글 작성에 실패했습니다.');
            }
        } catch (error) {
            console.error('Error creating board:', error);
            alert('오류가 발생했습니다. 다시 시도해주세요.');
        }
    });

    /**
     * 취소 버튼 이벤트 핸들러
     */
    $('#cancel-button').on('click', function() {
        if(confirm('작성을 취소하시겠습니까? 변경사항이 저장되지 않습니다.')) {
            window.history.back();
        }
    });
});