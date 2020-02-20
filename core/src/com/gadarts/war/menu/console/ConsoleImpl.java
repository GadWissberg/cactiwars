package com.gadarts.war.menu.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.gadarts.war.menu.console.commands.CommandInvoke;
import com.gadarts.war.menu.console.commands.Commands;
import com.gadarts.war.menu.console.commands.ConsoleCommandResult;
import com.gadarts.war.systems.player.input.KeyMap;

public class ConsoleImpl extends Table implements Console, InputProcessor {
    public static final String NAME = "console";
    public static final Interpolation.Pow INTERPOLATION = Interpolation.pow2;
    public static final String NOT_RECOGNIZED = "'%s' is not recognized as a command.";
    public static final String INPUT_FIELD_NAME = "input";
    public static final Color INPUT_COLOR = Color.WHITE;
    static final String TEXT_LABEL_NAME = "text";
    private static final float INPUT_HEIGHT = 20f;
    private static final float PADDING = 10f;
    private static final float TRANSITION_DURATION = 0.5f;
    private static final String PARAMETER_EXPECTED = "Failed to apply command! Parameter is expected at '%s'";
    private static final String PARAMETER_VALUE_EXPECTED = "Failed to apply command! Value is expected for " +
            "parameter '%s'";

    private ConsoleTextures consoleTextures = new ConsoleTextures();
    private boolean active;
    private Array<ConsoleEventsSubscriber> subscribers = new Array<>();
    private ConsoleTextData consoleTextData;
    private ConsoleCommandResult consoleCommandResult = new ConsoleCommandResult();
    private ConsoleInputHistoryHandler consoleInputHistoryHandler;

    public ConsoleImpl() {
        consoleTextData = new ConsoleTextData();
        setName(NAME);
        float height = Gdx.graphics.getHeight() * 2f / 3f;
        setPosition(0, Gdx.graphics.getHeight());
        consoleTextures.init((int) height);
        TextureRegionDrawable textBackgroundTextureRegionDrawable = new TextureRegionDrawable(consoleTextures.getTextBackgroundTexture());
        addTextView(textBackgroundTextureRegionDrawable, (int) height);
        createInputField(textBackgroundTextureRegionDrawable);
        setBackground(new TextureRegionDrawable(consoleTextures.getBackgroundTexture()));
        setSize(Gdx.graphics.getWidth(), consoleTextures.getBackgroundTexture().getHeight());
        consoleInputHistoryHandler = new ConsoleInputHistoryHandler();
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        consoleTextData.setStage(stage);
        consoleInputHistoryHandler.setStage(stage);
    }

    private void createInputField(TextureRegionDrawable textBackgroundTexture) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle(consoleTextData.getFont(), INPUT_COLOR, new TextureRegionDrawable(consoleTextures.getCursorTexture()),
                null, textBackgroundTexture);
        TextField input = new TextField("", style);
        input.setName(INPUT_FIELD_NAME);
        Label arrow = new Label(">", consoleTextData.getTextStyle());
        add(arrow).padBottom(PADDING).padLeft(PADDING).size(10f, INPUT_HEIGHT);
        add(input).size(Gdx.graphics.getWidth() - PADDING * 3, INPUT_HEIGHT).padBottom(PADDING).padRight(PADDING).align(Align.left).row();
        input.setFocusTraversal(false);
        input.setTextFieldListener((textField, c) -> {
            if (c == KeyMap.GRAVE.getAsciiValue()) {
                textField.setText(null);
                if (!ConsoleImpl.this.hasActions()) if (isActive()) deactivate();
            } else if (c == '\r' || c == '\n') {
                applyInput(textField);
            }
        });
    }

    private void applyInput(TextField textField) {
        insertNewLog(textField.getText(), true);
        String inputCommand = textField.getText();
        consoleInputHistoryHandler.applyInput(inputCommand);
        try {
            CommandInvoke commandToInvoke;
            commandToInvoke = parseCommandFromInput(inputCommand);
            commandToInvoke.getCommand().getCommandImpl().run(this, commandToInvoke.getParameters());
        } catch (InputParsingFailureException e) {
            insertNewLog(e.getMessage(), false);
        }
        textField.setText(null);
    }

    private CommandInvoke parseCommandFromInput(String text) throws InputParsingFailureException {
        if (text == null) return null;
        String[] entries = text.toUpperCase().split(" ");
        String commandName = entries[0];
        CommandInvoke command;
        command = new CommandInvoke(Commands.findCommandByNameOrAlias(commandName));
        parseParameters(entries, command);
        return command;
    }

    private void parseParameters(String[] entries, CommandInvoke command) throws InputParsingFailureException {
        for (int i = 1; i < entries.length; i += 2) {
            String parameter = entries[i];
            if (!parameter.startsWith("-"))
                throw new InputParsingFailureException(String.format(PARAMETER_EXPECTED, parameter));
            if (i + 1 == entries.length || entries[i + 1].startsWith("-"))
                throw new InputParsingFailureException(String.format(PARAMETER_VALUE_EXPECTED, parameter));
            command.addParameter(parameter.substring(1), entries[i + 1]);
        }
    }

    @Override
    public void insertNewLog(String text, boolean logTime) {
        consoleTextData.insertNewLog(text, logTime);
    }

    @Override
    public ConsoleCommandResult notifyCommandExecution(Commands command) {
        boolean result = false;
        consoleCommandResult.clear();
        for (ConsoleEventsSubscriber sub : subscribers) {
            result |= sub.onCommandRun(command, consoleCommandResult);
        }
        consoleCommandResult.setResult(result);
        return consoleCommandResult;
    }


    private void addTextView(TextureRegionDrawable textBackgroundTexture, int consoleHeight) {
        Label.LabelStyle textStyle = consoleTextData.getTextStyle();
        textStyle.background = textBackgroundTexture;
        float width = Gdx.graphics.getWidth() - PADDING * 2;
        float height = consoleHeight - (INPUT_HEIGHT);
        Label textView = new Label(consoleTextData.getStringBuilder(), textStyle);
        textView.setAlignment(Align.bottomLeft);
        textView.setName(TEXT_LABEL_NAME);
        textView.setWrap(true);
        add(textView).colspan(2).size(width, height).align(Align.bottomLeft).padRight(PADDING).padLeft(PADDING).row();
    }


    public void activate() {
        if (active) return;
        getStage().setKeyboardFocus(getStage().getRoot().findActor(INPUT_FIELD_NAME));
        initializeInput();
        active = true;
        float amountY = -Gdx.graphics.getHeight() / 3f;
        addAction(Actions.moveBy(0, amountY, TRANSITION_DURATION, INTERPOLATION));
        setVisible(true);
        subscribers.forEach(ConsoleEventsSubscriber::onConsoleActivated);
    }

    private void initializeInput() {
        InputMultiplexer inputProcessor = (InputMultiplexer) Gdx.input.getInputProcessor();
        inputProcessor.addProcessor(this);
        inputProcessor.addProcessor(getStage());
    }

    public void deactivate() {
        if (!active) return;
        active = false;
        float amountY = Gdx.graphics.getHeight() / 3f;
        MoveByAction move = Actions.moveBy(0, amountY, TRANSITION_DURATION, Interpolation.pow2);
        addAction(Actions.sequence(move, Actions.visible(false)));
        subscribers.forEach(ConsoleEventsSubscriber::onConsoleDeactivated);
        getStage().unfocusAll();
        detachInput();
    }

    private void detachInput() {
        InputMultiplexer inputProcessor = (InputMultiplexer) Gdx.input.getInputProcessor();
        inputProcessor.removeProcessor(this);
        inputProcessor.removeProcessor(getStage());
    }

    public boolean isActive() {
        return active;
    }

    public void dispose() {
        consoleTextData.dispose();
        consoleTextures.dispose();
    }

    public void subscribeForEvents(ConsoleEventsSubscriber subscriber) {
        if (subscribers.contains(subscriber, true)) return;
        subscribers.add(subscriber);
    }

    @Override
    public boolean keyDown(int keycode) {
        consoleInputHistoryHandler.onKeyDown(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
