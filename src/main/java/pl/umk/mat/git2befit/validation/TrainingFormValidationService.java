package pl.umk.mat.git2befit.validation;

import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.workout.training.TrainingForm;

import java.util.List;

@Service
public class TrainingFormValidationService {
    private final static List<String> bodyPartsList = List.of("SIXPACK", "CALVES", "BICEPS", "TRICEPS","SHOULDERS", "CHEST", "BACK", "THIGHS");
    private final static List<String> trainingTypes = List.of("SPLIT", "FBW", "CARDIO", "FITNESS");
    private final static List<String> scheduleTypes = List.of("PER_DAY", "REPETITIVE", "SERIES", "CIRCUIT");

    private TrainingFormValidationService(){}

    public static void  validate(TrainingForm trainingForm) throws IllegalArgumentException{
        validateBodyParts(trainingForm.getBodyParts());
        validateTrainingTypes(trainingForm.getTrainingType());
        validateScheduleTypes(trainingForm.getScheduleType());
        validateDuration(trainingForm.getDuration());
        validateDaysCount(trainingForm.getDaysCount());
    }

    private static void validateBodyParts(List<String> bodyParts) throws IllegalArgumentException{
        for (String bodyPart: bodyParts){
            if(!bodyPartsList.contains(bodyPart))
                throw new IllegalArgumentException("Body part: " + bodyPart + " is unknown");
        }
    }

    private static void validateTrainingTypes(String trainingType) throws IllegalArgumentException{
        if(!trainingTypes.contains(trainingType))
            throw new IllegalArgumentException("Training type: " + trainingType + " is unknown");
    }
    private static void validateDaysCount(int daysCount) throws IllegalArgumentException{
        if(daysCount < 0)
            throw new IllegalArgumentException("Days count:" + daysCount + "is less than 0");
    }

    private static void validateDuration(int duration) throws IllegalArgumentException {
        if(duration < 0)
            throw new IllegalArgumentException("Duration: " + duration + "is less than 0");
    }

    private static void validateScheduleTypes(String scheduleType) throws IllegalArgumentException{
        if(!scheduleTypes.contains(scheduleType))
            throw new IllegalArgumentException("Schedule type: " + scheduleType + " is unknown");
    }

}
