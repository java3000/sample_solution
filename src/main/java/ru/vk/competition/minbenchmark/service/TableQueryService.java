package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.entity.TableQuery;
import ru.vk.competition.minbenchmark.repository.TableQueryRepository;
import ru.vk.competition.minbenchmark.repository.TableRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class TableQueryService {

    Logger logger = Logger.getLogger("table-query");
    private final TableRepository tableRepository;
    private final TableQueryRepository tableQueryRepository;
    //private final String QUERY_PATTERN = "([a-zA-Zа-яА-Я*\\s]+)"; // "Customerлала = валидное имя :)"
    //private final String QUERY_PATTERN = "([a-zA-Z*\\s]+)"; // "Customerлала = валидное имя :)"
    private final String QUERY_PATTERN = "([a-zA-Z*]+)";
    private final Pattern queryPattern = Pattern.compile(QUERY_PATTERN, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    public Mono<ResponseEntity<Void>> addQuery(TableQuery query) {
        return Mono.fromCallable(() -> {
            if (query.getQueryId() == null || query.getTableName() == null || query.getQuery() == null) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (Integer.parseInt(query.getQueryId()) <= 0) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (query.getQuery().length() > 120 || query.getTableName().length() > 50) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (!tableRepository.findByTableName(query.getTableName()).map(Table::getTableName).isEmpty()) {
                logger.info(String.format("addQuery there is no such table name: %s ", query.getTableName()));
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (tableQueryRepository.findByQueryId(query.getQueryId()).isPresent()) {
                logger.info(String.format("addQuery query already exists: %s ", query.getQueryId()));
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else if (!queryPattern.matcher(query.getQuery()).matches()) {
                logger.info(String.format("addQuery invalid query: %s ", query.getQuery()));
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            } else {
                try {
                    logger.info("addQuery start creating query");
                    tableQueryRepository.save(query);
                } catch (Exception e) {
                    logger.info(String.format("addQuery creating query exception: %s ", e.getMessage()));
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }
                logger.info("addQuery successfully created");
                return new ResponseEntity<Void>(HttpStatus.CREATED);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> updateQuery(TableQuery query) {
        return Mono.fromCallable(() -> {
            try {
                if (query.getQueryId() == null || query.getTableName() == null || query.getQuery() == null) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (Integer.parseInt(query.getQueryId()) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (query.getQuery().length() > 120 || query.getTableName().length() > 50) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (!tableRepository.findByTableName(query.getTableName()).map(Table::getTableName).isEmpty()) {
                    logger.info(String.format("updateQuery there is no such table name: %s ", query.getTableName()));
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (!tableQueryRepository.findByQueryId(query.getQueryId()).isPresent()) {
                    logger.info(String.format("updateQuery there is no such query: %s ", query.getQueryId()));
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (!queryPattern.matcher(query.getQuery()).matches()) {
                    logger.info(String.format("addQuery invalid query: %s ", query.getQuery()));
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }  else {
                    tableQueryRepository.save(query);
                    logger.info(String.format("updateQuery success query update: %s ", query.getQueryId()));
                    return ResponseEntity.<Void>ok(null);
                }
            } catch (RuntimeException e) {
                logger.info(String.format("updateQuery creating query exception: %s ", e.getMessage()));
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> deleteQuery(String id) {
        return Mono.fromCallable(() -> {
            try {
                if (id == null || Integer.parseInt(id) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (tableQueryRepository.findByQueryId(id).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else {
                    tableQueryRepository.deleteByQueryId(id);
                    return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
                }
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> executeQuery(String id) {
        return Mono.fromCallable(() -> {
            Connection connection;
            Statement statement;
            Optional<String> createSql;

            try {
                if (id == null || Integer.parseInt(id) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }

                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection("jdbc:h2:mem:mydb", "sa", "password");

                statement = connection.createStatement();
                createSql = tableQueryRepository.findByQueryId(id).map(TableQuery::getQuery);
                statement.execute(createSql.get());
                statement.close();
                connection.close();
                return new ResponseEntity<Void>(HttpStatus.CREATED);
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<TableQuery>> getQuery(String id) {
        return Mono.fromCallable(() -> {
                    try {
                        if (id == null) {
                            return new ResponseEntity<TableQuery>(HttpStatus.NOT_ACCEPTABLE);
                        } else if (Integer.parseInt(id) <= 0) {
                            return new ResponseEntity<TableQuery>(HttpStatus.NOT_ACCEPTABLE);
                        } else if (!tableQueryRepository.findByQueryId(id).isPresent()) {
                            return new ResponseEntity<TableQuery>(HttpStatus.INTERNAL_SERVER_ERROR);
                        } else {
                            TableQuery query = tableQueryRepository.findByQueryId(id).get();
                            return new ResponseEntity<TableQuery>(query, HttpStatus.OK);
                        }
                    } catch (RuntimeException e) {
                        return new ResponseEntity<TableQuery>(HttpStatus.NOT_ACCEPTABLE);
                    }
                }
        ).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<List<TableQuery>>> listQueries() {
        return Mono.fromCallable(() -> {
            List<TableQuery> tableQueriesList = new ArrayList<>();
            tableQueryRepository.findAll().forEach(tableQueriesList::add);

            return new ResponseEntity<>(tableQueriesList, HttpStatus.OK);
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<List<TableQuery>>> getAllQueriesByTable(String name) {
        return Mono.fromCallable(() -> {
            if (name == null) {
                return new ResponseEntity<List<TableQuery>>(HttpStatus.OK);
            }
            List<TableQuery> tableQueries = tableQueryRepository.findAllByTableName(name).get();
            return new ResponseEntity<>(tableQueries, HttpStatus.OK);
        }).publishOn(Schedulers.boundedElastic());
    }
}
