package com.juane.saltagranota.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
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
    private float tiempo_viva;
    private int monedas;

    // UI PAUSA
    private boolean pausado;
    private Stage PauseStage;
    private Table pauseMenu;
    private Texture overlayTexture;


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

        // 2. Overlay
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.5f);
        pixmap.fill();
        overlayTexture = new Texture(pixmap);
        pixmap.dispose();

        // 3. Stage de UI
        PauseStage = new Stage(new FitViewport(800, 480)); // Usamos base 800x480 para coherencia

        // Botón de Pausa (Esquina superior derecha)
        Image btnPausa = new Image(game.assetManager.get(AssetsDescriptor.bt_pause));
        btnPausa.setSize(100, 60);
        // Lo ponemos arriba a la derecha con un margen
        btnPausa.setPosition(350, 420);
        btnPausa.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pausado = !pausado;
                pauseMenu.setVisible(pausado);
            }
        });
        PauseStage.addActor(btnPausa);

        setupPauseMenu();

        // 4. Input Multiplexer
        InputHandler gameInput = new InputHandler(new InputHandler.Listener() {
            @Override public void onMove(int dx, int dy) { if(!pausado) { frog.jump(dx, dy); salto.play(0.6f); } }
            @Override public void onTap() { if(!pausado) { frog.jump(0, Animal.TILE_SIZE); salto.play(0.6f); } }
        });

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(PauseStage); // Importante: UI primero
        multiplexer.addProcessor(gameInput);
        Gdx.input.setInputProcessor(multiplexer);


    }

    private void setupPauseMenu() {
        pauseMenu = new Table();
        pauseMenu.setFillParent(true);
        pauseMenu.setVisible(false);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        TextButton btnContinuar = new TextButton("CONTINUAR", style);
        btnContinuar.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                pausado = false;
                pauseMenu.setVisible(false);
            }
        });

        TextButton btnMenu = new TextButton("MENU", style);
        btnMenu.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        pauseMenu.add(new TextButton("PAUSA", style)).padBottom(30).row();
        pauseMenu.add(btnContinuar).padBottom(15).row();
        pauseMenu.add(btnMenu);
        PauseStage.addActor(pauseMenu);
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
        assetManager.load(AssetsDescriptor.moneda);
        assetManager.load(AssetsDescriptor.bt_pause);

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
        objetosHandler.renderPajaros(game.batch);
        objetosHandler.renderMonedas(game.batch);
        frog.draw(game.batch);


        drawHud();
        if (pausado) {
            game.batch.draw(overlayTexture, 0, 0, Filas.WORLD_WIDTH, game.getWorldHeight());
        }
        game.batch.end();

        // Dibujar UI (Botones)
        PauseStage.act(delta);
        PauseStage.draw();
    }

    private void actualizarEstado(float delta) {
        if(pausado){
            return;
        }

        frog.update(delta);
        tiempo_viva += delta;


        if (deathTimer >= 0f) { // Si estamos en modo de muerte
            deathTimer -= delta; // Actualizamos el tiempo de muerte
            if (deathTimer <= 0f) { // Si el tiempo de muerte ha terminado
                resolverFinDeGolpe(); // Resolvemos el fin de golpe
                tiempo_viva = 0;
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

            // Ganar una vida cada 10 puntos
            if (puntuacion / 10 > puntuacionPrevia / 10) {
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

        if (resultado == ObjetosHandler.ResultadoColision.VIDA_PERDIDA_PAJARO) {
            muerte.play(0.8f);
            manejarGolpe();
        }

        if (resultado == ObjetosHandler.ResultadoColision.VIDA_PERDIDA_AGUA) {
            agua.play(0.7f, MathUtils.random(0.8f, 1.2f), 0);
            manejarGolpe();
        }

        if(resultado == ObjetosHandler.ResultadoColision.MONEDA){
            monedas++;
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

        // Dibuja el numero de vidas
        font.draw(game.batch, "Vidas: " + frog.getVidas(), 20f, 460f);

        // Dibuja la puntuación
        layout.setText(font, "Score: " + puntuacion);
        font.draw(game.batch, "Score: " + puntuacion, Filas.WORLD_WIDTH - layout.width - 20f, 460f); // Posición en la pantalla

        // Dibuja el tiempo de vida
        font.draw(game.batch, "Tiempo de vida: " + (int) tiempo_viva, 20f, 400f);

        font.draw(game.batch, "Monedas: "+ monedas, 850f, 380f);



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
