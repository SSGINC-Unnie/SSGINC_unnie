window.onload = function() {
    fetchPopularPosts();

    const query = '메이크업';
    const apiUrl = `/fetchVideos?query=${query}&relevanceLanguage=ko&regionCode=KR`;

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            console.log("백엔드로부터 받은 전체 응답:", data);

            let youtubeResult;
            try {
                // data.data가 null이 아닌지 먼저 확인
                if (data && data.data && data.data.youtubeResult) {
                    youtubeResult = JSON.parse(data.data.youtubeResult);
                } else {
                    console.error("youtubeResult 필드가 응답에 없습니다.");
                    return;
                }
            } catch (e) {
                console.error("youtubeResult 파싱 실패:", e);
                return;
            }

            if (!youtubeResult || !youtubeResult.items || youtubeResult.items.length === 0) {
                console.log("YouTube API 검색 결과가 없습니다.");
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

const fetchPopularPosts = () => {
    fetch('/api/community/board/popular')
        .then(response => response.json())
        .then(result => {
            if (result.status === 200) {
                renderPopularPosts(result.data);
            }
        })
        .catch(error => console.error("인기 게시글 로딩 실패:", error));
};

const renderPopularPosts = (postList) => {
    const wrapper = document.getElementById('popular-posts-wrapper');
    wrapper.innerHTML = '';

    if (!postList || postList.length === 0) {
        wrapper.innerHTML = '<p>인기 게시글이 없습니다.</p>';
        return;
    }

    postList.forEach(post => {
        const item = document.createElement('div');
        item.className = 'popular-post-item';
        item.onclick = () => { location.href = `/community/board/${post.boardId}`; };

        item.innerHTML = `
            <img src="${post.boardThumbnail || '/img/common/tip1.png'}" alt="${post.boardTitle}">
            <div class="popular-post-info">
                <span class="title">${post.boardTitle}</span>
                <span class="meta">
                    좋아요 ${post.likeCount} · 댓글 ${post.commentCount} · 조회 ${post.boardViews}
                </span>
            </div>
        `;
        wrapper.appendChild(item);
    });
};

function renderShopCards(shopList) {
    const wrapper = document.getElementById('shop-wrapper');
    wrapper.innerHTML = '';

    shopList.forEach(shop => {
        const imageUrl = shop.shopImageUrl || '/img/common/tip1.png';
        const address = shop.shopAddress || '';

        const card = document.createElement('div');
        card.className = 'shop-card';
        card.style.cursor = 'pointer';

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

        card.addEventListener('click', () => {
            window.location.href = `http://localhost:8111/map/shopdetail?shopId=${shop.shopId}`;
        });

        wrapper.appendChild(card);
    });
}