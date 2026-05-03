package com.juane.saltagranota.Objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.juane.saltagranota.Mapa.Filas;

public class Tronco extends Sprite {
    private final float velocidad;

    public Tronco(Texture textura, float x, float y, float velocidad) {
        super(textura);
        setPosition(x, y);
        setSize(200f, 60f);
        this.velocidad = velocidad;
    }


    public void update(float delta) {
        setX(getX() + velocidad * delta);

        // Margen para que el tronco entre y salga suavemente
        float margen = 200f;
        float anchoTotalCircuito = Filas.WORLD_WIDTH + (margen * 2);

        if (velocidad > 0f && getX() > Filas.WORLD_WIDTH + margen) {
            // Reaparece por la izquierda manteniendo distancia relativa
            setX(getX() - anchoTotalCircuito);
        } else if (velocidad < 0f && getX() < -margen - getWidth()) {
            // Reaparece por la derecha manteniendo distancia relativa
            setX(getX() + anchoTotalCircuito);
        }
    }

    public float getVelocidad() {
        return velocidad;
    }
}
