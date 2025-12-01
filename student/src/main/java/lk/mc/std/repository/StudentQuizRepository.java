package lk.mc.std.repository;

import lk.mc.std.bean.ExamPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StudentQuizRepository extends JpaRepository<ExamPic, Integer> {

    @Query("SELECT e FROM ExamPic e WHERE e.sqId = :sqId ORDER BY e.addedTime")
    List<ExamPic> findBySqId(@Param("sqId") Integer sqId);

    @Query("SELECT e FROM ExamPic e WHERE e.addedTime < :cutoffDate")
    List<ExamPic> findOlderThan(@Param("cutoffDate") Date cutoffDate);
}

