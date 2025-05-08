package tn.rapid_post.agence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.rapid_post.agence.entity.Douane;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface douaneRepo extends JpaRepository<Douane,Long> {
    @Query("SELECT d FROM Douane d WHERE d.delivered = true AND d.dateSortie BETWEEN ?1 AND ?2")
    List<Douane> findBetweenDates(LocalDate startDate, LocalDate endDate);

    List<Douane> findByPrintedFalse();

    Douane findByNumColis(String num);

    Douane findByBloc(String bloc);

    List<Douane> findByDeliveredTrue();
    List<Douane> findByDeliveredFalse();

    Optional<Douane> findBySequence(String sequence);


    @Query("SELECT d FROM Douane d WHERE " +
            "LOWER(d.numColis) LIKE :key OR " +
            "LOWER(d.nom) LIKE :key OR " +
            "CAST(d.sequence AS string) LIKE :key OR " +
            "LOWER(d.bloc) LIKE :key")
    List<Douane> searchMultiFields(@Param("key") String key);

    @Query("SELECT d FROM Douane d WHERE d.delivered = :etat AND (" +
            "LOWER(d.numColis) LIKE :key OR " +
            "LOWER(d.nom) LIKE :key OR " +
            "CAST(d.sequence AS string) LIKE :key OR " +
            "LOWER(d.bloc) LIKE :key)")
    List<Douane> searchMultiFieldsByDelivered(@Param("key") String key, @Param("etat") boolean etat);


//    List<Douane> findByValidatedTrue();
//
//    List<Douane> findByValidatedFalse();
//    @Query("SELECT d FROM Douane d WHERE d.delivered = false AND d.dateSortie BETWEEN ?1 AND ?2 OR d.dateSortie BETWEEN ?1 AND ?2")
//
//    List<Douane> findBetweenDatesAndDeliverdTrue(LocalDate date1, LocalDate date2);
}
