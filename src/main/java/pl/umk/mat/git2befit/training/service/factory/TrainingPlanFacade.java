package pl.umk.mat.git2befit.training.service.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.umk.mat.git2befit.application.TrainingRequestLog;
import pl.umk.mat.git2befit.application.TrainingRequestLogService;
import pl.umk.mat.git2befit.training.exceptions.EquipmentCountException;
import pl.umk.mat.git2befit.training.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.training.model.training.TrainingForm;
import pl.umk.mat.git2befit.training.model.training.TrainingPlan;
import pl.umk.mat.git2befit.training.validation.TrainingFormValidationService;

@Component
public class TrainingPlanFacade {
    private final TrainingPlanFactory trainingPlanFactory;
    private final TrainingRequestLogService logService;
    private final ObjectMapper objectMapper;
    private final Logger log = LoggerFactory.getLogger(TrainingPlanFacade.class);

    @Autowired
    public TrainingPlanFacade(TrainingPlanFactory trainingPlanFactory, TrainingRequestLogService logService, ObjectMapper objectMapper) {
        this.trainingPlanFactory = trainingPlanFactory;
        this.logService = logService;
        this.objectMapper = objectMapper;
    }

    public TrainingPlan createTrainingPlan(TrainingForm trainingForm)
            throws IllegalArgumentException, NotValidTrainingException, EquipmentCountException {
        try {

            TrainingFormValidationService.validate(trainingForm);

            TrainingPlanGenerator trainingPlanGenerator = trainingPlanFactory.createPlan(trainingForm.getTrainingType());

            TrainingPlan trainingPlan = trainingPlanGenerator.create(trainingForm);

            trainingPlanGenerator.validate(trainingPlan, trainingPlan.getTrainingForm());

            saveLog(trainingForm, trainingPlan, true);
            return trainingPlan;
        } catch (Throwable exception) {
            var exceptionMessage = "Exception type: %s, message: %s".formatted(exception.getClass().getName(), exception.getMessage());
            saveLog(trainingForm, exceptionMessage, false);
            throw exception;
        }
    }

    private void saveLog(Object form, Object result, boolean valid) {
        try {
            var jsonForm = objectMapper.writeValueAsString(form);
            var jsonResult = objectMapper.writeValueAsString(result);
            System.out.println(jsonResult.length());
            logService.addLog(new TrainingRequestLog(jsonForm, jsonResult, valid));
        } catch (JsonProcessingException jsonProcessingException) {
            jsonProcessingException.printStackTrace();
        }
    }
}
