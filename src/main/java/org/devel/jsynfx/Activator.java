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

import org.devel.jsynfx.app.SynthesizerFX;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author stefan.illgen
 */
public class Activator implements BundleActivator {

    private Stage stage;
    private SynthesizerFX synthesizer;
    private BundleContext context;

    @Override
    public void start(BundleContext context) throws Exception {
        this.context = context;
        synthesizer = new SynthesizerFX();
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
            stage.setScene(new Scene(createRoot(), 800, 300));
            stage.show();
            stage.sizeToScene();
            stage.setOnCloseRequest(event -> {
                try {
                    // stop this bundle + javafx toolkit (including application)
                    stop(context);
                    // stop system bundle
                    context.getBundle(0).stop();
                    // EclipseStarter.shutdown();
                    // exit jvm
                    System.exit(0);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });
        });
    }

    private Parent createRoot() {
        return synthesizer.createView();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        Platform.exit();
        synthesizer.stop();
    }

}
