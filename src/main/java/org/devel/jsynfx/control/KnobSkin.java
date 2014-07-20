package org.devel.jsynfx.control;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

public class KnobSkin extends SkinBase<Knob> {

    public static final double MOUSE_SPEED = 0.5;
    private static final double RANGE_IN_DEGREE = 300;

    private static final URL URL = KnobSkin.class.getResource("./Knob.fxml");

    /**
     * @author stefan.illgen
     */
    public class SawFaderController implements Initializable {

        @FXML
        private Group rotatePane;

        @FXML
        private MeshView rotateMesh;

        @FXML
        private Label knobLabel;

        @Override
        public void initialize(java.net.URL location, ResourceBundle resources) {

            knobLabel.textProperty().bind(getSkinnable().textProperty());

            rotatePane.getTransforms().add(new Rotate(-10, 0, 50, 0, Rotate.X_AXIS));
            rotatePane.getTransforms().add(new Rotate(30, 0, 0, 0, Rotate.Z_AXIS));
            rotatePane.getTransforms().add(rotate);
            rotatePane.setOnMousePressed(event -> startDrag(event));
            rotatePane.setOnMouseDragged(event -> handleDrag(event));
            rotatePane.setOnMouseReleased(event -> disposeDrag());

            invalidRotate = true;
            getSkinnable().requestLayout();
        }

    }

    private Parent root;
    private Rotate rotate = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
    private boolean invalidRotate = true;
    private double lastY = Double.NaN;

    /**
     * @param knob
     */
    public KnobSkin(final Knob knob) {
        super(knob);
        root = loadFXML();
        getChildren().add(root);
        getSkinnable().valueProperty().addListener(observable -> invalidRotate = true);
        getSkinnable().requestLayout();
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

    private double startDrag(MouseEvent event) {
        return lastY = event.getSceneY();
    }

    private void handleDrag(MouseEvent event) {
        if (lastY != Double.NaN) {
            final double y = event.getSceneY();
            double newAngle = rotate.getAngle() + (lastY - y) / MOUSE_SPEED;
            getSkinnable().setValue(computeValue(newAngle));
            lastY = y;
            getSkinnable().requestLayout();
        }
    }

    private double disposeDrag() {
        return lastY = Double.NaN;
    }

    private double computeAngle(double newValue) {
        long newAngle = Math.round((newValue - getSkinnable().getMinValue()) / computeValueRange() * RANGE_IN_DEGREE);
        return (newAngle >= 0 && newAngle <= RANGE_IN_DEGREE) ? newAngle : (newAngle < 0) ? 0 : RANGE_IN_DEGREE;
    }

    private double computeValue(double newAngle) {
        double newValue =
            (newAngle / RANGE_IN_DEGREE * computeValueRange()) + getSkinnable().getMinValue();
        return (newValue < getSkinnable().getMinValue())
                                                        ? getSkinnable().getMinValue()
                                                        : (newValue > getSkinnable().getMaxValue())
                                                                                                   ? getSkinnable().getMaxValue()
                                                                                                   : newValue;
    }

    private double computeValueRange() {
        return getSkinnable().getMaxValue() - getSkinnable().getMinValue();
    }

    private boolean updateAngle(double newAngle) {
        if (newAngle != rotate.getAngle()) {
            if (newAngle >= 0 && newAngle <= RANGE_IN_DEGREE) {
                rotate.setAngle(Math.round(newAngle));
                // is in range
                return true;
            } else if (newAngle < 0 || newAngle > RANGE_IN_DEGREE) {
                rotate.setAngle(0);
            } else if (newAngle > RANGE_IN_DEGREE) {
                rotate.setAngle(RANGE_IN_DEGREE);
            }
            getSkinnable().requestLayout();
        }
        return false;
    }

    @Override
    protected double computePrefWidth(double height,
                                      double topInset,
                                      double rightInset,
                                      double bottomInset,
                                      double leftInset) {
        return leftInset + rightInset + root.prefWidth(height);
    }

    @Override
    protected double computePrefHeight(double width,
                                       double topInset,
                                       double rightInset,
                                       double bottomInset,
                                       double leftInset) {
        return topInset + bottomInset + root.prefHeight(width);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

        if (invalidRotate) {
            updateAngle(computeAngle(getSkinnable().getValue()));
            invalidRotate = false;
        }
        layoutInArea(root, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER);
    }

}
