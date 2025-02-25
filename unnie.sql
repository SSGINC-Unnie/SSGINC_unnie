-- 1. 기존 스키마 삭제 및 새 스키마 생성
drop schema if exists `unnie`;
create schema `unnie`;
use `unnie`;

-- 1. KEYWORD
DROP TABLE IF EXISTS `keyword`;
CREATE TABLE `keyword` (
  `keyword_id` TINYINT NOT NULL AUTO_INCREMENT,
  `keyword` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`keyword_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `keyword` (`keyword`)
VALUES
('친절함'),
('깔끔함'),
('만족도 높음'),
('가격이 합리적'),
('재방문의사');

-- 2. MEMBER
DROP TABLE IF EXISTS `member`;
CREATE TABLE `member` (
  `member_id` INT NOT NULL AUTO_INCREMENT,
  `member_email` VARCHAR(100) NOT NULL COMMENT '이메일 형식',
  `member_pw` VARCHAR(100) NOT NULL COMMENT '영문/숫자/특수문자 조합(8~20자)',
  `member_name` VARCHAR(30) NOT NULL,
  `member_nickname` VARCHAR(20) NOT NULL,
  `member_phone` VARCHAR(30) NOT NULL COMMENT '중복 허용',
  `member_birth` VARCHAR(30) NOT NULL,
  `member_gender` CHAR(1) NOT NULL COMMENT '남성(M) / 여성(F)',
  `member_profile` VARCHAR(255) NOT NULL,
  `member_role` TINYINT NOT NULL DEFAULT 1 COMMENT '0:관리자 / 1: 일반/ 2: 업체',
  `member_state` TINYINT NOT NULL DEFAULT 0 COMMENT '활성화여부(0:활성/1:정지/2:탈퇴)',
  `member_created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `member_updated_at` TIMESTAMP NULL,
  PRIMARY KEY (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `member` 
(`member_email`, `member_pw`, `member_name`, `member_nickname`, `member_phone`, `member_birth`, `member_gender`, `member_profile`,`member_role`)
VALUES
('admin@example.com', 'abcd1234!', '김민수', '숨겨진이름', '010-1111-2222', '1990-01-01', 'M', 'profile1.jpg', 0),
('user1@example.com', 'q1w2e3r4!', '박서연', '푸른바람', '010-2222-3333', '1992-02-02', 'F', 'profile2.jpg', 1),
('shop@example.com', 'qwer1234!', '이도윤', '별빛노래', '010-3333-4444', '1985-03-03', 'F', 'profile3.jpg', 2);

-- 3. SHOP
DROP TABLE IF EXISTS `shop`;
CREATE TABLE `shop` (
  `shop_id` INT NOT NULL AUTO_INCREMENT,
  `shop_name` VARCHAR(30) NOT NULL,
  `shop_location` VARCHAR(100) NOT NULL,
  `shop_registrated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `shop_category` ENUM('헤어샵', '네일샵', '왁싱샵', '에스테틱', '바버샵') NOT NULL,
  `shop_business_time` TIME NOT NULL,
  `shop_tel` VARCHAR(20) NOT NULL,
  `shop_introduction` VARCHAR(255) NOT NULL,
  `shop_closed_day` CHAR(1) NOT NULL,
  `shop_business_number` VARCHAR(15) NOT NULL,
  `shop_representation_name` VARCHAR(10) NOT NULL,
  `shop_created_at` DATETIME NOT NULL,
  `shop_updated_at` TIMESTAMP NULL,
  `shop_member_id` INT NOT NULL,
  `shop_latitude` DOUBLE NULL,
  `shop_longitude` DOUBLE NULL,
  `shop_status` TINYINT NOT NULL DEFAULT 0 COMMENT '0: 대기중 1: 승인',
  PRIMARY KEY (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `shop`
(`shop_name`, `shop_location`, `shop_category`, `shop_business_time`,
 `shop_tel`, `shop_introduction`, `shop_closed_day`, `shop_business_number`, `shop_representation_name`, `shop_created_at`, 
 `shop_member_id`, `shop_latitude`, `shop_longitude`)
VALUES
('언니헤어샵', '서울시 강남구 역삼동 718-9', '헤어샵', '09:00:00', 
 '02-0000-0000', '완성도 높은 커트와 퀄리티 있는 디자인을 위하여 맞춤 커트와, 샴푸 서비스,두피 마사지 서비스, 마무리 커트, 스타일링 5단계로 세심하게 진행 도와드리고 있습니다.', '1', '111-11-11111', '이철수', '2025-02-01 10:00:00', 
 2, 37.5007893, 127.0388223),
('오빠네일샵', '서울시 강남구 논현동 202-7', '네일샵', '10:00:00',
 '02-1111-1111', '저희 샵은 빠르면서도 만족도를 꼼꼼히 느끼고 가실 수 있도록 최선을 다해 섬세하면서도 꼼꼼하게 해드리기위해 항상 노력하고 있답니다!', '2', '222-22-22222', '김철수', '2025-02-02 10:00:00',
 2, 37.5057714, 127.0274314);

-- 4. RECEIPT
DROP TABLE IF EXISTS `receipt`;
CREATE TABLE `receipt` (
  `receipt_id` BIGINT NOT NULL AUTO_INCREMENT,
  `receipt_date` DATETIME NOT NULL,
  `receipt_amount` INT NOT NULL,
  `receipt_business_number` VARCHAR(255) NOT NULL,
  `receipt_approval_number` VARCHAR(255) NOT NULL,
  `receipt_created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `receipt_shop_id` INT NOT NULL,
  `receipt_member_id` INT NOT NULL,
  PRIMARY KEY (`receipt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `receipt`
(`receipt_date`, `receipt_amount`, `receipt_business_number`, `receipt_approval_number`, `receipt_shop_id`, `receipt_member_id`)
VALUES
('2025-02-09 12:00:00', 25000, 12341234, 121212, 1, 1),
('2025-02-10 15:00:00', 70000, 12341234, 121212, 1, 1);

-- 5. REVIEW
DROP TABLE IF EXISTS `review`;
CREATE TABLE `review` (
  `review_id` BIGINT NOT NULL AUTO_INCREMENT,
  `review_member_id` INT NOT NULL,
  `receipt_id` INT NOT NULL,
  `review_image` VARCHAR(300) NOT NULL,
  `review_rate` TINYINT NOT NULL,
  `review_content` VARCHAR(400) NOT NULL,
  `review_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `review_emotional_score` TINYINT NOT NULL,
  PRIMARY KEY (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `review`
(`review_member_id`, `review_image`, `review_rate`, `review_content`, `receipt_id`, `review_emotional_score`)
VALUES
(1, 'review_img1.jpg', 5, '정말 친절했어요 잘 놀다 갑니다!', 1, 9),
(1, 'review_img2.jpg', 4, '만족스러웠습니다!', 2, 8);

-- 6. REVIEW_KEYWORD (복합 PK: AUTO_INCREMENT 미적용)
DROP TABLE IF EXISTS `review_keyword`;
CREATE TABLE `review_keyword` (
  `keyword_id` TINYINT NOT NULL,
  `review_id` BIGINT NOT NULL,
  PRIMARY KEY (`keyword_id`, `review_id`),
  FOREIGN KEY (`keyword_id`) REFERENCES `keyword` (`keyword_id`),
  FOREIGN KEY (`review_id`) REFERENCES `review` (`review_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `review_keyword` (`keyword_id`, `review_id`)
VALUES
(1, 1),
(2, 1),
(3, 2);

-- 7. BOOKMARK (복합 PK: AUTO_INCREMENT 미적용)
DROP TABLE IF EXISTS `bookmark`;
CREATE TABLE `bookmark` (
  `bookmark_member_id` INT NOT NULL,
  `bookmark_shop_id` INT NOT NULL,
  PRIMARY KEY (`bookmark_member_id`, `bookmark_shop_id`),
  FOREIGN KEY (`bookmark_member_id`) REFERENCES `member` (`member_id`),
  FOREIGN KEY (`bookmark_shop_id`) REFERENCES `shop` (`shop_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `bookmark`
(`bookmark_member_id`, `bookmark_shop_id`)
VALUES
(1, 1),
(1, 2);

-- 8. PROCEDURE
DROP TABLE IF EXISTS `procedure`;
CREATE TABLE `procedure` (
  `procedure_id` INT NOT NULL AUTO_INCREMENT,
  `procedure_name` VARCHAR(30) NOT NULL,
  `procedure_price` INT NOT NULL,
  `procedure_designer_id` INT NOT NULL,
  PRIMARY KEY (`procedure_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `procedure`
(`procedure_name`, `procedure_price`, `procedure_designer_id`)
VALUES
('남성 커트', 30000, 1),
('젤네일', 50000, 2);

-- 9. DESIGNER
DROP TABLE IF EXISTS `designer`;
CREATE TABLE `designer` (
  `designer_id` INT NOT NULL AUTO_INCREMENT,
  `designer_thumbnail` VARCHAR(255) NULL,
  `designer_name` VARCHAR(10) NOT NULL,
  `designer_introduction` VARCHAR(255) NULL,
  `designer_shop_id` INT NOT NULL,
  PRIMARY KEY (`designer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `designer`
(`designer_thumbnail`, `designer_name`, `designer_introduction`, `designer_shop_id`)
VALUES
(NULL, '이동욱', '스타일리스트 이동욱 입니다.', 1),
(NULL, '박예은', '네일아티스트 박예은 입니다.', 2);

-- 10. RECEIPT_ITEM
DROP TABLE IF EXISTS `receipt_item`;
CREATE TABLE `receipt_item` (
  `receipt_item_id` BIGINT NOT NULL AUTO_INCREMENT,
  `receipt_id` BIGINT NOT NULL,
  `receipt_item_name` VARCHAR(255) NOT NULL,
  `receipt_item_price` INT NOT NULL,
  `receipt_item_quantity` TINYINT NOT NULL,
  PRIMARY KEY (`receipt_item_id`, `receipt_id`),
  FOREIGN KEY (`receipt_id`) REFERENCES `receipt` (`receipt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `receipt_item` 
(`receipt_id`, `receipt_item_name`, `receipt_item_price`, `receipt_item_quantity`)
VALUES
(1, '커트', 25000, 1),
(2, '염색', 70000, 1);

-- 11. COMMENT
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `comment_id` BIGINT NOT NULL AUTO_INCREMENT,
  `comment_contents` VARCHAR(255) NOT NULL,
  `comment_created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `comment_updated_at` TIMESTAMP NULL,
  `comment_isactive` TINYINT NOT NULL DEFAULT 1 COMMENT '비활성화(0) / 활성화(1)',
  `comment_board_id` INT NOT NULL,
  `comment_parent_id` INT NULL COMMENT '상위 댓글 번호',
  `comment_member_id` INT NOT NULL,
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `comment`
(`comment_contents`, `comment_isactive`, `comment_board_id`, `comment_parent_id`, `comment_member_id`)
VALUES
('첫번째 댓글입니다', 1, 1, 0, 1),
('대댓글입니다', 1, 1, 1, 2);

-- 12. REPORT
DROP TABLE IF EXISTS `report`;
CREATE TABLE `report` (
  `report_id` BIGINT NOT NULL AUTO_INCREMENT,
  `report_target_type` TINYINT NOT NULL COMMENT '게시글(1)/ 댓글(2) / 리뷰(3)',
  `report_target_id` INT NOT NULL COMMENT '신고당한 컨텐츠의 ID',
  `report_reason` VARCHAR(255) NOT NULL,
  `report_reason_detailed` VARCHAR(1200) NULL COMMENT '300자 제한',
  `report_created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `report_resolved_at` TIMESTAMP NULL COMMENT '처리일시',
  `report_status` TINYINT NOT NULL DEFAULT 0 COMMENT '0 : 미처리 / 1: 처리 / 2: 무시',
  `report_member_id` INT NOT NULL,
  PRIMARY KEY (`report_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `report`
(`report_target_type`, `report_target_id`, `report_reason`, `report_reason_detailed`, `report_member_id`)
VALUES
(1, 1, '게시글이 부적절', '욕설이 포함됨', 1),
(2, 2, '댓글이 이상해요', '도배성 댓글', 2);

-- 13. BOARD
DROP TABLE IF EXISTS `board`;
CREATE TABLE `board` (
  `board_id` BIGINT NOT NULL AUTO_INCREMENT,
  `board_title` VARCHAR(100) NOT NULL,
  `board_category` ENUM('공지 있어!', '여기 할인어때?', '나 뭐가 어울려?', '이 꿀팁 들어봤어?', '이템 어때?', '자유게시판') NOT NULL,
  `board_contents` VARCHAR(800) NULL COMMENT '10~800자',
  `board_created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `board_updated_at` TIMESTAMP NULL,
  `board_views` INT NULL,
  `board_thumbnail` VARCHAR(2000) NOT NULL COMMENT '썸네일 이미지',
  `board_author` INT NOT NULL,
  `board_isactive` TINYINT NOT NULL DEFAULT 1 COMMENT 'inactive : 0 / active : 1',
  PRIMARY KEY (`board_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `board`
(`board_title`, `board_category`, `board_contents`, `board_views`, `board_thumbnail`, `board_author`)
VALUES
('공지사항입니다', '공지 있어!', '공지사항 테스트입니다', 10, 'thumb1.jpg', 3),
('자유글입니다', '자유게시판', '안녕하세요! 반갑습니다.', 20, 'thumb2.jpg', 1);

-- 14. MEDIA
DROP TABLE IF EXISTS `media`;
CREATE TABLE `media` (
  `media_id` BIGINT NOT NULL AUTO_INCREMENT,
  `media_target_type` ENUM('SHOP','PROCEDURE','REVIEW','BOARD') NOT NULL COMMENT '업체 / 시술  / 리뷰 / 게시글 / 기타',
  `media_target_id` INT NOT NULL COMMENT '미디어 출력 대상 ID(매장 ID / 상품  ID 등)',
  `media_urn` VARCHAR(100) NOT NULL,
  `media_created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `media_origin_name` VARCHAR(255) NOT NULL,
  `media_changed_name` VARCHAR(255) NOT NULL,
  `media_isactive` TINYINT NOT NULL DEFAULT 1 COMMENT '비활성화(0) / 활성화(1)',
  PRIMARY KEY (`media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `media`
(`media_target_type`, `media_target_id`, `media_urn`, `media_origin_name`, `media_changed_name`)
VALUES
('SHOP', 1, 'shop1_img.jpg','original1.jpg', 'changed1.jpg'),
('BOARD', 1, 'board1_img.jpg','original2.jpg', 'changed2.jpg');

-- 15. NOTIFICATION
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
  `notification_id` BIGINT NOT NULL AUTO_INCREMENT,
  `notification_type` ENUM('좋아요', '댓글', '대댓글', '리뷰', '경고', '업체 승인여부') NOT NULL DEFAULT '좋아요' COMMENT '좋아요 / 댓글 / 대댓글 / 리뷰 / 경고 / 업체 승인여부',
  `notification_contents` VARCHAR(255) NOT NULL,
  `notification_created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `notification_urn` VARCHAR(100) NOT NULL,
  `notification_isread` TINYINT NOT NULL DEFAULT 0 COMMENT '읽지 않음(0) / 읽음(1)',
  `notification_member_id` INT NOT NULL,
  PRIMARY KEY (`notification_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `notification`
(`notification_type`, `notification_contents`,`notification_urn`,`notification_member_id`)
VALUES
('좋아요', '회원님 게시글에 좋아요가 눌렸습니다.','post/1', 1),
('리뷰', '회원님 작성 리뷰에 댓글이 달렸습니다.','review/1', 1);

-- 16. LIKE
DROP TABLE IF EXISTS `like`;
CREATE TABLE `like` (
  `like_id` BIGINT NOT NULL AUTO_INCREMENT,
  `like_target_type` ENUM('BOARD','COMMENT','REVIEW') NOT NULL COMMENT '게시글, 댓글, 리뷰',
  `like_target_id` INT NOT NULL COMMENT '좋아요 대상의 ID(게시글, 댓글, 리뷰 ID)',
  `like_member_id` INT NOT NULL,
  PRIMARY KEY (`like_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `like`
(`like_target_type`, `like_target_id`, `like_member_id`)
VALUES
('BOARD', 1, 2),
('COMMENT', 1, 1);


-- =================================== INSERT ====================================================
        SELECT
            r.report_id AS reportId,
            CASE
                WHEN r.report_target_type = 1 THEN '게시글'
                WHEN r.report_target_type = 2 THEN '댓글'
                WHEN r.report_target_type = 3 THEN '리뷰'
                END AS reportTargetType,
            r.report_target_id AS reportTargetId,
            r.report_member_id AS reportMemberId,
            r.report_reason AS reportReason,
            DATE_FORMAT(r.report_created_at, '%Y-%m-%dT%H:%i:%s') AS reportCreatedAt,
            DATE_FORMAT(r.report_rosolved_at, '%Y-%m-%dT%H:%i:%s') AS reportRosolvedAt,
            CASE
                WHEN r.report_status = 0 THEN '미처리'
                WHEN r.report_status = 1 THEN '처리 완료'
                WHEN r.report_status = 2 THEN '무시'
                END AS status
        FROM report r
        WHERE
            (NULL IS NULL OR r.report_status = 1) AND
            r.report_created_at BETWEEN "2025-01-01" AND "2025-02-25"
        ORDER BY r.report_created_at DESC;
    
