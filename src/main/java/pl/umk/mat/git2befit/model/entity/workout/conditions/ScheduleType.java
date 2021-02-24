package pl.umk.mat.git2befit.model.entity.workout.conditions;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "schedule_types")
public class ScheduleType implements Serializable {
    private static final long serialVersionUID = 6L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    private String name;

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

    public ScheduleType(){}

    public ScheduleType(long id, String name) {
        this.id = id;
        this.name = name;
    }
}