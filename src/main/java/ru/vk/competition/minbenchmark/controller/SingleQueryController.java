package ru.vk.competition.minbenchmark.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.vk.competition.minbenchmark.entity.SingleQuery;
import ru.vk.competition.minbenchmark.service.SingleQueryService;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/single-query")
@RequiredArgsConstructor
public class SingleQueryController {

    private final SingleQueryService singleQueryService;

    @GetMapping("/get-all-single-queries")
    public Mono<ResponseEntity<List<SingleQuery>>> getAllTableQueries() {
        return singleQueryService.getAllQueries();
    }

    @GetMapping("/get-single-query-by-id/{id}")
    public Mono<ResponseEntity<SingleQuery>> getTableQueryById(@PathVariable @NotNull String id) {
        return singleQueryService.getQueryById(id);
    }

    @DeleteMapping("/delete-single-query-by-id/{id}")
    public Mono<ResponseEntity<Void>> deleteTableQueryById(@PathVariable @NotNull String id) {
        return singleQueryService.deleteQueryById(id);
    }

    @PostMapping("/add-new-query")
    public Mono<ResponseEntity<Void>> addNewQueryToTable(@RequestBody @NotNull SingleQuery singleQuery) {
        return singleQueryService.addQueryWithQueryId(singleQuery);
    }

    @PutMapping("/modify-single-query")
    public Mono<ResponseEntity<Void>> modifyQueryInTable(@RequestBody @NotNull SingleQuery singleQuery) {
        return singleQueryService.updateQueryWithQueryId(singleQuery);
    }

    @GetMapping("/execute-single-query-by-id/{id}")
    Mono<ResponseEntity<Void>> executeSingleQuery(@PathVariable @NotNull String id) {
        return singleQueryService.executeSingleQuery(id);
    }

    @GetMapping("/add-new-query-result")
    public Mono<ResponseEntity<Void>> addResult(@PathVariable @NotNull String resultId,
                          @PathVariable @NotNull String code) {
        return singleQueryService.addResult(resultId, code);
    }

    @PostMapping("/add-new-query?resultId={id}")
    public Mono<ResponseEntity<Void>> addNewQuery (@PathVariable @NotNull String id, @RequestBody String queryId, @RequestBody String query) {
        return singleQueryService.addNewQuery(id, queryId, query);
    }

    @PostMapping("/add-modify-result")
    public void addModifyResult
}