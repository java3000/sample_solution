package ru.vk.competition.minbenchmark.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vk.competition.minbenchmark.entity.TableQuery;

@Repository
public interface TableQueryRepository extends CrudRepository<TableQuery, Long> {
}
