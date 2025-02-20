package com.ssginc.unnie.media;

import com.ssginc.unnie.media.mapper.MediaMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MyBatisConfigTest {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void testSqlSessionFactory() {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            Object mapper = session.getMapper(MediaMapper.class);
            assertNotNull(mapper, "MyBatis가 MediaMapper를 찾지 못했습니다.");
        }
    }
}
