package org.devel.jsynfx.control;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

public class KnobSkin extends SkinBase<Knob> {

    private static final URL URL = KnobSkin.class.getResource("./Knob.fxml");

    // TODO replace
    public static final double DRAG_AREA_HEIGHT = 150;
    public static final double RANGE_IN_DEGREE = 300;

    public KnobSkin(final Knob knob) {
        super(knob);
        getChildren().add(loadFXML());
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
                double newAngle = rotate.getAngle() + (lastY - y) / DRAG_AREA_HEIGHT * RANGE_IN_DEGREE;

                if (newAngle >= 0 && newAngle <= RANGE_IN_DEGREE) {
                    rotate.setAngle(newAngle);
                    lastY = y;
                } else if (newAngle > RANGE_IN_DEGREE) {
                    rotate.setAngle(RANGE_IN_DEGREE);
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

            getSkinnable().valueProperty()
                          .bind(rotate.angleProperty()
                                      .multiply(getSkinnable().maxValueProperty()
                                                              .subtract(getSkinnable().minValueProperty()))
                                      .divide(RANGE_IN_DEGREE)
                                      .add(getSkinnable().minValueProperty()));
        }
    }
}
