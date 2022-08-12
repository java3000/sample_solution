package ru.vk.competition.minbenchmark.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vk.competition.minbenchmark.entity.SingleQuery;
import ru.vk.competition.minbenchmark.entity.Table;

@Repository
public interface TableRepository  extends CrudRepository<Table, Long> {
}
