package lk.mc.core.dao;

import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import java.util.*;

/**
 * DBManager contains generic utility methods relevant for JPA activities
 *
 * @author vihanga
 * @since 26/10/2021
 * MC-lms
 */
@SuppressWarnings("Duplicates")
public class DBManager {
    private static DBManager instance = null;
    private static Logger logger = LogManager.getLogger(DBManager.class);

    private DBManager() {
        //Left empty intentionally
    }

    public static DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }

        return instance;
    }

    /**
     * @param em         Entity Manager instance for JPA entity implementation
     * @param jpaSpName  is the name property value of the NamedStoredProcedureQuery object..look at your bean in genImpl
     * @param parameters HashMap of procedure input parameter list.
     * @param hasOutPara indicate is it have an OUT parameter. If this is true, OUT parameter should be ApplicationConstants.DEFAULT_SP_OUT_PARA_NAME
     * @return
     */
    public String executeNamedProcedure(EntityManager em, String jpaSpName, Map<String, Object> parameters,
                                        boolean hasOutPara) {
        logger.info("> Has output parameters | " + hasOutPara);
        return String.valueOf(executeNamedProcedure(em, jpaSpName, parameters));
    }

    /**
     * Execute given named stored procedure
     *
     * @param em            Entity Manager instance for JPA entity implementation
     * @param jpaSpName     is the name property value of the NamedStoredProcedureQuery object..look at your bean in genImpl
     * @param parameters    HashMap of procedure input parameter list.
     * @param outParameters the list of OUT parameters. If this is null or empty, empty list will return.
     * @return result output parameter map
     */
    public HashMap<String, Object> executeNamedProcedure(EntityManager em, String jpaSpName, Map<String, Object> parameters, String... outParameters) {
        try {
            logger.info("Execute Named Stored Procedure  >> " + em + "|" + jpaSpName + "|" + parameters);

            StoredProcedureQuery query = em.createNamedStoredProcedureQuery(jpaSpName);

            if (query == null) {
                logger.error("Failed to GET Named Stored Procedure >> " + jpaSpName);
                return null;
            }

            logger.info("Assign " + parameters.size() + " parameter(s) to " + jpaSpName);

            parameters.keySet().forEach(i -> query.setParameter(i, parameters.get(i) == null ? "" : parameters.get(i)));

            logger.info("Ready to execute named stored procedure");

            query.execute();

            logger.info("Executed named stored procedure");

            val map = new HashMap<String, Object>();

            logger.info("Output Parameters >> " + Arrays.toString(outParameters));

            if (outParameters != null && outParameters.length > 0) {
                for (String outParam : outParameters) {
                    Object result = query.getOutputParameterValue(outParam);
                    if (result == null) {
                        logger.error("Out parameter is null | " + outParam);
                    }
                    map.put(outParam, result);
                }
            }

            logger.info("SP OUT Parameter Value >> " + map.toString());

            return map;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Execute JPA named query return single entity
     * <p>
     * ex:-
     * When we want to find a customer by email.
     *
     * @param em               Entity Manager instance for JPA entity implementation
     * @param jpaQueryName     is the name of NamedQuery
     * @param keyPropertyName  name of key mapping property
     * @param keyPropertyValue searching key value
     * @return entity object which is primary key is equal to given input
     */
    public Object getEntityByNamedQuery(EntityManager em, String jpaQueryName, String keyPropertyName, Object keyPropertyValue) {
        Object response = null;

        try {
            logger.info("Execute Named Query  >> " + em + "|" + jpaQueryName + "|" + keyPropertyName + "|" + keyPropertyValue);

            Query queryForFilter = em.createNamedQuery(jpaQueryName);
            if (queryForFilter == null) {
                logger.error("Failed to generate JPA named query for get entity by key");
                return null;
            }

            queryForFilter = queryForFilter.setParameter(keyPropertyName, keyPropertyValue);

            try {
                response = queryForFilter.setMaxResults(1).getSingleResult();
            } catch (NoResultException e) {
                logger.error("No entity found for key >> " + keyPropertyName + "|" + keyPropertyValue);
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return response;
    }

    /**
     * Execute JPA named query return entity list
     * <p>
     * ex:-
     * If we want to see ticket list which are belong to same category and it's status should be '0'
     *
     * @param em           Entity Manager instance for JPA entity implementation
     * @param jpaQueryName is the name of NamedQuery
     * @return entity object which is primary key is equal to given input
     */
    public List getEntityListByNamedQueryByKeys(EntityManager em, String jpaQueryName, String keyPropertyName,
                                                Set<String> keys, HashMap<String, Object> params) {
        List response = null;

        try {
            if (keys != null)
                logger.info("Execute Named Query  >> " + em + "|" + jpaQueryName + "|" + keyPropertyName + "|"
                        + Arrays.toString(keys.toArray()));

            Query queryForFilter = em.createNamedQuery(jpaQueryName);
            if (queryForFilter == null) {
                logger.error("Failed to generate JPA named query for get entity list by keys");
                return null;
            }
            if (params != null) {
                logger.info("Assign " + params.size() + " parameter(s) to " + jpaQueryName);

                for (String key : params.keySet()) {
                    Object o = params.get(key);
                    queryForFilter = queryForFilter.setParameter(key, o);
                    logger.info("Key value pair >> " + key + "|" + o);
                }
            }

            if (keys != null && keyPropertyName != null)
                queryForFilter = queryForFilter.setParameter(keyPropertyName, keys);

            try {
                response = queryForFilter.getResultList();
            } catch (NoResultException e) {
                logger.error("No entities found for key >> " + keyPropertyName);
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return response;
    }

    /**
     * Execute update entity return integer value
     * <p>
     * ex:-
     * Update the status of the ticket, 1 to 4.
     *
     * @param em           Entity Manager instance for JPA entity implementation
     * @param jpaQueryName is the name of NamedQuery
     * @param params       Query parameter map
     * @return integer
     */
    public int updateEntityByNamedQuery(EntityManager em,
                                        String jpaQueryName,
                                        Map<String, Object> params) {

        try {
            logger.info("Execute Named Query Map >> " + em + "|" + jpaQueryName);

            Query queryForFilter = em.createNamedQuery(jpaQueryName);
            if (queryForFilter == null) {
                logger.error("Failed to generate JPA named query for update entity by key");
                return -1;
            }

            for (String key : params.keySet()) {
                Object o = params.get(key);
                queryForFilter = queryForFilter.setParameter(key, o);
                logger.info("Key value pair >> " + key + "|" + o);
            }

            try {
                em.getTransaction().begin();
                int res = queryForFilter.executeUpdate();
                em.flush();
                em.getTransaction().commit();
                return res;
            } catch (Exception e) {
                logger.error("Update failed >> " + jpaQueryName);
                return -1;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return -1;
    }

    /**
     * Execute JPA named query return single entity
     * <p>
     * ex:-
     * Find the counter entity by counter name and branch id.
     *
     * @param em           Entity Manager instance for JPA entity implementation
     * @param jpaQueryName is the name of NamedQuery
     * @param params       Query parameter map
     * @return entity object which is primary key is equal to given input
     */
    public Object getEntityByNamedQuery(EntityManager em, String jpaQueryName, Map<String, Object> params) {
        Object response = null;

        try {
            logger.info("Execute Named Query Map >> " + em + "|" + jpaQueryName);

            Query queryForFilter = em.createNamedQuery(jpaQueryName);
            if (queryForFilter == null) {
                logger.error("Failed to generate JPA named query for get entity by key");
                return null;
            }

            for (String key : params.keySet()) {
                Object o = params.get(key);
                queryForFilter = queryForFilter.setParameter(key, o);
                logger.info("Key value pair >> " + key + "|" + o);
            }

            try {
                response = queryForFilter.getSingleResult();
            } catch (NoResultException e) {
                logger.error("No entity found for query >> " + jpaQueryName);
                return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return response;
    }

    /**
     * Execute JPA named query return entity counter
     * <p>
     * ex:-
     * Get branch list by customer id and level, then count items of that list.
     *
     * @param em           Entity Manager instance for JPA entity implementation
     * @param jpaQueryName is the name of NamedQuery
     * @param params       Query parameter map
     * @return the entity count
     */
    public Integer getEntityCount(EntityManager em, String jpaQueryName, Map<String, Object> params) {
        Integer response = 0;

        try {
            logger.info("Execute Named Query Map >> " + em + "|" + jpaQueryName);

            Query queryForFilter = em.createNamedQuery(jpaQueryName);
            if (queryForFilter == null) {
                logger.error("Failed to generate JPA named query for get entity count");
                return null;
            }

            for (String key : params.keySet()) {
                Object o = params.get(key);
                queryForFilter = queryForFilter.setParameter(key, o);
                logger.info("Key value pair >> " + key + "|" + o);
            }

            return queryForFilter.getResultList().size();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return response;
    }

    /**
     * Find an entity by id
     * <p>
     * ex:-
     * Get Customer entity by ID.
     *
     * @param cls              Bean entity class
     * @param em               Entity Manager instance for JPA entity implementation
     * @param keyPropertyValue searching key value
     * @return entity object which is primary key is equal to given input
     */
    public Object getEntityById(Class cls, EntityManager em, Object keyPropertyValue) {

        try {
            logger.info("Find by key " + "| " + keyPropertyValue);

            return em.find(cls, keyPropertyValue);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

}
