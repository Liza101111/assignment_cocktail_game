package com.ridango.game.Controller;

import com.ridango.game.model.Cocktail;
import com.ridango.game.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        String response = "Instructions: " + gameService.getCocktailInstructions() + "\n" +
                "Guess the cocktail: " + gameService.getMaskedCocktailName();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
