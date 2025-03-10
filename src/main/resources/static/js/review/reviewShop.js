document.addEventListener("DOMContentLoaded", function() {
    // shopId를 페이지에서 추출 (예: 숨겨진 요소에 data-shop-id 속성이 있다고 가정)
    const shopId = getShopId();
    if (!shopId) {
        console.error("shopId를 찾을 수 없습니다.");
        return;
    }

    // 초기 데이터 로드
    loadReviewCount(shopId);
    loadReviews(shopId, "", document.getElementById("sortSelect").value, 0, 10);

    // 정렬 드롭다운 이벤트: 정렬 변경 시 리뷰 목록 재조회
    const sortSelect = document.getElementById('sortSelect');
    sortSelect.addEventListener('change', function() {
        loadReviews(shopId, getCurrentKeyword(), this.value, 0, 10);
    });
});

// shopId를 HTML에서 추출하는 함수 (예: id="shopId"인 요소의 data-shop-id 속성)
function getShopId() {
    const shopIdElement = document.getElementById('shopId');
    return shopIdElement ? shopIdElement.getAttribute('data-shop-id') : null;
}

// 현재 선택된 키워드를 반환하는 함수 (예: 현재 활성화된 키워드 버튼의 값)
// 여기서는 간단히, 페이지 상에 선택된 키워드가 따로 관리된다면 그 값을 반환하도록 구현
function getCurrentKeyword() {
    const activeKeywordButton = document.querySelector('.keyword-filters button.active');
    return activeKeywordButton ? activeKeywordButton.textContent : "";
}

// 리뷰 개수 로드: REST API 호출 후, 리뷰 개수를 화면에 표시
function loadReviewCount(shopId) {
    fetch(`/api/review/shop/${shopId}/count`)
        .then(response => response.json())
        .then(data => {
            // 응답 예시: { status: 200, message: "업체 리뷰 개수 조회 성공", data: { totalReviewCount: 19 } }
            const count = data.data.totalReviewCount;
            const countElement = document.querySelector('.review-count span');
            if(countElement) {
                countElement.textContent = "리뷰 " + count;
            }
        })
        .catch(error => console.error('Error fetching review count:', error));
}

// 리뷰 목록 로드: REST API 호출 후, 리뷰 목록을 HTML에 동적으로 생성
function loadReviews(shopId, keyword, sortType, offset, limit) {
    // REST API URL 구성 (keyword는 URL 인코딩)
    const url = `/api/review/shop/${shopId}?keyword=${encodeURIComponent(keyword)}&sortType=${sortType}&offset=${offset}&limit=${limit}`;
    fetch(url)
        .then(response => response.json())
        .then(data => {
            // 응답 예시: { status: 200, message: "업체 리뷰 목록 조회 성공", data: { reviews: [ ... ] } }
            const reviews = data.data.reviews;
            const reviewListContainer = document.querySelector('.review-list');
            if (!reviewListContainer) return;
            reviewListContainer.innerHTML = ""; // 기존 리뷰 삭제

            reviews.forEach(review => {
                const reviewItem = document.createElement('div');
                reviewItem.className = "review-item";
                reviewItem.innerHTML = `
                <div class="review-item-header">
                    <span class="review-author">${review.memberNickName}</span>
                    <span class="review-date">${formatDate(review.reviewDate)}</span>
                </div>
                <div class="review-image-section">
                    <img src="${review.reviewImage}" alt="리뷰 이미지">
                </div>
                <div class="review-content">${review.reviewContent}</div>
                <div class="review-keywords">${review.reviewKeywords}</div>
            `;
                reviewListContainer.appendChild(reviewItem);
            });
        })
        .catch(error => console.error('Error fetching reviews:', error));
}

// 날짜를 'yyyy-MM-dd' 형식으로 변환하는 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = ("0" + (date.getMonth() + 1)).slice(-2);
    const day = ("0" + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
}

// 키워드 버튼 클릭 이벤트 핸들러: 클릭된 키워드를 활성화 처리 후 리뷰 재조회
function onKeywordClick(button) {
    // 활성화된 키워드 버튼 처리: 기존 active 클래스 제거 후 현재 버튼에 추가
    document.querySelectorAll('.keyword-filters button').forEach(btn => btn.classList.remove('active'));
    button.classList.add('active');

    // shopId와 현재 선택된 키워드로 리뷰 재조회
    const shopId = getShopId();
    const selectedKeyword = button.textContent;
    const sortType = document.getElementById('sortSelect').value;
    loadReviews(shopId, selectedKeyword, sortType, 0, 10);
}
