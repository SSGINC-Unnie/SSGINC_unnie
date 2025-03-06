document.addEventListener("DOMContentLoaded", function () {
    const reviewForm = document.getElementById("reviewForm");
    const submitButton = document.getElementById("submitReview");

submitButton.addEventListener("click", function () {
    const reviewReceiptId = document.getElementById("reviewReceiptId").value;
    const file = document.getElementById("file").files[0];
    const reviewRate = document.getElementById("reviewRate").value;
    const reviewContent = document.getElementById("reviewContent").value;
    // const keywordIds = document.getElementById("keywordIds").value; // 콤마로 구분된 키워드 ID
    const keywords = document.getElementById("keywordIds").value; // 콤마로 구분된 키워드 ID

    if (!reviewReceiptId || !file || !reviewRate || !reviewContent || !keywords) {
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
    if (file) {
        formData.append("file", file);
    }
    // 추가한 부분
    formData.append("keywords", keywords);

    formData.forEach((value, key) => {
        console.log(`${key}:`, value);
    });
    // console.log(keywordIds)
    // console.log(typeof keywordIds)

    fetch("http://localhost:8111/api/review", {
        method: "POST",
        // headers: {
        //     // "Authorization": "Bearer " + localStorage.getItem("accessToken") // ✅ Content-Type 자동 처리
        // },
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
    // const token = localStorage.getItem("accessToken");
    // if (!token) {
    //     alert("로그인이 필요합니다.");
    // }
    //
    // console.log("Sending Authorization header:", localStorage.getItem("accessToken"));
});
});
