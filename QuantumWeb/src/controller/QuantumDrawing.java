package controller;

import net.bootsfaces.component.canvas.Drawing;

/**
 * Adds onclick functionality to Drawing for the canvas object in the view.
 * 
 * @author cberkstresser
 *
 */
public final class QuantumDrawing extends Drawing {
	@Override
	public String getJavaScript() {
		return super.getJavaScript()
				+ "canvas.onclick = function() {var xOffset = canvas.offsetLeft;var yOffset = canvas.offsetTop;setClick(event.clientX-xOffset, event.clientY-yOffset,canvas.clientWidth,canvas.clientHeight, event);};";
	}
}
