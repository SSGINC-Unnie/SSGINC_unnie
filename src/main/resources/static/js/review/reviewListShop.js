// 전역 변수 설정
let offset = 0;
const limit = 10;
let isLoading = false;
let allLoaded = false;

/**
 * URL의 쿼리 파라미터에서 shopId 추출
 */
function getShopIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    const shopId = params.get('shopId');
    if (!shopId) {
        console.log("shopId를 찾을 수 없습니다.");
    }
    return shopId;
}

/**
 * 주어진 평점(rating)을 기반으로 별점 HTML 생성
 * - rating: 1 ~ 5 사이 숫자
 * - 채워진 별은 클래스 "star full", 나머지는 "star" 클래스를 적용
 */
function getStarRatingHTML(rating) {
    let html = '';
    for (let i = 1; i <= 5; i++) {
        html += `<span class="star ${i <= rating ? 'full' : ''}">★</span>`;
    }
    return html;
}

/**
 * 날짜 문자열을 "yyyy-mm-dd" 형식으로 변환
 */
function formatDate(dateString) {
    if (!dateString) return "";
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = ("0" + (date.getMonth() + 1)).slice(-2);
    const day = ("0" + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
}

/**
 * 리뷰 개수를 로드하여 .review-count 요소에 표시
 */
function loadReviewCount(shopId) {
    fetch(`/api/review/shop/${shopId}/count`)
        .then(response => response.json())
        .then(data => {
            const count = data.data.totalReviewCount;
            const countElement = document.querySelector('.review-count');
            if (countElement) {
                countElement.textContent = "리뷰 " + count;
            }
        })
        .catch(error => console.error('Error fetching review count:', error));
}

/**
 * 리뷰 목록을 로드하여 화면에 렌더링
 * @param {string} shopId
 * @param {string} keyword
 * @param {string} sortType
 * @param {number} offsetParam
 * @param {number} limitParam
 * @param {boolean} isRefresh - true이면 컨테이너를 초기화 후 로드, false이면 append 모드로 추가
 */
function loadReviews(shopId, keyword, sortType, offsetParam, limitParam, isRefresh = false) {
    if (isLoading || allLoaded) return;
    isLoading = true;

    const url = `/api/review/shop/${shopId}?keyword=${encodeURIComponent(keyword)}&sortType=${sortType}&offset=${offsetParam}&limit=${limitParam}`;
    fetch(url)
        .then(response => response.json())
        .then(data => {
            const reviews = data.data.reviews;
            const reviewListContainer = document.querySelector('.review-list');
            if (!reviewListContainer) return;

            // 만약 새로 로드하는 경우 컨테이너 초기화
            if (isRefresh) {
                reviewListContainer.innerHTML = "";
            }

            // 리뷰가 없으면 모두 로드되었다고 표시
            if (!reviews || reviews.length === 0) {
                allLoaded = true;
            } else {
                reviews.forEach(review => {
                    const reviewItem = document.createElement('div');
                    reviewItem.className = "review-item";

                    const formattedDate = formatDate(review.reviewDate);
                    const starRatingHTML = getStarRatingHTML(review.reviewRate);

                    // review.reviewKeyword가 "친절함, 깔끔함, 재방문 의사"와 같은 콤마 구분 문자열이라고 가정
                    const keywordHTML = review.reviewKeyword
                        ? review.reviewKeyword.split(',').map(keyword => `<span class="keyword-pill">${keyword.trim()}</span>`).join(' ')
                        : '';

                    reviewItem.innerHTML = `
                        <div class="review-meta-top">
                            <span class="review-shopname">${review.shopName}</span>
                            <span class="review-date">${formattedDate}</span>
                        </div>
                        <div class="review-author">
                            <span class="reviewer">${review.memberNickName}</span>
                        </div>
                        <div class="star-rating">
                            ${starRatingHTML}
                        </div>
                        <div class="review-image-section">
                            <img src="${review.reviewImage ? review.reviewImage : '/img/review/icon.png'}" alt="리뷰 이미지">
                        </div>
                        <div class="review-content">${review.reviewContent}</div>
                        ${ keywordHTML ? `<div class="review-keyword-list">${keywordHTML}</div>` : '' }
                    `;
                    reviewListContainer.appendChild(reviewItem);
                });
                // 다음 호출을 위해 offset 업데이트
                offset += reviews.length;
            }
            isLoading = false;
        })
        .catch(error => {
            console.error('Error fetching reviews:', error);
            isLoading = false;
        });
}

/**
 * 현재 선택된 키워드를 반환 (키워드 필터 영역에서 선택한 체크박스 값)
 */
function getCurrentKeyword() {
    return getSelectedKeywords();
}

/**
 * 선택된 체크박스 값을 콤마 구분 문자열로 조합하는 함수
 */
function getSelectedKeywords() {
    const keywordCheckboxes = document.querySelectorAll('input[name="keyword"]:checked');
    return Array.from(keywordCheckboxes)
        .map(cb => cb.value)
        .join(",");
}

// 키워드 체크박스에 change 이벤트 리스너 등록 (필터링 시)
document.querySelectorAll('input[name="keyword"]').forEach(checkbox => {
    checkbox.addEventListener('change', function () {
        const shopId = getShopIdFromURL();
        const selectedKeywords = getSelectedKeywords(); // 예: "친절함,깔끔함"
        const sortType = document.getElementById('sortSelect') ? document.getElementById('sortSelect').value : 'newest';
        // 필터나 정렬 변경 시 offset과 allLoaded 초기화 후 새로 로드
        offset = 0;
        allLoaded = false;
        loadReviews(shopId, selectedKeywords, sortType, offset, limit, true);
    });
});

// 정렬 드롭다운 이벤트 처리
const sortSelect = document.getElementById('sortSelect');
if (sortSelect) {
    sortSelect.addEventListener('change', function () {
        const shopId = getShopIdFromURL();
        const selectedKeywords = getCurrentKeyword();
        sortType = this.value;
        offset = 0;
        allLoaded = false;
        loadReviews(shopId, selectedKeywords, sortType, offset, limit, true);
    });
}

// 스크롤 이벤트: 페이지 하단 근처에 도달하면 추가 데이터 로드 (무한 스크롤)
window.addEventListener('scroll', function() {
    const threshold = 100; // 바닥에서 100px 이내로 오면
    if (window.pageYOffset + window.innerHeight >= document.body.offsetHeight - threshold) {
        const shopId = getShopIdFromURL();
        const selectedKeywords = getCurrentKeyword();
        const currentSortType = document.getElementById('sortSelect') ? document.getElementById('sortSelect').value : 'newest';
        loadReviews(shopId, selectedKeywords, currentSortType, offset, limit);
    }
});

// 페이지 로드 시 초기 데이터 로드
document.addEventListener("DOMContentLoaded", function () {
    const shopId = getShopIdFromURL();
    if (!shopId) {
        console.error("shopId를 찾을 수 없습니다.");
        return;
    }
    // 초기 데이터 로드 (새로고침 모드)
    offset = 0;
    allLoaded = false;
    loadReviewCount(shopId);
    loadReviews(shopId, "", document.getElementById("sortSelect").value, offset, limit, true);
});
