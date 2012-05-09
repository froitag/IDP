package de.tum.in.fedsparql.inference.GUI;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class startsite extends Composite {
	private Text text;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public startsite(Composite parent, int style) {
		super(parent, style);
		
		text = new Text(this, SWT.BORDER);
		text.setBounds(10, 10, 215, 149);
		
		Button button = new Button(this, SWT.NONE);
		button.setText("Weiter");
		button.setBounds(10, 165, 75, 25);
		
		Button button_1 = new Button(this, SWT.NONE);
		button_1.setText("Zur\u00FCck");
		button_1.setBounds(315, 165, 75, 25);

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
