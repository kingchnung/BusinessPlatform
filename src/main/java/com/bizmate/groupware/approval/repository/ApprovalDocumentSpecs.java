package com.bizmate.groupware.approval.repository;

import com.bizmate.groupware.approval.domain.document.ApprovalDocuments;
import com.bizmate.groupware.approval.domain.document.DocumentStatus;
import com.bizmate.groupware.approval.domain.document.DocumentType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ApprovalDocumentSpecs {

    public static Specification<ApprovalDocuments> hasStatus(DocumentStatus status) {
        return (root, query, cb) -> (status == null) ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<ApprovalDocuments> titleContains(String keyword) {
        return (root, query, cb) ->
                (keyword == null || keyword.isBlank()) ? null : cb.like(root.get("title"), "%" + keyword + "%");
    }

    public static Specification<ApprovalDocuments> createdBetween(LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if(from != null && to != null) {
                return cb.between(root.get("createdAt"), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), to);
            }
            return null;
        };
    }

    public static Specification<ApprovalDocuments> notDeleted() {
        return (root, query, cb) ->
                cb.notEqual(root.get("status"), DocumentStatus.DELETED);
    }

    public static Specification<ApprovalDocuments> hasDocType(DocumentType type) {
        return (root, q, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("docType"), type);
    }
}
