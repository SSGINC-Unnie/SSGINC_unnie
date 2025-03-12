const pathParts = window.location.pathname.split('/');
const shopId = parseInt(pathParts[pathParts.length - 1]);

// 전역 변수: 임시 저장할 디자이너 목록 (파일 객체도 함께 저장)
let pendingDesigners = [];
let editingItem = null; // 수정 중인 항목 추적

function openModal() {
    document.getElementById('modalOverlay').style.display = 'flex';
}

function closeModal() {
    document.getElementById('modalOverlay').style.display = 'none';
    document.getElementById('designerName').value = '';
    document.getElementById('designerIntro').value = '';
    document.getElementById('previewImage').src = '/img/shop/camera.png';
    document.getElementById('previewImage').style = 'width:20px; height:20px; object-fit:contain;';
    document.getElementById('fileInput').value = '';
    editingItem = null;
}

// 이미지 미리보기
document.getElementById('fileInput').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const previewImage = document.getElementById('previewImage');
            previewImage.src = e.target.result;
            previewImage.style.width = '100%';
            previewImage.style.height = '100%';
            previewImage.style.objectFit = 'cover';
        };
        reader.readAsDataURL(file);
    }
});

// "추가" 버튼 클릭 시: pendingDesigners 배열에 정보와 파일 저장 및 UI 업데이트
document.querySelector('.btn-save').addEventListener('click', async function (event) {
    event.preventDefault();

    const designerName = document.getElementById('designerName').value.trim();
    const designerIntro = document.getElementById('designerIntro').value.trim();
    const previewImage = document.getElementById('previewImage');

    // 신규 추가일 경우에만 파일(미리보기 이미지가 기본 이미지인지) 체크
    if (!designerName || !designerIntro || (!editingItem && previewImage.src.includes('camera.png'))) {
        alert('모든 필수 항목을 입력해주세요!');
        return;
    }

    const designerList = document.querySelector('.designer-list');

    if (!editingItem) {
        // 신규 추가: pendingDesigners에 객체 추가 (파일 객체와 미리보기 URL 모두 저장)
        const file = document.getElementById('fileInput').files[0];
        pendingDesigners.push({
            designerName: designerName,
            designerIntroduction: designerIntro,
            designerShopId: shopId,
            file: file,
            previewImage: previewImage.src
        });

        // UI 업데이트: pendingDesigners에 추가된 항목 표시
        const newDesignerItem = document.createElement('div');
        newDesignerItem.className = 'designer-item';
        newDesignerItem.innerHTML = `
            <img src="${previewImage.src}" alt="${designerName}" class="designer-img" />
            <div class="designer-content">
              <div class="designer-header">
                <div class="designer-name">${designerName}</div>
                <div class="designer-actions">
                  <a href="#" class="edit-link">수정</a>
                  <a href="#" class="delete-link">삭제</a>
                </div>
              </div>
              <div class="designer-intro">${designerIntro}</div>
            </div>
        `;
        // data-index 속성에 배열의 인덱스 저장 (수정/삭제 시 활용)
        newDesignerItem.setAttribute('data-index', pendingDesigners.length - 1);
        designerList.appendChild(newDesignerItem);
        const divider = document.createElement('hr');
        divider.className = 'divider';
        designerList.appendChild(divider);
    } else {
        // 수정 모드: UI 업데이트 및 pendingDesigners 배열 업데이트
        const file = document.getElementById('fileInput').files[0] || pendingDesigners[editingItem.getAttribute('data-index')].file;
        const index = editingItem.getAttribute('data-index');
        pendingDesigners[index] = {
            designerName: designerName,
            designerIntroduction: designerIntro,
            file: file,
            previewImage: previewImage.src
        };
        editingItem.querySelector('.designer-name').textContent = designerName;
        editingItem.querySelector('.designer-intro').textContent = designerIntro;
        editingItem.querySelector('.designer-img').src = previewImage.src;
        editingItem = null;
    }

    closeModal();
});

document.querySelector('.btn-next').addEventListener('click', async function(event) {
    event.preventDefault();
    if (pendingDesigners.length === 0) {
        alert("저장할 디자이너 정보가 없습니다.");
        return;
    }

    try {
        const formData = new FormData();
        // pendingDesigners 배열에서 디자이너 요청 정보만 추출 (파일은 별도로 전송)
        const requests = pendingDesigners.map(designer => ({
            designerName: designer.designerName,
            designerIntroduction: designer.designerIntroduction,
            designerShopId: shopId
        }));
        formData.append("data", new Blob([JSON.stringify(requests)], { type: "application/json" }));

        // 백엔드에서는 "designerThumbnailFiles"라는 이름의 파일 목록을 기대합니다.
        pendingDesigners.forEach(designer => {
            formData.append("designerThumbnailFiles", designer.file);
        });

        const response = await fetch(`/api/mypage/designer/${shopId}`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            alert("디자이너 등록에 실패했습니다.");
            return;
        }

        await response.json();
        alert("디자이너 등록이 완료되었습니다.");
        // 성공 후, pendingDesigners 배열 및 UI 초기화
        pendingDesigners = [];
        document.querySelector('.designer-list').innerHTML = '';
        window.location.href = `/mypage/procedure/${shopId}`;

    } catch (error) {
        console.error(error);
        alert("디자이너 등록 중 오류 발생");
    }
});

document.querySelector('.btn-complete').addEventListener('click', async function(event) {
    event.preventDefault();
    if (pendingDesigners.length === 0) {
        alert("저장할 디자이너 정보가 없습니다.");
        return;
    }

    try {
        const formData = new FormData();
        // pendingDesigners 배열에서 디자이너 요청 정보만 추출 (파일은 별도로 전송)
        const requests = pendingDesigners.map(designer => ({
            designerName: designer.designerName,
            designerIntroduction: designer.designerIntroduction,
            designerShopId: shopId
        }));
        formData.append("data", new Blob([JSON.stringify(requests)], { type: "application/json" }));

        // 백엔드에서는 "designerThumbnailFiles"라는 이름의 파일 목록을 기대합니다.
        pendingDesigners.forEach(designer => {
            formData.append("designerThumbnailFiles", designer.file);
        });

        const response = await fetch(`/api/mypage/designer/${shopId}`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            alert("디자이너 등록에 실패했습니다.");
            return;
        }

        await response.json();
        alert("디자이너 등록이 완료되었습니다.");
        // 성공 후, pendingDesigners 배열 및 UI 초기화
        pendingDesigners = [];
        document.querySelector('.designer-list').innerHTML = '';
        window.location.href = '';

    } catch (error) {
        console.error(error);
        alert("디자이너 등록 중 오류 발생");
    }
});

document.querySelector('.btn-prev').addEventListener('click', function(event) {
    event.preventDefault();
    window.location.href = '/mypage/shop';
});

// 수정/삭제 이벤트 위임 (pendingDesigners 배열과 UI 동기화)
document.querySelector('.designer-list').addEventListener('click', function(event) {
    event.preventDefault();
    const target = event.target;
    const designerItem = target.closest('.designer-item');
    if (!designerItem) return;

    const index = parseInt(designerItem.getAttribute('data-index'));
    if (target.classList.contains('edit-link')) {
        editingItem = designerItem;
        document.getElementById('designerName').value = designerItem.querySelector('.designer-name').textContent;
        document.getElementById('designerIntro').value = designerItem.querySelector('.designer-intro').textContent;
        document.getElementById('previewImage').src = designerItem.querySelector('.designer-img').src;
        document.getElementById('previewImage').style.width = '100%';
        document.getElementById('previewImage').style.height = '100%';
        document.getElementById('previewImage').style.objectFit = 'cover';
        openModal();
    } else if (target.classList.contains('delete-link')) {
        pendingDesigners.splice(index, 1);
        const divider = designerItem.nextElementSibling;
        if (divider && divider.classList.contains('divider')) {
            divider.remove();
        }
        designerItem.remove();
        // 업데이트: 나머지 아이템들의 data-index 재설정
        const allItems = document.querySelectorAll('.designer-item');
        allItems.forEach((item, i) => {
            item.setAttribute('data-index', i);
        });
    }
});
