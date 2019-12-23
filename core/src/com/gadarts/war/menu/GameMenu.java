package com.gadarts.war.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.SnapshotArray;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameC.Menu.CactusIcons;
import com.gadarts.war.GameScreen;
import com.gadarts.war.menu.input.MenuInputEventsSubscriber;
import com.gadarts.war.menu.input.MenuInputHandler;
import com.gadarts.war.menu.input.definitions.MenuInputDefinitions;

public class GameMenu extends Table implements MenuInputEventsSubscriber {
    private final Table optionsTable;
    private final GameScreen parentScreen;
    private int selected;
    private Image leftCactusIcon;
    private Image rightCactusIcon;

    public GameMenu(MenuInputHandler menuInputHandler, GameScreen parentScreen) {
        this.parentScreen = parentScreen;
        menuInputHandler.subscribeForInputEvents(this);
        addHeader();
        optionsTable = new Table();
        optionsTable.setName(GameC.Menu.NAME_OPTIONS_TABLE);
        add(optionsTable).row();
        addCactusIcons();
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

    private void addHeader() {
        BitmapFont bigFont = GameAssetManager.getInstance().get("cactus_big.ttf", BitmapFont.class);
        add(new Label(GameC.General.GAME, new Label.LabelStyle(bigFont, Color.WHITE))).row();
    }

    private void addCactusIcons() {
        String fileName = GameC.Files.TEXTURES_FOLDER_NAME + "/cactus_icon.png";
        leftCactusIcon = createCactusIcon(fileName);
        rightCactusIcon = createCactusIcon(fileName);
    }

    private Image createCactusIcon(String fileName) {
        Image cactus = new Image(GameAssetManager.getInstance().get(fileName, Texture.class));
        cactus.setOrigin(cactus.getWidth() / 2, cactus.getHeight() / 2);
        addActor(cactus);
        Action action = createCactusIconAction();
        cactus.addAction(Actions.forever(action));
        return cactus;
    }

    private ParallelAction createCactusIconAction() {
        float duration = 0.5f;
        SequenceAction movementAction = Actions.sequence(
                Actions.moveBy(0, -10, duration, Interpolation.exp5),
                Actions.moveBy(0, 10, duration, Interpolation.exp5));
        return movementAction;
    }

    public void addOption(GameMenuOption menuOption) {
        optionsTable.add(menuOption).row();
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
        update();
    }

    public void update() {
        float optionsTableHeight = optionsTable.getHeight();
        boolean visible = optionsTableHeight != 0;
        leftCactusIcon.setVisible(visible);
        rightCactusIcon.setVisible(visible);
        repositionCacti();
    }

    private void repositionCacti() {
        SnapshotArray<Actor> items = optionsTable.getChildren();
        GameMenuOption option = (GameMenuOption) items.get(selected);
        float halfTable = optionsTable.getPrefWidth() / 2;
        float leftX = -leftCactusIcon.getWidth() / 2 - halfTable - CactusIcons.MARGIN;
        float rightX = -rightCactusIcon.getWidth() / 2 + halfTable + CactusIcons.MARGIN;
        float y = option.getY() + leftCactusIcon.getHeight() / 2 - optionsTable.getHeight() + option.getHeight();
        leftCactusIcon.setPosition(leftX, y);
        rightCactusIcon.setPosition(rightX, y);
    }

    @Override
    public void onKeyDown(int key) {
        MenuInputDefinitions event = MenuInputDefinitions.findByKey(key);
        if (event != null) {
            event.getDef().execute(this, parentScreen);
        }
    }

    public Table getOptionsTable() {
        return optionsTable;
    }
}
