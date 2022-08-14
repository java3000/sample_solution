package ru.vk.competition.minbenchmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.service.TableService;

import java.util.logging.Logger;

@Slf4j
@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
public class TableController {

    //Logger logger = Logger.getLogger("table");
    private final TableService tableService;

    @PostMapping("/create-table")
    public Mono<ResponseEntity<Void>> createTable(@RequestBody Table table) {
        //logger.info(" create-table request is " + table.getTableName());
        return tableService.createTable(table);
    }

    @GetMapping("/get-table-by-name/{name}")
    public Mono<ResponseEntity<Table>> getTable(@PathVariable String name) {
        //logger.info("get-table-by-name request is " + name);
        return tableService.getTable(name);
    }

    @DeleteMapping("/drop-table/{name}")
    public Mono<ResponseEntity<Void>>  deleteTable(@PathVariable String name) {
        //logger.info("drop-table-by-name request is " + name);
        return tableService.deleteTable(name);
    }
}
