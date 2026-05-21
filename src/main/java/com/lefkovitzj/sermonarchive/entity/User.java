package com.lefkovitzj.sermonarchive.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name="app_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Integer id;

    @Column(unique = true)
    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private String password;

    @Setter
    @Getter
    private List<String> roles;

    @Setter
    @Getter
    @Column(unique = true)
    private String email;
}
