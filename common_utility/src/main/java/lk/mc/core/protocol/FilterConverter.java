package lk.mc.core.protocol;

import com.google.gson.annotations.SerializedName;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author vihanga
 * @since 28/10/2021
 * MC-lms
 */
public class FilterConverter {

    private static Logger logger = LogManager.getLogger(FilterConverter.class);

    /**
     * convert where close form json tags to db columns (call from MessageHelper-getList methods)
     *
     * @param filter     - filter criteria from client
     * @param bean       - bean class (ex: ExchangeBean.class)
     * @param record     - record bean class (ex: ExchangeRecordBean.class)
     * @param nameFields - (optional) map properties name from bean class to record bean class
     * @return - converted filter criteria with real db column names.
     */
    public static String getConvertedFilter(String filter, Class bean, Class record, Map<String, String> nameFields) {
        String genFilter = filter + "";
        // Tag decorator should be :TEST_TEST:
        Matcher replaceMatcher = Pattern.compile(":[A-Z0-9_]+:").matcher(filter);
        while (replaceMatcher.find()) {
            // if any matching found
            int start = replaceMatcher.start();
            int end = replaceMatcher.end();
            String tag = filter.substring(start + 1, end - 1); // select tag that will be replaced
            boolean updateField = false;
            for (Field recordField : record.getDeclaredFields()) {
                // iterator through all record bean fields
                SerializedName sn = recordField.getAnnotation(SerializedName.class);
                if (sn.value().equals(tag)) {
                    // That field matches with the tag
                    for (Field beanFields : bean.getDeclaredFields()) {
                        // iterator through all entity bean fields
                        String newName;
                        if (nameFields != null && nameFields.containsKey(beanFields.getName())) {
                            // when the filed names are not matched by default, find it in the hash map
                            newName = nameFields.get(beanFields.getName());
                        } else {
                            // fields name are match in two classes by default
                            newName = beanFields.getName();
                        }
                        if (newName.equals(recordField.getName())) {
                            Column c = beanFields.getAnnotation(Column.class);
                            logger.info("replaced tag :" + tag + " to --> " + c.name());
                            genFilter = genFilter.replaceFirst(":" + tag + ":", c.name());
                            updateField = true;
                            break;
                        }
                    }
                    if (updateField) break;
                }
            }
            if (!updateField) {
                // can be no matching fields -> update the hash map correctly
                logger.info("this tag is not replaced >> " + tag);
            }
        }
        return genFilter;
    }

    public static String getConvertedFilter(String filter, Class bean, Class record) {
        return FilterConverter.getConvertedFilter(filter, bean, record, null);
    }
}