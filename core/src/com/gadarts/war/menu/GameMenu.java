package com.gadarts.war.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.gadarts.shared.SharedC.AssetRelated;
import com.gadarts.war.DefaultGameSettings;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameC.Menu.CactusIcons;
import com.gadarts.war.GameC.Menu.MainMenu;
import com.gadarts.war.menu.input.MenuInputEventsSubscriber;
import com.gadarts.war.menu.input.MenuInputHandler;
import com.gadarts.war.menu.input.definitions.MenuInputDefinitions;
import com.gadarts.war.screens.BaseGameScreen;
import com.gadarts.war.screens.BattleScreen;

public class GameMenu extends Table implements MenuInputEventsSubscriber {
    private final MenuOptionsTable menuOptionsTable;
    private final BaseGameScreen parentScreen;
    private int selected;
    private Image leftCactusIcon;
    private Image rightCactusIcon;

    public GameMenu(BaseGameScreen parentScreen) {
        this.parentScreen = parentScreen;
        addLogo();
        menuOptionsTable = new MenuOptionsTable();
        menuOptionsTable.setName(GameC.Menu.NAME_OPTIONS_TABLE);
        add(menuOptionsTable).row();
        addCactusIcons();
    }

    private void addLogo() {
        GameAssetManager assetManager = GameAssetManager.getInstance();
        TextureAtlas menuAtlas = assetManager.getGameAsset(
                AssetRelated.ATLAS_ASSET_PREFIX,
                MainMenu.ATLAS_NAME,
                TextureAtlas.class);
        TextureAtlas.AtlasRegion region = menuAtlas.findRegion(GameC.Menu.LOGO_NAME);
        Image logo = new Image(region);
        logo.setScaling(Scaling.none);
        add(logo).size(region.getRegionWidth(), region.getRegionHeight()).row();
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            update();
        }
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
    }

    private void addCactusIcons() {
        GameAssetManager am = GameAssetManager.getInstance();
        TextureAtlas atlas = am.getGameAsset(AssetRelated.ATLAS_ASSET_PREFIX, MainMenu.ATLAS_NAME, TextureAtlas.class);
        TextureAtlas.AtlasRegion cactusIcon = atlas.findRegion(CactusIcons.REGION_NAME);
        leftCactusIcon = createCactusIcon(cactusIcon);
        rightCactusIcon = createCactusIcon(cactusIcon);
    }

    private Image createCactusIcon(TextureAtlas.AtlasRegion cactusIcon) {
        Image cactus = new Image(cactusIcon);
        cactus.setOrigin(cactus.getWidth() / 2, cactus.getHeight() / 2);
        addActor(cactus);
        Action action = createCactusIconAction();
        cactus.addAction(Actions.forever(action));
        return cactus;
    }

    private ParallelAction createCactusIconAction() {
        float duration = 0.5f;
        return Actions.sequence(
                Actions.moveBy(0, -10, duration, Interpolation.exp5),
                Actions.moveBy(0, 10, duration, Interpolation.exp5));
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        update();
    }

    public void update() {
        menuOptionsTable.repositionCacti(selected, leftCactusIcon, rightCactusIcon);
    }


    @Override
    public void onKeyDown(int key) {
        MenuInputDefinitions event = MenuInputDefinitions.findByKey(key);
        if (event != null) {
            event.getDef().execute(this, parentScreen);
        }
    }

    public MenuOptionsTable getMenuOptionsTable() {
        return menuOptionsTable;
    }

    private void createMenuTable(Stage stage) {
        MenuInputHandler menuInputHandler = new MenuInputHandler();
        menuInputHandler.subscribeForInputEvents(this);
        if (!DefaultGameSettings.SPECTATOR) {
            InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
            InputProcessor processor;
            if (multiplexer != null) {
                multiplexer.addProcessor(menuInputHandler);
                processor = multiplexer;
            } else {
                processor = menuInputHandler;
            }
            Gdx.input.setInputProcessor(processor);
        }
        stage.addActor(this);
    }

    public void initialize(Stage stage) {
        BitmapFont font = GameAssetManager.getInstance().get("cactus_med.ttf", BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        stage.addActor(new Label(GameC.General.GAME + " v" + GameC.General.VERSION, labelStyle));
        createMenuTable(stage);
        addMenuOptions(labelStyle);
        menuOptionsTable.addIndicators(leftCactusIcon, rightCactusIcon);
        update();
        setVisible(BattleScreen.isPaused());
    }

    private void addMenuOptions(Label.LabelStyle labelStyle) {
        GameMenuOptions[] options = GameMenuOptions.values();
        for (GameMenuOptions option : options) {
            GameMenuOption menuOption = new GameMenuOption(option, labelStyle);
            menuOption.setName(option.name());
            menuOptionsTable.addOption(menuOption);
        }
    }


}
