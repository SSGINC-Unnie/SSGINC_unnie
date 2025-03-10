let lastOpenedDropdown = null; // 마지막으로 열린 드롭다운 추적
document.addEventListener('DOMContentLoaded', fetchShops);


// 드롭다운 토글 함수
function toggleDropdown(id) {
    const dropdown = document.getElementById(id);
    if (lastOpenedDropdown && lastOpenedDropdown !== dropdown) {
        lastOpenedDropdown.style.maxHeight = '0';
    }
    if (!dropdown.style.maxHeight || dropdown.style.maxHeight === '0px') {
        dropdown.style.maxHeight = dropdown.scrollHeight + 'px';
        lastOpenedDropdown = dropdown;
    } else {
        dropdown.style.maxHeight = '0';
        lastOpenedDropdown = null;
    }
}

// 업체 데이터를 백엔드에서 받아와 테이블에 채워넣는 함수
// 업체 데이터를 백엔드에서 받아와 테이블에 채워넣는 함수
async function fetchShops() {
    try {
        const response = await fetch(`/api/admin/shop/approve`);
        const data = await response.json();
        // 백엔드 응답이 리스트 형태이므로 바로 할당합니다.
        let shops = data.data.shop;

        let tableBody = document.getElementById("shopTableBody");
        tableBody.innerHTML = "";
        shops.forEach((shop, index) => {
            let row = document.createElement("tr");
            row.innerHTML = `
                <td>${shop.shopName}</td>
                <td>${shop.shopRepresentationName}</td>
                <td>${shop.shopCategory}</td>
                <td>${shop.shopLocation}</td>
                <td>
                    <button class="details-button" onclick="fetchShopDetail(${shop.shopId}, 'dropdown${index}')">
                        상세보기
                    </button>
                </td>
            `;
            tableBody.appendChild(row);

            let detailRow = document.createElement("tr");
            let detailCell = document.createElement("td");
            detailCell.setAttribute("colspan", "5");
            detailCell.innerHTML = `
                <div id="dropdown${index}" class="dropdown-content">
                    <!-- 상세 정보가 동적으로 추가됩니다. -->
                </div>
            `;
            detailRow.appendChild(detailCell);
            tableBody.appendChild(detailRow);
        });
    } catch (error) {
        console.error("업체 데이터를 불러오는 중 오류 발생:", error);
    }
}


// 업체 상세 데이터를 백엔드에서 받아와 상세 정보를 표시하는 함수
async function fetchShopDetail(shopId, dropdownId) {
    try {
        const response = await fetch(`/api/admin/shop/approve/detail/${shopId}`);
        const data = await response.json();
        const shop = data.data.shop;
        const shopDetailContainer = document.getElementById(dropdownId);

        let designerHTML = '';
        if (shop.designers && shop.designers.length > 0) {
            designerHTML = shop.designers.map(designer => `
                <div class="info-item">
                    <img class="thumbnail" src="${designer.designerThumbnail}">
                    <div>
                        <p><strong>디자이너명:</strong> ${designer.designerName}</p>
                        <p><strong>디자이너 소개:</strong> ${designer.designerIntroduction}</p>
                    </div>
                </div>
            `).join('');
        } else {
            designerHTML = '<p>디자이너 정보가 없습니다.</p>';
        }

        let procedureHTML = '';
        if (shop.procedures && shop.procedures.length > 0) {
            procedureHTML = shop.procedures.map(procedure => `
                <div class="info-item">
                    <img class="thumbnail" src="${procedure.procedureThumbnail}">
                    <div>
                        <p><strong>시술명:</strong> ${procedure.procedureName}</p>
                        <p><strong>가격:</strong> ${procedure.procedurePrice}</p>
                    </div>
                </div>
            `).join('');
        } else {
            procedureHTML = '<p>시술 정보가 없습니다.</p>';
        }

        shopDetailContainer.innerHTML = `
            <div class="section-title">업체 정보</div>
            <p><strong>업체명:</strong> ${shop.shopName}</p>
            <p><strong>업체 위치:</strong> ${shop.shopLocation}</p>
            <p><strong>카테고리:</strong> ${shop.shopCategory}</p>
            <p><strong>영업시간:</strong> ${shop.shopBusinessTime}</p>
            <p><strong>전화번호:</strong> ${shop.shopTel}</p>
            <p><strong>업체 소개:</strong> ${shop.shopIntroduction}</p>
            <p><strong>휴무일:</strong> ${shop.shopClosedDay}</p>
            <p><strong>사업자 등록번호:</strong> ${shop.shopBusinessNumber}</p>
            <p><strong>대표자명:</strong> ${shop.shopRepresentationName}</p>

            <div class="section-title">디자이너 정보</div>
            ${designerHTML}

            <div class="section-title">시술 정보</div>
            ${procedureHTML}
            
            <div class="action-buttons">
                <button class="edit-button" onclick="approveShop(${shopId})">승인</button>
                <button class="delete-button" onclick="rejectShop(${shopId})">거절</button>
            </div>
        `;
        toggleDropdown(dropdownId);
    } catch (error) {
        console.error("업체 상세 정보를 불러오는 중 오류 발생:", error);
    }
}

function approveShop(shopId) {
    // 승인 요청 데이터: AdminShopUpdateRequest의 형태로 최소 shopId 포함
    const requestData = {
        shopId: shopId
    };

    fetch("/api/admin/shop/approve", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestData)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("업체 승인 처리 중 오류 발생");
            }
            return response.json();
        })
        .then(data => {
            console.log("업체 승인 처리 완료:", data);

            // 승인 완료 후 추가 동작 구현 (예: UI 업데이트, 알림 등)
            alert("업체 승인이 완료되었습니다.");
            location.reload();

        })
        .catch(error => {
            console.error("업체 승인 처리 중 오류 발생:", error);
            alert("업체 승인 처리에 실패하였습니다.");
        });
}


// 거절 버튼 클릭 시 처리할 함수 (추후 실제 로직 구현 필요)
function rejectShop(shopId) {
    fetch(`/api/admin/shop/approve/${shopId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("업체 거절 처리 중 오류 발생");
            }
            return response.json();
        })
        .then(data => {
            console.log("업체 거절 처리 완료:", data);
            // 거절 완료 후 추가 동작 구현 (예: UI 업데이트, 알림 등)
            alert("업체 거절 처리가 완료되었습니다.");
            location.reload();

        })
        .catch(error => {
            console.error("업체 거절 처리 중 오류 발생:", error);
            alert("업체 거절 처리에 실패하였습니다.");
        });
}



