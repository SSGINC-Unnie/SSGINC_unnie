let lastOpenedDropdown = null; // 마지막으로 열린 드롭다운
let currentPage = 1;            // 현재 페이지
const pageSize = 5;             // 한 페이지에 표시할 데이터 수
let totalPages = 1;             // 총 페이지 수

document.addEventListener("DOMContentLoaded", function() {
    console.log("DOM fully loaded. 초기 데이터 로드 시작");

    // 페이지 전환 버튼
    document.getElementById("prevPage").addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            console.log("이전 페이지 클릭, 현재 페이지:", currentPage);
            fetchMembers(currentPage, pageSize);
        }
    });
    document.getElementById("nextPage").addEventListener("click", () => {
        if (currentPage < totalPages) {
            currentPage++;
            console.log("다음 페이지 클릭, 현재 페이지:", currentPage);
            fetchMembers(currentPage, pageSize);
        }
    });
    // 초기 데이터 로드
    fetchMembers(currentPage, pageSize);
});

// 드롭다운 토글 함수
function toggleDropdown(id) {
    const dropdown = document.getElementById(id);
    console.log("toggleDropdown 호출됨, id:", id);
    if (lastOpenedDropdown && lastOpenedDropdown !== dropdown) {
        console.log("이전 드롭다운 닫기:", lastOpenedDropdown.id);
        lastOpenedDropdown.style.maxHeight = '0';
    }
    if (!dropdown.style.maxHeight || dropdown.style.maxHeight === '0px') {
        dropdown.style.maxHeight = dropdown.scrollHeight + 'px';
        console.log("드롭다운 열림:", id, "새 maxHeight:", dropdown.style.maxHeight);
        lastOpenedDropdown = dropdown;
    } else {
        dropdown.style.maxHeight = '0';
        console.log("드롭다운 닫힘:", id);
        lastOpenedDropdown = null;
    }
}

// 회원 데이터를 백엔드에서 받아와 테이블에 채워넣는 함수
async function fetchMembers(page = 1, pageSize = 5) {
    try {
        console.log("fetchMembers 호출됨, page:", page, "pageSize:", pageSize);
        const response = await fetch(`/api/admin/member?page=${page}&pageSize=${pageSize}`);
        const data = await response.json();
        console.log("fetchMembers 응답 데이터:", data);
        let memberPage = data.data.member;
        let members = memberPage.list;
        console.log("members 리스트:", members);

        // API 응답의 페이지 정보를 업데이트
        currentPage = memberPage.pageNum;
        totalPages = memberPage.pages;
        updatePaginationUI();

        let tableBody = document.getElementById("memberTableBody");
        tableBody.innerHTML = "";
        members.forEach((member, index) => {
            console.log(`member 데이터 (index ${index}):`, member);
            let row = document.createElement("tr");
            row.innerHTML = `
                <td>${member.memberEmail}</td>
                <td>${member.memberName}</td>
                <td>${member.memberState}</td>
                <td>${member.memberRole}</td>
                <td>
                    <button class="details-button" onclick="fetchMemberDetail(${member.memberId}, 'dropdown${index}')">
                        상세보기
                    </button>
                </td>
            `;
            tableBody.appendChild(row);

            let detailRow = document.createElement("tr");
            let detailCell = document.createElement("td");
            detailCell.setAttribute("colspan", "5");
            detailCell.innerHTML = `
                <div id="dropdown${index}" class="dropdown-content">
                </div>
            `;
            detailRow.appendChild(detailCell);
            tableBody.appendChild(detailRow);
        });
    } catch (error) {
        console.error("회원 데이터를 불러오는 중 오류 발생:", error);
    }
}

// 회원 상세 데이터
async function fetchMemberDetail(memberId, dropdownId) {
    try {
        console.log("fetchMemberDetail 호출됨, memberId:", memberId, "dropdownId:", dropdownId);
        // 상세보기 API 호출
        const response = await fetch(`/api/admin/member/${memberId}`);
        const data = await response.json();
        console.log("회원 상세보기 API 응답 데이터:", data);
        const member = data.data.memberDetail;
        console.log("member 객체:", member);
        const memberDetailContainer = document.getElementById(dropdownId);

        // 회원 상세 정보 HTML 구성
        memberDetailContainer.innerHTML = `
            <div class="section-title">회원 상세 정보</div>
            <p><strong>아이디:</strong> ${member.memberEmail}</p>
            <p><strong>회원명:</strong> ${member.memberName}</p>
            <p><strong>닉네임:</strong> ${member.memberNickname}</p>
            <p><strong>전화번호:</strong> ${member.memberPhone}</p>
            <p><strong>회원 상태:</strong> ${member.memberState}</p>
            <p><strong>회원 권한:</strong> ${member.memberRole}</p>
            <p><strong>가입일:</strong> ${member.memberCreatedAt}</p>
        `;
        console.log("회원 상세정보 HTML 조합 완료. dropdownId:", dropdownId);
        toggleDropdown(dropdownId);
    } catch (error) {
        console.error("회원 상세 정보를 불러오는 중 오류 발생:", error);
    }
}

// 페이지네이션 UI 업데이트 함수
function updatePaginationUI() {
    console.log("페이지네이션 업데이트: 현재 페이지:", currentPage, "총 페이지:", totalPages);
    document.getElementById("currentPage").textContent = currentPage;
    document.getElementById("totalPages").textContent = totalPages;
}
