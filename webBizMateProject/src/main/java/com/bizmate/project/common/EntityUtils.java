package com.bizmate.project.common;

import org.springframework.data.jpa.repository.JpaRepository;

public class EntityUtils {

    private EntityUtils() {
        // 인스턴스 생성 방지
    }

    public static <T> T getEntityOrThrow(JpaRepository<T, Long> repo, Long id, String entityName) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException(entityName + " 정보를 불러올 수 없습니다."));
    }
}