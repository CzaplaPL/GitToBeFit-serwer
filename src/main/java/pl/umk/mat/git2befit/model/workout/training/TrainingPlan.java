package pl.umk.mat.git2befit.model.workout.training;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class TrainingPlan implements Serializable {
    private static final long serialVersionUID = 984651L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private TrainingForm trainingForm;
    @OneToMany
    private List<Training> planList;

    public TrainingPlan() {}

    public TrainingPlan(TrainingForm trainingForm, List<Training> planList) {
        this.trainingForm = trainingForm;
        this.planList = planList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TrainingForm getTrainingForm() {
        return trainingForm;
    }

    public void setTrainingForm(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
    }

    public List<Training> getPlanList() {
        return planList;
    }

    public void setPlanList(List<Training> planList) {
        this.planList = planList;
    }
}
