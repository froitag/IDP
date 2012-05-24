package de.tum.in.fedsparql.inference.GUI;

import old.InferenceIO;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import swing2swt.layout.BorderLayout;
import de.tum.in.fedsparql.inference.dummy.JenaIO;

public class NEWGUI extends ApplicationWindow {
	private static Composite container;
	private static firstsite firstsite;
	private static secondsite secondsite;
	private static thirdsite thirdsite;
	private static StatusLineManager statusLineManager;
	private static String saved_statusBar;
	public static InferenceIO inferenceIO = new InferenceIO(new JenaIO());

	/**
	 * Create the application window.
	 */
	public NEWGUI() {
		super(null);
		createActions();
		//addToolBar(SWT.FLAT | SWT.WRAP);
		//addMenuBar();
		addStatusLine();
	}

	/**
	 * Create the status line manager.
	 * @return the status line manager
	 */
	@Override
	protected StatusLineManager createStatusLineManager() {
		statusLineManager = new StatusLineManager();
		return statusLineManager;
	}

	public static void goToSecondSite() {
		firstsite.setVisible(false);
		secondsite = new secondsite(container, SWT.NONE);
		secondsite.setForeground(SWTResourceManager.getColor(0, 0, 0));
		secondsite.setSize(container.getSize());
	}

	public static void goToThirdSite() {
		secondsite.setVisible(false);
		thirdsite = new thirdsite(container, SWT.NONE);
		thirdsite.setForeground(SWTResourceManager.getColor(0, 0, 0));
		thirdsite.setSize(container.getSize());
	}

	public static void goBackToFirstSite() {
		secondsite.setVisible(false);
		firstsite.setVisible(true);
	}

	public static void goBackToSecondSite() {
		thirdsite.setVisible(false);
		secondsite.setVisible(true);
	}

	public static void setStatusBar(String message) {
		saved_statusBar = message;
		statusLineManager.setMessage(message);
	}

	public static void temporaryStatusBar(String temp_string) {
		statusLineManager.setMessage(temp_string);
	}

	public static void undoStatusBar() {
		statusLineManager.setMessage(saved_statusBar);
	}

	/**
	 * Create contents of the application window.
	 * @param parent
	 */
	@Override
	protected Control createContents(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new BorderLayout(0, 0));

		firstsite = new firstsite(container, SWT.NONE);
		firstsite.setForeground(SWTResourceManager.getColor(0, 0, 0));

		Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(BorderLayout.SOUTH);

		//composite = new Composite(container, SWT.NONE);
		//composite.setForeground(SWTResourceManager.getColor(0, 0, 0));

		return container;
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}




	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			NEWGUI window = new NEWGUI();
			window.setBlockOnOpen(true);
			window.open();
			Display.getCurrent().dispose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Configure the shell.
	 * @param newShell
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("RDF Inferences");
	}

	/**
	 * Return the initial size of the window.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(466, 315);
	}
}