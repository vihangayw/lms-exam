package lk.mc.controller;

import lk.mc.core.exceptions.TsActiveException;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * All the common controller function can be listed here.
 * For instance authentication handling, entity management, etc..
 *
 * @author vihangawicks
 * @since 11/28/21
 * MC-lms
 */
public abstract class BaseController<T> {

    // private static Logger logger = LogManager.getLogger(BaseController.class);
//    protected EntityManagerFactory emf = Persistence
//            .createEntityManagerFactory(ApplicationConstants.PERSISTENCE_UNIT_NAME);

    // region abstract start
    public abstract ResponseEntity add(T beanClass, HttpServletRequest request,
                                       HttpServletResponse response) throws TsActiveException;

    public abstract ResponseEntity update(T beanClass, HttpServletRequest request,
                                          HttpServletResponse response);

    public abstract ResponseEntity getAll(HttpServletRequest request,
                                          HttpServletResponse response);
    // region abstract end

}
