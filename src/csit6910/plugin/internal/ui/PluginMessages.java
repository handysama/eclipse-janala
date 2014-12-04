package csit6910.plugin.internal.ui;

import org.eclipse.osgi.util.NLS;

/**
 * Strings for Randoop command line options that may be specified by text boxes
 * in the Randoop plugin for Eclipse
 */
public class PluginMessages extends NLS {
  private static final String BUNDLE_NAME = "csit6910.plugin.internal.ui.messages"; //$NON-NLS-1$
  public static String RandoopOption_forbid_null;
  public static String RandoopOption_forbid_null_tooltip;
  public static String RandoopOption_null_ratio_tooltip;
  public static String RandoopOption_inputlimit;
  public static String RandoopOption_inputlimit_tooltip;
  public static String RandoopOption_junit_classname;
  public static String RandoopOption_junit_classname_tooltip;
  public static String RandoopOption_junit_output_dir;
  public static String RandoopOption_junit_output_dir_tooltip;
  public static String RandoopOption_junit_package_name;
  public static String RandoopOption_junit_package_name_tooltip;
  public static String RandoopOption_maxsize;
  public static String RandoopOption_maxsize_tooltip;
  public static String RandoopOption_output_tests;
  public static String RandoopOption_output_tests_tooltip;
  public static String RandoopOption_outputlimit;
  public static String RandoopOption_outputlimit_tooltip;
  public static String RandoopOption_randomseed;
  public static String RandoopOption_randomseed_tooltip;
  public static String RandoopOption_testsperfile;
  public static String RandoopOption_testsperfile_tooltip;
  public static String RandoopOption_timelimit;
  public static String RandoopOption_timelimit_tooltip;
  public static String RandoopOption_usethreads;
  public static String RandoopOption_usethreads_tooltip;
  public static String RandoopOption_timeout_tooltip;
  // csit6910 modified here
  public static String PalusOption_timelimit;
  public static String PalusOption_timelimit_tooltip;
  public static String PalusOption_classfile;
  public static String PalusOption_classfile_tooltip;
  public static String PalusOption_tracefile;
  public static String PalusOption_tracefile_tooltip;
  public static String JanalaOption_testingmode;
  public static String JanalaOption_testingmode_tooltip;
  public static String JanalaOption_offline;
  public static String JanalaOption_offline_tooltip;
  public static String JanalaOption_maxiterations;
  public static String JanalaOption_maxiterations_tooltip;  
  public static String JanalaOption_mainclass;
  public static String JanalaOption_mainclass_tooltip;


  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, PluginMessages.class);
  }

  private PluginMessages() {
  }
}
