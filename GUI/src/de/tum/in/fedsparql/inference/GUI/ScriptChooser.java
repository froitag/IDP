package de.tum.in.fedsparql.inference.GUI;

import java.io.File;

import javax.swing.JFileChooser;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ScriptChooser {

	protected Shell shell;
	private Text text;
	private Text text_1;

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @wbp.parser.entryPoint
	 */
	protected void createContents() {
		shell = new Shell(SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.RESIZE);
		shell.setSize(400, 301);
		shell.setText("SWT Application");
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("Name:");
		label.setBounds(10, 10, 55, 15);
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(71, 10, 237, 21);
		
		Label label_1 = new Label(shell, SWT.NONE);
		label_1.setText("Path:");
		label_1.setBounds(10, 37, 55, 15);
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(71, 34, 237, 21);
		
		Button button = new Button(shell, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				JFileChooser fileChooser = new JFileChooser(".");
			    int result = fileChooser.showOpenDialog(fileChooser);
			    if (result == JFileChooser.APPROVE_OPTION) {
			    	File file = fileChooser.getSelectedFile();
			    	text_1.setText(file.getAbsolutePath());
			    }
				
			}
		});
		button.setText("Select");
		button.setBounds(314, 32, 60, 25);
		
		Button button_1 = new Button(shell, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				secondsite.addScript(text.getText());
				shell.close();
			}
		});
		button_1.setText("OK");
		button_1.setBounds(10, 229, 75, 25);
		
		Button button_2 = new Button(shell, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		button_2.setText("Cancel");
		button_2.setBounds(299, 229, 75, 25);

	}

}
