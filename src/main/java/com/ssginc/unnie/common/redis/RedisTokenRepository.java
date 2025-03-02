package com.ssginc.unnie.common.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *  RedisToken 엔티티에 대한 기본 CRUD 작업을 제공하는 Repository
 */
@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {
}
