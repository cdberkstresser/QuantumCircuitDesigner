package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Quantum gate class. Contains gates for quantum circuits.
 * 
 * @author cdberkstresser
 *
 */
public class ControlledQuantumGate implements QuantumGate {
	/** List of gates supported by this class. */
	private static List<String> gateTypes = new ArrayList<>(
			Arrays.asList("CNOT", "C0NOT", "CCNOT", "CC00NOT", "CH", "C0H"));
	/** The gate type as a string. Should be filtered through the list above. */
	private String gateType;
	/** The horizontal position of this gate on the circuit. Zero based. */
	private int gatePosition;
	/**
	 * The wires involved in this gate. The last is the target wire. The others are
	 * controls.
	 */
	private List<Integer> wires;

	/**
	 * Constructor.
	 * 
	 * @param type         The type of circuit.
	 * @param gatePosition The horizontal position of the gate in the circuit. Zero
	 *                     based.
	 * @param wires        The wires involved in this gate.
	 */
	public ControlledQuantumGate(final String type, final int gatePosition, final List<Integer> wires) {
		if (gateTypes.contains(type)) {
			gateType = type;
			this.gatePosition = gatePosition;
			this.wires = wires;
		} else {
			throw new IllegalArgumentException("That gate type is not supported.");
		}
	}

	/**
	 * Clone to return a copy. Make sure to override gateType, gatePosition, and
	 * wires.
	 */
	@Override
	public ControlledQuantumGate clone() {
		return new ControlledQuantumGate(gateType, gatePosition, wires);
	}

	/**
	 * Get the gate matrix for each gate. Must be done for each gate you accept as
	 * well as each wire configuration you wish to support for each gate.
	 */
	@Override
	public Complex[][] getGateMatrix() {
		switch (gateType) {
		case "CNOT":
			if (wires.get(0) == wires.get(1) - 1) {
				return new Complex[][] { { new Complex(1), new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(1), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(1) },
						{ new Complex(0), new Complex(0), new Complex(1), new Complex(0) } };
			} else if (wires.get(0) == wires.get(1) + 1) {
				return new Complex[][] { { new Complex(1), new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(1) },
						{ new Complex(0), new Complex(0), new Complex(1), new Complex(0) },
						{ new Complex(0), new Complex(1), new Complex(0), new Complex(0) } };
			} else if (wires.get(0) == wires.get(1) - 2) {
				return new Complex[][] {
						{ new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(1), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(1) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(1), new Complex(0) },

				};
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "C0NOT":
			if (wires.get(0) == wires.get(1) - 1) {
				return new Complex[][] { { new Complex(0), new Complex(1), new Complex(0), new Complex(0) },
						{ new Complex(1), new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(1), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(1) } };
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CCNOT":
			if (Math.abs(wires.get(0) - wires.get(1)) == 1
					&& Math.max(wires.get(0), wires.get(1)) == wires.get(2) - 1) {
				return new Complex[][] {
						{ new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(1), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(1) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(1), new Complex(0) },

				};
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CC00NOT":
			if (Math.abs(wires.get(0) - wires.get(1)) == 1
					&& Math.max(wires.get(0), wires.get(1)) == wires.get(2) - 1) {
				return new Complex[][] {
						{ new Complex(0), new Complex(1), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(1), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(1), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(1), new Complex(0),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(1),
								new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(1), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(1), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(0), new Complex(0),
								new Complex(0), new Complex(0), new Complex(1) },

				};
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CH":
			if (wires.get(0) == wires.get(1) - 1) {
				return new Complex[][] { { new Complex(1), new Complex(0), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(1), new Complex(0), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(1 / Math.sqrt(2)),
								new Complex(1 / Math.sqrt(2)) },
						{ new Complex(0), new Complex(0), new Complex(1 / Math.sqrt(2)),
								new Complex(-1 / Math.sqrt(2)) },

				};
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "C0H":
			if (wires.get(0) == wires.get(1) - 1) {
				return new Complex[][] {
						{ new Complex(1 / Math.sqrt(2)), new Complex(1 / Math.sqrt(2)), new Complex(0),
								new Complex(0) },
						{ new Complex(1 / Math.sqrt(2)), new Complex(-1 / Math.sqrt(2)), new Complex(0),
								new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(1), new Complex(0) },
						{ new Complex(0), new Complex(0), new Complex(0), new Complex(1) },

				};
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		default:
			throw new UnsupportedOperationException("Gate not implemented yet!");
		}
	}

	/**
	 * @return The gate type.
	 */
	@Override
	public String getGateType() {
		return gateType;
	}

	/**
	 * Set the gate type.
	 */
	@Override
	public void setGateType(final String value) {
		if (gateTypes.contains(gateType)) {
			gateType = value;
		} else {
			throw new IllegalArgumentException("That gatetype is not supported.");
		}

	}

	/**
	 * @return a string representation of this gate.
	 */
	@Override
	public String toString() {
		if (!gateType.equals("I")) {
			return gateType;
		}
		return "";
	}

	/**
	 * @return the horizontal gate position in the circuit.
	 */
	@Override
	public int getGatePosition() {
		return gatePosition;
	}

	/**
	 * Sets the gate position to a different location.
	 */
	@Override
	public void setGatePosition(final int value) {
		this.gatePosition = value;
	}

	/**
	 * Compares two gates.
	 */
	@Override
	public int compareTo(final QuantumGate arg0) {
		return this.getWires().stream().min(Comparator.naturalOrder()).orElse(0)
				- arg0.getWires().stream().min(Comparator.naturalOrder()).orElse(0);
	}

	/**
	 * @return the wires associated with this gate.
	 */
	@Override
	public List<Integer> getWires() {
		return wires;
	}

	/**
	 * @return A list of gate types supported by this class.
	 */
	public static List<String> getGateTypes() {
		return gateTypes;
	}
}
