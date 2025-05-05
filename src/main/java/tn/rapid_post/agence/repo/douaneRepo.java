package tn.rapid_post.agence.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.rapid_post.agence.entity.Douane;

import java.time.LocalDate;
import java.util.List;

public interface douaneRepo extends JpaRepository<Douane,Long> {
    @Query("SELECT d FROM Douane d WHERE d.delivered = true AND d.dateSortie BETWEEN ?1 AND ?2")
    List<Douane> findBetweenDates(LocalDate startDate, LocalDate endDate);

    List<Douane> findByPrintedFalse();

    Douane findByNumColis(String num);

    Douane findByBloc(String bloc);
//    @Query("SELECT d FROM Douane d WHERE d.delivered = false AND d.dateSortie BETWEEN ?1 AND ?2 OR d.dateSortie BETWEEN ?1 AND ?2")
//
//    List<Douane> findBetweenDatesAndDeliverdTrue(LocalDate date1, LocalDate date2);
}
