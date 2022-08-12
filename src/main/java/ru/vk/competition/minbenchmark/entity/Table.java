package ru.vk.competition.minbenchmark.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@javax.persistence.Table(name = "tabled")
public class Table {
    @Id
    @Column(name = "id", nullable = false)
    @NotNull
    private Long id;

    @NotNull
    String tableName;
    @NotNull
    int columnsAmount;
    @NotNull
    String primaryKey;
    @NotNull
    @ManyToMany
    List<ColumnInfo> columnInfos;
}
