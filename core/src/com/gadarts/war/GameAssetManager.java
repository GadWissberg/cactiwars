package com.gadarts.war;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g3d.Model;
import com.gadarts.shared.SharedC.AssetRelated;
import com.gadarts.shared.definitions.AtlasDefinition;
import com.gadarts.shared.definitions.Definitions;
import com.gadarts.shared.par.*;
import com.gadarts.shared.par.inflations.DefinitionType;
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
        createAtlases();
    }

    private void createAtlases() {
        String fileName = SectionType.DEF + AssetRelated.ASSET_NAME_SEPARATOR + DefinitionType.ATLASES.name().toLowerCase();
        Definitions<AtlasDefinition> atlasesDef = getInstance().get(fileName, Definitions.class);
        atlasesDef.getDefinitions().forEach((defName, atlasDefinition) -> {
            PixmapPacker p = new PixmapPacker(Files.ATLAS_SIZE, Files.ATLAS_SIZE, Format.RGBA8888, 0, true);
            p.setPageWidth(atlasDefinition.getWidth());
            p.setPageHeight(atlasDefinition.getHeight());
            atlasDefinition.getTextures().forEach(textureName -> {
                String pix = SectionType.PIX + AssetRelated.ASSET_NAME_SEPARATOR + textureName;
                Pixmap image = getInstance().get(pix);
                p.pack(textureName, image);
                unload(pix);
            });
            TextureAtlas atlas = p.generateTextureAtlas(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest, false);
            BaseGameAsset baseGameAsset = new BaseGameAsset(AssetRelated.ATLAS_ASSET_PREFIX + AssetRelated.ASSET_NAME_SEPARATOR + defName, atlas);
            addGameAsset(baseGameAsset, TextureAtlas.class);
            p.dispose();
        });
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
