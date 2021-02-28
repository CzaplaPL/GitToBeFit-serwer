package pl.umk.mat.git2befit.model.training.generation.model;

import java.util.List;

public class Training {
    private TrainingForm trainingForm;
    private List<TrainingPlan> planList;

    public Training(TrainingForm trainingForm, List<TrainingPlan> planList) {
        this.trainingForm = trainingForm;
        this.planList = planList;
    }

    public TrainingForm getTrainingForm() {
        return trainingForm;
    }

    public void setTrainingForm(TrainingForm trainingForm) {
        this.trainingForm = trainingForm;
    }

    public List<TrainingPlan> getPlanList() {
        return planList;
    }

    public void setPlanList(List<TrainingPlan> planList) {
        this.planList = planList;
    }
}
/*

 */