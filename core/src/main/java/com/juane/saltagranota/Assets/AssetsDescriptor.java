package com.juane.saltagranota.Assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class AssetsDescriptor {
    public static final AssetDescriptor<Texture> convertible =
            new AssetDescriptor<>("Carros/convertible.png", Texture.class);
    public static final AssetDescriptor<Texture> taxi =
            new AssetDescriptor<>("Carros/taxi.png", Texture.class);
    public static final AssetDescriptor<Texture> suv =
            new AssetDescriptor<>("Carros/suv.png", Texture.class);
    public static final AssetDescriptor<Texture> van =
            new AssetDescriptor<>("Carros/van.png", Texture.class);
    public static final AssetDescriptor<Texture> truck =
            new AssetDescriptor<>("Carros/truck.png", Texture.class);
    public static final AssetDescriptor<Texture> ranaUp1 =
            new AssetDescriptor<>("Animales/rana_up1.png", Texture.class);
    public static final AssetDescriptor<Texture> ranaUp2 =
            new AssetDescriptor<>("Animales/rana_up2.png", Texture.class);
    public static final AssetDescriptor<Texture> ranaUp3 =
            new AssetDescriptor<>("Animales/rana_up3.png", Texture.class);
    public static final AssetDescriptor<Texture> ranaUp4 =
            new AssetDescriptor<>("Animales/rana_up4.png", Texture.class);
    public static final AssetDescriptor<Texture> ranaUp5 =
            new AssetDescriptor<>("Animales/rana_up5.png", Texture.class);
    public static final AssetDescriptor<Texture> ranalateral1 =
        new AssetDescriptor<>("Animales/rana_lateral1.png", Texture.class);
    public static final AssetDescriptor<Texture> ranalateral2 =
        new AssetDescriptor<>("Animales/rana_lateral2.png", Texture.class);
    public static final AssetDescriptor<Texture> ranalateral3 =
        new AssetDescriptor<>("Animales/rana_lateral3.png", Texture.class);
    public static final AssetDescriptor<Texture> ranalateral4 =
        new AssetDescriptor<>("Animales/rana_lateral4.png", Texture.class);
    public static final AssetDescriptor<Texture> ranalateral5 =
        new AssetDescriptor<>("Animales/rana_lateral5.png", Texture.class);
    public static final AssetDescriptor<Texture> ranamuerte =
            new AssetDescriptor<>("Animales/rana_muerte.png", Texture.class);
    public static final AssetDescriptor<Texture> troncoImg =
            new AssetDescriptor<>("troco1.png", Texture.class);
    public static final AssetDescriptor<Texture> cesped =
        new AssetDescriptor<>("cesped.png", Texture.class);
    public static final AssetDescriptor<Texture> rio =
        new AssetDescriptor<>("Rio.png", Texture.class);
    public static final AssetDescriptor<Texture> road =
        new AssetDescriptor<>("Road.png", Texture.class);

    // Sonidos (Note: directory is "souds" according to file listing)
    public static final AssetDescriptor<Sound> saltoSound =
            new AssetDescriptor<>("souds/sound-frogger-hop.wav", Sound.class);
    public static final AssetDescriptor<Sound> muerteSound =
            new AssetDescriptor<>("souds/sound-frogger-squash.wav", Sound.class);
    public static final AssetDescriptor<Sound> aguaSound =
            new AssetDescriptor<>("souds/sound-frogger-plunk.wav", Sound.class);
    public static final AssetDescriptor<Music> musicaFondo =
            new AssetDescriptor<>("souds/frogger-music.mp3", Music.class);
}
