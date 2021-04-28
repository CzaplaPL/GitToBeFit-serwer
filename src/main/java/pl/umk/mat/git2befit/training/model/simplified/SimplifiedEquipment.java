package pl.umk.mat.git2befit.training.model.simplified;

import pl.umk.mat.git2befit.training.model.equipment.Equipment;

public class SimplifiedEquipment {
    private long id;
    private String name;
    private String url;

    public SimplifiedEquipment(Equipment equipment) {
        this.id = equipment.getId();
        this.name = equipment.getName();
        this.url = equipment.getUrl();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
