document.addEventListener("DOMContentLoaded", function () {
    const reviewForm = document.getElementById("reviewForm");
    const submitButton = document.getElementById("submitReview");

submitButton.addEventListener("click", function () {
    const reviewReceiptId = document.getElementById("reviewReceiptId").value;
    const file = document.getElementById("file").files[0];
    const reviewRate = document.getElementById("reviewRate").value;
    const reviewContent = document.getElementById("reviewContent").value;
    const keywordId = document.getElementById("keywordId").value; // 콤마로 구분된 키워드 ID

    if (!reviewReceiptId || !file || !reviewRate || !reviewContent || !keywordId) {
        alert("필수 입력 항목을 모두 채워주세요.");
        return;
    }

    let formData = new FormData();
    // formData.append("review_receipt_id", reviewReceiptId);
    formData.append("reviewReceiptId", reviewReceiptId);
    // formData.append("review_rate", reviewRate);
    formData.append("reviewRate", reviewRate);
    // formData.append("review_content", reviewContent);
    formData.append("reviewContent", reviewContent);
    formData.append("keywordId", keywordId);
    formData.forEach((value, key) => {
        console.log(`${key}:`, value);
    });
    if (file) {
        formData.append("file", file);
    }
    // 추가한 부분
    console.log(keywordId)

    fetch("/api/review",{
        method: "POST",
        // 쿠키 전송을 허용 (same-origin or include)
        credentials: "include",
        body: formData
    })
        .then(response => response.json())
        .then(data => {
            if (data.status === 201) {
                alert("리뷰 작성이 완료되었습니다!");
                location.reload();
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
