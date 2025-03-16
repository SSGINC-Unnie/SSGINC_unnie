/**
 * 1) 카카오 주소 API 실행 함수
 */
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('shopLocation').value = data.address;
        }
    }).open();
}

// 파일 업로드 API를 호출하는 함수
async function uploadMedia(file, targetType, targetId) {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("targetType", targetType);
    formData.append("targetId", targetId);

    const response = await fetch("/api/media/upload", {
        method: "POST",
        body: formData
    });

    if (!response.ok) {
        const err = await response.json();
        throw new Error(err.message || "파일 업로드 실패");
    }
    const data = await response.json();
    return data.data.fileUrn;
}

// 모든 선택된 미디어 파일을 업로드하는 함수
async function uploadAllMediaFiles(targetId) {
    const container = document.getElementById('designerPhotoContainer');
    const fileInputs = container.querySelectorAll('input[type="file"]');
    const targetType = "SHOP"; // media_target_type 값
    for (const input of fileInputs) {
        if (input.files && input.files[0]) {
            try {
                const fileUrn = await uploadMedia(input.files[0], targetType, targetId);
                console.log("업로드 성공, fileUrn:", fileUrn);
            } catch (err) {
                console.error("파일 업로드 실패:", err);
                alert("파일 업로드에 실패했습니다.");
            }
        }
    }
}

// submitBtn 이벤트 (업체 등록 후 디자이너 등록 페이지로 이동)
document.getElementById('submitBtn').addEventListener('click', async function() {
    const form = document.getElementById('shopForm');
    if (!form.checkValidity()) {
        alert('필수 항목을 입력해 주세요.');
        return;
    }
    // 폼 값 수집
    const shopBusinessNumber = document.getElementById('shopBusinessNumber').value.trim();
    const shopRepresentationName = document.getElementById('shopRepresentationName').value.trim();
    const shopCreatedAt = document.getElementById('shopCreatedAt').value;
    const shopLocation = document.getElementById('shopLocation').value.trim();
    const shopName = document.getElementById('shopName').value.trim();
    const shopCategory = document.querySelector('input[name="shopCategory"]:checked').value;
    const startTime = document.querySelector('input[name="shopBusinessStartTime"]').value;
    const endTime = document.querySelector('input[name="shopBusinessEndTime"]').value;
    const shopTel = document.getElementById('shopTel').value.trim();
    const shopClosedDay = document.querySelector('input[name="shopClosedDay"]:checked').value;
    const shopIntroduction = document.getElementById('shopIntroduction').value.trim();

    const shopBusinessTime = `${startTime}-${endTime}`;
    const fullLocation = shopLocation;

    const requestBody = {
        shopName: shopName,
        shopLocation: fullLocation,
        shopCategory: shopCategory,
        shopBusinessTime: shopBusinessTime,
        shopTel: shopTel,
        shopIntroduction: shopIntroduction,
        shopClosedDay: shopClosedDay,
        shopRepresentationName: shopRepresentationName,
        shopBusinessNumber: shopBusinessNumber,
        shopCreatedAt: shopCreatedAt
    };

    try {
        // 업체 등록 API 호출
        const response = await fetch('/api/mypage/shop', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });
        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || '업체 등록 실패');
        }
        const data = await response.json();
        alert(data.message || '업체 등록이 완료되었습니다. ' + data.data.shopId);
        const shopId = data.data.shopId;
        console.log('등록된 shopId', shopId);
        // 업체 등록 후 미디어 파일 업로드 진행
        await uploadAllMediaFiles(shopId);
        window.location.href = `/mypage/designer/${shopId}`;
    } catch (err) {
        alert(err.message);
    }
});


// DOMContentLoaded: 미디어 파일 업로드 미리보기, 취소(X) 버튼 및 새로운 업로드 영역 추가 처리
document.addEventListener('DOMContentLoaded', function () {
    const container = document.getElementById('designerPhotoContainer');
    const defaultImage = '/img/shop/camera.png';

    // 클릭 이벤트: 업로드 아이콘 클릭 시 file input 호출
    container.addEventListener('click', function (e) {
        // 취소 버튼(X) 클릭 시 해당 로직은 아래에서 처리
        if (e.target.closest('.cancel-btn')) return;
        const uploadIcon = e.target.closest('.upload-icon');
        if (uploadIcon) {
            const uploadArea = uploadIcon.closest('.upload-area');
            const fileInput = uploadArea.querySelector('input[type="file"]');
            fileInput.click();
        }
    });

    // 파일 선택 변경 이벤트 처리: 미리보기 업데이트 및 X 버튼 표시, 새로운 업로드 영역 추가
    container.addEventListener('change', function (e) {
        const target = e.target;
        if (target && target.matches('input[type="file"]')) {
            if (target.files && target.files[0]) {
                const file = target.files[0];
                const reader = new FileReader();
                reader.onload = function (ev) {
                    const uploadArea = target.closest('.upload-area');
                    const previewImg = uploadArea.querySelector('img');
                    previewImg.src = ev.target.result;
                    previewImg.style.width = "80px";
                    previewImg.style.height = "80px";
                    previewImg.style.objectFit = "cover";
                    // X(취소) 버튼 표시
                    const cancelBtn = uploadArea.querySelector('.cancel-btn');
                    if (cancelBtn) {
                        cancelBtn.style.display = 'block';
                    }
                    // 마지막 업로드 영역이면, 오른쪽에 새로운 빈 업로드 영역 추가
                    const allUploadAreas = container.querySelectorAll('.upload-area');
                    if (uploadArea === allUploadAreas[allUploadAreas.length - 1]) {
                        const newUploadArea = document.createElement('div');
                        newUploadArea.className = 'upload-area';
                        newUploadArea.innerHTML = `
                            <label class="upload-icon">
                              <img src="${defaultImage}" alt="Upload" style="width: 20px; height: 20px; object-fit: contain;">
                            </label>
                            <button type="button" class="cancel-btn" style="display: none;">X</button>
                            <input type="file" accept="image/*" style="display: none;">
                        `;
                        container.appendChild(newUploadArea);
                    }
                };
                reader.readAsDataURL(file);
            }
        }
    });

    // 취소(X) 버튼 클릭 이벤트 처리: 영역 삭제(2개 이상) 또는 초기 상태로 복원(단 하나)
    container.addEventListener('click', function (e) {
        const cancelBtn = e.target.closest('.cancel-btn');
        if (cancelBtn) {
            const uploadArea = cancelBtn.closest('.upload-area');
            const allUploadAreas = container.querySelectorAll('.upload-area');
            if (allUploadAreas.length > 1) {
                uploadArea.remove();
            } else {
                const previewImg = uploadArea.querySelector('img');
                previewImg.src = defaultImage;
                const fileInput = uploadArea.querySelector('input[type="file"]');
                fileInput.value = "";
                cancelBtn.style.display = 'none';
            }
            e.stopPropagation();
        }
    });
});
