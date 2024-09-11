package com.ridango.game.Controller;

import com.ridango.game.model.Cocktail;
import com.ridango.game.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/start")
    public ResponseEntity<String> startNewGame() {
        try {
            Cocktail newCocktail = gameService.startNewGame();
            String response = String.format("Instructions: %s\nGuess the cocktail: %s",
                    gameService.getCocktailInstructions(), gameService.getMaskedCocktailName());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to start a new game.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/skip")
    public ResponseEntity<String> skipTurn() {
        try {
            String response = gameService.wrongGuessOrSkip();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error skipping turn", e);
            return new ResponseEntity<>("Failed to skip turn.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/guess")
    public ResponseEntity<String> makeGuess(@RequestParam(required = false) String guess) {
        if (guess == null || guess.trim().isEmpty()) {
            return new ResponseEntity<>("Guess cannot be empty.", HttpStatus.BAD_REQUEST);
        }

        try {
            String originalCocktailName = gameService.getCurrentCocktail().getStrDrink();
            String normalizedGuess = gameService.normalizeName(guess);
            String normalizedCocktailName = gameService.normalizeName(originalCocktailName);

            if (normalizedGuess.equals(normalizedCocktailName)) {
                gameService.increaseScore();
                String successMessage = String.format("Correct! The cocktail is %s. Your score: %d",
                        originalCocktailName, gameService.getScore());
                return new ResponseEntity<>(successMessage, HttpStatus.OK);
            } else {
                String response = gameService.wrongGuessOrSkip();
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error processing guess.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getGameStatus() {
        try {
            String response = String.format("Masked cocktail name: %s\nAttempts left: %d",
                    gameService.getMaskedCocktailName(), gameService.getAttemptsLeft());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to retrieve game status.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/hint")
    public ResponseEntity<String> getHint() {
        try {
            String additionalInfo = gameService.getAdditionalCocktailInfo();
            return new ResponseEntity<>(additionalInfo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to retrieve hint.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/saveScore")
    public ResponseEntity<String> saveHighScore(@RequestParam(required = false) String playerName) {
        if (playerName == null || playerName.trim().isEmpty()) {
            return new ResponseEntity<>("Player name cannot be empty.", HttpStatus.BAD_REQUEST);
        }

        try {
            gameService.saveHighScore(playerName);
            String response = String.format("High score saved for player: %s", playerName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save high score.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
