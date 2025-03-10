// URL의 쿼리 파라미터에서 shopId 추출
function getShopIdFromURL() {
    const params = new URLSearchParams(window.location.search);
    const shopId = params.get('shopId');
    if (!shopId) {
        console.log("shopId를 찾을 수 없습니다.");
    }
    return shopId;
}

// 페이지 로드 시 초기 데이터 로드
document.addEventListener("DOMContentLoaded", function() {
    const shopId = getShopIdFromURL();
    if (!shopId) {
        console.error("shopId를 찾을 수 없습니다.");
        return;
    }

    // 초기 데이터 로드
    loadReviewCount(shopId);
    loadReviews(shopId, "", document.getElementById("sortSelect").value, 0, 10);

    // 정렬 드롭다운 이벤트
    const sortSelect = document.getElementById('sortSelect');
    sortSelect.addEventListener('change', function () {
        loadReviews(shopId, getCurrentKeyword(), this.value, 0, 10);
    });
});

// 현재 선택된 키워드를 반환
function getCurrentKeyword() {
    const activeKeywordButton = document.querySelector('.keyword-filters button.active');
    return activeKeywordButton ? activeKeywordButton.textContent : "";
}

// 리뷰 개수 로드
function loadReviewCount(shopId) {
    fetch(`/api/review/shop/${shopId}/count`)
        .then(response => response.json())
        .then(data => {
            // 응답: { status: 200, message: "...", data: { totalReviewCount: 19 } }
            const count = data.data.totalReviewCount;
            const countElement = document.querySelector('.review-count span');
            if(countElement) {
                countElement.textContent = "리뷰 " + count;
            }
        })
        .catch(error => console.error('Error fetching review count:', error));
}

// 리뷰 목록 로드
function loadReviews(shopId, keyword, sortType, offset, limit) {
    const url = `/api/review/shop/${shopId}?keyword=${encodeURIComponent(keyword)}&sortType=${sortType}&offset=${offset}&limit=${limit}`;
    fetch(url)
        .then(response => response.json())
        .then(data => {
            // 응답: { status: 200, message: "...", data: { reviews: [...] } }
            const reviews = data.data.reviews;
            const reviewListContainer = document.querySelector('.review-list');
            if (!reviewListContainer) return;

            // 기존 리뷰 초기화
            reviewListContainer.innerHTML = "";

            // 각 리뷰를 동적으로 생성
            reviews.forEach(review => {
                const reviewItem = document.createElement('div');
                reviewItem.className = "review-item";

                // 작성일 변환
                const formattedDate = formatDate(review.reviewDate);

                reviewItem.innerHTML = `
                    <div class="review-item-header">
                        <span class="review-author">${review.memberNickName}</span>
                        <span class="review-date">${formattedDate}</span>
                        <span class="review-rate">${review.reviewRate}</span>
                    </div>
                    <div class="review-shopname">${review.shopName}</div>
                    <div class="review-image-section">
                        <img src="${review.reviewImage}" alt="리뷰 이미지">
                    </div>
                    <div class="review-content">${review.reviewContent}</div>
                `;
                reviewListContainer.appendChild(reviewItem);
            });
        })
        .catch(error => console.error('Error fetching reviews:', error));
}

// 날짜 포맷 함수
function formatDate(dateString) {
    if (!dateString) return "";
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = ("0" + (date.getMonth() + 1)).slice(-2);
    const day = ("0" + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
}

// 키워드 버튼 클릭
function onKeywordClick(button) {
    document.querySelectorAll('.keyword-filters button').forEach(btn => btn.classList.remove('active'));
    button.classList.add('active');

    const shopId = getShopIdFromURL();
    const selectedKeyword = button.textContent;
    const sortType = document.getElementById('sortSelect').value;
    loadReviews(shopId, selectedKeyword, sortType, 0, 10);
}
