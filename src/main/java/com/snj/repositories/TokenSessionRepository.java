package com.snj.repositories;

import com.snj.entities.TokenSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenSessionRepository extends CrudRepository<TokenSession, Long> {
    TokenSession findOneByUsername(String username);
}
