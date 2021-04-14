package pl.umk.mat.git2befit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.umk.mat.git2befit.model.CheckSums;

public interface CheckSumsRepository extends JpaRepository<CheckSums, String> {

}