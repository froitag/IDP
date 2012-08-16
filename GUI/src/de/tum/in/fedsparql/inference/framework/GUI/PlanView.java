package de.tum.in.fedsparql.inference.framework.GUI;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.tum.in.fedsparql.inference.dummy.DummyDispatcher;
import de.tum.in.fedsparql.inference.dummy.DummyMonitoring;
import de.tum.in.fedsparql.inference.framework.plan.ExecutionPlan;
import de.tum.in.fedsparql.inference.framework.plandispatcher.DBPriorityScheduler;
import de.tum.in.fedsparql.inference.framework.plandispatcher.Scheduler;
import de.tum.in.fedsparql.inference.framework.plandispatcher.SimpleScheduler;
import de.tum.in.fedsparql.inference.framework.plandispatcher.SimpleWindowedScheduler;
import de.tum.in.fedsparql.inference.framework.xceptions.ExecutionPlanException;

@SuppressWarnings("serial")
public class PlanView extends JPanel {

	ExecutionPlan plan;
	Thread updateThread;

	/**
	 * Create the panel.
	 */
	public PlanView(GUI gui) {

		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));

		try {
			plan = new ExecutionPlan(gui.getDependencyGraph());

			// add dispatcher-dropdown
			final JComboBox<String> comboBox = new JComboBox<String>();
			comboBox.addItem("SimpleScheduler");
			comboBox.addItem("SimpleWindowScheduler");
			comboBox.addItem("DBPriorityDispatcher");
			add(comboBox, "3, 2, fill, default");

			// add execute button
			final JButton btnExecute = new JButton("execute");
			btnExecute.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					btnExecute.setEnabled(false);
					comboBox.setEnabled(false);
					(new Thread() {
						@Override
						public void run() {
							// update image periodically
							updateThread = new Thread() {
								@Override
								public void run() {

									// update ExecutionPlan-Image every 1000ms
									while (!Thread.interrupted() && !plan.isFinished()) {
										try {
											SwingUtilities.invokeAndWait(new ImageUpdater());
											Thread.sleep(500);
										} catch (InterruptedException e) {
											e.printStackTrace();
											this.interrupt();
										} catch (InvocationTargetException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}

									try {
										SwingUtilities.invokeAndWait(new ImageUpdater());
									} catch (InvocationTargetException e) {
										e.printStackTrace();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}

								}
							};
							updateThread.start();

							// run plan
							runPlan((String)comboBox.getSelectedItem());
						}
					}).start();
				}
			});
			add(btnExecute, "2, 2");


			new ImageUpdater().run();

		} catch (ExecutionPlanException e) {
			e.printStackTrace();
		}

	}

	public void dispose() {
		updateThread.interrupt();
	}


	// Image-Update Funktion ist eine Klasse damit wir die mit SwingUtilities.invokeAndWait() aus nem andern Thread aufrufen können
	int i=0;
	protected class ImageUpdater implements Runnable {
		@Override
		public void run() {
			try {
				plan.generatePNG().save(new File("plan"+(i++)+".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			Image pic = Toolkit.getDefaultToolkit().createImage(plan.generatePNG().getBytes());
			JLabel lblNewLabel = new JLabel(new ImageIcon(pic));
			if (PlanView.this.getComponentAt(3, 4) != null) {
				PlanView.this.remove(PlanView.this.getComponentAt(3, 4));
			}
			PlanView.this.add(lblNewLabel, "3, 4");
			PlanView.this.validate();
		}
	}

	protected void runPlan(String dispatcherString) {
		// execute execution plan
		Scheduler dispatcher=null;

		if ("DBPriorityDispatcher".equals(dispatcherString)) {
			dispatcher = new DBPriorityScheduler(plan.getDependencyGraph(), GUI.getIO(), new DummyMonitoring(), new DummyDispatcher());
		} else if ("SimpleWindowScheduler".equals(dispatcherString)) {
			dispatcher = new SimpleWindowedScheduler(plan.getDependencyGraph(), GUI.getIO(), new DummyMonitoring(), new DummyDispatcher());
		} else {
			dispatcher = new SimpleScheduler(plan.getDependencyGraph(), GUI.getIO(), new DummyMonitoring(), new DummyDispatcher());
		}

		try {
			plan.execute(dispatcher);
		} catch (Exception e) {
			System.err.println("Execution failed: ");
			e.printStackTrace();
		}
	}

}
