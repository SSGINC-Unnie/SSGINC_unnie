let map;
let markers = []; // 상점 마커 저장 배열
let userMarker = null; // 사용자 위치 마커
let currentShopList = []; // 현재 불러온 상점 데이터
let currentSortOrder = 'asc';
let carouselPositions = {};
let isDragging = false;
let startY = 0;
let startHeight = 0;

// 2) dragHandle, bottomSheet 엘리먼트 참조
const dragHandle = document.getElementById('dragHandle');

// 원하는 최소/최대 높이 비율 (윈도우 전체 높이에 대한 퍼센트)
const MIN_HEIGHT_RATIO = 0.3; // 30%
const MAX_HEIGHT_RATIO = 0.8; // 80%


// 드래그 끝
document.addEventListener('mouseup', () => {
    isDragging = false;
});

// 카테고리에 따른 마커 아이콘 반환 함수
function getMarkerIcon(category) {
    const normalized = category.trim();
    switch (normalized) {
        case "헤어샵":
            return '/img/shop/map_hair.png';
        case "네일샵":
            return '/img/shop/map_neil.png';
        case "에스테틱":
            return '/img/shop/map_aesthetic.png';
        case "왁싱샵":
            return '/img/shop/map_waxing.png';
        case "바버샵":
            return '/img/shop/map_barber.png';
        default:
            console.warn("지원하지 않는 카테고리:", category);
            return '/img/download.png';
    }
}

// 지도 영역 내 상점만 필터링 (옵션)
function filterShopsInView(shops) {
    const bounds = map.getBounds();
    console.log("Map bounds:", bounds);

    return shops.filter(shop => {
        const shopLatLng = new naver.maps.LatLng(shop.shopLatitude, shop.shopLongitude);
        console.log("Shop Coordinates:", shop.shopLatitude, shop.shopLongitude);
        return bounds.hasLatLng(shopLatLng);
    });
}

// 기존 마커 제거
function removeMarkers() {
    markers.forEach(marker => marker.setMap(null));
    markers = [];
}

// 마커 업데이트
function updateMarkers(shops) {
    removeMarkers();
    const visibleShops = filterShopsInView(shops);

    visibleShops.forEach(shop => {
        console.log(`Creating marker for ${shop.shopName}: (${shop.shopLatitude}, ${shop.shopLongitude})`);
        const marker = new naver.maps.Marker({
            position: new naver.maps.LatLng(shop.shopLatitude, shop.shopLongitude),
            map: map,
            title: `${shop.shopName} - ${shop.shopCategory}`,
            icon: {
                url: getMarkerIcon(shop.shopCategory),  // 아이콘 설정
                size: new naver.maps.Size(40, 40),
                anchor: new naver.maps.Point(20, 20)
            }
        });
        markers.push(marker);

        // 마커 클릭 시 로그 출력 및 상세 페이지로 이동
        naver.maps.Event.addListener(marker, 'click', function () {
            console.log("Marker clicked for shop:", shop.shopName);
            window.location.href = `shopdetail?shopId=${shop.shopId}`;
        });
    });
}

// 지도 초기화 함수
async function initMap(lat, lng, shops) {
    const mapOptions = {
        center: new naver.maps.LatLng(lat, lng),
        zoom: 15
    };
    map = new naver.maps.Map('map', mapOptions);
    await updateMarkers(shops);

    // 사용자 위치 마커 생성
    userMarker = new naver.maps.Marker({
        position: new naver.maps.LatLng(lat, lng),
        map: map,
        title: '현재 위치'
    });
}

// 사용자 위치와 상점 데이터 로드 후 지도 초기화
async function getLocationAndInit() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            async (position) => {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;

                // 지도 초기화
                await initMap(lat, lng, currentShopList);

                // Reverse Geocoding 호출
                await updateMyLocationText(lat, lng);  // 이 부분을 지도 초기화 후 호출
            },
            error => {
                console.error('위치 정보를 가져올 수 없습니다:', error.message);
                document.getElementById('addressFallback').style.display = 'block';
            },
            {enableHighAccuracy: true}
        );
    } else {
        console.error('브라우저가 위치 정보를 지원하지 않습니다.');
    }
} // 누락된 괄호 추가

// Reverse Geocoding: 현재 위치 주소 업데이트 함수
async function updateMyLocationText(lat, lng) {
    return new Promise((resolve, reject) => {
        naver.maps.Service.reverseGeocode({
            coords: new naver.maps.LatLng(lat, lng),
            orders: [naver.maps.Service.OrderType.ADDR, naver.maps.Service.OrderType.ROAD_ADDR].join(',') // 주소 변환
        }, (status, response) => {
            if (status === naver.maps.Service.Status.OK) {
                const result = response.v2.results[0];
                if (result) {
                    const {region} = result;
                    const area1 = region.area1.name || '';
                    const area2 = region.area2.name || '';
                    const shortAddress = `${area1} ${area2}`.trim();
                    document.getElementById('myLocationText').textContent = shortAddress || '현재 위치';
                } else {
                    document.getElementById('myLocationText').textContent = '주소 정보를 찾을 수 없습니다.';
                }
            } else {
                console.error('Reverse Geocoding 실패:', status);
                document.getElementById('myLocationText').textContent = '주소 정보를 불러오지 못했습니다.';
            }
        });
    });
}

// "이 지역 재검색" 버튼 클릭 이벤트
document.getElementById('reSearchBtn').addEventListener('click', async () => {
    const center = map.getCenter();
    const lat = center.lat();
    const lng = center.lng();
    const currentZoom = map.getZoom();
    try {
        const response = await fetch('/api/shop');
        const data = await response.json();
        currentShopList = data.data.shops;

        // 지도 마커 업데이트
        updateMarkers(currentShopList);  // 마커를 다시 갱신

        // 하단 리스트 렌더링
        renderShopList(currentShopList);
    } catch (error) {
        console.error('상점 데이터를 가져오는 데 실패했습니다:', error);
        document.getElementById('shopList').innerHTML = '<div>상점 데이터를 불러오는 데 실패했습니다. 나중에 다시 시도해 주세요.</div>';
    }
});

// 카테고리별 매장 불러오기 함수
async function loadShopsByCategory(category) {
    try {
        const res = await fetch(`/api/shop/category/${category}`);
        const response = await res.json();

        // 디버깅: 응답 데이터 확인
        console.log('[Debug] Category:', category, 'Response:', response);

        // shops 배열 확인
        currentShopList = response.data.shops;
        console.log('[Debug] currentShopList:', currentShopList);

        // 지도 마커 업데이트
        updateMarkers(currentShopList);  // 마커를 다시 갱신

        // 하단 리스트 렌더링
        renderShopList(currentShopList);
    } catch (err) {
        console.error('카테고리별 상점 조회 실패:', err);
    }
}

// 미디어 API를 호출하여 해당 상점의 미디어 이미지 HTML을 반환하는 함수
async function getMediaImagesHTML(shopId) {
    try {
        const res = await fetch(`/api/media/file?targetType=SHOP&targetId=${shopId}`);
        const data = await res.json();

        if (data.data) {
            if (Array.isArray(data.data.fileUrns) && data.data.fileUrns.length > 0) {
                return getCarouselHTML(shopId, data.data.fileUrns);
            } else if (data.data.fileUrn) {
                return getCarouselHTML(shopId, [data.data.fileUrn]);
            }
        }
        return '';
    } catch (error) {
        console.error("미디어 이미지 가져오기 실패:", error);
        return '';
    }
}

async function renderShopList(shops) {
    console.log('[Debug] renderShopList shops:', shops); // 추가

    const sortMode = document.getElementById('sortSelect').value;
    const shopListEl = document.getElementById('shopList');

    // 데이터가 없으면 안내 문구
    if (!shops || shops.length === 0) {
        shopListEl.innerHTML = '<div>해당 카테고리 상점이 없습니다.</div>';
        return;
    }


    let sortedShops = shops.slice(); // 원본 배열 복사
    if (sortMode === 'asc') {
        // 이름 오름차순 (가나다 / ABC)
        sortedShops.sort((a, b) => a.shopName.localeCompare(b.shopName));
    } else if (sortMode === 'desc') {
        // 이름 내림차순 (역순)
        sortedShops.sort((a, b) => b.shopName.localeCompare(a.shopName));
    }

    shopListEl.innerHTML = ''; // 기존 목록 초기화

    // 각 상점에 대해 미디어 이미지를 가져와 렌더링
    for (const shop of sortedShops) {
        let mediaHTML = await getMediaImagesHTML(shop.shopId);
        // 미디어 이미지가 없으면 기본 이미지 출력 (원하는 경우)
        if (!mediaHTML) {
            mediaHTML = ` 
                    <div class="media-image">
                        <img src="/img/shop/download.jpg" alt="기본 이미지">
                    </div>
            `;
        }

        const item = document.createElement('div');
        item.classList.add('shop-item');

        item.innerHTML = `
          <div class="shop-thumbs">
            ${mediaHTML}
          </div>
          <div class="shop-info">
            <h3>
              <a href="/map/shopdetail?shopId=${shop.shopId}">
                ${shop.shopName}
              </a>
            </h3>
            <p class="shop-location">${shop.shopLocation}</p>
            <p>평점 ${shop.avgRate} / 리뷰 ${shop.review_count}</p>
          </div>
        `;
        shopListEl.appendChild(item);
    }
}

// Daum 우편번호 서비스로 주소 검색 팝업 열기
function openDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 선택된 주소 (기본 주소)
            var fullAddr = data.address;
            searchAddressToCoordinate(fullAddr);
        }
    }).open();
}

// 주소 검색을 통한 좌표 변환 함수 (네이버 지도 Geocoder 사용)
async function searchAddressToCoordinate(address) {
    try {
        const res = await naver.maps.Service.geocode({ query: address });
        const result = res.v2.addresses[0];
        if (!result) {
            console.error("주소 검색 결과가 없습니다.");
            return;
        }
        const lat = parseFloat(result.y);
        const lng = parseFloat(result.x);

        // 여기서 직접 입력한 주소를 #myLocationText에 표시
        document.getElementById('myLocationText').textContent = address;

        // 지도 초기화 (상점 데이터 다시 fetch)
        const response = await fetch('/api/shop');
        const data = await response.json();
        currentShopList = data.data.shops;
        initMap(lat, lng, currentShopList);
        // 주소 입력 폼 숨기기
        document.getElementById('addressFallback').style.display = 'none';
    } catch (error) {
        alert('주소 검색에 실패했습니다. 올바른 주소를 입력해 주세요.');
    }
}

// 주소 입력 fallback의 "주소 검색" 버튼 클릭 이벤트
document.getElementById('openPostcodeBtn').addEventListener('click', function () {
    openDaumPostcode();
});

// 드롭다운으로 정렬을 선택할 때마다 목록 재렌더링
document.getElementById('sortSelect').addEventListener('change', function () {
    renderShopList(currentShopList);
});

document.addEventListener('DOMContentLoaded', () => {
    getLocationAndInit();

    const listViewBtn = document.getElementById('listViewBtn');
    const bottomSheet = document.getElementById('bottomSheet');
    const closeBottomSheetBtn = document.getElementById('closeBottomSheetBtn');

    // 기존 바텀시트 열기/닫기
    listViewBtn.addEventListener('click', () => {
        bottomSheet.classList.add('open');
    });
    closeBottomSheetBtn.addEventListener('click', () => {
        bottomSheet.classList.remove('open');
    });

    // 카테고리 탭 클릭 이벤트 (기존 유지)
    const categoryBtns = document.querySelectorAll('.category-btn');
    categoryBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            categoryBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const selectedCategory = btn.getAttribute('data-category');
            loadShopsByCategory(selectedCategory);
        });
    });

    const urlParams = new URLSearchParams(window.location.search);
    const selectedCategory = urlParams.get('category');
    if (selectedCategory) {
        // 해당 카테고리 탭 활성화
        categoryBtns.forEach(btn => {
            if (btn.getAttribute('data-category') === selectedCategory) {
                btn.classList.add('active');
                loadShopsByCategory(selectedCategory);
            } else {
                btn.classList.remove('active');
            }
        });
        // 목록(바텀시트) 자동 open
        bottomSheet.classList.add('open');
    } else {
        // 기존처럼 defaultCategory 사용
        const defaultCategory = document.querySelector('.category-btn.active')?.getAttribute('data-category');
        if (defaultCategory) {
            loadShopsByCategory(defaultCategory);
        }
    }
});

function getCarouselHTML(shopId, fileUrns) {
    carouselPositions[shopId] = 0; // 캐러셀 인덱스 초기화

    // 각 이미지에 onerror 이벤트 추가 → 로드 실패 시 기본 이미지로 교체
    const imagesHTML = fileUrns.map(url => `
        <div class="media-image">
            <img 
                src="${url}" 
                alt="업체 이미지" 
                onerror="this.onerror=null;this.src='/img/shop/download.jpg';"
            >
        </div>
    `).join('');

    // 이미지가 3장 이상일 때만 화살표 버튼 추가
    const arrowsHTML = fileUrns.length > 3 ? `
        <button class="arrow left" onclick="prevSlide(${shopId})">&#10094;</button>
        <button class="arrow right" onclick="nextSlide(${shopId})">&#10095;</button>
    ` : '';

    return `
        <div class="media-carousel" id="carousel-${shopId}">
            ${arrowsHTML}
            <div class="carousel-wrapper" id="carouselWrapper-${shopId}">
                ${imagesHTML}
            </div>
        </div>
    `;
}
