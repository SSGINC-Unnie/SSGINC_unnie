document.addEventListener('DOMContentLoaded', () => {
    const manageMyShopBtn = document.getElementById('manage-my-shop-btn');
    const shopSubmenu = document.getElementById('shop-submenu');
    const dropdownArrow = document.getElementById('dropdown-arrow');

    // '내 업체 관리' 메뉴가 페이지에 존재하는 경우에만 실행
    if (manageMyShopBtn) {
        manageMyShopBtn.addEventListener('click', async (e) => {
            e.preventDefault(); // 링크의 기본 동작(페이지 이동) 방지

            const parentMenuItem = manageMyShopBtn.parentElement;
            const isOpen = parentMenuItem.classList.contains('open');

            // 이미 열려있으면 닫기
            if (isOpen) {
                shopSubmenu.style.display = 'none';
                parentMenuItem.classList.remove('open');
            } else {
                // 서브메뉴 내용이 비어있을 때만 API를 호출하여 목록을 가져옴 (최초 1회)
                if (shopSubmenu.innerHTML.trim() === '') {
                    shopSubmenu.innerHTML = '<li><a>로딩 중...</a></li>';
                    await loadManagerShops();
                }
                shopSubmenu.style.display = 'block';
                parentMenuItem.classList.add('open');
            }
        });
    }

    /**
     * 관리자 소유 매장 목록 API를 호출하고 서브메뉴를 생성하는 함수
     */
    async function loadManagerShops() {
        try {
            const response = await fetch('/api/mypage/shops/manager');
            const result = await response.json();

            if (!response.ok || !result.data) {
                throw new Error('매장 목록을 불러오는 데 실패했습니다.');
            }

            shopSubmenu.innerHTML = ''; // '로딩 중...' 메시지 제거

            if (result.data.length > 0) {
                result.data.forEach(shop => {
                    const listItem = document.createElement('li');
                    // 각 업체를 클릭하면 해당 shopId를 가진 대시보드 페이지로 이동
                    listItem.innerHTML = `<a href="/manager/dashboard?shopId=${shop.shopId}">${shop.shopName}</a>`;
                    shopSubmenu.appendChild(listItem);
                });
            } else {
                shopSubmenu.innerHTML = '<li><a>등록된 매장이 없습니다.</a></li>';
            }
        } catch (error) {
            shopSubmenu.innerHTML = `<li><a>${error.message}</a></li>`;
        }
    }
});