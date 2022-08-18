package ru.vk.competition.minbenchmark.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.vk.competition.minbenchmark.entity.SingleQuery;
import ru.vk.competition.minbenchmark.repository.SingleQueryRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SingleQueryService {

    private final SingleQueryRepository queryRepository;

    public Mono<ResponseEntity<List<SingleQuery>>> getAllQueries() {
        return Mono.fromCallable(() -> {
            List<SingleQuery> querysList = new ArrayList<>();
            queryRepository.findAll().forEach(querysList::add);

            return new ResponseEntity<>(querysList, HttpStatus.OK);
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<SingleQuery>> getQueryById(String id) {
        return Mono.fromCallable(() -> {
                    try {
                        if (id == null) {
                            return new ResponseEntity<SingleQuery>(HttpStatus.NOT_ACCEPTABLE);
                        } else if (Integer.parseInt(id) <= 0) {
                            return new ResponseEntity<SingleQuery>(HttpStatus.NOT_ACCEPTABLE);
                        } else if (!queryRepository.findByQueryId(id).isPresent()) {
                            return new ResponseEntity<SingleQuery>(HttpStatus.INTERNAL_SERVER_ERROR);
                        } else {
                            SingleQuery query = queryRepository.findByQueryId(id).get();
                            return new ResponseEntity<SingleQuery>(query, HttpStatus.OK);
                        }
                    } catch (RuntimeException e) {
                        return new ResponseEntity<SingleQuery>(HttpStatus.NOT_ACCEPTABLE);
                    }
                }
        ).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> deleteQueryById(String id) {
        return Mono.fromCallable(() -> {
            try {
                if (id == null || Integer.parseInt(id) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (queryRepository.findByQueryId(id).map(SingleQuery::getQueryId).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else {
                    queryRepository.deleteByQueryId(id);
                    return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
                }
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> addQueryWithQueryId(SingleQuery singleQuery) {
        return Mono.fromCallable(() -> {

            try {
                if (singleQuery.getQueryId() == null || Integer.parseInt(singleQuery.getQueryId()) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                } else if (singleQuery.getQuery() == null || singleQuery.getQuery().isEmpty() || singleQuery.getQuery().length() > 120) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }

                queryRepository.save(singleQuery);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> updateQueryWithQueryId(SingleQuery singleQuery) {
        return Mono.fromCallable(() -> {
            try {
                if (singleQuery.getQueryId() == null || Integer.parseInt(singleQuery.getQueryId()) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (singleQuery.getQuery() == null || singleQuery.getQuery().isEmpty() || singleQuery.getQuery().length() > 120) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (queryRepository.findByQueryId(singleQuery.getQueryId()).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else {
                    queryRepository.save(singleQuery);
                    return ResponseEntity.<Void>ok(null);
                }
            } catch (NumberFormatException e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> executeSingleQuery(String id) {
        return Mono.fromCallable(() -> {
            Connection connection = null;
            Statement statement = null;
            Optional<String> createSql = null;
            try {
                if (id == null || Integer.parseInt(id) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (queryRepository.findByQueryId(id).map(SingleQuery::getQueryId).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }

                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection("jdbc:h2:mem:mydb", "sa", "password");

                statement = connection.createStatement();
                createSql = queryRepository.findByQueryId(id).map(SingleQuery::getQuery);
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

    public Mono<ResponseEntity<Void>> addResult(String resultId, String code) {
        return Mono.fromCallable(() -> {
            try {
                if(resultId == null || Integer.parseInt(resultId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(code == null || Integer.parseInt(code) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(Integer.parseInt(code) != HttpStatus.BAD_REQUEST.value()) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> addNewQuery(String resultId, String queryId, String query) {
        return Mono.fromCallable(() -> {
            try {
                if(queryId == null || Integer.parseInt(queryId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(resultId == null || Integer.parseInt(resultId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if (queryRepository.findByQueryId(queryId).map(SingleQuery::getQueryId).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                //TODO не cуществует такого resuitId
                //TODO what is query and what to do with it...


                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> addModifyResult(String resultId, String code) {
        return Mono.fromCallable(() -> {
            try {
                if(resultId == null || Integer.parseInt(resultId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(code == null || Integer.parseInt(code) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(Integer.parseInt(code) != HttpStatus.NOT_ACCEPTABLE.value()) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }

                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> modifySingleQuery(String resultId, String queryId, String query) {
        return Mono.fromCallable(() -> {
            try {
                if(queryId == null || Integer.parseInt(queryId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (queryRepository.findByQueryId(queryId).map(SingleQuery::getQueryId).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }

                if(resultId == null || Integer.parseInt(resultId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }/*  else if () {
                    //TODO Запроса с таким id не существует
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }*/

                if(query == null || query.isEmpty() || query.isBlank()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                } else if (query.length() > 120) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }

                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> addDeleteResult(String resultId, String code) {
        return Mono.fromCallable(() -> {
            try {
                if(resultId == null || Integer.parseInt(resultId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(code == null || Integer.parseInt(code) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(Integer.parseInt(code) != HttpStatus.NOT_ACCEPTABLE.value()) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }

                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> deleteSingleQueryById(int id, int resid) {
        return Mono.fromCallable(() -> {
            try {
                if (queryRepository.findByQueryId(String.valueOf(id)).map(SingleQuery::getQueryId).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }
                //TODO ● Не существует заданного resultId

                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> addExecuteResult(String resultId, String code) {
        return Mono.fromCallable(() -> {
            try {
                if(resultId == null || Integer.parseInt(resultId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(code == null || Integer.parseInt(code) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(Integer.parseInt(code) != HttpStatus.NOT_ACCEPTABLE.value()) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }

                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> executeSingleQueryById(int id, int resid) {
        return Mono.fromCallable(() -> {
            try {
                if (queryRepository.findByQueryId(String.valueOf(id)).map(SingleQuery::getQueryId).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
                }
                //TODO ● Синтаксис запроса неверный
                //TODO ● Не существует заданного resultId

                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> addGetSingleQueryByIdResult(String resultId, String code) {
        return Mono.fromCallable(() -> {
            try {
                if(resultId == null || Integer.parseInt(resultId) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(code == null || Integer.parseInt(code) <= 0) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }
                if(Integer.parseInt(code) != HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
                }

                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
            }
        }).publishOn(Schedulers.boundedElastic());
    }

    public Mono<ResponseEntity<Void>> getSingleQueryBiId(int id, int resid) {
        return Mono.fromCallable(() -> {
            try {
                if (queryRepository.findByQueryId(String.valueOf(id)).map(SingleQuery::getQueryId).isEmpty()) {
                    return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
                }
                //TODO ● Не существует заданного resultId

                //TODO add some work here
                return new ResponseEntity<Void>(HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }).publishOn(Schedulers.boundedElastic());
    }
}