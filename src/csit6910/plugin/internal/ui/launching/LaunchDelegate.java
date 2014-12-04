package csit6910.plugin.internal.ui.launching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import csit6910.plugin.Activator;
import csit6910.plugin.internal.core.PluginStatus;
import csit6910.plugin.internal.core.launching.ArgumentCollector;
import csit6910.plugin.internal.core.launching.LaunchResources;
import csit6910.plugin.internal.core.runtime.TestGeneratorSession;
import csit6910.plugin.internal.core.runtime.MessageSessionListener;

import randoop.plugin.internal.IConstants;
import randoop.plugin.internal.core.MutableBoolean;
import randoop.plugin.internal.core.runtime.IMessageListener;
import randoop.plugin.internal.core.runtime.MessageReceiver;
import randoop.plugin.internal.ui.MessageUtil;
import randoop.plugin.internal.ui.ResourcesListQuestionDialogWithToggle;

public class LaunchDelegate extends AbstractJavaLaunchConfigurationDelegate {
  MessageReceiver fMessageReceiver;
  int fPort;

  public LaunchDelegate() {
    super();
    fMessageReceiver = null;
  }
  
  private IProject[] computeReferencedProjectOrder(IJavaProject javaProject) throws JavaModelException {
    List<IProject> buildOrder = new ArrayList<IProject>();
    buildOrder.add(javaProject.getProject());
    
    for (IClasspathEntry ce : javaProject.getRawClasspath()) {
      if (ce.getEntryKind() == IClasspathEntry.CPE_VARIABLE) {
        ce = JavaCore.getResolvedClasspathEntry(ce);
      }
      
      if (ce != null && ce.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
        buildOrder.add(getWorkspaceRoot().getProject(ce.getPath().toString()));
      }
    }
    
    return buildOrder.toArray(new IProject[buildOrder.size()]);
  }
  
  @Override
  protected IProject[] getBuildOrder(ILaunchConfiguration configuration, String mode) throws CoreException {
    
    ArgumentCollector args = new ArgumentCollector(configuration, getWorkspaceRoot());
    
    IJavaProject javaProject = args.getJavaProject();
    return computeReferencedProjectOrder(javaProject);
  }
    
  @Override
  protected IProject[] getProjectsForProblemSearch(ILaunchConfiguration configuration, String mode) throws CoreException {

    IWorkspaceRoot root = getWorkspaceRoot();
    ArgumentCollector args = new ArgumentCollector(configuration, root);
    
    IJavaProject javaProject = args.getJavaProject();
    return computeReferencedProjectOrder(javaProject);
  }
  
  public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
    System.out.println("Begin launch"); //$NON-NLS-1$
    
    if (monitor == null)
      monitor = new NullProgressMonitor();

    // check for cancellation
    if (monitor.isCanceled())
      return;

    ArgumentCollector args = new ArgumentCollector(configuration, getWorkspaceRoot());
    
    IStatus status = args.getWarnings();
    if (status.getSeverity() == IStatus.ERROR) {
      informAndAbort(status);
    } else if (status.getSeverity() == IStatus.WARNING) {
      String qst = NLS.bind("{0}{1}", status.getMessage(),
          "\n\nProceed with test generations?");
      if (!MessageUtil.openQuestion(qst)) {
        return;
      }
    }
    
    LaunchResources launchResources = new LaunchResources(args, monitor);
    final TestGeneratorSession session = new TestGeneratorSession(launch, args);

    fPort = ArgumentCollector.getPort(configuration);

    boolean useDefault = (fPort == IConstants.INVALID_PORT);
    
    fMessageReceiver = null;
    if (useDefault) {
      try {
        IMessageListener listener = new MessageSessionListener(session);
        fMessageReceiver = new MessageReceiver(listener);
        fPort = fMessageReceiver.getPort();
      } catch (IOException e) {
        fMessageReceiver = null;
        
        IStatus s = PluginStatus.COMM_NO_FREE_PORT.getStatus();
        throw new CoreException(s);
      }
    }

    String mainTypeName = verifyMainTypeName(configuration);
    IVMRunner runner = getVMRunner(configuration, mode);

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    File workingDir = root.getLocation().toFile();
    String workingDirName = workingDir.getAbsolutePath();

    // Search for similarly named files in the output directory and warn the user if any are found.
    final IResource[] resourcesInQuestion = launchResources.getThreatendedResources();

    // Check if the output directory exists
    if (resourcesInQuestion.length > 0) {
      final String message = "The following files were found in the output directory and may be overwritten by the generated tests.";
      final String yesNoQuestion = "Proceed with test generation?";
      final String toggleQuestion = "Delete these files before launch";
      
      final MutableBoolean okToProceed = new MutableBoolean(false);
      final MutableBoolean deleteFiles = new MutableBoolean(false);
      
      PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

        public void run() {
          MessageDialogWithToggle d = new ResourcesListQuestionDialogWithToggle(PlatformUI
              .getWorkbench().getDisplay().getActiveShell(), "Randoop", message, yesNoQuestion,
              toggleQuestion, resourcesInQuestion);
          
          okToProceed.setValue(d.open() == Dialog.OK);
          deleteFiles.setValue(d.getToggleState());
        }
      });
      
      if (!okToProceed.getValue()) {
        return;
      }
      
      if (deleteFiles.getValue()) {
        for (IResource r : resourcesInQuestion) {
          monitor.beginTask("Deleting files", 0);
          r.delete(true, new SubProgressMonitor(monitor, 0));
        }
      }
    }
    

    // Set the shared instance of the session to the session about to run
    TestGeneratorSession.setActiveSession(session);
    
    // Open the randoop view
    // TODO: Future revisions should not need to set the session this way
    // and setActiveTestRunSession should be private
//    final TestGeneratorViewPart viewPart = TestGeneratorViewPart.openInstance();
//    viewPart.getSite().getShell().getDisplay().syncExec(new Runnable() {
//
//      public void run() {
////        viewPart.setActiveTestRunSession(session);
//      }
//    });

    ArrayList<String> vmArguments = new ArrayList<String>();
    ArrayList<String> programArguments = new ArrayList<String>();
    collectProgramArguments(launchResources, programArguments);
    collectExecutionArguments(configuration, vmArguments, programArguments);
    

    // Classpath
    List<String> cpList = new ArrayList(Arrays.asList(JavaRuntime.computeDefaultRuntimeClassPath(args.getJavaProject())));

    for (IPath path : Activator.getJanalaClasspaths()) {
      cpList.add(path.toOSString());
    }
    
    String[] classpath = cpList.toArray(new String[0]);
    
    // TODO: Sometimes the classpaths do not seem to work...
    for(String str : classpath) {
      System.out.println(str);
    }
    
    // Create VM config
    VMRunnerConfiguration runConfig = new VMRunnerConfiguration(mainTypeName, classpath);

    runConfig.setVMArguments((String[]) vmArguments.toArray(new String[vmArguments.size()]));
    runConfig.setProgramArguments((String[]) programArguments.toArray(new String[programArguments.size()]));
    runConfig.setEnvironment(getEnvironment(configuration));
    runConfig.setVMSpecificAttributesMap(getVMSpecificAttributesMap(configuration));
    runConfig.setBootClassPath(getBootpath(configuration));

    String path = workingDirName;
    try {
      File specifiedWorkingDir = getWorkingDirectory(configuration);
      if (specifiedWorkingDir != null) {
        path = specifiedWorkingDir.getAbsolutePath();
      }
    } catch (CoreException ce) {
    }
    runConfig.setWorkingDirectory(path);

    // check for cancellation
    if (monitor.isCanceled()) {
      return;
    }

    // done the verification phase
    monitor.worked(1);
    for (String str : programArguments) {
      System.out.println(str);
    }
    
    if (useDefault) {
      new Thread(fMessageReceiver).start();
    }
    
    // csit6910: implement janala launcher in here
    // this execution based on original python script
    List<String> classNames = new ArrayList<String>();
    boolean isOffline = launchResources.getArguments().getJanalaOfflineMode();
    int maxIterations = launchResources.getArguments().getJanalaMaxIterations();

    for (IType type : launchResources.getArguments().getSelectedTypes()) {
    	classNames.add(type.getFullyQualifiedName());
    }

    String projectPath = path + args.getJavaProject().getPath().toOSString();
    File temp = new File(projectPath, "catg_tmp");
    temp.mkdir();
    String wDir = temp.getAbsolutePath();
    File testlog = new File(temp, "testlog.txt");
    File fInputs = new File(temp, "inputs");
    File fHistory = new File(temp, "history");
    
    for (String classname : classNames) {
    	mainTypeName = classname;
  		File[] files = temp.listFiles();

    	for (File f: files) {
    		System.out.println("delete: "+ f.getAbsolutePath());
    		f.delete();
    	}

    	for (int i = 1; i <= maxIterations; ++i) {
      	vmArguments = new ArrayList<String>();
      	setJanalaArguments(vmArguments, mainTypeName, false);

//      	System.out.println("--------------------------DEBUG VM ARGS");
//        for (String s: vmArguments) {
//        	System.out.println(s);
//        }
      	runConfig = new VMRunnerConfiguration(mainTypeName, classpath);
        runConfig.setVMArguments((String[]) vmArguments.toArray(new String[vmArguments.size()]));
        runConfig.setProgramArguments((String[]) programArguments.toArray(new String[programArguments.size()]));
        runConfig.setEnvironment(getEnvironment(configuration));
        runConfig.setVMSpecificAttributesMap(getVMSpecificAttributesMap(configuration));
        runConfig.setBootClassPath(getBootpath(configuration));
        runConfig.setWorkingDirectory(wDir);
      	runConfig.setVMArguments((String[]) vmArguments.toArray(new String[vmArguments.size()]));

      	runner.run(runConfig, launch, monitor);
      	while (!launch.isTerminated()) {
      		try {
      			Thread.sleep(200);
      		} catch(InterruptedException ie) {
      			ie.printStackTrace();
      		}
      	}

    		if (isOffline) {
    			vmArguments = new ArrayList<String>();
    			vmArguments.add("-Djanala.iteration=" + i);//$NON-NLS-1$
    			setJanalaArguments(vmArguments, mainTypeName, true);
        	
//        	System.out.println("--------------------------DEBUG VM ARGS: OFFLINE");
//          for (String s: vmArguments) {
//          	System.out.println(s);
//          }
          runConfig = new VMRunnerConfiguration(mainTypeName, classpath);
          runConfig.setVMArguments((String[]) vmArguments.toArray(new String[vmArguments.size()]));
          runConfig.setProgramArguments((String[]) programArguments.toArray(new String[programArguments.size()]));
          runConfig.setEnvironment(getEnvironment(configuration));
          runConfig.setVMSpecificAttributesMap(getVMSpecificAttributesMap(configuration));
          runConfig.setBootClassPath(getBootpath(configuration));
          runConfig.setWorkingDirectory(wDir);
        	runConfig.setVMArguments((String[]) vmArguments.toArray(new String[vmArguments.size()]));

        	runner.run(runConfig, launch, monitor);
        	while (!launch.isTerminated()) {
        		try {
        			Thread.sleep(200);
        		} catch(InterruptedException ie) {
        			ie.printStackTrace();
        		}
        	}
    		}
    		
    		if (fInputs.exists()) {
      		copyFile(fInputs, new File(temp, "inputs" + i));
      		copyFile(fInputs, new File(temp, "inputs.old"));
    		}
    		
    		if (fHistory.exists()) {
    			copyFile(fHistory, new File(temp, "history.old"));
    		}

    		if ((new File("history")).exists() || (new File("backtrackFlag")).exists()) {
    			// nothing
    		}
    		else if (i == maxIterations) {
    			appendTestLog(testlog, mainTypeName + " ("+ maxIterations +") passed");
    		}
    		else {
//    			appendTestLog(testlog, "****************** " + mainTypeName + " ("+ maxIterations +") failed!!!");
//    			break;
    		}
      }
    }

    // check for cancellation
    if (monitor.isCanceled()) {
      return;
    }

    System.out.println("Launching complete");
  }

  /**
   * Collects all VM and program arguments. Implementors can modify and add
   * arguments.
   * 
   * @param configuration
   *          the configuration to collect the arguments for
   * @param vmArguments
   *          a {@link List} of {@link String} representing the resulting VM
   *          arguments
   * @param programArguments
   *          a {@link List} of {@link String} representing the resulting
   *          program arguments
   * @exception CoreException
   *              if unable to collect the execution arguments
   */
  protected void collectExecutionArguments(ILaunchConfiguration configuration,
      List<String> vmArguments, List<String> programArguments)
      throws CoreException {
    // add program & VM arguments provided by getProgramArguments and
    // getVMArguments
    String pgmArgs = getProgramArguments(configuration);
    String vmArgs = getVMArguments(configuration);
    ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);
    vmArguments.addAll(Arrays.asList(execArgs.getVMArgumentsArray()));
    vmArguments.add("-ea"); //$NON-NLS-1$
    programArguments.addAll(Arrays.asList(execArgs.getProgramArgumentsArray()));
  }

  protected void collectProgramArguments(LaunchResources launchResources,
      List<String> programArguments) {
    ArgumentCollector args = launchResources.getArguments();
    programArguments.add("gentests"); //$NON-NLS-1$

    for (IType type : args.getSelectedTypes()) {
      programArguments.add("--testclass=" + type.getFullyQualifiedName()); //$NON-NLS-1$
    }

    // TODO - Fix ERROR: while parsing command-line arguments: unknown option '--comm-port=
    // TODO - Fix java.lang.Error: Unexpected type long when --timeout is uncommented
    // programArguments.add("--log=randooplog.txt"); // XXX remove
    
    // TODO: add program arguments in here when necessary
//    programArguments.add("--randomseed=" + args.getRandomSeed());//$NON-NLS-1$
//    programArguments.add("--maxsize=" + args.getMaxTestSize());//$NON-NLS-1$
  }

  private void informAndAbort(String message, Throwable exception, int code) throws CoreException {
    IStatus status = new Status(IStatus.INFO, Activator.getPluginId(), code, message, exception);
    informAndAbort(status);
  }

  private void informAndAbort(IStatus status) throws CoreException {
    if (showStatusMessage(status)) {
      // Status message successfully shown
      // -> Abort with INFO exception
      // -> Worker.run() will not write to log
      throw new CoreException(status);
    } else {
      // Status message could not be shown
      // -> Abort with original exception
      // -> Will write WARNINGs and ERRORs to log
      abort(status.getMessage(), status.getException(), status.getCode());
    }
  }

  private boolean showStatusMessage(final IStatus status) {
    final boolean[] success = new boolean[] { false };
    Activator.getDisplay().syncExec(new Runnable() {

      public void run() {
        Shell shell = Activator.getActiveWorkbenchShell();
        if (shell == null)
          shell = Activator.getDisplay().getActiveShell();
        if (shell != null) {
          MessageDialog.openInformation(shell, "Problems Launching Randoop", status.getMessage());
          success[0] = true;
        }
      }
    });
    return success[0];
  }

  @Override
  public String verifyMainTypeName(ILaunchConfiguration configuration) throws CoreException {
  	return "";
  	/** for Randoop experiment */
  	//return "randoop.main.Main"; //$NON-NLS-1$
  	/** for Palus experiment */
  	//return "palus.main.OfflineMain"; //$NON-NLS-1$
  }
  
  private static IWorkspaceRoot getWorkspaceRoot() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }
  
  /** csit6910: janala arguments */
  protected void setJanalaArguments(List<String> vmArguments, String classname, boolean isOffline) {
  	vmArguments.add("-Djanala.conf=" + Activator.getJanalaConfigPath());//$NON-NLS-1$
  	
  	// -Xmx is optional, but for some large model check, you may consider increase paging size
  	vmArguments.add("-Xmx1024m");//$NON-NLS-1$
  	
  	// -javaagent is required in both offline and on-the-fly process
  	vmArguments.add("-javaagent:" + Activator.getIAgentPath());//$NON-NLS-1$
  	
  	// -noverify, to relax restriction on Java bytecode instrumentation
   	// http://stackoverflow.com/questions/15122890/java-lang-verifyerror-expecting-a-stackmap-frame-at-branch-target-jdk-1-7
  	vmArguments.add("-noverify");//$NON-NLS-1$

  	if (!isOffline) {
    	vmArguments.add("-Djanala.loggerClass=janala.logger.DirectConcolicExecution");//$NON-NLS-1$
    	vmArguments.add("-ea:" + classname);//$NON-NLS-1$
  	} else {
  		vmArguments.add("-Djanala.mainClass=" + classname);//$NON-NLS-1$
    	vmArguments.add("-Djanala.loggerClass=janala.logger.FileLogger");//$NON-NLS-1$
  		vmArguments.add("-ea:janala.interpreters.LoadAndExecuteInstructions");//$NON-NLS-1$
  	}
  }

  /** csit6910: copy filestream */
  protected void copyFile(File source, File dest) {
  	java.nio.channels.FileChannel in = null;
		java.nio.channels.FileChannel out = null;
		try {
			dest.createNewFile();
			dest.setWritable(true);
			in = new java.io.FileInputStream(source).getChannel();
			out = new java.io.FileOutputStream(dest).getChannel();
			out.transferFrom(in, 0, in.size());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} 
		finally {
			try {
				if (in != null) in.close();
			} catch (IOException e) {}
			try {
				if(out != null) out.close();
			} catch (IOException e) {}
		}
  }

  /** csit6910: append test log */
  protected void appendTestLog(File file, String message) {
  	java.io.PrintWriter out = null;
  	try {
  		out = new java.io.PrintWriter(new java.io.BufferedWriter(new java.io.FileWriter(file, true)));
  		out.println(message);
  	} catch (IOException e) {
  		System.out.println(e.getMessage());
  	} finally {
  		if (out != null)
  			out.close();
  	}
  }

}
