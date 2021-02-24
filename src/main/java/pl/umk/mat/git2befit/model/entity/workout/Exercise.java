package pl.umk.mat.git2befit.model.entity.workout;


import pl.umk.mat.git2befit.model.entity.workout.conditions.BodyPart;
import pl.umk.mat.git2befit.model.entity.workout.conditions.ExerciseForm;
import pl.umk.mat.git2befit.model.entity.workout.conditions.TrainingType;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "exercises")
public class Exercise implements Serializable {
    private static final long serialVersionUID = 4L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true)
    private String name;
    private String descriptionOfStartPosition;
    private String descriptionOfCorrectExecution;
    private String hints;
    @ManyToOne(fetch = FetchType.EAGER)
    private ExerciseForm exerciseForm;
    private String videoUrl;
    private String photoUrl;
    @ManyToOne(fetch = FetchType.LAZY)
    private BodyPart bodyPart;
    @ManyToMany(fetch = FetchType.LAZY)
    @Column(nullable = false)
    @JoinTable(name = "training_types_of_exercises",
            joinColumns = {@JoinColumn(name = "exercise_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name="training_type_id", referencedColumnName="id")}
    )
    private List<TrainingType> trainingTypes;
    @ManyToMany(fetch = FetchType.LAZY)
    @Column(nullable = false)
    @JoinTable(name = "exercise_equipment",
               joinColumns = {@JoinColumn(name = "exercise_id", referencedColumnName = "id")},
               inverseJoinColumns = {@JoinColumn(name="equipment_id", referencedColumnName="id")}
    )
    private List<Equipment> equipmentsNeeded;

    public Exercise(){}

    public Exercise(long id, String name, String descriptionOfStartPosition, String descriptionOfCorrectExecution, String hints, ExerciseForm exerciseForm, String videoUrl, BodyPart bodyPart, List<TrainingType> trainingTypes, List<Equipment> equipmentsNeeded) {
        this.id = id;
        this.name = name;
        this.descriptionOfStartPosition = descriptionOfStartPosition;
        this.descriptionOfCorrectExecution = descriptionOfCorrectExecution;
        this.hints = hints;
        this.exerciseForm = exerciseForm;
        this.videoUrl = videoUrl;
        this.bodyPart = bodyPart;
        this.trainingTypes = trainingTypes;
        this.equipmentsNeeded = equipmentsNeeded;
    }

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

    public String getDescriptionOfStartPosition() {
        return descriptionOfStartPosition;
    }

    public void setDescriptionOfStartPosition(String descriptionOfStartPosition) {
        this.descriptionOfStartPosition = descriptionOfStartPosition;
    }

    public String getDescriptionOfCorrectExecution() {
        return descriptionOfCorrectExecution;
    }

    public void setDescriptionOfCorrectExecution(String descriptionOfCorrectExecution) {
        this.descriptionOfCorrectExecution = descriptionOfCorrectExecution;
    }

    public String getHints() {
        return hints;
    }

    public void setHints(String hints) {
        this.hints = hints;
    }

    public ExerciseForm getExerciseForm() {
        return exerciseForm;
    }

    public void setExerciseForm(ExerciseForm exerciseForm) {
        this.exerciseForm = exerciseForm;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }

    public void setBodyPart(BodyPart bodyParts) {
        this.bodyPart = bodyParts;
    }

    public List<TrainingType> getTrainingTypes() {
        return trainingTypes;
    }

    public void setTrainingTypes(List<TrainingType> trainingTypes) {
        this.trainingTypes = trainingTypes;
    }

    public List<Equipment> getEquipmentsNeeded() {
        return equipmentsNeeded;
    }

    public void setEquipmentsNeeded(List<Equipment> equipmentsNeeded) {
        this.equipmentsNeeded = equipmentsNeeded;
    }
}
