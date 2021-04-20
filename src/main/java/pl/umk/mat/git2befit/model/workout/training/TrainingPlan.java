package pl.umk.mat.git2befit.model.workout.training;

import pl.umk.mat.git2befit.model.user.entity.User;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class TrainingPlan implements Serializable {
    private static final long serialVersionUID = 984651L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String title;
    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private TrainingForm trainingForm;
    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    private List<Training> planList;
    @ManyToOne
    private User user;
    private String createdAt;

    public TrainingPlan() {}

    public TrainingPlan(TrainingForm trainingForm, List<Training> planList, String date) {
        this.trainingForm = trainingForm;
        this.planList = planList;
        this.createdAt = date;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
