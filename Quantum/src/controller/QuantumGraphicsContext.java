package controller;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

/**
 * Utility method for drawing on canvas. Would like to have extended
 * GraphicsContext, but it was marked final :(.
 * 
 * @author cdberkstresser
 *
 */
public class QuantumGraphicsContext {
	/** font size for the drawing. */
	public static final int FONT_SIZE = 30;
	/** height and width of the gates. */
	public static final int GATE_HEIGHT = 50;
	/** length of each wire segment. */
	public static final int WIRE_SEGMENT_WIDTH = 150;
	/** 3/5 of the gate height. */
	public static final int THREE_FIFTHS_HEIGHT = GATE_HEIGHT * 3 / 5;

	/** Placeholder for the underlying GraphicsContext. */
	private GraphicsContext graphicsContext;

	/** The length of wires between gates. */
	private int wireSpacing;

	/**
	 * Constructor.
	 * 
	 * @param graphicsContext The underlying graphics context to utilize.
	 * @param numberOfQubits  The number of qubits on the canvas.
	 */
	public QuantumGraphicsContext(final GraphicsContext graphicsContext, final int numberOfQubits) {
		this.graphicsContext = graphicsContext;
		this.graphicsContext.setFont(Font.font(FONT_SIZE));
		this.wireSpacing = (int) (graphicsContext.getCanvas().getHeight() / (numberOfQubits + 1));
	}

	/**
	 * Clear the canvas.
	 */
	public void clearAll() {
		graphicsContext.clearRect(0, 0, graphicsContext.getCanvas().getWidth(),
				graphicsContext.getCanvas().getHeight());
	}

	/**
	 * Creates an empty gate on the canvas.
	 * 
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	public void setEmptyGate(final int wire, final int position) {
		graphicsContext.fillText("  ☐", WIRE_SEGMENT_WIDTH * (position + 1), (wire + 1) * wireSpacing);
	}

	/**
	 * Creates a control dot on the canvas for controlled gates.
	 * 
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	public void setControlDot(final int wire, final int position) {
		graphicsContext.fillOval(WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5 + GATE_HEIGHT / 4, GATE_HEIGHT / 2, GATE_HEIGHT / 2);
	}

	/**
	 * Creates a controlled not gate target dot on the canvas.
	 * 
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	public void setCNOTTargetDot(final int wire, final int position) {
		graphicsContext.strokeOval(WIRE_SEGMENT_WIDTH * (position + 1),
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5, GATE_HEIGHT, GATE_HEIGHT);
		graphicsContext.strokeLine(WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 2,
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5,
				WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 2,
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5 + GATE_HEIGHT);
		graphicsContext.strokeLine(WIRE_SEGMENT_WIDTH * (position + 1),
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5 + GATE_HEIGHT / 2,
				WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT,
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5 + GATE_HEIGHT / 2);
	}

	/**
	 * Creates a controlled not gate target dot on the canvas.
	 * 
	 * @param qubitLabel The label for the qubit to draw.
	 * @param wire       The wire on which to draw the gate.
	 */
	public void setQubitLabel(final String qubitLabel, final int wire) {
		graphicsContext.fillText(qubitLabel, 0, (wire + 1) * wireSpacing);
	}

	/**
	 * Creates a wire segment going from this wire/position on down the score.
	 * 
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	public void setNextWireSegment(final int wire, final int position) {
		graphicsContext.strokeLine(GATE_HEIGHT + WIRE_SEGMENT_WIDTH * position, (wire + 1) * wireSpacing - 10,
				WIRE_SEGMENT_WIDTH * (position + 1), (wire + 1) * wireSpacing - 10);
	}

	/**
	 * Sets a gate label for a given gate.
	 * 
	 * @param gateLabel The gate label to put on the gate.
	 * @param wire      The wire on which to draw the gate.
	 * @param position  The position on the wire to draw the gate.
	 */
	public void setGateLabel(final String gateLabel, final int wire, final int position) {
		graphicsContext.fillText(gateLabel, WIRE_SEGMENT_WIDTH * (position + 1), (wire + 1) * wireSpacing,
				WIRE_SEGMENT_WIDTH);
	}

	/**
	 * Sets a gate sub-label for a given gate.
	 * 
	 * @param subLabel The gate label to put on the gate.
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	public void setGateSublabel(final String subLabel, final int wire, final int position) {
		graphicsContext.fillText(subLabel, WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing + THREE_FIFTHS_HEIGHT + GATE_HEIGHT / 4, WIRE_SEGMENT_WIDTH);
	}

	/**
	 * Draws a wire between two points.
	 * 
	 * @param wire1    The first wire from which to draw the control wire.
	 * @param wire2    The second wire from which to draw the control wire.
	 * @param position The position on the wires to connect.
	 */
	public void setControlWire(final int wire1, final int wire2, final int position) {
		graphicsContext.strokeLine(WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 2, (wire1 + 1) * wireSpacing,
				WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 2, (wire2 + 1) * wireSpacing - 15);
	}

}
