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
        console.log(`fetchShops 호출됨, page: ${page}, pageSize: ${pageSize}`);
        const response = await fetch(`/api/mypage/manager/myshops?page=${page}&pageSize=${pageSize}`);
        const data = await response.json();
        console.log("전체 shop 데이터:", data);
        let shopPage = data.data.shop;
        let shops = shopPage.list;
        console.log("shops 리스트:", shops);
        let tableBody = document.getElementById("shopTableBody");
        tableBody.innerHTML = "";

        shops.forEach((shop, index) => {
            console.log(`각 shop 데이터 (index ${index}):`, shop);
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
    console.log("fetchShopDetail 호출됨, shopId:", shopId, "dropdownId:", dropdownId);
    try {
        // 1) 업체 상세 정보 가져오기
        const response = await fetch(`/api/mypage/manager/shopdetail/${shopId}`);
        const data = await response.json();
        console.log("업체 상세 정보 데이터:", data);
        const shop = data.data.shop;
        console.log("shop 객체:", shop);
        const shopDetailContainer = document.getElementById(dropdownId);

        // 2) media 정보 가져오기 (media_target_type은 "SHOP", media_target_id는 shopId)
        let mediaImageHTML = '';
        try {
            const mediaResponse = await fetch(`/api/media/file?targetType=SHOP&targetId=${shop.shopId}`);
            const mediaData = await mediaResponse.json();
            console.log("media 정보 데이터:", mediaData);

            if (mediaData.data && mediaData.data.fileUrns) {
                const fileUrns = mediaData.data.fileUrns;

                // 1) 컨테이너 시작 태그
                mediaImageHTML += `<div class="media-images">`;

                // 2) 각 이미지마다 .media-image 블록 생성
                fileUrns.forEach((urn) => {
                    mediaImageHTML += `
            <div class="media-image">
                <img src="${urn}" alt="업체 이미지">
            </div>
        `;
                });

                // 3) 컨테이너 종료 태그
                mediaImageHTML += `</div>`;
            } else {
                console.log("media 데이터에 fileUrns 없음");
            }

        } catch (error) {
            console.error("미디어 정보를 불러오는 중 오류 발생:", error);
        }

        // 3) 디자이너 목록 HTML 생성
        let designerHTML = '';
        if (shop.designers && shop.designers.length > 0) {
            designerHTML = shop.designers.map(designer => `
                <div class="info-item">
                    <img class="thumbnail" src="${designer.designerThumbnail}" alt="디자이너 이미지">
                    <div>
                        <p><strong>디자이너명:</strong> ${designer.designerName}</p>
                        <p><strong>디자이너 소개:</strong> ${designer.designerIntroduction}</p>
                    </div>
                </div>
            `).join('');

            designerHTML += `
                <div class="designer-action-buttons">
                    <button class="edit-button" onclick="editDesigner(${shop.shopId})">디자이너 추가</button>
                    <button class="delete-button" onclick="deleteDesigner(${shop.shopId})">디자이너 수정/삭제</button>
                </div>
            `;
        } else {
            designerHTML += `
                <div class="designer-action-buttons">
                    <button class="edit-button" onclick="editDesigner(${shop.shopId})">디자이너 추가</button>
                </div>
            `;
        }

        // 4) 시술 목록 HTML 생성
        let procedureHTML = '';
        if (shop.procedures && shop.procedures.length > 0) {
            procedureHTML = shop.procedures.map(procedure => `
                <div class="info-item">
                    <img class="thumbnail" src="${procedure.procedureThumbnail}" alt="시술 이미지">
                    <div>
                        <p><strong>시술명:</strong> ${procedure.procedureName}</p>
                        <p><strong>가격:</strong> ${procedure.procedurePrice}</p>
                    </div>
                </div>
            `).join('');

            procedureHTML += `
                <div class="procedure-action-buttons">
                    <button class="edit-button" onclick="editProcedure(${shop.shopId})">시술 추가</button>
                    <button class="delete-button" onclick="deleteProcedure(${shop.shopId})">시술 수정/삭제</button>
                </div>
            `;
        } else {
            procedureHTML = `
                <div class="procedure-action-buttons">
                    <button class="edit-button" onclick="editProcedure(${shop.shopId})">시술 추가</button>
                </div>
            `;
        }

        // 5) 최종 HTML 조합 (미디어 정보 포함)
        shopDetailContainer.innerHTML = `
            <div class="section-title">업체 정보</div>
            ${mediaImageHTML}
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
window.editShop = function(shopId) {
    console.log("editShop 호출됨, shopId:", shopId);
    window.location.href = `/mypage/setShop/${shopId}`;
};

window.editDesigner = function(shopId) {
    console.log("editDesigner 호출됨, shopId:", shopId);
    window.location.href = `/mypage/designer/${shopId}`;
};

window.deleteDesigner = function(shopId) {
    console.log("deleteDesigner 호출됨, shopId:", shopId);
    window.location.href = `/mypage/setDesigner/${shopId}`;
};

function deleteShop(shopId) {
    console.log("deleteShop 호출됨, shopId:", shopId);
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

window.editProcedure = function(shopId) {
    console.log("editProcedure 호출됨, shopId:", shopId);
    window.location.href = `/mypage/procedure/${shopId}`;
};

window.deleteProcedure = function(shopId) {
    console.log("deleteProcedure 호출됨, shopId:", shopId);
    window.location.href = `/mypage/setProcedure/${shopId}`;
};

// 초기 데이터 로드
fetchShops();
