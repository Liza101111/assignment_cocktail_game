package com.ridango.game.repository;

import com.ridango.game.model.HighScore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HighScoreRepository extends JpaRepository<HighScore, Long> {
}
