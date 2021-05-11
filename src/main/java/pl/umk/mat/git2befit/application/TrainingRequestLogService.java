package pl.umk.mat.git2befit.application;

import org.springframework.stereotype.Service;

@Service
public class TrainingRequestLogService {

    private final TrainingRequestLogRepo repo;

    public TrainingRequestLogService(TrainingRequestLogRepo repo) {
        this.repo = repo;
    }

    public void addLog(TrainingRequestLog log){
        repo.save(log);
    }
}
