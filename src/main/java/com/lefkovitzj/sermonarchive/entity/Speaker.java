package com.lefkovitzj.sermonarchive.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
public class Speaker {
    @Getter
    @Id
    @Column(unique = true)
    private String name;

    public Speaker() {}

    public Speaker(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Speaker speaker = (Speaker) o;
        return Objects.equals(name, speaker.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
