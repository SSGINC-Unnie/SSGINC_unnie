/**
 * 1) 카카오 주소 API 실행 함수
 */
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            document.getElementById('shopLocation').value = data.address;
        }
    }).open();
}

/**
 * 2) "다음" 버튼 클릭 시 -> /api/mypage/shop 엔드포인트로 JSON POST 후,
 * POST 성공 시 /api/mypage/designer 페이지로 이동
 */
document.getElementById('submitBtn').addEventListener('click', function() {
    // 폼 유효성 체크(간단 예시)
    const form = document.getElementById('shopForm');
    if (!form.checkValidity()) {
        alert('필수 항목을 입력해 주세요.');
        return;
    }

    // 폼 값 수집
    const shopBusinessNumber = document.getElementById('shopBusinessNumber').value.trim();
    const shopRepresentationName = document.getElementById('shopRepresentationName').value.trim();
    const shopCreatedAt = document.getElementById('shopCreatedAt').value;
    const shopLocation = document.getElementById('shopLocation').value.trim();
    const shopLocationDetail = document.getElementById('shopLocationDetail').value.trim();
    const shopName = document.getElementById('shopName').value.trim();
    const shopCategory = document.querySelector('input[name="shopCategory"]:checked').value;
    const startTime = document.querySelector('input[name="shopBusinessStartTime"]').value;
    const endTime = document.querySelector('input[name="shopBusinessEndTime"]').value;
    const shopTel = document.getElementById('shopTel').value.trim();
    const shopClosedDay = document.querySelector('input[name="shopClosedDay"]:checked').value;
    const shopIntroduction = document.getElementById('shopIntroduction').value.trim();

    // 예: 영업시간은 startTime과 endTime을 문자열로 결합 (필요 시 수정)
    const shopBusinessTime = `${startTime}-${endTime}`;

    // 주소와 상세주소 결합
    const fullLocation = shopLocationDetail ? shopLocation + ' ' + shopLocationDetail : shopLocation;

    // ShopInsertRequest 형태(JSON)
    const requestBody = {
        shopName: shopName,
        shopLocation: fullLocation,
        shopCategory: shopCategory,
        shopBusinessTime: shopBusinessTime,
        shopTel: shopTel,
        shopIntroduction: shopIntroduction,
        shopClosedDay: shopClosedDay,
        shopRepresentationName: shopRepresentationName,
        shopBusinessNumber: shopBusinessNumber,
        shopCreatedAt: shopCreatedAt
    };

    // 서버로 JSON POST
    fetch('/api/mypage/shop', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(requestBody)
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.message || '업체 등록 실패');
                });
            }
            return response.json();
        })
        .then(data => {
            alert(data.message || '업체 등록이 완료되었습니다.' + data.data.shopId);
            console.log('등록된 shopId', data.data.shopId);
            let shopId = data.data.shopId;
            window.location.href = `/mypage/designer/${shopId}`;
        })
        .catch(err => {
            alert(err.message);
        });
});
