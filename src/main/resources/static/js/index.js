window.onload = function() {
    const apiUrl = "/fetchVideos?query=뷰티꿀팁";  // YouTube API 엔드포인트

    fetch(apiUrl)
        .then(response => response.json())
        .then(data => {
            console.log('YouTube API 데이터:', data);  // 데이터를 콘솔에 출력하여 확인

            // 문자열 형태로 반환된 youtubeResult를 파싱하여 객체로 변환
            let youtubeResult;
            try {
                youtubeResult = JSON.parse(data.data.youtubeResult);  // JSON 문자열을 객체로 변환
            } catch (e) {
                console.error("youtubeResult 파싱 실패:", e);
                return;
            }

            // 결과가 없는 경우 처리
            if (!youtubeResult || !youtubeResult.items || youtubeResult.items.length === 0) {
                console.log('검색 결과가 없습니다.');
                return;
            }

            const tipsWrapper = document.getElementById('tips-wrapper');
            tipsWrapper.innerHTML = '';  // 기존 콘텐츠 제거

            youtubeResult.items.forEach(video => {
                // 동적으로 iframe 요소 생성
                const iframeContainer = document.createElement('div');
                iframeContainer.classList.add('video-container');

                // iframe으로 유튜브 영상을 삽입 (640x360 크기)
                iframeContainer.innerHTML = `
                    <iframe width="640" height="360" src="https://www.youtube.com/embed/${video.id.videoId}" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
                `;

                // 생성된 iframe 요소를 tips-wrapper에 추가
                tipsWrapper.appendChild(iframeContainer);
            });
        })
        .catch(error => {
            console.error('YouTube 데이터를 가져오는 데 실패했습니다:', error);
        });
};
