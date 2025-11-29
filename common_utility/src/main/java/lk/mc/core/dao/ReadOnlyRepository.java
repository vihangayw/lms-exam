package lk.mc.core.dao;


import org.jobrunr.storage.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

/**
 * All standard lk.mc.report.repository definitions provided by Spring Data JPA support read and write operations.
 * If you want to create a read-only lk.mc.report.repository, you need to define it yourself. But don’t worry,
 * you don’t need to provide any implementation for the read operation. You only need to define an interface,
 * and you can use all standard methods provided by one of Spring Data JPA’s standard repositories.
 * <p>
 * I always like to define my read-only repositories in 2 steps. I first create a reusable ReadOnlyRepository definition
 * that I then extend and customize for each immutable entity class.
 * <b><i>A Generic Read-Only Repository</i></b>
 * <p>
 * The 2 things you need to do to create your own lk.mc.report.repository definition are defining an interface that extends the
 * Repository interface and copying a few method definitions from Spring Data JPA’s standard repositories.
 *
 * @param <T>  Generic Class
 * @param <ID> ID
 * @author vihangawicks
 * @since 14/07/22
 * MC-lms
 */
@NoRepositoryBean
public interface ReadOnlyRepository<T, ID> extends Repository<T, ID> {

    List<T> findAll();

    List<T> findAll(Sort sort);

    Page<T> findAll(Pageable pageable);

    Optional<T> findById(ID id);

    long count();
}