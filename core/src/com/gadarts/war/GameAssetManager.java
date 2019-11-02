package com.gadarts.war;

import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.gadarts.shared.SharedC;
import com.gadarts.shared.par.AssetManagerWrapper;
import com.gadarts.shared.par.MainParLoadingFailureException;
import com.gadarts.shared.par.ParReader;

import java.io.File;
import java.io.IOException;

public class GameAssetManager extends AssetManagerWrapper {
    private static final GameAssetManager instance = new GameAssetManager();

    public static GameAssetManager getInstance() {
        return instance;
    }

    void loadAssets() throws IOException, MainParLoadingFailureException {
        ParReader reader = new ParReader();
        reader.loadMainParData(GameC.Files.MAIN_PAR_FILE, this);
        GameAssetManager gameAssetManager = GameAssetManager.getInstance();
        gameAssetManager.load(GameC.Files.TEXTURES_FOLDER_NAME + "/" + SharedC.TILE_FILE_NAME, TextureAtlas.class);
        loadModels(gameAssetManager);
        loadSounds(gameAssetManager);
        gameAssetManager.finishLoading();
    }

    private void loadModels(GameAssetManager gameAssetManager) {
        ModelLoader.ModelParameters param = new ModelLoader.ModelParameters();
        param.textureParameter.genMipMaps = true;
        gameAssetManager.load(GameC.Files.MODELS_FOLDER_NAME + "/" + "artillery.g3dj", Model.class, param);
        gameAssetManager.load(GameC.Files.MODELS_FOLDER_NAME + "/" + "street_lamp.g3dj", Model.class, param);
    }

    private void loadSounds(GameAssetManager gameAssetManager) {
        File fileHandler = new File(GameC.Files.SOUNDS_FOLDER_NAME);
        File[] files = fileHandler.listFiles();
        for (File file : files) {
            String[] split = file.getName().split("\\.");
            if (!split[split.length - 1].equals(GameC.Files.SOUND_FORMAT)) continue;
            gameAssetManager.load(GameC.Files.SOUNDS_FOLDER_NAME + "/" + file.getName(), Sound.class);
        }
    }

}
