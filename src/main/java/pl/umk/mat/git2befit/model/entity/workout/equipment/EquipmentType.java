package pl.umk.mat.git2befit.model.entity.workout.equipment;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "equipment_type")
public class EquipmentType implements Serializable {
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String url;

    public EquipmentType() { }

    public EquipmentType(long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return "EquipmentType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ulr='" + url + '\'' +
                '}';
    }
}
