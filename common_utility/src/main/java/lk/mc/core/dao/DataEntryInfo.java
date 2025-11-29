package lk.mc.core.dao;

import lk.mc.core.enums.PersistMode;


/**
 * DataEntryInfo class contains meta data for manipulate master data.
 * Base {@link TsActiveDAO} implement generic actions using property values of this instance.
 *
 * @author vihanga
 * @since 27/10/2021
 * MC-lms
 */
public class DataEntryInfo {

    private String findAll;
    private String defaultSortColumn;
    private PersistMode persistMode = PersistMode.JPA_PERSIST;
    private String keyPropertyName;
    private String findByKeyQueryJpaName;

    //start constructor region
    public DataEntryInfo(String implClassName) {
        this(implClassName + ".findAll", implClassName + ".findById");
    }

    public DataEntryInfo(String findAll, String findByKeyQueryJpaName) {
        this.findAll = findAll;
        this.defaultSortColumn = defaultSortColumn;
        this.findByKeyQueryJpaName = findByKeyQueryJpaName;
    }

    //endregion

    //region Getters and Setters

    public String getFindAll() {
        return findAll;
    }

    public String getDefaultSortColumn() {
        return defaultSortColumn;
    }

    public PersistMode getPersistMode() {
        return persistMode;
    }

    public void setPersistMode(PersistMode persistMode) {
        this.persistMode = persistMode;
    }

    public String getKeyPropertyName() {
        return keyPropertyName;
    }

    public void setKeyPropertyName(String keyPropertyName) {
        this.keyPropertyName = keyPropertyName;
    }

    public String getFindByKeyQueryJpaName() {
        return findByKeyQueryJpaName;
    }

    //endregion

}
