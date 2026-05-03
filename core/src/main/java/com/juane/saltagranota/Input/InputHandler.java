package com.juane.saltagranota.Input;

import com.badlogic.gdx.InputAdapter;
import com.juane.saltagranota.Mapa.Filas;
import com.juane.saltagranota.Objetos.Animal;

public class InputHandler extends InputAdapter {

    // Distancia mínima para considerar que el dedo se ha movido
    private static final float UMBRAL = 30f;

    private static final int STEP = (int) Filas.ALTURA_FILA;;

    private final Listener listener;

    // Posición inicial del toque
    private float touchStartX;
    private float touchStartY;

    // Interfaz para comunicar acciones al GameScreen
    public interface Listener {
        void onMove(int dx, int dy); // movimiento en X e Y
        void onTap(); // toque simple (sin swipe)
    }

    public InputHandler(Listener listener) {
        this.listener = listener;
    }

    // Se ejecuta cuando el usuario toca la pantalla
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        // Guardamos dónde empezó el toque
        touchStartX = screenX;
        touchStartY = screenY;

        return true; // indicamos que hemos gestionado el evento
    }

    // Se ejecuta cuando el usuario levanta el dedo
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        // Calculamos cuánto se ha movido el dedo
        float deltaX = screenX - touchStartX;
        float deltaY = touchStartY - screenY;

        // Calculamos la distancia total del gesto
        float distancia = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (distancia < UMBRAL) {
            listener.onTap();
            return true;
        }

        // Comparamos si el movimiento es más horizontal o vertical
        if (Math.abs(deltaX) > Math.abs(deltaY)) {

            // Movimiento horizontal
            if (deltaX > 0) {
                listener.onMove(STEP, 0);   // derecha
            } else {
                listener.onMove(-STEP, 0);  // izquierda
            }

        } else {

            // Movimiento vertical
            if (deltaY > 0) {
                listener.onMove(0, STEP);   // arriba
            } else {
                listener.onMove(0, -STEP);  // abajo
            }
        }

        return true;
    }
}
