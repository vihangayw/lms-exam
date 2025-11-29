package lk.mc.core.dao;

import lk.mc.core.constants.ApplicationConstants;
import lk.mc.core.data.ListResult;
import lk.mc.core.enums.PersistMode;
import lk.mc.core.exceptions.OperationNotSupportException;
import lk.mc.core.exceptions.TsActiveException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.util.*;


/**
 * All DAO should extend this one. This contain the method lk.mc.core to all DAO.
 *
 * @param <C> Entity Implementation Class
 * @param <I> Entity interface
 * @author vihanga
 * @since 26/10/2021
 * MC-lms
 */

@SuppressWarnings("Duplicates")
public abstract class TsActiveDAO<C, I> {
    private static Logger logger = LogManager.getLogger(TsActiveDAO.class);

    private EntityManager entityManager;
    private DataEntryInfo daoMeta;

    private Class<C> beanClass;
    private String implClassName;

    //region Constructor

    public TsActiveDAO(EntityManager em, Class<C> entityImplClass) {
        this.entityManager = em;
        this.beanClass = entityImplClass;

        implClassName = beanClass.getSimpleName();
        logger.info("Entity implementation class name >> " + implClassName);
    }

    //endregion

    //region Getters and Setters

    public DataEntryInfo getDaoMeta() {
        return daoMeta;
    }

    protected void setDaoMeta(DataEntryInfo daoMeta) {
        this.daoMeta = daoMeta;
    }

    //endregion


    /**
     * Store an instance of JPA entity class in database
     *
     * @param entityImpl JPA entity implementation
     * @return insert action related information. For procedure calls, this is the OUT parameter value
     * @throws TsActiveException error
     */
    public C insert(C entityImpl) throws TsActiveException {
        try {
            return insert(entityImpl, null, null, false);
        } catch (OperationNotSupportException e) {
            logger.error("Add operation not support. >> " + e.getMessage(), e);
            throw new TsActiveException(e);
        }
    }

    /**
     * Store a list of JPA entity class in database
     *
     * @param list JPA entities implementation
     * @return insert action related information. For procedure calls, this is the OUT parameter value
     * @throws TsActiveException error
     */
    public List<C> insertList(List<C> list) throws TsActiveException {
        try {
            return insert(list, null, null, false);
        } catch (OperationNotSupportException e) {
            logger.error("Add operation not support. >> " + e.getMessage(), e);
            throw new TsActiveException(e);
        }
    }

    public C insert(C entityImpl, PersistMode persistMode) throws TsActiveException {
        C result = null;
        try {
            logger.info("Inserting entity in database >> " + entityImpl.toString());
            logger.info("Persistence Mode >> " + persistMode);

            switch (persistMode) {
                case JPA_PERSIST:
                    entityManager.persist(entityImpl); //Persist takes an entity instance, adds it to the context and makes that instance managed
                    logger.info("Entity added to database using JPA.");
                    result = entityImpl;
                    break;
                case DB_SP_CALL:
                    DBManager.getInstance().executeNamedProcedure(entityManager, /*daoMeta.getAddSpJpaName()*/
                            null,/* getAddSpParams(entityImpl)*/null, /*daoMeta.hasAddOutPara()*/
                            false);
                    logger.info("Entity added to database using NamedStoredProcedure.", result);
                    break;
                default:
                    logger.error("Unsupported Persistence Mode");
                    break;
            }
        } catch (Exception e) {
            logger.error("Failed to add entity in database >> " + e.getMessage(), e);
            throw new TsActiveException(e);
        }

        return result;
    }

    public List<C> insert(List<C> entities, String spName, Map<String, Object> paramMap, boolean hasOutPara) throws TsActiveException {
        List<C> result = null;
        try {
            logger.info("Inserting entities to database >> " + entities.size());
            logger.info("Persistence Mode >> " + daoMeta.getPersistMode());

            switch (daoMeta.getPersistMode()) {
                case JPA_PERSIST:
                    entityManager.getTransaction().begin();
                    for (C entity : entities) {
                        entityManager.persist(entity);
                        entityManager.flush();
                    }
                    entityManager.getTransaction().commit();
                    logger.info("Entity added to database using JPA.");
                    result = entities;
                    break;
                case DB_SP_CALL:
                    DBManager.getInstance().executeNamedProcedure(entityManager, spName, paramMap, hasOutPara);
                    logger.info("Entity added to database using NamedStoredProcedure.");
                    break;
                default:
                    logger.error("Unsupported Persistence Mode");
                    break;
            }
        } catch (Exception e) {
            logger.error("Failed to insert entity to database >> " + e.getMessage(), e);
            entityManager.getTransaction().rollback();
            throw new TsActiveException(e);
        }

        return result;
    }

    public C insert(C entityImpl, String spName, Map<String, Object> paramMap, boolean hasOutPara) throws TsActiveException {
        C result = null;
        try {
            logger.info("Inserting entity to database >> " + entityImpl.toString());
            logger.info("Persistence Mode >> " + daoMeta.getPersistMode());

            switch (daoMeta.getPersistMode()) {
                case JPA_PERSIST:
                    entityManager.getTransaction().begin();
                    entityManager.persist(entityImpl);
                    entityManager.flush();
                    entityManager.getTransaction().commit();
                    logger.info("Entity added to database using JPA.");
                    result = entityImpl;
                    break;
                case DB_SP_CALL:
                    DBManager.getInstance().executeNamedProcedure(entityManager, spName, paramMap, hasOutPara);
                    logger.info("Entity added to database using NamedStoredProcedure.");
                    break;
                default:
                    logger.error("Unsupported Persistence Mode");
                    break;
            }
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            logger.error("Failed to insert entity to database >> " + e.getMessage(), e);
            throw new TsActiveException(e);
        }

        return result;
    }

    /**
     * Update JPA entity store in database
     * <p>
     * ex:-
     *
     * @param entityImpl JPA entity implementation with updated properties
     * @return update action related information. For procedure call and has the OUT parameter, this is the OUT parameter value
     * @throws TsActiveException error
     */
    public C update(C entityImpl) throws TsActiveException {
        return update(entityImpl, daoMeta.getPersistMode());
    }

    public C update(C entityImpl, PersistMode persistMode) throws TsActiveException {
        C result = null;
        try {
            logger.info("Updating entity in database >> " + entityImpl.toString());
            logger.info("Persistence Mode >> " + persistMode);

            switch (persistMode) {
                case JPA_PERSIST:
                    C entityResponse;
                    entityManager.getTransaction().begin();
                    //Merge creates a new instance of your entity, copies the state from the supplied entity, and makes the new copy managed. The instance you pass in will not be managed
                    entityResponse = entityManager.merge(entityImpl);
                    entityManager.flush();
                    entityManager.getTransaction().commit();
                    if (entityResponse == null) {
                        logger.error("Failed to update entity in database using JPA.");
                        result = entityImpl;
                    } else {
                        logger.info("Entity updated in database using JPA.");
                        logger.info(entityResponse);
                        result = entityImpl;
                    }
                    break;
                case DB_SP_CALL:
                    DBManager.getInstance().executeNamedProcedure(entityManager,
                            /*daoMeta.getEditSpJpaName()*/null, /*getEditSpParams(entityImpl)*/null,
                            /*daoMeta.hasEditOutPara()*/false);
                    logger.info("Entity edited to database using NamedStoredProcedure.");
                    break;
                default:
                    logger.error("Unsupported Persistence Mode");
                    break;
            }
        } catch (Exception e) {
            logger.error("Failed to update entity in database >> " + e.getMessage(), e);
            entityManager.getTransaction().rollback();
            throw new TsActiveException(e);
        }

        return result;
    }

    /**
     * Use to get all entities
     *
     * @return all ListResult object with list of entities
     */
    public ListResult<I> findAll() {
        return findPaginatedSearchResults(0, daoMeta.getDefaultSortColumn(), "");
    }

    /**
     * Used to get entities list
     *
     * @param prmPageNumber  requested page number of result set. Should greater than or equal zero. Return all entities when this is equal to zero
     * @param prmSortColumn  is sort column of result set
     * @param filterCriteria is filter criteria apply for select query
     * @return ListResult - return a list result object
     */
    public ListResult<I> findPaginatedSearchResults(int prmPageNumber, String prmSortColumn, String filterCriteria) {
        ListResult<I> entityListResult = new ListResult<>();
        try {
            int pageNumber = prmPageNumber;
            String sortColumn = prmSortColumn;
            logger.info("Find all paginated results set with given filter criteria");
            logger.info("pageNumber>> " + pageNumber + " |sortColumn>> " + sortColumn + " |filterCriteria>> " + filterCriteria);

            int startIndex, lastIndex;

            if (pageNumber < 0) {
                logger.info("Page Number parameter is negative. Reset to one.");
                pageNumber = 0;
            }

            if (pageNumber > 0) {
                startIndex = ((pageNumber - 1) * ApplicationConstants.RECORDS_PER_PAGE);//+1 if return values are incorrect
                lastIndex = pageNumber * ApplicationConstants.RECORDS_PER_PAGE;
            } else {
                //Set a big value for last index
                startIndex = ApplicationConstants.FIRST_INDEX;
                lastIndex = 999999999;
            }

            if (sortColumn != null && sortColumn.isEmpty()) {
                logger.info("Empty sortColumn retrieved.");

                if (!daoMeta.getDefaultSortColumn().isEmpty()) {
                    logger.info("Reset to default sort column >> " + daoMeta.getDefaultSortColumn());
                    sortColumn = daoMeta.getDefaultSortColumn();
                }
            }

            switch (daoMeta.getPersistMode()) {
                case DB_SP_CALL:
                    entityListResult = executeBaseProcedure(entityManager, daoMeta.getFindAll(), sortColumn, filterCriteria, startIndex, lastIndex);
                    logger.info("Entities Listed NamedStoredProcedure.", entityListResult);
                    break;
                case JPA_PERSIST:
                    entityListResult = list(daoMeta.getFindAll(), sortColumn, filterCriteria, startIndex, lastIndex);

                    logger.info("Entities Listed Using JPA.", entityListResult);
                    break;
                default:
                    logger.error("Unsupported Persistence Mode");
                    break;
            }

        } catch (Exception e) {
            logger.error("Failed to find paginated filtered entity list in database >> " + e.getMessage(), e);
        }

        return entityListResult;
    }


    /**
     * Execute single entity return JPA named query
     *
     * @param jpaQueryName     is the name of NamedQuery
     * @param keyPropertyName  name of key mapping property
     * @param lastIndex        number of results
     * @param startIndex       page number
     * @param keyPropertyValue searching key value
     * @return ListResult entity object which is primary key is equal to given input
     */
    public ListResult<I> list(String jpaQueryName, String keyPropertyName, Object keyPropertyValue, int startIndex, int lastIndex) {
        List response = null;
        try {
            logger.info("Execute list Query >> " + entityManager + "|" + jpaQueryName + "|" + keyPropertyName + "|" + keyPropertyValue);
            logger.info("Execute list Query >> FROM " + startIndex + " TO " + lastIndex);

            Query query = entityManager.createNamedQuery(jpaQueryName, Object.class)
                    .setMaxResults(ApplicationConstants.RECORDS_PER_PAGE)
                    .setFirstResult(startIndex);

            if (query == null) {
                logger.error("Failed to generate JPA named query for get entity by key");
                return null;
            }
            if (keyPropertyName != null)
                query = query.setParameter(keyPropertyName, keyPropertyValue);

            try {
                response = query
                        .getResultList();
            } catch (NoResultException e) {
                logger.error("No entity found for key >> " + keyPropertyName);
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ListResult<>((List<I>) response, response != null ? response.size() : 0);
    }

    /**
     * Execute single entity return JPA named query
     *
     * @param jpaQueryName is the name of NamedQuery
     * @param params       mapped value of string and object
     * @param startIndex   Number which is starting page
     * @param lastIndex    Number which is ending page
     * @return entity object which is primary key is equal to given input
     */
    public ListResult<I> list(String jpaQueryName, Map<String, Object> params,
                              int startIndex, int lastIndex) {
        List response = null;
        try {
            logger.info("Execute list Query >> " + entityManager + "|" + jpaQueryName + "|" + params);
            logger.info("Execute list Query >> FROM " + startIndex + " TO " + lastIndex);
            Query query = entityManager.createNamedQuery(jpaQueryName, Object.class)
                    .setMaxResults(lastIndex)
                    .setFirstResult(startIndex);
            if (query == null) {
                logger.error("Failed to generate JPA named query for get entity by key");
                return null;
            }
            for (String key : params.keySet()) {
                Object o = params.get(key);
                query = query.setParameter(key, o);
                logger.info("Key value pair >> " + key + "|" + o);
            }
            try {
                response = query
                        .getResultList();
            } catch (NoResultException e) {
                logger.error("No entity found for key >> " + e.getMessage());
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return new ListResult<>((List<I>) response, response != null ? response.size() : 0);
    }


    /**
     * Used to get the entity related to primary key
     *
     * @param searchKey key value
     * @return interface
     */
    public I findByKey(Object searchKey) {
        I entity = null;

        try {
            logger.info("Find entity by key >> " + searchKey);
            Object result = DBManager.getInstance().getEntityById(beanClass, entityManager, searchKey);
//            Object result = DBManager.getInstance().getEntityByNamedQuery(entityManager, daoMeta.getFindByKeyQueryJpaName(), daoMeta.getKeyPropertyName(), searchKey);
            entity = (I) result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Return entity by key >> " + entity);

        return entity;
    }

    /**
     * Used to get the entity related to the given field value
     *
     * @param namedQuery JPA named query
     * @param key        key parameter
     * @param value      value parameter
     * @return Interface
     */
    public I findByNamedQuery(String namedQuery, String key, Object value) {
        I entity = null;

        try {
            logger.info("Find by named query >> " + namedQuery);
            Object result = DBManager.getInstance().getEntityByNamedQuery(entityManager, namedQuery, key, value);
            entity = (I) result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Return entity by key >> " + entity);

        return entity;
    }

    /**
     * Used to get the entity related to the given field value
     *
     * @param namedQuery JPA named query
     * @param keys       list of keys
     * @param key        key value
     * @param map        named query parameters
     * @return List of Interface
     */
    public List<I> findByKeyList(String namedQuery, String key, Set<String> keys, HashMap<String, Object> map) {
        List<I> entity = null;

        try {
            logger.info("Find by named query >> " + namedQuery);
            Object result = DBManager.getInstance().getEntityListByNamedQueryByKeys(entityManager,
                    namedQuery,
                    key,
                    keys,
                    map);
            entity = (List<I>) result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Return entities by key >> " + (entity != null ? entity.size() : null));

        return entity;
    }

    public List<C> findByKeyListC(String namedQuery, String key, Set<String> keys, HashMap<String, Object> map) {
        List<C> entity = null;

        try {
            logger.info("Find by named query >> " + namedQuery);
            Object result = DBManager.getInstance().getEntityListByNamedQueryByKeys(entityManager,
                    namedQuery,
                    key,
                    keys,
                    map);
            entity = (List<C>) result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Return entities by key >> " + (entity != null ? entity.size() : entity));

        return entity;
    }


    /**
     * Used to get the entity related to the given set of fields
     *
     * @param namedQuery JPA named query
     * @param params     Query parameter map
     * @return Interface
     */
    public I findByNamedQuery(String namedQuery, Map<String, Object> params) {
        I entity = null;

        try {
            logger.info("Find by named query >> " + namedQuery);
            Object result = DBManager.getInstance().getEntityByNamedQuery(entityManager, namedQuery, params);
            entity = (I) result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Return entity by key >> " + entity);

        return entity;
    }

    /**
     * Used to update the entity related to the given set of fields
     *
     * @param namedQuery JPA named query
     * @param params     Query parameter map
     * @return Interface
     */
    public int updateEntityByNamedQuery(String namedQuery, Map<String, Object> params) {
        I entity = null;

        try {
            logger.info("Find by named query >> " + namedQuery);
            return DBManager.getInstance().updateEntityByNamedQuery(entityManager, namedQuery, params);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Return entity by key >> " + entity);

        return -1;
    }

    /**
     * Used to get the entity count
     *
     * @param namedQuery JPA named query
     * @param params     Query parameter map
     * @return count
     */
    public Integer getCount(String namedQuery, Map<String, Object> params) {
        Integer entity = 0;

        try {
            logger.info("Count by named query >> " + namedQuery);
            entity = DBManager.getInstance().getEntityCount(entityManager, namedQuery, params);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        logger.info("Return entity count >> " + entity);

        return entity;
    }


    /**
     * Execute given view by considering given filter criteria using lk.mc.core sp in data base. Statement sort result set according to given sort order and return only given index related records only.
     *
     * @param em               is the entity manager
     * @param viewName         is the executing base view name
     * @param sortColumn       order the result set using this field. Should be available in view output column list
     * @param filterCriteria   is the basic conditions available in Sql WHERE condition block
     * @param firstRecordIndex used to filter result ser which is ROWNUM is greater than this index
     * @param lastRecordIndex  used to filter result ser which is ROWNUM is less than this index
     * @return result set after executing the view by applying filter criteria and paging criteria
     */
    private ListResult<I> executeBaseProcedure(EntityManager em, String viewName, String sortColumn, String filterCriteria, int firstRecordIndex, int lastRecordIndex) {
        try {
            logger.info("Execute Common SP  >> " + em + "|viewName:" + viewName + "|sortColumn:" + sortColumn + "|filterCriteria:" + filterCriteria + "|firstRecordIndex:" + firstRecordIndex + "|lastRecordIndex:" + lastRecordIndex);


            StoredProcedureQuery query = em.createStoredProcedureQuery("pkg_dc_common.sp_show_range", beanClass);

            if (query == null) {
                logger.error("Failed to generate StoredProcedureQuery for Common SP >> pkg_dc_common.sp_show_range");
                return new ListResult<>(Collections.emptyList(), -1); // TODO [check] return -1 as count when an exception
            }

            logger.info("Define parameters of CommonSP StoredProcedureQuery.");

            query.registerStoredProcedureParameter("ptableorview", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("psortcolumn", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("psearchcriteria", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("pn1", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("pn2", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("prows", Integer.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("pview", beanClass, ParameterMode.REF_CURSOR); //void.class

            logger.info("Assign parameter values of CommonSP StoredProcedureQuery.");

            query.setParameter("ptableorview", viewName);
            query.setParameter("psortcolumn", sortColumn);
            query.setParameter("psearchcriteria", filterCriteria);
            query.setParameter("pn1", firstRecordIndex);
            query.setParameter("pn2", lastRecordIndex);

            logger.info("Ready to execute lk.mc.core sp");
            logger.info("Bean Class :  " + beanClass + "ENtity MAnager : " + em);

            logger.info("-----Query : " + query + "------");
            query.execute();

            logger.info("Executed lk.mc.core sp");

            int totalNoOfRows;

            Object resultingRowCount = query.getOutputParameterValue("prows");
            if (resultingRowCount == null) {

                logger.error("prows Out parameter is null");
                totalNoOfRows = -1;
            } else {
                logger.info("Total number of results for query >> " + resultingRowCount);
                totalNoOfRows = (int) resultingRowCount;
            }

            logger.info("Total number of results (integer value) for query >> " + totalNoOfRows);

            Object resultView = query.getResultList();

            logger.info("Results for query >> " + resultView);

            List<I> response = (List<I>) resultView;

            logger.info("Results after cast for query >> " + response);

            return new ListResult<>(response, totalNoOfRows);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ListResult<>(Collections.emptyList(), -1); // TODO [check] return -1 as count when an exception
        }
    }

    /**
     * Used to delete the entity
     *
     * @param beanClass entity which will delete
     * @return boolean
     * @throws TsActiveException
     */
    public boolean delete(C beanClass) throws TsActiveException {
        try {
            logger.info("Entity to delete >> " + beanClass);
            entityManager.getTransaction().begin();
            entityManager.remove(beanClass);
            entityManager.flush();
            entityManager.getTransaction().commit();
            logger.info("Entity deleted.");
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete entity in database >> " + e.getMessage(), e);
            throw new TsActiveException(e);
        }
    }

    /**
     * Used to execute a query
     *
     * @param namedQuery Named Query
     * @param params     parameters
     * @return boolean
     * @throws TsActiveException - on transaction failure.
     */
    public boolean executeUpdate(String namedQuery, HashMap<String, Object> params) throws TsActiveException {
        EntityTransaction transaction = null;
        try {
            logger.info("Entity to delete >> " + beanClass);
            Query query = entityManager.createNamedQuery(namedQuery);
            if (query == null) {
                logger.error("Failed to generate JPA named query for get entity by key");
                return false;
            }
            if (params != null) {
                for (String key : params.keySet()) {
                    Object o = params.get(key);
                    query = query.setParameter(key, o);
                    logger.info("Key value pair >> " + key + "|" + o);
                }
            }
            transaction = entityManager.getTransaction();
            transaction.begin();
            logger.info("Execute updated result | " + query.executeUpdate());
            entityManager.flush();
            transaction.commit();
            logger.info("Entity updated.");
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Failed to update entity in database >> " + e.getMessage(), e);
            throw new TsActiveException(e);
        }
    }

    /**
     * Execute given named stored procedure
     *
     * @param jpaSpName    is the name property value of the NamedStoredProcedureQuery object..look at your bean in genImpl
     * @param paramMap     HashMap of procedure input parameter list.
     * @param outputParams the list of OUT parameters. If this is null or empty, empty list will return.
     * @return result output parameter map
     */
    public HashMap<String, Object> executeNamedProcedure(String jpaSpName, Map<String, Object> paramMap, String... outputParams) {
        logger.info("Execute Named Stored Procedure  >> " + jpaSpName);
        return DBManager.getInstance().executeNamedProcedure(entityManager, jpaSpName, paramMap, outputParams);
    }
}