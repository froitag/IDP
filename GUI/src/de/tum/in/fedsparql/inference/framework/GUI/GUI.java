package de.tum.in.fedsparql.inference.framework.GUI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import de.tum.in.fedsparql.inference.io.Database;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JLayeredPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.CardLayout;
import java.util.ArrayList;

import javax.swing.JList;

public class GUI {

	private JFrame frame;
	private JLayeredPane layeredPane;
	private DefaultListModel<String> dlm = new DefaultListModel<String>();
	private JList<String> list;
	private ArrayList<ScriptForm> scriptFormList = new ArrayList<ScriptForm>();
	private static ArrayList<Database> databaseList = new ArrayList<Database>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		databaseList.add(new TestDatabase("schwul"));
		databaseList.add(new TestDatabase("schwuler"));
		databaseList.add(new TestDatabase("domi"));
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 696, 438);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("fill:default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,}));
		
		JButton btnNewScript = new JButton("New script");
		btnNewScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ScriptForm form = new ScriptForm(GUI.this);
				scriptFormList.add(form);
				refreshList();
				list.setSelectedIndex(dlm.size() - 1);
			}
		});
		frame.getContentPane().add(btnNewScript, "2, 2");
		
		JButton btnDeleteScript = new JButton("Delete script");
		btnDeleteScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int selected = list.getSelectedIndex();
				if (selected != -1) {
					ScriptForm scriptForm = scriptFormList.remove(selected);
					refreshList();
					layeredPane.remove(scriptForm);
					layeredPane.validate();
				}
			}
		});
		frame.getContentPane().add(btnDeleteScript, "4, 2");
		
		final JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		frame.getContentPane().add(layeredPane, "6, 2, 1, 5, fill, fill");
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, "2, 4, 3, 1, fill, fill");
		
		final JList<String> list = new JList<String>(dlm);
		scrollPane.setViewportView(list);
		this.layeredPane = layeredPane;
		this.list = list;
		layeredPane.setLayout(new CardLayout(0, 0));
		
		JButton btnShowGraph = new JButton("Show Graph");
		btnShowGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				list.clearSelection();
				GraphView graph = new GraphView(GUI.this);
				layeredPane.removeAll();
				layeredPane.add(graph);
				layeredPane.validate();
			}
		});
		frame.getContentPane().add(btnShowGraph, "2, 6");
		
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				int selected = list.getSelectedIndex();
				if (selected != -1) {
					layeredPane.removeAll();
					layeredPane.add(scriptFormList.get(selected));
					layeredPane.validate();
				}
			}
		});
	}
	
	void refreshList() {
		int selected = list.getSelectedIndex();
		dlm.clear();
		for (ScriptForm form : scriptFormList) {
			dlm.addElement(form.getTextField());
		}
		list.setSelectedIndex(selected);
	}

	public JLayeredPane getLayeredPane() {
		return layeredPane;
	}
	
	public JList<String> getList() {
		return list;
	}
	
	public ArrayList<ScriptForm> getScriptFormList() {
		return scriptFormList;
	}
	
	ArrayList<Database> getDatabaseList() {
		return databaseList;
	}
	
}
