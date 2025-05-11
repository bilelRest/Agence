package tn.rapid_post.agence.sec.repo;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.rapid_post.agence.sec.entity.AppRole;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<AppRole, Long> {
    @EntityGraph(attributePaths = {"permissionList"})
    @Query("SELECT r FROM AppRole r LEFT JOIN FETCH r.permissionList")
    List<AppRole> findAllWithPermissions();

    Optional<AppRole> findByName(String roleName);
}