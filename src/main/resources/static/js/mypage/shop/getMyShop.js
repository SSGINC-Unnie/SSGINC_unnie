let lastOpenedDropdown = null; // 마지막으로 열린 드롭다운 추적

// 드롭다운 토글 함수
function toggleDropdown(id) {
    var dropdown = document.getElementById(id);
    if (lastOpenedDropdown && lastOpenedDropdown !== dropdown) {
        lastOpenedDropdown.style.maxHeight = '0';
    }
    if (dropdown.style.maxHeight === '0px' || dropdown.style.maxHeight === '') {
        dropdown.style.maxHeight = dropdown.scrollHeight + 'px';
        lastOpenedDropdown = dropdown;
    } else {
        dropdown.style.maxHeight = '0';
        lastOpenedDropdown = null;
    }
}

// 업체 데이터를 백엔드에서 받아와 테이블에 채워넣는 함수
async function fetchShops(page = 1, pageSize = 5) {
    try {
        const response = await fetch(`/api/mypage/manager/myshops?page=${page}&pageSize=${pageSize}`);
        const data = await response.json();
        let shopPage = data.data.shop;
        let shops = shopPage.list;
        let tableBody = document.getElementById("shopTableBody");
        tableBody.innerHTML = "";

        shops.forEach((shop, index) => {
            console.log(shop);
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
    console.log("shop 객체:", shopId); // shop 객체 확인

    try {
        const response = await fetch(`/api/mypage/manager/shopdetail/${shopId}`);
        const data = await response.json();
        const shop = data.data.shop;
        const shopDetailContainer = document.getElementById(dropdownId);

        // 1) 디자이너 목록 HTML 생성
        let designerHTML = '';
        if (shop.designers && shop.designers.length > 0) {
            // 모든 디자이너 정보를 표시
            designerHTML = shop.designers.map(designer => `
                <div class="info-item">
                    <img class="thumbnail" src="${designer.designerThumbnail}">
                    <div>
                        <p><strong>디자이너명:</strong> ${designer.designerName}</p>
                        <p><strong>디자이너 소개:</strong> ${designer.designerIntroduction}</p>
                    </div>
                </div>
            `).join('');

            // 디자이너 목록 아래에 수정/삭제 버튼 배치
            designerHTML += `
                <div class="designer-action-buttons">
                    <button class="edit-button" onclick="editDesigner(${shop.shopId})">디자이너 수정</button>
                    <button class="delete-button" onclick="deleteDesigner(${shop.shopId})">디자이너 삭제</button>
                </div>
            `;
        } else {
            designerHTML = '<p>디자이너 정보가 없습니다.</p>';
        }

        // 2) 시술 목록 HTML 생성
        let procedureHTML = '';
        if (shop.procedures && shop.procedures.length > 0) {
            // 모든 시술 정보를 표시
            procedureHTML = shop.procedures.map(procedure => `
                <div class="info-item">
                    <img class="thumbnail" src="${procedure.procedureThumbnail}">
                    <div>
                        <p><strong>시술명:</strong> ${procedure.procedureName}</p>
                        <p><strong>가격:</strong> ${procedure.procedurePrice}</p>
                    </div>
                </div>
            `).join('');

            // 시술 목록 아래에 수정/삭제 버튼 배치
            procedureHTML += `
                <div class="procedure-action-buttons">
                    <button class="edit-button" onclick="editProcedure(${shop.shopId})">시술 수정</button>
                    <button class="delete-button" onclick="deleteProcedure(${shop.shopId})">시술 삭제</button>
                </div>
            `;
        } else {
            procedureHTML = '<p>시술 정보가 없습니다.</p>';
        }

        // 3) 최종 HTML 조합
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
                        <div class="shop-action-buttons">
                <button class="edit-button" onclick="editShop(${shopId})">업체 수정</button>
                <button class="delete-button" onclick="deleteShop(${shopId})">업체 삭제</button>
            </div>

            <div class="section-title">디자이너 정보</div>
            ${designerHTML}

            <div class="section-title">시술 정보</div>
            ${procedureHTML}
        `;
        toggleDropdown(dropdownId);
    } catch (error) {
        console.error("업체 상세 정보를 불러오는 중 오류 발생:", error);
    }
}

// 추가적인 수정/삭제 함수 (임시)
function editDesigner(shopId) {
    console.log("디자이너 수정:", shopId);
    // 실제 수정 로직 구현
}

function deleteShop(shopId) {
    console.log("deleteShop 호출, shopId:", shopId);
    if (confirm("정말 업체를 삭제하시겠습니까?")) {
        fetch(`/api/mypage/manager/shop/${shopId}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("업체 삭제 실패");
                }
                return response.json();
            })
            .then(data => {
                console.log("업체 삭제 완료:", data);
                alert("업체 삭제가 완료되었습니다.");
                // 업체 삭제 후 목록 새로고침
                fetchShops(currentPage, pageSize);
                location.reload();
            })
            .catch(error => console.error("업체 삭제 중 오류 발생:", error));
    }
}
function editProcedure(shopId) {
    console.log("시술 수정:", shopId);
    // 실제 수정 로직 구현
}

function deleteProcedure(shopId) {
    console.log("시술 삭제:", shopId);
    // 실제 삭제 로직 구현
}

// 초기 데이터 로드
fetchShops();
