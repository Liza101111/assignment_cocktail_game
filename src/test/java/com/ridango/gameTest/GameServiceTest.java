package com.ridango.gameTest;

import com.ridango.game.model.Cocktail;
import com.ridango.game.model.HighScore;
import com.ridango.game.repository.HighScoreRepository;
import com.ridango.game.service.CocktailService;
import com.ridango.game.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GameServiceTest {

    @Mock
    private CocktailService cocktailService;

    @Mock
    private HighScoreRepository highScoreRepository;

    @InjectMocks
    private GameService gameService;

    private Cocktail mockCocktail;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockCocktail = new Cocktail();
        mockCocktail.setIdDrink("12345");
        mockCocktail.setStrDrink("Mojito");

        when(cocktailService.getRandomCocktail()).thenReturn(mockCocktail);
    }

    @Test
    public void testStartNewGame() {
        Cocktail cocktail = gameService.startNewGame();
        assertNotNull(cocktail);
        System.out.println("Mock Cocktail: " + cocktail.getStrDrink());
        assertEquals("Mojito", cocktail.getStrDrink());
        assertEquals(5, gameService.getAttemptsLeft());
        assertEquals(0, gameService.getScore());
        assertEquals("______", gameService.getMaskedCocktailName());
        verify(cocktailService).getRandomCocktail();
    }

    @Test
    public void testMaskCocktailName() {
        String maskedName = gameService.maskCocktailName("Mojito", 1);
        assertNotNull(maskedName);
        assertEquals(6, maskedName.length());

        long revealedLettersCount = maskedName.chars().filter(ch -> ch != '_').count();
        assertEquals(1, revealedLettersCount);
    }

    @Test
    public void testRevealMoreLetters() {
        gameService.startNewGame();
        String maskedName = gameService.revealMoreLetters();
        assertNotNull(maskedName);

        long revealedLettersCount = maskedName.chars().filter(ch -> ch != '_').count();
        assertTrue(revealedLettersCount >= 1);
    }

    @Test
    public void testWrongGuessOrSkip() {
        gameService.startNewGame();
        int initialAttempts = gameService.getAttemptsLeft();
        String response = gameService.wrongGuessOrSkip();
        assertNotNull(response);
        assertEquals(initialAttempts - 1, gameService.getAttemptsLeft());
    }

    @Test
    public void testReduceAttempts() {
        gameService.startNewGame();
        gameService.reduceAttempts();
        assertEquals(4, gameService.getAttemptsLeft());
    }

    @Test
    public void testIncreaseScore() {
        gameService.startNewGame();
        gameService.increaseScore();
        assertEquals(5, gameService.getScore());
    }

    @Test
    public void testNormalizeName() {
        String nameWithSpaces = "  Mojito  ";
        String normalized = gameService.normalizeName(nameWithSpaces);
        assertEquals("mojito", normalized);

        String nameWithPunctuation = "Mojito!";
        normalized = gameService.normalizeName(nameWithPunctuation);
        assertEquals("mojito", normalized);

        String complexName = "A Day at the Beach";
        normalized = gameService.normalizeName(complexName);
        assertEquals("adayatthebeach", normalized);
    }

    @Test
    public void testIsGameOver() {
        gameService.startNewGame();
        for (int i = 0; i < 5; i++) {
            gameService.reduceAttempts();
        }
        assertTrue(gameService.isGameOver());
    }

    @Test
    public void testSaveHighScore() {
        gameService.startNewGame();
        gameService.increaseScore();
        gameService.saveHighScore("TestPlayer");

        ArgumentCaptor<HighScore> highScoreCaptor = ArgumentCaptor.forClass(HighScore.class);
        verify(highScoreRepository).save(highScoreCaptor.capture());
        HighScore savedHighScore = highScoreCaptor.getValue();

        assertEquals("TestPlayer", savedHighScore.getPlayerName());
        assertEquals(5, savedHighScore.getScore());
    }
}
