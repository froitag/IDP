package de.tum.in.fedsparql.inference.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

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
import org.eclipse.swt.widgets.List;

import de.tum.in.fedsparql.inference.dummy.JenaDatabase;
import de.tum.in.fedsparql.inference.dummy.JenaIO;
import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

public class Filechooser {
	private DataBindingContext m_bindingContext;

	protected Shell shell;
	private Text text;
	private Text text_1;
	private String text1Content="test";
	private List list;

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
		shell.setSize(400, 302);
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
		text.setBounds(71, 10, 237, 21);
		
		text_1 = new Text(shell, SWT.BORDER);
		text_1.setBounds(71, 34, 237, 21);
		
		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			    try {
			    	JFileChooser fileChooser = new JFileChooser(".");
				    int result = fileChooser.showOpenDialog(fileChooser);
				    String path = "";
				    if (result == JFileChooser.APPROVE_OPTION) {
				    	File file = fileChooser.getSelectedFile();
				    	path = file.getAbsolutePath();
				    	text_1.setText(path);
				    }
				    JenaIO io = new JenaIO();
					Database db = new JenaDatabase("", path);
					FSResultSet rs;
					try {
						rs = io.execute("SELECT ?s ?p ?o WHERE {?s ?p ?o.}", db);
						//System.out.println(Arrays.toString(rs.getHeader()));
						while (rs.hasNext()){
							String[] tuple = rs.next();
							
							String triple = tuple[rs.column("s")]+" | "+ tuple[rs.column("p")]+" | "+ tuple[rs.column("o")];
							list.add(triple);
						}
						rs.close();
					} catch (FSException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			    
			}
		});
		btnNewButton.setBounds(314, 32, 60, 25);
		btnNewButton.setText("Select");
		
		list = new List(shell, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		list.setBounds(10, 102, 364, 117);
		
		Button btnNewButton_1 = new Button(shell, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//...
				firstsite.addDatabase(text.getText(), text_1.getText());
				shell.close();
			}
		});
		btnNewButton_1.setBounds(10, 229, 75, 25);
		btnNewButton_1.setText("OK");
		
		Button btnNewButton_2 = new Button(shell, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		btnNewButton_2.setBounds(299, 229, 75, 25);
		btnNewButton_2.setText("Cancel");

	}

}
