package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.SingleQuery;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.entity.TableQuery;
import ru.vk.competition.minbenchmark.repository.TableQueryRepository;
import ru.vk.competition.minbenchmark.repository.TableRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableQueryService {

    Logger logger = Logger.getLogger("table-query");
    private final TableRepository tableRepository;
    private final TableQueryRepository tableQueryRepository;

    private final String QUERY_PATTERN = "[a-zA-Z]+";
    private final Pattern queryPattern = Pattern.compile(QUERY_PATTERN);

    public Mono<ResponseEntity<Void>> addQuery(TableQuery query) {
        logger.info(String.format("addQuery NEW. id: %s%n tableName: %s%n query: %s%n",
                query.getQueryId(),
                query.getTableName(),
                query.getQuery()));
        return Mono.fromCallable(() -> {
            if (!tableRepository.findByTableName(query.getTableName()).map(Table::getTableName).isEmpty()) {
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
        logger.info(String.format("updateQuery UPDATE. id: %s%n tableName: %s%n query: %s%n",
                query.getQueryId(),
                query.getTableName(),
                query.getQuery()));

        return Mono.fromCallable(() -> {
            try {
                if (!tableRepository.findByTableName(query.getTableName()).map(Table::getTableName).isEmpty()) {
                    logger.info(String.format("updateQuery there is no such table name: %s ", query.getTableName()));
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (!tableQueryRepository.findByQueryId(query.getQueryId()).isPresent()) {
                    logger.info(String.format("updateQuery there is no such query: %s ", query.getQueryId()));
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else {
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

    public Mono<ResponseEntity<Void>> deleteQuery(int id) {
        return Mono.fromCallable(() -> {
            try {
                if (tableQueryRepository.findByQueryId(id).isEmpty()) {
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

    public Mono<ResponseEntity<Void>> executeQuery(int id) {
        return Mono.fromCallable(() -> {
            Connection connection = null;
            Statement statement = null;
            Optional<String> createSql = null;
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(
                        "jdbc:postgresql://localhost:5432/postgres",
                        "postgres",
                        "123"
                );
                log.debug("Database connected hahaha....");
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

    public Mono<ResponseEntity<TableQuery>> getQuery(int id) {
        return Mono.fromCallable(() -> {
                    try {
                        if (!tableQueryRepository.findByQueryId(id).isPresent()) {
                            logger.info(String.format("getQuery there is no such query: %s ", id));
                            return new ResponseEntity<TableQuery>(HttpStatus.NOT_ACCEPTABLE);
                        } else {
                            TableQuery query = tableQueryRepository.findByQueryId(id).get();
                            logger.info(String.format("getQuery success query update: %s ", query.getQueryId()));
                            return new ResponseEntity<TableQuery>(query, HttpStatus.CREATED);
                        }
                    } catch (RuntimeException e) {
                        logger.info(String.format("getQuery creating query exception: %s ", e.getMessage()));
                        return new ResponseEntity<TableQuery>(HttpStatus.NOT_ACCEPTABLE);
                    }
                }
        ).publishOn(Schedulers.boundedElastic());
    }

    public Flux<TableQuery> listQueries() {
        return Mono.fromCallable(tableQueryRepository::findAll)
                .publishOn(Schedulers.boundedElastic())
                .flatMapIterable(x -> x);
    }

    public Mono<ResponseEntity<List<TableQuery>>>  getAllQueriesByTable(String name) {
        return Mono.fromCallable(() -> {
            List<TableQuery> queries = tableQueryRepository.findAllByTableName(name).get();
            return new ResponseEntity<>(queries, HttpStatus.OK);
        }).publishOn(Schedulers.boundedElastic());
    }
}
