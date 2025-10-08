// ============== 전역 변수 및 함수 선언부 ==============
const md = window.markdownit();
let lastOpenedDropdown = null;

// 관리자 조치 함수 (onclick에서 호출되도록 전역에 선언)
async function ignoreReport(reportId) {
    if (!confirm(`신고 ID ${reportId}를 '무시' 처리하시겠습니까?`)) return;
    try {
        const response = await fetch(`/api/admin/report/${reportId}`, { method: 'PATCH' });
        if (response.ok) {
            alert('신고를 무시 처리했습니다.');
            document.getElementById('search-btn').click(); // 목록 새로고침
        } else {
            alert('처리 중 오류가 발생했습니다.');
        }
    } catch(error) { console.error('Error ignoring report:', error); }
}

async function processReport(reportId, targetType, targetId) {
    if (!confirm(`신고 ID ${reportId}에 대한 콘텐츠를 '삭제 처리'하시겠습니까?`)) return;
    try {
        // 백엔드의 AdminReportController @DeleteMapping에 AdminReportDeleteRequest를 @RequestBody로 받도록 수정해야 할 수 있습니다.
        const response = await fetch(`/api/admin/report`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ reportId, reportTargetType: targetType, reportTargetId: targetId })
        });
        if (response.ok) {
            alert('콘텐츠를 삭제 처리했습니다.');
            document.getElementById('search-btn').click(); // 목록 새로고침
        } else {
            alert('처리 중 오류가 발생했습니다.');
        }
    } catch(error) { console.error('Error processing report:', error); }
}

// 드롭다운 토글 함수
function toggleDropdown(id) {
    const dropdown = document.getElementById(id);
    if (!dropdown) return;

    if (lastOpenedDropdown && lastOpenedDropdown !== dropdown) {
        lastOpenedDropdown.style.maxHeight = '0';
        lastOpenedDropdown.style.padding = '0 20px';
    }
    if (!dropdown.style.maxHeight || dropdown.style.maxHeight === '0px') {
        dropdown.style.padding = '20px';
        dropdown.style.maxHeight = (dropdown.scrollHeight + 40) + 'px'; // padding 고려
        lastOpenedDropdown = dropdown;
    } else {
        dropdown.style.maxHeight = '0';
        dropdown.style.padding = '0 20px';
        lastOpenedDropdown = null;
    }
}

// 상세 정보 조회 함수 (onclick에서 호출되도록 전역에 선언)
async function fetchReportDetail(reportId, dropdownId) {
    const dropdown = document.getElementById(dropdownId);
    if (dropdown.innerHTML) {
        toggleDropdown(dropdownId);
        return;
    }

    dropdown.innerHTML = '<p>상세 정보를 불러오는 중...</p>';
    toggleDropdown(dropdownId);
    try {
        const response = await fetch(`/api/admin/report/${reportId}`);
        if (!response.ok) throw new Error('상세 정보를 불러오지 못했습니다.');

        const result = await response.json();
        const report = result.data.report;

        // 백엔드에서 targetType을 숫자로 보내준다고 가정 (예: 1, 2, 3)
        const targetTypeNumeric = report.reportTargetType === '게시글' ? 1 : (report.reportTargetType === '댓글' ? 2 : 3);

        const detailHtml = `
            <div class="section-title">신고 정보</div>
            <div class="report-detail-info">
                <dl>
                    <div><dt>신고한 회원 ID</dt><dd>${report.reportMemberId}</dd></div>
                    <div><dt>처리일</dt><dd>${report.reportResolvedAt ? new Date(report.reportResolvedAt).toLocaleString() : '미처리'}</dd></div>
                </dl>
            </div>
            <div class="section-title">신고 상세 사유</div>
            <div class="markdown-body">
                ${report.reportReasonDetailed ? md.render(report.reportReasonDetailed) : '상세 사유가 없습니다.'}
            </div>
            <div class="section-title">신고된 콘텐츠 원문</div>
            <div class="reported-content-body">
                ${report.reportedContent || '콘텐츠 원문을 불러올 수 없습니다.'}
            </div>
            <div class="admin-actions">
                <button class="btn-secondary" onclick="ignoreReport(${report.reportId})">신고 무시</button>
                <button class="btn-danger" onclick="processReport(${report.reportId}, ${targetTypeNumeric}, ${report.reportTargetId})">콘텐츠 삭제 처리</button>
            </div>
        `;
        dropdown.innerHTML = detailHtml;
        // 내용이 로드된 후 정확한 높이로 다시 조절
        dropdown.style.maxHeight = (dropdown.scrollHeight + 40) + 'px';

    } catch (error) {
        dropdown.innerHTML = `<p style="color: red; padding: 20px;">${error.message}</p>`;
        dropdown.style.maxHeight = (dropdown.scrollHeight + 40) + 'px';
    }
}


// ============== DOMContentLoaded 이벤트 리스너 ==============
document.addEventListener('DOMContentLoaded', () => {

    const startDateInput = document.getElementById('start-date');
    const endDateInput = document.getElementById('end-date');
    const targetTypeSelect = document.getElementById('target-type');
    const reportStatusSelect = document.getElementById('report-status');
    const searchBtn = document.getElementById('search-btn');
    const tableBody = document.getElementById('report-list-body');
    const paginationContainer = document.getElementById('pagination-container');

    // 날짜 기본값 설정 (오늘 ~ 한 달 전)
    const today = new Date();
    const oneMonthAgo = new Date(new Date().setMonth(today.getMonth() - 1));
    endDateInput.value = today.toISOString().split('T')[0];
    startDateInput.value = oneMonthAgo.toISOString().split('T')[0];

    const fetchReports = async (page = 1) => {
        const startDate = startDateInput.value;
        const endDate = endDateInput.value;
        const targetType = targetTypeSelect.value;
        const status = reportStatusSelect.value;

        const params = new URLSearchParams({
            page: page,
            startDate: startDate,
            endDate: endDate
        });

        if (targetType) {
            params.append('targetType', targetType);
        }
        if (status) {
            params.append('status', status);
        }

        const url = `/api/admin/report?${params.toString()}`;

        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error('데이터를 불러오는 데 실패했습니다.');

            const result = await response.json();
            const pageInfo = result.data.reports;

            renderTable(pageInfo.list);
            renderPagination(pageInfo);

        } catch (error) {
            console.error(error);
            if(tableBody) tableBody.innerHTML = `<tr><td colspan="7">${error.message}</td></tr>`;
        }
    };

    const renderTable = (reports) => {
        if(!tableBody) return;
        tableBody.innerHTML = '';
        if (reports.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="7">조회된 신고 내역이 없습니다.</td></tr>';
            return;
        }

        reports.forEach(report => {
            let statusClass = '';
            switch(report.reportStatus) {
                case '미처리': statusClass = 'status-pending'; break;
                case '처리 완료': statusClass = 'status-processed'; break;
                case '무시': statusClass = 'status-ignored'; break;
            }

            // 기본 행 생성
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${report.reportId}</td>
                <td>${report.reportTargetType}</td>
                <td>${report.reportTargetId}</td>
                <td>${report.reportReason}</td>
                <td>${report.reportCreatedAt ? report.reportCreatedAt.replace('T', ' ').substring(0, 16) : ''}</td>
                <td><span class="status-badge ${statusClass}">${report.reportStatus}</span></td>
                <td><button class="details-button" onclick="fetchReportDetail(${report.reportId}, 'dropdown${report.reportId}')">보기</button></td>
            `;
            tableBody.appendChild(row);

            // 드롭다운 상세 정보 행 생성
            const detailRow = document.createElement('tr');
            detailRow.innerHTML = `<td colspan="7"><div id="dropdown${report.reportId}" class="dropdown-content"></div></td>`;
            tableBody.appendChild(detailRow);
        });
    };

    const renderPagination = (pageInfo) => {
        if(!paginationContainer) return;
        paginationContainer.innerHTML = '';

        if (pageInfo.hasPreviousPage) {
            const prevBtn = document.createElement('button');
            prevBtn.textContent = '이전';
            prevBtn.onclick = () => fetchReports(pageInfo.prePage);
            paginationContainer.appendChild(prevBtn);
        }

        pageInfo.navigatepageNums.forEach(pageNum => {
            const pageBtn = document.createElement('button');
            pageBtn.textContent = pageNum;
            if (pageNum === pageInfo.pageNum) {
                pageBtn.classList.add('active');
            }
            pageBtn.onclick = () => fetchReports(pageNum);
            paginationContainer.appendChild(pageBtn);
        });

        if (pageInfo.hasNextPage) {
            const nextBtn = document.createElement('button');
            nextBtn.textContent = '다음';
            nextBtn.onclick = () => fetchReports(pageInfo.nextPage);
            paginationContainer.appendChild(nextBtn);
        }
    };

    if(searchBtn) {
        searchBtn.addEventListener('click', () => fetchReports(1));
    }

    // 페이지 첫 로드 시 데이터 조회
    fetchReports(1);
});