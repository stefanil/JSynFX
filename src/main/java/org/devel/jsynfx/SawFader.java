package org.devel.jsynfx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.SawtoothOscillatorBL;
import com.jsyn.unitgen.UnitOscillator;

public class SawFader extends Application {

    private static final URL URL = SawFader.class.getResource("./SawFader.fxml");

    private static final double MINIMUM_FREQUENCY = 200;
    private static final double BANDWIDTH = 500.0;
    private static final double KNOB_RANGE_IN_DEGREE = 300;
    private static final double DRAG_AREA_HEIGHT = 150;

    public static void main(String[] args) {
        launch(args);
    }

    private Synthesizer synth;
    private UnitOscillator osc;
    private LinearRamp lag;
    private LineOut lineOut;

    @Override
    public void start(Stage primaryStage) {
        createSynth();
        primaryStage.setScene(new Scene(loadFXML(), 400.0, 400.0));
        primaryStage.show();
        primaryStage.sizeToScene();
        primaryStage.setOnCloseRequest(event -> {
            synth.stop();
        });
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

    private void createSynth() {
        synth = JSyn.createSynthesizer();
        synth.setRealTime(true);

        // Add a tone generator. (band limited sawtooth)
        synth.add(osc = new SawtoothOscillatorBL());
        // Add a lag to smooth out amplitude changes and avoid pops.
        synth.add(lag = new LinearRamp());
        // Add an output mixer.
        synth.add(lineOut = new LineOut());
        // Connect the oscillator to both left and right output.
        osc.output.connect(0, lineOut.input, 0);
        osc.output.connect(0, lineOut.input, 1);

        // Set the minimum, current and maximum values for the port.
        lag.output.connect(osc.amplitude);
        lag.input.setup(0.0, 0.5, 1.0);
        lag.time.set(0.2);

        osc.frequency.setup(50.0, 300.0, 10000.0);

        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start();

        // We only need to start the LineOut. It will pull data from the
        // oscillator.
        lineOut.start();
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
                double newAngle = rotate.getAngle() + (lastY - y) / DRAG_AREA_HEIGHT * KNOB_RANGE_IN_DEGREE;

                if (newAngle >= 0 && newAngle <= KNOB_RANGE_IN_DEGREE) {
                    rotate.setAngle(newAngle);
                    lastY = y;
                    osc.frequency.set((newAngle / KNOB_RANGE_IN_DEGREE) * BANDWIDTH + MINIMUM_FREQUENCY);
                } else if (newAngle > KNOB_RANGE_IN_DEGREE) {
                    rotate.setAngle(KNOB_RANGE_IN_DEGREE);
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
            // rotate = new Rotate(0, 20, 20);
            rotate = new Rotate(0, 0, 0/*-72.7*/, 0, Rotate.Z_AXIS);
            // rotate.pivotXProperty()
            // .bind(Bindings.createDoubleBinding(() ->
            // rotateCircle.getBoundsInLocal().getWidth() / 2,
            // rotateCircle.boundsInParentProperty()));
            // rotate.pivotYProperty()
            // .bind(Bindings.createDoubleBinding(() ->
            // rotateCircle.getBoundsInLocal().getHeight() / 2,
            // rotateCircle.boundsInParentProperty()));
            rotatePane.getTransforms().add(rotate);
            // rotateCircle.getTransforms().add(new Rotate((360 -
            // KNOB_RANGE_IN_DEGREE) / 2, 0, 0, 0, Rotate.Z_AXIS));
        }
    }
}
