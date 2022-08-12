package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public Mono<ResponseEntity<Void>> createTable(Table table) {
        return Mono.fromCallable(() -> {
            try {
                tableRepository.save(table);
                return new ResponseEntity<Void>(HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<Table> getTable(String name) {
        return Mono.fromCallable(() -> tableRepository.findByTableName(name).get())
        .publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> deleteTable(String name) {
        return Mono.fromCallable(() -> {
            try {
                if(tableRepository.findByTableName(name).map(Table::getTableName).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else {
                    tableRepository.deleteByTableName(name);
                    return new ResponseEntity<Void>(HttpStatus.CREATED);
                }
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }
}
