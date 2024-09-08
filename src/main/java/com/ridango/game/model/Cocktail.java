package com.ridango.game.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Cocktail {

    private String id;
    private String name;
    private String strInstructions;
    private String category;
    private String glass;
    private String thumbnails;
    private List<String> ingredients = new ArrayList<>();
}
