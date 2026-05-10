package com.juane.saltagranota.Objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.juane.saltagranota.Mapa.Filas;

public class Pajaro extends Sprite {
    private final float velocidad;

    public Pajaro(Texture textura, float x, float y, float velocidad) {
        super(textura);
        setPosition(x, y);
        setSize(72f, 40f);
        this.velocidad = velocidad;
    }

    public void update(float delta) {
        setX(getX() + (velocidad * delta));

        // Definimos un ancho total del "circuito" para que recirculen
        // Usamos WORLD_WIDTH + un margen para que no desaparezcan de golpe
        float margen = 150f;
        float anchoTotalCircuito = Filas.WORLD_WIDTH + (margen * 2);

        if (velocidad > 0 && getX() > Filas.WORLD_WIDTH + margen) {
            // Si va a la derecha y se sale del margen derecho, reaparece por la izquierda
            setX(getX() - anchoTotalCircuito);
        } else if (velocidad < 0 && getX() < -margen - getWidth()) {
            // Si va a la izquierda y se sale del margen izquierdo, reaparece por la derecha
            setX(getX() + anchoTotalCircuito);
        }
    }
}
