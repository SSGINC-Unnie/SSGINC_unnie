<!-- 자바스크립트: Fetch API를 통한 비동기 요청 -->
    // 1) OCR 업로드 폼 전송
    document.getElementById("ocrForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const fileInput = document.getElementById("file");

    if (!fileInput.files.length) {
    alert("파일을 선택해주세요!");
    return;
}

    const formData = new FormData();
    formData.append("file", fileInput.files[0]);

    try {
    // /api/ocr/upload 엔드포인트로 POST 요청
    const response = await fetch("/api/ocr/upload", {
    method: "POST",
    body: formData
});

    if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
}

    // JSON 파싱
    const result = await response.json();
    document.getElementById("ocrResult").textContent = JSON.stringify(result, null, 2);

    // OCR 결과(JSON 중 data 필드)가 ReceiptRequest 형태라 가정
    // 이 데이터를 DB 저장 폼의 textarea에 미리 넣어줄 수 있음
    if (result.data) {
    document.getElementById("receiptData").value = JSON.stringify(result.data, null, 2);
}
} catch (error) {
    document.getElementById("ocrResult").textContent = `오류 발생: ${error}`;
}
});

    // 2) 영수증 DB 저장 폼 전송
    document.getElementById("saveReceiptForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const receiptData = document.getElementById("receiptData").value;

    try {
    // /api/receipt/save 엔드포인트로 POST 요청
    const response = await fetch("/api/receipt/save", {
    method: "POST",
    headers: {
    "Content-Type": "application/json"
},
    body: receiptData // JSON 문자열
});

    if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
}

    const result = await response.json();
    document.getElementById("saveReceiptResult").textContent = JSON.stringify(result, null, 2);
} catch (error) {
    document.getElementById("saveReceiptResult").textContent = `오류 발생: ${error}`;
}
});

    // 3) 영수증 조회 폼 전송
    document.getElementById("getReceiptForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const receiptId = document.getElementById("receiptId").value;

    try {
    // /api/receipt/{receiptId} 엔드포인트로 GET 요청
    const response = await fetch(`/api/receipt/${receiptId}`, {
    method: "GET"
});

    if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
}

    const result = await response.json();
    document.getElementById("getReceiptResult").textContent = JSON.stringify(result, null, 2);
} catch (error) {
    document.getElementById("getReceiptResult").textContent = `오류 발생: ${error}`;
}
});