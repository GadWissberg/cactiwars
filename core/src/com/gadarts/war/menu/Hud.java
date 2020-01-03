package com.gadarts.war.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.gadarts.war.*;
import com.gadarts.war.menu.input.MenuInputHandler;
import com.gadarts.war.sound.SFX;
import com.gadarts.war.systems.render.RenderSystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Hud {
    private final Stage stage;
    private final RenderSystem renderSystem;
    private final GameScreen parentScreen;
    private ShaderProgram blurShaderProgram;
    private Profiler profiler;
    private GameMenu menu;
    private FrameBuffer blurFrameBuffer;
    private int blurRadiusLocation;
    private int blurDirectionLocation;
    private int blurResolutionLocation;
    private Vector2 blurMag = new Vector2(1, 1);
    private ShaderProgram regularShaderProgram;
    private List<HudEventsSubscriber> subscribers = new ArrayList<>();

    public Hud(RenderSystem renderSystem, GameScreen parentScreen) {
        this.renderSystem = renderSystem;
        this.parentScreen = parentScreen;
        stage = new Stage();
        stage.setDebugAll(GameSettings.DRAW_TABLES_BORDERS);
        createMenu(parentScreen);
        stage.setViewport(new ScreenViewport(stage.getCamera()));
        profiler = new Profiler(stage, renderSystem);
        initializeBlur();
    }

    public void dispose() {
        blurShaderProgram.dispose();
        blurFrameBuffer.dispose();
        stage.dispose();
    }

    private void initializeBlur() {
        regularShaderProgram = stage.getBatch().getShader();
        String blurFragmentShader = Gdx.files.internal("shaders" + File.separator + "fragment_blur.glsl").readString();
        String blurVertexShader = Gdx.files.internal("shaders" + File.separator + "vertex_blur.glsl").readString();
        blurShaderProgram = new ShaderProgram(blurVertexShader, blurFragmentShader);
        blurFrameBuffer = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        blurRadiusLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_RADIUS);
        blurDirectionLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_DIRECTION);
        blurResolutionLocation = blurShaderProgram.getUniformLocation(GameC.Menu.BlurShaderKeys.BLUR_RESOLUTION);
    }

    private void createMenu(GameScreen parentScreen) {
        BitmapFont font = GameAssetManager.getInstance().get("cactus_med.ttf", BitmapFont.class);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        stage.addActor(new Label(GameC.General.GAME + " v" + GameC.General.VERSION, labelStyle));
        createMenuTable(parentScreen);
        addMenuOptions(labelStyle);
        menu.update();
        menu.setVisible(BattleScreen.isPaused());
    }

    private void createMenuTable(GameScreen parentScreen) {
        MenuInputHandler menuInputHandler = new MenuInputHandler();
        menu = new GameMenu(menuInputHandler, parentScreen);
        if (!GameSettings.SPECTATOR) {
            InputMultiplexer multiplexer = (InputMultiplexer) Gdx.input.getInputProcessor();
            multiplexer.addProcessor(menuInputHandler);
            Gdx.input.setInputProcessor(multiplexer);
        }
        stage.addActor(menu);
    }

    private void addMenuOptions(Label.LabelStyle labelStyle) {
        GameMenuOptions[] options = GameMenuOptions.values();
        for (GameMenuOptions option : options) {
            GameMenuOption menuOption = new GameMenuOption(option, labelStyle);
            menuOption.setName(option.name());
            menu.addOption(menuOption);
        }
    }

    private void applyBlur() {
        Batch batch = stage.getBatch();
        batch.setShader(blurShaderProgram);
        batch.getProjectionMatrix().idt();
        batch.begin();
        setBlurUniforms(blurMag.set(35f, 0f));
        batch.draw(blurFrameBuffer.getColorBufferTexture(), -1, 1, 2, -2);
        batch.end();
    }

    private void setBlurUniforms(Vector2 blur) {
        blurShaderProgram.setUniformf(blurRadiusLocation, blurMag.len2());
        blur.nor();
        blurShaderProgram.setUniformf(blurDirectionLocation, blurMag.x, blurMag.y);
        blurShaderProgram.setUniformf(blurResolutionLocation, ((float) Gdx.graphics.getWidth()));
    }

    public void render(float delta) {
        if (BattleScreen.isPaused()) {
            renderSystem.render(Gdx.graphics.getDeltaTime(), blurFrameBuffer);
            applyBlur();
            stage.getBatch().setShader(regularShaderProgram);
        }
        profiler.update();
        stage.act(delta);
        stage.draw();
        profiler.reset();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void activateMenu() {
        menu.setVisible(true);
        parentScreen.getSoundPlayer().play(SFX.MENU_SELECT);
        for (HudEventsSubscriber subscriber : subscribers) {
            subscriber.onMenuActivated();
        }
    }

    public void deactivate() {
        menu.setVisible(false);
        parentScreen.getSoundPlayer().play(SFX.MENU_SELECT);
        for (HudEventsSubscriber subscriber : subscribers) {
            subscriber.onMenuDeactivated();
        }
    }

    public void subscribeForEvents(HudEventsSubscriber subscriber) {
        if (subscribers.contains(subscriber)) return;
        subscribers.add(subscriber);
    }
}
