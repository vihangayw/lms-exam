package lk.mc.std.repository;

import lk.mc.std.bean.Configs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * TellerSessionRepository handle all the immutable transactions between vw_current_teller_sessions view.
 *
 * @author vihangawicks
 * @since 26/09/22
 * MC-lms
 */
@Repository
@Transactional
public interface ConfigRepository extends JpaRepository<Configs, String> {

}
