package pl.umk.mat.git2befit.application;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TrainingRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String formJson;
    @Lob
    @Column(length = 30000)
    private String resultJson;
    private boolean valid;
    private LocalDateTime dateTime;

    public TrainingRequestLog(String formJson, String resultJson, boolean valid) {
        this.formJson = formJson;
        this.valid = valid;
        this.resultJson = resultJson;
        this.dateTime = LocalDateTime.now();
    }

    public TrainingRequestLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormJson() {
        return formJson;
    }

    public void setFormJson(String formJson) {
        this.formJson = formJson;
    }

    public String getResultJson() {
        return resultJson;
    }

    public void setResultJson(String resultJson) {
        this.resultJson = resultJson;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
