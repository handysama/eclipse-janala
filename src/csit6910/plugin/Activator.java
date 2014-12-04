package csit6910.plugin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
//import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleActivator;
//import org.osgi.util.tracker.ServiceTracker;
//import org.osgi.service.prefs.PreferencesService;
//import org.osgi.service.prefs.Preferences;

import csit6910.plugin.internal.core.launching.LaunchResources;
import csit6910.plugin.internal.core.PluginStatus;

public class Activator extends AbstractUIPlugin {
	/** The plug-in's unique identifier */
	public static final String PLUGIN_ID = "janala"; //$NON-NLS-1$

	/** As of Indigo, this breaks launching when set to false because we make the
	 *  assumption the we can get to the randoop's bin folder by navigating to
	 *  ../bin/ If you think about it, it's amazing it ever worked. */
	//public final static boolean USE_RANDOOP_JAR = true;
	public final static boolean USE_IAGENT_JAR = true;

	// Uncomment this to enable Randoop/Palus plugin
	//private static final IPath RANDOOP_JAR = new Path("randoop.jar"); //$NON-NLS-1$
	
	private static final IPath IAGENT_JAR = new Path("iagent.jar"); //$NON-NLS-1$
	
	private static final IPath CATG_CONF = new Path("catg.conf"); //$NON-NLS-1$

	/** The shared instance */
	private static Activator plugin = null;

	/**
	 * Indicator of when the shared instance is stopped. This is not reset when
	 * <code>stop</code> is called
	 */
	private static boolean isStopped = false;

	/**
	 * Returns the shared instance of this plug-in.
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Starts up this plug-in and sets <code>this</code> to be the shared
	 * instance.
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * Stops this plug-in and sets the shared instance to <code>null</code>.
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		// Remove all files from the temp folder in the state location
		LaunchResources.deleteAllLaunchResources();

		plugin = null;
		isStopped = true;

		super.stop(context);
	}

	/**
	 * Convenience method that returns the plug-in's unique identifier.
	 * 
	 * @return the plug-in's ID, also <code>PLUGIN_ID</code>
	 */
	public static String getPluginId() {
		return PLUGIN_ID;
	}

	/**
	 * Logs a status in the shared instance's log
	 * 
	 * @param status
	 *          the status to log
	 * 
	 * @see org.eclipse.core.runtime.ILog#log(IStatus)
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Returns the active workbench shell.
	 * 
	 * @return the active workbench shell
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
		if (workBenchWindow == null)
			return null;
		return workBenchWindow.getShell();
	}

	/**
	 * Returns the active workbench window.
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		if (plugin == null)
			return null;
		IWorkbench workBench = plugin.getWorkbench();
		if (workBench == null)
			return null;
		return workBench.getActiveWorkbenchWindow();
	}

	/**
	 * Returns the full path to the randoop.jar runtime archive.
	 * 
	 * @return full path to randoop.jar, or <code>null</code> if no the
	 *         <code>IPath</code> could not be created
	 * @throws CoreException 
	 */
	/*
	public static List<IPath> getPalusClasspaths() throws CoreException {
		ArrayList<IPath> cp = new ArrayList<IPath>();
		if (USE_RANDOOP_JAR) {
			try {
				cp.add(getFullPath(RANDOOP_JAR));
			} catch (IOException e) {
				throw new CoreException(PluginStatus.NO_LOCAL_RANDOOPJAR_ERROR.getStatus(e));
			}
		} else {
			// Hard-coded location of Randoop class files and plume.jar, this is
			// only for development purposes. USE_JAR should always be true when
			// exporting the plug-in to the update site.
			try {
				cp.add(getFullPath(new Path("../bin"))); //$NON-NLS-1$
				cp.add(getFullPath(new Path("../lib/plume.jar"))); //$NON-NLS-1$
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return cp;
	}
	*/
	
	/**
	 * Returns the full path to the iagent.jar runtime archive and lib.
	 * 
	 * @return full path to randoop.jar, or <code>null</code> if no the
	 *         <code>IPath</code> could not be created
	 * @throws CoreException 
	 */
	public static List<IPath> getJanalaClasspaths() throws CoreException {
		ArrayList<IPath> cp = new ArrayList<IPath>();
		if (USE_IAGENT_JAR) {
			try {
				cp.add(getFullPath(IAGENT_JAR));
			} catch (IOException e) {
				throw new CoreException(PluginStatus.NO_LOCAL_RANDOOPJAR_ERROR.getStatus(e));
			}
		} else {
			// Hard-coded location of Randoop class files and plume.jar, this is
			// only for development purposes. USE_JAR should always be true when
			// exporting the plug-in to the update site.
			try {
				cp.add(getFullPath(new Path("../bin"))); //$NON-NLS-1$
				cp.add(getFullPath(new Path("../lib/plume.jar"))); //$NON-NLS-1$
				cp.add(getFullPath(new Path("../lib/asm-all-3.3.1.jar"))); //$NON-NLS-1$
				cp.add(getFullPath(new Path("../lib/automaton.jar"))); //$NON-NLS-1$
				cp.add(getFullPath(new Path("../lib/choco-solver-2.1.4.jar"))); //$NON-NLS-1$
				cp.add(getFullPath(new Path("../lib/trove-3.0.3.jar"))); //$NON-NLS-1$
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return cp;
	}
	
	/**
	 * Returns the full path to the iagent.jar runtime archive and lib.
	 * 
	 * @return full path to randoop.jar, or <code>null</code> if no the
	 *         <code>IPath</code> could not be created
	 * @throws CoreException 
	 */
	public static IPath getJanalaConfPath() throws CoreException {
		IPath cp;
		try {
			cp = getFullPath(CATG_CONF);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new CoreException(PluginStatus.NO_LOCAL_CATGCONF_ERROR.getStatus(e));
		}
		String p = cp.toOSString();
		java.io.File ff = new java.io.File(p);
		assert ff.exists();
		if (!ff.exists()) {
			System.out.println("File not exists");
		}
		return cp;
	}
	
	/**
	 * Returns the full path to the iagent.jar runtime archive and lib.
	 * 
	 * @return full path to randoop.jar, or <code>null</code> if no the
	 *         <code>IPath</code> could not be created
	 * @throws CoreException 
	 */
	public static String getJanalaConfigPath() {
		try {
			return getFullPath(CATG_CONF).toOSString();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return "";
	}
	
	/**
	 * Returns the full path to the iagent.jar runtime archive and lib.
	 * 
	 * @return full path to randoop.jar, or <code>null</code> if no the
	 *         <code>IPath</code> could not be created
	 * @throws CoreException 
	 */
	public static String getIAgentPath() {
		try {
			return getFullPath(IAGENT_JAR).toOSString();
		} catch (Exception e) {
			System.out.println("getIAgentPathErr: "+e.getMessage());
		}
		return "";
	}
	

	/**
	 * file://doc/index.html
	 *
	 * Returns the full path to the the given Path.
	 * 
	 * @return local path to the , or <code>null</code> if no the
	 *         <code>IPath</code> could not be created
	 * @throws CoreException 
	 */
	private static IPath getFullPath(IPath localPath) throws IOException {
		Bundle bundle = Platform.getBundle(getPluginId());
		URL url = FileLocator.find(bundle, localPath, null);
		url = FileLocator.toFileURL(url);
		return new Path(url.getPath());
	}

	/**
	 * Returns the current display, or the default display if the currently
	 * running thread is not a user-interface thread for any display.
	 * 
	 * @see org.eclipse.swt.widgets.Display#getCurrent()
	 * @see org.eclipse.swt.widgets.Display#getDefault()
	 */
	public static Display getDisplay() {
		Display display;
		display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;
	}
	
}
