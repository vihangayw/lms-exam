package lk.mc.std.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * JPA entity implementation of {@link Configs}. BranchBean defines all JPA mappings with database
 *
 * @author vihangawicks
 * @since 11/17/21
 * MC-lms
 */
@SuppressWarnings("JpaAttributeTypeInspection")
@Entity
@Data
@NamedQueries({
})
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "configuration")
@NoArgsConstructor
@AllArgsConstructor
public class Configs {

    public static final String SERVER_BASE_URL = "server_base_url";
    public static final String SERVER_LOCAL_PATH = "local_path";
    public static final String SERVER_HIBERNATE = "hibernate_seq";//   http://localhost:8383/api/v1/branch/hibernate-seq/1747236200000

    @Id
    @Column(length = 20)
    private String id;
    @Column(length = 100)
    private String val;


}
