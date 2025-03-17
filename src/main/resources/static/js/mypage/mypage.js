document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.menu-item.dropdown').forEach(menu => {
        menu.addEventListener('click', function() {
            this.classList.toggle('active'); // 클릭 시 active 클래스 추가/제거
        });
    });
});
    // ROLE_MANAGER 여부에 따라 드롭다운 숨김 처리
    const managerDropdown = document.querySelector(".dropdown-content li[sec2\\:authorize]");
    if (managerDropdown && !managerDropdown.hasAttribute("data-visible")) {
        managerDropdown.style.display = "none";
    }