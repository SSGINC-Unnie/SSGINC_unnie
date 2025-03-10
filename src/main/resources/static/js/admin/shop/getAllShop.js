// getAllShop.js

let lastOpenedDropdown = null; // 마지막으로 열린 드롭다운 추적
let currentPage = 1;            // 현재 페이지
const pageSize = 5;             // 한 페이지에 표시할 데이터 수
let totalPages = 1;             // 총 페이지 수 (API 응답에 따라 업데이트됨)

// DOM이 완전히 로드된 후 실행
document.addEventListener("DOMContentLoaded", function() {
    // 페이지 전환 버튼 이벤트 핸들러 등록
    document.getElementById("prevPage").addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            fetchShops(currentPage, pageSize);
        }
    });
    document.getElementById("nextPage").addEventListener("click", () => {
        if (currentPage < totalPages) {
            currentPage++;
            fetchShops(currentPage, pageSize);
        }
    });
    // 초기 데이터 로드
    fetchShops(currentPage, pageSize);
});

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
async function fetchShops(page = 1, pageSize = 5) {
    try {
        const response = await fetch(`/api/admin/shop?page=${page}&pageSize=${pageSize}`);
        const data = await response.json();
        let shopPage = data.data.shop;
        let shops = shopPage.list;

        // API 응답의 페이지 정보를 업데이트
        currentPage = shopPage.pageNum;
        totalPages = shopPage.pages; // 또는 Math.ceil(shopPage.total / pageSize)
        updatePaginationUI();

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
        // 상세보기 API 호출
        const response = await fetch(`/api/admin/shop/detail/${shopId}`);
        const data = await response.json();
        const shop = data.data.shop;
        const shopDetailContainer = document.getElementById(dropdownId);

        // 1) 디자이너 목록 HTML 생성
        let designerHTML = '';
        if (shop.designers && shop.designers.length > 0) {
            // 모든 디자이너 정보 표시
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

        // 2) 시술 목록 HTML 생성
        let procedureHTML = '';
        if (shop.procedures && shop.procedures.length > 0) {
            // 모든 시술 정보 표시
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

// 페이지네이션 UI 업데이트 함수
function updatePaginationUI() {
    document.getElementById("currentPage").textContent = currentPage;
    document.getElementById("totalPages").textContent = totalPages;
}

// 수정/삭제 관련 함수 (구현은 필요에 따라 수정)
function editDesigner(shopId) {
    console.log("디자이너 수정 (shopId):", shopId);
    // 실제 수정 로직 구현
}
function deleteDesigner(shopId) {
    console.log("디자이너 삭제 (shopId):", shopId);
    // 실제 삭제 로직 구현
}

