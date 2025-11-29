package lk.mc.core.facade;

/**
 * TsActiveDataEntryManager is the base interface for back office data entry facades
 *
 * @param <E> Entity type which is going to managed by extending this interface.
 * @param <K> Type of primary key in entity managed by extending this interface.
 * @author vihanga
 * @since 28/10/2021
 * MC-lms
 */
public interface TsActiveDataEntryManager<E, K> extends TsActiveEntityManager<E, K> {
    /**
     * Insert entity information into database
     *
     * @param entity contains new entity information
     * @return added entity meta information. Most probably this may contains the auto generated private key information.
     */
    Object addEntity(E entity);

    /**
     * Update entity information in database
     *
     * @param entity contains updated information which is required to persist
     * @return update action related information.
     */
    Object updateEntity(E entity);

    /**
     * Used to get a created dummy entity object
     *
     * @return dummy object without any property values
     */
    E getDummyEntity();
}
