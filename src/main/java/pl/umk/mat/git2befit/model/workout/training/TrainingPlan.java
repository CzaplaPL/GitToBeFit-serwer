package pl.umk.mat.git2befit.model.workout.training;

import java.io.Serializable;
import java.util.List;

public class TrainingPlan implements Serializable {
    private static final long serialVersionUID = 984651L;

    //todo klucz główny
    private TrainingForm trainingForm;
    private List<Training> planList;

    public TrainingPlan(TrainingForm trainingForm, List<Training> planList) {
        this.trainingForm = trainingForm;
        this.planList = planList;
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
