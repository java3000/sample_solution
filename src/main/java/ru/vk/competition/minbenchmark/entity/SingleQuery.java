package ru.vk.competition.minbenchmark.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@Entity
@Table(name = "single_query")
@NoArgsConstructor
@AllArgsConstructor
public class SingleQuery {

    @Id
    @Column(name = "queryId")
    @NotNull
    private String queryId;

    @Column(name = "query")
    @NotNull
    @Size(max = 120)
    private String query;
}