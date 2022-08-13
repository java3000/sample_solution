package ru.vk.competition.minbenchmark.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@javax.persistence.Table(name = "tables")
public class Table {
    @Id
    @Column(name = "tableName", nullable = false)
    @NotNull
    String tableName;
    @NotNull
    int columnsAmount;
    @NotNull
    String primaryKey;
    @NotNull
    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    List<Columns> columnInfos;
}
