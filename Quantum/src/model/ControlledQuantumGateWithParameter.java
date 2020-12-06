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
public class ControlledQuantumGateWithParameter implements QuantumGateWithParameter {
	/** Serializable ID. */
	private static final long serialVersionUID = 746505294177134097L;
	/** List of gates supported by this class. */
	private static final List<String> gateTypes = new ArrayList<>(Arrays.asList("CRx", "CRy", "CRz", "C0Rx", "C0Ry", "C0Rz"));
	/** The gate type as a string. Should be filtered through the list above. */
	private final String gateType;
	/** The horizontal position of this gate on the circuit. Zero based. */
	private final int gatePosition;
	/**
	 * The wires involved in this gate. The last is the target wire. The others are
	 * controls.
	 */
	private final List<Integer> wires;
	/**
	 * The value associated with this quantum gate.
	 */
	private final double value;

	/**
	 * Constructor.
	 * 
	 * @param type         The type of circuit.
	 * @param value        The value required for this gate type.
	 * @param gatePosition The horizontal position of the gate in the circuit. Zero
	 *                     based.
	 * @param wires        The wires involved in this gate.
	 */
	public ControlledQuantumGateWithParameter(final String type, final double value, final int gatePosition,
			final List<Integer> wires) {
		if (gateTypes.contains(type)) {
			gateType = type;
			this.value = value;
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
	public ControlledQuantumGateWithParameter clone() {
		return new ControlledQuantumGateWithParameter(gateType, value, gatePosition, wires);
	}

	/**
	 * Get the gate matrix for each gate. Must be done for each gate you accept as
	 * well as each wire configuration you wish to support for each gate.
	 */
	@Override
	public Complex[][] getGateMatrix() {
		switch (gateType) {
		case "CRx":
			if (wires.get(0) < wires.get(1)) {
				int size = (int) Math.pow(2, wires.get(1) - wires.get(0) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row < size / 2 || col < size / 2) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (Math.abs(row - col) == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(0, -Math.sin(value / 2));
							} else if (row == col) {
								returnGate[row][col] = new Complex(Math.cos(value / 2));
							} else
								returnGate[row][col] = new Complex(0);
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CRy":
			if (wires.get(0) < wires.get(1)) {
				int size = (int) Math.pow(2, wires.get(1) - wires.get(0) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row < size / 2 || col < size / 2) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (row - col == -1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(-Math.sin(value / 2));
							} else if (row - col == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(Math.sin(value / 2));
							} else if (row == col) {
								returnGate[row][col] = new Complex(Math.cos(value / 2));
							} else {
								returnGate[row][col] = new Complex(0);
							}
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CRz":
			if (wires.get(0) < wires.get(1)) {
				int size = (int) Math.pow(2, wires.get(1) - wires.get(0) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row < size / 2 || col < size / 2) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (row == col) {
								if (row % 2 == 0) {
									returnGate[row][col] = new Complex(Math.cos(value / 2), -Math.sin(value / 2));
								} else {
									returnGate[row][col] = new Complex(Math.cos(value / 2), Math.sin(value / 2));
								}
							} else
								returnGate[row][col] = new Complex(0);
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "C0Rx":
			if (wires.get(0) < wires.get(1)) {
				int size = (int) Math.pow(2, wires.get(1) - wires.get(0) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row >= size / 2 || col >= size / 2) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (Math.abs(row - col) == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(0, -Math.sin(value / 2));
							} else if (row == col) {
								returnGate[row][col] = new Complex(Math.cos(value / 2));
							} else
								returnGate[row][col] = new Complex(0);
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "C0Ry":
			if (wires.get(0) < wires.get(1)) {
				int size = (int) Math.pow(2, wires.get(1) - wires.get(0) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row >= size / 2 || col >= size / 2) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (row - col == -1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(-Math.sin(value / 2));
							} else if (row - col == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(Math.sin(value / 2));
							} else if (row == col) {
								returnGate[row][col] = new Complex(Math.cos(value / 2));
							} else {
								returnGate[row][col] = new Complex(0);
							}
						}
					}
				}
				for (Complex[] c : returnGate) {
					System.out.println(Arrays.toString(c));
				}
				System.out.println();
				System.out.println();
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "C0Rz":
			if (wires.get(0) < wires.get(1)) {
				int size = (int) Math.pow(2, wires.get(1) - wires.get(0) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row >= size / 2 || col >= size / 2) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (row == col) {
								if (row % 2 == 0) {
									returnGate[row][col] = new Complex(Math.cos(value / 2), -Math.sin(value / 2));
								} else {
									returnGate[row][col] = new Complex(Math.cos(value / 2), Math.sin(value / 2));
								}
							} else
								returnGate[row][col] = new Complex(0);
						}
					}
				}
				return returnGate;
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

	@Override
	public double getValue() {
		return value;
	}
}
