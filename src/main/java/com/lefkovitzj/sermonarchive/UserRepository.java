package com.lefkovitzj.sermonarchive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.NullLiteral;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByUsernameContaining(String username);
}
