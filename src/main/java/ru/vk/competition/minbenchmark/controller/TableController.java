package ru.vk.competition.minbenchmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.service.TableService;

@Slf4j
@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping("/create-table")
    public Mono<ResponseEntity<Void>> createTable(@RequestBody Table table) {
        return tableService.createTable(table);
    }

    @GetMapping("/get-table-by-name/{name}")
    public Mono<Table> getTable(@PathVariable String name) {
        return tableService.getTable(name);
    }

    @DeleteMapping("/drop-table-by-name/{name}")
    public Mono<ResponseEntity<Void>>  deleteTable(@PathVariable String name) {
        return tableService.deleteTable(name);
    }
}
