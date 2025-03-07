document.addEventListener("DOMContentLoaded", function() {
    const loginButton = document.querySelector('.login-button');

    loginButton.addEventListener('click', function() {
        // 아이디, 비밀번호 입력값 가져오기
        const email = document.querySelector('.login-input[type="text"]').value;
        const password = document.querySelector('.login-input[type="password"]').value;

        // JSON 형식으로 데이터 생성
        const payload = {
            memberEmail: email,
            memberPw: password
        };

        // axios를 사용하여 POST 요청 보내기
        axios.post('/api/member/login', payload)
            .then(response => {
                // 로그인 성공 시 처리
                console.log("로그인 성공:", response.data);
                alert("로그인 성공!");
                window.location.href = '/'; // 로그인 성공 후 홈으로 이동
            })
            .catch(error => {
                // 로그인 실패 시 처리
                console.error("로그인 실패:", error);
                alert("아이디와 비밀번호를 확인해주세요.");
            });
    });
    // 네이버 소셜 로그인 초기화 및 버튼 이벤트 처리
    var naverLogin = new naver_id_login("cVXzKiI7vI8yCxFy2axQ", "http://localhost:8111/callback.html");
    var state = naverLogin.getUniqState();
    naverLogin.setState(state);
    naverLogin.setDomain("http://localhost:8111");
    naverLogin.init_naver_id_login();

    // 커스텀 네이버 로그인 버튼에 클릭 이벤트 연결
    document.getElementById("naver-login").addEventListener("click", function() {
        var naverAnchor = document.querySelector("#naver_id_login a");
        if(naverAnchor) {
            naverAnchor.click();
        } else {
            console.error("네이버 로그인 버튼 요소를 찾을 수 없습니다.");
        }
    });
});
