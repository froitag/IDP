package de.tum.in.fedsparql.inference.GUI;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class thirdsite extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public thirdsite(Composite parent, int style) {
		super(parent, style);
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(10, 10, 250, 200);
		
		Button button = new Button(this, SWT.NONE);
		button.setText("OK");
		button.setBounds(10, 265, 75, 25);
		
		Button btnAddSomethingelse = new Button(this, SWT.NONE);
		btnAddSomethingelse.setText("Add something else");
		btnAddSomethingelse.setBounds(266, 10, 174, 25);
		
		Button btnRemoveSomethingElse = new Button(this, SWT.NONE);
		btnRemoveSomethingElse.setText("Remove something else");
		btnRemoveSomethingElse.setBounds(266, 41, 174, 25);
		
		Button button_3 = new Button(this, SWT.NONE);
		button_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NEWGUI.goBackToSecondSite();
			}
		});
		button_3.setText("Back");
		button_3.setBounds(365, 265, 75, 25);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
