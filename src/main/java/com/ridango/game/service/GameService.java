package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import com.ridango.game.model.HighScore;
import com.ridango.game.repository.HighScoreRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final CocktailService cocktailService;
    private final HighScoreRepository highScoreRepository;
    private int attemptsLeft;
    private int score;

    public GameService(CocktailService cocktailService, HighScoreRepository highScoreRepository) {
        this.cocktailService = cocktailService;
        this.highScoreRepository = highScoreRepository;
        this.attemptsLeft = 5;
        this.score = 0;
    }

    public Cocktail startNewGame() {
        cocktailService.resetGame();
        attemptsLeft = 5;
        score = 0;
        return cocktailService.getRandomCocktail();
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public void reduceAttempts() {
        if (attemptsLeft > 0) {
            attemptsLeft--;
        }
    }

    public void increaseScore() {
        score += attemptsLeft;
    }

    public int getScore() {
        return score;
    }

    public void saveHighScore(String playerName) {
        HighScore highScore = new HighScore();
        highScore.setPlayerName(playerName);
        highScore.setScore(score);
        highScoreRepository.save(highScore);
    }
}
