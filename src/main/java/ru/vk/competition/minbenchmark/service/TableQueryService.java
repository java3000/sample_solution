package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.entity.TableQuery;
import ru.vk.competition.minbenchmark.repository.TableQueryRepository;
import ru.vk.competition.minbenchmark.repository.TableRepository;

import java.util.List;
import java.util.logging.Logger;

@Service
@Slf4j
@RequiredArgsConstructor
public class TableQueryService {

    Logger logger = Logger.getLogger("table-query");
    private final TableRepository tableRepository;
    private final TableQueryRepository tableQueryRepository;

    public Mono<ResponseEntity<Void>> addQuery(TableQuery query) {
        logger.info(String.format("addQuery NEW. id: %s%n tableName: %s%n query: %s%n",
                query.getQueryId(),
                query.getTableName(),
                query.getQuery()));
        return Mono.fromCallable(() -> {
        if (!tableRepository.findByTableName(query.getTableName()).map(Table::getTableName).isEmpty()) {
            logger.info(String.format("addQuery there is no such table name"));
            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
        } else if (!tableQueryRepository.findByQueryId(query.getQueryId()).isPresent()) {
            logger.info(String.format("addQuery query already exists"));
            return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
        } else {
            try {
                logger.info(String.format("addQuery start creating query"));
                tableQueryRepository.save(query);
            } catch (Exception e) {
                logger.info(String.format("addQuery creating query exception: " + e.getMessage()));
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
            logger.info(String.format("addQuery successfully created"));
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }
        }).publishOn(Schedulers.boundedElastic());
    }

    public int updateQuery(TableQuery query) {
        return 0;
    }

    public int deleteQuery(int id) {
        return 0;
    }

    public int executeQuery(int id) {
        return 0;
    }

    public TableQuery getQuery(int id) {
        return null;
    }

    public List<TableQuery> listQueries() {
        return null;
    }
}
