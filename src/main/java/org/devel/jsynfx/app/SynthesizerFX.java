/**
 * 
 */
package org.devel.jsynfx.app;

import javafx.scene.Parent;

import org.devel.jsynfx.control.Knob;

import com.jsyn.JSyn;
import com.jsyn.Synthesizer;
import com.jsyn.unitgen.LineOut;
import com.jsyn.unitgen.LinearRamp;
import com.jsyn.unitgen.SawtoothOscillatorBL;
import com.jsyn.unitgen.UnitOscillator;

/**
 * @author stefan.illgen
 */
public class SynthesizerFX {

    private static final double MINIMUM_FREQUENCY = 200;
    private static final double MAXIMUM_FREQUENCY = 700;

    private Synthesizer synth;
    private UnitOscillator osc;
    private LinearRamp lag;
    private LineOut lineOut;

    /**
     * 
     */
    public SynthesizerFX() {
        createModel();
    }

    private void createModel() {
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

    public void stop() {
        synth.stop();
    }

    public Parent createView() {
        Knob knob = new Knob(MINIMUM_FREQUENCY, MAXIMUM_FREQUENCY);
        knob.valueProperty()
            .addListener((obs, oldV, newV) -> osc.frequency.set(newV.doubleValue()));
        return knob;
    }

}
