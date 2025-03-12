// 전역 변수: 임시 저장할 시술 목록 (파일 객체도 함께 저장)
let pendingProcedures = [];
let editingProcedureItem = null; // 수정 중인 시술 항목 추적

// 페이지 로드시 백엔드에서 시술 목록을 가져와 UI에 반영하는 함수
function loadProcedures() {
    // URL에서 shopId 추출 (예: /manager/procedure/123)
    const urlParts = window.location.pathname.split('/');
    const shopId = parseInt(urlParts[urlParts.length - 1]);
    console.log("shopId", shopId);

    // 백엔드에서 /api/mypage/manager/procedure/{shopId} 호출
    // -> 백엔드가 { data: { shop: [ {procedureId, procedureName, procedurePrice, procedureThumbnail}, ... ] } } 형태로 반환한다고 가정
    fetch(`/api/mypage/manager/procedure/${shopId}`)
        .then(response => response.json())
        .then(data => {
            console.log(data);
            // 백엔드가 "shop" 키에 시술 목록을 담아 보내므로, 아래처럼 작성
            const procedures = data.data.shop;
            // pendingProcedures 배열 업데이트 (파일은 백엔드 조회 데이터에 없으므로 null로 처리)
            pendingProcedures = procedures.map(proc => ({
                procedureId: proc.procedureId,
                procedureName: proc.procedureName,
                procedurePrice: proc.procedurePrice,
                previewImage: proc.procedureThumbnail,
                file: null
            }));

            // UI 업데이트: 기존 목록 초기화 후 새로 생성
            const procedureList = document.querySelector('.procedure-list');
            procedureList.innerHTML = '';

            pendingProcedures.forEach((proc, index) => {
                const procItem = document.createElement('div');
                procItem.className = 'procedure-item';
                procItem.setAttribute('data-index', index);

                // 시술 가격은 .procedure-price 클래스로 표시
                procItem.innerHTML = `
          <img src="${proc.previewImage}" alt="${proc.procedureName}" class="procedure-img" />
          <div class="procedure-content">
            <div class="procedure-header">
              <div class="procedure-name">${proc.procedureName}</div>
              <div class="procedure-actions">
                <a href="#" class="edit-proc-link">수정</a>
                <a href="#" class="delete-proc-link">삭제</a>
              </div>
            </div>
            <div class="procedure-price">${proc.procedurePrice}</div>
          </div>
        `;
                procedureList.appendChild(procItem);

                const divider = document.createElement('hr');
                divider.className = 'divider';
                procedureList.appendChild(divider);
            });
        })
        .catch(err => {
            console.error(err);
            alert("시술 목록을 불러오는데 실패했습니다.");
        });
}

// 모달 열기
function openProcedureModal() {
    document.getElementById('procedureModalOverlay').style.display = 'flex';
}

// 모달 닫기
function closeProcedureModal() {
    document.getElementById('procedureModalOverlay').style.display = 'none';
    document.getElementById('procedureName').value = '';
    document.getElementById('procedurePrice').value = '';
    document.getElementById('procedurePreviewImage').src = '/img/shop/camera.png';
    document.getElementById('procedurePreviewImage').style = 'width:20px; height:20px; object-fit:contain;';
    document.getElementById('procedureFileInput').value = '';
    editingProcedureItem = null;
}

// 이미지 미리보기
document.getElementById('procedureFileInput').addEventListener('change', function(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const previewImage = document.getElementById('procedurePreviewImage');
            previewImage.src = e.target.result;
            previewImage.style.width = '100%';
            previewImage.style.height = '100%';
            previewImage.style.objectFit = 'cover';
        };
        reader.readAsDataURL(file);
    }
});

// "추가/수정" 버튼 클릭 시: pendingProcedures 배열에 정보 저장 및 UI 업데이트
document.querySelector('.btn-proc-save').addEventListener('click', function(event) {
    event.preventDefault();

    const procedureName = document.getElementById('procedureName').value.trim();
    const procedurePrice = document.getElementById('procedurePrice').value.trim();
    const previewImage = document.getElementById('procedurePreviewImage');

    // 신규 추가일 경우에만 파일(미리보기 이미지가 기본 이미지인지) 체크
    if (!procedureName || !procedurePrice || (!editingProcedureItem && previewImage.src.includes('camera.png'))) {
        alert("모든 필수 항목을 입력해주세요!");
        return;
    }

    // URL에서 shopId 추출 (update 시 사용)
    const urlParts = window.location.pathname.split('/');
    const shopId = parseInt(urlParts[urlParts.length - 1]);
    const procedureList = document.querySelector('.procedure-list');

    if (!editingProcedureItem) {
        // 신규 추가
        const file = document.getElementById('procedureFileInput').files[0];
        pendingProcedures.push({
            procedureName: procedureName,
            procedurePrice: procedurePrice,
            file: file,
            previewImage: previewImage.src,
            procedureId: null
        });

        // UI 업데이트
        const newProcItem = document.createElement('div');
        newProcItem.className = 'procedure-item';
        newProcItem.innerHTML = `
      <img src="${previewImage.src}" alt="${procedureName}" class="procedure-img" />
      <div class="procedure-content">
        <div class="procedure-header">
          <div class="procedure-name">${procedureName}</div>
          <div class="procedure-actions">
            <a href="#" class="edit-proc-link">수정</a>
            <a href="#" class="delete-proc-link">삭제</a>
          </div>
        </div>
        <div class="procedure-price">${procedurePrice}</div>
      </div>
    `;
        newProcItem.setAttribute('data-index', pendingProcedures.length - 1);
        procedureList.appendChild(newProcItem);

        const divider = document.createElement('hr');
        divider.className = 'divider';
        procedureList.appendChild(divider);
    } else {
        // 수정 모드: UI 업데이트 및 pendingProcedures 배열 업데이트
        const file = document.getElementById('procedureFileInput').files[0]
            || pendingProcedures[editingProcedureItem.getAttribute('data-index')].file;
        const index = editingProcedureItem.getAttribute('data-index');
        const existingProcedureId = pendingProcedures[index].procedureId;

        // 배열 갱신
        pendingProcedures[index] = {
            procedureName: procedureName,
            procedurePrice: procedurePrice,
            file: file,
            previewImage: previewImage.src,
            procedureId: existingProcedureId,
            procedureShopId: shopId
        };

        // UI 갱신
        editingProcedureItem.querySelector('.procedure-name').textContent = procedureName;
        editingProcedureItem.querySelector('.procedure-price').textContent = procedurePrice;
        editingProcedureItem.querySelector('.procedure-img').src = previewImage.src;

        // **중요**: payload 변수를 먼저 선언
        const payload = {
            procedureName: procedureName,
            procedurePrice: procedurePrice,
            procedureShopId: shopId,
            procedureId: existingProcedureId
        };

        // 파일/기존 썸네일 처리
        const file1 = document.getElementById('procedureFileInput').files[0] || file;

        // FormData 생성
        const formData = new FormData();
        formData.append("data", new Blob([JSON.stringify(payload)], { type: "application/json" }));
        if (file1) {
            formData.append("procedureThumbnailFile", file1);
        } else {
            // 새 파일이 없으면 기존 미리보기 URL을 전송
            formData.append("procedureThumbnail", previewImage.src);
        }

        // PUT 요청 전송
        fetch(`/api/mypage/manager/procedure/${existingProcedureId}`, {
            method: 'PUT',
            body: formData
        })
            .then(response => response.json())
            .then(result => {
                if (result.status === 200) {
                    alert("시술 수정이 완료되었습니다.");
                } else {
                    alert("시술 수정에 실패했습니다.");
                }
            })
            .catch(error => {
                console.error(error);
                alert("시술 수정 중 오류가 발생했습니다.");
            });

        editingProcedureItem = null;
    }
    closeProcedureModal();
});

// "수정 완료" 버튼 클릭 시 (전체 pendingProcedures 처리 후 /mypage/myshop으로 이동)
document.querySelector('.btn-proc-save-all').addEventListener('click', function(event) {
    event.preventDefault();
    console.log("현재 pendingProcedures 목록", pendingProcedures);
    alert("시술 수정 작업이 완료되었습니다.");
    window.location.href = '';
});

// 수정/삭제 이벤트 위임 (pendingProcedures 배열과 UI 동기화)
document.querySelector('.procedure-list').addEventListener('click', function(event) {
    event.preventDefault();
    const target = event.target;
    const procItem = target.closest('.procedure-item');
    if (!procItem) return;

    const index = parseInt(procItem.getAttribute('data-index'));
    if (target.classList.contains('edit-proc-link')) {
        editingProcedureItem = procItem;
        document.getElementById('procedureName').value = procItem.querySelector('.procedure-name').textContent;
        document.getElementById('procedurePrice').value = procItem.querySelector('.procedure-price').textContent;
        document.getElementById('procedurePreviewImage').src = procItem.querySelector('.procedure-img').src;
        document.getElementById('procedurePreviewImage').style.width = '100%';
        document.getElementById('procedurePreviewImage').style.height = '100%';
        document.getElementById('procedurePreviewImage').style.objectFit = 'cover';
        openProcedureModal();
    } else if (target.classList.contains('delete-proc-link')) {
        // 삭제 처리: 만약 삭제할 시술의 procedureId가 있다면 백엔드 DELETE API 호출
        const procToDelete = pendingProcedures[index];
        if (procToDelete.procedureId) {
            fetch(`/api/mypage/manager/procedure/${procToDelete.procedureId}`, {
                method: 'DELETE'
            })
                .then(response => response.json())
                .then(result => {
                    if (result.status === 200) {
                        alert("시술 삭제가 완료되었습니다.");
                        // 삭제 성공 시 UI와 pendingProcedures 배열에서 제거
                        pendingProcedures.splice(index, 1);
                        const divider = procItem.nextElementSibling;
                        if (divider && divider.classList.contains('divider')) {
                            divider.remove();
                        }
                        procItem.remove();
                        // 나머지 아이템들의 data-index 재설정
                        const allItems = document.querySelectorAll('.procedure-item');
                        allItems.forEach((item, i) => {
                            item.setAttribute('data-index', i);
                        });
                    } else {
                        alert("시술 삭제에 실패했습니다.");
                    }
                })
                .catch(error => {
                    console.error(error);
                    alert("시술 삭제 중 오류가 발생했습니다.");
                });
        } else {
            // 아직 백엔드에 저장되지 않은 신규 추가 항목인 경우 단순 삭제
            pendingProcedures.splice(index, 1);
            const divider = procItem.nextElementSibling;
            if (divider && divider.classList.contains('divider')) {
                divider.remove();
            }
            procItem.remove();
            // 나머지 아이템들의 data-index 재설정
            const allItems = document.querySelectorAll('.procedure-item');
            allItems.forEach((item, i) => {
                item.setAttribute('data-index', i);
            });
        }
    }
});

// 페이지 로드 시 시술 목록을 백엔드에서 불러옴
window.addEventListener('load', loadProcedures);
