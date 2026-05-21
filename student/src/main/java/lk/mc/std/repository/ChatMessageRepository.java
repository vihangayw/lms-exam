package lk.mc.std.repository;

import lk.mc.std.bean.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findBySqidOrderByAddedTimeAsc(@Param("sqid") Integer sqid);

    @Query("SELECT COUNT(c) FROM ChatMessage c WHERE c.sqid = :sqid AND c.fromAdmin = false")
    long countStudentMessagesBySqid(@Param("sqid") Integer sqid);
}
