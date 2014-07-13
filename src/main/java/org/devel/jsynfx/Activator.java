/**
 * 
 */
package org.devel.jsynfx;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author stefan.illgen
 */
public class Activator implements BundleActivator {

    @Override
    public void start(BundleContext context) throws Exception {
        SawFader.main(null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // TODO Auto-generated method stub

    }

}
