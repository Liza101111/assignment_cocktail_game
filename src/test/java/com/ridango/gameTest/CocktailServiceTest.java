package com.ridango.gameTest;

import com.ridango.game.model.Cocktail;
import com.ridango.game.service.CocktailApiUtil;
import com.ridango.game.service.CocktailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CocktailServiceTest {
    private CocktailService cocktailService;
    private CocktailApiUtil cocktailApiUtilMock;

    @BeforeEach
    public void setUp() {
        cocktailApiUtilMock = mock(CocktailApiUtil.class);
        cocktailService = new CocktailService(cocktailApiUtilMock);
    }

    @Test
    public void testGetRandomCocktail_UniqueCocktails() {

        Cocktail cocktail1 = new Cocktail();
        cocktail1.setIdDrink("1");
        cocktail1.setStrDrink("Mojito");

        Cocktail cocktail2 = new Cocktail();
        cocktail2.setIdDrink("2");
        cocktail2.setStrDrink("Martini");

        when(cocktailApiUtilMock.getRandomCocktail())
                .thenReturn(cocktail1)
                .thenReturn(cocktail1)
                .thenReturn(cocktail2);

        Cocktail firstCocktail = cocktailService.getRandomCocktail();
        Cocktail secondCocktail = cocktailService.getRandomCocktail();

        assertEquals("1", firstCocktail.getIdDrink());
        assertEquals("2", secondCocktail.getIdDrink());
    }
}
