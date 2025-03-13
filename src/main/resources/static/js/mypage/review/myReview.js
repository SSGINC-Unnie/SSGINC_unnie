// ======================================
// 리뷰 개수 로드 함수
// ======================================
function loadReviewCount() {
    fetch("/api/mypage/review/count")
        .then(res => res.json())
        .then(data => {
            const count = data.data.reviewCount;
            document.querySelector('.review-count').textContent = "리뷰 " + count;
        })
        .catch(err => console.error("Review count error:", err));
}

// ======================================
// 키워드 ID → 라벨 매핑 객체
// ======================================
const keywordMap = {
    "1": "친절함",
    "2": "깔끔함",
    "3": "만족도 높음",
    "4": "가격이 합리적",
    "5": "재방문 의사"
};

// 키워드 문자열("1,2") → 라벨 배열(["친절함","깔끔함"])로 변환
function getKeywordLabels(keywordString) {
    if (!keywordString) return [];
    return keywordString.split(',').map(id => {
        const trimmed = id.trim();
        return keywordMap[trimmed] || trimmed;
    });
}

// ======================================
// DOMContentLoaded 이벤트
// ======================================
document.addEventListener("DOMContentLoaded", function() {
    // 1) 리뷰 개수 로드
    loadReviewCount();

    // 2) 리뷰 목록 무한 스크롤 관련 초기 변수
    let offset = 0;
    const limit = 10;
    let sortType = document.getElementById('sortSelect') ? document.getElementById('sortSelect').value : "newest";
    let isLoading = false;
    let allLoaded = false;

    // 날짜 포맷 함수 (yyyy-mm-dd)
    function formatDate(dateStr) {
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = ("0" + (date.getMonth() + 1)).slice(-2);
        const day = ("0" + date.getDate()).slice(-2);
        return `${year}-${month}-${day}`;
    }

    // 별점(보기용) HTML 생성 함수
    function getStarRatingHTML(rating) {
        let html = '';
        for (let i = 1; i <= 5; i++) {
            html += `<span class="star ${i <= rating ? 'full' : ''}">★</span>`;
        }
        return html;
    }

    // 편집용 별점 설정 함수 (인라인 편집 모드)
    function setEditStarRating(reviewId, rating) {
        const starElems = document.querySelectorAll(`#editStarRating-${reviewId} .star`);
        starElems.forEach(star => {
            const starValue = star.getAttribute("data-value");
            star.classList.toggle("full", starValue <= rating);
        });
        // 현재 별점 값을 data-rating 속성에 저장
        document.querySelector(`#editStarRating-${reviewId}`).setAttribute("data-rating", rating);
    }

    // 편집용 별점 초기화 및 클릭 이벤트 등록
    function initEditStarRating(reviewId, currentRating) {
        const editStars = document.querySelectorAll(`#editStarRating-${reviewId} .star`);
        setEditStarRating(reviewId, currentRating);
        editStars.forEach(star => {
            star.addEventListener("click", function() {
                const rating = this.getAttribute("data-value");
                setEditStarRating(reviewId, rating);
            });
        });
    }

    // 인라인 편집 키워드 체크박스 초기화 (기존 키워드에 맞게 체크)
    function initEditKeyword(reviewId, keywordString) {
        if (!keywordString) return;
        const keywords = keywordString.split(',').map(kw => kw.trim());
        keywords.forEach(kw => {
            const checkbox = document.getElementById(`keyword${kw}-${reviewId}`);
            if (checkbox) {
                checkbox.checked = true;
            }
        });
    }

    // 리뷰 데이터를 받아와 review-list에 추가하는 함수
    function appendReviews(reviews) {
        const reviewList = document.querySelector('.review-list');
        reviews.forEach(review => {
            // 변환된 키워드 라벨을 HTML로 생성
            const keywordLabels = getKeywordLabels(review.reviewKeyword);
            const keywordsHTML = keywordLabels
                .map(label => `<span class="keyword-pill">${label}</span>`)
                .join('');

            // 동적으로 review-item 생성
            const reviewItem = document.createElement('div');
            reviewItem.className = 'review-item';
            reviewItem.innerHTML = `
                <!-- 상단 영역: 업체명, 날짜 -->
                <div class="review-meta-top">
                    <span class="review-shopname">${review.shopName}</span>
                    <span class="review-date">${formatDate(review.reviewDate)}</span>
                </div>
                <!-- 작성자 -->
                <div class="review-author">
                    <span class="reviewer">${review.memberNickName}</span>
                </div>
                <!-- 별점 (보기 모드) -->
                <div class="star-rating view-rating">
                    ${getStarRatingHTML(review.reviewRate)}
                </div>
                <!-- 리뷰 이미지 -->
                <div class="review-image-section">
                    ${review.reviewImage ? `<img src="${review.reviewImage}" alt="리뷰 이미지">` : `<img src="/img/review/icon.png" alt="기본 이미지">`}
                </div>
                <!-- 리뷰 내용 (보기 모드) -->
                <div class="review-content view-content">${review.reviewContent}</div>
                <!-- 하단 영역: 리뷰 키워드 및 수정/삭제 버튼 -->
                <div class="review-bottom">
                    <div class="review-keyword-list view-keywords">
                        ${keywordsHTML}
                    </div>
                    <div class="review-actions">
                        <button class="action-button">⋮</button>
                        <div class="action-menu">
                            <button type="button" class="edit-review-btn" data-review-id="${review.reviewId}">리뷰 수정</button>
                            <button type="button" class="delete-review-btn" data-review-id="${review.reviewId}">리뷰 삭제</button>
                        </div>
                    </div>
                </div>
                <!-- 인라인 편집 영역 (초기 숨김) -->
                <div class="inline-edit" id="editArea-${review.reviewId}" style="display: none;">
                    <!-- 편집용 별점 영역 -->
                    <div class="star-rating edit-rating" id="editStarRating-${review.reviewId}" data-rating="${review.reviewRate}">
                        <span class="star" data-value="1">&#9733;</span>
                        <span class="star" data-value="2">&#9733;</span>
                        <span class="star" data-value="3">&#9733;</span>
                        <span class="star" data-value="4">&#9733;</span>
                        <span class="star" data-value="5">&#9733;</span>
                    </div>
                    <!-- 수정용 리뷰 내용 -->
                    <textarea class="edit-content" rows="4">${review.reviewContent}</textarea>
                    <!-- 수정용 키워드 선택 영역 -->
                    <div class="edit-keyword">
                        <div class="keyword-group">
                            <input type="checkbox" id="keyword1-${review.reviewId}" name="keyword" value="1">
                            <label for="keyword1-${review.reviewId}">친절함</label>
                            <input type="checkbox" id="keyword2-${review.reviewId}" name="keyword" value="2">
                            <label for="keyword2-${review.reviewId}">깔끔함</label>
                            <input type="checkbox" id="keyword3-${review.reviewId}" name="keyword" value="3">
                            <label for="keyword3-${review.reviewId}">만족도 높음</label>
                            <input type="checkbox" id="keyword4-${review.reviewId}" name="keyword" value="4">
                            <label for="keyword4-${review.reviewId}">가격이 합리적</label>
                            <input type="checkbox" id="keyword5-${review.reviewId}" name="keyword" value="5">
                            <label for="keyword5-${review.reviewId}">재방문 의사</label>
                        </div>
                    </div>
                    <div class="edit-buttons">
                        <button class="save-edit-btn" data-review-id="${review.reviewId}">수정 완료</button>
                        <button class="cancel-edit-btn" data-review-id="${review.reviewId}">취소</button>
                    </div>
                </div>
            `;
            reviewList.appendChild(reviewItem);
            // 인라인 편집 시 별점 및 키워드 초기화
            initEditStarRating(review.reviewId, review.reviewRate);
            initEditKeyword(review.reviewId, review.reviewKeyword);
        });
    }

    // ======================================
    // 리뷰 목록 불러오기 (무한 스크롤)
    // ======================================
    function loadReviews() {
        if (isLoading || allLoaded) return;
        isLoading = true;
        fetch(`/api/mypage/review/reviews?sortType=${sortType}&offset=${offset}&limit=${limit}`)
            .then(response => response.json())
            .then(data => {
                const reviews = data.data.reviews;
                if (!reviews || reviews.length === 0) {
                    allLoaded = true;
                } else {
                    appendReviews(reviews);
                    offset += reviews.length;
                }
                isLoading = false;
            })
            .catch(error => {
                console.error('리뷰 로드 에러:', error);
                isLoading = false;
            });
    }

    // ======================================
    // 무한 스크롤 이벤트
    // ======================================
    window.addEventListener('scroll', function() {
        const threshold = 100;
        if (window.pageYOffset + window.innerHeight >= document.body.offsetHeight - threshold) {
            loadReviews();
        }
    });

    // ======================================
    // 정렬 변경 시 새로 로드
    // ======================================
    const sortSelect = document.getElementById('sortSelect');
    if (sortSelect) {
        sortSelect.addEventListener('change', function() {
            sortType = this.value;
            document.querySelector('.review-list').innerHTML = "";
            offset = 0;
            allLoaded = false;
            loadReviews();
        });
    }

    // ======================================
    // ⋮ 버튼 클릭 시 수정/삭제 메뉴 토글
    // ======================================
    document.addEventListener("click", function(e) {
        const actionBtn = e.target.closest('.action-button');
        if (actionBtn) {
            const actionContainer = actionBtn.closest('.review-actions');
            if (actionContainer) {
                actionContainer.classList.toggle('show');
            }
        } else {
            document.querySelectorAll('.review-actions.show').forEach(container => {
                container.classList.remove('show');
            });
        }
    });

    // ======================================
    // 인라인 수정: 수정 버튼 클릭 시 편집 영역 토글
    // ======================================
    document.addEventListener("click", function(e) {
        if (e.target.classList.contains("edit-review-btn")) {
            const reviewId = e.target.getAttribute("data-review-id");
            const editArea = document.getElementById(`editArea-${reviewId}`);
            if (editArea) {
                editArea.style.display = (editArea.style.display === "none" ? "block" : "none");
            }
        }
    });

    // ======================================
    // 인라인 수정 완료 및 취소 처리
    // ======================================
    document.addEventListener("click", function(e) {
        // 수정 완료
        if (e.target.classList.contains("save-edit-btn")) {
            const reviewId = e.target.getAttribute("data-review-id");
            const reviewItem = e.target.closest(".review-item");
            const newContent = reviewItem.querySelector(".edit-content").value;
            const newKeywords = (function() {
                const checkboxes = reviewItem.querySelectorAll(".edit-keyword input[name='keyword']:checked");
                return Array.from(checkboxes).map(cb => cb.value).join(",");
            })();
            const newRating = document.querySelector(`#editStarRating-${reviewId}`).getAttribute("data-rating");

            // 수정 API 호출 시, 키워드 값은 쿼리 파라미터로 전송
            fetch(`/api/review/${reviewId}?keywordId=${encodeURIComponent(newKeywords)}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    reviewContent: newContent,
                    reviewRate: newRating
                })
            })
                .then(response => response.json())
                .then(data => {
                    alert("리뷰 수정 성공!");
                    // 뷰 모드 업데이트
                    reviewItem.querySelector(".review-content").textContent = newContent;
                    reviewItem.querySelector(".star-rating.view-rating").innerHTML = getStarRatingHTML(newRating);
                    const keywordListElem = reviewItem.querySelector(".review-keyword-list");
                    if (newKeywords.trim() !== "") {
                        const keywordsArray = newKeywords.split(',').map(kw => kw.trim());
                        const keywordLabels = keywordsArray.map(id => keywordMap[id] || id);
                        keywordListElem.innerHTML = keywordLabels.map(label => `<span class="keyword-pill">${label}</span>`).join('');
                    } else {
                        keywordListElem.innerHTML = "";
                    }
                    document.getElementById(`editArea-${reviewId}`).style.display = "none";
                })
                .catch(error => {
                    console.error("리뷰 수정 에러:", error);
                    alert("리뷰 수정 실패");
                });
        }
        // 취소
        if (e.target.classList.contains("cancel-edit-btn")) {
            const reviewId = e.target.getAttribute("data-review-id");
            document.getElementById(`editArea-${reviewId}`).style.display = "none";
        }
    });

    // ======================================
    // 리뷰 삭제: 모달 처리
    // ======================================
    const modal = document.getElementById("reviewModal");
    const modalBody = document.getElementById("modal-body");
    const closeModal = modal ? modal.querySelector(".close") : null;

    document.addEventListener("click", function(e) {
        if (e.target.classList.contains("delete-review-btn")) {
            const reviewId = e.target.getAttribute("data-review-id");
            modalBody.innerHTML = `
                <h3>리뷰 삭제</h3>
                <p>정말 이 리뷰를 삭제하시겠습니까?</p>
                <button id="confirmDeleteBtn">삭제</button>
                <button id="cancelDeleteBtn">취소</button>
            `;
            modal.style.display = "block";

            document.getElementById("confirmDeleteBtn").addEventListener("click", function() {
                fetch(`/api/review/${reviewId}`, {
                    method: "PATCH"
                })
                    .then(response => response.json())
                    .then(data => {
                        alert("리뷰 삭제 성공!");
                        modal.style.display = "none";
                        location.reload();
                    })
                    .catch(error => {
                        console.error("리뷰 삭제 에러:", error);
                        alert("리뷰 삭제 실패");
                    });
            });
            document.getElementById("cancelDeleteBtn").addEventListener("click", function() {
                modal.style.display = "none";
            });
        }
    });

    if (closeModal) {
        closeModal.addEventListener("click", function() {
            modal.style.display = "none";
        });
    }

    // ======================================
    // 초기 리뷰 데이터 로드
    // ======================================
    loadReviews();
});
