package com.juane.saltagranota.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Drop extends Game {
    private static final String PREFS_NAME = "granota_prefs"; // Nombre de las preferencias
    private static final String HIGH_SCORE_KEY = "high_score"; // Clave para guardar la puntuación más alta


    public SpriteBatch batch;
    public AssetManager assetManager;
    public Viewport viewport;
    public OrthographicCamera camera;
    private Preferences preferences;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assetManager = new AssetManager();
        preferences = Gdx.app.getPreferences(PREFS_NAME);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        viewport = new ExtendViewport(800, 480, camera);
        setScreen(new MenuScreen(this));
    }

    public int getHighScore() {
        return preferences.getInteger(HIGH_SCORE_KEY, 0);
    }

    public boolean guardarHighScoreSiMejora(int puntuacion) {
        if (puntuacion <= getHighScore()) {
            return false;
        }

        preferences.putInteger(HIGH_SCORE_KEY, puntuacion);
        preferences.flush();
        return true;
    }

    public float getWorldWidth() {
        return viewport.getWorldWidth();
    }

    public float getWorldHeight() {
        return viewport.getWorldHeight();
    }

    @Override
    public void dispose() {
        batch.dispose();
        assetManager.dispose();
    }
}
