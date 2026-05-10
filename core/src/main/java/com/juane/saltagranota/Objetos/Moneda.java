package com.juane.saltagranota.Objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Moneda extends Sprite {

    public Moneda(Texture textura, float x, float y){
        super(textura);
        setPosition(x, y);
        setSize(32f, 32f);
    }
}
