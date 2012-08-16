package de.tum.in.fedsparql.inference.framework.GUI;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.tum.in.fedsparql.inference.dummy.DummyDatabase;
import de.tum.in.fedsparql.inference.dummy.DummyIO;
import de.tum.in.fedsparql.inference.dummy.JenaDatabase;
import de.tum.in.fedsparql.inference.framework.graph.IntelligentDependencyGraph;
import de.tum.in.fedsparql.inference.io.Database;
import de.tum.in.fedsparql.inference.io.Node;

public class GUI {

	private JFrame frame;
	private JLayeredPane layeredPane;
	private DefaultListModel<String> dlm = new DefaultListModel<String>();
	private JList<String> list;
	private ArrayList<ScriptForm> scriptFormList = new ArrayList<ScriptForm>();
	private static ArrayList<Database> databaseList = new ArrayList<Database>();
	private static DummyIO io;
	private IntelligentDependencyGraph DependencyGraph;
	private HashSet<EdgeClass> deletedEdges = new HashSet<EdgeClass>();

	JButton btnShowGraph;
	JButton btnShowPlan;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// set up execution environment
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(new Node("Node1", "localhost", 2221));
		nodes.add(new Node("Node2", "localhost", 2222));
		nodes.add(new Node("Node3", "localhost", 2223));

		io = new DummyIO(nodes);
		try {
			io.register(io.getNodeByName("Node1"), new DummyDatabase("Test1", "database/test.nt"));
			io.register(io.getNodeByName("Node1"), new DummyDatabase("Test2", "database/test.nt"));
			io.register(io.getNodeByName("Node2"), new DummyDatabase("Test3", "database/test.nt"));
			io.register(io.getNodeByName("Node2"), new DummyDatabase("Test4", "database/test.nt"));
			io.register(io.getNodeByName("Node3"), new DummyDatabase("Test5", "database/test.nt"));
		} catch (FileNotFoundException e) {
			System.err.println("Couldn't create Databases");
			e.printStackTrace();
		}

		io.register(io.getNodeByName("Node1"), new JenaDatabase("a"));
		io.register(io.getNodeByName("Node1"), new JenaDatabase("b"));
		io.register(io.getNodeByName("Node2"), new JenaDatabase("c"));
		io.register(io.getNodeByName("Node2"), new JenaDatabase("d"));
		io.register(io.getNodeByName("Node3"), new JenaDatabase("e"));

		databaseList.addAll(io.getDatabases());
		/*		databaseList.add(new TestDatabase("a"));
		databaseList.add(new TestDatabase("b"));
		databaseList.add(new TestDatabase("c"));
		databaseList.add(new TestDatabase("d"));
		databaseList.add(new TestDatabase("e"));
		databaseList.add(new TestDatabase("f"));*/
		EventQueue.invokeLater(new Runnable() {
			@Override
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
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
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
			@Override
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
			@Override
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

		btnShowGraph = new JButton("Show Graph");
		btnShowGraph.setEnabled(false);
		btnShowGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				list.clearSelection();
				GraphView graph = new GraphView(GUI.this);
				layeredPane.removeAll();
				layeredPane.add(graph);
				layeredPane.validate();
			}
		});
		frame.getContentPane().add(btnShowGraph, "2, 6");

		btnShowPlan = new JButton("Show Plan");
		btnShowPlan.setEnabled(false);
		btnShowPlan.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				list.clearSelection();
				PlanView plan = new PlanView(GUI.this);
				layeredPane.removeAll();
				layeredPane.add(plan);
				layeredPane.validate();
			}
		});
		frame.getContentPane().add(btnShowPlan, "4, 6");

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
		if (!scriptFormList.isEmpty()) {
			btnShowGraph.setEnabled(true);
			btnShowPlan.setEnabled(true);
		} else {
			btnShowGraph.setEnabled(false);
			btnShowPlan.setEnabled(false);
		}
	}

	public static DummyIO getIO() {
		return io;
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

	public IntelligentDependencyGraph getDependencyGraph() {
		return DependencyGraph;
	}

	public void setDependencyGraph(IntelligentDependencyGraph DependencyGraph) {
		this.DependencyGraph = DependencyGraph;
	}

	public HashSet<EdgeClass> getDeletedEdges() {
		return deletedEdges;
	}

	public void setDeletedEdges(HashSet<EdgeClass> deletedEdges) {
		this.deletedEdges = deletedEdges;
	}

}
