package com.gadarts.war.menu.console;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import com.gadarts.war.menu.console.commands.CommandInvoke;
import com.gadarts.war.menu.console.commands.CommandResult;
import com.gadarts.war.menu.console.commands.Commands;
import com.gadarts.war.systems.player.input.KeyMap;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Stack;

public class ConsoleImpl extends Table implements Console, InputProcessor {
	public static final String NAME = "console";
	public static final int CURSOR_WIDTH = 10;
	public static final int CURSOR_HEIGHT = 10;
	public static final Interpolation.Pow INTERPOLATION = Interpolation.pow2;
	public static final String NOT_RECOGNIZED = "'%s' is not recognized as a command.";
	private static final float INPUT_HEIGHT = 20f;
	private static final float PADDING = 10f;
	private static final Color TEXT_BACKGROUND_COLOR = new Color(0, 0.2f, 0, 0.8f);
	private static final String INPUT_NAME = "input";
	private static final String TEXT_LABEL_NAME = "text";
	private static final Color INPUT_COLOR = Color.WHITE;
	private static final float TRANSITION_DURATION = 0.5f;
	private static final String PARAMETER_EXPECTED = "Failed to apply command! Parameter is expected at '%s'";
	private static final String PARAMETER_VALUE_EXPECTED = "Failed to apply command! Value is expected for " +
			"parameter '%s'";
	private static final Color CONSOLE_BACKGROUND_COLOR = new Color(0, 0.1f, 0, 1f);

	private final BitmapFont font = new BitmapFont();
	private Texture backgroundTexture;
	private boolean active;
	private Texture textBackgroundTexture;
	private Texture cursorTexture;
	private Array<ConsoleEventsSubscriber> subscribers = new Array<>();
	private StringBuilder stringBuilder = new StringBuilder();
	private SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
	private Timestamp timeStamp = new Timestamp(TimeUtils.millis());
	private CommandResult commandResult = new CommandResult();
	private Stack<String> inputHistory = new Stack<>();
	private Stack<String> inputHistoryAux = new Stack<>();
	private Label.LabelStyle textStyle;

	public ConsoleImpl() {
		setName(NAME);
		TextureRegionDrawable textBackgroundTexture = createTextBackgroundTexture();
		float height = Gdx.graphics.getHeight() * 2f / 3f;
		addTextView(textBackgroundTexture, (int) height);
		setPosition(0, Gdx.graphics.getHeight());
		createInputField(textBackgroundTexture);
		setBackground((int) height);
		setSize(Gdx.graphics.getWidth(), backgroundTexture.getHeight());
	}

	private void setBackground(int height) {
		createBackgroundTexture(height);
		setBackground(new TextureRegionDrawable(backgroundTexture));
	}

	private void createBackgroundTexture(int height) {
		Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), height, Format.RGBA8888);
		pixmap.setColor(CONSOLE_BACKGROUND_COLOR);
		pixmap.fillRectangle(0, 0, Gdx.graphics.getWidth(), height);
		backgroundTexture = new Texture(pixmap);
		pixmap.dispose();
	}

	private void createInputField(TextureRegionDrawable textBackgroundTexture) {
		TextField.TextFieldStyle style = new TextField.TextFieldStyle(font, INPUT_COLOR, createCursorTexture(),
				null, textBackgroundTexture);
		TextField input = new TextField("", style);
		input.setName(INPUT_NAME);
		Label arrow = new Label(">", textStyle);
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
		if (!inputHistoryAux.empty()) inputHistoryAux.insertElementAt(inputCommand, 0);
		else inputHistory.push(inputCommand);
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
		timeStamp.setTime(TimeUtils.millis());
		if (logTime) {
			stringBuilder.append(" [").append(date.format(timeStamp)).append("]: ").append(text).append('\n');
		} else {
			stringBuilder.append(text).append('\n');
		}
		((Label) getStage().getRoot().findActor(TEXT_LABEL_NAME)).setText(stringBuilder);
	}

	@Override
	public CommandResult notifyCommandExecution(Commands command) {
		boolean result = false;
		commandResult.clear();
		for (ConsoleEventsSubscriber sub : subscribers) {
			result |= sub.onCommandRun(command, commandResult);
		}
		commandResult.setResult(result);
		return commandResult;
	}

	private TextureRegionDrawable createCursorTexture() {
		Pixmap cursorPixmap = new Pixmap(CURSOR_WIDTH, CURSOR_HEIGHT, Format.RGBA8888);
		cursorPixmap.setColor(INPUT_COLOR);
		cursorPixmap.fill();
		cursorTexture = new Texture(cursorPixmap);
		TextureRegionDrawable cursorTexture = new TextureRegionDrawable(this.cursorTexture);
		cursorPixmap.dispose();
		return cursorTexture;
	}

	private void addTextView(TextureRegionDrawable textBackgroundTexture, int consoleHeight) {
		textStyle = new Label.LabelStyle(font, Color.WHITE);
		textStyle.background = textBackgroundTexture;
		float width = Gdx.graphics.getWidth() - PADDING * 2;
		float height = consoleHeight - (INPUT_HEIGHT);
        Label textView = new Label(stringBuilder, textStyle);
		textView.setAlignment(Align.bottomLeft);
		textView.setName(TEXT_LABEL_NAME);
		textView.setWrap(true);
		add(textView).colspan(2).size(width, height).align(Align.bottomLeft).padRight(PADDING).padLeft(PADDING).row();
	}

	private TextureRegionDrawable createTextBackgroundTexture() {
		Pixmap textBackground = new Pixmap(1, 1, Format.RGBA8888);
		textBackground.setColor(TEXT_BACKGROUND_COLOR);
		textBackground.fill();
		textBackgroundTexture = new Texture(textBackground);
		TextureRegionDrawable textBackgroundTexture = new TextureRegionDrawable(this.textBackgroundTexture);
		textBackground.dispose();
		return textBackgroundTexture;
	}

	public void activate() {
		if (active) return;
		getStage().setKeyboardFocus(getStage().getRoot().findActor(INPUT_NAME));
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
		font.dispose();
		textBackgroundTexture.dispose();
		backgroundTexture.dispose();
		cursorTexture.dispose();
	}

	public void subscribeForEvents(ConsoleEventsSubscriber subscriber) {
		if (subscribers.contains(subscriber, true)) return;
		subscribers.add(subscriber);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.DOWN) {
			manipulateInputHistory(inputHistoryAux, inputHistory);
		} else if (keycode == Input.Keys.UP) {
			manipulateInputHistory(inputHistory, inputHistoryAux);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	private boolean manipulateInputHistory(Stack<String> takeFrom, Stack<String> putIn) {
		if (takeFrom.empty()) return true;
		TextField input = getStage().getRoot().findActor(INPUT_NAME);
		String pop = takeFrom.pop();
		input.setText(pop);
		input.setCursorPosition(input.getText().length());
		putIn.push(pop);
		return true;
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
