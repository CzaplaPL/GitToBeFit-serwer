package pl.umk.mat.git2befit.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CheckSums {

    @Id
    private String tableName;
    private Long sumValue;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getSumValue() {
        return sumValue;
    }

    public void setSumValue(Long sumValue) {
        this.sumValue = sumValue;
    }
}