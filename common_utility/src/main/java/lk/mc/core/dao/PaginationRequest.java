package lk.mc.core.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.mc.core.util.TsStringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * @author vihangawicks
 * @created 12/23/22
 * MC-lms - project-lms
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationRequest implements Serializable {

    private int page;
    private int size;

    private Sorting sort;
    private String searchText;
    private String key;

    @Override
    public String toString() {
        return "PaginationRequest{" +
                "page=" + page +
                ", size=" + size +
                ", sort=" + sort +
                ", searchText=" + searchText +
                '}';
    }


    public static Sort.Order getSort(Sorting sorting) {
        if (sorting != null && !TsStringUtils.isNullOrEmpty(sorting.getName()))
            if (!TsStringUtils.isNullOrEmpty(sorting.getDirection())
                    && sorting.getDirection().toLowerCase().contains("desc")) {
                return new Sort.Order(Sort.Direction.DESC, sorting.getName().trim());
            } else
                return new Sort.Order(Sort.Direction.ASC, sorting.getName().trim());

        return new Sort.Order(Sort.Direction.DESC, "id");
    }
}
