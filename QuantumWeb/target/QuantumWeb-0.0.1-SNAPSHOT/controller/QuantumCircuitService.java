package controller;

import java.util.Arrays;
import java.util.Comparator;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import model.ControlledQuantumGate;
import model.QuantumCircuit;
import model.QuantumWire;
import model.SingleQuantumGate;
import model.SingleQuantumGateWithParameter;
import net.bootsfaces.component.canvas.Drawing;

@ManagedBean
@RequestScoped
public class QuantumCircuitService {
	/** height and width of the gates. */
	public static final int GATE_HEIGHT = 25;
	/**
	 * The maximum number of quantum wires we want to potentially support on the
	 * canvas.
	 */
	public static final int MAX_WIRES_AND_STATES = 10;
	/** 3/5 of the gate height. */
	public static final int THREE_FIFTHS_HEIGHT = GATE_HEIGHT * 3 / 5;
	/** length of each wire segment. */
	public static final int WIRE_SEGMENT_WIDTH = 150;
	/** width of canvas. */
	public static final int WIDTH = 1600;
	/** height of canvas. */
	public static final int HEIGHT = 500;
	/** font. */
	public static final String FONT = "2em Arial";

	@ManagedProperty(value = "#{quantumCircuit}")
	private QuantumCircuit quantumCircuit;

	private int wireSpacing;

	public void setNumberOfQubits(final int numberOfQubits) {
		while (quantumCircuit.getWires().size() < numberOfQubits) {
			quantumCircuit.addWire(new QuantumWire());
		}
		while (quantumCircuit.getWires().size() > numberOfQubits) {
			quantumCircuit.removeLastWire();
		}
		wireSpacing = (int) (HEIGHT / (quantumCircuit.getWires().size() + 1));
	}

	public void clear() {
		quantumCircuit = new QuantumCircuit();
	}

	public Drawing getDrawing() {
		Drawing canvas = new Drawing();
		if (quantumCircuit.getWires().size() > 0) {
			for (int wire = 0; wire < quantumCircuit.getWires().size(); ++wire) {
				setQubitLabel(quantumCircuit.getWires().get(wire).getStart().toString(), wire, canvas);
				for (int position = 0; position < quantumCircuit.getMaxWireGatePosition() + 2; ++position) {
					setNextWireSegment(wire, position, canvas);
					if (quantumCircuit.getGate(wire, position) == null) {
						setEmptyGate(wire, position, canvas);
					} else {
						String gateType = quantumCircuit.getGate(wire, position).getGateType();
						// if single simple gate
						if (SingleQuantumGate.getGateTypes().contains(gateType)) {
							setGateLabel(" " + quantumCircuit.getGate(wire, position).getGateType(), wire, position,
									canvas);
							// if single parameter gate
						} else if (SingleQuantumGateWithParameter.getGateTypes().contains(gateType)) {
							gateType += "\n("
									+ ((SingleQuantumGateWithParameter) quantumCircuit.getGate(wire, position))
											.getValue()
									+ ")";
							setGateLabel(gateType, wire, position, canvas);
							// if controlled gate
						} else if (ControlledQuantumGate.getGateTypes().contains(gateType)) {
							int targetWire = quantumCircuit.getGate(wire, position).getWires()
									.get(quantumCircuit.getGate(wire, position).getWires().size() - 1);
							// if control bit
							if (targetWire != wire) {
								setControlDot(wire, position, canvas);
								if (quantumCircuit.getGate(wire, position).getGateType().contains("0")) {
									setGateSublabel("(On 0)", wire, position, canvas);
								}
							} else { // if not control bit
								if (gateType.contains("NOT")) {
									setCNOTTargetDot(wire, position, canvas);
								} else {
									setGateLabel("|H|", wire, position, canvas);
								}
								setControlWire(
										quantumCircuit.getGate(wire, position).getWires().stream()
												.min(Comparator.naturalOrder()).get(),
										quantumCircuit.getGate(wire, position).getWires().stream()
												.max(Comparator.naturalOrder()).get(),
										position, canvas);

							}
						}
					}
				}
			}
		}
		return canvas;
	}

	public QuantumCircuit getQuantumCircuit() {
		return quantumCircuit;
	}

	public void setQuantumCircuit(QuantumCircuit quantumCircuit) {
		this.quantumCircuit = quantumCircuit;
	}

	/**
	 * Creates a controlled not gate target dot on the canvas.
	 * 
	 * @param qubitLabel The label for the qubit to draw.
	 * @param wire       The wire on which to draw the gate.
	 */
	private void setQubitLabel(final String qubitLabel, final int wire, Drawing canvas) {
		canvas.text(0, (wire + 1) * wireSpacing, qubitLabel, FONT);
	}

	/**
	 * Creates a wire segment going from this wire/position on down the score.
	 * 
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	private void setNextWireSegment(final int wire, final int position, Drawing graphicsContext) {
		graphicsContext.line(GATE_HEIGHT + WIRE_SEGMENT_WIDTH * position + GATE_HEIGHT, (wire + 1) * wireSpacing - 10,
				WIRE_SEGMENT_WIDTH * (position + 1) - GATE_HEIGHT / 2, (wire + 1) * wireSpacing - 10);
	}

	/**
	 * Creates an empty gate on the canvas.
	 * 
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	private void setEmptyGate(final int wire, final int position, Drawing graphicsContext) {
		graphicsContext.circle(WIRE_SEGMENT_WIDTH * (position + 1) + 3 * GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing - GATE_HEIGHT / 3, GATE_HEIGHT);
	}

	/**
	 * Sets a gate label for a given gate.
	 * 
	 * @param gateLabel The gate label to put on the gate.
	 * @param wire      The wire on which to draw the gate.
	 * @param position  The position on the wire to draw the gate.
	 */
	private void setGateLabel(final String gateLabel, final int wire, final int position, Drawing graphicsContext) {
		graphicsContext.text(WIRE_SEGMENT_WIDTH * (position + 1), (wire + 1) * wireSpacing, gateLabel, FONT);
	}

	/**
	 * Creates a control dot on the canvas for controlled gates.
	 * 
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	private void setControlDot(final int wire, final int position, Drawing graphicsContext) {
		graphicsContext.circle(WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5 + GATE_HEIGHT / 4, GATE_HEIGHT / 2);
	}

	/**
	 * Sets a gate sub-label for a given gate.
	 * 
	 * @param subLabel The gate label to put on the gate.
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	public void setGateSublabel(final String subLabel, final int wire, final int position, Drawing graphicsContext) {
		graphicsContext.text(WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing + THREE_FIFTHS_HEIGHT + GATE_HEIGHT / 4, subLabel, FONT);
	}

	/**
	 * Creates a controlled not gate target dot on the canvas.
	 * 
	 * @param wire     The wire on which to draw the gate.
	 * @param position The position on the wire to draw the gate.
	 */
	private void setCNOTTargetDot(final int wire, final int position, Drawing graphicsContext) {
		graphicsContext.circle(WIRE_SEGMENT_WIDTH * (position + 1), (wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5,
				GATE_HEIGHT);
		graphicsContext.line(WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 2,
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5,
				WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 2,
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5 + GATE_HEIGHT);
		graphicsContext.line(WIRE_SEGMENT_WIDTH * (position + 1),
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5 + GATE_HEIGHT / 2,
				WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT,
				(wire + 1) * wireSpacing - THREE_FIFTHS_HEIGHT - 5 + GATE_HEIGHT / 2);
	}

	/**
	 * Draws a wire between two points.
	 * 
	 * @param wire1    The first wire from which to draw the control wire.
	 * @param wire2    The second wire from which to draw the control wire.
	 * @param position The position on the wires to connect.
	 */
	public void setControlWire(final int wire1, final int wire2, final int position, Drawing graphicsContext) {
		graphicsContext.line(WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 2, (wire1 + 1) * wireSpacing,
				WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 2, (wire2 + 1) * wireSpacing - 15);
	}
}
