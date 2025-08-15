// URL의 쿼리 파라미터에서 shopId 추출
function getShopIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    return params.get('shopId');
}

/**
 * shopId로 해당 업체의 이미지들을 불러와서
 * shop-hero(상단 이미지 영역)에 캐러셀 형태로 삽입하는 예시
 */
async function loadShopImages(shopId) {
    try {
        // 1) 이미지 데이터 가져오기
        const res = await fetch(`/api/media/file?targetType=SHOP&targetId=${shopId}`);
        const data = await res.json();

        // 2) fileUrns 확인 (여러 장) 또는 fileUrn (단일)
        let fileList = [];
        if (data && data.data) {
            if (Array.isArray(data.data.fileUrns)) {
                fileList = data.data.fileUrns;
            } else if (data.data.fileUrn) {
                fileList = [data.data.fileUrn];
            }
        }

        // 3) 캐러셀 HTML 생성
        //    fileList.length > 1 이면 슬라이드, 1이면 단일 이미지
        const heroEl = document.querySelector('.shop-hero');
        if (!heroEl) return; // 엘리먼트가 없으면 중단

        if (fileList.length === 0) {
            // 이미지가 없는 경우 → 기본 이미지를 세팅
            heroEl.innerHTML = `
                <img src="/img/shop/download.jpg" alt="기본 이미지" style="width:100%; height:100%; object-fit:cover;">
            `;
            return;
        }

        // 여러 장일 경우 캐러셀, 한 장일 경우 단일이미지
        if (fileList.length === 1) {
            heroEl.innerHTML = `
                <img src="${fileList[0]}" alt="샵 이미지" 
                     onerror="this.onerror=null; this.src='/img/shop/download.jpg';"
                     style="width:100%; height:100%; object-fit:cover;">
            `;
        } else {
            // 캐러셀 생성 (좌우 버튼 + 래퍼)
            heroEl.innerHTML = getCarouselHTMLForDetail(fileList);
        }

    } catch (error) {
        console.error("이미지 로딩 실패:", error);
    }
}

/**
 * [캐러셀 HTML 생성] - map.html에서 사용한 것과 유사하게 작성하되,
 * 여기서는 shopId 대신에 배열 자체만 받아서 만듭니다.
 * 필요하다면 shopId로 구분해도 됩니다.
 */
function getCarouselHTMLForDetail(fileUrns) {
    // 캐러셀 컨테이너에 넣을 슬라이드들
    const slides = fileUrns.map(url => `
        <div class="carousel-slide">
            <img 
                src="${url}" 
                alt="샵 이미지" 
                onerror="this.onerror=null;this.src='/img/shop/download.jpg';"
            >
        </div>
    `).join('');

    return `
        <div class="carousel-container">
            <div class="carousel-wrapper">
                ${slides}
            </div>
            <button class="carousel-btn left" onclick="prevSlideDetail()">&#10094;</button>
            <button class="carousel-btn right" onclick="nextSlideDetail()">&#10095;</button>
        </div>
    `;
}

// 현재 슬라이드 인덱스
let currentSlideIndex = 0;

/** 이전 슬라이드 버튼 */
function prevSlideDetail() {
    const wrapper = document.querySelector('.carousel-wrapper');
    if (!wrapper) return;
    const slideCount = wrapper.querySelectorAll('.carousel-slide').length;
    if (slideCount <= 1) return;

    currentSlideIndex = (currentSlideIndex > 0) ? currentSlideIndex - 1 : slideCount - 1;
    updateSlidePositionDetail();
}

/** 다음 슬라이드 버튼 */
function nextSlideDetail() {
    const wrapper = document.querySelector('.carousel-wrapper');
    if (!wrapper) return;
    const slideCount = wrapper.querySelectorAll('.carousel-slide').length;
    if (slideCount <= 1) return;

    currentSlideIndex = (currentSlideIndex < slideCount - 1) ? currentSlideIndex + 1 : 0;
    updateSlidePositionDetail();
}

/** 캐러셀 이동 처리 */
function updateSlidePositionDetail() {
    const wrapper = document.querySelector('.carousel-wrapper');
    if (!wrapper) return;

    // 슬라이드 하나의 폭을 100%로 보고, currentSlideIndex * 100%만큼 왼쪽으로 이동
    const shiftPercentage = 100 * currentSlideIndex;
    wrapper.style.transform = `translateX(-${shiftPercentage}%)`;
}


// 1) 홈 탭 상세 정보 로딩
async function loadShopDetails(shopId) {
    try {
        const response = await fetch('/api/shop/shopdetails/home/' + shopId);
        const data = await response.json();

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

        const hasReview =
            (typeof shop.reviewCount === 'number' && shop.reviewCount > 0) &&
            (shop.latestReviewDate || shop.latestReviewContent);

        if (!hasReview) {
            // 상단 평균 평점도 ‘-’ 로 표시
            document.getElementById('avgRate').textContent = '-';
            renderEmptyReviewState();
        } else {
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
        }
    } catch (err) {
        console.error("[홈 탭] 샵 상세 조회 실패:", err);
    }
}

function renderEmptyReviewState() {
    const box = document.querySelector('.review-box');
    if (!box) return;

    box.innerHTML = `
      <div class="empty-review">
        <img src="/img/shop/empty-review.svg" class="empty-illust"
             onerror="this.style.display='none'" alt="empty" />
        <div class="empty-title">아직 리뷰가 없어요</div>
        <div class="empty-desc">첫 리뷰어가 되어 다른 이용자에게 도움을 주세요.</div>
        <div class="empty-actions">
          <button class="btn btn-primary" onclick="goWriteReview()">첫 리뷰 쓰기</button>
          <button class="btn" onclick="copyShareLink()">링크 공유</button>
        </div>
      </div>
    `;

    // “리뷰 더보기” 버튼 숨김
    const more = document.querySelector('.review-more-container');
    if (more) more.style.display = 'none';
}
function goWriteReview() {
    const shopId = getShopIdFromURL();
    if (shopId) window.location.href = `/review/ocr`;
}
function copyShareLink() {
    const url = location.href;
    if (navigator.clipboard?.writeText) {
        navigator.clipboard.writeText(url).then(() => alert('링크를 복사했어요.'));
    } else {
        prompt('아래 주소를 복사하세요:', url);
    }
}


// 2) 디자이너 탭 정보 로딩
async function loadDesigners(shopId) {
    try {
        const response = await fetch('/api/shop/shopdetails/designer/' + shopId);
        const data = await response.json();

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
    } catch (err) {
        console.error("[디자이너 탭] 디자이너 조회 실패:", err);
    }
}

// 3) 시술 탭 정보 로딩
async function loadProcedures(shopId) {
    try {
        const response = await fetch('/api/shop/shopdetails/procedure/' + shopId);
        const data = await response.json();

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
    } catch (err) {
        console.error("[시술 탭] 시술 조회 실패:", err);
    }
}

// 4) 정보 탭 업체 상세정보 로딩
async function loadShopInfo(shopId) {
    try {
        const response = await fetch('/api/shop/shopdetails/info/' + shopId);
        const data = await response.json();

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
    } catch (err) {
        console.error("[정보 탭] 업체 상세정보 조회 실패:", err);
    }
}

// 페이지 로드 및 탭 전환 처리
document.addEventListener('DOMContentLoaded', () => {
    const shopId = getShopIdFromURL();
    if (!shopId) {
        console.error("shopId 파라미터가 존재하지 않습니다.");
        return;
    }
    loadShopImages(shopId);

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


function goToReservation() {
    const shopId = getShopIdFromURL(); // 기존에 있던 shopId 추출 함수 재사용
    if (!shopId) {
        console.error("예약할 업체의 shopId를 찾을 수 없습니다.");
        alert("업체 정보가 올바르지 않아 예약을 진행할 수 없습니다.");
        return;
    }

    // shopId를 쿼리 파라미터로 넘기면서 /reservation 페이지로 이동
    window.location.href = `/reservation?shopId=${shopId}`;
}

// JS 부분 (showMoreReviews 함수 추가)
function showMoreReviews() {
    const shopId = getShopIdFromURL();  // 이미 선언된 함수 활용
    if (!shopId) {
        console.error("shopId가 없습니다.");
        return;
    }
    // /review/shop?shopId=... 형태로 이동
    window.location.href = `/review/shop?shopId=${shopId}`;
}