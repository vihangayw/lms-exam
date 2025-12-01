package lk.mc.std.repository;

import lk.mc.std.bean.ExamPreflight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamPreflightRepository extends JpaRepository<ExamPreflight, Integer> {

    @Query("SELECT e FROM ExamPreflight e WHERE e.qrcode = :qrcode")
    Optional<ExamPreflight> findByQrcode(@Param("qrcode") String qrcode);

    @Query("SELECT e FROM ExamPreflight e WHERE e.reference = :reference")
    Optional<ExamPreflight> findByReference(@Param("reference") String reference);

    @Modifying
    @Transactional
    @Query("UPDATE ExamPreflight e SET e.admin = ?1 WHERE e.qrcode = ?2")
    void reupload(String name, String qrcode);

    @Query("SELECT e FROM ExamPreflight e WHERE e.addedTime < :cutoffDate")
    List<ExamPreflight> findOlderThan(@Param("cutoffDate") Date cutoffDate);
}

