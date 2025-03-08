// 전역 변수에 OCR 결과(ReceiptRequest 전체)를 임시 저장
let currentReceiptData = {};

// 1) OCR 업로드 폼 전송
document.getElementById("ocrForm").addEventListener("submit", async (e) => {
    e.preventDefault();

    const fileInput = document.getElementById("ocrFile");
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
            // credentials: "include", // JWT 쿠키가 필요하면 주석 해제
            body: formData
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // JSON 파싱
        const result = await response.json();

        // OCR 결과(ResponseDto)에서 data 필드를 ReceiptRequest 형태라 가정
        if (result.data) {
            // OCR 결과 전체를 전역 변수에 저장
            currentReceiptData = result.data;

            // 화면에 필요한 필드만 추출해서 입력 필드에 세팅
            document.getElementById("shopName").value = result.data.receiptShopName || "";
            document.getElementById("totalAmount").value = result.data.receiptAmount || "";
            // receiptDate가 문자열 형식이어야 하므로 필요시 포맷 변경 (여기서는 그대로 사용)
            document.getElementById("paymentDate").value = result.data.receiptDate || "";

            // 수정 영역 보이기
            document.getElementById("checkOcrSection").style.display = "block";
        } else {
            alert("OCR 분석 결과에 데이터가 없습니다.");
        }
    } catch (error) {
        console.error("OCR 업로드 실패:", error);
        alert(`오류 발생: ${error}`);
    }
});

// 2) 영수증 저장 버튼 클릭 이벤트
document.getElementById("saveReceipt").addEventListener("click", async () => {
    // 수정된 값 가져오기
    const updatedShopName = document.getElementById("shopName").value;
    const updatedTotalAmount = document.getElementById("totalAmount").value;
    const updatedPaymentDate = document.getElementById("paymentDate").value;

    // 기존 OCR 데이터와 수정된 데이터를 병합
    const finalReceiptData = {
        ...currentReceiptData,
        receiptShopName: updatedShopName,
        receiptAmount: updatedTotalAmount,
        receiptDate: updatedPaymentDate
    };

    try {
        // /api/receipt/save 엔드포인트로 POST 요청 (JSON 전송)
        const response = await fetch("/api/receipt/save", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(finalReceiptData)
            // credentials: "include", // JWT 쿠키가 필요하면 주석 해제
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const saveResult = await response.json();
        alert("영수증 인증이 완료되었습니다!");
        console.log("영수증 저장 결과:", saveResult);
        // saveResult.data.receiptId가 저장된 영수증 ID라고 가정
        const receiptId = saveResult.data.receiptId;

        window.location.href = `/review/create?receiptId=${receiptId}`;
        // 영수증 저장 후 리뷰 작성 폼 페이지로 이동
    } catch (error) {
        console.error("영수증 저장 실패:", error);
        alert(`오류 발생: ${error}`);
    }
});