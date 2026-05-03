package com.juane.saltagranota.Objetos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.juane.saltagranota.Mapa.Filas;

public class Animal extends Sprite {
    // Altura total del mundo (pantalla del juego)
    public static final float WORLD_HEIGHT = 480f;

    // Tamaño de cada "casilla" o fila (la rana se mueve en este grid)
    public static final float TILE_SIZE = Filas.ALTURA_FILA;

    // Pequeño desplazamiento inicial en Y para que no aparezca pegada al borde
    private static final float START_Y_OFFSET = 4f;

    // Duración de la animación de movimiento (salto)
    private static final float MOVE_ANIMATION_TIME = 0.18f;


    // Estados posibles de animación de la rana
    private enum AnimationMode {
        IDLE,       // quieta
        MOVE_UP,    // salto hacia arriba
        MOVE_SIDE,  // movimiento lateral
        DEAD        // muerte
    }


    // Número de vidas del jugador
    private int vidas;

    // Animaciones de la rana
    private final Animation<TextureRegion> idleAnimation;     // animación en reposo
    private final Animation<TextureRegion> moveUpAnimation;   // animación al avanzar
    private final Animation<TextureRegion> moveSideAnimation; // animación lateral

    // Imagen fija de muerte
    private final TextureRegion deathRegion;


    // Tiempo acumulado (para reproducir animaciones)
    private float stateTime;

    // Temporizador que controla cuánto dura el movimiento
    private float moveTimer;

    // Estado actual de la animación
    private AnimationMode currentMode = AnimationMode.IDLE;

    // Indica si la rana mira hacia la izquierda
    private boolean facingLeft;


    public Animal(Texture[] idleTextures, Texture upMoveTexture, Texture[] sideTextures, Texture deathTexture) {

        // Inicializa el sprite con la primera textura
        super(configurarFiltro(idleTextures[0]));

        // Vidas iniciales
        vidas = 3;

        // Animación en reposo
        idleAnimation = new Animation<>(0.18f,
            crearRegion(idleTextures[0], 47, 132, 240, 188),
            crearRegion(idleTextures[1], 42, 133, 239, 187),
            crearRegion(idleTextures[2], 40, 132, 235, 185),
            crearRegion(idleTextures[3], 0, 125, 320, 195)
        );
        idleAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);

        // Animación de salto hacia arriba
        moveUpAnimation = new Animation<>(0.06f,
            crearRegion(idleTextures[2], 40, 132, 235, 185),
            crearRegion(idleTextures[3], 0, 125, 320, 195),
            crearRegion(upMoveTexture, 44, 173, 194, 146)
        );
        moveUpAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // Animación lateral
        moveSideAnimation = new Animation<>(0.05f,
            crearRegion(sideTextures[0], 81, 154, 181, 164),
            crearRegion(sideTextures[1], 54, 157, 184, 162),
            crearRegion(sideTextures[2], 47, 161, 197, 156),
            crearRegion(sideTextures[3], 28, 134, 231, 186),
            crearRegion(sideTextures[4], 0, 199, 354, 154)
        );
        moveSideAnimation.setPlayMode(Animation.PlayMode.NORMAL);

        // Imagen de muerte
        deathRegion = crearRegion(deathTexture, 47, 17, 248, 302);

        // Tamaño de la rana en pantalla
        setSize(TILE_SIZE, TILE_SIZE);

        // Frame inicial
        setRegion(idleAnimation.getKeyFrame(0f));

        // Posición inicial
        resetToStartPosition();
    }


    public void jump(float dx, float dy) {

        // Si está muerta no puede moverse
        if (currentMode == AnimationMode.DEAD) {
            return;
        }

        // Limita la posición dentro de la pantalla
        float nuevaX = MathUtils.clamp(getX() + dx, 0f, Filas.WORLD_WIDTH - getWidth());
        float nuevaY = MathUtils.clamp(getY() + dy, START_Y_OFFSET, WORLD_HEIGHT - getHeight());

        // Aplica la nueva posición
        setPosition(nuevaX, nuevaY);

        // Activa animación de movimiento
        moveTimer = MOVE_ANIMATION_TIME;

        // Determina tipo de movimiento
        if (dx != 0f) {
            currentMode = AnimationMode.MOVE_SIDE;
            facingLeft = dx < 0f; // dirección
        } else if (dy != 0f) {
            currentMode = AnimationMode.MOVE_UP;
        }
    }

    // Actualiza animaciones cada frame
    public void update(float delta) {

        // Aumenta el tiempo de animación
        stateTime += delta;

        TextureRegion frame;

        // Si está muerta, mostrar sprite de muerte
        if (currentMode == AnimationMode.DEAD) {
            frame = deathRegion;

            // Si está en movimiento, reproducir animación correspondiente
        } else if (moveTimer > 0f) {

            moveTimer = Math.max(0f, moveTimer - delta);

            frame = currentMode == AnimationMode.MOVE_SIDE
                ? moveSideAnimation.getKeyFrame(stateTime, false)
                : moveUpAnimation.getKeyFrame(stateTime, false);

        } else {
            // Si no se mueve, reproducir animación idle
            currentMode = AnimationMode.IDLE;
            frame = idleAnimation.getKeyFrame(stateTime, true);
        }

        // Aplica el frame al sprite
        setRegion(frame);

        // Ajusta la dirección (flip horizontal)
        if (isFlipX() != facingLeft) {
            flip(true, false);
        }
    }

    private static Texture configurarFiltro(Texture texture) {
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        return texture;
    }

    private static TextureRegion crearRegion(Texture texture, int x, int y, int width, int height) {
        texture.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        return new TextureRegion(texture, x, y, width, height);
    }

    public int getVidas() {
        return vidas;
    }

    public void setVidas(int vidas) {
        this.vidas = vidas;
    }

    public void ganarVida() {
        vidas++;
    }

    public boolean perderVida() {
        vidas = Math.max(0, vidas - 1);
        return vidas == 0;
    }

    public void mostrarMuerte() {
        currentMode = AnimationMode.DEAD;
        moveTimer = 0f;
        setRegion(deathRegion);
    }

    public void volverAIdle() {
        currentMode = AnimationMode.IDLE;
        setRegion(idleAnimation.getKeyFrame(stateTime, true));
    }

    public void resetToStartPosition() {
        setPosition((Filas.WORLD_WIDTH - getWidth()) / 2f, START_Y_OFFSET);
    }
}
