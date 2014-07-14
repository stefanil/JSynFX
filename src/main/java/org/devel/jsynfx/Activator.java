/**
 * 
 */
package org.devel.jsynfx;

import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.SwingUtilities;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.SawtoothOscillatorBL;
import com.jsyn.unitgen.UnitOscillator;

/**
 * @author stefan.illgen
 */
public class Activator implements BundleActivator {

    private static final double MINIMUM_FREQUENCY = 200;
    private static final double MAXIMUM_FREQUENCY = 700;
    private static final double BANDWIDTH = MAXIMUM_FREQUENCY - MINIMUM_FREQUENCY;
    private static final double KNOB_RANGE_IN_DEGREE = 300;

    private Synthesizer synth;
    private UnitOscillator osc;
    private LinearRamp lag;
    private LineOut lineOut;
    private Stage stage;

    @Override
    public void start(BundleContext context) throws Exception {
        createSynth();
        createStage();
    }

    private void createStage() {
        // init JavaFX Toolkit
        final CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JFXPanel(); // initializes JavaFX environment
                latch.countDown();
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // create stage
        Platform.runLater(() -> {
            stage = new Stage();
            stage.setScene(new Scene(createRoot(), 400.0, 400.0));
            stage.show();
            stage.sizeToScene();
            stage.setOnCloseRequest(event -> {
                synth.stop();
            });
        });
    }

    private Parent createRoot() {

        SawFader knob = new SawFader(MINIMUM_FREQUENCY, MAXIMUM_FREQUENCY);
        knob.valueProperty()
            .addListener((obs, oldV, newV) -> osc.frequency.set(newV.doubleValue()));
        return knob;
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

        osc.frequency.setup(MINIMUM_FREQUENCY, 300.0, 10000.0);

        // Start synthesizer using default stereo output at 44100 Hz.
        synth.start();

        // We only need to start the LineOut. It will pull data from the
        // oscillator.
        lineOut.start();

    }

    @Override
    public void stop(BundleContext context) throws Exception {
        stage.close();
    }

}
