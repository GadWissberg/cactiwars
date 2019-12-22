package com.gadarts.war.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.ParallelAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.gadarts.war.BattleScreen;
import com.gadarts.war.GameAssetManager;
import com.gadarts.war.GameC;
import com.gadarts.war.GameC.Menu.CactusIcons;

public class GameMenu extends Table {
    private final Table optionsTable;
    private final MenuInputHandler input;
    private int selected;
    private Image leftCactusIcon;
    private Image rightCactusIcon;

    public GameMenu(MenuInputHandler menuInputHandler) {
        this.input = menuInputHandler;
        setVisible(BattleScreen.isPaused());
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
            InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
            multiplexer.getProcessors();
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

    private void update() {
        GameMenuOption option = (GameMenuOption) optionsTable.getChildren().items[selected];
        float halfTable = optionsTable.getPrefWidth() / 2;
        float tableX = optionsTable.getX();
        float leftX = tableX - leftCactusIcon.getWidth() / 2 - halfTable - CactusIcons.MARGIN;
        float rightX = tableX - rightCactusIcon.getWidth() / 2 + halfTable + CactusIcons.MARGIN;
        float y = option.getY() + leftCactusIcon.getHeight() / 2;
        leftCactusIcon.setPosition(leftX, y);
        rightCactusIcon.setPosition(rightX, y);
    }
}
