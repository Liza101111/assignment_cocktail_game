package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import com.ridango.game.model.HighScore;
import com.ridango.game.repository.HighScoreRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final CocktailService cocktailService;
    private final HighScoreRepository highScoreRepository;
    private Cocktail currentCocktail;
    private String maskedName;
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
        currentCocktail = cocktailService.getRandomCocktail();
        maskedName = maskCocktailName(currentCocktail.getName(), 0);
        return cocktailService.getRandomCocktail();
    }

    public String getMaskedCocktailName() {
        return maskedName;
    }

    public String maskCocktailName(String name, int revealCount) {
        StringBuilder maskedName = new StringBuilder();
        int revealed = 0;
        for (int i = 0; i < name.length(); i++) {
            if (revealed < revealCount && Character.isLetter(name.charAt(i))) {
                maskedName.append(name.charAt(i));
                revealed++;
            } else if (name.charAt(i) == ' ') {
                maskedName.append(' ');
            } else {
                maskedName.append('_');
            }
        }
        return maskedName.toString();
    }

    public String getCocktailInstructions() {
        return currentCocktail.getStrInstructions();
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
