//유효성 검증
let isValid = {
    email: false,
    pw: false,
    confirmPw: false,
    name: false,
    nickname: false,
    phone: false
};

const phoneRegex = /^01[0-9]\d{7,8}$/;

// ------------------------------------
// 유틸리티 함수
// ------------------------------------

// 응답 처리: 응답 상태가 200이면 성공 메시지를 표시
function handleResponse(response, element) {
    if (response.status === 200) {
        showMsg(element, "success", response.data.message);
    }
}

// 오류 처리: 서버 응답에 따른 에러 메시지를 표시
function handleError(error, element) {
    if (error.response) {
        showMsg(element, "error", error.response.data.message);
    } else {
        alert('서버와의 통신 중 문제가 발생했습니다.');
    }
}

// 메시지 표시: success이면 초록색, 아니면 빨간색으로 표시
function showMsg(element, type, message) {
    element.html(`<p>${message}</p>`).css('color', type === "success" ? 'green' : 'red');
}


// ------------------------------------
// 인증 타이머 클래스 (AuthTimer)
// ------------------------------------
// 인증번호 전송 후 남은 유효시간(3분)을 표시,
// 시간이 초과되면 지정된 콜백 호출
class AuthTimer {
    constructor(duration, $timerElement, onExpire) {
        this.duration = duration;
        this.$timerElement = $timerElement;
        this.timerId = null;
        this.onExpire = onExpire;
    }

    //타이머 시작
    start() {
        let timeLeft = this.duration;
        //남은 시간 포맷해서 timerElement에 표시
        this.$timerElement.text(this.formatTime(timeLeft));
        this.$timerElement.show(); // 타이머 영역

        //1초마다 실행되는 타이머
        this.timerId = setInterval(() => {
            timeLeft--;
            // 시간이 다 떨어지면 타이머 정지 후 onExpire
            if (timeLeft <= 0) {
                clearInterval(this.timerId);
                this.onExpire();
            } else {
                this.$timerElement.text(this.formatTime(timeLeft));
            }
        }, 1000);
    }

    //타이머 정지
    stop() {
        clearInterval(this.timerId);
    }

    hide() {
        this.$timerElement.hide();
    }

    //초 단위 시간을 분:초 형식으로 변환
    formatTime(seconds) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        return `${minutes}:${remainingSeconds < 10 ? '0' : ''}${remainingSeconds}`;
    }
}


document.addEventListener('DOMContentLoaded', () => {
    // ==========================
    //  탭 전환
    // ==========================
    const tabs = document.querySelectorAll('.tab');
    const tabContents = document.querySelectorAll('.tab-content');

    tabs.forEach((tab, index) => {
        tab.addEventListener('click', () => {
            // 탭/탭컨텐츠에서 active 제거
            tabs.forEach(t => t.classList.remove('active'));
            tabContents.forEach(tc => tc.classList.remove('active'));

            // 현재 탭만 active
            tab.classList.add('active');
            // 해당 인덱스의 컨텐츠만 active
            tabContents[index].classList.add('active');
        });
    });

// ================================= 전화번호 ======================================
// ------------------------------------
// 전화번호 인증
// ------------------------------------
    class PhoneAuthentication {
        constructor() {
            this.isSending = false;
            this.authManager = new AuthTimer
            (180,
                $("#phone-auth-timer"),
                this.onTimerExpire.bind(this)
            );

            this.$phoneInput = $("#memberPhone");
            this.$phoneError = $("#phoneError");
            this.$authInput = $("#phoneAuthCode");
            this.$sendButton = $("#sendPhoneVerification");
            this.$verifyButton = $("#verifyPhoneCode");
            this.$phoneAuthSection = $("#phoneAuthSection");
            this.$phoneAuthGroup = $("#phoneAuthGroup");

            this.initializeEvents();
        }

        initializeEvents() {
            this.$sendButton.on("click", () => this.sendAuthNum());
            this.$verifyButton.on("click", () => this.verifyAuthNum());
        }

        async sendAuthNum() {
            if (this.isSending) {
                showMsg(this.$phoneError, "error", "이미 전송되었습니다.");
                return;
            }

            const phone = this.$phoneInput.val().trim();

            if (!phone) {
                showMsg(this.$phoneError, "error", "전화번호를 입력해주세요.");
                return;
            }

            if (!phoneRegex.test(phone)) {
                showMsg(this.$phoneError, "error", "전화번호 형식이 올바르지 않습니다.");
                return;
            }

            this.isSending = true;

            try {
                const response = await axios.post("/api/member/sendPhone",
                    { memberPhone: phone });
                handleResponse(response, this.$phoneError);

                // 전송 성공 시 인증번호 영역 보이기
                this.$phoneAuthGroup.show();
                // 인증번호 입력칸 보이기 & 초기화
                this.$authInput.prop("readonly", false).val("").focus();

                // 타이머 시작
                this.authManager.start();
            } catch (error) {
                handleError(error, this.$phoneError);
                this.isSending = false;
                isValid.phone = false;
            } finally {
            }
        }

        async verifyAuthNum() {
            const authCode = this.$authInput.val().trim();
            if (!authCode) {
                showMsg(this.$phoneAuthSection, "error", "인증번호를 입력해주세요.");
                return;
            }

            try {
                const response = await axios.post("/api/member/checkPhone",  {
                    memberPhone: this.$phoneInput.val(),
                    code: authCode
                });
                handleResponse(response, this.$phoneAuthSection);

                // 인증 성공
                this.authManager.stop();
                this.authManager.hide();
                this.$phoneInput.prop("readonly", true);
                this.$authInput.prop("readonly", true);
                this.$sendButton.prop("disabled", true);
                this.$verifyButton.prop("disabled", true);
                isValid.phone = true;
            } catch (error) {
                handleError(error, this.$phoneAuthSection);
                isValid.phone = false;
            }
        }

        onTimerExpire() {
            showMsg(this.$phoneError, "error", "인증 시간이 초과되었습니다.");
            this.authManager.hide();
        }
    }

    const phoneAuth = new PhoneAuthentication();

    // ==========================
    // 아이디 찾기
    // ==========================
    const btnFindId = document.getElementById('btnFindId');
    const idModal = document.getElementById('idModal');
    const idResultText = document.getElementById('idResultText');
    const btnGoLogin = document.getElementById('btnGoLogin');

    btnFindId.addEventListener('click', () => {
        const nameValue = document.getElementById('name').value.trim();
        const phoneValue = document.getElementById('memberPhone').value.trim();

        if (!isValid.phone) {
            alert('전화번호 인증이 완료되지 않았습니다.');
            return;
        }

        if (!nameValue || !phoneValue) {
            alert('이름과 전화번호를 입력해주세요.');
            return;
        }

        // 아이디 찾기 요청
        fetch('/api/member/findId', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                memberName: nameValue,
                memberPhone: phoneValue
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 200) {
                    // 아이디 찾기 성공
                    const foundEmail = data.data; // 반환한 이메일
                    // 이름 넣기
                    document.getElementById('idUserName').textContent = `${nameValue} 회원님의 아이디입니다.`;
                    // 모달창 열기
                    idModal.style.display = 'block';
                    // 모달에 이메일 표시 (마스킹 처리)
                    idResultText.textContent = maskEmail(foundEmail);
                } else {
                    // 아이디 찾기 실패
                    alert(data.message || '아이디 찾기에 실패했습니다.');
                }
            })
            .catch(err => {
                console.error('아이디 찾기 에러:', err);
                alert('서버 통신 중 오류가 발생했습니다.');
            });
    });

    // 이메일 마스킹
    function maskEmail(email) {
        const [memberId, domain] = email.split('@');
        if (memberId.length > 2) {
            return memberId.slice(0, 2) + '****@' + domain;
        } else {
            // 사용자명이 2글자 이하일 경우 처리
            return memberId + '****@' + domain;
        }
    }

    // 모달창에서 로그인 버튼 클릭 시 로그인 페이지로 이동
    btnGoLogin.addEventListener('click', () => {
        // 로그인 페이지로 이동
        idModal.style.display = 'none';
        window.location.href = '/member/login';
    });

    //모달창 닫기
    document.getElementById('closeModal').addEventListener('click', () => {
        document.getElementById('idModal').style.display = 'none';
    });


    // ==========================
    // 비밀번호 찾기
    // ==========================
    const btnFindPw = document.getElementById('btnFindPw');

    btnFindPw.addEventListener('click', () => {
        const nameValue = document.getElementById('pwName').value.trim();
        const emailValue = document.getElementById('pwEmail').value.trim();

        const $findPwError = $("#findPwError");

        if (!nameValue) {
            showMsg($findPwError, "error", "이름을 입력해주세요.");
            return;
        }

        if (!emailValue) {
            showMsg($findPwError, "error", "이메일을 입력해주세요.");
            return;
        }

        if (!nameValue || !emailValue) {
            showMsg($findPwError, "error", "이름과 이메일을 입력해주세요.");
            return;
        }

        // 비밀번호 찾기(임시비밀번호 발급) 요청
        fetch('/api/member/findPw', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ memberEmail: emailValue })
        })
            .then(response => response.json())
            .then(data => {
                if (data.status === 200) {
                    // 임시 비밀번호 전송 성공
                    alert(`${emailValue}로 임시 비밀번호가 전송되었습니다.\n로그인 후 비밀번호를 변경해주세요.`);
                    window.location.href = '/member/login';
                } else {
                    alert(data.message || '비밀번호 찾기에 실패했습니다.');
                }
            })
            .catch(err => {
                console.error('비밀번호 찾기 에러:', err);
                alert('서버 통신 중 오류가 발생했습니다.');
            });
    });
});
