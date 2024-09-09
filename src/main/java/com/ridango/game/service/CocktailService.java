package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class CocktailService {

    private final CocktailApiUtil cocktailApiUtil;
    private Set<String> cocktails = new HashSet<>();

    public CocktailService(CocktailApiUtil cocktailApiUtil) {
        this.cocktailApiUtil = cocktailApiUtil;
    }

    public Cocktail getRandomCocktail() {
        Cocktail cocktail;

        do {
            cocktail = cocktailApiUtil.getRandomCocktail();
        } while (cocktails.contains(cocktail.getIdDrink()));

        cocktails.add(cocktail.getIdDrink());
        return cocktail;
    }

    public void resetGame() {
        cocktails.clear();
    }
}
