package com.ssginc.unnie.common.handler;

import com.ssginc.unnie.common.exception.UnnieCategoryInvaildException;
import com.ssginc.unnie.common.exception.UnnieException;
import com.ssginc.unnie.common.util.EnumDescription;
import com.ssginc.unnie.common.util.ErrorCode;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * ENUM 타입을 자동으로 myBatis 가 인식할 수 있도록 변환시켜주는 핸들러
 * @param <T>
 */
public class EnumTypeHandler<T extends Enum<T> & EnumDescription> extends BaseTypeHandler<T> {

    private final Class<T> type; // 리플렉션

    public EnumTypeHandler(Class<T> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getDescription()); // ENUM의 description 값 저장
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromDescription(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromDescription(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromDescription(cs.getString(columnIndex));
    }

    /**
     * DB에서 불러온 `description` 값을 ENUM으로 변환
     */
    private T fromDescription(String description) {
        return Arrays.stream(type.getEnumConstants())
                .filter(e -> e.getDescription().equals(description))
                .findFirst()
                .orElseThrow(() -> new UnnieCategoryInvaildException(ErrorCode.INVALID_CATEGORY));
    }
}
