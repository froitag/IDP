package de.tum.in.fedsparql.inference.GUI;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import de.tum.in.fedsparql.inference.dummy.JenaDatabase;

public class firstsite extends Composite {

	private static List list;
	private String status_message = "Select your input databases.";
	private static HashMap<String, String> hashMap = new HashMap<String, String>();

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public firstsite(Composite parent, int style) {
		super(parent, style);
		//setLayout(null);

		NEWGUI.setStatusBar(status_message);

		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.setBounds(10, 216, 75, 25);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				for (String name : hashMap.keySet()) {
					String path = hashMap.get(name);
					try {
						NEWGUI.inferenceIO.addDatabase(new JenaDatabase(name, path));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
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
		btnNewButton_1.setBounds(266, 9, 174, 25);
		btnNewButton_1.setText("Add database");

		Button btnNewButton_2 = new Button(this, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeDatabase();
			}
		});
		btnNewButton_2.setBounds(266, 40, 174, 25);
		btnNewButton_2.setText("Remove database");

		list = new List(this, SWT.BORDER);
		list.setBounds(10, 10, 250, 200);

		Button btnNewButton_3 = new Button(this, SWT.NONE);
		btnNewButton_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					Preview window = new Preview();
					window.open();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_3.setBounds(91, 216, 75, 25);
		btnNewButton_3.setText("Preview");

	}

	@Override
	public void setVisible(boolean bool) {
		super.setVisible(bool);
		if (true) {
			NEWGUI.setStatusBar(status_message);
		}
	}

	public static void addDatabase(String name, String path) {
		if (!hashMap.containsKey(name)) {
			list.add(name);
		}
		hashMap.put(name, path);
	}

	public static void removeDatabase() {
		String[] array = list.getSelection();
		if (array.length > 0) {
			String item = array[0];
			hashMap.remove(item);
		}
		list.remove(list.getSelectionIndex());
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}