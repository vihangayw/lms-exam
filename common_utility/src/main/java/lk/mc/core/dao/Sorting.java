package lk.mc.core.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sorting implements Serializable {

    private String name;
    private String direction;

    @Override
    public String toString() {
        return "Sort{" +
                "name='" + name + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}