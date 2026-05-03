package com.juane.saltagranota.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.juane.saltagranota.Assets.AssetsDescriptor;
import com.juane.saltagranota.Input.InputHandler;
import com.juane.saltagranota.Mapa.Filas;
import com.juane.saltagranota.Objetos.Animal;

public class GameScreen implements Screen {
    private static final float DURACION_MUERTE = 0.45f;
    private static final float RESPAWN_INVULNERABILIDAD = 0.35f;

    final Drop game;
    private Animal frog;
    private ObjetosHandler objetosHandler;
    private BitmapFont font;
    private GlyphLayout layout;
    private String statusMessage = "";
    private float messageTimer;
    private int puntuacion;
    private float deathTimer = -1f;
    private float respawnInvulnerableTimer;
    private boolean pendingGameOver;
    private Sound salto;
    private Sound muerte;
    private Sound agua;

    public GameScreen(Drop game) {
        this.game = game;
    }

    @Override
    public void show() {
        Filas.WORLD_WIDTH = game.getWorldWidth();
        loadAssets();

        // Inicializar fuente correctamente una sola vez
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/PixelifySans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        font = generator.generateFont(parameter);
        generator.dispose();

        layout = new GlyphLayout();
        objetosHandler = new ObjetosHandler(game.assetManager);
        frog = new Animal(
            new com.badlogic.gdx.graphics.Texture[]{
                game.assetManager.get(AssetsDescriptor.ranaUp1),
                game.assetManager.get(AssetsDescriptor.ranaUp2),
                game.assetManager.get(AssetsDescriptor.ranaUp3),
                game.assetManager.get(AssetsDescriptor.ranaUp4)
            },
            game.assetManager.get(AssetsDescriptor.ranaUp5),
            new com.badlogic.gdx.graphics.Texture[]{
                game.assetManager.get(AssetsDescriptor.ranalateral1),
                game.assetManager.get(AssetsDescriptor.ranalateral2),
                game.assetManager.get(AssetsDescriptor.ranalateral3),
                game.assetManager.get(AssetsDescriptor.ranalateral4),
                game.assetManager.get(AssetsDescriptor.ranalateral5)
            },
            game.assetManager.get(AssetsDescriptor.ranamuerte)
        );

        salto = game.assetManager.get(AssetsDescriptor.saltoSound);
        muerte = game.assetManager.get(AssetsDescriptor.muerteSound);
        agua = game.assetManager.get(AssetsDescriptor.aguaSound);

        Gdx.input.setInputProcessor(new InputHandler(new InputHandler.Listener() {
            @Override
            public void onMove(int dx, int dy) {
                frog.jump(dx, dy);
                salto.play(0.6f);
            }

            @Override
            public void onTap() {
                frog.jump(0, Animal.TILE_SIZE);
                salto.play(0.6f);
            }
        }));



    }


    private void loadAssets() {
        AssetManager assetManager = game.assetManager;
        assetManager.load(AssetsDescriptor.ranaUp1);
        assetManager.load(AssetsDescriptor.ranaUp2);
        assetManager.load(AssetsDescriptor.ranaUp3);
        assetManager.load(AssetsDescriptor.ranaUp4);
        assetManager.load(AssetsDescriptor.ranaUp5);
        assetManager.load(AssetsDescriptor.ranalateral1);
        assetManager.load(AssetsDescriptor.ranalateral2);
        assetManager.load(AssetsDescriptor.ranalateral3);
        assetManager.load(AssetsDescriptor.ranalateral4);
        assetManager.load(AssetsDescriptor.ranalateral5);
        assetManager.load(AssetsDescriptor.ranamuerte);
        assetManager.load(AssetsDescriptor.convertible);
        assetManager.load(AssetsDescriptor.taxi);
        assetManager.load(AssetsDescriptor.suv);
        assetManager.load(AssetsDescriptor.van);
        assetManager.load(AssetsDescriptor.truck);
        assetManager.load(AssetsDescriptor.troncoImg);
        assetManager.load(AssetsDescriptor.cesped);
        assetManager.load(AssetsDescriptor.rio);
        assetManager.load(AssetsDescriptor.road);
        assetManager.load(AssetsDescriptor.saltoSound);
        assetManager.load(AssetsDescriptor.muerteSound);
        assetManager.load(AssetsDescriptor.aguaSound);
        assetManager.finishLoading();
    }

    @Override
    public void render(float delta) {
        actualizarEstado(delta);

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.viewport.apply();
        game.camera.update();
        Filas.WORLD_WIDTH = game.getWorldWidth();

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        objetosHandler.renderFondos(game.batch);
        objetosHandler.renderCoches(game.batch);
        frog.draw(game.batch);
        drawHud();
        game.batch.end();
    }

    private void actualizarEstado(float delta) {
        frog.update(delta);

        if (deathTimer >= 0f) {
            deathTimer -= delta;
            if (deathTimer <= 0f) {
                resolverFinDeGolpe();
            }
            return;
        }

        if (respawnInvulnerableTimer > 0f) {
            respawnInvulnerableTimer = Math.max(0f, respawnInvulnerableTimer - delta);
        }

        if (messageTimer > 0f) {
            messageTimer -= delta;
            if (messageTimer <= 0f) {
                statusMessage = "";
            }
        }


        int puntosGanados = objetosHandler.avanzarMapaSiHaceFalta(frog);
        if (puntosGanados > 0) {
            int puntuacionPrevia = puntuacion;
            puntuacion += puntosGanados;

            // Ganar una vida cada 100 puntos
            if (puntuacion / 100 > puntuacionPrevia / 100) {
                frog.ganarVida();
                statusMessage = "¡VIDA EXTRA!";
                messageTimer = 1.5f;
            }
        }

        objetosHandler.update(delta);
        ObjetosHandler.ResultadoColision resultado = objetosHandler.comprobarColisiones(frog, delta);

        if (resultado == ObjetosHandler.ResultadoColision.VIDA_PERDIDA_COCHE) {
            muerte.play(0.8f);
            manejarGolpe();
        }

        if (resultado == ObjetosHandler.ResultadoColision.VIDA_PERDIDA_AGUA) {
            agua.play(0.7f, MathUtils.random(0.8f, 1.2f), 0);
            manejarGolpe();
        }
    }

    private void manejarGolpe() {
        boolean seTerminaronLasVidas = frog.perderVida();
        muerte.play(0.6f);

        pendingGameOver = seTerminaronLasVidas;
        deathTimer = DURACION_MUERTE;
        frog.mostrarMuerte();
    }

    private void resolverFinDeGolpe() {
        deathTimer = -1f;
        objetosHandler.prepararRespawnSeguro();
        frog.resetToStartPosition();
        frog.volverAIdle();

        if (pendingGameOver) {
            pendingGameOver = false;
            cambiarPantalla(new GameOverScreen(game, puntuacion));
            return;
        }

        respawnInvulnerableTimer = RESPAWN_INVULNERABILIDAD;
        statusMessage = "Te quedan " + frog.getVidas() + " vidas";
        messageTimer = 1.5f;
    }

    private void cambiarPantalla(Screen nuevaPantalla) {
        game.setScreen(nuevaPantalla);
        dispose();
    }

    private void drawHud() {
        font.draw(game.batch, "Vidas: " + frog.getVidas(), 20f, 460f);
        layout.setText(font, "Score: " + puntuacion);
        font.draw(game.batch, "Score: " + puntuacion, Filas.WORLD_WIDTH - layout.width - 20f, 460f);

        if (statusMessage != null && !statusMessage.isEmpty()) {
            layout.setText(font, statusMessage);
            float statusX = (Filas.WORLD_WIDTH - layout.width) / 2f;
            font.draw(game.batch, statusMessage, statusX, 425f);
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        Filas.WORLD_WIDTH = game.getWorldWidth();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (font != null) font.dispose();
    }
}
