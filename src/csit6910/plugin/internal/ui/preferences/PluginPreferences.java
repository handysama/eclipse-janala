package csit6910.plugin.internal.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;

import csit6910.plugin.Activator;
import csit6910.plugin.internal.core.launching.ILaunchConfigurationConstants;

/**
 * Class used to initialize default preference values.
 */
public class PluginPreferences extends AbstractPreferenceInitializer {

  public static final int WORKSPACE = 0;
  public static final int PROJECT = 1;

  static final String WORKSPACE_VALUE = "workspace"; //$NON-NLS-1$
  static final String PROJECT_VALUE = "project"; //$NON-NLS-1$

  public static String getRandomSeed(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_RANDOM_SEED);
  }

  public static String getMaxTestSize(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE);
  }

  public static boolean getUseThreads(IPreferenceStore store) {
    return store.getBoolean(ILaunchConfigurationConstants.ATTR_USE_THREADS);
  }

  public static String getThreadTimeout(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT);
  }

  public static boolean getUseNull(IPreferenceStore store) {
    return store.getBoolean(ILaunchConfigurationConstants.ATTR_USE_NULL);
  }

  public static String getNullRatio(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_NULL_RATIO);
  }

  public static String getInputLimit(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_INPUT_LIMIT);
  }

  public static String getTimeLimit(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_TIME_LIMIT);
  }

  public static String getOutputDirectoryName(IPreferenceStore store) {
    return store
        .getString(ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME);
  }

  public static String getJUnitPackageName(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME);
  }

  public static String getJUnitClassName(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME);
  }

  public static String getTestKinds(IPreferenceStore store) {
    return store.getString(ILaunchConfigurationConstants.ATTR_TEST_KINDS);
  }

  public static String getMaxTestsWritten(IPreferenceStore store) {
    return store
        .getString(ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN);
  }

  public static String getMaxTestsPerFile(IPreferenceStore store) {
    return store
        .getString(ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE);
  }

  public static void setRandomSeed(IPreferenceStore store, String seed) {
    store.setValue(ILaunchConfigurationConstants.ATTR_RANDOM_SEED, seed);
  }

  public static void setMaxTestSize(IPreferenceStore store, String size) {
    store.setValue(ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE, size);
  }

  public static void setUseThreads(IPreferenceStore store, boolean useThreads) {
    store.setValue(ILaunchConfigurationConstants.ATTR_USE_THREADS, useThreads);
  }

  public static void setThreadTimeout(IPreferenceStore store, String threadTimeout) {
    store.setValue(ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT,
        threadTimeout);
  }

  public static void setUseNull(IPreferenceStore store, boolean useNull) {
    store.setValue(ILaunchConfigurationConstants.ATTR_USE_NULL, useNull);
  }

  public static void setNullRatio(IPreferenceStore store, String nullRatio) {
    store.setValue(ILaunchConfigurationConstants.ATTR_NULL_RATIO, nullRatio);
  }

  public static void setInputLimit(IPreferenceStore store, String testInputs) {
    store.setValue(ILaunchConfigurationConstants.ATTR_INPUT_LIMIT, testInputs);
  }

  public static void setTimeLimit(IPreferenceStore store, String timeLimit) {
    store.setValue(ILaunchConfigurationConstants.ATTR_TIME_LIMIT, timeLimit);
  }

  public static void setOutputDirectoryName(IPreferenceStore store, String outputDirectory) {
    store.setValue(ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME,
        outputDirectory);
  }

  public static void setJUnitPackageName(IPreferenceStore store, String junitPackageName) {
    store.setValue(ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME,
        junitPackageName);
  }

  public static void setJUnitClassName(IPreferenceStore store, String junitClassName) {
    store.setValue(ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME,
        junitClassName);
  }

  public static void setTestKinds(IPreferenceStore store, String testKinds) {
    store.setValue(ILaunchConfigurationConstants.ATTR_TEST_KINDS, testKinds);
  }

  public static void setMaxTestsWritten(IPreferenceStore store, String maxTestsWritten) {
    store.setValue(ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN,
        maxTestsWritten);
  }

  public static void setMaxTestsPerFile(IPreferenceStore store, String maxTestsPerFile) {
    store.setValue(ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE,
        maxTestsPerFile);
  }
  
  @Override
  public void initializeDefaultPreferences() {
    IPreferenceStore store = Activator.getDefault().getPreferenceStore();
    
    store.setDefault(IPreferenceConstants.P_REMEMBER_PARAMETERS, true);
    
    store.setDefault(IPreferenceConstants.P_PARAMETER_STORAGE_LOCATION, WORKSPACE_VALUE);
    
    store.setDefault(ILaunchConfigurationConstants.ATTR_RANDOM_SEED,
    ILaunchConfigurationConstants.DEFAULT_RANDOM_SEED);

    store.setDefault(ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE,
    ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TEST_SIZE);

    store.setDefault(ILaunchConfigurationConstants.ATTR_USE_THREADS,
    Boolean.parseBoolean(ILaunchConfigurationConstants.DEFAULT_USE_THREADS));

    store.setDefault(ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT,
    ILaunchConfigurationConstants.DEFAULT_THREAD_TIMEOUT);

    store.setDefault(ILaunchConfigurationConstants.ATTR_USE_NULL,
    Boolean.parseBoolean(ILaunchConfigurationConstants.DEFAULT_USE_NULL));

    store.setDefault(ILaunchConfigurationConstants.ATTR_NULL_RATIO,
    ILaunchConfigurationConstants.DEFAULT_NULL_RATIO);

    store.setDefault(ILaunchConfigurationConstants.ATTR_INPUT_LIMIT,
    ILaunchConfigurationConstants.DEFAULT_INPUT_LIMIT);

    store.setDefault(ILaunchConfigurationConstants.ATTR_TIME_LIMIT,
    ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT);

    store.setDefault(ILaunchConfigurationConstants.ATTR_PROJECT_NAME,
    ILaunchConfigurationConstants.DEFAULT_PROJECT);

    store.setDefault(ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME,
    ILaunchConfigurationConstants.DEFAULT_OUTPUT_DIRECTORY_NAME);

    store.setDefault(ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME,
    ILaunchConfigurationConstants.DEFAULT_JUNIT_PACKAGE_NAME);

    store.setDefault(ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME,
    ILaunchConfigurationConstants.DEFAULT_JUNIT_CLASS_NAME);

    store.setDefault(ILaunchConfigurationConstants.ATTR_TEST_KINDS,
    ILaunchConfigurationConstants.DEFAULT_TEST_KINDS);

    store.setDefault(ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN,
    ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_WRITTEN);

    store.setDefault(ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE,
    ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_PER_FILE);

    store.setDefault(ILaunchConfigurationConstants.ATTR_OFFLINE_MODE,
    ILaunchConfigurationConstants.DEFAULT_OFFLINE_MODE);
    
    store.setDefault(ILaunchConfigurationConstants.ATTR_MAX_ITERATIONS,
    ILaunchConfigurationConstants.DEFAULT_MAX_ITERATIONS);
  }

  public static boolean doRememberParameters(IPreferenceStore store) {
    return store.getBoolean(IPreferenceConstants.P_REMEMBER_PARAMETERS);
  }

  public static int getParameterStorageLocation(IPreferenceStore store) {
    String storageLocation = store
        .getString(IPreferenceConstants.P_PARAMETER_STORAGE_LOCATION);

    if (storageLocation.equals(WORKSPACE_VALUE)) {
      return WORKSPACE;
    }

    if (storageLocation.equals(PROJECT_VALUE)) {
      return PROJECT;
    }

    return -1;
  }

}
