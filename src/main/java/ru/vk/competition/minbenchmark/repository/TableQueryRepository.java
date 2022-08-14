package ru.vk.competition.minbenchmark.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.vk.competition.minbenchmark.entity.TableQuery;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableQueryRepository extends CrudRepository<TableQuery, String> {
    Optional<TableQuery> findByQueryId(String id);

    Optional<List<TableQuery>> findAllByTableName(String name);

    @Transactional
    void deleteByQueryId(String id);
}
