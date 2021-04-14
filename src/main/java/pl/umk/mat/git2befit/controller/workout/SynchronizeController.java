package pl.umk.mat.git2befit.controller.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.service.SynchService;

@RestController
@RequestMapping("/check-sum")
public class SynchronizeController {

    private final SynchService synchService;

    @Autowired
    public SynchronizeController(SynchService synchService) {
        this.synchService = synchService;
    }

    @GetMapping
    public ResponseEntity<?> checkSum(@RequestHeader String authorization){
        var checkSum = synchService.getCheckSum();
        return ResponseEntity.ok(checkSum);
    }
}
