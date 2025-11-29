package lk.mc.std.repository;

import lk.mc.std.bean.ExamPic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentQuizRepository extends JpaRepository<ExamPic, Integer> {

}

