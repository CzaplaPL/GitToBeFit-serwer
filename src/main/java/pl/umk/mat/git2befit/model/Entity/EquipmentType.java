package pl.umk.mat.git2befit.model.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class EquipmentType implements Serializable {
    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column
    private String url;

    public EquipmentType() { }

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

    public String getUlr() {
        return url;
    }

    public void setUlr(String ulr) {
        this.url = ulr;
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
