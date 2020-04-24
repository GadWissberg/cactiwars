package com.gadarts.war;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
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
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.utils.JsonReader;
import com.gadarts.shared.SharedC.AssetRelated;
import com.gadarts.shared.definitions.AtlasDefinition;
import com.gadarts.shared.definitions.Definitions;
import com.gadarts.shared.par.*;
import com.gadarts.shared.par.inflations.DefinitionType;
import com.gadarts.war.GameC.Files.Font;
import com.gadarts.war.menu.console.ConsoleImpl;
import com.gadarts.war.sound.GameSound;

import java.io.IOException;

public class GameAssetManager extends AssetManagerWrapper {
	private static final GameAssetManager instance = new GameAssetManager();

	public static GameAssetManager getInstance() {
		return instance;
	}

	void loadAssets(ConsoleImpl consoleImpl) throws IOException, MainParLoadingFailureException {
		setLoader(GameSound.class, new GameSoundLoader(getFileHandleResolver(), consoleImpl));
		GameAssetManager gameAssetManager = GameAssetManager.getInstance();
		tempLoading(gameAssetManager);
		ParInflater inflater = new ParInflater();
		String path = GameC.Files.ASSETS_PATH + "cactiwars.par";
		inflater.inflatePar(inflater.readPar(Gdx.files.internal(path).read()), this, consoleImpl);
		gameAssetManager.finishLoading();
		createAtlases();
	}

	private void tempLoading(GameAssetManager gameAssetManager) {
		temp_loadModels(gameAssetManager);
		loadSounds(gameAssetManager);
		loadFonts(gameAssetManager);
	}

	private void createAtlases() {
		String fileName = SectionType.DEF + AssetRelated.ASSET_NAME_SEPARATOR + DefinitionType.ATLASES.name().toLowerCase();
		Definitions<AtlasDefinition> atlasesDef = getInstance().get(fileName, Definitions.class);
		atlasesDef.getDefinitions().forEach((defName, atlasDefinition) -> {
			PixmapPacker p = new PixmapPacker(GameC.Files.ATLAS_SIZE, GameC.Files.ATLAS_SIZE, Format.RGBA8888, 0, true);
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

	private void temp_loadModels(GameAssetManager gameAssetManager) {
		ModelLoader.ModelParameters param = new ModelLoader.ModelParameters();
		param.textureParameter.genMipMaps = true;
		G3dModelLoader loader = new G3dModelLoader(new JsonReader());
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/tank", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "tank.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/tank_aux", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "tank_aux.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/tank_head", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "tank_head.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/street_lamp", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "street_lamp.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/rock_1", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "rock_1.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/rock_2", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "rock_2.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/rock_3", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "rock_3.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/menu_dec_barrel", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "menu_dec_barrel.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/menu_dec_saguaro", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "menu_dec_saguaro.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/menu_dec_prickly", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "menu_dec_prickly.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/cannon_ball", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "cannon_ball.g3dj", Files.FileType.Internal)), SectionType.MDL));
		gameAssetManager.addGameAsset(new GameAsset(SectionType.MDL + "/building_1", loader.loadModel(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "building_1.g3dj", Files.FileType.Internal)), SectionType.MDL));
		TextureLoader.TextureParameter par = new TextureLoader.TextureParameter();
		par.genMipMaps = true;
		gameAssetManager.load(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "building_1_texture_1.png", Files.FileType.Internal).path(), Texture.class, par);
		gameAssetManager.load(Gdx.files.getFileHandle(GameC.Files.MODELS_FOLDER_NAME + "/" + "building_1_texture_2.png", Files.FileType.Internal).path(), Texture.class, par);
	}

	private void loadSounds(GameAssetManager gameAssetManager) {
		FileHandle internal = Gdx.files.internal(GameC.Files.Sound.FOLDER_PATH);
		for (FileHandle file : internal.list()) {
			String[] split = file.name().split("\\.");
			if (!split[split.length - 1].equals(GameC.Files.Sound.FORMAT)) continue;
			gameAssetManager.load(GameC.Files.Sound.FOLDER_PATH + "/" + file.name(), GameSound.class);
		}
	}

	private void loadFonts(GameAssetManager gameAssetManager) {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		gameAssetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		gameAssetManager.setLoader(BitmapFont.class, Font.FORMAT, new FreetypeFontLoader(resolver));
		FreetypeFontLoader.FreeTypeFontLoaderParameter params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		params.fontFileName = Font.FOLDER_PATH + "/" + "cactus." + Font.FORMAT;
		params.fontParameters.size = Font.Size.BIG;
		defineFontParameters(params);
		gameAssetManager.load("cactus_big.ttf", BitmapFont.class, params);
		params = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		params.fontFileName = Font.FOLDER_PATH + "/" + "cactus." + Font.FORMAT;
		params.fontParameters.size = Font.Size.MED;
		defineFontParameters(params);
		gameAssetManager.load("cactus_med.ttf", BitmapFont.class, params);
	}

	private void defineFontParameters(FreetypeFontLoader.FreeTypeFontLoaderParameter params) {
		params.fontParameters.color = new Color(0f, 64f, 0f, 1f);
		params.fontParameters.borderColor = new Color(0f, 0, 0f, 0.8f);
		params.fontParameters.borderWidth = 2f;
		params.fontParameters.shadowColor = new Color(0f, 0, 0f, 0.5f);
		params.fontParameters.shadowOffsetX = -2;
		params.fontParameters.shadowOffsetY = 2;
		params.fontParameters.borderStraight = true;
		params.fontParameters.kerning = true;
	}

}
