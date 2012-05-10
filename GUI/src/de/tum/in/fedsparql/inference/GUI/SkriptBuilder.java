package de.tum.in.fedsparql.inference.GUI;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SkriptBuilder {

	protected Shell shell;
	private Text text;
	private Text text_1;
	private String save_path = "./script/";

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
		shell.setSize(400, 300);
		shell.setText("SWT Application");
		
		final Button btnCheckButton = new Button(shell, SWT.CHECK);
		btnCheckButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnCheckButton.setBounds(10, 205, 93, 16);
		btnCheckButton.setText("Save");
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnCheckButton.getSelection()) {
					//...
					File file = new File(save_path + text.getText() + ".js");
					try {
						file.createNewFile();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				secondsite.addScript(text.getText());
				shell.close();
			}
		});
		btnNewButton.setBounds(10, 227, 75, 25);
		btnNewButton.setText("OK");
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		btnNewButton_1.setBounds(299, 227, 75, 25);
		btnNewButton_1.setText("Cancel");
		
		Label label = new Label(shell, SWT.NONE);
		label.setText("Name:");
		label.setBounds(10, 10, 55, 15);
		
		Label lblSkript = new Label(shell, SWT.NONE);
		lblSkript.setText("Script Text:");
		lblSkript.setBounds(10, 37, 75, 15);
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(71, 10, 237, 21);
		
		text_1 = new Text(shell, SWT.BORDER | SWT.MULTI);
		text_1.setBounds(10, 58, 364, 141);

	}
}
