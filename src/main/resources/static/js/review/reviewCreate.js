document.addEventListener("DOMContentLoaded", function () {
    // URL 쿼리에서 receiptId 추출 후 hidden input에 설정
    const params = new URLSearchParams(window.location.search);
    const receiptId = params.get("receiptId");
    if (receiptId) {
        document.getElementById("reviewReceiptId").value = receiptId;
    }

    // 별점 클릭 기능 구현
    const stars = document.querySelectorAll("#starRating .star");
    const reviewRateInput = document.getElementById("reviewRate");
    stars.forEach(star => {
        star.addEventListener("click", function () {
            const rating = this.getAttribute("data-value");
            reviewRateInput.value = rating;
            // 업데이트: 클릭한 별 이하의 별들에 "selected" 클래스 추가
            stars.forEach(s => {
                s.classList.toggle("selected", s.getAttribute("data-value") <= rating);
            });
        });
    });

    // 리뷰 제출 이벤트
    const submitButton = document.getElementById("submitReview");
    submitButton.addEventListener("click", function () {
        const reviewReceiptId = document.getElementById("reviewReceiptId").value;
        const file = document.getElementById("file").files[0];
        const reviewRate = reviewRateInput.value;
        const reviewContent = document.getElementById("reviewContent").value;

        // 키워드: 선택된 체크박스 값을 콤마 구분 문자열로 조합
        const keywordCheckboxes = document.querySelectorAll('input[name="keyword"]:checked');
        const keywords = Array.from(keywordCheckboxes).map(cb => cb.value).join(",");

        if (!reviewReceiptId || !reviewRate || !reviewContent || !keywords) {
            alert("필수 입력 항목을 모두 채워주세요.");
            return;
        }

        let formData = new FormData();
        formData.append("reviewReceiptId", reviewReceiptId);
        formData.append("reviewRate", reviewRate);
        formData.append("reviewContent", reviewContent);
        formData.append("keywordId", keywords);
        if (file) {
            formData.append("file", file);
        }

        fetch("/api/review", {
            method: "POST",
            credentials: "include",
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 201) {
                    alert("리뷰 작성이 완료되었습니다!");
                    window.location.href = "/review/my"
                } else {
                    alert("리뷰 작성 실패: " + data.message);
                }
            })
            .catch(error => {
                console.error("Error:", error);
                alert("서버 오류가 발생했습니다.");
            });
    });
});