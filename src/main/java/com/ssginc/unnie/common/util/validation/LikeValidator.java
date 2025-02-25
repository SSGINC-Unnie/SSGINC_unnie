package com.ssginc.unnie.common.util.validation;

import com.ssginc.unnie.common.exception.UnnieLikeException;
import com.ssginc.unnie.common.util.ErrorCode;
import com.ssginc.unnie.like.dto.LikeRequest;
import com.ssginc.unnie.like.vo.LikeTargetType;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class LikeValidator implements Validator<LikeRequest> {

    @Override
    public boolean validate(LikeRequest like) {

        if (like == null || like.getLikeTargetId() < 1){
            throw new UnnieLikeException(ErrorCode.LIKE_NOT_FOUND);
        }

        if (!isValidTargetType(like.getLikeTargetType())){
            throw new UnnieLikeException(ErrorCode.LIKE_INVALID_TARGET_TYPE);
        }

        return true;
    }

    // 유효성 검증 메서드 추가
    private boolean isValidTargetType(String value) {
        return Arrays.stream(LikeTargetType.values())
                .map(LikeTargetType::name)
                .anyMatch(v -> v.equalsIgnoreCase(value));
    }
}
