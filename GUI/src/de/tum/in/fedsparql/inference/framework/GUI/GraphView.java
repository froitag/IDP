package de.tum.in.fedsparql.inference.framework.GUI;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.TableModel;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.tum.in.fedsparql.inference.dummy.JenaDatabase;
import de.tum.in.fedsparql.inference.framework.Script;
import de.tum.in.fedsparql.inference.framework.ScriptCollection;
import de.tum.in.fedsparql.inference.framework.exceptions.CircularDependencyException;
import de.tum.in.fedsparql.inference.io.Database;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

@SuppressWarnings("serial")
public class GraphView extends JPanel {

	Graph<Script, String> g;
	Factory <Script> vertexFactory;
	Factory<String> edgeFactory;

	/**
	 * Create the panel.
	 */
	public GraphView(GUI gui) {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,},
				new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,}));

		JPanel panel = new JPanel();
		add(panel, "2, 2, 1, 3, fill, fill");

		ArrayList<Script> scriptList = new ArrayList<Script>();
		for (ScriptForm form : gui.getScriptFormList()) {

			TableModel inputModel = form.getInputTable().getModel();
			ArrayList<String> inputList = new ArrayList<String>();
			for (int i = 0; i < inputModel.getRowCount(); i++) {
				if ((boolean)inputModel.getValueAt(i, 0) == true) {
					inputList.add((String)inputModel.getValueAt(i, 1));
				}
			}
			TableModel outputModel = form.getOutputTable().getModel();
			ArrayList<String> outputList = new ArrayList<String>();
			for (int i = 0; i < outputModel.getRowCount(); i++) {
				if ((boolean)outputModel.getValueAt(i, 0) == true) {
					outputList.add((String)outputModel.getValueAt(i, 1));
				}
			}

			String name = form.getTextField();
			Database[] inputArray = new Database[inputList.size()];
			for (int i = 0; i < inputList.size(); i++) {
				inputArray[i] = new JenaDatabase(inputList.get(i));
			}
			Database[] outputArray = new Database[outputList.size()];
			for (int i = 0; i < outputList.size(); i++) {
				outputArray[i] = new JenaDatabase(outputList.get(i));
			}
			String scriptCode = form.getTextArea().getText();

			Script script = new Script(name, inputArray, outputArray, scriptCode);
			scriptList.add(script);
		}

		Script[] scriptArray = new Script[scriptList.size()];
		for (int i = 0; i < scriptList.size(); i++) {
			scriptArray[i] = scriptList.get(i);
		}

		ScriptCollection scripts = null;
		try {
			scripts = new ScriptCollection(scriptArray);
		} catch (CircularDependencyException e) {
			e.printStackTrace();
		}

		g = new SparseMultigraph<Script, String>();

		for (Script script : scripts.getScripts()) {
			g.addVertex(script);
		}
		Map<Script,Set<Script>> map = scripts.getDirectDependencies();
		for (Script vertex1 : map.keySet()) {
			for (Script vertex2 : map.get(vertex1)) {
				g.addEdge(vertex1 + ":" + vertex2, vertex1, vertex2, EdgeType.DIRECTED);
			}
		}

		Layout<Script, String> layout = new ISOMLayout<Script, String>(g);
		final VisualizationViewer<Script, String> vv = new VisualizationViewer<Script, String>(layout);
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		GraphZoomScrollPane graphPanel = new GraphZoomScrollPane(vv);
		panel.add(graphPanel);

		JButton btnMinus = new JButton("-");
		add(btnMinus, "4, 2, fill, bottom");

		JButton btnPlus = new JButton("+");
		add(btnPlus, "4, 4, fill, top");

		final ScalingControl scaler = new CrossoverScalingControl();

		btnMinus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1/1.1f, vv.getCenter());
			}
		});
		btnPlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scaler.scale(vv, 1.1f, vv.getCenter());
			}
		});

		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Script>());

		EditingModalGraphMouse<Script, String> gm = new EditingModalGraphMouse<Script, String>(vv.getRenderContext(), vertexFactory, edgeFactory);
		vv.setGraphMouse(gm);
		gm.setMode(ModalGraphMouse.Mode.PICKING);

		Transformer<Script, Paint> vertexPaint = new Transformer<Script, Paint>() {
			@Override
			public Paint transform(Script script) {
				return Color.LIGHT_GRAY;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);

	}

}