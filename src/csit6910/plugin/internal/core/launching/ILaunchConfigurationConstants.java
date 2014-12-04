package csit6910.plugin.internal.core.launching;

import csit6910.plugin.Activator;
import randoop.plugin.internal.core.TestKinds;

/**
 * Constant definitions for Randoop launch configurations.
 * 
 * @author Peter Kalauskas
 */
public class ILaunchConfigurationConstants {
  
  public static final String ID_RANDOOP_TEST_GENERATION = "csit6910.plugin.core.launching.gentestsType"; //$NON-NLS-1$
  
  public static final String ATTR_PORT = Activator.getPluginId() + ".PORT"; //$NON-NLS-1$
  
  public static final String ATTR_RANDOM_SEED = Activator.getPluginId() + ".RANDOM_SEED"; //$NON-NLS-1$

  public static final String ATTR_MAXIMUM_TEST_SIZE = Activator.getPluginId() + ".MAXIMUM_TEST_SIZE"; //$NON-NLS-1$
  
  public static final String ATTR_USE_THREADS = Activator.getPluginId() + ".USE_THREADS"; //$NON-NLS-1$
  
  public static final String ATTR_THREAD_TIMEOUT = Activator.getPluginId() + ".THREAD_TIMEOUT"; //$NON-NLS-1$
  
  public static final String ATTR_USE_NULL = Activator.getPluginId() + ".USE_NULL"; //$NON-NLS-1$
  
  public static final String ATTR_NULL_RATIO = Activator.getPluginId() + ".NULL_RATIO"; //$NON-NLS-1$
  
  public static final String ATTR_INPUT_LIMIT = Activator.getPluginId() + ".INPUT_LIMIT"; //$NON-NLS-1$
  
  public static final String ATTR_TIME_LIMIT = Activator.getPluginId() + ".TIME_LIMIT"; //$NON-NLS-1$
  
  public static final String ATTR_JUNIT_PACKAGE_NAME = Activator.getPluginId() + ".JUNIT_PACKAGE_NAME"; //$NON-NLS-1$
  
  public static final String ATTR_JUNIT_CLASS_NAME = Activator.getPluginId() + ".JUNIT_CLASS_NAME"; //$NON-NLS-1$

  public static final String ATTR_TEST_KINDS = Activator.getPluginId() + ".TEST_KINDS"; //$NON-NLS-1$
  
  public static final String ATTR_MAXIMUM_TESTS_WRITTEN = Activator.getPluginId() + ".MAXIMUM_TESTS_WRITTEN"; //$NON-NLS-1$
  
  public static final String ATTR_MAXIMUM_TESTS_PER_FILE = Activator.getPluginId() + ".MAXIMUM_TESTS_PER_FILE"; //$NON-NLS-1$
  
  public static final String ATTR_PROJECT_NAME = Activator.getPluginId() + ".PROJECT_NAME"; //$NON-NLS-1$
  
  public static final String ATTR_OUTPUT_DIRECTORY_NAME = Activator.getPluginId() + ".OUTPUT_DIRECTORY"; //$NON-NLS-1$
  
  public static final String ATTR_USE_LOCAL_TEMP_FOLDER = Activator.getPluginId() +  ".USE_LOCAL_TEMP_FOLDER"; //$NON-NLS-1$
  
  public static final String ATTR_AVAILABLE_TYPES = Activator.getPluginId() + ".AVAILABLE_TYPES"; //$NON-NLS-1$
  
  public static final String ATTR_GRAYED_TYPES = Activator.getPluginId() + ".GRAYED_TYPES"; //$NON-NLS-1$
  
  public static final String ATTR_CHECKED_TYPES = Activator.getPluginId() + ".CHECKED_TYPES"; //$NON-NLS-1$
  
  public static final String ATTR_AVAILABLE_METHODS_PREFIX = Activator.getPluginId() + ".AVAILABLE_METHODS."; //$NON-NLS-1$
  
  public static final String ATTR_CHECKED_METHODS_PREFIX = Activator.getPluginId() + ".SELECTED_METHODS."; //$NON-NLS-1$
  
  public static final String DEFAULT_RANDOM_SEED = "0"; //$NON-NLS-1$
  
  public static final String DEFAULT_MAXIMUM_TEST_SIZE = "100"; //$NON-NLS-1$
  
  public static final String DEFAULT_USE_THREADS = "true"; //$NON-NLS-1$
  
  public static final String DEFAULT_THREAD_TIMEOUT = "5000"; //$NON-NLS-1$
  
  public static final String DEFAULT_USE_NULL = "false"; //$NON-NLS-1$
  
  public static final String DEFAULT_NULL_RATIO = ""; //$NON-NLS-1$
  
  public static final String DEFAULT_INPUT_LIMIT = "100000000"; //$NON-NLS-1$
  
  public static final String DEFAULT_TIME_LIMIT = "100"; //$NON-NLS-1$
  
  public static final String DEFAULT_JUNIT_PACKAGE_NAME = "randoop"; //$NON-NLS-1$
  
  public static final String DEFAULT_JUNIT_CLASS_NAME = "RandoopTest"; //$NON-NLS-1$
  
  public static final String DEFAULT_MAXIMUM_TESTS_WRITTEN = "100000000"; //$NON-NLS-1$
  
  public static final String DEFAULT_MAXIMUM_TESTS_PER_FILE = "500"; //$NON-NLS-1$
  
  public static final String DEFAULT_TEST_KINDS = TestKinds.all.getArgumentName();

  public static final String DEFAULT_PROJECT = ""; //$NON-NLS-1$
  
  public static final String DEFAULT_OUTPUT_DIRECTORY_NAME = "test"; //$NON-NLS-1$

  public static final String DEFAULT_USE_LOCAL_TEMP_FOLDER = "false"; //$NON-NLS-1$

  
  /** csit6910 JANALA ARGUMENTS */
  public static final String ATTR_OFFLINE_MODE = Activator.getPluginId() + ".OFFLINE_MODE"; //$NON-NLS-1$
  public static final String ATTR_MAX_ITERATIONS = Activator.getPluginId() + ".MAX_ITERATION"; //$NON-NLS-1$
  public static final String ATTR_PRINT_TRACE = Activator.getPluginId() + ".PRINT_TRACE"; //$NON-NLS-1$
  public static final String ATTR_MAIN_CLASS = Activator.getPluginId() + ".MAIN_CLASS"; //$NON-NLS-1$

  public static final String DEFAULT_OFFLINE_MODE = "true"; //$NON-NLS-1$
  public static final String DEFAULT_MAX_ITERATIONS = "5"; //$NON-NLS-1$
  public static final String DEFAULT_PRINT_TRACE = "true"; //$NON-NLS-1$
  public static final String DEFAULT_MAIN_CLASS = ""; //$NON-NLS-1$

  /** csit6910 PALUS ARGUMENTS */
  //public static final String ATTR_TIME_LIMIT = Activator.getPluginId() + ".TIME_LIMIT"; //$NON-NLS-1$
  public static final String ATTR_CLASS_FILE = Activator.getPluginId() + ".CLASS_FILE"; //$NON-NLS-1$
  public static final String ATTR_TRACE_FILE = Activator.getPluginId() + ".TRACE_FILE"; //$NON-NLS-1$
  public static final String ATTR_MODEL_CLASS_FILE = Activator.getPluginId() + ".MODEL_CLASS_FILE"; //$NON-NLS-1$
  public static final String ATTR_DONT_BUILD_MODEL_FROM_TRACE = Activator.getPluginId() + ".DONT_BUILD_MODEL_FROM_TRACE"; //$NON-NLS-1$
  public static final String ATTR_DONT_PROCESS_ALL_TRACE = Activator.getPluginId() + ".DONT_PROCESS_ALL_TRACE"; //$NON-NLS-1$
  public static final String ATTR_INSTANCE_PER_MODEL = Activator.getPluginId() + ".INSTANCE_PER_MODEL"; //$NON-NLS-1$
  public static final String ATTR_INSTANCE_PROCESS_FILE = Activator.getPluginId() + ".INSTANCE_PROCESS_FILE"; //$NON-NLS-1$
  public static final String ATTR_AUTO_SWITCH_TO_RANDOM = Activator.getPluginId() + ".AUTO_SWITCH_TO_RANDOM"; //$NON-NLS-1$
  public static final String ATTR_SWITCH_TIME_TO_RANDOM = Activator.getPluginId() + ".SWITCH_TIME_TO_RANDOM"; //$NON-NLS-1$
  public static final String ATTR_PERCENTAGE_OF_RANDOM_GEN = Activator.getPluginId() + ".PERCENTAGE_OF_RANDOM_GEN"; //$NON-NLS-1$
  public static final String ATTR_PRINT_MODEL_COVERAGE = Activator.getPluginId() + ".PRINT_MODEL_COVERAGE"; //$NON-NLS-1$
  public static final String ATTR_EXHAUSTIVE_DIVERSIFY_SEQ = Activator.getPluginId() + ".EXHAUSTIVE_DIVERSIFY_SEQ"; //$NON-NLS-1$
  public static final String ATTR_ADD_RETURN_TYPE_RELATED_METHODS = Activator.getPluginId() + ".ADD_RETURN_TYPE_RELATED_METHODS"; //$NON-NLS-1$
  public static final String ATTR_PROCESS_LARGE_TRACE = Activator.getPluginId() + ".PROCESS_LARGE_TRACE"; //$NON-NLS-1$
  public static final String ATTR_USE_THEORY_CHECK = Activator.getPluginId() + ".USE_THEORY_CHECK"; //$NON-NLS-1$
  public static final String ATTR_CHECK_NPE = Activator.getPluginId() + ".CHECK_NPE"; //$NON-NLS-1$
  public static final String ATTR_DUMP_MODEL_AS_TEXT = Activator.getPluginId() + ".DUMP_MODEL_AS_TEXT"; //$NON-NLS-1$
  public static final String ATTR_SAVE_TRACE_AS_TXT = Activator.getPluginId() + ".SAVE_TRACE_AS_TXT"; //$NON-NLS-1$
  public static final String ATTR_ONLY_OUTPUT_FAILED_TESTS = Activator.getPluginId() + ".ONLY_OUTPUT_FAILED_TESTS"; //$NON-NLS-1$
  public static final String ATTR_FILTER_REDUNDANT_FAILURES = Activator.getPluginId() + ".FILTER_REDUNDANT_FAILURES"; //$NON-NLS-1$
  public static final String ATTR_ERROR_IGNORED_METHODS = Activator.getPluginId() + ".ERROR_IGNORED_METHODS"; //$NON-NLS-1$
  public static final String ATTR_USE_RANDOM = Activator.getPluginId() + ".USE_RANDOM"; //$NON-NLS-1$
  //public static final String ATTR_RANDOM_SEED = Activator.getPluginId() + ".RANDOM_SEED"; //$NON-NLS-1$
  public static final String ATTR_DISABLE_ASSERTION = Activator.getPluginId() + ".DISABLE_ASSERTION"; //$NON-NLS-1$
  public static final String ATTR_EXCEPTION_DUMP_FILE = Activator.getPluginId() + ".EXCEPTION_DUMP_FILE"; //$NON-NLS-1$
  public static final String ATTR_USE_VERBOSE = Activator.getPluginId() + ".USE_VERBOSE"; //$NON-NLS-1$
  public static final String ATTR_LOG_FILE = Activator.getPluginId() + ".LOG_FILE"; //$NON-NLS-1$
  

  //public static final String DEFAULT_TIME_LIMIT = "100"; //$NON-NLS-1$
  public static final String DEFAULT_CLASS_FILE = ""; //$NON-NLS-1$
  public static final String DEFAULT_TRACE_FILE = ""; //$NON-NLS-1$
  public static final String DEFAULT_MODEL_CLASS_FILE = ""; //$NON-NLS-1$
  public static final String DEFAULT_DONT_BUILD_MODEL_FROM_TRACE = "false"; //$NON-NLS-1$
  public static final String DEFAULT_DONT_PROCESS_ALL_TRACE = "false"; //$NON-NLS-1$
  public static final String DEFAULT_INSTANCE_PER_MODEL = "20"; //$NON-NLS-1$
  public static final String DEFAULT_INSTANCE_PROCESS_FILE = ""; //$NON-NLS-1$
  public static final String DEFAULT_AUTO_SWITCH_TO_RANDOM = "false"; //$NON-NLS-1$
  public static final String DEFAULT_SWITCH_TIME_TO_RANDOM = "6000";  //$NON-NLS-1$
  public static final String DEFAULT_PERCENTAGE_OF_RANDOM_GEN = "4"; //$NON-NLS-1$
  public static final String DEFAULT_PRINT_MODEL_COVERAGE = "false"; //$NON-NLS-1$
  public static final String DEFAULT_EXHAUSTIVE_DIVERSIFY_SEQ = "false"; //$NON-NLS-1$
  public static final String DEFAULT_ADD_RETURN_TYPE_RELATED_METHODS = "false"; //$NON-NLS-1$
  public static final String DEFAULT_PROCESS_LARGE_TRACE = "false"; //$NON-NLS-1$
  public static final String DEFAULT_USE_THEORY_CHECK = "true"; //$NON-NLS-1$
  public static final String DEFAULT_CHECK_NPE = "false"; //$NON-NLS-1$
  public static final String DEFAULT_DUMP_MODEL_AS_TEXT = "false"; //$NON-NLS-1$
  public static final String DEFAULT_SAVE_TRACE_AS_TXT = "false"; //$NON-NLS-1$
  public static final String DEFAULT_ONLY_OUTPUT_FAILED_TESTS = "false"; //$NON-NLS-1$
  public static final String DEFAULT_FILTER_REDUNDANT_FAILURES = "false"; //$NON-NLS-1$
  public static final String DEFAULT_ERROR_IGNORED_METHODS = "null"; //$NON-NLS-1$
  public static final String DEFAULT_USE_RANDOM = "false"; //$NON-NLS-1$
  //public static final String DEFAULT_RANDOM_SEED = "0"; //$NON-NLS-1$
  public static final String DEFAULT_DISABLE_ASSERTION = "false"; //$NON-NLS-1$
  public static final String DEFAULT_EXCEPTION_DUMP_FILE = ""; //$NON-NLS-1$
  public static final String DEFAULT_USE_VERBOSE = "false"; //$NON-NLS-1$
  public static final String DEFAULT_LOG_FILE = ""; //$NON-NLS-1$
  
}
