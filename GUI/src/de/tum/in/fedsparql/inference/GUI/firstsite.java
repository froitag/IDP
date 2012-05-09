package de.tum.in.fedsparql.inference.GUI;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class firstsite extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public firstsite(Composite parent, int style) {
		super(parent, style);
		setLayout(null);
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(10, 10, 250, 200);
		
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.setBounds(10, 265, 75, 25);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NEWGUI.goToSecondSite();
			}
		});
		btnNewButton.setText("OK");
		
		Button btnNewButton_1 = new Button(this, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Filechooser window = new Filechooser();
					window.open();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(266, 10, 174, 25);
		btnNewButton_1.setText("Add database");
		
		Button btnNewButton_2 = new Button(this, SWT.NONE);
		btnNewButton_2.setBounds(266, 41, 174, 25);
		btnNewButton_2.setText("Remove database");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
