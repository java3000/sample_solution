package ru.vk.competition.minbenchmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.vk.competition.minbenchmark.entity.Table;
import ru.vk.competition.minbenchmark.service.TableService;

@Slf4j
@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @PostMapping("/create-table")
    public int createTable(@RequestBody Table table) {
        return tableService.createTable(table);
    }

    @GetMapping("/get-table-by-name/{name}")
    public Table getTable(@PathVariable String name) {
        return tableService.getTable(name);
    }

    @DeleteMapping("/drop-table-by-name/{name}")
    public int deleteTable(@PathVariable String name) {
        return tableService.deleteTable(name);
    }
}
