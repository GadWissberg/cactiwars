package com.gadarts.war;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.gadarts.shared.par.AssetManagerWrapper;
import com.gadarts.shared.par.MainParLoadingFailureException;
import com.gadarts.shared.par.ParInflater;
import com.gadarts.war.GameC.Files;
import com.gadarts.war.GameC.Files.Font;

import java.io.File;
import java.io.IOException;

public class GameAssetManager extends AssetManagerWrapper {
    private static final GameAssetManager instance = new GameAssetManager();

    public static GameAssetManager getInstance() {
        return instance;
    }

    void loadAssets() throws IOException, MainParLoadingFailureException {
        ParInflater inflater = new ParInflater();
        inflater.inflatePar(inflater.readPar(Files.MAIN_PAR_FILE), this);
        GameAssetManager gameAssetManager = GameAssetManager.getInstance();
        loadModels(gameAssetManager);
        loadSounds(gameAssetManager);
        loadFonts(gameAssetManager);
        gameAssetManager.finishLoading();
    }

    private void loadModels(GameAssetManager gameAssetManager) {
        ModelLoader.ModelParameters param = new ModelLoader.ModelParameters();
        param.textureParameter.genMipMaps = true;
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "tank.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "tank_head.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "tank_aux.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "street_lamp.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "rock_1.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "rock_2.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "rock_3.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "menu_dec_barrel.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "menu_dec_saguaro.g3dj", Model.class, param);
        gameAssetManager.load(Files.MODELS_FOLDER_NAME + "/" + "menu_dec_prickly.g3dj", Model.class, param);
    }

    private void loadSounds(GameAssetManager gameAssetManager) {
        File fileHandler = new File(Files.Sound.FOLDER_PATH);
        File[] files = fileHandler.listFiles();
        for (File file : files) {
            String[] split = file.getName().split("\\.");
            if (!split[split.length - 1].equals(Files.Sound.FORMAT)) continue;
            gameAssetManager.load(Files.Sound.FOLDER_PATH + "/" + file.getName(), Sound.class);
        }
    }

    private void loadFonts(GameAssetManager gameAssetManager) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        gameAssetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        gameAssetManager.setLoader(BitmapFont.class, Font.FORMAT, new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName = Font.FOLDER_PATH + "/" + "cactus." + Font.FORMAT;
        params.fontParameters.size = Font.Size.BIG;
        params.fontParameters.color = new Color(0f, 64f, 0f, 1f);
        params.fontParameters.borderColor = new Color(0f, 0, 0f, 0.8f);
        params.fontParameters.borderWidth = 2f;
        params.fontParameters.shadowColor = new Color(0f, 0, 0f, 0.5f);
        params.fontParameters.shadowOffsetX = -2;
        params.fontParameters.shadowOffsetY = 2;
        params.fontParameters.borderStraight = true;
        params.fontParameters.kerning = true;
        gameAssetManager.load("cactus_big.ttf", BitmapFont.class, params);
        params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        params.fontFileName = Font.FOLDER_PATH + "/" + "cactus." + Font.FORMAT;
        params.fontParameters.size = Font.Size.MED;
        params.fontParameters.color = new Color(0f, 64f, 0f, 1f);
        params.fontParameters.borderColor = new Color(0f, 0, 0f, 0.8f);
        params.fontParameters.borderWidth = 2f;
        params.fontParameters.shadowColor = new Color(0f, 0, 0f, 0.5f);
        params.fontParameters.shadowOffsetX = -2;
        params.fontParameters.shadowOffsetY = 2;
        params.fontParameters.borderStraight = true;
        params.fontParameters.kerning = true;
        gameAssetManager.load("cactus_med.ttf", BitmapFont.class, params);
    }

}
