document.addEventListener('DOMContentLoaded', function () {
    // URL 경로에서 shopId 추출 (URL 구조가 /mypage/shop/{shopId}/shopUpdate.html 인 경우)
    const pathSegments = window.location.pathname.split('/');
    // 예를 들어, URL이 "/mypage/shop/1/shopUpdate.html"이면, shopId는 두 번째 마지막 요소가 됨.
    const shopId = pathSegments[pathSegments.length - 1];
    console.log("추출된 shopId:", shopId);

    if (shopId) {
        getShopDetail(shopId);
    }

    // ---------------------- (A) 업체 정보 조회 & 폼 세팅 ----------------------
    async function getShopDetail(shopId) {
        try {
            const response = await fetch(`/api/mypage/shopdetail/${shopId}`);
            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.message || '업체 조회 실패');
            }
            const data = await response.json();
            const shop = data.data; // 응답에서 업체 정보가 data에 있음

            // 폼 채우기
            document.getElementById('shopBusinessNumber').value = shop.shopBusinessNumber;
            document.getElementById('shopRepresentationName').value = shop.shopRepresentationName;
            document.getElementById('shopCreatedAt').value = shop.shopCreatedAt;
            document.getElementById('shopLocation').value = shop.shopLocation;
            document.getElementById('shopName').value = shop.shopName;
            document.getElementById('shopTel').value = shop.shopTel;
            document.getElementById('shopIntroduction').value = shop.shopIntroduction;

            // shopCategory 라디오 체크
            const categoryRadio = document.querySelector(`input[name="shopCategory"][value="${shop.shopCategory}"]`);
            if (categoryRadio) {
                categoryRadio.checked = true;
            }

            // 영업시간 (예: "09:00-18:00")
            if (shop.shopBusinessTime) {
                const [startTime, endTime] = shop.shopBusinessTime.split("-");
                document.querySelector('input[name="shopBusinessStartTime"]').value = startTime;
                document.querySelector('input[name="shopBusinessEndTime"]').value = endTime;
            }

            // 휴무일 라디오 체크
            const closedDayRadio = document.querySelector(`input[name="shopClosedDay"][value="${shop.shopClosedDay}"]`);
            if (closedDayRadio) {
                closedDayRadio.checked = true;
            }

            // ---- 이미지 출력 추가 ----
            // shop.shopImages가 배열로 전달된다고 가정
            const container = document.getElementById('designerPhotoContainer');
            if (shop.shopImages && shop.shopImages.length > 0) {
                shop.shopImages.forEach(function(imageUrl) {
                    // upload-area 형태로 기존 이미지를 표시
                    const uploadArea = document.createElement('div');
                    uploadArea.className = 'upload-area';
                    // 기존 이미지는 파일 input이 필요 없을 수 있으나, UI 통일을 위해 넣어둠
                    uploadArea.innerHTML = `
                        <label class="upload-icon">
                          <img src="${imageUrl}" alt="업체 이미지" style="object-fit: cover;" />
                        </label>
                        <button type="button" class="cancel-btn" style="display: block;">X</button>
                        <input type="file" accept="image/*" style="display: none;">
                    `;
                    // 원하는 위치에 삽입 (맨 앞에 추가하거나, 맨 뒤에 추가)
                    // 아래는 "맨 앞"에 추가 예시:
                    container.insertBefore(uploadArea, container.firstChild);
                    // 만약 맨 뒤에 추가하고 싶다면 container.appendChild(uploadArea);
                });
            }
        } catch (err) {
            alert(err.message);
        }
    }

    // ---------------------- (B) 업체 수정 API 호출 ----------------------
    async function updateShop(shopId) {
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
            shopName,
            shopLocation: fullLocation,
            shopCategory,
            shopBusinessTime,
            shopTel,
            shopIntroduction,
            shopClosedDay,
            shopRepresentationName,
            shopBusinessNumber,
            shopCreatedAt
        };

        try {
            const response = await fetch(`/api/mypage/manager/shop/${shopId}`, {
                method: 'PUT', // 수정 API
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(requestBody)
            });
            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.message || '업체 수정 실패');
            }
            const data = await response.json();
            alert(data.message || '업체 수정이 완료되었습니다.');

            // 수정 후 파일 업로드 (이미지) 진행
            await uploadAllMediaFiles(shopId);

            // 원하는 페이지로 이동 (예: 상세 페이지)
            // window.location.href = `/mypage/shop/detail/${shopId}`;
        } catch (err) {
            alert(err.message);
        }
    }

    // ---------------------- (C) 파일 업로드 관련 함수 ----------------------
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

    // ---------------------- (D) 버튼 이벤트 ----------------------
    // 수정 완료 버튼 클릭 시 updateShop() 호출
    document.getElementById('completeBtn').addEventListener('click', async function () {
        const urlParams = new URLSearchParams(window.location.search);
        const shopId = urlParams.get('shopId');
        if (!shopId) {
            alert('shopId가 없습니다. 수정 모드가 아닙니다.');
            return;
        }
        await updateShop(shopId);
    });

    // ---------------------- (E) 파일 업로드 미리보기 및 취소(X) 로직 ----------------------
    const container = document.getElementById('designerPhotoContainer');
    const defaultImage = '/img/shop/camera.png';

    container.addEventListener('click', function (e) {
        if (e.target.closest('.cancel-btn')) return;
        const uploadIcon = e.target.closest('.upload-icon');
        if (uploadIcon) {
            const uploadArea = uploadIcon.closest('.upload-area');
            const fileInput = uploadArea.querySelector('input[type="file"]');
            fileInput.click();
        }
    });

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
                    const cancelBtn = uploadArea.querySelector('.cancel-btn');
                    if (cancelBtn) {
                        cancelBtn.style.display = 'block';
                    }
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
