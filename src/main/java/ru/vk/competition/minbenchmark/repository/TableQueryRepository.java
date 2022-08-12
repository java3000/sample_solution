package ru.vk.competition.minbenchmark.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vk.competition.minbenchmark.entity.TableQuery;

import java.util.Optional;

@Repository
public interface TableQueryRepository extends CrudRepository<TableQuery, Long> {
    Optional<Object> findByQueryId(int id);
}
