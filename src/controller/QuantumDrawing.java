package controller;

import net.bootsfaces.component.canvas.Drawing;

/**
 * Adds onclick functionality to Drawing for the canvas object in the view.
 * 
 * @author cberkstresser
 *
 */
public final class QuantumDrawing extends Drawing {
	private String toolTip;

	@Override
	public String getJavaScript() {
		return super.getJavaScript()
				+ "canvas.onclick = function() {"
				+ " var xOffset = canvas.offsetLeft;"
				+ " var yOffset = canvas.offsetTop;"
				+ " setClick(event.clientX-xOffset, event.clientY-yOffset+window.scrollY,canvas.clientWidth,canvas.clientHeight,event.ctrlKey, event);" 
				+ "};"
				+ "canvas.title = '"+ toolTip +"';";
		
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}
}
