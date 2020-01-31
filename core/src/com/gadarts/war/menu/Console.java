package com.gadarts.war.menu;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import com.gadarts.war.systems.player.input.KeyMap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Console extends Table {
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

	private final BitmapFont font = new BitmapFont();
	private Texture backgroundTexture;
	private boolean active;
	private Texture textBackgroundTexture;
	private Texture cursorTexture;
	private Array<ConsoleEventsSubscriber> subscribers = new Array<>();
	private StringBuilder stringBuilder = new StringBuilder();
	private SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
	private Timestamp timeStamp = new Timestamp(TimeUtils.millis());

	public Console() {
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
				if (!Console.this.hasActions()) if (isActive()) deactivate();
			} else if (c == '\r' || c == '\n') {
				appendLine(textField);
			}
		});
	}

	private void appendLine(TextField textField) {
		String text = textField.getText();
		timeStamp.setTime(TimeUtils.millis());
		stringBuilder.append(" [").append(date.format(timeStamp)).append("]: ").append(text).append('\n');
		((Label) getStage().getRoot().findActor(TEXT_LABEL_NAME)).setText(stringBuilder);
		textField.setText(null);
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
		((InputMultiplexer) Gdx.input.getInputProcessor()).addProcessor(getStage());
		active = true;
		float amountY = -Gdx.graphics.getHeight() / 3f;
		addAction(Actions.moveBy(0, amountY, TRANSITION_DURATION, INTERPOLATION));
		setVisible(true);
		subscribers.forEach(ConsoleEventsSubscriber::consoleActivated);
	}

	public void deactivate() {
		if (!active) return;
		active = false;
		float amountY = Gdx.graphics.getHeight() / 3f;
		MoveByAction move = Actions.moveBy(0, amountY, TRANSITION_DURATION, Interpolation.pow2);
		addAction(Actions.sequence(move, Actions.visible(false)));
		subscribers.forEach(ConsoleEventsSubscriber::consoleDeactivated);
		getStage().unfocusAll();
		((InputMultiplexer) Gdx.input.getInputProcessor()).removeProcessor(getStage());
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
}
