package csit6910.plugin.internal.ui.wizards;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import csit6910.plugin.Activator;
import csit6910.plugin.internal.core.PluginStatus;
import csit6910.plugin.internal.core.launching.ILaunchConfigurationConstants;
import csit6910.plugin.internal.core.launching.ArgumentCollector;
import csit6910.plugin.internal.ui.preferences.PluginPreferences;

import randoop.plugin.internal.ui.wizards.OptionWizardPage;

/**
 * 
 * @author Peter Kalauskas
 */
public class LaunchConfigurationWizard extends Wizard {
  protected static final String DIALOG_SETTINGS_KEY = "RandoopWizard"; //$NON-NLS-1$

  ILaunchConfigurationWorkingCopy fConfig;
  IJavaProject fJavaProject;
  OptionWizardPage fMainPage;
  OptionWizardPage fTestInputsPage;

  public LaunchConfigurationWizard(IJavaProject javaProject,
      List<String> checkedTypes, List<String> grayedTypes,
      Map<String, List<String>> selectedMethodsByDeclaringTypes,
      ILaunchConfigurationWorkingCopy config) {

    super();

    fConfig = config;
    fJavaProject = javaProject;

    ArgumentCollector.setProjectName(config, fJavaProject.getElementName());

    Set<String> availableTypesSet = new HashSet<String>();
    availableTypesSet.addAll(checkedTypes);
    availableTypesSet.addAll(grayedTypes);
    
    List<String> availableTypes = new ArrayList<String>();
    availableTypes.addAll(availableTypesSet);

    ArgumentCollector.saveClassTree(config, availableTypes, checkedTypes,
        grayedTypes, null, null, selectedMethodsByDeclaringTypes);
    
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    
    // csit6910 add this to force remember configuration
    if (!PluginPreferences.doRememberParameters(store)) {
    	PluginPreferences plugPref = new PluginPreferences();
    	plugPref.initializeDefaultPreferences();
    }

    if (PluginPreferences.doRememberParameters(store)) {
      int location = PluginPreferences.getParameterStorageLocation(store);
      switch(location) {
      case PluginPreferences.WORKSPACE:
    	  setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_MAX_ITERATIONS);
    	  setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_OFFLINE_MODE);
    	  setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_TIME_LIMIT);
    	  setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_USE_THEORY_CHECK);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_RANDOM_SEED);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_USE_THREADS);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_USE_NULL);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_NULL_RATIO);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_INPUT_LIMIT);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_TIME_LIMIT);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_TEST_KINDS);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN);
        setConfigAttribute(store, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE);
        
        break;
      case PluginPreferences.PROJECT:
        IProject project = fJavaProject.getProject();
        IScopeContext projectScope = new ProjectScope(project);
        Preferences prefs = (Preferences) projectScope.getNode(Activator.getPluginId());
        
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_MAX_ITERATIONS, ILaunchConfigurationConstants.DEFAULT_MAX_ITERATIONS);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_OFFLINE_MODE, ILaunchConfigurationConstants.DEFAULT_OFFLINE_MODE);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_TIME_LIMIT, ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_USE_THEORY_CHECK, ILaunchConfigurationConstants.DEFAULT_USE_THEORY_CHECK);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_RANDOM_SEED, ILaunchConfigurationConstants.DEFAULT_RANDOM_SEED);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE, ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TEST_SIZE);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_USE_THREADS, ILaunchConfigurationConstants.DEFAULT_USE_THREADS); 
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT, ILaunchConfigurationConstants.DEFAULT_THREAD_TIMEOUT);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_USE_NULL, ILaunchConfigurationConstants.DEFAULT_USE_NULL);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_NULL_RATIO, ILaunchConfigurationConstants.DEFAULT_NULL_RATIO);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_INPUT_LIMIT, ILaunchConfigurationConstants.DEFAULT_INPUT_LIMIT); 
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_TIME_LIMIT, ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME, ILaunchConfigurationConstants.DEFAULT_OUTPUT_DIRECTORY_NAME); 
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME,ILaunchConfigurationConstants.DEFAULT_JUNIT_PACKAGE_NAME);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME, ILaunchConfigurationConstants.DEFAULT_JUNIT_CLASS_NAME);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_TEST_KINDS, ILaunchConfigurationConstants.DEFAULT_TEST_KINDS);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN, ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_WRITTEN);
        setConfigAttribute(prefs, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE,ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_PER_FILE);
        
        break;
      }
    }
    
    //fTestInputsPage = new TestInputsPage("Test Inputs", fJavaProject, fConfig);
    fMainPage = new ParametersPage("Main", fJavaProject, fConfig);
    fMainPage.setPreviousPage(fMainPage);
    
    fMainPage.setDefaults(fConfig);

//    addPage(fTestInputsPage);
    addPage(fMainPage);
    
    setTitleBarColor(new RGB(167, 215, 250));
    setWindowTitle("New Janala Launch Configuration");
  }
  
  @Override
  public void createPageControls(Composite pageContainer) {
    super.createPageControls(pageContainer);

//    fTestInputsPage.initializeFrom(fConfig);
    fMainPage.initializeFrom(fConfig);

    setNeedsProgressMonitor(true);
    setHelpAvailable(false);
  }

  @Override
  public boolean performFinish() {
    if(!fMainPage.isValid(fConfig)) {
      return false;
    }
    
//    if(!fTestInputsPage.isValid(fConfig)) {
//      return false;
//    }

    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    if (PluginPreferences.doRememberParameters(store)) {
      int location = PluginPreferences.getParameterStorageLocation(store);
      switch(location) {
      case PluginPreferences.WORKSPACE:
      	
      	setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_MAX_ITERATIONS, ILaunchConfigurationConstants.DEFAULT_MAX_ITERATIONS);
      	setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_OFFLINE_MODE, ILaunchConfigurationConstants.DEFAULT_OFFLINE_MODE);
    	  setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_TIME_LIMIT, ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT);
    	  setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_USE_THEORY_CHECK, ILaunchConfigurationConstants.DEFAULT_USE_THEORY_CHECK);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_RANDOM_SEED, ILaunchConfigurationConstants.DEFAULT_RANDOM_SEED);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE, ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TEST_SIZE);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_USE_THREADS, ILaunchConfigurationConstants.DEFAULT_USE_THREADS); 
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT, ILaunchConfigurationConstants.DEFAULT_THREAD_TIMEOUT);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_USE_NULL, ILaunchConfigurationConstants.DEFAULT_USE_NULL);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_NULL_RATIO, ILaunchConfigurationConstants.DEFAULT_NULL_RATIO);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_INPUT_LIMIT, ILaunchConfigurationConstants.DEFAULT_INPUT_LIMIT); 
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_TIME_LIMIT, ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME, ILaunchConfigurationConstants.DEFAULT_OUTPUT_DIRECTORY_NAME); 
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME,ILaunchConfigurationConstants.DEFAULT_JUNIT_PACKAGE_NAME);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME, ILaunchConfigurationConstants.DEFAULT_JUNIT_CLASS_NAME);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_TEST_KINDS, ILaunchConfigurationConstants.DEFAULT_TEST_KINDS);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN, ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_WRITTEN);
        setValueFromConfig(store, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE,ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_PER_FILE);
        
        break;
      case PluginPreferences.PROJECT:
        IProject project = fJavaProject.getProject();
        IScopeContext projectScope = new ProjectScope(project);
        Preferences prefs = (Preferences) projectScope.getNode(Activator
            .getPluginId());
        
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_MAX_ITERATIONS, ILaunchConfigurationConstants.DEFAULT_MAX_ITERATIONS);
      	setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_OFFLINE_MODE, ILaunchConfigurationConstants.DEFAULT_OFFLINE_MODE);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_TIME_LIMIT, ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_USE_THEORY_CHECK, ILaunchConfigurationConstants.DEFAULT_USE_THEORY_CHECK);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_RANDOM_SEED, ILaunchConfigurationConstants.DEFAULT_RANDOM_SEED);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE, ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TEST_SIZE);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_USE_THREADS, ILaunchConfigurationConstants.DEFAULT_USE_THREADS); 
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT, ILaunchConfigurationConstants.DEFAULT_THREAD_TIMEOUT);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_USE_NULL, ILaunchConfigurationConstants.DEFAULT_USE_NULL);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_NULL_RATIO, ILaunchConfigurationConstants.DEFAULT_NULL_RATIO);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_INPUT_LIMIT, ILaunchConfigurationConstants.DEFAULT_INPUT_LIMIT); 
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_TIME_LIMIT, ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME, ILaunchConfigurationConstants.DEFAULT_OUTPUT_DIRECTORY_NAME); 
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME,ILaunchConfigurationConstants.DEFAULT_JUNIT_PACKAGE_NAME);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME, ILaunchConfigurationConstants.DEFAULT_JUNIT_CLASS_NAME);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_TEST_KINDS, ILaunchConfigurationConstants.DEFAULT_TEST_KINDS);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN, ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_WRITTEN);
        setValueFromConfig(prefs, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE,ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_PER_FILE);

        try {
          prefs.flush();
        } catch (BackingStoreException e) {
          // TODO: What do we do here?
        }
        
        break;
      }
    }
    
    return true;
  }
  
  private void setConfigAttribute(Preferences prefs, String attributeName, String def) {
  	fConfig.setAttribute(attributeName, prefs.get(attributeName, def));
  }
  
  private void setConfigAttribute(IPreferenceStore store, String attributeName) {
  	fConfig.setAttribute(attributeName, store.getString(attributeName));
  }
  
  private void setValueFromConfig(Preferences prefs, String attributeName, String def) {
    try {
      prefs.put(attributeName, fConfig.getAttribute(attributeName, def));
    } catch (CoreException e) {
      prefs.put(attributeName, def);
    }
  }
  
  private void setValueFromConfig(IPreferenceStore store, String attributeName, String def) {
    try {
      store.setValue(attributeName, fConfig.getAttribute(attributeName, def));
    } catch (CoreException e) {
      store.setValue(attributeName, def);
    }
  }
  
}
