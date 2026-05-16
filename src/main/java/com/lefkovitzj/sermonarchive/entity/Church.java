package com.lefkovitzj.sermonarchive.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Church {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String name;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Speaker> speakers;

    @ManyToOne(cascade = CascadeType.ALL)
    public User owner;

    public Church() {}

    public Church(int id, String name, List<Speaker> speakers, User owner) {
        this.id = id;
        this.name = name;
        this.speakers = speakers;
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Church churches = (Church) o;
        return id == churches.id && Objects.equals(name, churches.name) && Objects.equals(speakers, churches.speakers) && Objects.equals(owner, churches.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, speakers, owner);
    }
}
