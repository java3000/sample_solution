package ru.vk.competition.minbenchmark.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.vk.competition.minbenchmark.entity.SingleQuery;
import ru.vk.competition.minbenchmark.entity.Table;

import java.util.Optional;

@Repository
public interface TableRepository extends CrudRepository<Table, Long> {
    Optional<Table> findByTableName(String name);

    Optional<Table> findById(Long id);

    @Transactional
    void deleteByTableName(String name);
}
