package pl.umk.mat.git2befit.controller.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.service.SynchService;

@RestController
@RequestMapping("/check-sum")
public class SynchronizeController {

    private final SynchService synchService;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SynchronizeController(SynchService synchService, JdbcTemplate jdbcTemplate) {
        this.synchService = synchService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public ResponseEntity<?> checkSum(){
        var checkSums = synchService.getCheckSum();
        return ResponseEntity.ok(checkSums);
    }
}
