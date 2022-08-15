package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.repository.TableQueryRepository;
import ru.vk.competition.minbenchmark.repository.TableRepository;

import java.util.Objects;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableService {
    Logger logger = Logger.getLogger("table");

    private final TableRepository tableRepository;
    private final TableQueryRepository tableQueryRepository;

    //private final String TABLE_PATTERN = "[a-zA-Z]+";
    private final String TABLE_PATTERN = "([a-zA-Zа-яА-Я]+)"; // "Customerлала = валидное имя :)"
    //private final String INFOS_PATTERN = "[a-zA-Z0-9]+";
    private final Pattern tablePattern = Pattern.compile(TABLE_PATTERN);
    //private final Pattern infosPattern = Pattern.compile(INFOS_PATTERN);

    public Mono<ResponseEntity<Void>> createTable(Table table) {
        return Mono.fromCallable(() -> {
                    try {
                        if (!tableRepository.findByTableName(table.getTableName()).map(Table::getTableName).isEmpty()) { //уже есть такая
                            logger.info("createTable already exists with name " + table.getTableName());
                            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                        } else if (table.getPrimaryKey() == null) { //нет ключа
                            logger.info("createTable primarykey is null maybe with key " + table.getPrimaryKey());
                            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                        } else if (!tablePattern.matcher(table.getTableName()).matches()) { //имя таблицы неправильное
                            logger.info("createTable tableName not matches pattern [a-zA-Z] with name " + table.getTableName());
                            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                        } else if (table.getColumnInfos().stream().filter(x -> Objects.equals(x.getTitle(), table.getPrimaryKey())).count() == 0) {
                            logger.info(String.format("createTable columnInfos titles are not match prymary key: %s", table.getPrimaryKey()));
                            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE); //ключ неверен
                        } else {
                            for (var column : table.getColumnInfos()) {
                                if (!tablePattern.matcher(column.getTitle()).matches()) {
                                    logger.info(String.format("createTable columnInfos are not match pattern [a-zA-z] with title: %s %n type: %s",
                                            column.getTitle(), column.getType()));
                                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                                }
                            }
                            tableRepository.save(table);
                            logger.info("createTable table successfully created with name " + table.getTableName());
                            return new ResponseEntity<Void>(HttpStatus.CREATED);
                        }//имя колонок или их тип неправельные
                    } catch (Exception e) {
                        logger.info("createTable exception " + e.getMessage());
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
