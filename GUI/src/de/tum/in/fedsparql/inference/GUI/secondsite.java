package de.tum.in.fedsparql.inference.GUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class secondsite extends Composite {

	private static List list;
	private String status_message = "Select your input scripts.";

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public secondsite(Composite parent, int style) {
		super(parent, style);

		NEWGUI.setStatusBar(status_message);

		Button btnAddSomething = new Button(this, SWT.NONE);
		btnAddSomething.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					SkriptBuilder window = new SkriptBuilder();
					window.open();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
		btnAddSomething.setText("New script");
		btnAddSomething.setBounds(266, 10, 174, 25);

		Button btnRemoveSomething = new Button(this, SWT.NONE);
		btnRemoveSomething.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try {
					ScriptChooser window = new ScriptChooser();
					window.open();
				} catch (Exception e2) {
					e2.printStackTrace();
				}

			}
		});
		btnRemoveSomething.setText("Load script");
		btnRemoveSomething.setBounds(266, 41, 174, 25);

		Button button_2 = new Button(this, SWT.NONE);
		button_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NEWGUI.goToThirdSite();
			}
		});
		button_2.setText("OK");
		button_2.setBounds(10, 216, 75, 25);

		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NEWGUI.goBackToFirstSite();
			}
		});
		btnNewButton.setBounds(365, 216, 75, 25);
		btnNewButton.setText("Back");

		list = new List(this, SWT.BORDER);
		list.setBounds(10, 10, 250, 200);

		Button btnNewButton_1 = new Button(this, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				list.remove(list.getSelectionIndex());
			}
		});
		btnNewButton_1.setBounds(266, 72, 174, 25);
		btnNewButton_1.setText("Remove script");

	}

	@Override
	public void setVisible(boolean bool) {
		super.setVisible(bool);
		if (true) {
			NEWGUI.setStatusBar(status_message);
		}
	}

	public static void addScript(String name) {
		list.add(name);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}