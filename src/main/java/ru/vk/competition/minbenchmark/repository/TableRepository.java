package ru.vk.competition.minbenchmark.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.vk.competition.minbenchmark.entity.Table;

import java.util.Optional;

@Repository
public interface TableRepository extends CrudRepository<Table, String> {
    Optional<Table> findByTableName(String name);

    @Transactional
    void deleteByTableName(String name);
}
