package com.gadarts.war.menu.console;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import com.gadarts.war.GameAssetManager;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class ConsoleTextData implements Disposable {
	private static final String TIME_COLOR = "SKY";
	private final BitmapFont font;
	private final float fontHeight;
	private Stage stage;
	private StringBuilder stringBuilder = new StringBuilder();
	private SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
	private Timestamp timeStamp = new Timestamp(TimeUtils.millis());
	private Label.LabelStyle textStyle;

	public ConsoleTextData() {
		font = GameAssetManager.getInstance().get("consola.ttf", BitmapFont.class);
		font.getData().markupEnabled = true;
		textStyle = new Label.LabelStyle(font, Color.WHITE);
		GlyphLayout layout = new GlyphLayout();
		layout.setText(font, "test");
		fontHeight = layout.height;
	}

	public float getFontHeight() {
		return fontHeight;
	}

	public StringBuilder getStringBuilder() {
		return stringBuilder;
	}

	public Label.LabelStyle getTextStyle() {
		return textStyle;
	}

	public BitmapFont getFont() {
		return font;
	}

	public void insertNewLog(String text, boolean logTime, String color) {
		timeStamp.setTime(TimeUtils.millis());
		String colorText = Optional.ofNullable(color).isPresent() ? color : ConsoleImpl.OUTPUT_COLOR;
		if (logTime) {
			appendTextWithTime(text, colorText);
		} else stringBuilder.append(colorText).append(text).append('\n');
		stringBuilder.append(ConsoleImpl.OUTPUT_COLOR);
		((Label) stage.getRoot().findActor(ConsoleImpl.TEXT_VIEW_NAME)).setText(stringBuilder);
	}

	private void appendTextWithTime(String text, String colorText) {
		stringBuilder.append("[").append(TIME_COLOR).append("]")
				.append(" [").append(date.format(timeStamp)).append("]: ")
				.append(colorText)
				.append(text).append('\n');
	}

	@Override
	public void dispose() {
		font.dispose();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
}
