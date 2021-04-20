package pl.umk.mat.git2befit.controller.workout;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.service.SynchronizationService;

@RestController
@RequestMapping("/check-sum")
public class SynchronizationController {

    private final SynchronizationService synchronizationService;

    @Autowired
    public SynchronizationController(SynchronizationService synchronizationService) {
        this.synchronizationService = synchronizationService;
    }

    @GetMapping
    public ResponseEntity<?> checkSum() {
        var checkSums = synchronizationService.getCheckSum();
        return ResponseEntity.ok(checkSums);
    }
}
