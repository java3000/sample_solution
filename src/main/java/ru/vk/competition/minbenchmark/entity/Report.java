package ru.vk.competition.minbenchmark.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    @Id
    @NotNull
    private String reportId;

    @NotNull
    private String tableAmount;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @NotNull
    private List<Table> tables;

}
