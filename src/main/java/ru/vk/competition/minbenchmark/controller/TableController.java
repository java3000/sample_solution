package ru.vk.competition.minbenchmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.vk.competition.minbenchmark.entity.Table;

@Slf4j
@RestController
@RequestMapping("/api/table")
@RequiredArgsConstructor
public class TableController {

    @PostMapping("/create-table")
    public int createTable() {

        return 0;
    }

    @GetMapping("/get-table-by-name/{name}")
    public Table getTable() {
        Table result = new Table();


        return result;
    }

    @DeleteMapping("/drop-table-by-name/{name}")
    public void deleteTable() {

    }
}
