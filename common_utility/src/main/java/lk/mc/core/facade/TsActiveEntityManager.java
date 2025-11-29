package lk.mc.core.facade;


import lk.mc.core.data.ListResult;

import javax.persistence.EntityManager;

/**
 * TsActiveEntityManager is the base interface for back office facades which having API for get a list
 *
 * @param <E> Entity type which is going to managed by extending this interface.
 * @param <K> Type of primary key in entity managed by extending this interface.
 * @author vihanga
 * @since 28/10/2021
 * MC-lms
 */
public interface TsActiveEntityManager<E, K> {
    /**
     * Used to get all entity list
     *
     * @return ListResult object with all entity list contains in database
     */
    ListResult<E> getAllEntityList();

    /**
     * Used to get all paginated entity list
     *
     * @param pageNumber      is the number of page to display data
     * @param sortingProperty is the property which is required to sort before pagination
     * @return ResultList object with entity list which need to display in given page after sorting according to given sorting property
     */
    ListResult<E> getAllPaginatedEntityList(int pageNumber, String sortingProperty);

    /**
     * Used to get entity for given primary key
     *
     * @param id is the primary key value of entity table
     * @return entity which is relevant for given primary key. return null if there is no entity with given key.
     */
    E getEntityByKey(K id);

    /**
     * Used to get filtered results for given custom filter criteria
     *
     * @param filterCriteria is the query string to pass to filter expected results
     * @return ListResult object with all entity list retrieved from database after appending given filter criteria to where clause
     */
    ListResult<E> getEntityListByFilter(String filterCriteria);

    /**
     * Used to get filtered paginated results for given custom filter criteria
     *
     * @param pageNumber      is expected page number of display data
     * @param sortingProperty is the property which is required to sort before pagination
     * @param filterCriteria  is the query string to pass to filter expected results. This should be a valid sql where condition.
     * @return ListResult object with paginated entity list retrieved from database after appending given filter criteria to where clause
     */
    ListResult<E> getPaginatedEntityListByFilter(int pageNumber, String sortingProperty, String filterCriteria);


    /**
     * Injects Entity Manager implementation into database interact layer. Actual database interaction done via this entity manager.
     *
     * @param entityManager is the created entity manager object in transaction handling layer
     */
    void injectEntityManager(EntityManager entityManager);
}
