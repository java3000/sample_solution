package ru.vk.competition.minbenchmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.vk.competition.minbenchmark.entity.TableQuery;
import ru.vk.competition.minbenchmark.service.TableQueryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/table-query")
@RequiredArgsConstructor
public class TableQueryController {

    private final TableQueryService tableQueryService;

    @PostMapping("/add-new-query-to-table")
    public Mono<ResponseEntity<Void>> addQuery (@RequestBody TableQuery query) {
        return tableQueryService.addQuery(query);
    }

    @PutMapping("/modify-query-in-table")
    public int modifyQuery (@RequestBody TableQuery query) {
        return tableQueryService.updateQuery(query);
    }

    @DeleteMapping("/delete-table-query-by-id/{id}")
    public int deleteQuery (@PathVariable int id) {
        return tableQueryService.deleteQuery(id);
    }

    @GetMapping("/execute-table-query-by-id/{id}")
    public int executeQuery (@PathVariable int id) {
        return tableQueryService.executeQuery(id);
    }

    @GetMapping("/get-table-query-by-id/{id}")
    public TableQuery getQuery(@PathVariable int id) {
        return tableQueryService.getQuery(id);
    }

    @GetMapping("/get-all-table-queries")
    public List<TableQuery> listAllQueries () {
        return tableQueryService.listQueries();
    }
}
