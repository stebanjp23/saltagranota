package com.juane.saltagranota.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.juane.saltagranota.Screens.Drop;
import com.juane.saltagranota.Screens.MenuScreen;

public class GameOverScreen implements Screen {

    private final Drop game;
    private final int puntuacionFinal;
    private int highScore;
    private boolean nuevoRecord;

    private Stage stage;
    private Texture background;
    private BitmapFont font;

    public GameOverScreen(Drop game, int puntuacionFinal) {
        this.game = game;
        this.puntuacionFinal = puntuacionFinal;

        nuevoRecord = game.guardarHighScoreSiMejora(puntuacionFinal);
        highScore = game.getHighScore();
    }

    @Override
    public void show() {

        stage = new Stage(new FitViewport(800, 480));
        Gdx.input.setInputProcessor(stage);

        // Fondo (igual que menú)
        background = new Texture("dead_rana.png");
        Image bg = new Image(background);
        bg.setFillParent(true);
        stage.addActor(bg);

        // Fuente
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/PixelifySans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 32;
        parameter.color = Color.WHITE;
        parameter.borderWidth = 2;
        parameter.borderColor = Color.BLACK;
        font = generator.generateFont(parameter);
        generator.dispose();

        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        // TEXTOS
        Label title = new Label("GAME OVER", style);
        Label score = new Label("Puntuación: " + puntuacionFinal, style);
        Label best = new Label("Record: " + highScore, style);

        Label recordLabel = new Label(
            nuevoRecord ? "¡Nuevo record!" : "",
            style
        );

        // BOTÓN
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = font;

        TextButton btnMenu = new TextButton("VOLVER AL MENU", btnStyle);

        btnMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        // TABLE (centrado automático)
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        table.center();

        table.add(title).padBottom(30);
        table.row();

        table.add(score).padBottom(10);
        table.row();

        table.add(best).padBottom(20);
        table.row();

        table.add(recordLabel).padBottom(30);
        table.row();

        table.add(btnMenu).width(300).height(70);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void dispose() {
        stage.dispose();
        background.dispose();
        font.dispose();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
