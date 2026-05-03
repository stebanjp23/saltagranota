package com.juane.saltagranota.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.juane.saltagranota.Objetos.Animal;

public class WinScreen implements Screen {
    private final Drop game;
    private final int vidasRestantes;
    private ShapeRenderer shapeRenderer;
    private BitmapFont titleFont;
    private BitmapFont bodyFont;
    private GlyphLayout layout;

    public WinScreen(Drop game, int vidasRestantes) {
        this.game = game;
        this.vidasRestantes = vidasRestantes;
    }

    @Override
    public void show() {
        shapeRenderer = new ShapeRenderer();
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.4f);
        titleFont.setColor(Color.WHITE);

        bodyFont = new BitmapFont();
        bodyFont.getData().setScale(1.25f);
        bodyFont.setColor(Color.WHITE);

        layout = new GlyphLayout();
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                volverAlMenu();
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.05f, 0.2f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.viewport.apply();
        game.camera.update();

        shapeRenderer.setProjectionMatrix(game.camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.08f, 0.35f, 0.12f, 1f);
        shapeRenderer.rect(0f, 0f, game.getWorldWidth(), game.getWorldHeight());
        shapeRenderer.setColor(0.8f, 0.7f, 0.18f, 1f);
        shapeRenderer.rect((game.getWorldWidth() - 600f) / 2f, 110f, 600f, 220f);
        shapeRenderer.end();

        game.batch.setProjectionMatrix(game.camera.combined);
        game.batch.begin();
        drawCentered(titleFont, "VICTORIA", 300f);
        drawCentered(bodyFont, "Llegaste a la meta con " + vidasRestantes + " vidas", 235f);
        drawCentered(bodyFont, "Toca la pantalla para volver al menu", 180f);
        game.batch.end();
    }

    private void drawCentered(BitmapFont font, String text, float y) {
        layout.setText(font, text);
        float x = (game.getWorldWidth() - layout.width) / 2f;
        font.draw(game.batch, text, x, y);
    }

    private void volverAlMenu() {
        game.setScreen(new MenuScreen(game));
        dispose();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
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
        shapeRenderer.dispose();
        titleFont.dispose();
        bodyFont.dispose();
    }
}
