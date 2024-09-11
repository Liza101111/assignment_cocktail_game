package com.ridango.gameTest;

import com.ridango.game.Controller.GameController;
import com.ridango.game.model.Cocktail;
import com.ridango.game.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    private Cocktail mockCocktail;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();

        mockCocktail = new Cocktail();
        mockCocktail.setStrDrink("Mojito");

        when(gameService.startNewGame()).thenReturn(mockCocktail);
        when(gameService.getCocktailInstructions()).thenReturn("Mix ingredients and serve over ice.");
        when(gameService.getMaskedCocktailName()).thenReturn("______");
        when(gameService.getAttemptsLeft()).thenReturn(5);
        when(gameService.getCurrentCocktail()).thenReturn(mockCocktail);
    }

    @Test
    public void testStartNewGame() throws Exception {
        mockMvc.perform(get("/game/start"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Instructions:")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Guess the cocktail:")));
    }

    @Test
    public void testMakeGuess_Correct() throws Exception {
        String guess = "Mojito";
        when(gameService.normalizeName(guess)).thenReturn("mojito");
        when(gameService.normalizeName("Mojito")).thenReturn("mojito");
        doNothing().when(gameService).increaseScore();
        when(gameService.getScore()).thenReturn(5);

        mockMvc.perform(post("/game/guess")
                        .param("guess", guess))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Correct! The cocktail is Mojito.")));
    }

    @Test
    public void testMakeGuess_Incorrect() throws Exception {
        String guess = "WrongGuess";
        when(gameService.normalizeName(guess)).thenReturn("wrongguess");
        when(gameService.normalizeName("Mojito")).thenReturn("mojito");
        when(gameService.wrongGuessOrSkip()).thenReturn("Wrong guess! Attempts left: 4");

        mockMvc.perform(post("/game/guess")
                        .param("guess", guess))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Wrong guess!")));
    }

    @Test
    public void testMakeGuess_EmptyGuess() throws Exception {
        mockMvc.perform(post("/game/guess")
                        .param("guess", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Guess cannot be empty."));
    }

    @Test
    public void testSkipTurn() throws Exception {
        when(gameService.wrongGuessOrSkip()).thenReturn("Skipped turn. Attempts left: 4");

        mockMvc.perform(post("/game/skip"))
                .andExpect(status().isOk())
                .andExpect(content().string("Skipped turn. Attempts left: 4"));
    }

    @Test
    public void testGetGameStatus() throws Exception {
        when(gameService.getMaskedCocktailName()).thenReturn("_o_i_o");
        when(gameService.getAttemptsLeft()).thenReturn(3);

        mockMvc.perform(get("/game/status"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Masked cocktail name: _o_i_o")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Attempts left: 3")));
    }

    @Test
    public void testGetHint() throws Exception {
        when(gameService.getAdditionalCocktailInfo()).thenReturn("Category: Cocktail\nGlass: Highball glass");

        mockMvc.perform(get("/game/hint"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Category: Cocktail")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Glass: Highball glass")));
    }

    @Test
    public void testSaveHighScore() throws Exception {
        doNothing().when(gameService).saveHighScore("TestPlayer");
        mockMvc.perform(post("/game/saveScore")
                        .param("playerName", "TestPlayer"))
                .andExpect(status().isOk())
                .andExpect(content().string("High score saved for player: TestPlayer"));
    }

    @Test
    public void testSaveHighScore_EmptyName() throws Exception {
        mockMvc.perform(post("/game/saveScore")
                        .param("playerName", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Player name cannot be empty."));
    }
}
