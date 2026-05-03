package com.juane.saltagranota.Mapa;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.juane.saltagranota.Objetos.Carro;
import com.juane.saltagranota.Objetos.Tronco;

public class Filas {

    public static final float ALTURA_FILA = 60; // altura de cada fila
    public static float WORLD_WIDTH = 800f; // tamaño de la pantalla


    public enum Tipo { CESPED, CARRETERA, AGUA } // tipos de filas


    public float y; // posición vertical
    public final Tipo tipo; // tipo de fila
    public final Array<Carro> carros; // coches en esta fila
    public final Array<Tronco> troncos; // troncos en esta fila


    public Filas(float y, Tipo tipo) {
        this.y = y;
        this.tipo = tipo;
        carros = new Array<>();
        troncos = new Array<>();
    }

    public void agregarCarro(Carro carro) {
        carros.add(carro);
    } // agrega un coche a la fila


    public void agregarTronco(Tronco tronco) {
        troncos.add(tronco);
    } // agrega un tronco a la fila


    public void update(float delta) { // actualiza la fila y sus elementos
        for (Carro carro : carros) {
            carro.update(delta);
        }

        for (Tronco tronco : troncos) {
            tronco.update(delta);
        }
    }

    // renderiza la fila y sus elementos
    public void renderFondo(SpriteBatch batch, Texture cesped, Texture rio, Texture carretera) {
        Texture fondo;

        if (tipo == Tipo.CESPED) {
            fondo = cesped;
        } else if (tipo == Tipo.AGUA) {
            fondo = rio;
        } else {
            fondo = carretera;
        }

        batch.draw(fondo, 0f, y, WORLD_WIDTH, ALTURA_FILA);
    }

    // renderiza los coches y troncos
    public void renderCoches(SpriteBatch batch) {
        for (Carro carro : carros) {
            carro.draw(batch);
        }

        for (Tronco tronco : troncos) {
            tronco.draw(batch);
        }
    }


    // mueve la fila y sus elementos
    public void shiftY(float deltaY) {
        y += deltaY;

        for (Carro carro : carros) {
            carro.setY(carro.getY() + deltaY);
        }

        for (Tronco tronco : troncos) {
            tronco.setY(tronco.getY() + deltaY);
        }
    }
}
