package pl.umk.mat.git2befit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.umk.mat.git2befit.model.CheckSums;
import pl.umk.mat.git2befit.repository.CheckSumsRepository;

import java.util.List;

@Service
public class SynchService {

    private final CheckSumsRepository checkSumsRepository;

    @Autowired
    public SynchService(CheckSumsRepository checkSumsRepository) {
        this.checkSumsRepository = checkSumsRepository;
    }

    public List<CheckSums> getCheckSum() {
        return checkSumsRepository.findAll();
    }

}
