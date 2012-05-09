package de.tum.in.fedsparql.inference.GUI;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class secondsite extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public secondsite(Composite parent, int style) {
		super(parent, style);
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(10, 10, 250, 200);
		
		Button btnAddSomething = new Button(this, SWT.NONE);
		btnAddSomething.setText("Add something");
		btnAddSomething.setBounds(266, 10, 174, 25);
		
		Button btnRemoveSomething = new Button(this, SWT.NONE);
		btnRemoveSomething.setText("Remove something");
		btnRemoveSomething.setBounds(266, 41, 174, 25);
		
		Button button_2 = new Button(this, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NEWGUI.goToThirdSite();
			}
		});
		button_2.setText("OK");
		button_2.setBounds(10, 265, 75, 25);
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NEWGUI.goBackToFirstSite();
			}
		});
		btnNewButton.setBounds(365, 265, 75, 25);
		btnNewButton.setText("Back");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
