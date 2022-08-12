package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.repository.TableRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    public int createTable(Table table) {
        return 0;
    }

    public Mono<Table> getTable(String name) {
        return Mono.fromCallable(() ->
                tableRepository.findByTableName(name).orElseThrow(() -> new RuntimeException(
                String.format("Cannot find table by name %s", name)
        ))).publishOn(Schedulers.boundedElastic());
    }

    public int deleteTable(String name) {
        return 0;
    }
}
