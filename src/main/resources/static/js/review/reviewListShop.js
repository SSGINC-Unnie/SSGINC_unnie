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
 */
function loadReviews(shopId, keyword, sortType, offset, limit) {
    const url = `/api/review/shop/${shopId}?keyword=${encodeURIComponent(keyword)}&sortType=${sortType}&offset=${offset}&limit=${limit}`;
    fetch(url)
        .then(response => response.json())
        .then(data => {
            const reviews = data.data.reviews;
            const reviewListContainer = document.querySelector('.review-list');
            if (!reviewListContainer) return;

            // 기존 리뷰 초기화
            reviewListContainer.innerHTML = "";

            // 각 리뷰에 대해 HTML 생성
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
          <span class="review-author">${review.memberNickName}</span>
          </div>
          <div class="star-rating">
              ${starRatingHTML}
          </div>
      </div>
      <div class="review-image-section">
          <img src="${review.reviewImage ? review.reviewImage : '/img/review/icon.png'}" alt="리뷰 이미지">
      </div>
      <div class="review-content">${review.reviewContent}</div>
      ${keywordHTML ? `<div class="review-keyword-list">${keywordHTML}</div>` : ''}
    `;
                reviewListContainer.appendChild(reviewItem);
            });
        })
        .catch(error => console.error('Error fetching reviews:', error));
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
        // loadReviews 함수를 호출할 때, 키워드 파라미터로 전달 (서버에서 이 값으로 필터링)
        loadReviews(shopId, selectedKeywords, sortType, 0, 10);
    });
});

/**
 * 페이지 로드 시 초기 데이터 로드
 */
document.addEventListener("DOMContentLoaded", function () {
    const shopId = getShopIdFromURL();
    if (!shopId) {
        console.error("shopId를 찾을 수 없습니다.");
        return;
    }

    // 초기 데이터 로드
    loadReviewCount(shopId);
    loadReviews(shopId, "", document.getElementById("sortSelect").value, 0, 10);

    // 정렬 드롭다운 이벤트 처리
    const sortSelect = document.getElementById('sortSelect');
    sortSelect.addEventListener('change', function () {
        loadReviews(shopId, getCurrentKeyword(), this.value, 0, 10);
    });
});
