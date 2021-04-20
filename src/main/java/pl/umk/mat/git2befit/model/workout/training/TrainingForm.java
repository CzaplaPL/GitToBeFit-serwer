package pl.umk.mat.git2befit.model.workout.training;

import pl.umk.mat.git2befit.converter.LongListConverter;
import pl.umk.mat.git2befit.converter.StringListConverter;

import javax.persistence.*;
import java.util.List;

@Entity(name = "training_forms")
public class TrainingForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Convert(converter = LongListConverter.class)
    private List<Long> equipmentIDs;
    private String trainingType;
    @Convert(converter = StringListConverter.class)
    private List<String> bodyParts;
    private int daysCount;
    private String scheduleType;
    private int duration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean checkIfScheduleTypeIsCircuit() {
        return this.scheduleType.equalsIgnoreCase("CIRCUIT");
    }
}
