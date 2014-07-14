package org.devel.jsynfx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

public class SawFader extends Control {

    private static final URL URL = SawFader.class.getResource("./SawFader.fxml");

    // TODO replace
    private static final double DRAG_AREA_HEIGHT = 150;

    public SawFader() {
        getChildren().add(loadFXML());
    }

    public SawFader(double minValue, double maxValue) {
        this();
        setMinValue(minValue);
        setMaxValue(maxValue);
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

    private Parent loadFXML() {
        FXMLLoader loader = new FXMLLoader(URL);
        loader.setController(new SawFaderController());
        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double getKnobRangeAsDegree() {
        return 300;
    }

    public class SawFaderController implements Initializable {

        @FXML
        private Group rotatePane;

        @FXML
        private MeshView rotateCircle;
        private double lastY = Double.NaN;

        private Rotate rotate;

        @FXML
        void onDragEntered(MouseEvent event) {
            lastY = event.getSceneY();
        }

        @FXML
        void onMouseDragOver(MouseEvent event) {
            if (lastY != Double.NaN) {
                final double y = event.getSceneY();
                double newAngle = rotate.getAngle() + (lastY - y) / DRAG_AREA_HEIGHT * getKnobRangeAsDegree();

                if (newAngle >= 0 && newAngle <= getKnobRangeAsDegree()) {
                    rotate.setAngle(newAngle);
                    lastY = y;
                } else if (newAngle > getKnobRangeAsDegree()) {
                    rotate.setAngle(getKnobRangeAsDegree());
                } else if (newAngle < 0) {
                    rotate.setAngle(0);
                }
            }
        }

        @FXML
        void onMouseReleased(MouseEvent event) {
            lastY = Double.NaN;
        }

        @Override
        public void initialize(java.net.URL location, ResourceBundle resources) {
            rotatePane.getTransforms().add(new Rotate(30, 0, 0, 0, Rotate.Z_AXIS));
            rotate = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
            // rotate.pivotXProperty()
            // .bind(Bindings.createDoubleBinding(() ->
            // rotateCircle.getBoundsInLocal().getWidth() / 2,
            // rotateCircle.boundsInParentProperty()));
            // rotate.pivotYProperty()
            // .bind(Bindings.createDoubleBinding(() ->
            // rotateCircle.getBoundsInLocal().getHeight() / 2,
            // rotateCircle.boundsInParentProperty()));
            rotatePane.getTransforms().add(rotate);

            valueProperty().bind(rotate.angleProperty()
                                       .multiply(maxValueProperty().subtract(minValueProperty()))
                                       .divide(getKnobRangeAsDegree())
                                       .add(minValueProperty()));
        }
    }
}
