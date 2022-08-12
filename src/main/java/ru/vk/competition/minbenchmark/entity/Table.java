package ru.vk.competition.minbenchmark.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@javax.persistence.Table(name = "table")
public class Table {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    String tableName;
    int columnsAmount;
    String primaryKey;

    List<ColumnInfo> columnInfos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
