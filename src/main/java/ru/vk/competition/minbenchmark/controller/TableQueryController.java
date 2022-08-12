package ru.vk.competition.minbenchmark.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.vk.competition.minbenchmark.entity.TableQuery;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/table-query")
@RequiredArgsConstructor
public class TableQueryController {

    @PostMapping("/add-new-query-to-table")
    public String addQuery (@RequestBody TableQuery query) {

    }

    @PutMapping("/modify-query-in-table")
    public int modifyQuery (@RequestBody TableQuery query) {

    }

    @DeleteMapping("/delete-table-query-by-id/{id}")
    public int deleteQuery (@PathVariable int id) {

    }

    @GetMapping("/execute-table-query-by-id/{id}")
    public int executeQuery (@PathVariable int id) {

    }

    @GetMapping("/get-table-query-by-id/{id}")
    public TableQuery getQuery(@PathVariable int id) {

    }

    @GetMapping("/get-all-table-queries")
    public List<TableQuery> listAllQueries () {

    }
}
