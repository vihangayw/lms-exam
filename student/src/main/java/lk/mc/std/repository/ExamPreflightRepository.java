package lk.mc.std.repository;

import lk.mc.std.bean.ExamPreflight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface ExamPreflightRepository extends JpaRepository<ExamPreflight, Integer> {

    @Query("SELECT e FROM ExamPreflight e WHERE e.qrcode = :qrcode")
    Optional<ExamPreflight> findByQrcode(@Param("qrcode") String qrcode);

    @Query("SELECT e FROM ExamPreflight e WHERE e.reference = :reference")
    Optional<ExamPreflight> findByReference(@Param("reference") String reference);
}

