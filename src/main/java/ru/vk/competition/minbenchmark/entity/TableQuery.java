package ru.vk.competition.minbenchmark.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "table_query")
public class TableQuery {
    @Id
    @NotNull
    String queryId;
    @NotNull
    @Size(max=50)
    String tableName;
    @NotNull
    @Size(max=120)
    String query;
}
