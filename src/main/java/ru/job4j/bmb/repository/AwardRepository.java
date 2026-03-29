package ru.job4j.bmb.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.bmb.model.Award;

import java.util.List;

@Repository
public interface AwardRepository extends CrudRepository<Award, Long> {
    public List<Award> findAll();
}
