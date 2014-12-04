package csit6910.plugin.internal.ui.options;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import csit6910.plugin.internal.ui.PluginMessages;
import csit6910.plugin.internal.core.launching.ILaunchConfigurationConstants;
import csit6910.plugin.internal.core.PluginStatus;

//import randoop.plugin.internal.core.RandoopStatus;
import randoop.plugin.internal.core.TestKinds;
import randoop.plugin.internal.core.launching.IRandoopLaunchConfigurationConstants;
//import randoop.plugin.internal.core.launching.IRandoopLaunchConfigurationConstants;
import randoop.plugin.internal.ui.options.BoundedDoubleOption;
import randoop.plugin.internal.ui.options.ComboOption;
import randoop.plugin.internal.ui.options.EnablementOption;
import randoop.plugin.internal.ui.options.IEnableableOption;
import randoop.plugin.internal.ui.options.IOption;
import randoop.plugin.internal.ui.options.IOptionChangeListener;
import randoop.plugin.internal.ui.options.IntegerOption;
import randoop.plugin.internal.ui.options.PositiveIntegerOption;
//import randoop.plugin.internal.ui.RandoopMessages;
import randoop.plugin.internal.ui.SWTFactory;

/**
 * 
 * @author Peter Kalauskas
 */
public class OptionFactory {

  private final static int MARGIN = 5;
  private final static int INDENTATION = 6;
  private final static int VERTICAL_LABEL_SPACING = 9;
  private final static int VERTICAL_TEXT_SPACING = 6;

  /**
   * Creates and returns a list of placeholders for <code>Option</code>s used in
   * <code>createStoppingCriterionOptionGroup</code>. These placeholders may be
   * used to set defaults.
   */
  public static List<IOption> createStoppingCriterionOptionGroupPlaceholders() {
    List<IOption> options = new ArrayList<IOption>();
    
    options.add(new InputLimitOption());
    options.add(new TimeLimitOption());

    return options;
  }
  
  public static List<IOption> createStoppingCriterionOptionGroup(Composite parent,
      IOptionChangeListener changeListener) {
    
    Composite comp = new Composite(parent, SWT.NONE);

    FormLayout formLayout = new FormLayout();
    formLayout.marginTop = MARGIN;
    formLayout.marginBottom = MARGIN;
    formLayout.marginRight = MARGIN;
    formLayout.marginLeft = MARGIN;
    formLayout.spacing = 4;
    comp.setLayout(formLayout);

    List<IOption> options = new ArrayList<IOption>();
    Font boldFont = SWTFactory.getBoldFont(comp.getFont());

    Label stoppingCriterionTitle = new Label(comp, SWT.NONE);
    stoppingCriterionTitle.setText("Stopping Criterion");
    stoppingCriterionTitle.setFont(boldFont);
    Label stoppingCriterionSubtitle = new Label(comp, SWT.NONE);
    stoppingCriterionSubtitle.setText("Stop test generation after:");

    Label inputLimitLabel = new Label(comp, SWT.NONE);
    inputLimitLabel.setText("Randoop has generated");
    Text inputLimitText = new Text(comp, SWT.BORDER);
    Label inputLimitLabel2 = new Label(comp, SWT.NONE);
    inputLimitLabel2.setText("tests, OR");

    Label timeLimitLabel = new Label(comp, SWT.NONE);
    timeLimitLabel.setText("Randoop has generated tests for ");
    Text timeLimitText = new Text(comp, SWT.BORDER);
    Label timeLimitLabel2 = new Label(comp, SWT.NONE);
    timeLimitLabel2.setText(" seconds");
    Label convertedTimeLimit = new Label(comp, SWT.NONE);

    FormData data = new FormData();
    stoppingCriterionTitle.setLayoutData(data);

    data = new FormData();
    data.left = new FormAttachment(stoppingCriterionTitle);
    stoppingCriterionSubtitle.setLayoutData(data);

    // The input limit phrase
    data = new FormData();
    data.top = new FormAttachment(stoppingCriterionTitle, VERTICAL_LABEL_SPACING);
    data.left = new FormAttachment(0, INDENTATION);
    inputLimitLabel.setLayoutData(data);

    data = new FormData(SWTFactory.computeWidth(inputLimitText, 8), SWT.DEFAULT);
    data.top = new FormAttachment(stoppingCriterionTitle, VERTICAL_TEXT_SPACING);
    data.left = new FormAttachment(inputLimitLabel);
    inputLimitText.setLayoutData(data);

    data = new FormData();
    data.top = new FormAttachment(stoppingCriterionTitle, VERTICAL_LABEL_SPACING);
    data.left = new FormAttachment(inputLimitText);
    inputLimitLabel2.setLayoutData(data);

    // The time limit phrase
    data = new FormData();
    data.top = new FormAttachment(inputLimitLabel, VERTICAL_LABEL_SPACING);
    data.left = new FormAttachment(0, INDENTATION);
    timeLimitLabel.setLayoutData(data);

    data = new FormData(SWTFactory.computeWidth(timeLimitText, 5), SWT.DEFAULT);
    data.top = new FormAttachment(inputLimitLabel, VERTICAL_TEXT_SPACING);
    data.left = new FormAttachment(timeLimitLabel);
    timeLimitText.setLayoutData(data);

    data = new FormData();
    data.top = new FormAttachment(inputLimitLabel, VERTICAL_LABEL_SPACING);
    data.left = new FormAttachment(timeLimitText);
    timeLimitLabel2.setLayoutData(data);

    data = new FormData(SWTFactory.computeWidth(convertedTimeLimit, 13), SWT.DEFAULT);
    data.top = new FormAttachment(inputLimitLabel, VERTICAL_LABEL_SPACING);
    data.left = new FormAttachment(timeLimitLabel2);
    convertedTimeLimit.setLayoutData(data);

    options.add(new InputLimitOption(inputLimitText));
    options.add(new TimeLimitOption(timeLimitText, convertedTimeLimit));

    for (IOption option : options) {
      option.addChangeListener(changeListener);
    }

    return options;
  }

  /**
   * Creates and returns a list of placeholders for <code>Option</code>s used in
   * <code>createOutputParametersOptionGroup</code>. These placeholders may be
   * used to set defaults.
   */
  public static List<IOption> createOutputParametersOptionGroupPlaceholders() {
    List<IOption> options = new ArrayList<IOption>();
    
    options.add(new TestKindsOption());

    return options;
  }
  
  public static List<IOption> createOutputParametersOptionGroup(Composite parent,
      IOptionChangeListener changeListener) {

    Composite comp = new Composite(parent, SWT.NONE);

    FormLayout formLayout = new FormLayout();
    formLayout.marginTop = MARGIN;
    formLayout.marginBottom = MARGIN;
    formLayout.marginRight = MARGIN;
    formLayout.marginLeft = MARGIN;
    formLayout.spacing = 4;
    comp.setLayout(formLayout);

    List<IOption> options = new ArrayList<IOption>();
    Font boldFont = SWTFactory.getBoldFont(comp.getFont());

    Label outputParametersTitle = new Label(comp, SWT.NONE);
    outputParametersTitle.setText("Test Output Parameters");
    outputParametersTitle.setFont(boldFont);

    Label testKindsLabel = new Label(comp, SWT.NONE);
    testKindsLabel.setText("Output tests that:");

    Combo testKindsCombo = new Combo(comp, SWT.READ_ONLY);
    testKindsCombo.setItems(TestKinds.getTranslatableNames());
    testKindsCombo.select(0);

    testKindsLabel.setToolTipText(PluginMessages.RandoopOption_output_tests_tooltip);
    testKindsCombo.setToolTipText(PluginMessages.RandoopOption_output_tests_tooltip);

    FormData data = new FormData();
    outputParametersTitle.setLayoutData(data);
  
    // The input limit phrase
    data = new FormData();
    data.top = new FormAttachment(outputParametersTitle, VERTICAL_LABEL_SPACING);
    data.left = new FormAttachment(0, INDENTATION);
    testKindsLabel.setLayoutData(data);

    data = new FormData();
    data.top = new FormAttachment(outputParametersTitle, VERTICAL_TEXT_SPACING);
    data.left = new FormAttachment(testKindsLabel);
    testKindsCombo.setLayoutData(data);

    options.add(new TestKindsOption(testKindsCombo));

    for (IOption option : options) {
      option.addChangeListener(changeListener);
    }

    return options;
  }

  /**
   * Creates and returns a list of placeholders for <code>Option</code>s used in
   * <code>createAdvancedOptionGroup</code>. These placeholders may be
   * used to set defaults.
   */
  public static List<IOption> createAdvancedOptionGroupPlaceholders() {
    List<IOption> options = new ArrayList<IOption>();
    
    options.add(new RandomSeedOption());
    options.add(new MaximumTestSizeOption());
    options.add(new UseThreadsOption(new ThreadTimeoutOption()));
    options.add(new UseNullOption(new NullRatioOption()));
    options.add(new MaximumTestsPerFileOption());
    // options.add(new JUnitTestTypesOption());
    // addOption(fMaxTestsWritten);
    
    return options;
  }
  
  public static List<IOption> createAdvancedOptionGroup(Composite parent,
      IOptionChangeListener changeListener) {

    Composite comp = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_BOTH);

    GridLayout ld = (GridLayout) comp.getLayout();
    ld.marginWidth = 1;
    ld.marginHeight = 1;
    ld.marginLeft = MARGIN;
    ld.marginRight = MARGIN;
    ld.marginTop = MARGIN;
    ld.marginBottom = MARGIN;

    List<IOption> options = new ArrayList<IOption>();
    Font boldFont = SWTFactory.getBoldFont(comp.getFont());

    Label advancedTitle = SWTFactory.createLabel(comp, "Advanced", 1);
    advancedTitle.setFont(boldFont);

    comp = SWTFactory.createComposite(comp, 2, 1, GridData.FILL_BOTH);
    ld = (GridLayout) comp.getLayout();
    ld.marginLeft = INDENTATION;

    Label randomSeedLabel = SWTFactory.createLabel(comp,
    		PluginMessages.RandoopOption_randomseed, 1);
    Text randomSeedText = SWTFactory.createSingleText(comp, 1);
    randomSeedLabel.setToolTipText(PluginMessages.RandoopOption_randomseed_tooltip);
    IOption randomSeed = new RandomSeedOption(randomSeedText);

    Label maxTestSizeLabel = SWTFactory.createLabel(comp,
    		PluginMessages.RandoopOption_maxsize, 1);
    Text maxTestSizeText = SWTFactory.createSingleText(comp, 1);
    maxTestSizeLabel.setToolTipText(PluginMessages.RandoopOption_maxsize_tooltip);
    IOption maxTestSize = new MaximumTestSizeOption(maxTestSizeText);

    Button threadTimeoutButton = SWTFactory.createCheckButton(comp,
    		PluginMessages.RandoopOption_usethreads, null, false, 1);
    Text threadTimeoutText = SWTFactory.createSingleText(comp, 1);
    threadTimeoutButton.setToolTipText(PluginMessages.RandoopOption_usethreads_tooltip);
    threadTimeoutButton.setSelection(true);
    threadTimeoutText.setToolTipText(PluginMessages.RandoopOption_timeout_tooltip);
    threadTimeoutText.setEnabled(threadTimeoutButton.getSelection());
    ThreadTimeoutOption threadTimeout = new ThreadTimeoutOption(threadTimeoutText);
    IOption useThreads = new UseThreadsOption(threadTimeout, threadTimeoutButton);

    Button nullRatioButton = SWTFactory.createCheckButton(comp,
    		PluginMessages.RandoopOption_forbid_null, null, false, 1);
    Text nullRatioText = SWTFactory.createSingleText(comp, 1);
    nullRatioButton.setToolTipText(PluginMessages.RandoopOption_forbid_null_tooltip);
    nullRatioButton.setSelection(false);
    nullRatioText.setToolTipText(PluginMessages.RandoopOption_null_ratio_tooltip);
    nullRatioText.setEnabled(nullRatioButton.getSelection());
    NullRatioOption nullRatio = new NullRatioOption(nullRatioText);
    IOption useNull = new UseNullOption(nullRatio, nullRatioButton);

    Label maxTestsPerFileLabel = SWTFactory.createLabel(comp,
    		PluginMessages.RandoopOption_testsperfile, 1);
    Text maxTestsPerFileText = SWTFactory.createSingleText(comp, 1);
    maxTestsPerFileLabel
        .setToolTipText(PluginMessages.RandoopOption_testsperfile_tooltip);
    IOption maxTestsPerFile = new MaximumTestsPerFileOption(maxTestsPerFileText);

    // Label maxTestsWrittenLabel = SWTFactory.createLabel(group,
    // RandoopMessages.RandoopOption_outputlimit, 1);
    // Text maxTestsWrittenText = SWTFactory.createSingleText(group, 1);
    // maxTestsWrittenLabel.setToolTipText(RandoopMessages.RandoopOption_outputlimit_tooltip);
    // maxTestsWrittenText.setToolTipText(RandoopMessages.RandoopOption_outputlimit_tooltip);
    // fMaxTestsWritten =
    // new MaximumTestsWrittenOption(maxTestsWrittenText);

    /* csit6910 */
//    Label junitTestTypesLabel = new Label(comp, SWT.NONE);
//    junitTestTypesLabel.setText("JUnit Test Type:");

//    Combo junitTestTypesCombo = new Combo(comp, SWT.READ_ONLY);
//    junitTestTypesCombo.setItems(JUnitTestTypes.getTranslatableNames());
//    junitTestTypesCombo.select(0);
//    junitTestTypesCombo.setToolTipText(PalusMessages.RandoopOption_junit_test_types_tooltip);
    
//    IOption junitTestTypes = new JUnitTestTypesOption(junitTestTypesCombo);
    // -------------------
    
    options.add(randomSeed);
    options.add(maxTestSize);
    options.add(useThreads);
    options.add(useNull);
    options.add(maxTestsPerFile);
//    options.add(junitTestTypes);
    // addOption(fMaxTestsWritten);
    
    threadTimeout.addChangeListener(changeListener);
    nullRatio.addChangeListener(changeListener);
    for (IOption option : options) {
      option.addChangeListener(changeListener);
    }

    return options;
  }

  /**
   * csit6910 (palus) creates and returns a list of placeholders for <code>Option</code>s used in
   * <code>createAdvancedOptionGroup</code>. These placeholders may be
   * used to set defaults.
   */
  public static List<IOption> createPalusArgsOptionGroupPlaceholders() {
    List<IOption> options = new ArrayList<IOption>();
    
    options.add(new PTimeLimitOption());
    options.add(new RandomSeedOption());
//    options.add(new MaximumTestSizeOption());
//    options.add(new UseThreadsOption(new ThreadTimeoutOption()));
//    options.add(new UseNullOption(new NullRatioOption()));
    
    return options;
  }
  
  public static List<IOption> createPalusOptionGroup(Composite parent,
      IOptionChangeListener changeListener) {

    Composite comp = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_BOTH);

    GridLayout ld = (GridLayout) comp.getLayout();
    ld.marginWidth = 1;
    ld.marginHeight = 1;
    ld.marginLeft = MARGIN;
    ld.marginRight = MARGIN;
    ld.marginTop = MARGIN;
    ld.marginBottom = MARGIN;

    List<IOption> options = new ArrayList<IOption>();
    Font boldFont = SWTFactory.getBoldFont(comp.getFont());

    Label advancedTitle = SWTFactory.createLabel(comp, "Program Arguments", 1);
    advancedTitle.setFont(boldFont);

    comp = SWTFactory.createComposite(comp, 2, 1, GridData.FILL_BOTH);
    ld = (GridLayout) comp.getLayout();
    ld.marginLeft = INDENTATION;

    
    Label timeLimitLabel = SWTFactory.createLabel(comp, PluginMessages.PalusOption_timelimit, 1);
    Text timeLimitText = SWTFactory.createSingleText(comp, 1);
    timeLimitLabel.setToolTipText(PluginMessages.PalusOption_timelimit_tooltip);
    IOption timeLimit = new PTimeLimitOption(timeLimitText);

    Label randomSeedLabel = SWTFactory.createLabel(comp,
    		PluginMessages.RandoopOption_randomseed, 1);
    Text randomSeedText = SWTFactory.createSingleText(comp, 1);
    randomSeedLabel.setToolTipText(PluginMessages.RandoopOption_randomseed_tooltip);
    IOption randomSeed = new RandomSeedOption(randomSeedText);

//    Label maxTestSizeLabel = SWTFactory.createLabel(comp,
//        PalusMessages.RandoopOption_maxsize, 1);
//    Text maxTestSizeText = SWTFactory.createSingleText(comp, 1);
//    maxTestSizeLabel.setToolTipText(PalusMessages.RandoopOption_maxsize_tooltip);
//    IOption maxTestSize = new MaximumTestSizeOption(maxTestSizeText);

//    Button threadTimeoutButton = SWTFactory.createCheckButton(comp,
//        PalusMessages.RandoopOption_usethreads, null, false, 1);
//    Text threadTimeoutText = SWTFactory.createSingleText(comp, 1);
//    threadTimeoutButton.setToolTipText(PalusMessages.RandoopOption_usethreads_tooltip);
//    threadTimeoutButton.setSelection(true);
//    threadTimeoutText.setToolTipText(PalusMessages.RandoopOption_timeout_tooltip);
//    threadTimeoutText.setEnabled(threadTimeoutButton.getSelection());
//    ThreadTimeoutOption threadTimeout = new ThreadTimeoutOption(threadTimeoutText);
//    IOption useThreads = new UseThreadsOption(threadTimeout, threadTimeoutButton);

//    Button nullRatioButton = SWTFactory.createCheckButton(comp,
//        PalusMessages.RandoopOption_forbid_null, null, false, 1);
//    Text nullRatioText = SWTFactory.createSingleText(comp, 1);
//    nullRatioButton.setToolTipText(PalusMessages.RandoopOption_forbid_null_tooltip);
//    nullRatioButton.setSelection(false);
//    nullRatioText.setToolTipText(PalusMessages.RandoopOption_null_ratio_tooltip);
//    nullRatioText.setEnabled(nullRatioButton.getSelection());
//    NullRatioOption nullRatio = new NullRatioOption(nullRatioText);
//    IOption useNull = new UseNullOption(nullRatio, nullRatioButton);


    // PALUS: time-limit, class-file, trace-file (hidden)
    options.add(timeLimit);
    options.add(randomSeed);
//    options.add(maxTestSize);
//    options.add(useThreads);
//    options.add(useNull);
    
//    threadTimeout.addChangeListener(changeListener);
//    nullRatio.addChangeListener(changeListener);
    for (IOption option : options) {
      option.addChangeListener(changeListener);
    }

    return options;
  }

  /**
   * csit6910 (janala) creates and returns a list of placeholders for <code>Option</code>s used in
   * <code>createAdvancedOptionGroup</code>. These placeholders may be
   * used to set defaults.
   */
  public static List<IOption> createJanalaArgsOptionGroupPlaceholders() {
    List<IOption> options = new ArrayList<IOption>();
    options.add(new JanalaMaxIterationsOption());
    options.add(new JanalaOfflineOption());
    return options;
  }
  
  public static List<IOption> createJanalaOptionGroup(Composite parent,
      IOptionChangeListener changeListener) {

    Composite comp = SWTFactory.createComposite(parent, 1, 1, GridData.FILL_BOTH);

    GridLayout ld = (GridLayout) comp.getLayout();
    ld.marginWidth = 1;
    ld.marginHeight = 1;
    ld.marginLeft = MARGIN;
    ld.marginRight = MARGIN;
    ld.marginTop = MARGIN;
    ld.marginBottom = MARGIN;

    List<IOption> options = new ArrayList<IOption>();
    Font boldFont = SWTFactory.getBoldFont(comp.getFont());

    Label advancedTitle = SWTFactory.createLabel(comp, "Program Arguments", 1);
    advancedTitle.setFont(boldFont);

    comp = SWTFactory.createComposite(comp, 2, 1, GridData.FILL_BOTH);
    ld = (GridLayout) comp.getLayout();
    ld.marginLeft = INDENTATION;

    Label maxIterationsLabel = SWTFactory.createLabel(comp,
    		PluginMessages.JanalaOption_maxiterations, 1);
    Text maxIterationsText = SWTFactory.createSingleText(comp, 1);
    maxIterationsLabel.setToolTipText(PluginMessages.JanalaOption_maxiterations_tooltip);
    IOption maxIterations = new JanalaMaxIterationsOption(maxIterationsText);
    
    Label testingModeLabel = SWTFactory.createLabel(comp,
    		PluginMessages.JanalaOption_testingmode, 1);
    testingModeLabel.setToolTipText(PluginMessages.JanalaOption_testingmode_tooltip);
    
    Button offlineButton = SWTFactory.createCheckButton(comp, 
    		PluginMessages.JanalaOption_offline, null, false, 1);
    offlineButton.setToolTipText(PluginMessages.JanalaOption_offline_tooltip);
    IOption offlineMode = new JanalaOfflineOption(offlineButton);

    options.add(maxIterations);
    options.add(offlineMode);

    for (IOption option : options) {
      option.addChangeListener(changeListener);
    }

    return options;
  }
  
  private static class JanalaMaxIterationsOption extends IntegerOption {

    public JanalaMaxIterationsOption() {
    }
    
    public JanalaMaxIterationsOption(Text text) {
      super(text);
    }

    @Override
    protected String getName() {
      return "Max iteration";
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_MAX_ITERATIONS;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_MAX_ITERATIONS;
    }
  }
  
  private static class JanalaOfflineOption extends ButtonOption {

	  public JanalaOfflineOption() {
	  }

	  public JanalaOfflineOption(Button button) {
		  super(button);
	  }

	  @Override
	  protected IStatus validate(boolean b) {
		  try {

			  return PluginStatus.OK_STATUS;
		  } catch (IllegalArgumentException e) {
			  return PluginStatus.createUIStatus(IStatus.ERROR, "Input must be boolean.");
		  }
	  }

	  @Override
	  protected boolean getValue() {
		  return fButton.getSelection();
	  }

	  @Override
	  protected String getAttributeName() {
		  return ILaunchConfigurationConstants.ATTR_OFFLINE_MODE;
	  }

	  @Override
	  protected boolean getDefaultValue() {
		  return Boolean.parseBoolean(ILaunchConfigurationConstants.DEFAULT_OFFLINE_MODE);
	  }

  }
  
  
  
  private static class RandomSeedOption extends IntegerOption {

    public RandomSeedOption() {
    }
    
    public RandomSeedOption(Text text) {
      super(text);
    }

    @Override
    protected String getName() {
      return "Random Seed";
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_RANDOM_SEED;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_RANDOM_SEED;
    }
  }

  private static class MaximumTestSizeOption extends PositiveIntegerOption {

    public MaximumTestSizeOption() {
    }
    
    public MaximumTestSizeOption(Text text) {
      super(text);
    }

    @Override
    protected String getName() {
      return "Maximum Test Size";
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_MAXIMUM_TEST_SIZE;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TEST_SIZE;
    }

  }

  private static class UseThreadsOption extends EnablementOption {

    public UseThreadsOption(ThreadTimeoutOption option) {
      super(option);
    }
    
    public UseThreadsOption(ThreadTimeoutOption option, Button enablement) {
      super(option, enablement);
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_USE_THREADS;
    }

    @Override
    protected boolean getDefaultValue() {
      return Boolean
          .parseBoolean(ILaunchConfigurationConstants.DEFAULT_USE_THREADS);
    }

  }

  private static class ThreadTimeoutOption extends PositiveIntegerOption implements
      IEnableableOption {

    public ThreadTimeoutOption() {
    }
    
    public ThreadTimeoutOption(Text text) {
      super(text);
    }

    @Override
    protected String getName() {
      return "Thread Timeout";
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_THREAD_TIMEOUT;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_THREAD_TIMEOUT;
    }

    public void setEnabled(boolean enabled) {
      if (fText != null) {
        fText.setEnabled(enabled);
      }
    }

  }

  private static class UseNullOption extends EnablementOption {
    
    public UseNullOption(NullRatioOption nullRatioOption) {
      super(nullRatioOption);
    }
    
    public UseNullOption(NullRatioOption option, Button enablement) {
      super(option, enablement);
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_USE_NULL;
    }

    @Override
    protected boolean getDefaultValue() {
      return Boolean.parseBoolean(ILaunchConfigurationConstants.DEFAULT_USE_NULL);
    }

  }

  private static class NullRatioOption extends BoundedDoubleOption implements
      IEnableableOption {

    public NullRatioOption() {
    }
    
    public NullRatioOption(Text text) {
      super(text, 0.0, 1.0);
    }

    @Override
    protected String getName() {
      return "Null Ratio";
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_NULL_RATIO;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_NULL_RATIO;
    }

    public void setEnabled(boolean enabled) {
      if (fText != null) {
        fText.setEnabled(enabled);
      }
    }

  }

  private static class PTimeLimitOption extends PositiveIntegerOption {

	  public PTimeLimitOption() {
	  }

	  public PTimeLimitOption(Text text) {
		  super(text);
	  }

	  @Override
	  protected String getName() {
		  return "Time Limit";
	  }

	  @Override
	  public String getAttributeName() {
		  return ILaunchConfigurationConstants.ATTR_TIME_LIMIT;
	  }

	  @Override
	  protected String getDefaultValue() {
		  return ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT;
	  }

  }
  
  private static class InputLimitOption extends PositiveIntegerOption {

    public InputLimitOption() {
    }
    
    public InputLimitOption(Text text) {
      super(text);
    }

    @Override
    protected String getName() {
      return "Input Limit";
    }

    @Override
    public String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_INPUT_LIMIT;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_INPUT_LIMIT;
    }

  }

  private static class TimeLimitOption extends PositiveIntegerOption {
    Label fConvertedTimeLimit;
    
    public TimeLimitOption() {
    }

    public TimeLimitOption(Text text, Label convertedTime) {
      super(text);
      fConvertedTimeLimit = convertedTime;

      fText.addModifyListener(new ModifyListener() {

        public void modifyText(ModifyEvent e) {
          setConvertedTime();
        }
      });

    }

    @Override
    protected String getName() {
      return "Time Limit";
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_TIME_LIMIT;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_TIME_LIMIT;
    }

    private void setConvertedTime() {
      if (fConvertedTimeLimit == null) {
        return;
      }

      try {
        int seconds = Integer.parseInt(fText.getText());

        DecimalFormat twoPlacesFormat = new DecimalFormat("#0.0"); //$NON-NLS-1$
        String timeStr;
        if (seconds < 60) {
          timeStr = ""; //$NON-NLS-1$
        } else if (seconds < 3600) {
          timeStr = MessageFormat.format("({0} minutes)",
              twoPlacesFormat.format(seconds / 60.0));
        } else if (seconds < 86400) {
          timeStr = MessageFormat.format("({0} hours)",
              twoPlacesFormat.format(seconds / 3600.0));
        } else if (seconds < 31556926) {
          timeStr = MessageFormat.format("({0} days)",
              twoPlacesFormat.format(seconds / 86400.0));
        } else {
          timeStr = MessageFormat.format("({0} years)",
              twoPlacesFormat.format(seconds / 31556926.0));
        }
        fConvertedTimeLimit.setText(timeStr);
      } catch (NumberFormatException e) {
      } finally {
        fConvertedTimeLimit.setText(""); //$NON-NLS-1$
      }
    }

  };

  private static class TestKindsOption extends ComboOption {

    public TestKindsOption() {
    }

    public TestKindsOption(Combo testKinds) {
      super(testKinds);
    }

    @Override
    protected IStatus validate(String testKindArgument) {
      try {
        TestKinds.valueOf(testKindArgument);
        return PluginStatus.OK_STATUS;
      } catch (IllegalArgumentException e) {
        return PluginStatus.createUIStatus(IStatus.ERROR, "Test Kinds must be of type All, Pass, or Fail.");
      }
    }

    @Override
    protected String getValue() {
      return TestKinds.getTestKind(fCombo.getSelectionIndex()).getArgumentName();
    }

    @Override
    protected String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_TEST_KINDS;
    }

    @Override
    protected String getDefaltValue() {
      return ILaunchConfigurationConstants.DEFAULT_TEST_KINDS;
    }

    @Override
    protected int getDefaultIndex() {
      return TestKinds.valueOf(getDefaltValue()).ordinal();
    }

  }

  private static class MaximumTestsWrittenOption extends PositiveIntegerOption {

    public MaximumTestsWrittenOption(Text text) {
      super(text);
    }

    @Override
    protected String getName() {
      return "Maximum Tests Written";
    }

    @Override
    public String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_WRITTEN;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_WRITTEN;
    }

  }

  private static class MaximumTestsPerFileOption extends PositiveIntegerOption {
    
    public MaximumTestsPerFileOption() {
    }
    
    public MaximumTestsPerFileOption(Text text) {
      super(text);
    }

    @Override
    protected String getName() {
      return "Maximum Tests Per File";
    }

    @Override
    public String getAttributeName() {
      return ILaunchConfigurationConstants.ATTR_MAXIMUM_TESTS_PER_FILE;
    }

    @Override
    protected String getDefaultValue() {
      return ILaunchConfigurationConstants.DEFAULT_MAXIMUM_TESTS_PER_FILE;
    }

  }
  
}
