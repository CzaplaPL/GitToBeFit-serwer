package pl.umk.mat.git2befit.training.validation;


import pl.umk.mat.git2befit.training.exceptions.NotValidTrainingException;
import pl.umk.mat.git2befit.training.model.training.Training;

import java.util.List;


public class SplitValidator {

    public static void validateTraining(List<Training> trainingList) {
        buu();
    }

    private static void buu() {
        throw new NotValidTrainingException("BUUUU");
    }
}