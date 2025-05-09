package tn.rapid_post.agence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.rapid_post.agence.entity.B3;
import tn.rapid_post.agence.entity.RetourB3;

import java.util.List;

public interface b3Repo extends JpaRepository<B3,Long> {
    boolean existsByNumB3(String b3);
    List<B3> findByNotifiedFalse();
    B3 findByNumB3(String b);
    B3 findByIdB3(long b);
    @Query("SELECT b FROM B3 b WHERE b.nom LIKE %:name%")
    List<B3> findByNumB3RoOrNomPrenB3Ro( @Param("name") String name);
}
