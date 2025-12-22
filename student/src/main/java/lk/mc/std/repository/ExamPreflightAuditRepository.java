package lk.mc.std.repository;

import lk.mc.std.bean.ExamPreflightAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface ExamPreflightAuditRepository extends JpaRepository<ExamPreflightAudit, Integer> {

    @Query("SELECT e FROM ExamPreflightAudit e WHERE e.qr = :qr ORDER BY e.addedTime")
    List<ExamPreflightAudit> findByQr(@Param("qr") String qr);

    @Query("SELECT e FROM ExamPreflightAudit e WHERE e.sqid = :sqid ORDER BY e.addedTime")
    List<ExamPreflightAudit> findBySqid(@Param("sqid") Integer sqid);

    @Query("SELECT e FROM ExamPreflightAudit e WHERE e.qr = :qr or e.sqid = :sqid" +
            " ORDER BY e.addedTime")
    List<ExamPreflightAudit> findBySqid(@Param("qr") String qr, @Param("sqid") Integer sqid);

    @Query("SELECT e FROM ExamPreflightAudit e WHERE e.addedTime < :cutoffDate")
    List<ExamPreflightAudit> findOlderThan(@Param("cutoffDate") Date cutoffDate);
}

