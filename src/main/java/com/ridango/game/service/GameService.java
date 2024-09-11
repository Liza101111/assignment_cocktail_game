package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import com.ridango.game.model.HighScore;
import com.ridango.game.repository.HighScoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Slf4j
@Service
public class GameService {

    private final CocktailService cocktailService;
    private final HighScoreRepository highScoreRepository;
    private Cocktail currentCocktail;
    private String maskedName;
    private int attemptsLeft;
    private int score;
    private int revealedLetters;

    public GameService(CocktailService cocktailService, HighScoreRepository highScoreRepository) {
        this.cocktailService = cocktailService;
        this.highScoreRepository = highScoreRepository;
        resetGameState();
    }

    private void resetGameState() {
        attemptsLeft = 5;
        score = 0;
        revealedLetters = 0;
        currentCocktail = null;
        maskedName = "";
    }

    public Cocktail startNewGame() {
        resetGameState();
        currentCocktail = cocktailService.getRandomCocktail();
        log.info("Fetched Cocktail: {}", currentCocktail != null ? currentCocktail.getStrDrink() : "null");

        if (currentCocktail == null || currentCocktail.getStrDrink() == null || currentCocktail.getStrDrink().isEmpty()) {
            throw new IllegalStateException("Random cocktail or cocktail name is missing!");
        }

        maskedName = maskCocktailName(currentCocktail.getStrDrink(), revealedLetters);
        return currentCocktail;
    }

    public Cocktail getCurrentCocktail() {
        return currentCocktail;
    }

    public String getMaskedCocktailName() {
        return maskedName;
    }

    public String maskCocktailName(String name, int revealCount) {
        StringBuilder maskedName = new StringBuilder();
        Set<Integer> revealedPositions = generateUniqueRandomPositions(name, revealCount);

        for (int i = 0; i < name.length(); i++) {
            if (revealedPositions.contains(i) || !Character.isLetter(name.charAt(i))) {
                maskedName.append(name.charAt(i));
            } else {
                maskedName.append('_');
            }
        }
        return maskedName.toString();
    }

    private Set<Integer> generateUniqueRandomPositions(String name, int revealCount) {
        Set<Integer> revealedPositions = new HashSet<>();
        while (revealedPositions.size() < revealCount) {
            int position = (int) (Math.random() * name.length());
            if (Character.isLetter(name.charAt(position))) {
                revealedPositions.add(position);
            }
        }
        return revealedPositions;
    }

    public String revealMoreLetters() {
        int nameLength = currentCocktail.getStrDrink().length();
        revealedLetters += Math.max(1, nameLength / 5);
        maskedName = maskCocktailName(currentCocktail.getStrDrink(), revealedLetters);

        return maskedName;
    }

    public String wrongGuessOrSkip() {
        if (isGameOver()) {
            return "Game Over! The cocktail was: " + currentCocktail.getStrDrink();
        }
        reduceAttempts();
        return "Wrong guess! " + revealMoreLetters() + "\nAttempts left: " + attemptsLeft + "\nAdditional Info: " + getAdditionalCocktailInfo();
    }

    public String getCocktailInstructions() {
        return currentCocktail.getStrInstructions();
    }

    public String getAdditionalCocktailInfo() {
        return String.format("Category: %s\nGlass: %s\nIngredients: %s\nPicture: %s",
                currentCocktail.getStrCategory(),
                currentCocktail.getStrGlass(),
                formatIngredients(),
                currentCocktail.getStrDrinkThumb());
    }

    private String formatIngredients() {
        return Stream.of(currentCocktail.getStrIngredient1(), currentCocktail.getStrIngredient2(),
                        currentCocktail.getStrIngredient3(), currentCocktail.getStrIngredient4(), currentCocktail.getStrIngredient5())
                .filter(Objects::nonNull)
                .filter(ingredient -> !ingredient.isEmpty())
                .collect(Collectors.joining(", "));
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public void reduceAttempts() {
        if (attemptsLeft > 0) {
            attemptsLeft--;
        }
    }

    public String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        return name.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    public boolean isGameOver() {
        return attemptsLeft <= 0;
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

    public List<HighScore> getHighScores() {
        return highScoreRepository.findAll();
    }
}
