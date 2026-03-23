package com.example.project.config;

import cn.dev33.satoken.dao.SaTokenDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Sa-Token 持久层配置：将 Token/Session 统一存入 Redis。
 */
@Configuration
public class SaTokenRedisDaoConfig {

    /**
     * 注册 Sa-Token Redis DAO，实现登录态跨实例共享与重启不丢失。
     */
    @Bean
    public SaTokenDao saTokenDao(RedisTemplate<String, Object> redisTemplate) {
        return new RedisSaTokenDao(redisTemplate);
    }

    /**
     * 基于 RedisTemplate 的 Sa-Token DAO 实现。
     */
    private static final class RedisSaTokenDao implements SaTokenDao {

        /**
         * Redis 读写模板。
         */
        private final RedisTemplate<String, Object> redisTemplate;

        private RedisSaTokenDao(RedisTemplate<String, Object> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        @Override
        public String get(String key) {
            Object value = redisTemplate.opsForValue().get(key);
            return value == null ? null : String.valueOf(value);
        }

        @Override
        public void set(String key, String value, long timeout) {
            setValue(key, value, timeout);
        }

        @Override
        public void update(String key, String value) {
            long timeout = getTimeout(key);
            if (timeout == NOT_VALUE_EXPIRE) {
                return;
            }
            setValue(key, value, timeout);
        }

        @Override
        public void delete(String key) {
            redisTemplate.delete(key);
        }

        @Override
        public long getTimeout(String key) {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return ttl == null ? NOT_VALUE_EXPIRE : ttl;
        }

        @Override
        public void updateTimeout(String key, long timeout) {
            if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                return;
            }
            if (timeout == NEVER_EXPIRE) {
                redisTemplate.persist(key);
                return;
            }
            if (timeout <= NOT_VALUE_EXPIRE) {
                return;
            }
            redisTemplate.expire(key, Duration.ofSeconds(timeout));
        }

        @Override
        public Object getObject(String key) {
            return redisTemplate.opsForValue().get(key);
        }

        @Override
        public void setObject(String key, Object object, long timeout) {
            setValue(key, object, timeout);
        }

        @Override
        public void updateObject(String key, Object object) {
            long timeout = getObjectTimeout(key);
            if (timeout == NOT_VALUE_EXPIRE) {
                return;
            }
            setValue(key, object, timeout);
        }

        @Override
        public void deleteObject(String key) {
            redisTemplate.delete(key);
        }

        @Override
        public long getObjectTimeout(String key) {
            return getTimeout(key);
        }

        @Override
        public void updateObjectTimeout(String key, long timeout) {
            updateTimeout(key, timeout);
        }

        @Override
        public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
            String safePrefix = prefix == null ? "" : prefix;
            String safeKeyword = keyword == null ? "" : keyword;
            String pattern = safePrefix + "*" + safeKeyword + "*";

            Set<String> keys = redisTemplate.keys(pattern);
            if (keys == null || keys.isEmpty()) {
                return Collections.emptyList();
            }

            List<String> all = new ArrayList<>(keys);
            Collections.sort(all);
            if (!sortType) {
                Collections.reverse(all);
            }

            int fromIndex = Math.max(start, 0);
            if (fromIndex >= all.size()) {
                return Collections.emptyList();
            }
            int toIndex = size <= 0 ? all.size() : Math.min(fromIndex + size, all.size());
            return all.subList(fromIndex, toIndex);
        }

        /**
         * 统一写入 Redis 值并处理过期时间语义。
         */
        private void setValue(String key, Object value, long timeout) {
            if (timeout == NEVER_EXPIRE) {
                redisTemplate.opsForValue().set(key, value);
                return;
            }
            if (timeout <= NOT_VALUE_EXPIRE) {
                redisTemplate.opsForValue().set(key, value);
                return;
            }
            redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeout));
        }
    }
}
