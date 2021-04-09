package com.khiphach.benchmark.repository;

import com.khiphach.benchmark.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameDAO extends JpaRepository<Game, String> {
}
