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

import java.util.regex.Pattern;

import static org.springframework.http.ResponseEntity.ok;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;

    private final String TABLE_PATTERN = "[a-zA-Z]";
    private final Pattern tablePattern = Pattern.compile(TABLE_PATTERN);

    public Mono<ResponseEntity<Void>> createTable(Table table) {
        return Mono.fromCallable(() -> {
            try {
                if (!tableRepository.findByTableName(table.getTableName()).map(Table::getTableName).isEmpty()) { //уже есть такая
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (table.getPrimaryKey() == null) { //нет ключа
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }
                else if (!tablePattern.matcher(table.getTableName()).matches()) { //имя таблицы неправильное
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if(table.getColumnInfos() != null) { //имя колонок или их тип неправельные
                    for (var column : table.getColumnInfos()) {
                        if(!tablePattern.matcher(column.getTitle()).matches() ||
                                !tablePattern.matcher(column.getType()).matches()) {
                            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                        }
                    }
                } else if (!table.getColumnInfos().stream().anyMatch(x -> x.getTitle() == table.getPrimaryKey())) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE); //ключ неверен
                }
                else {
                    tableRepository.save(table);
                    return new ResponseEntity<Void>(HttpStatus.CREATED);
                }
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Table>> getTable(String name) {
        if (tableRepository.findByTableName(name).map(Table::getTableName).isEmpty()) {
            return Mono.fromCallable(() -> {
                        return new ResponseEntity<Table>(HttpStatus.OK);
                    }).publishOn(Schedulers.boundedElastic());
        } else {
            Table table = tableRepository.findByTableName(name).get();
            table.setPrimaryKey(table.getPrimaryKey().toLowerCase());
            table.getColumnInfos().forEach(x -> x.setTitle(x.getTitle().toUpperCase()));
            table.getColumnInfos().forEach(x -> x.setType(x.getType().toUpperCase()));
            return Mono.fromCallable(() -> new ResponseEntity<>(table, HttpStatus.OK))
                    .publishOn(Schedulers.boundedElastic());
            }
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
