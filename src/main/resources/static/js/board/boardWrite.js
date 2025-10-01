$(document).ready(function() {

    const attachmentList = $('#attachment-list');
    const MAX_IMAGE_WIDTH = 1280; // 이미지 리사이징 최대 너비 설정 (px)
    const IMAGE_COMPRESSION_QUALITY = 0.85; // 이미지 압축 품질 (0.0 ~ 1.0)

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
                    resizeImage(files[i], this);
                }
            }
        }
    });

    /**
     * 이미지를 리사이징하는 함수
     * @param {File} file - 원본 이미지 파일
     * @param {object} editor - Summernote 에디터 인스턴스
     */
    function resizeImage(file, editor) {
        const reader = new FileReader();

        reader.onload = function(event) {
            const img = new Image();
            img.src = event.target.result;

            img.onload = function() {
                // 1. 이미지의 너비가 최대 너비보다 클 경우에만 리사이징 실행
                if (img.width > MAX_IMAGE_WIDTH) {
                    const canvas = document.createElement('canvas');
                    const ratio = MAX_IMAGE_WIDTH / img.width;
                    canvas.width = MAX_IMAGE_WIDTH;
                    canvas.height = img.height * ratio;

                    const ctx = canvas.getContext('2d');
                    ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

                    // 2. Canvas를 Blob으로 변환
                    canvas.toBlob(function(blob) {
                        // 3. Blob을 File 객체로 다시 만들어 원본 파일명과 타입을 유지
                        const resizedFile = new File([blob], file.name, { type: file.type });
                        // 4. 리사이징된 파일을 서버로 전송
                        sendFile(resizedFile, editor);
                    }, file.type, IMAGE_COMPRESSION_QUALITY);

                } else {
                    // 5. 이미지 너비가 최대 너비보다 작으면 원본을 그대로 전송
                    sendFile(file, editor);
                }
            };
        };

        reader.readAsDataURL(file);
    }


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
                if (response.status === 201 || response.status === 200) {
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
        // const fileUrnToDelete = item.data('urn');

        if (confirm("이 첨부파일을 삭제하시겠습니까? ")) {
            // const currentContent = $('#summernote-content').summernote('code');
            // const imgTagRegex = new RegExp(`<img[^>]+src\\s*=\\s*['"]${fileUrnToDelete}['\"][^>]*>`, 'g');
            // const updatedContent = currentContent.replace(imgTagRegex, '');
            // $('#summernote-content').summernote('code', updatedContent);
            item.remove();
        }
    });


    $('#board-form').on('submit', async function (e) {
        e.preventDefault();

        const formData = new FormData(this);
        const content = $('#summernote-content').summernote('code');

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

            if (response.ok && (result.status === 201 || result.status === 200)) {
                alert('게시글이 성공적으로 작성되었습니다.');
                window.history.back();
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