package de.tum.in.fedsparql.inference.framework.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
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

import de.tum.in.fedsparql.inference.framework.DatabaseID;
import de.tum.in.fedsparql.inference.framework.DependencyGraph;
import de.tum.in.fedsparql.inference.framework.Script;
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

	Graph<Script, EdgeClass> g;
	Factory <Script> vertexFactory;
	Factory<EdgeClass> edgeFactory;

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
			DatabaseID[] inputArray = new DatabaseID[inputList.size()];
			for (int i = 0; i < inputList.size(); i++) {
				inputArray[i] = new DatabaseID(inputList.get(i));
			}
			DatabaseID[] outputArray = new DatabaseID[outputList.size()];
			for (int i = 0; i < outputList.size(); i++) {
				outputArray[i] = new DatabaseID(outputList.get(i));
			}
			String scriptCode = form.getTextArea().getText();

			Script script = new Script(name, inputArray, outputArray, scriptCode);
			scriptList.add(script);
		}

		Script[] scriptArray = new Script[scriptList.size()];
		for (int i = 0; i < scriptList.size(); i++) {
			scriptArray[i] = scriptList.get(i);
		}

		DependencyGraph scripts = null;
		scripts = new DependencyGraph(scriptArray);
		for (EdgeClass edge : gui.getDeletedEdges()) {
			scripts.removeDependency(edge.vertex1, edge.vertex2);
		}
		gui.setDependencyGraph(scripts);

		g = new SparseMultigraph<Script, EdgeClass>();

		for (Script script : scripts.getScripts()) {
			g.addVertex(script);
		}
		Map<Script,Set<Script>> map = scripts.getDirectDependencies();
		for (Script vertex1 : map.keySet()) {
			for (Script vertex2 : map.get(vertex1)) {
				g.addEdge(new EdgeClass(vertex1, vertex2), vertex1, vertex2, EdgeType.DIRECTED);
			}
		}
		Map<Script,Set<Script>> manuallyRemovedDependencies = scripts.getManuallyRemovedDependencies();
		for (Script vertex1 : manuallyRemovedDependencies.keySet()) {
			for (Script vertex2 : manuallyRemovedDependencies.get(vertex1)) {
				g.addEdge(new EdgeClass(vertex1, vertex2, true), vertex1, vertex2, EdgeType.DIRECTED);
			}
		}

		Layout<Script, EdgeClass> layout = new ISOMLayout<Script, EdgeClass>(g);
		final VisualizationViewer<Script, EdgeClass> vv = new VisualizationViewer<Script, EdgeClass>(layout);
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

		EditingModalGraphMouse<Script, EdgeClass> gm =
				new EditingModalGraphMouseExtension<Script, EdgeClass>(
						vv.getRenderContext(),
						vertexFactory,
						edgeFactory,
						gui);
		vv.setGraphMouse(gm);
		gm.setMode(ModalGraphMouse.Mode.PICKING);

		Transformer<Script, Paint> vertexPaint = new Transformer<Script, Paint>() {
			@Override
			public Paint transform(Script script) {
				return Color.LIGHT_GRAY;
			}
		};
		vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);

		float[] dash = {10.0f};
		final Stroke edgeStroke1 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		final Stroke edgeStroke2 = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

		Transformer<EdgeClass, Stroke> edgeStrokeTransformer = new Transformer<EdgeClass, Stroke>() {
			@Override
			public Stroke transform(EdgeClass edge) {
				if (edge.getDeleted()) {
					return edgeStroke1;
				} else {
					return edgeStroke2;
				}
			}
		};
		vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);

	}
}