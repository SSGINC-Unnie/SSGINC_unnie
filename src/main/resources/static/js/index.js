window.onload = function() {
    const query = 'beauty';
    const apiUrl = `/fetchVideos?query=${query}&relevanceLanguage=ko&regionCode=KR`;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            let youtubeResult;
            try {
                youtubeResult = JSON.parse(data.data.youtubeResult);
            } catch (e) {
                console.error("youtubeResult 파싱 실패:", e);
                return;
            }

            if (!youtubeResult || !youtubeResult.items || youtubeResult.items.length === 0) {
                return;
            }

            const youtubeWrapper = document.getElementById('youtube-wrapper');
            youtubeWrapper.innerHTML = '';

            youtubeResult.items.forEach(video => {
                const iframeContainer = document.createElement('div');
                iframeContainer.classList.add('video-container');
                iframeContainer.innerHTML = `
                    <iframe width="320" height="180" src="https://www.youtube.com/embed/${video.id.videoId}" frameborder="0" allowfullscreen></iframe>
                `;
                youtubeWrapper.appendChild(iframeContainer);
            });
        });
};

navigator.geolocation.getCurrentPosition(function(position) {
    fetch('/api/shop/nearby', {
        method: 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
        })
    })
        .then(response => response.json())
        .then(result => {
            const shopList = result.data.shops;
            renderShopCards(shopList);
        });
});

function renderShopCards(shopList) {
    const wrapper = document.getElementById('shop-wrapper');
    wrapper.innerHTML = '';

    shopList.forEach(shop => {
        const imageUrl = shop.shopImageUrl || '/img/common/tip1.png';
        const address = shop.shopAddress || ''; // 주소 필드

        const card = document.createElement('div');
        card.className = 'shop-card';
        card.style.cursor = 'pointer'; // 마우스 오버 시 손가락

        card.innerHTML = `
            <div class="shop-image">
                <img src="${imageUrl}" alt="${shop.shopName}">
            </div>
            <div class="shop-info">
                <div class="shop-name">${shop.shopName}</div>
                <div class="shop-address">${address}</div>
                <div class="shop-meta">
                    평점 ${shop.avgRate} &nbsp; 리뷰 ${shop.reviewCount}
                </div>
            </div>
        `;

        // 카드 클릭 시 상세페이지 이동
        card.addEventListener('click', () => {
            window.location.href = `http://localhost:8111/map/shopdetail?shopId=${shop.shopId}`;
        });

        wrapper.appendChild(card);
    });
}

