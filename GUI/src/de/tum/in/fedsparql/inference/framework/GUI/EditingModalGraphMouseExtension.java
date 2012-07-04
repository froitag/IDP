package de.tum.in.fedsparql.inference.framework.GUI;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;

public class EditingModalGraphMouseExtension<V,E> extends EditingModalGraphMouse<V,E> {
	
	protected EditingPopupGraphMousePluginExtension<V, E> newDeletePlugin;
	
	public EditingModalGraphMouseExtension(RenderContext<V, E> rc,
			Factory<V> vertexFactory, Factory<E> edgeFactory, GUI gui) {
		super(rc, vertexFactory, edgeFactory);
		// TODO Auto-generated constructor stub
		newDeletePlugin = new EditingPopupGraphMousePluginExtension<V,E>(vertexFactory, edgeFactory, gui);
	}
	
	@Override
    protected void setPickingMode() {
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(editingPlugin);
		remove(annotatingPlugin);
		remove(popupEditingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
		add(labelEditingPlugin);
		add(newDeletePlugin);
	}

}
