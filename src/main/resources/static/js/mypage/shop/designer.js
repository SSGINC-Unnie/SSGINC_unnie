    // 전역 변수: 임시 저장할 디자이너 목록 (파일 객체도 함께 저장)
    let pendingDesigners = [];
    let editingItem = null; // 수정 중인 항목 추적

    // 페이지 로드시 백엔드에서 디자이너 목록을 가져와서 UI에 반영하는 함수
    function loadDesigners() {
    const urlParts = window.location.pathname.split('/');
    const shopId = parseInt(urlParts[urlParts.length - 1]);

    fetch(`/api/mypage/manager/designer/${shopId}`)
    .then(response => response.json())
    .then(data => {
    // 응답 구조: { status, message, data: { shop: [ {designerId, designerName, designerIntroduction, designerThumbnail}, ... ] } }
    const designers = data.data.shop;
    // pendingDesigners 배열 업데이트 (파일은 백엔드 조회 데이터에는 없으므로 null로 처리)
    pendingDesigners = designers.map(designer => ({
    designerId: designer.designerId,
    designerName: designer.designerName,
    designerIntroduction: designer.designerIntroduction,
    previewImage: designer.designerThumbnail,
    file: null
}));

    // UI 업데이트: 기존 목록 초기화 후 새로 생성
    const designerList = document.querySelector('.designer-list');
    designerList.innerHTML = '';
    pendingDesigners.forEach((designer, index) => {
    const designerItem = document.createElement('div');
    designerItem.className = 'designer-item';
    designerItem.setAttribute('data-index', index);
    designerItem.innerHTML = `
                    <img src="${designer.previewImage}" alt="${designer.designerName}" class="designer-img" />
                    <div class="designer-content">
                      <div class="designer-header">
                        <div class="designer-name">${designer.designerName}</div>
                        <div class="designer-actions">
                          <a href="#" class="edit-link">수정</a>
                          <a href="#" class="delete-link">삭제</a>
                        </div>
                      </div>
                      <div class="designer-intro">${designer.designerIntroduction}</div>
                    </div>
                `;
    designerList.appendChild(designerItem);
    const divider = document.createElement('hr');
    divider.className = 'divider';
    designerList.appendChild(divider);
});
})
    .catch(err => {
    console.error(err);
    alert("디자이너 목록을 불러오는데 실패했습니다.");
});
}

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

    // "추가/수정" 버튼 클릭 시: pendingDesigners 배열에 정보와 파일 저장 및 UI 업데이트
    document.querySelector('.btn-save').addEventListener('click', function (event) {
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

    // URL에서 shopId 추출 (업데이트 시 DesignerRequest에 사용)
    const urlParts = window.location.pathname.split('/');
    const shopId = parseInt(urlParts[urlParts.length - 1]);

    if (!editingItem) {
    // 신규 추가: pendingDesigners에 객체 추가 (파일 객체와 미리보기 URL 모두 저장)
    const file = document.getElementById('fileInput').files[0];
    pendingDesigners.push({
    designerName: designerName,
    designerIntroduction: designerIntro,
    file: file,
    previewImage: previewImage.src,
    designerId: null // 신규 생성인 경우 후에 생성 API 호출(여기선 수정만 처리)
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
    newDesignerItem.setAttribute('data-index', pendingDesigners.length - 1);
    designerList.appendChild(newDesignerItem);
    const divider = document.createElement('hr');
    divider.className = 'divider';
    designerList.appendChild(divider);
} else {
    // 수정 모드: UI 업데이트 및 pendingDesigners 배열 업데이트
    const file = document.getElementById('fileInput').files[0] || pendingDesigners[editingItem.getAttribute('data-index')].file;
    const index = editingItem.getAttribute('data-index');
    // 기존 pendingDesigners 객체에서 designerId가 있을 것으로 가정 (백엔드 조회 시 값 존재)
    const existingDesignerId = pendingDesigners[index].designerId;
    pendingDesigners[index] = {
    designerName: designerName,
    designerIntroduction: designerIntro,
    file: file,
    previewImage: previewImage.src,
    designerId: existingDesignerId
};

    // UI 업데이트
    editingItem.querySelector('.designer-name').textContent = designerName;
    editingItem.querySelector('.designer-intro').textContent = designerIntro;
    editingItem.querySelector('.designer-img').src = previewImage.src;

    // 백엔드에 수정 요청 (PUT)
    const requestPayload = {
    designerName: designerName,
    designerIntroduction: designerIntro,
    designerShopId: shopId,
    designerThumbnail: previewImage.src,
    designerId: existingDesignerId
};

    fetch(`/api/mypage/manager/designer/${existingDesignerId}`, {
    method: 'PUT',
    headers: {
    'Content-Type': 'application/json'
},
    body: JSON.stringify(requestPayload)
})
    .then(response => response.json())
    .then(result => {
    if(result.status === 200) {
    alert("디자이너 수정이 완료되었습니다.");
} else {
    alert("디자이너 수정에 실패했습니다.");
}
})
    .catch(error => {
    console.error(error);
    alert("디자이너 수정 중 오류가 발생했습니다.");
});

    editingItem = null;
}

    closeModal();
});

    // "수정 완료" 버튼 클릭 시 (전체 pendingDesigners 처리 후 /mypage/myshop으로 이동)
    // 신규 추가는 별도 생성 API 호출로 처리하거나 페이지 전환 후 새로고침 처리하세요.
    document.querySelector('.btn-save-all').addEventListener('click', function(event) {
    event.preventDefault();

    console.log("현재 pendingDesigners 목록", pendingDesigners);
    alert("수정 작업이 완료되었습니다.");
    // 수정 완료 후 /mypage/myshop 페이지로 이동
    window.location.href = '/mypage/myshop';
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
    // 삭제 처리: 만약 삭제할 디자이너의 designerId가 있다면 백엔드 DELETE API 호출
    const designerToDelete = pendingDesigners[index];
    if (designerToDelete.designerId) {
    fetch(`/api/mypage/manager/designer/${designerToDelete.designerId}`, {
    method: 'DELETE'
})
    .then(response => response.json())
    .then(result => {
    if(result.status === 200) {
    alert("디자이너 삭제가 완료되었습니다.");
    // 삭제 성공 시 UI와 pendingDesigners 배열에서 제거
    pendingDesigners.splice(index, 1);
    const divider = designerItem.nextElementSibling;
    if (divider && divider.classList.contains('divider')) {
    divider.remove();
}
    designerItem.remove();
    // 나머지 아이템들의 data-index 재설정
    const allItems = document.querySelectorAll('.designer-item');
    allItems.forEach((item, i) => {
    item.setAttribute('data-index', i);
});
} else {
    alert("디자이너 삭제에 실패했습니다.");
}
})
    .catch(error => {
    console.error(error);
    alert("디자이너 삭제 중 오류가 발생했습니다.");
});
} else {
    // 아직 백엔드에 저장되지 않은 신규 추가 항목인 경우 단순 삭제 처리
    pendingDesigners.splice(index, 1);
    const divider = designerItem.nextElementSibling;
    if (divider && divider.classList.contains('divider')) {
    divider.remove();
}
    designerItem.remove();
    // 나머지 아이템들의 data-index 재설정
    const allItems = document.querySelectorAll('.designer-item');
    allItems.forEach((item, i) => {
    item.setAttribute('data-index', i);
});
}
}
});

    // 페이지 로드 시 디자이너 목록을 백엔드에서 불러옴
    window.addEventListener('load', loadDesigners);
