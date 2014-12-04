package csit6910.plugin.internal.core.launching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;

import csit6910.plugin.Activator;
import csit6910.plugin.internal.core.PluginStatus;

import randoop.plugin.internal.IConstants;
import randoop.plugin.internal.core.MethodMnemonic;
import randoop.plugin.internal.core.RandoopCoreUtil;
import randoop.plugin.internal.core.TypeMnemonic;

/**
 * Argument collecter for the Randoop launch configuration type. The class may
 * be instantiated to get the object's represented by the by the values in a
 * configurations attribute map. The class also has a suite of static methods
 * that may be used to get and set Randoop launch configuration attributes.
 * 
 * @author Peter Kalauskas
 */
public class ArgumentCollector {
  private static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();
  
  private String fName;
  private List<IType> fSelectedTypes;
  private Map<IType, List<IMethod>> fSelectedMethodsByType;
  private int fRandomSeed;	// Shared with palus
  private int fMaxTestSize;
  private boolean fUseThreads;
  private int fThreadTimeout;
  private boolean fUseNull;
  private double fNullRatio;
  private int fInputLimit;
  private int fTimeLimit;	// Shared with palus (Required parameter)
  private IProject fProject;
  private IJavaProject fJavaProject;
  private IPath fOutputDirectory;
  private String fJUnitPackageName;
  private String fJUnitClassName;
  private String fTestKinds;
  private int fMaxTestsWritten;
  private int fMaxTestsPerFile;
  
  /** Janala arguments */
  private int fJanalaMaxIterations;
  private boolean fJanalaOfflineMode;
  
  /** Palus arguments */
  private String fClassFile;
  private String fTraceFile;
  private String fModelClassFile;
  private boolean fDontBuildModelFromTrace;
  private boolean fDontProcessAllTrace;
  private int fInstancePerModel;
  private int fInstanceProcessFile;
  private boolean fAutoSwitchToRandom;
  private int fSwitchTimeToRandom;
  private int fPercentageOfRandomGen;
  private boolean fPrintModelCoverage;
  private boolean fExhaustiveDiversifySeq;
  private boolean fAddReturnTypeRelatedMethods;
  private boolean fProcessLargeTrace;
  private boolean fUseTheoryCheck;
  private boolean fCheckNPE;
  private boolean fDumpModelAsText;
  private boolean fSaveTraceAsTxt;
  private boolean fOnlyOutputFailedTests;
  private boolean fFilterRedundantFailures;
  private String fErrorIgnoredMethods;
  private boolean fUseRandom;
  private boolean fDisableAssertion;
  private String fExceptionDumpFile;
  private boolean fUseVerbose;
  private String fLogFile;
  

  /**
   * Constructs a set of objects corresponding to the parameters in the given
   * launch configuration. The configuration is guaranteed to be runnable if a
   * <code>CoreException</code> is not thrown, but it may not perform as
   * expected. For more information,
   * {@link RandoopArgumentCollector#getWarnings()} must be called.
   * 
   * @param config
   *          the configuration to
   * @param root
   * @throws CoreException
   *           if the there was an error accessing or interpreting data from the
   *           <code>ILaunchConfiguration</code>, or if the configuration would
   *           fail when launched
   */
  public ArgumentCollector(ILaunchConfiguration config, IWorkspaceRoot root) throws CoreException {
    fName = config.getName();
    Assert.isNotNull(fName, "Configuration name not given");

    String projectName = getProjectName(config);
    fProject = RandoopCoreUtil.getProjectFromName(projectName);
    fJavaProject = JavaCore.create(fProject);
    if (fJavaProject == null) {
      String msg = NLS.bind("Java project ''{0}'' was not found.", projectName);
      IStatus s = PluginStatus.createUIStatus(IStatus.ERROR, msg);
      throw new CoreException(s);
    } else if (!fJavaProject.exists()) {
      String msg = NLS.bind("Java project ''{0}'' does not exist.", projectName);
      IStatus s = PluginStatus.createUIStatus(IStatus.ERROR, msg);
      throw new CoreException(s);
    }

    fSelectedTypes = new ArrayList<IType>();
    fSelectedMethodsByType = new HashMap<IType, List<IMethod>>();

    List<String> checkedTypes = getCheckedTypes(config);
    List<String> grayedTypes = getGrayedTypes(config);

    if (checkedTypes.isEmpty()) {
      IStatus s = PluginStatus.createLaunchConfigurationStatus(IStatus.ERROR, "No classes or methods have been selected for testing", null);
      throw new CoreException(s);
    }

    for (String typeMnemonicString : checkedTypes) {
      
      TypeMnemonic typeMnemonic = new TypeMnemonic(typeMnemonicString, root);
      IType type = typeMnemonic.getType();

      if (!fJavaProject.equals(typeMnemonic.getJavaProject())) {
        String msg = NLS.bind("The class {0} is selected for testing but does not exist in the selected project.", typeMnemonic.getFullyQualifiedName());
        IStatus s = PluginStatus.createLaunchConfigurationStatus(IStatus.ERROR, msg, null);
        throw new CoreException(s);
      }

      if (grayedTypes.contains(typeMnemonicString)) {
        List<IMethod> methodList = new ArrayList<IMethod>();

        try {
          List<String> selectedMethods = getCheckedMethods(config, new TypeMnemonic(type).toString());
          for (String methodMnemonicString : selectedMethods) {
            IMethod m = new MethodMnemonic(methodMnemonicString).findMethod(type);
            
            if (m == null || !m.exists()) {
              String msg;
              if (m == null) {
                msg = "One of the methods selected for testing does not exist";
              } else {
                msg = NLS.bind("Stored method ''{0}'' does not exist", m.getElementName());
              }
              IStatus s = PluginStatus.createLaunchConfigurationStatus(IStatus.ERROR, msg, null);
              throw new CoreException(s);
            }

            methodList.add(m);
          }
        } catch (JavaModelException e) {
          IStatus s = PluginStatus.createLaunchConfigurationStatus(IStatus.ERROR,
              "One of the classes selected for testing does not exist.", e);
          throw new CoreException(s);
        }
        fSelectedMethodsByType.put(type, methodList);
      } else if (checkedTypes.contains(typeMnemonicString)) {
        fSelectedTypes.add(type);
      }
    }

    try {
      fRandomSeed = Integer.parseInt(getRandomSeed(config));
      fMaxTestSize = Integer.parseInt(getMaxTestSize(config));
      fUseThreads = getUseThreads(config);
      if (fUseThreads)
        fThreadTimeout = Integer.parseInt(getThreadTimeout(config));
      fUseNull = getUseNull(config);
      if (fUseNull)
        fNullRatio = Double.parseDouble(getNullRatio(config));
      fInputLimit = Integer.parseInt(getInputLimit(config));
      fTimeLimit = Integer.parseInt(getTimeLimit(config));
      fMaxTestsWritten = Integer.parseInt(getMaxTestsWritten(config));
      fMaxTestsPerFile = Integer.parseInt(getMaxTestsPerFile(config));
    } catch (NumberFormatException nfe) {
      IStatus s = PluginStatus.createLaunchConfigurationStatus(
        IStatus.CANCEL,
        "One of the stored attributes in the launch configuration was not formatted as a number as was expected. Try opening the launch configuration dialog and resaving the configuration",
        nfe);
      throw new CoreException(s);
    }

    // TODO: Check validaty of these three arguments
    String outputSourceFolderName = getOutputDirectoryName(config);
    fOutputDirectory = fJavaProject.getPath().append(outputSourceFolderName).makeAbsolute();
    fJUnitPackageName = getJUnitPackageName(config);
    fJUnitClassName = getJUnitClassName(config);
    fTestKinds = getTestKinds(config);
    
    // csit6910
    fJanalaMaxIterations = Integer.parseInt(getJanalaMaxIterations(config));
    fJanalaOfflineMode = Boolean.parseBoolean(getJanalaOfflineMode(config));

    /**
     * There is another <code>IType</code> with the same fully-qualified-name as
     * the given <code>IMethod</code>s declaring <code>IType</code> in a classpath
     * with a higher priority. The configuration will likely crash after launching
     * since it is unlikely the two methods have the same exact methods.
     */
    for (IType type : fSelectedMethodsByType.keySet()) {
      String fqname = type.getFullyQualifiedName().replace('$', '.');

      try {
        if (!type.equals(getJavaProject().findType(fqname, (IProgressMonitor) null))) {
          String msg = NLS.bind("Methods in class {0} are selected for testing, but there is another class by the same fully-qualified name in the project's classpath that has priority and will be tested instead of the selected method's declaring class.", fqname);
          IStatus s = PluginStatus.createLaunchConfigurationStatus(IStatus.ERROR, msg, null);
          throw new CoreException(s);
        }
      } catch (JavaModelException e) {
        IStatus s = PluginStatus.JAVA_MODEL_EXCEPTION.getStatus(e);
        Activator.log(s);
      }
    }
    
  }

  /**
   * Returns a WARNING status if the arguments in the launch configuration may
   * cause Randoop to behave unexpectedly. The checks this method performs are:
   * <p>
   * <ul>
   * <li>Another type in the project's classpath may exist with the same
   * fully-qualified-name as the a selected type, and it may have a higher
   * priority.
   * </ul>
   * 
   * @return a WARNING status with a description of the problem, or an OK status
   *         with an empty message if there are no warnings
   */
  public IStatus getWarnings() {
    for (IType type : getSelectedTypes()) {
      String fqname = type.getFullyQualifiedName().replace('$', '.');

      try {
        if (!type.equals(fJavaProject.findType(fqname, (IProgressMonitor) null))) {
          String msg = NLS.bind("There are two clases by the name ''{0}'' in the project classpath. The selected class is of a lower priority.", fqname);
          return PluginStatus.createLaunchConfigurationStatus(IStatus.WARNING, msg, null);
        }
      } catch (JavaModelException e) {
        IStatus s = PluginStatus.JAVA_MODEL_EXCEPTION.getStatus(e);
        Activator.log(s);
      }
    }
    
    return PluginStatus.OK_STATUS;
  }
  
  public String getName() {
    return fName;
  }
  
	public IProject getProject() {
		return fProject;
	}
  
  public IJavaProject getJavaProject() {
    return fJavaProject;
  }

  public List<IType> getSelectedTypes() {
    return fSelectedTypes;
  }

  public Map<IType, List<IMethod>> getSelectedMethodsByType() {
    return fSelectedMethodsByType;
  }

  public int getRandomSeed() {
    return fRandomSeed;
  }

  public int getMaxTestSize() {
    return fMaxTestSize;
  }

  public boolean getUseThreads() {
    return fUseThreads;
  }

  public int getThreadTimeout() {
    return fThreadTimeout;
  }

  public boolean getUseNull() {
    return fUseNull;
  }

  public double getNullRatio() {
    return fNullRatio;
  }

  public int getInputLimit() {
    return fInputLimit;
  }

  public int getTimeLimit() {
    return fTimeLimit;
  }

  public IPath getOutputDirectory() {
    return fOutputDirectory;
  }

  public String getJUnitPackageName() {
    return fJUnitPackageName;
  }

  public String getJUnitClassName() {
    return fJUnitClassName;
  }

  public String getTestKinds() {
    return fTestKinds;
  }

  public int getMaxTestsWritten() {
    return fMaxTestsWritten;
  }

  public int getMaxTestsPerFile() {
    return fMaxTestsPerFile;
  }

  /** JANALA ATTR */
  public int getJanalaMaxIterations() {
	  return fJanalaMaxIterations;
  }
  
  public boolean getJanalaOfflineMode() {
	  return fJanalaOfflineMode;
  }
  
  /** PALUS ATTR */
  public String getClassFile() {
	  return fClassFile;
  }
  
  public String getTraceFile() {
	  return fTraceFile;
  }
  
  public String getModelClassFile() {
	  return fModelClassFile;
  }
  
  public boolean getDontBuildModelFromTrace () {
	  return fDontBuildModelFromTrace;
  }
  
  public boolean getDontProcessAllTrace () {
	  return fDontProcessAllTrace;
  }
  
  public int getInstancePerModel () {
	  return fInstancePerModel;
  }
  
  public int getInstanceProcessFile () {
	  return fInstanceProcessFile;
  }

  public boolean getAutoSwitchToRandom () {
	  return fAutoSwitchToRandom;
  }
  
  public int getSwitchTimeToRandom () {
	  return fSwitchTimeToRandom;
  }

  public int getPercentageOfRandomGen () {
	  return fPercentageOfRandomGen;
  }
  
  public boolean getPrintModelCoverage () {
	  return fPrintModelCoverage;
  }
  
  public boolean getExhaustiveDiversifySeq () {
	  return fExhaustiveDiversifySeq;
  }
  
  public boolean getUseTheoryCheck () {
	  return fUseTheoryCheck;
  }
  
  public boolean getCheckNPE () {
	  return fCheckNPE;
  }
  
  public boolean getAddReturnTypeRelatedMethods () {
	  return fAddReturnTypeRelatedMethods;
  }
  
  public boolean getProcessLargeTrace () {
	  return fProcessLargeTrace;
  }
  
  public boolean getDumpModelAsText () {
	  return fDumpModelAsText;
  }

  public boolean getSaveTraceAsTxt () {
	  return fSaveTraceAsTxt;
  }
  
  public boolean getOnlyOutputFailedTests () {
	  return fOnlyOutputFailedTests;
  }
  
  public boolean getFilterRedundantFailures () {
	  return fFilterRedundantFailures;
  }
  
  public String getErrorIgnoredMethods() {
	  return fErrorIgnoredMethods;
  }
  
  public boolean getRandom () {
	  return fUseRandom;
  }
  
  public boolean getDisableAssertion () {
	  return fDisableAssertion;
  }
  
  public String getExceptionDumpFile() {
	  return fExceptionDumpFile;
  }
  
  public boolean getVerbose () {
	  return fUseVerbose;
  }
  
  public String getLogFile() {
	  return fLogFile;
  }
  
//-----------------------------------------------
  
  
  public static int getPort(ILaunchConfiguration config) {
    return getAttribute(config, ILaunchConfigurationConstants.ATTR_PORT,
        IConstants.INVALID_PORT);
  }

  public static List<String> getAvailableTypes(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_AVAILABLE_TYPES,
        EMPTY_STRING_LIST);
  }
  
  public static List<String> getGrayedTypes(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_GRAYED_TYPES,
        EMPTY_STRING_LIST);
  }

  public static List<String> getCheckedTypes(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_CHECKED_TYPES,
        EMPTY_STRING_LIST);
  }

  public static List<String> getAvailableMethods(ILaunchConfiguration config, String typeMnemonic) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_AVAILABLE_METHODS_PREFIX + typeMnemonic,
        EMPTY_STRING_LIST);
  }
  
  public static List<String> getCheckedMethods(ILaunchConfiguration config, String typeMnemonic) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_CHECKED_METHODS_PREFIX + typeMnemonic,
        EMPTY_STRING_LIST);
  }
  
  public static String getRandomSeed(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_RANDOM_SEED,
        ILaunchConfigurationConstants.DEFAULT_RANDOM_SEED);
  }

  public static String getMaxTestSize(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE,
        ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TEST_SIZE);
  }

  public static boolean getUseThreads(ILaunchConfiguration config) {
    return Boolean.parseBoolean(getAttribute(config,
        ILaunchConfigurationConstants.ATTR_USE_THREADS,
        ILaunchConfigurationConstants.DEFAULT_USE_THREADS));
  }

  public static String getThreadTimeout(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT,
        ILaunchConfigurationConstants.DEFAULT_THREAD_TIMEOUT);
  }

  public static boolean getUseNull(ILaunchConfiguration config) {
    return Boolean.parseBoolean(getAttribute(config,
        ILaunchConfigurationConstants.ATTR_USE_NULL,
        ILaunchConfigurationConstants.DEFAULT_USE_NULL));
  }

  public static String getNullRatio(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_NULL_RATIO,
        ILaunchConfigurationConstants.DEFAULT_NULL_RATIO);
  }

  public static String getInputLimit(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_INPUT_LIMIT,
        ILaunchConfigurationConstants.DEFAULT_INPUT_LIMIT);
  }

  public static String getTimeLimit(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_TIME_LIMIT,
        ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT);
  }

  public static String getProjectName(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_PROJECT_NAME,
        ILaunchConfigurationConstants.DEFAULT_PROJECT);
  }
  
  public static String getOutputDirectoryName(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME,
        ILaunchConfigurationConstants.DEFAULT_OUTPUT_DIRECTORY_NAME);
  }

  public static String getJUnitPackageName(
      ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME,
        ILaunchConfigurationConstants.DEFAULT_JUNIT_PACKAGE_NAME);
  }

  public static String getJUnitClassName(
      ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME,
        ILaunchConfigurationConstants.DEFAULT_JUNIT_CLASS_NAME);
  }

  public static String getTestKinds(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_TEST_KINDS,
        ILaunchConfigurationConstants.DEFAULT_TEST_KINDS);
  }

  public static String getMaxTestsWritten(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN,
        ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_WRITTEN);
  }

  public static String getMaxTestsPerFile(ILaunchConfiguration config) {
    return getAttribute(config,
        ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE,
        ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_PER_FILE);
  }
  
  public static void deleteAvailableMethods(ILaunchConfigurationWorkingCopy config, String typeMnemonic) {
    setAttribute(config,
        ILaunchConfigurationConstants.ATTR_AVAILABLE_METHODS_PREFIX + typeMnemonic,
        (String) null);
  }
  
  public static void deleteCheckedMethods(ILaunchConfigurationWorkingCopy config, String typeMnemonic) {
    setAttribute(config,
        ILaunchConfigurationConstants.ATTR_CHECKED_METHODS_PREFIX + typeMnemonic,
        (String) null);
  }

  /** JANALA ATTR */  
  public static String getJanalaMaxIterations(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_MAX_ITERATIONS,
			  ILaunchConfigurationConstants.DEFAULT_MAX_ITERATIONS);
  }
  
  public static String getJanalaOfflineMode(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_OFFLINE_MODE,
			  ILaunchConfigurationConstants.DEFAULT_OFFLINE_MODE);
  }
  
  /** PALUS ATTR */  
  public static String getClassFile(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_CLASS_FILE,
			  ILaunchConfigurationConstants.DEFAULT_CLASS_FILE);
  }
  
  public static String getTraceFile(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_TRACE_FILE,
			  ILaunchConfigurationConstants.DEFAULT_TRACE_FILE);
  }
  
  public static String getModelClassFile(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_MODEL_CLASS_FILE,
			  ILaunchConfigurationConstants.DEFAULT_MODEL_CLASS_FILE);
  }
  
  public static boolean getDontBuildModelFromTrace (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_DONT_BUILD_MODEL_FROM_TRACE,
			  ILaunchConfigurationConstants.DEFAULT_DONT_BUILD_MODEL_FROM_TRACE));
  }
  
  public static boolean getDontProcessAllTrace (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_DONT_PROCESS_ALL_TRACE,
			  ILaunchConfigurationConstants.DEFAULT_DONT_PROCESS_ALL_TRACE));
  }
  
  public static String getInstancePerModel(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_INSTANCE_PER_MODEL,
			  ILaunchConfigurationConstants.DEFAULT_INSTANCE_PER_MODEL);
  }
  
  public static String getInstanceProcessFile (ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_INSTANCE_PROCESS_FILE,
			  ILaunchConfigurationConstants.DEFAULT_INSTANCE_PROCESS_FILE);
  }

  public static boolean getAutoSwitchToRandom (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_AUTO_SWITCH_TO_RANDOM,
			  ILaunchConfigurationConstants.DEFAULT_AUTO_SWITCH_TO_RANDOM));
  }
  
  public static String getSwitchTimeToRandom (ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_SWITCH_TIME_TO_RANDOM,
			  ILaunchConfigurationConstants.DEFAULT_SWITCH_TIME_TO_RANDOM);
  }

  public static String getPercentageOfRandomGen (ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_PERCENTAGE_OF_RANDOM_GEN,
			  ILaunchConfigurationConstants.DEFAULT_PERCENTAGE_OF_RANDOM_GEN);
  }
  
  public static boolean getPrintModelCoverage (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_PRINT_MODEL_COVERAGE,
			  ILaunchConfigurationConstants.DEFAULT_PRINT_MODEL_COVERAGE));
  }
  
  public static boolean getExhaustiveDiversifySeq (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_EXHAUSTIVE_DIVERSIFY_SEQ,
			  ILaunchConfigurationConstants.DEFAULT_EXHAUSTIVE_DIVERSIFY_SEQ));
  }
  
  public static boolean getUseTheoryCheck (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_USE_THEORY_CHECK,
			  ILaunchConfigurationConstants.DEFAULT_USE_THEORY_CHECK));
  }
  
  public static boolean getCheckNPE (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_CHECK_NPE,
			  ILaunchConfigurationConstants.DEFAULT_CHECK_NPE));
  }
  
  public static boolean getAddReturnTypeRelatedMethods (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_ADD_RETURN_TYPE_RELATED_METHODS,
			  ILaunchConfigurationConstants.DEFAULT_ADD_RETURN_TYPE_RELATED_METHODS));
  }
  
  public static boolean getProcessLargeTrace (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_PROCESS_LARGE_TRACE,
			  ILaunchConfigurationConstants.DEFAULT_PROCESS_LARGE_TRACE));
  }
  
  public static boolean getDumpModelAsText (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_DUMP_MODEL_AS_TEXT,
			  ILaunchConfigurationConstants.DEFAULT_DUMP_MODEL_AS_TEXT));
  }

  public static boolean getSaveTraceAsTxt (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_SAVE_TRACE_AS_TXT,
			  ILaunchConfigurationConstants.DEFAULT_SAVE_TRACE_AS_TXT));
  }
  
  public static boolean getOnlyOutputFailedTests (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_ONLY_OUTPUT_FAILED_TESTS,
			  ILaunchConfigurationConstants.DEFAULT_ONLY_OUTPUT_FAILED_TESTS));
  }
  
  public static boolean getFilterRedundantFailures (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_FILTER_REDUNDANT_FAILURES,
			  ILaunchConfigurationConstants.DEFAULT_FILTER_REDUNDANT_FAILURES));
  }
  
  public static String getErrorIgnoredMethods(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_ERROR_IGNORED_METHODS,
			  ILaunchConfigurationConstants.DEFAULT_ERROR_IGNORED_METHODS);
  }
  
  public static boolean getUseRandom (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_USE_RANDOM,
			  ILaunchConfigurationConstants.DEFAULT_USE_RANDOM));
  }
  
  public static boolean getDisableAssertion (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_DISABLE_ASSERTION,
			  ILaunchConfigurationConstants.DEFAULT_DISABLE_ASSERTION));
  }
  
  public static String getExceptionDumpFile(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_EXCEPTION_DUMP_FILE,
			  ILaunchConfigurationConstants.DEFAULT_EXCEPTION_DUMP_FILE);
  }
  
  public static boolean getVerbose (ILaunchConfiguration config) {
	  return Boolean.parseBoolean(getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_USE_VERBOSE,
			  ILaunchConfigurationConstants.DEFAULT_USE_VERBOSE));
  }
  
  public static String getLogFile(ILaunchConfiguration config) {
	  return getAttribute(config,
			  ILaunchConfigurationConstants.ATTR_LOG_FILE,
			  ILaunchConfigurationConstants.DEFAULT_LOG_FILE);
  }

 // ---------------------------------------------------------------------------------
  
  /*
   * Methods to set ILaunchConfigurationWorkingCopy attributes
   */
  public static void setPort(ILaunchConfigurationWorkingCopy config, int port) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_PORT, port);
  }

  public static void setAvailableTypes(ILaunchConfigurationWorkingCopy config, List<String> availableTypes) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_AVAILABLE_TYPES, availableTypes);
  }

  public static void setGrayedTypes(ILaunchConfigurationWorkingCopy config, List<String> grayedTypes) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_GRAYED_TYPES, grayedTypes);
  }
  
  public static void setCheckedTypes(ILaunchConfigurationWorkingCopy config, List<String> checkedTypes) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_CHECKED_TYPES, checkedTypes);
  }

  public static void setAvailableMethods(ILaunchConfigurationWorkingCopy config, String typeMnemonic, List<String> availableMethods) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_AVAILABLE_METHODS_PREFIX + typeMnemonic, availableMethods);
  }

  public static void setCheckedMethods(ILaunchConfigurationWorkingCopy config, String typeMnemonic, List<String> selectedMethods) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_CHECKED_METHODS_PREFIX + typeMnemonic, selectedMethods);
  }

  public static void setRandomSeed(ILaunchConfigurationWorkingCopy config, String seed) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_RANDOM_SEED, seed);
  }

  public static void setMaxTestSize(ILaunchConfigurationWorkingCopy config, String size) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE, size);
  }

  public static void setUseThreads(ILaunchConfigurationWorkingCopy config, boolean useThreads) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_USE_THREADS, Boolean.toString(useThreads));
  }

  public static void setThreadTimeout(ILaunchConfigurationWorkingCopy config, String threadTimeout) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT, threadTimeout);
  }

  public static void setUseNull(ILaunchConfigurationWorkingCopy config, boolean useNull) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_USE_NULL, Boolean.toString(useNull));
  }

  public static void setNullRatio(ILaunchConfigurationWorkingCopy config, String nullRatio) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_NULL_RATIO, nullRatio);
  }

  public static void setInputLimit(ILaunchConfigurationWorkingCopy config, String testInputs) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_INPUT_LIMIT, testInputs);
  }

  public static void setTimeLimit(ILaunchConfigurationWorkingCopy config, String timeLimit) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_TIME_LIMIT, timeLimit);
  }

  public static void setProjectName(ILaunchConfigurationWorkingCopy config, String projectName) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
  }

  public static void setOutputDirectoryName(ILaunchConfigurationWorkingCopy config, String outputDirectory) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_OUTPUT_DIRECTORY_NAME, outputDirectory);
  }

  public static void setJUnitPackageName(ILaunchConfigurationWorkingCopy config, String junitPackageName) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_JUNIT_PACKAGE_NAME, junitPackageName);
  }

  public static void setJUnitClassName(ILaunchConfigurationWorkingCopy config, String junitClassName) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_JUNIT_CLASS_NAME, junitClassName);
  }

  public static void setTestKinds(ILaunchConfigurationWorkingCopy config, String testKinds) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_TEST_KINDS, testKinds);
  }

  public static void setMaxTestsWritten(ILaunchConfigurationWorkingCopy config, String maxTestsWritten) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN, maxTestsWritten);
  }

  public static void setMaxTestsPerFile(ILaunchConfigurationWorkingCopy config, String maxTestsPerFile) {
    setAttribute(config, ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE, maxTestsPerFile);
  }

  private static int getAttribute(ILaunchConfiguration config,
      String attributeName, int defaultValue) {
    try {
      return config.getAttribute(attributeName, defaultValue);
    } catch (CoreException ce) {
      return defaultValue;
    }
  }
  
  private static String getAttribute(ILaunchConfiguration config,
      String attributeName, String defaultValue) {
    try {
      return config.getAttribute(attributeName, defaultValue);
    } catch (CoreException ce) {
      return defaultValue;
    }
  }

  @SuppressWarnings("unchecked")
  private static List<String> getAttribute(ILaunchConfiguration config,
      String attributeName, List<String> defaultValue) {
    try {
      return (List<String>) config.getAttribute(attributeName, defaultValue);
    } catch (CoreException ce) {
      return defaultValue;
    }
  }

  private static void setAttribute(ILaunchConfigurationWorkingCopy config,
      String attributeName, int value) {
    
    config.setAttribute(attributeName, value);
  }
  
  private static void setAttribute(ILaunchConfigurationWorkingCopy config,
      String attributeName, String value) {
    config.setAttribute(attributeName, value);
  }

  private static void setAttribute(ILaunchConfigurationWorkingCopy config,
      String attributeName, List<String> value) {
    config.setAttribute(attributeName, value);
  }

  public static void saveClassTree(ILaunchConfigurationWorkingCopy config,
      List<String> availableTypesa, List<String> checkedTypesa, List<String> grayedTypesa,
      Collection<String> deletedTypesa, Map<String, List<String>> availableMethodsByDeclaringTypes,
      Map<String, List<String>> checkedMethodsByDeclaringTypes) {

    if (deletedTypesa != null) {
      for (String mnemonic : deletedTypesa) {
    	  ArgumentCollector.deleteAvailableMethods(config, mnemonic);
    	  ArgumentCollector.deleteCheckedMethods(config, mnemonic);
      }
    }

    ArgumentCollector.setAvailableTypes(config, availableTypesa);
    ArgumentCollector.setGrayedTypes(config, grayedTypesa);
    ArgumentCollector.setCheckedTypes(config, checkedTypesa);

    if (availableMethodsByDeclaringTypes != null) {
      for (String typeMnemonic : availableMethodsByDeclaringTypes.keySet()) {
        List<String> availableMethods = availableMethodsByDeclaringTypes.get(typeMnemonic);

        ArgumentCollector.setAvailableMethods(config, typeMnemonic, availableMethods);
      }
    }

    if (checkedMethodsByDeclaringTypes != null) {
      for (String typeMnemonic : checkedMethodsByDeclaringTypes.keySet()) {
        List<String> checkedMethods = checkedMethodsByDeclaringTypes.get(typeMnemonic);

        ArgumentCollector.setCheckedMethods(config, typeMnemonic, checkedMethods);
      }
    }
  }

  /** JANALA ATTR */
  public static void setJanalaMaxIterations(ILaunchConfigurationWorkingCopy config, String value) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_MAX_ITERATIONS, value);
  }

  public static void setJanalaOfflineMode(ILaunchConfigurationWorkingCopy config, String value) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_OFFLINE_MODE, value);
  }

  /** PALUS ATTR */
  public static void setClassFile(ILaunchConfigurationWorkingCopy config, String classFile) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_CLASS_FILE, classFile);
  }
  
  public static void setTraceFile(ILaunchConfigurationWorkingCopy config, String traceFile) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_TRACE_FILE, traceFile);
  }
  
  public static void setModelClassFile(ILaunchConfigurationWorkingCopy config, String modelClassFile) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_MODEL_CLASS_FILE, modelClassFile);
  }
  
  public static void setDontBuildModelFromTrace (ILaunchConfigurationWorkingCopy config, String dontBuildModel) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_DONT_BUILD_MODEL_FROM_TRACE, dontBuildModel);
  }
  
  public static void setDontProcessAllTrace (ILaunchConfigurationWorkingCopy config, String processAllTrace) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_DONT_PROCESS_ALL_TRACE, processAllTrace);
  }
  
  public static void setInstancePerModel(ILaunchConfigurationWorkingCopy config, String instancePerModel) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_INSTANCE_PER_MODEL, instancePerModel);
  }
  
  public static void setInstanceProcessFile (ILaunchConfigurationWorkingCopy config, String instanceProcessFile) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_INSTANCE_PROCESS_FILE, instanceProcessFile);
  }

  public static void setAutoSwitchToRandom (ILaunchConfigurationWorkingCopy config, String autoSwitchToRandom) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_AUTO_SWITCH_TO_RANDOM, autoSwitchToRandom);
  }
  
  public static void setSwitchTimeToRandom (ILaunchConfigurationWorkingCopy config, String switchTimeToRandom) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_SWITCH_TIME_TO_RANDOM, switchTimeToRandom);
  }

  public static void setPercentageOfRandomGen (ILaunchConfigurationWorkingCopy config, String percentageOfRandomGen) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_PERCENTAGE_OF_RANDOM_GEN, percentageOfRandomGen);
  }
  
  public static void setPrintModelCoverage (ILaunchConfigurationWorkingCopy config, String printModelCoverage) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_PRINT_MODEL_COVERAGE, printModelCoverage);
  }
  
  public static void setExhaustiveDiversifySeq (ILaunchConfigurationWorkingCopy config, String exhaustiveDiversifySeq) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_EXHAUSTIVE_DIVERSIFY_SEQ, exhaustiveDiversifySeq);
  }
  
  public static void setUseTheoryCheck (ILaunchConfigurationWorkingCopy config, String useTheoryCheck) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_USE_THEORY_CHECK, useTheoryCheck);
  }
  
  public static void setCheckNPE (ILaunchConfigurationWorkingCopy config, String checkNPE) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_CHECK_NPE, checkNPE);
  }
  
  public static void setAddReturnTypeRelatedMethods (ILaunchConfigurationWorkingCopy config, String addReturnTypeRelatedMethods) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_ADD_RETURN_TYPE_RELATED_METHODS, addReturnTypeRelatedMethods);
  }
  
  public static void setProcessLargeTrace (ILaunchConfigurationWorkingCopy config, String processLargeTrace) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_PROCESS_LARGE_TRACE, processLargeTrace);
  }
  
  public static void setDumpModelAsText (ILaunchConfigurationWorkingCopy config, String dumpModelAsText) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_DUMP_MODEL_AS_TEXT, dumpModelAsText);
  }

  public static void setSaveTraceAsTxt (ILaunchConfigurationWorkingCopy config, String saveTraceAsTxt) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_SAVE_TRACE_AS_TXT, saveTraceAsTxt);
  }
  
  public static void setOnlyOutputFailedTests (ILaunchConfigurationWorkingCopy config, String onlyOutputFailedTests) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_ONLY_OUTPUT_FAILED_TESTS, onlyOutputFailedTests);
  }
  
  public static void setFilterRedundantFailures (ILaunchConfigurationWorkingCopy config, String filterRedundantFailures) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_FILTER_REDUNDANT_FAILURES, filterRedundantFailures);
  }
  
  public static void setErrorIgnoredMethods(ILaunchConfigurationWorkingCopy config, String errorIgnoredMethods) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_ERROR_IGNORED_METHODS, errorIgnoredMethods);
  }
  
  public static void setUseRandom (ILaunchConfigurationWorkingCopy config, String useRandom) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_USE_RANDOM, useRandom);
  }
  
  public static void setDisableAssertion (ILaunchConfigurationWorkingCopy config, String disableAssertion) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_DISABLE_ASSERTION, disableAssertion);
  }
  
  public static void setExceptionDumpFile(ILaunchConfigurationWorkingCopy config, String exceptionDumpFile) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_EXCEPTION_DUMP_FILE, exceptionDumpFile);
  }
  
  public static void setVerbose (ILaunchConfigurationWorkingCopy config, String useVerbose) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_USE_VERBOSE, useVerbose);
  }
  
  public static void setLogFile(ILaunchConfigurationWorkingCopy config, String logFile) {
	  setAttribute(config, ILaunchConfigurationConstants.ATTR_LOG_FILE, logFile);
  }

 // ---------------------------------------------------------------------------------

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ArgumentCollector) {
    	ArgumentCollector other = (ArgumentCollector) obj;

      return getName().equals(other.getName())
          && getSelectedTypes().equals(other.getSelectedTypes())
          && getSelectedMethodsByType().equals(other.getSelectedMethodsByType())
          && getRandomSeed() == other.getRandomSeed()
          && getMaxTestSize() == other.getMaxTestSize()
          && getUseThreads() == other.getUseThreads()
          && getThreadTimeout() == other.getThreadTimeout()
          && getUseNull() == other.getUseNull()
          && getNullRatio() == other.getNullRatio()
          && getInputLimit() == other.getInputLimit()
          && getTimeLimit() == other.getTimeLimit()
          && getOutputDirectory().equals(other.getOutputDirectory())
          && getJUnitPackageName().equals(other.getJUnitPackageName())
          && getJUnitClassName().equals(other.getJUnitClassName())
          && getTestKinds().equals(other.getTestKinds())
          && getMaxTestsWritten() == other.getMaxTestsWritten()
          && getMaxTestsPerFile() == other.getMaxTestsPerFile();
    }

    return super.equals(obj);
  }
  
  @Override
  public int hashCode() {
    return (getName() + getSelectedTypes().toString()
        + getSelectedMethodsByType().toString() + getRandomSeed() + getMaxTestSize()
        + getUseThreads() + getThreadTimeout() + getUseNull() + getNullRatio()
        + getInputLimit() + getTimeLimit() + getOutputDirectory()
        + getJUnitPackageName() + getJUnitClassName() + getTestKinds()
        + getMaxTestsWritten() + getMaxTestsPerFile()).hashCode();
  }

}
