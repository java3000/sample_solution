package ru.vk.competition.minbenchmark.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "table_query")
public class TableQuery {
    @Id
    @NonNull
    int queryId;
    @NonNull
    //@Size(max=50)
    String tableName;
    @NonNull
    //@Size(max=120)
    String query;
}
