package com.warehub.warehub.infrastructure.customerOrders.specification;

import com.warehub.warehub.entity.CustomerOrder;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;

public class CustomerOrderSpecification {

    public static Specification<CustomerOrder> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) ->
                (userId == null) ? null : criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<CustomerOrder> hasStatusId(Long statusId) {
        return (root, query, criteriaBuilder) ->
                (statusId == null) ? null : criteriaBuilder.equal(root.get("orderStatus").get("id"), statusId);
    }

    public static Specification<CustomerOrder> hasSearchQuery(String searchQuery) {
        return (root, query, criteriaBuilder) ->
                (searchQuery == null || searchQuery.isBlank()) ? null :
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("invoiceCode")),
                                "%" + searchQuery.toLowerCase() + "%");
    }

    public static Specification<CustomerOrder> hasStartDate(OffsetDateTime startDate) {
        return (root, query, criteriaBuilder) ->
                (startDate == null) ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
    }

    public static Specification<CustomerOrder> hasEndDate(OffsetDateTime endDate) {
        return (root, query, criteriaBuilder) ->
                (endDate == null) ? null : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
    }

}
