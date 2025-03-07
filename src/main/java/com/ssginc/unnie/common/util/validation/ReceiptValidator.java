package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.review.dto.ReceiptRequest;
import com.ssginc.unnie.review.mapper.ReceiptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReceiptValidator implements Validator<ReceiptRequest> {

    private final ReceiptMapper receiptMapper;

    /**
     * 영수증 전체 유효성 검증 (Validator 인터페이스 구현)
     */
    @Override
    public boolean validate(ReceiptRequest receiptRequest) {
        if (receiptRequest == null) {
            log.error("ReceiptRequest가 null입니다.");
            return false;
        }

        return validateReceiptExists(receiptRequest.getReceiptId()) &&
                validateReceiptDate(receiptRequest.getReceiptDate()) &&
                validateApprovalNumber(receiptRequest.getReceiptApprovalNumber()) &&
                validateReceiptAmount(receiptRequest.getReceiptAmount()) &&
                validateBusinessNumber(receiptRequest.getReceiptBusinessNumber()) &&
                validateShopName(receiptRequest.getReceiptShopName());
    }

    /**
     * 영수증이 DB에 존재하는지 검증
     */
    private boolean validateReceiptExists(Long receiptId) {
        if (receiptMapper.findReceiptById(receiptId) == null) {
            log.error("유효하지 않은 영수증 ID: {}", receiptId);
            return false;
        }
        return true;
    }

    /**
     * 결제일 기준 30일 이내인지 검증
     */
    private boolean validateReceiptDate(LocalDateTime receiptDate) {
        if (receiptDate == null) {
            log.error("영수증 결제일이 없습니다.");
            return false;
        }
        long daysBetween = ChronoUnit.DAYS.between(receiptDate, LocalDateTime.now());
        if (daysBetween > 30) {
            log.error("결제일({})이 30일을 초과하여 리뷰를 작성할 수 없습니다.", receiptDate);
            return false;
        }
        return true;
    }

    /**
     * 승인번호가 유효한지 검증 (빈 값 여부)
     */
    private boolean validateApprovalNumber(String approvalNumber) {
        if (approvalNumber == null || approvalNumber.trim().isEmpty()) {
            log.error("승인번호가 누락되었습니다.");
            return false;
        }
        return true;
    }

    /**
     * 결제 금액이 0원 초과인지 검증
     */
    private boolean validateReceiptAmount(int receiptAmount) {
        if (receiptAmount <= 0) {
            log.error("결제 금액이 0원 이하입니다. (receiptAmount={})", receiptAmount);
            return false;
        }
        return true;
    }

    /**
     * 사업자 번호 형식 검증
     */
    private boolean validateBusinessNumber(String businessNumber) {
        if (businessNumber == null || !businessNumber.matches("\\d{3}-?\\d{2}-?\\d{5}|\\d{10}")) {
            log.error("사업자 번호 형식이 잘못되었습니다. (businessNumber={})", businessNumber);
            return false;
        }
        return true;
    }

    /**
     * 업체 이름이 비어 있지 않은지 검증
     */
    private boolean validateShopName(String shopName) {
        if (shopName == null || shopName.trim().isEmpty()) {
            log.error("가게 이름이 없습니다.");
            return false;
        }
        return true;
    }
}
