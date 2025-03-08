package com.warehub.warehub.infrastructure.warehouse.repository;

import com.warehub.warehub.entity.Role;
import com.warehub.warehub.entity.User;
import com.warehub.warehub.entity.Warehouse;
import com.warehub.warehub.entity.WarehouseAdmin;
import com.warehub.warehub.infrastructure.admin.dto.UserAdminDetailResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseAdminRepository extends JpaRepository<WarehouseAdmin, Long> {
    Optional<WarehouseAdmin> findByUserAssigneeId(Long userId);
    List<WarehouseAdmin> findByWarehouseId(Long warehouseId);

    @Query("""
    SELECT wa FROM WarehouseAdmin wa
    JOIN FETCH wa.warehouse w
    WHERE w.deletedAt IS NULL
    """)
    List<WarehouseAdmin> findAllWarehouseAdminsWithWarehouse();

    @Query("""
    SELECT new com.warehub.warehub.infrastructure.admin.dto.UserAdminDetailResponseDTO(
        u.id, u.username, u.email, u.fullname, u.phoneNumber, u.profileImageUrl, 
        COALESCE(wa.warehouse.id, -1), 
        COALESCE(wa.userAssigner.id, -1),
        COALESCE(w.name, ''), 
        COALESCE(userAssigner.email, '')
    )
    FROM User u
    LEFT JOIN WarehouseAdmin wa ON wa.userAssignee.id = u.id
    LEFT JOIN Warehouse w ON wa.warehouse.id = w.id
    LEFT JOIN User userAssigner ON wa.userAssigner.id = userAssigner.id
    WHERE u.role.id = :roleId AND u.deletedAt IS NULL
    AND (:selectNotAssigned = false OR wa.id IS NULL)
    """)
    List<UserAdminDetailResponseDTO> findAllWarehouseAdmins(@Param("roleId") Long roleId, @Param("selectNotAssigned") boolean selectNotAssigned);

    @Query("""
    SELECT new com.warehub.warehub.infrastructure.admin.dto.UserAdminDetailResponseDTO(
        u.id, u.username, u.email, u.fullname, u.phoneNumber, u.profileImageUrl, 
        wa.warehouse.id, 
        wa.userAssigner.id,
        w.name, 
        userAssigner.email
    )
    FROM WarehouseAdmin wa
    JOIN wa.userAssignee u
    JOIN wa.warehouse w
    LEFT JOIN wa.userAssigner userAssigner
    WHERE wa.warehouse.id = :warehouseId
    """)
    List<UserAdminDetailResponseDTO> findAdminsByWarehouseId(@Param("warehouseId") Long warehouseId);
}
