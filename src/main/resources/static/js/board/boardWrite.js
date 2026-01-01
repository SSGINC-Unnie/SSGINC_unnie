// boardWrite.js (FULL — paste-safe, upload-safe)

$(document).ready(function() {

    const attachmentList = $('#attachment-list');
    const MAX_IMAGE_WIDTH = 1280;
    const IMAGE_COMPRESSION_QUALITY = 0.85;

    // 업로드 진행 상태
    let uploading = 0;

    // ===== Summernote 초기화 =====
    $('#summernote-content').summernote({
        height: 300,
        focus: true,
        lang: "ko-KR",
        placeholder: '내용을 입력해주세요',
        dialogsInBody: true,
        toolbar: [
            ['fontname', ['fontname']],
            ['fontsize', ['fontsize']],
            ['style', ['bold','italic','underline','strikethrough','clear']],
            ['color', ['forecolor','color']],
            ['table', ['table']],
            ['para', ['ul','ol','paragraph']],
            ['height', ['height']],
            ['picture', ['picture']],
            ['insert',['link','video']],
            ['view', ['fullscreen','help']]
        ],
        fontNames: ['Arial','Arial Black','Comic Sans MS','Courier New','맑은 고딕','궁서','굴림체','돋움체','바탕체'],
        fontSizes: ['8','9','10','11','12','14','16','18','20','22','24','28','30','36','50','72'],
        callbacks: {
            // (1) 에디터에서 직접 이미지 선택 시: 리사이즈 + 업로드 경유
            onImageUpload: function(files) {
                for (let i = 0; i < files.length; i++) {
                    resizeImage(files[i], this);
                }
            },
            onPaste: function(e) {
                const ev = e.originalEvent || e;
                const cd = ev.clipboardData || window.clipboardData;
                if (!cd) return;

                const items = cd.items || [];
                let hasImageItem = false;
                for (let i = 0; i < items.length; i++) {
                    if (items[i].kind === 'file' && items[i].type.startsWith('image/')) {
                        hasImageItem = true;
                    }
                }
                if (hasImageItem) {
                    ev.preventDefault();
                    for (let i = 0; i < items.length; i++) {
                        if (items[i].kind === 'file' && items[i].type.startsWith('image/')) {
                            const file = items[i].getAsFile();
                            if (file) resizeImage(file, this);
                        }
                    }
                    return;
                }

                const html = cd.getData('text/html');
                if (html && html.length > 0) {
                    ev.preventDefault();
                    const sanitized = sanitizeHTML(html);
                    $(this).summernote('pasteHTML', sanitized);

                    setTimeout(() => replaceDataURIImagesWithUploads(this), 0);
                    return;
                }

            }
        }
    });

    // ===== 이미지 리사이즈 후 업로드 =====
    function resizeImage(file, editor) {
        const reader = new FileReader();
        reader.onload = function(evt) {
            const img = new Image();
            img.src = evt.target.result;
            img.onload = function() {
                if (img.width > MAX_IMAGE_WIDTH) {
                    const canvas = document.createElement('canvas');
                    const ratio = MAX_IMAGE_WIDTH / img.width;
                    canvas.width = MAX_IMAGE_WIDTH;
                    canvas.height = img.height * ratio;
                    const ctx = canvas.getContext('2d');
                    ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
                    canvas.toBlob(function(blob) {
                        const resizedFile = new File([blob], file.name, { type: file.type });
                        sendFile(resizedFile, editor);
                    }, file.type, IMAGE_COMPRESSION_QUALITY);
                } else {
                    sendFile(file, editor);
                }
            };
        };
        reader.readAsDataURL(file);
    }

    // ===== 파일 업로드(에디터에 삽입 + 첨부목록 추가) =====
    function sendFile(file, editor) {
        const formData = new FormData();
        formData.append('file', file);

        uploading++;
        toggleSubmit(false);

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
            },
            complete: function() {
                uploading = Math.max(0, uploading - 1);
                if (uploading === 0) toggleSubmit(true);
            }
        });
    }

    // ===== data:image/* 를 서버 업로드로 치환 =====
    function replaceDataURIImagesWithUploads(editor) {
        const $editable = $(editor).parent().find('.note-editable');
        $editable.find('img').each(function() {
            const $img = $(this);
            const src = $img.attr('src') || '';
            if (src.startsWith('data:')) {
                const blob = dataURLToBlob(src);
                const filename = guessFilenameFromMime(blob.type);
                // 업로드 카운트/버튼 제어
                uploading++;
                toggleSubmit(false);

                const formData = new FormData();
                formData.append('file', new File([blob], filename, { type: blob.type }));

                $.ajax({
                    url: '/api/media/upload',
                    type: 'POST',
                    data: formData,
                    contentType: false,
                    processData: false,
                    success: function(response) {
                        if (response.status === 201 || response.status === 200) {
                            const urn = response.data.fileUrn;
                            $img.attr('src', urn);
                            addAttachmentPreview(filename, urn);
                        } else {
                            // 실패 시 data URI 이미지는 제거(서버 정책에 맞게 조정 가능)
                            $img.remove();
                            alert(response.message || '붙여넣은 이미지 업로드에 실패했습니다.');
                        }
                    },
                    error: function() {
                        $img.remove();
                        alert('붙여넣은 이미지 업로드에 실패했습니다.');
                    },
                    complete: function() {
                        uploading = Math.max(0, uploading - 1);
                        if (uploading === 0) toggleSubmit(true);
                    }
                });
            }
        });
    }

    function dataURLToBlob(dataurl) {
        const arr = dataurl.split(',');
        const mime = arr[0].match(/:(.*?);/)[1];
        const bstr = atob(arr[1]);
        let n = bstr.length;
        const u8 = new Uint8Array(n);
        while (n--) u8[n] = bstr.charCodeAt(n);
        return new Blob([u8], { type: mime });
    }

    function guessFilenameFromMime(mime) {
        const ext = mime.split('/')[1] || 'png';
        return `pasted.${ext}`;
    }

    // ===== 첨부 미리보기 =====
    function addAttachmentPreview(fileName, fileUrn) {
        const html = `
      <div class="attachment-item" data-urn="${fileUrn}">
        <img src="${fileUrn}" alt="${fileName}" class="attachment-thumbnail">
        <span class="attachment-name">${fileName}</span>
        <button type="button" class="attachment-delete-btn" title="첨부 삭제">&times;</button>
      </div>
    `;
        attachmentList.append(html);
    }

    attachmentList.on('click', '.attachment-delete-btn', function() {
        const item = $(this).closest('.attachment-item');
        item.remove();
    });

    $('#board-form').on('submit', async function (e) {
        e.preventDefault();

        if (uploading > 0) {
            alert('이미지 업로드가 끝날 때까지 잠시만 기다려주세요.');
            return;
        }

        // 본문 HTML
        let html = $('#summernote-content').summernote('code');

        const $root = $('<div>').html(html);
        const $blocks = $root.children('p, div');
        if ($blocks.length && isBlankOrImgOnly($($blocks[0]))) {
            const $firstText = $blocks.filter(function() {
                return $(this).text().trim().length > 0;
            }).first();
            if ($firstText.length) {
                $($blocks[0]).before($firstText);
                html = $root.html();
                $('#summernote-content').summernote('code', html);
            }
        }

        const plain = $root.text().replace(/\u00A0|\u200B/g,'').trim();

        const hasImg = /<img\b/i.test(html);
        if (plain.length === 0 && !hasImg) {
            alert('텍스트나 이미지를 최소 1개 이상 입력해주세요.');
            return;
        }

        $('#summernote-content').val(html);

        const formData = new FormData(this);
        formData.set('boardContents', html);
        formData.set('boardContentsText', plain); // 서버에서 순수 텍스트 검증시 활용

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
        } catch (err) {
            console.error(err);
            alert('오류가 발생했습니다. 다시 시도해주세요.');
        }
    });

    function isBlankOrImgOnly($el) {
        const textTrim = $el.text().trim();
        const htmlTrim = $el.html().replace(/<br\s*\/?>/gi,'').trim();
        const hasImg = $el.find('img').length > 0;
        return textTrim.length === 0 && (hasImg || htmlTrim === '');
    }

    function toggleSubmit(enable) {
        const $submitBtn = $('#submit-button');
        if ($submitBtn.length) $submitBtn.prop('disabled', !enable);
    }

    function sanitizeHTML(input) {
        let html = input;

        html = html.replace(/<!--[\s\S]*?-->/g, '');
        html = html.replace(/<!\[if[\s\S]*?<!\[endif\]>/gi, '');

        html = html.replace(/<\/?(script|style)[^>]*>/gi, '');

        html = html.replace(/<\/?o:p[^>]*>/gi, '');

        html = html.replace(/<\/?span[^>]*>/gi, '');
        html = html.replace(/<\/?font[^>]*>/gi, '');

        html = html.replace(/\s+(class|style|id|width|height|align)=(".*?"|'.*?'|[^\s>]+)/gi, '');

        html = html.replace(/&nbsp;/gi, ' ');

        return html;
    }

    // 취소 버튼
    $('#cancel-button').on('click', function() {
        if (confirm('작성을 취소하시겠습니까? 변경사항이 저장되지 않습니다.')) {
            window.history.back();
        }
    });

});
