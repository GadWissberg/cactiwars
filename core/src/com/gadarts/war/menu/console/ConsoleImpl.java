package com.gadarts.war.menu.console;

import com.badlogic.gdx.*;
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
	private static final float TRANSITION_DURATION = 1f;
	private static final float INPUT_HEIGHT = 20f;
	private static final float PADDING = 10f;
	private static final Color TEXT_BACKGROUND_COLOR = new Color(0, 0.2f, 0, 0.8f);
	private static final String INPUT_NAME = "input";
	private static final String TEXT_LABEL_NAME = "text";
	private static final Color INPUT_COLOR = Color.WHITE;
	private static final String NOT_RECOGNIZED = "\'%s\' is not recognized as a command.";

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
		Pixmap pixmap = new Pixmap(Gdx.files.getFileHandle("console.jpg", Files.FileType.Local));
		Pixmap cropped = new Pixmap(Gdx.graphics.getWidth(), height, Format.RGBA8888);
		cropped.drawPixmap(pixmap, 0, 0, 0, pixmap.getHeight() - height, pixmap.getWidth(), height);
		backgroundTexture = new Texture(cropped);
		pixmap.dispose();
		cropped.dispose();
	}

	private void createInputField(TextureRegionDrawable textBackgroundTexture) {
		TextField.TextFieldStyle style = new TextField.TextFieldStyle(font, INPUT_COLOR, createCursorTexture(),
				null, textBackgroundTexture);
		TextField input = new TextField("", style);
		input.setName(INPUT_NAME);
		add(input).size(Gdx.graphics.getWidth() - PADDING * 2, INPUT_HEIGHT).pad(PADDING).row();
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
		try {
			if (!inputHistoryAux.empty()) {
				inputHistoryAux.insertElementAt(inputCommand, 0);
			} else {
				inputHistory.push(inputCommand);
			}
			Commands command = Commands.findCommandByNameOrAlias(prepareInput(inputCommand));
			command.getCommandImpl().run(this);
		} catch (Exception e) {
			insertNewLog(String.format(NOT_RECOGNIZED, inputCommand), false);
		}
		textField.setText(null);
	}

	private String prepareInput(String text) {
		if (text == null) return null;
		return text.toUpperCase().replaceAll(" ", "");
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
		Label.LabelStyle textStyle = new Label.LabelStyle(font, Color.WHITE);
		textStyle.background = textBackgroundTexture;
		float width = Gdx.graphics.getWidth() - PADDING * 2;
		float height = consoleHeight - (PADDING * 2 + INPUT_HEIGHT);
		Label label = new Label(stringBuilder, textStyle);
		label.setAlignment(Align.bottomLeft);
		label.setName(TEXT_LABEL_NAME);
		label.setWrap(true);
		add(label).size(width, height).align(Align.bottomLeft).padRight(PADDING).padLeft(PADDING).row();
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
