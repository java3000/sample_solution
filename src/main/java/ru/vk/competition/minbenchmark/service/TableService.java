package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.SingleQuery;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.helpers.DatabaseHelper;
import ru.vk.competition.minbenchmark.repository.TableQueryRepository;
import ru.vk.competition.minbenchmark.repository.TableRepository;

import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final TableQueryRepository tableQueryRepository;

    private final String TABLE_PATTERN = "([a-zA-Zа-яА-Я]+)"; // "Customerлала = валидное имя :)"
    private final Pattern tablePattern = Pattern.compile(TABLE_PATTERN);

    public Mono<ResponseEntity<Void>> createTable(Table table) {
        return Mono.fromCallable(() -> {
            try {
                if (!tableRepository.findByTableName(table.getTableName()).map(Table::getTableName).isEmpty()) { //уже есть такая
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (table.getPrimaryKey() == null) { //нет ключа
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (!tablePattern.matcher(table.getTableName()).matches()) { //имя таблицы неправильное
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (table.getColumnInfos().stream().filter(x -> Objects.equals(x.getTitle(), table.getPrimaryKey())).count() == 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE); //ключ неверен
                } else {
                    for (var column : table.getColumnInfos()) {
                        if (!tablePattern.matcher(column.getTitle()).matches()) {
                            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                        }
                    }
                    tableRepository.save(table);
                    DatabaseHelper.createTable(table);
                    return new ResponseEntity<Void>(HttpStatus.CREATED);
                }//имя колонок или их тип неправельные
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Table>> getTable(String name) {
        if (tableRepository.findByTableName(name).map(Table::getTableName).isEmpty()) {
            return Mono.fromCallable(() -> new ResponseEntity<>((Table) null, HttpStatus.OK)).publishOn(Schedulers.boundedElastic());
        } else {
            Table table = tableRepository.findByTableName(name).get();
            table.setPrimaryKey(table.getPrimaryKey().toLowerCase());
            table.getColumnInfos().forEach(x -> x.setTitle(x.getTitle().toUpperCase()));
            table.getColumnInfos().forEach(x -> x.setType((Objects.equals(x.getType(), "int4")) ? "INTEGER" : "CHARACTER VARYING"));

            return Mono.fromCallable(() -> new ResponseEntity<>(table, HttpStatus.OK))
                    .publishOn(Schedulers.boundedElastic());
        }
    }

    public Mono<ResponseEntity<Void>> deleteTable(String name) {
        return Mono.fromCallable(() -> {
            try {
                if (tableRepository.findByTableName(name).map(Table::getTableName).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else {
                    tableQueryRepository.deleteAllByTableName(name);
                    tableRepository.deleteByTableName(name);
                    return new ResponseEntity<Void>(HttpStatus.CREATED);
                }
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }
}
