package de.tum.in.fedsparql.inference.GUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.tum.in.fedsparql.rts.executor.FSException;
import de.tum.in.fedsparql.rts.executor.FSResultSet;

public class Preview {

	protected Shell shell;
	private Text text;
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
		shell = new Shell();
		shell.setSize(700, 450);
		shell.setText("SWT Application");

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setBounds(10, 10, 55, 15);
		lblNewLabel.setText("Query:");

		text = new Text(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		text.setBounds(10, 31, 664, 56);
		text.setText("SELECT ?s ?p ?o WHERE {?s ?p ?o.}");

		list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		list.setBounds(10, 124, 664, 247);

		Preview.this.updatePreview();

		Button btnNewButton = new Button(shell, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				list.removeAll();
				Preview.this.updatePreview();
			}
		});
		btnNewButton.setBounds(10, 93, 75, 25);
		btnNewButton.setText("Refresh");

		Button btnBack = new Button(shell, SWT.NONE);
		btnBack.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.close();
			}
		});
		btnBack.setBounds(599, 377, 75, 25);
		btnBack.setText("Cancel");

	}

	private void updatePreview() {
		FSResultSet rs;
		try {
			rs = NEWGUI.inferenceIO.execute(text.getText());
			//System.out.println(Arrays.toString(rs.getHeader()));
			while (rs.hasNext()){
				String[] tuple = rs.next();

				String triple="";
				for (int i=0; i<tuple.length; i++) {
					triple += tuple[i] + " | ";
				}
				triple = triple.substring(0,triple.length()-2);
				list.add(triple);
			}
			rs.close();
		} catch (FSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}