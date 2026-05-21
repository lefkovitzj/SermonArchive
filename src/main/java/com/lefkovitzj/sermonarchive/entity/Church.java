package com.lefkovitzj.sermonarchive.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Church {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Internal object ID representing the church.")
    public Integer id;
    @Schema(description = "The legal or common name of the church.")
    public String name;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Speaker> speakers;

    @ManyToOne(cascade = CascadeType.ALL)
    public User owner;
}
