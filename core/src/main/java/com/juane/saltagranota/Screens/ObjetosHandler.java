package com.juane.saltagranota.Screens;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.juane.saltagranota.Assets.AssetsDescriptor;
import com.juane.saltagranota.Mapa.Filas;
import com.juane.saltagranota.Objetos.Animal;
import com.juane.saltagranota.Objetos.Carro;
import com.juane.saltagranota.Objetos.Tronco;

public class ObjetosHandler {
    public enum ResultadoColision {
        NINGUNO,
        VIDA_PERDIDA_COCHE,
        VIDA_PERDIDA_AGUA
    }

    private static final float WORLD_HEIGHT = 480f;
    private static final float SCROLL_TRIGGER_Y = 300f;
    private static final float TOP_BUFFER = Filas.ALTURA_FILA * 2f;

    private final Array<Filas> filas;
    private final Array<Texture> texturasCarro;
    private final Texture textureTronco;
    private final Texture texturaCesped;
    private final Texture texturaRio;
    private final Texture texturaCarretera;
    private boolean siguienteHaciaDerecha;
    private int siguientePeligro;
    private int siguienteFilaLogica;

    public ObjetosHandler(AssetManager assetManager) {
        filas = new Array<>();
        texturasCarro = new Array<>();
        texturasCarro.add(assetManager.get(AssetsDescriptor.convertible));
        texturasCarro.add(assetManager.get(AssetsDescriptor.taxi));
        texturasCarro.add(assetManager.get(AssetsDescriptor.suv));
        texturasCarro.add(assetManager.get(AssetsDescriptor.van));
        texturasCarro.add(assetManager.get(AssetsDescriptor.truck));
        textureTronco = assetManager.get(AssetsDescriptor.troncoImg);
        texturaCesped = assetManager.get(AssetsDescriptor.cesped);
        texturaRio = assetManager.get(AssetsDescriptor.rio);
        texturaCarretera = assetManager.get(AssetsDescriptor.road);
        crearNivelInicial();
    }

    private void crearNivelInicial() {
        for (int i = 0; i < 8; i++) {
            filas.add(crearFilaInicial(i));
        }

        siguienteHaciaDerecha = false;
        siguientePeligro = 3;
        siguienteFilaLogica = 8;
        rellenarParteSuperior();
    }

    private Filas crearFilaInicial(int indiceFila) {
        Filas.Tipo tipo;

        if (indiceFila == 0 || indiceFila == 4 || indiceFila == 7) {
            tipo = Filas.Tipo.CESPED;
        } else if (indiceFila >= 5 && indiceFila <= 6) {
            tipo = Filas.Tipo.AGUA;
        } else {
            tipo = Filas.Tipo.CARRETERA;
        }

        Filas fila = new Filas(indiceFila * Filas.ALTURA_FILA, tipo);
        poblarFilaSiEsNecesario(fila, indiceFila == 1 || indiceFila == 3 || indiceFila == 6, contarPeligroInicial(indiceFila));
        return fila;
    }

    private int contarPeligroInicial(int indiceFila) {
        if (indiceFila <= 0) {
            return 0;
        }
        if (indiceFila == 1) {
            return 0;
        }
        if (indiceFila == 2) {
            return 1;
        }
        if (indiceFila == 3) {
            return 2;
        }
        if (indiceFila == 5) {
            return 3;
        }
        if (indiceFila == 6) {
            return 4;
        }
        return 0;
    }

    private void poblarFilaSiEsNecesario(Filas fila, boolean vaHaciaDerecha, int indicePeligro) {
        float velocidadBase = 70f + (Math.min(indicePeligro, 8) * 12f);
        float velocidad = vaHaciaDerecha ? velocidadBase : -velocidadBase;

        if (fila.tipo == Filas.Tipo.CARRETERA) {
            poblarCarretera(fila, velocidad, vaHaciaDerecha, indicePeligro);
        } else if (fila.tipo == Filas.Tipo.AGUA) {
            poblarRio(fila, velocidad, vaHaciaDerecha, indicePeligro);
        }
    }


    private void poblarCarretera(Filas fila, float velocidad, boolean vaHaciaDerecha, int indicePeligro) {
        int cantidadCarros = 1 + Math.min(indicePeligro / 3, 2);

        // Distribuimos los coches equitativamente en el ancho de la pantalla
        float espacioEntreCoches = Filas.WORLD_WIDTH / cantidadCarros;
        float desplazamientoInicial = MathUtils.random(0, espacioEntreCoches);

        for (int i = 0; i < cantidadCarros; i++) {
            Texture textura = texturasCarro.get(MathUtils.random(texturasCarro.size - 1));

            // Posición inicial repartida
            float posicionX = desplazamientoInicial + (i * espacioEntreCoches);

            // Ajustamos el tamaño según el tipo de vehículo (opcional, pero ayuda visualmente)
            // Por ahora mantenemos tu 72x39, pero podrías variarlo según la textura
            Carro carro = new Carro(textura, posicionX, fila.y + 11f, velocidad);

            if (!vaHaciaDerecha) {
                carro.flip(true, false);
            }
            fila.agregarCarro(carro);
        }
    }

    private void poblarRio(Filas fila, float velocidad, boolean vaHaciaDerecha, int indicePeligro) {

        int cantidadTroncos = indicePeligro >= 6 ? 2 : 3;

        float separacion = 320f; // distancia entre troncos
        float xActual = MathUtils.random(0, 150);

        for (int i = 0; i < cantidadTroncos; i++) {

            Tronco tronco = new Tronco(textureTronco, xActual, fila.y + 2f, velocidad);

            if (!vaHaciaDerecha) {
                tronco.flip(true, false);
            }

            fila.agregarTronco(tronco);

            xActual += separacion;
        }
    }

    private void rellenarParteSuperior() {
        while (getAlturaMaxima() < WORLD_HEIGHT + TOP_BUFFER) {
            generarNuevaFilaSuperior();
        }
    }

    private float getAlturaMaxima() {
        float maxY = 0f;
        for (Filas fila : filas) {
            if (fila.y > maxY) {
                maxY = fila.y;
            }
        }
        return maxY;
    }

    private void generarNuevaFilaSuperior() {
        Filas.Tipo tipo = elegirTipoParaFila(siguienteFilaLogica);
        Filas fila = new Filas(getAlturaMaxima() + Filas.ALTURA_FILA, tipo);
        poblarFilaSiEsNecesario(fila, siguienteHaciaDerecha, siguientePeligro);
        filas.add(fila);

        if (tipo != Filas.Tipo.CESPED) {
            siguienteHaciaDerecha = !siguienteHaciaDerecha;
            siguientePeligro++;
        }
        siguienteFilaLogica++;
    }

    private Filas.Tipo elegirTipoParaFila(int filaLogica) {
        if (filaLogica % 5 == 0) {
            return Filas.Tipo.CESPED;
        }
        return MathUtils.randomBoolean(0.35f) ? Filas.Tipo.AGUA : Filas.Tipo.CARRETERA;
    }

    public int avanzarMapaSiHaceFalta(Animal frog) {
        int puntosGanados = 0;

        while (frog.getY() >= SCROLL_TRIGGER_Y) {
            frog.setY(frog.getY() - Filas.ALTURA_FILA);
            desplazarFilas(-Filas.ALTURA_FILA);
            eliminarFilasFueraDePantalla();
            rellenarParteSuperior();
            puntosGanados++;
        }

        return puntosGanados;
    }

    private void desplazarFilas(float deltaY) {
        for (Filas fila : filas) {
            fila.shiftY(deltaY);
        }
    }

    private void eliminarFilasFueraDePantalla() {
        for (int i = filas.size - 1; i >= 0; i--) {
            Filas fila = filas.get(i);
            if (fila.y + Filas.ALTURA_FILA <= 0f) {
                filas.removeIndex(i);
            }
        }
    }

    public void prepararRespawnSeguro() {
        Filas filaSegura = buscarFilaSeguraInferior();

        if (filaSegura == null) {
            return;
        }

        if (filaSegura.y != 0f) {
            desplazarFilas(-filaSegura.y);
            eliminarFilasFueraDePantalla();
            rellenarParteSuperior();
        }
    }

    private Filas buscarFilaSeguraInferior() {
        Filas mejorFila = null;

        for (Filas fila : filas) {
            if (fila.tipo != Filas.Tipo.CESPED || fila.y < 0f) {
                continue;
            }

            if (mejorFila == null || fila.y < mejorFila.y) {
                mejorFila = fila;
            }
        }

        return mejorFila;
    }

    public void update(float delta) {
        for (Filas fila : filas) {
            fila.update(delta);
        }
    }

    public ResultadoColision comprobarColisiones(Animal frog, float delta) {
        float centroRanaY = frog.getY() + (frog.getHeight() / 2f);
        Filas filaActual = buscarFilaActual(centroRanaY);

        if (filaActual == null) {
            return ResultadoColision.NINGUNO;
        }
        Rectangle rectRana = frog.getBoundingRectangle();

        if (filaActual.tipo == Filas.Tipo.CARRETERA) {
            Rectangle hitboxRana = new Rectangle(
                    rectRana.x + 10f,
                    rectRana.y + 10f,
                    rectRana.width - 20f,
                    rectRana.height - 20f
            );

            for (Carro carro : filaActual.carros) {
                Rectangle hitboxCarro = carro.getBoundingRectangle();
                hitboxCarro.set(
                        hitboxCarro.x + 5f,
                        hitboxCarro.y + 5f,
                        hitboxCarro.width - 10f,
                        hitboxCarro.height - 10f
                );

                if (hitboxRana.overlaps(hitboxCarro)) {

                    return ResultadoColision.VIDA_PERDIDA_COCHE;
                }
            }
        } else if (filaActual.tipo == Filas.Tipo.AGUA) {
            boolean sobreTronco = false;

            for (Tronco tronco : filaActual.troncos) {
                if (rectRana.overlaps(tronco.getBoundingRectangle())) {
                    sobreTronco = true;
                    frog.setX(frog.getX() + tronco.getVelocidad() * delta);
                    break;
                }
            }

            if (!sobreTronco) {

                return ResultadoColision.VIDA_PERDIDA_AGUA;
            }
        }

        if (frog.getX() < 0f) {
            frog.setX(0f);
        }
        if (frog.getX() + frog.getWidth() > Filas.WORLD_WIDTH) {
            frog.setX(Filas.WORLD_WIDTH - frog.getWidth());
        }

        return ResultadoColision.NINGUNO;
    }

    private Filas buscarFilaActual(float centroRanaY) {
        for (Filas fila : filas) {
            if (centroRanaY >= fila.y && centroRanaY < fila.y + Filas.ALTURA_FILA) {
                return fila;
            }
        }
        return null;
    }

    public void renderFondos(SpriteBatch batch) {
        for (Filas fila : filas) {
            fila.renderFondo(batch, texturaCesped, texturaRio, texturaCarretera);
        }
    }

    public void renderCoches(SpriteBatch batch) {
        for (Filas fila : filas) {
            fila.renderCoches(batch);
        }
    }
}
