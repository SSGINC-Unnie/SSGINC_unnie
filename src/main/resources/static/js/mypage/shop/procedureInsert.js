// URL에서 shopId 추출 (예: /mypage/procedure/7)
const pathParts = window.location.pathname.split('/');
const shopId = parseInt(pathParts[pathParts.length - 1]);

// 전역 변수: 임시 저장할 시술 목록 (파일 객체도 함께 저장)
let pendingProcedures = [];
let editingItem = null; // 수정 중인 항목 추적

function openModal() {
    document.getElementById('modalOverlay').style.display = 'flex';
}

function closeModal() {
    document.getElementById('modalOverlay').style.display = 'none';
    document.getElementById('procedureName').value = '';
    document.getElementById('procedurePrice').value = '';
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

// "추가" 버튼 클릭 시: pendingProcedures 배열에 정보와 파일 저장 및 UI 업데이트
document.querySelector('.btn-save').addEventListener('click', async function (event) {
    event.preventDefault();

    const procedureName = document.getElementById('procedureName').value.trim();
    const procedurePrice = document.getElementById('procedurePrice').value.trim();
    const previewImage = document.getElementById('previewImage');

    // 신규 추가일 경우에만 파일(미리보기 이미지가 기본 이미지인지) 체크
    if (!procedureName || !procedurePrice || (!editingItem && previewImage.src.includes('camera.png'))) {
        alert('모든 필수 항목을 입력해주세요!');
        return;
    }

    const procedureList = document.querySelector('.procedure-list');

    if (!editingItem) {
        // 신규 추가: pendingProcedures에 객체 추가 (파일 객체와 미리보기 URL 모두 저장)
        const file = document.getElementById('fileInput').files[0];
        pendingProcedures.push({
            procedureName: procedureName,
            procedurePrice: procedurePrice,
            procedureShopId: shopId,
            file: file,
            previewImage: previewImage.src
        });

        // UI 업데이트: pendingProcedures에 추가된 항목 표시
        const newProcedureItem = document.createElement('div');
        newProcedureItem.className = 'procedure-item';
        newProcedureItem.innerHTML = `
                <img src="${previewImage.src}" alt="${procedureName}" class="procedure-img" />
                <div class="procedure-content">
                  <div class="procedure-header">
                    <div class="procedure-name">${procedureName}</div>
                    <div class="procedure-actions">
                      <a href="#" class="edit-link">수정</a>
                      <a href="#" class="delete-link">삭제</a>
                    </div>
                  </div>
                  <div class="procedure-price">${procedurePrice}</div>
                </div>
            `;
        // data-index 속성에 배열의 인덱스 저장 (수정/삭제 시 활용)
        newProcedureItem.setAttribute('data-index', pendingProcedures.length - 1);
        procedureList.appendChild(newProcedureItem);
        const divider = document.createElement('hr');
        divider.className = 'divider';
        procedureList.appendChild(divider);
    } else {
        // 수정 모드: UI 업데이트 및 pendingProcedures 배열 업데이트
        const file = document.getElementById('fileInput').files[0] || pendingProcedures[editingItem.getAttribute('data-index')].file;
        const index = editingItem.getAttribute('data-index');
        pendingProcedures[index] = {
            procedureName: procedureName,
            procedurePrice: procedurePrice,
            file: file,
            previewImage: previewImage.src
        };
        editingItem.querySelector('.procedure-name').textContent = procedureName;
        editingItem.querySelector('.procedure-price').textContent = procedurePrice;
        editingItem.querySelector('.procedure-img').src = previewImage.src;
        editingItem = null;
    }

    closeModal();
});

// "다음" 버튼 클릭 시: pendingProcedures 배열의 데이터를 서버에 전송하여 DB에 저장 (Bulk Insert)
document.querySelector('.btn-next','completeBtn').addEventListener('click', async function(event) {
    event.preventDefault();
    if (pendingProcedures.length === 0) {
        alert("저장할 시술 정보가 없습니다.");
        return;
    }

    try {
        const formData = new FormData();
        // pendingProcedures 배열에서 시술 요청 정보만 추출 (파일은 별도로 전송)
        const requests = pendingProcedures.map(proc => ({
            procedureName: proc.procedureName,
            procedurePrice: proc.procedurePrice,
            procedureShopId: shopId
        }));
        formData.append("data", new Blob([JSON.stringify(requests)], { type: "application/json" }));

        // 백엔드에서는 "procedureThumbnailFiles"라는 이름의 파일 목록을 기대합니다.
        pendingProcedures.forEach(proc => {
            formData.append("procedureThumbnailFiles", proc.file);
        });

        const response = await fetch(`/api/mypage/procedure/${shopId}`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            alert("시술 등록에 실패했습니다.");
            return;
        }

        await response.json();
        alert("시술 등록이 완료되었습니다.");
        // 성공 후, pendingProcedures 배열 및 UI 초기화
        pendingProcedures = [];
        document.querySelector('.procedure-list').innerHTML = '';
        window.location.href = `/index.html`;

    } catch (error) {
        console.error(error);
        alert("시술 등록 중 오류 발생");
    }
});

// "이전" 버튼 클릭 시: 디자이너 등록 페이지로 이동
document.querySelector('.btn-prev').addEventListener('click', function(event) {
    event.preventDefault();
    window.location.href = `/mypage/designer/${shopId}`;
});

// 수정/삭제 이벤트 위임 (pendingProcedures 배열과 UI 동기화)
document.querySelector('.procedure-list').addEventListener('click', function(event) {
    event.preventDefault();
    const target = event.target;
    const procedureItem = target.closest('.procedure-item');
    if (!procedureItem) return;

    const index = parseInt(procedureItem.getAttribute('data-index'));
    if (target.classList.contains('edit-link')) {
        editingItem = procedureItem;
        document.getElementById('procedureName').value = procedureItem.querySelector('.procedure-name').textContent;
        document.getElementById('procedurePrice').value = procedureItem.querySelector('.procedure-price').textContent;
        document.getElementById('previewImage').src = procedureItem.querySelector('.procedure-img').src;
        document.getElementById('previewImage').style.width = '100%';
        document.getElementById('previewImage').style.height = '100%';
        document.getElementById('previewImage').style.objectFit = 'cover';
        openModal();
    } else if (target.classList.contains('delete-link')) {
        pendingProcedures.splice(index, 1);
        const divider = procedureItem.nextElementSibling;
        if (divider && divider.classList.contains('divider')) {
            divider.remove();
        }
        procedureItem.remove();
        // 나머지 아이템들의 data-index 재설정
        const allItems = document.querySelectorAll('.procedure-item');
        allItems.forEach((item, i) => {
            item.setAttribute('data-index', i);
        });
    }
});
