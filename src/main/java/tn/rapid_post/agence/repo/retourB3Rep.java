package tn.rapid_post.agence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.rapid_post.agence.entity.RetourB3;

import java.util.List;
import java.util.Optional;

public interface retourB3Rep extends JpaRepository<RetourB3,Long> {
    @Query("SELECT b FROM RetourB3 b WHERE  b.nomPrenB3Ro LIKE %:name%")
    List<RetourB3> findByNumB3OrNomPrenB3Ro( @Param("name") String name);
//
    Optional<RetourB3> findBynumB3(String numb3);
    List<RetourB3> findByNomPrenB3RoContainingIgnoreCase(String nomPrenB3Ro);



}
