package de.tum.in.fedsparql.inference.framework.GUI;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Image;
import java.awt.Toolkit;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import de.tum.in.fedsparql.inference.framework.ExecutionPlan.ExecutionPlan;
import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;

import javax.swing.JLabel;

@SuppressWarnings("serial")
public class PlanView extends JPanel {

	ExecutionPlan plan;
	
	/**
	 * Create the panel.
	 */
	public PlanView(GUI gui) {
		
		try {
			plan = new ExecutionPlan(gui.getScriptCollection());
		} catch (CircularDependencyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		Image pic = Toolkit.getDefaultToolkit().createImage(plan.generatePNG());
		JLabel lblNewLabel = new JLabel(new ImageIcon(pic));
		add(lblNewLabel, "2, 2");

	}

}
