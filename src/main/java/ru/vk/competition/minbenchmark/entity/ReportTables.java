package ru.vk.competition.minbenchmark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class ReportTables {

    private String tableName;
    private List<ReportTableColumns> columns;
}
