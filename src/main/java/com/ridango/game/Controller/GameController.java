package com.ridango.game.Controller;

import com.ridango.game.model.Cocktail;
import com.ridango.game.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/start")
    public ResponseEntity<String> startNewGame() {
        Cocktail newCocktail = gameService.startNewGame();
        String response = String.format("Instructions: %s\nGuess the cocktail: %s",
                gameService.getCocktailInstructions(), gameService.getMaskedCocktailName());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/skip")
    public ResponseEntity<String> skipTurn() {
        String response = gameService.wrongGuessOrSkip();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/guess")
    public ResponseEntity<String> makeGuess(@RequestParam String guess) {
        String cocktailName = gameService.getMaskedCocktailName();

        if (guess.equalsIgnoreCase(gameService.getMaskedCocktailName())) {
            gameService.increaseScore();
            String successMessage = String.format("Correct! The cocktail is %s. Your score: %d",
                    gameService.getMaskedCocktailName(), gameService.getScore());
            return new ResponseEntity<>(successMessage, HttpStatus.OK);
        } else {
            String response = gameService.wrongGuessOrSkip();
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<String> getGameStatus() {
        String response = String.format("Masked cocktail name: %s\nAttempts left: %d",
                gameService.getMaskedCocktailName(), gameService.getAttemptsLeft());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/hint")
    public ResponseEntity<String> getHint() {
        String additionalInfo = gameService.getAdditionalCocktailInfo();
        return new ResponseEntity<>(additionalInfo, HttpStatus.OK);
    }

    @PostMapping("/saveScore")
    public ResponseEntity<String> saveHighScore(@RequestParam String playerName) {
        gameService.saveHighScore(playerName);
        String response = String.format("High score saved for player: %s", playerName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
