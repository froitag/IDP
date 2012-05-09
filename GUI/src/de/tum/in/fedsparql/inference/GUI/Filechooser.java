package de.tum.in.fedsparql.inference.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import swing2swt.layout.BorderLayout;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.beans.PojoObservables;

public class Filechooser {
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	private Text text;
	private Text text_1;
	private Text text_2;
	private String text1Content="test";

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
		shell.setSize(363, 300);
		shell.setText("SWT Application");
		shell.setLayout(null);
		
		Label lblName = new Label(shell, SWT.NONE);
		lblName.setBounds(10, 10, 55, 15);
		lblName.setText("Name:");
		
		Label lblPath = new Label(shell, SWT.NONE);
		lblPath.setBounds(10, 37, 55, 15);
		lblPath.setText("Path:");
		
		Label lblPreview = new Label(shell, SWT.NONE);
		lblPreview.setBounds(10, 81, 55, 15);
		lblPreview.setText("Preview:");
		
		text = new Text(shell, SWT.BORDER);
		text.setBounds(71, 10, 200, 21);
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(71, 34, 200, 21);
		
		text_2 = new Text(shell, SWT.BORDER);
		text_2.setBounds(71, 78, 266, 174);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		final JFrame frame = new JFrame("JFileChooser Popup");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    JFileChooser fileChooser = new JFileChooser(".");
	    frame.add(fileChooser, BorderLayout.CENTER);

	    ActionListener actionListener = new ActionListener() {
	      public void actionPerformed(ActionEvent actionEvent) {
	        JFileChooser theFileChooser = (JFileChooser) actionEvent.getSource();
	        String command = actionEvent.getActionCommand();
	        if (command.equals(JFileChooser.APPROVE_SELECTION)) {
	          File selectedFile = theFileChooser.getSelectedFile();
	          text1Content = selectedFile.getAbsolutePath();
	        } else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
	          System.out.println(JFileChooser.CANCEL_SELECTION);
	        }
	      }
	    };
	    fileChooser.addActionListener(actionListener);
	    frame.pack();
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			    frame.setVisible(true);
			}
		});
		btnNewButton.setBounds(277, 32, 60, 25);
		btnNewButton.setText("Select");
		m_bindingContext = initDataBindings();

	}
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue text_1ObserveTextObserveWidget_1 = SWTObservables.observeText(text_1, SWT.Modify);
		IObservableValue text1ContentEmptyObserveValue = PojoObservables.observeValue(text1Content, "empty");
		bindingContext.bindValue(text_1ObserveTextObserveWidget_1, text1ContentEmptyObserveValue, null, null);
		//
		return bindingContext;
	}
}
