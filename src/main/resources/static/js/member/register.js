//유효성 검증
let isValid = {
    email: false,
    pw: false,
    confirmPw: false,
    name: false,
    nickname: false,
    phone: false
};

// ------------------------------------
// 정규식 (입력값 유효성 검증)
// ------------------------------------
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,20}$/;
const nameRegex = /^[가-힣]{2,10}$/;
const nicknameRegex = /^[가-힣a-zA-Z0-9]{2,20}$/;
const phoneRegex = /^01[0-9]-\d{3,4}-\d{4}$/;
const birthRegex = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;

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

// 메시지 표시: type이 "success"이면 초록색, 아니면 빨간색으로 표시
function showMsg(element, type, message) {
    element.html(`<p>${message}</p>`).css('color', type === "success" ? 'green' : 'red');
}


// ------------------------------------
// 인증 타이머 클래스 (AuthTimer)
// ------------------------------------
// 인증번호 전송 후 남은 유효시간(여기서는 180초 = 3분)을 표시,
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

// ================================= 아이디(이메일) ======================================
// ------------------------------------
// 이메일 중복 체크
// ------------------------------------
async function validateEmailDuplication() {
    // 이메일 입력란의 값
    const emailValue = $("#memberEmail").val().trim();
    // 에러 메시지
    const $emailError = $("#emailError");

    if (!emailValue) {
        showMsg($emailError, "error", "이메일을 입력해주세요.");
        return false;
    }
    // 이메일 형식 검사
    if (!emailRegex.test(emailValue)) {
        showMsg($emailError, "error", "올바른 이메일 형식이 아닙니다.");
        return false;
    }

    try {
        // 서버에 GET 요청: /member/checkMemberEmail?email=입력값
        const response = await axios.get("/member/checkEmail", {
            params: { email: emailValue }
        });
        // 서버가 true를 반환하면 사용 가능한 이메일입니다.
        if (response.data) {
            showMsg($emailError, "success", "");
            return true;
        } else {
            showMsg($emailError, "error", "이미 사용 중인 이메일입니다.");
            return false;
        }
    } catch (error) {
        console.error(error);
        return false;
    }
}

// ------------------------------------
// 이메일 인증 클래스
// ------------------------------------
//이메일 인증번호 전송 및 확인, 3분 타이머 표시
class EmailAuthentication {
    constructor() {
        this.isSending = false; //인증번호 전송 중 여부
        this.authManager = new AuthTimer(
            180,
            $("#email-auth-timer"),
            this.onTimerExpire.bind(this)
        );

        this.$emailInput = $("#memberEmail");
        this.$emailError  = $("#emailError");
        this.$authInput = $("#emailAuthCode");
        this.$authSection = $("#emailAuthSection");
        this.$sendButton = $("#sendEmailVerification");
        this.$verifyButton = $("#verifyEmailCode");

        // 추가: 인증번호 입력 그룹
        this.$authGroup = $("#emailAuthGroup"); // HTML에 id="emailAuthGroup"

        this.initializeEvents();
    }

    initializeEvents() {
        // 이메일 입력란에서 포커스를 잃었을 때 자동으로 중복 체크 수행
        this.$emailInput.on("blur", async () => {
            // 중복 체크 결과를 저장
            const duplicationChecked = await validateEmailDuplication();
            // 통과 시 전송 버튼 활성화, 아니면 비활성화
            this.$sendButton.prop("disabled", !duplicationChecked);
        });

        //전송버튼
        this.$sendButton.on("click", () => this.sendAuthNum());
        //인증하기 버튼
        this.$verifyButton.on("click", () => this.verifyAuthNum());

    }

    // 인증번호 전송
    async sendAuthNum() {

        if (this.isSending) {
            showMsg(this.$emailError, "error", "이미 전송되었습니다.");
            return;
        }
        const emailVal = this.$emailInput.val().trim();
        if (!emailVal) {
            showMsg(this.$emailError, "error", "이메일을 입력해주세요.");
            return;
        }
        if (!emailRegex.test(emailVal)) {
            showMsg(this.$emailError, "error", "잘못된 이메일 형식입니다.");
            return;
        }

        this.isSending = true;

        //버튼 클릭 후 인증번호 입력 영역 (#email-auth-section) 표시
        // 인증번호 입력칸 / 영역 보이기 & 초기화
        this.$authGroup.show();
        this.$authInput.prop("readonly", false).val("").focus();
        this.$authSection.html(""); // 메시지 초기화
        this.$authSection.show();

        try{
            // 서버에 인증번호 발송 요청 (요청 본문은 { email: "입력된 이메일" } 형식)
            const response = await axios.post("/api/member/sendEmail",
                { memberEmail: emailVal }, { headers: { "Content-Type": "application/json" } });
            handleResponse(response, this.$emailError);

            // 인증번호 전송 성공 시:
            // 1. 3분 타이머 시작
            this.authManager.start();

        } catch (error) {
            handleError(error, this.$emailError);
            isValid.email = false;
            this.isSending = false;
            // 전역 플래그 isValid.email 업데이트
            isValid.email = false;
        } finally {
        }
    }

    // 인증번호 확인 함수
    async verifyAuthNum() {
        const codeVal = this.$authInput.val().trim();
        if (!codeVal) {
            showMsg(this.$authSection, "error", "인증번호를 입력해주세요.");
            isValid.email = false;
            return;
        }
        try {
            // 서버에 인증번호 확인 요청 (요청 본문: { key: 이메일, value: 인증번호 })
            const response = await axios.post("/api/member/checkEmail",  {
                memberEmail: this.$emailInput.val(),
                code: codeVal
            }, {
                headers: { "Content-Type": "application/json" }
            });
            handleResponse(response, this.$authSection);

            // 인증 성공 시
            this.authManager.stop();
            this.authManager.hide(); // 타이머 영역 숨김
            this.$authInput.prop("readonly", true);
            this.$sendButton.prop("disabled", true);
            this.$verifyButton.prop("disabled", true);

            isValid.email = true;
        } catch (error) {
            isValid.email = false;
            handleError(error, this.$authSection);
        }
    }

    // 타이머 만료
    onTimerExpire() {
        showMsg(this.$authSection, "error", "인증 시간이 초과되었습니다.");
        this.authManager.hide();
    }
}


// ================================= 비밀번호 ======================================
function checkPasswordValid() {
    const $pwInput = $("#memberPw");
    const pw = $("#memberPw").val().trim();
    const $pwError = $("#pwError");

    // 만약 비밀번호 필드가 없다면(소셜 로그인 사용자)
    if ($pwInput.length === 0) {
        isValid.pw = true;
        return;
    }

    if (!pw) {
        showMsg($pwError, "error", "비밀번호를 입력해주세요.");
        isValid.pw = false;
        return;
    }
    if (pwRegex.test(pw)) {
        isValid.pw = true;
        showMsg($pwError, "success", "");
    } else {
        showMsg($pwError, "error", "영문, 숫자, 특수문자(!@#$%^&*) 포함 8~20자 이내로 설정하세요.");
        isValid.pw = false;
    }
}

// 비밀번호 확인
function confirmPw() {
    const $pwInput = $("#memberPw");
    const $pwConfirmInput = $("#memberPwConfirm");
    const pw = $("#memberPw").val().trim();
    const pwConfirm = $("#memberPwConfirm").val().trim();
    const $pwConfirmError = $("#pwConfirmError");

    // 만약 비밀번호 필드가 없다면 (소셜 로그인 사용자), 건너뛰기
    if ($pwInput.length === 0 || $pwConfirmInput.length === 0) {
        isValid.confirmPw = true;
        $pwConfirmError.html("");
        return;
    }

    if (!pwConfirm) {
        // 아무 것도 입력 안 했으면 메시지 비우기
        $pwConfirmError.html("");
        isValid.confirmPw = false;
        return;
    }

    // 비밀번호가 유효한 상태에서만 비교
    if (pw === pwConfirm && isValid.pw) {
        isValid.confirmPw = true;
        showMsg($pwConfirmError, "success", "");
    } else {
        // 에러 메시지
        showMsg($pwConfirmError, "error", "비밀번호가 일치하지 않습니다.");
        isValid.confirmPw = false;
    }
}

// ================================= 이름 ======================================
function validateName() {
    const name = $("#memberName").val().trim();
    const $nameError = $("#nameError");

    if (!name) {
        showMsg($nameError, "error", "이름을 입력해주세요.");
        isValid.name = false;
        return;
    }
    if (!nameRegex.test(name)) {
        showMsg($nameError, "error", "한글 2~10자 이내로 입력해주세요.");
        isValid.name = false;
    } else {
        // 문제 없으면 메시지 제거
        $nameError.html("");
        isValid.name = true;
    }
}

// ================================= 닉네임 ======================================
//----------------------------------------------------
// 닉네임 중복 검사 (버튼 클릭 시)
//----------------------------------------------------
async function validateNicknameDuplication() {
    const nicknameValue = $("#memberNickname").val().trim();
    const $nicknameError = $("#nicknameError");

    if (!nicknameValue) {
        showMsg($nicknameError, "error", "닉네임을 입력해주세요.");
        isValid.nickname = false;
        return;
    }
    // 닉네임 정규식 체크
    if (!nicknameRegex.test(nicknameValue)) {
        showMsg($nicknameError, "error", "2~20글자로 입력해주세요.");
        isValid.nickname = false;
        return;
    }

    try {
        const response = await axios.get("/member/checkNickname", {
            params: { nickname: nicknameValue }
        });
        if (response.data) {
            // true면 사용 가능
            showMsg($nicknameError, "success", "✔ 사용 가능한 닉네임입니다.");
            isValid.nickname = true;
        } else {
            showMsg($nicknameError, "error", "이미 사용 중인 닉네임입니다.");
            isValid.nickname = false;
        }
    } catch (error) {
        console.error(error);
        showMsg($nicknameError, "error", "닉네임 중복 확인 중 오류가 발생했습니다.");
        isValid.nickname = false;
    }
}


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

        // 추가: 전화번호 인증번호 입력 영역 (통째로 show/hide)
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

            // 전송 성공 시: 인증번호 영역 보이기
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


// ------------------------------------
// 회원가입 폼 제출
// ------------------------------------
$(document).ready(() => {
    // 이메일 / 전화번호 인증 클래스 생성
    const emailAuth = new EmailAuthentication();
    const phoneAuth = new PhoneAuthentication();

    // 비밀번호 / 비밀번호 확인 실시간 체크
    $("#memberPw").on("keyup", checkPasswordValid);
    $("#memberPwConfirm").on("keyup", confirmPw);
    //이름
    $("#memberName").on("blur", validateName);
    // 닉네임 중복 확인 버튼
    $("#checkNickname").on("click", validateNicknameDuplication);

    // 회원가입 폼 submit
    $("#registrationForm").on("submit", function(e) {
        e.preventDefault();

        // 소셜 로그인 여부를 체크 (memberPw 필드가 없으면 소셜 로그인)
        const isSocial = ($("#memberPw").length === 0);
        // URL 설정 (소셜로그인 여부 확인)
        REGISTER_URL = isSocial ? "/api/oauth/register/complete" : "/api/member/register";

        // 최종 폼 검증 (비밀번호, 이름 등)
        if (!isSocial) {
            checkPasswordValid();
            confirmPw();
        } else {
            // 소셜 로그인 사용자는 비밀번호 검증 건너뛰기
            isValid.pw = true;
            isValid.confirmPw = true;
            // 이메일 인증 건너뛰기
            isValid.email = true;
        }

        validateName();

        if (isValid.email && isValid.pw && isValid.confirmPw && isValid.name && isValid.nickname && isValid.phone) {
            const requestBody = {
                // 소셜 로그인이면 숨은 필드의 값을 사용, 아니면 입력한 값을 사용
                memberEmail: isSocial ? $("#oauthMemberEmail").val().trim() : $("#memberEmail").val().trim(),
                memberPw: isSocial ? $("#oauthMemberPw").val().trim() : $("#memberPw").val().trim(),
                memberName: $("#memberName").val().trim(),
                memberNickname: $("#memberNickname").val().trim(),
                memberPhone: $("#memberPhone").val().trim()
            };

            if(isSocial) {
                requestBody.memberProvider = $("#oauthMemberProvider").val().trim();
            }

            console.log("Request Body:", requestBody);

            fetch(REGISTER_URL, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(requestBody)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("서버 응답이 올바르지 않습니다.");
                    }
                    return response.json();
                })
                .then(data => {
                    if (isSocial) {
                        // 소셜 회원가입: 서버가 { "redirect": "/" }를 반환하므로 바로 홈으로 이동
                        window.location.href = data.redirect || "/";
                    } else {
                        // 일반 회원가입: 로그인 페이지로 이동
                        window.location.href = "/member/login";
                    }
                })
                .catch(error => {
                    console.error("에러 발생:", error);
                    alert("회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
                });
        } else {
            alert("입력한 정보를 다시 확인해주세요.");
        }
    });
});


