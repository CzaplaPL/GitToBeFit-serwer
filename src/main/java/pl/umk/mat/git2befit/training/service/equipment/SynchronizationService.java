package pl.umk.mat.git2befit.training.service.equipment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SynchronizationService {


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SynchronizationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public List<Map<String, Object>> getCheckSum() {
        List<Map<String, Object>> checkSumList = new ArrayList<>();
        checkSumList.addAll(jdbcTemplate.queryForList("CHECKSUM TABLE equipment_type"));
        checkSumList.addAll(jdbcTemplate.queryForList("CHECKSUM TABLE equipment"));
        return checkSumList;
    }
}
