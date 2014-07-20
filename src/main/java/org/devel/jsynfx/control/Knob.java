package org.devel.jsynfx.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

public class Knob extends Control {

    private static final String DEFAULT_STYLE_CLASS = "knob";

    public Knob() {
        super();
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    public Knob(String label, double defaultValue, double minValue, double maxValue) {
        this();
        setMinValue(minValue);
        setValue(defaultValue);
        setMaxValue(maxValue);
        setText(label);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new KnobSkin(this);
    }

    private DoubleProperty value;

    public DoubleProperty valueProperty() {
        return value == null ? value = new SimpleDoubleProperty(getMinValue()) : value;
    }

    public double getValue() {
        return valueProperty().get();
    }

    public void setValue(double value) {
        this.valueProperty().set(value);
    }

    private DoubleProperty minValue;

    public DoubleProperty minValueProperty() {
        return minValue == null ? minValue = new SimpleDoubleProperty(0) : minValue;
    }

    public double getMinValue() {
        return minValueProperty().get();
    }

    public void setMinValue(double minValue) {
        this.minValueProperty().set(minValue);
    }

    private DoubleProperty maxValue;

    public DoubleProperty maxValueProperty() {
        return maxValue == null ? maxValue = new SimpleDoubleProperty(0) : maxValue;
    }

    public double getMaxValue() {
        return maxValueProperty().get();
    }

    public void setMaxValue(double maxValue) {
        this.maxValueProperty().set(maxValue);
    }

    private StringProperty text;

    public StringProperty textProperty() {
        return text == null ? text = new SimpleStringProperty("") : text;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        this.textProperty().set(text);
    }

}
