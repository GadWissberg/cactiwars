package com.gadarts.war.menu.console;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.TimeUtils;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ConsoleTextData implements Disposable {
    private final BitmapFont font = new BitmapFont();
    private Stage stage;
    private StringBuilder stringBuilder = new StringBuilder();
    private SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
    private Timestamp timeStamp = new Timestamp(TimeUtils.millis());
    private Label.LabelStyle textStyle;

    public ConsoleTextData() {
        textStyle = new Label.LabelStyle(font, Color.WHITE);
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

    public void insertNewLog(String text, boolean logTime) {
        timeStamp.setTime(TimeUtils.millis());
        if (logTime) {
            stringBuilder.append(" [").append(date.format(timeStamp)).append("]: ").append(text).append('\n');
        } else {
            stringBuilder.append(text).append('\n');
        }
        ((Label) stage.getRoot().findActor(ConsoleImpl.TEXT_LABEL_NAME)).setText(stringBuilder);
    }

    @Override
    public void dispose() {
        font.dispose();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
