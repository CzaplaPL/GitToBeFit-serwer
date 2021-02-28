package pl.umk.mat.git2befit.model.training.generation.model;

import java.util.List;

public class TrainingForm {
    private List<Long> equipmentIDs;
    private String trainingType;
    private List<String> bodyParts;
    private int daysCount;
    private String exerciseForm;
    private int duration;

    public List<Long> getEquipmentIDs() {
        return equipmentIDs;
    }

    public void setEquipmentIDs(List<Long> equipmentIDs) {
        this.equipmentIDs = equipmentIDs;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public List<String> getBodyParts() {
        return bodyParts;
    }

    public void setBodyParts(List<String> bodyParts) {
        this.bodyParts = bodyParts;
    }

    public int getDaysCount() {
        return daysCount;
    }

    public void setDaysCount(int daysCount) {
        this.daysCount = daysCount;
    }

    public String getExerciseForm() {
        return exerciseForm;
    }

    public void setExerciseForm(String exerciseForm) {
        this.exerciseForm = exerciseForm;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
