package pl.umk.mat.git2befit.training.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.umk.mat.git2befit.training.model.simplified.SimplifiedEquipment;
import pl.umk.mat.git2befit.training.service.equipment.EquipmentService;

import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    private EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping()
    public ResponseEntity<List<SimplifiedEquipment>> getEquipmentsOfSpecificType(@RequestParam Long typeId) {
        return equipmentService.getEquipmentsOfSpecificType(typeId);
    }

    @GetMapping("/no-equipment")
    public ResponseEntity<?> getNoEquipment() {
        return equipmentService.getNoEquipment();
    }
}
