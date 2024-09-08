package com.ridango.game.service;

import com.ridango.game.model.Cocktail;
import com.ridango.game.model.CocktailResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CocktailApiUtil {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String RANDOM_COCKTAIL_URL = "https://www.thecocktaildb.com/api/json/v1/1/random.php";

    public Cocktail getRandomCocktail(){
        CocktailResponse response = restTemplate.getForObject(RANDOM_COCKTAIL_URL, CocktailResponse.class);
        return response.getDrinks().get(0);
    }
}
