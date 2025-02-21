package com.ssginc.unnie.common.converter;

import com.ssginc.unnie.common.exception.UnnieCategoryInvaildException;
import com.ssginc.unnie.common.util.EnumDescription;
import com.ssginc.unnie.common.util.ErrorCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Spring에서 컨버터를 자동으로 감지하여
 * 모든 ENUM을 description 값 기준으로 동적으로 주입할 수 있도록 하는 팩토리
 */
@Component
public final class EnumDescriptionConverterFactory implements ConverterFactory<String, EnumDescription> {

    @Override
    public <T extends EnumDescription> Converter<String, T> getConverter(Class<T> targetType) {
        return new EnumDescriptionConverter(targetType);
    }

    /**
     * 모든 ENUM을 'description' 값을 기준으로 변환하는 범용 Converter
     *
     * @param <T>
     */
    public class EnumDescriptionConverter<T extends Enum<T> & EnumDescription> implements Converter<String, T> {

        private final Class<T> enumType;

        public EnumDescriptionConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            return Arrays.stream(enumType.getEnumConstants())
                    .filter(e -> e.getDescription().equals(source))
                    .findFirst()
                    .orElseThrow(() -> new UnnieCategoryInvaildException(ErrorCode.INVALID_CATEGORY));
        }
    }
}
