package pl.umk.mat.git2befit.model.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Equipment implements Serializable {
    private static final long serialVersionUID = 3L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    //TODO ogarniecie klucza obcego do klasy user
//  @JoinColumn(name = "user_id")
    private long userID;
    //TODO zrobic klucz obcy do equipmenttype
    @ManyToOne
    private EquipmentType type;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column
    private String url;

    public Equipment() {}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public EquipmentType getType() {
        return type;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
