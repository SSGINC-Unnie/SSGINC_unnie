// URL의 쿼리 파라미터에서 shopId 추출
function getShopIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get('shopId');
}

// 1) 홈 탭 상세 정보 로딩
function loadShopDetails(shopId) {
    fetch('/api/shop/shopdetails/home/' + shopId)
        .then(response => response.json())
        .then(data => {
            console.log("[홈 탭] 받은 데이터:", data);
            if (!data || !data.data || !data.data.shop) {
                console.error("[홈 탭] 유효하지 않은 응답 구조:", data);
                return;
            }
            const shop = data.data.shop;
            document.getElementById('shopName').textContent = shop.shopName || '이름 없음';
            document.getElementById('avgRate').textContent = (typeof shop.avgRate === 'number') ? shop.avgRate.toFixed(1) : '0.0';
            document.getElementById('reviewCount').textContent = (shop.reviewCount !== undefined && shop.reviewCount !== null) ? shop.reviewCount : 0;
            document.getElementById('shopLocation').textContent = shop.shopLocation || '주소 정보 없음';
            document.getElementById('shopBusinessTime').textContent = shop.shopBusinessTime || '영업시간 정보 없음';
            document.getElementById('shopTel').textContent = shop.shopTel || '0507-xxxx-xxxx (미등록)';
            document.getElementById('reviewSummary').textContent = shop.reviewSummary || '리뷰 요약 없음';
            document.getElementById('latestMemberNickname').textContent = shop.latestMemberNickname || '닉네임 없음';
            if (shop.latestReviewDate) {
                const dateObj = new Date(shop.latestReviewDate);
                const yyyy = dateObj.getFullYear();
                const mm = String(dateObj.getMonth() + 1).padStart(2, '0');
                const dd = String(dateObj.getDate()).padStart(2, '0');
                document.getElementById('latestReviewDate').textContent = `${yyyy}-${mm}-${dd}`;
            } else {
                document.getElementById('latestReviewDate').textContent = '리뷰 날짜 없음';
            }
            document.getElementById('latestReviewContent').textContent = shop.latestReviewContent || '리뷰 내용 없음';
        })
        .catch(err => {
            console.error("[홈 탭] 샵 상세 조회 실패:", err);
        });
}

// 2) 디자이너 탭 정보 로딩
function loadDesigners(shopId) {
    fetch('/api/shop/shopdetails/designer/' + shopId)
        .then(response => response.json())
        .then(data => {
            console.log("[디자이너 탭] 받은 데이터:", data);
            if (!data || !data.data || !data.data.designers) {
                console.error("[디자이너 탭] 유효하지 않은 응답 구조:", data);
                return;
            }
            const designers = data.data.designers;
            const designerListEl = document.getElementById('designerList');
            designerListEl.innerHTML = '';
            designers.forEach(designer => {
                const thumbnailSrc = designer.designerThumbnail ? designer.designerThumbnail : '/img/shop/download.jpg';
                const item = document.createElement('div');
                item.classList.add('designer-item');
                item.innerHTML = `
                    <img src="${thumbnailSrc}" alt="디자이너 사진" class="designer-thumbnail" />
                    <div class="designer-info">
                        <div class="designer-name">${designer.designerName}</div>
                        <div class="designer-intro">${designer.designerIntroduction || ''}</div>
                    </div>
                `;
                designerListEl.appendChild(item);
            });
        })
        .catch(err => {
            console.error("[디자이너 탭] 디자이너 조회 실패:", err);
        });
}

// 3) 시술 탭 정보 로딩
function loadProcedures(shopId) {
    fetch('/api/shop/shopdetails/procedure/' + shopId)
        .then(response => response.json())
        .then(data => {
            console.log("[시술 탭] 받은 데이터:", data);
            if (!data || !data.data || !data.data.procedures) {
                console.error("[시술 탭] 유효하지 않은 응답 구조:", data);
                return;
            }
            const procedures = data.data.procedures;
            const procedureListEl = document.getElementById('procedureList');
            procedureListEl.innerHTML = '';
            procedures.forEach(procedure => {
                const thumbnailSrc = '/img/shop/download.jpg'; // 시술은 기본 이미지 사용
                const item = document.createElement('div');
                item.classList.add('designer-item');
                item.innerHTML = `
                    <img src="${thumbnailSrc}" alt="시술 이미지" class="designer-thumbnail" />
                    <div class="designer-info">
                        <div class="designer-name">${procedure.procedureName}</div>
                        <div class="designer-intro">가격: ${procedure.procedurePrice}원</div>
                    </div>
                `;
                procedureListEl.appendChild(item);
            });
        })
        .catch(err => {
            console.error("[시술 탭] 시술 조회 실패:", err);
        });
}

// 4) 정보 탭 업체 상세정보 로딩
function loadShopInfo(shopId) {
    fetch('/api/shop/shopdetails/info/' + shopId)
        .then(response => response.json())
        .then(data => {
            console.log("[정보 탭] 받은 데이터:", data);
            if (!data || !data.data || !data.data.shopDetails) {
                console.error("[정보 탭] 유효하지 않은 응답 구조:", data);
                return;
            }
            const info = data.data.shopDetails;
            document.getElementById('infoShopName').textContent = info.shopName || '정보 없음';
            document.getElementById('infoShopLocation').textContent = info.shopLocation || '정보 없음';
            document.getElementById('infoShopCategory').textContent = info.shopCategory ? info.shopCategory.toString() : '정보 없음';
            document.getElementById('infoShopBusinessTime').textContent = info.shopBusinessTime || '정보 없음';
            document.getElementById('infoShopTel').textContent = info.shopTel || '정보 없음';
            document.getElementById('infoShopClosedDay').textContent = info.shopClosedDay || '정보 없음';
            document.getElementById('infoShopIntroduction').textContent = info.shopIntroduction || '정보 없음';
        })
        .catch(err => {
            console.error("[정보 탭] 업체 상세정보 조회 실패:", err);
        });
}

// 페이지 로드 및 탭 전환 처리
document.addEventListener('DOMContentLoaded', () => {
    const shopId = getShopIdFromURL();
    if (!shopId) {
        console.error("shopId 파라미터가 존재하지 않습니다.");
        return;
    }
    // 기본적으로 홈 탭 정보 먼저 로드
    loadShopDetails(shopId);
    // 탭 버튼 클릭 이벤트 처리
    const tabButtons = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    tabButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            tabButtons.forEach(b => b.classList.remove('active'));
            tabContents.forEach(tc => tc.classList.remove('active'));
            btn.classList.add('active');
            const targetTab = btn.getAttribute('data-tab');
            const targetContentId = 'tabContent' + targetTab.charAt(0).toUpperCase() + targetTab.slice(1);
            document.getElementById(targetContentId).classList.add('active');
            if (targetTab === 'designer') {
                loadDesigners(shopId);
            }
            if (targetTab === 'procedure') {
                loadProcedures(shopId);
            }
            if (targetTab === 'info') {
                loadShopInfo(shopId);
            }
        });
    });
});
