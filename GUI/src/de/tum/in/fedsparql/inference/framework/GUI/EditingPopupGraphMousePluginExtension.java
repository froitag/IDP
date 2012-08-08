package de.tum.in.fedsparql.inference.framework.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Set;

import javax.swing.AbstractAction;
import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
//import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;

public class EditingPopupGraphMousePluginExtension<V,E> extends EditingPopupGraphMousePlugin<V,E> {
	
	final GUI gui;
	
	public EditingPopupGraphMousePluginExtension(Factory<V> vertexFactory,
			Factory<E> edgeFactory, GUI gui) {
		super(vertexFactory, edgeFactory);
		// TODO Auto-generated constructor stubX
		this.gui = gui;
	}

	@SuppressWarnings("serial")
	@Override
	protected void handlePopup(MouseEvent e) {
		popup.removeAll();
        @SuppressWarnings("unchecked")
		final VisualizationViewer<V,E> vv =
            (VisualizationViewer<V,E>)e.getSource();
        final Layout<V,E> layout = vv.getGraphLayout();
        //final Graph<V,E> graph = layout.getGraph();
        final Point2D p = e.getPoint();
        final Point2D ivp = p;
        GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            
            final V vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
            final E edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
            final PickedState<V> pickedVertexState = vv.getPickedVertexState();
            final PickedState<E> pickedEdgeState = vv.getPickedEdgeState();
            
            if(vertex != null) {
            	Set<V> picked = pickedVertexState.getPicked();
            	if(picked.size() > 0) {
//            		if(graph instanceof UndirectedGraph == false) {
//            			JMenu directedMenu = new JMenu("Create Directed Edge");
//            			popup.add(directedMenu);
//            			for(final V other : picked) {
//            				directedMenu.add(new AbstractAction("["+other+","+vertex+"]") {
//            					public void actionPerformed(ActionEvent e) {
//            						graph.addEdge(edgeFactory.create(),
//            								other, vertex, EdgeType.DIRECTED);
//            						vv.repaint();
//            					}
//            				});
//            			}
//            		}
//            		if(graph instanceof DirectedGraph == false) {
//            			JMenu undirectedMenu = new JMenu("Create Undirected Edge");
//            			popup.add(undirectedMenu);
//            			for(final V other : picked) {
//            				undirectedMenu.add(new AbstractAction("[" + other+","+vertex+"]") {
//            					public void actionPerformed(ActionEvent e) {
//            						graph.addEdge(edgeFactory.create(),
//            								other, vertex);
//            						vv.repaint();
//            					}
//            				});
//            			}
//            		}
                }
//                popup.add(new AbstractAction("Delete Vertex") {
//                    public void actionPerformed(ActionEvent e) {
//                        pickedVertexState.pick(vertex, false);
//                        graph.removeVertex(vertex);
//                        vv.repaint();
//                    }});
            } else if(edge != null) {
            	final EdgeClass edgeClass = (EdgeClass)edge;
            	if (edgeClass.getDeleted()) {
                    popup.add(new AbstractAction("Restore Edge") {
                        public void actionPerformed(ActionEvent e) {
                            pickedEdgeState.pick(edge, false);
                            //graph.removeEdge(edge);
                            edgeClass.setDeleted(false);
                            System.err.println(gui.getDeletedEdges().size());
                            gui.getDeletedEdges().remove(edgeClass);
                            System.err.println(gui.getDeletedEdges().size());
                            gui.getDependencyGraph().addDependency(edgeClass.vertex1, edgeClass.vertex2);
                            vv.repaint();
                        }});
            	} else {
                    popup.add(new AbstractAction("Delete Edge") {
                        public void actionPerformed(ActionEvent e) {
                            pickedEdgeState.pick(edge, false);
                            //graph.removeEdge(edge);
                            edgeClass.setDeleted(true);
                            gui.getDeletedEdges().add(edgeClass);
                            gui.getDependencyGraph().removeDependency(edgeClass.vertex1, edgeClass.vertex2);
                            vv.repaint();
                        }});
            	}
            } else {
//                popup.add(new AbstractAction("Create Vertex") {
//                    public void actionPerformed(ActionEvent e) {
//                        V newVertex = vertexFactory.create();
//                        graph.addVertex(newVertex);
//                        layout.setLocation(newVertex, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p));
//                        vv.repaint();
//                    }
//                });
            }
            if(popup.getComponentCount() > 0) {
                popup.show(vv, e.getX(), e.getY());
            }
        }
    }
	
}
