package pl.umk.mat.git2befit.controller.workout;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.model.entity.workout.equipment.Equipment;
import pl.umk.mat.git2befit.service.workout.EquipmentService;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    private EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping()
    public ResponseEntity<List<Equipment>> getEquipmentsOfSpecificType(@RequestParam Long typeId) {
        return equipmentService.getEquipmentsOfSpecificType(typeId);
    }
}
