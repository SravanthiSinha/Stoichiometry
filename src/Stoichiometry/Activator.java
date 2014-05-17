package Stoichiometry;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.desktop.plugin.Plugin;

/**
 * This class Activates the Stoichiometry Plugin
 * @author Sravanthi Sinha
 * @version 1.0
 */
public class Activator implements BundleActivator 
{
	private static BundleContext context;
	static BundleContext getContext() 
	{
		return context;
	}
	
	/**
	 * This method starts the Stoichiometry Plugin
	 * @exception Exception 
	 */
	public void start(BundleContext bundleContext) throws Exception
	{
		Activator.context = bundleContext;
		StoichiometricPlugin toolbar = new StoichiometricPlugin();
		bundleContext.registerService(Plugin.class.getName(), toolbar, null);
	}
	
	public void stop(BundleContext bundleContext) throws Exception 
	{
		Activator.context = null;
	}

}
