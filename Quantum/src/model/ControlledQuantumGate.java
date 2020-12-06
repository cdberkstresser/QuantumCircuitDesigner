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
	/** Serializable ID. */
	private static final long serialVersionUID = 746505294177134097L;
	/** List of gates supported by this class. */
	private static final List<String> gateTypes = new ArrayList<>(
			Arrays.asList("CNOT", "C0NOT", "CCNOT", "CC00NOT", "CH", "C0H", "CCH", "CC00H", "CCCNOT", "CCC000NOT"));
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
			if (wires.get(0) < wires.get(1)) { // control above the target
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
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						}
					}
				}
				return returnGate;
			} else if (wires.get(0) > wires.get(1)) { // control below the target
				int size = (int) Math.pow(2, wires.get(0) - wires.get(1) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						if (row == col) { // diagonal
							if (row % 2 == 0) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else if (Math.abs(row - col) == size / 2 && row % 2 == 1) {
							returnGate[row][col] = new Complex(1);
						} else {
							returnGate[row][col] = new Complex(0);
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "C0NOT":
			if (wires.get(0) < wires.get(1)) { // control above the target
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
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CCNOT":
			if (Math.abs(wires.get(0) - wires.get(1)) == 1 && wires.get(2) > Math.max(wires.get(0), wires.get(1))) {
				int size = (int) Math.pow(2, wires.get(2) - Math.min(wires.get(0), wires.get(1)) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row < size * 3 / 4 || col < size * 3 / 4) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (Math.abs(row - col) == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CC00NOT":
			if (Math.abs(wires.get(0) - wires.get(1)) == 1 && wires.get(2) > Math.max(wires.get(0), wires.get(1))) {
				int size = (int) Math.pow(2, wires.get(2) - Math.min(wires.get(0), wires.get(1)) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row >= size / 4 || col >= size / 4) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (Math.abs(row - col) == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CCCNOT":
			if (wires.subList(0, wires.size() - 1).stream().max(Comparator.naturalOrder()).get()
					- wires.subList(0, wires.size() - 1).stream().min(Comparator.naturalOrder()).get() == 2
					&& wires.get(3) > Math.max(Math.max(wires.get(0), wires.get(1)), wires.get(2))) {
				int size = (int) Math.pow(2, wires.get(3) - Math.min(Math.min(wires.get(0), wires.get(1)),wires.get(2)) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row < size * 7 / 8 || col < size * 7 / 8) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (Math.abs(row - col) == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CCC000NOT":
			if (wires.subList(0, wires.size() - 1).stream().max(Comparator.naturalOrder()).get()
					- wires.subList(0, wires.size() - 1).stream().min(Comparator.naturalOrder()).get() == 2
					&& wires.get(3) > Math.max(Math.max(wires.get(0), wires.get(1)), wires.get(2))) {
				int size = (int) Math.pow(2, wires.get(3) - Math.min(Math.min(wires.get(0), wires.get(1)),wires.get(2)) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row >= size / 8 || col >= size / 8) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (Math.abs(row - col) == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CH":
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
								returnGate[row][col] = new Complex(1 / Math.sqrt(2));
							} else if (row == col) {
								if (row % 2 == 0) {
									returnGate[row][col] = new Complex(1 / Math.sqrt(2));
								} else {
									returnGate[row][col] = new Complex(-1 / Math.sqrt(2));
								}
							} else
								returnGate[row][col] = new Complex(0);
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "C0H":
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
								returnGate[row][col] = new Complex(1 / Math.sqrt(2));
							} else if (row == col) {
								if (row % 2 == 0) {
									returnGate[row][col] = new Complex(1 / Math.sqrt(2));
								} else {
									returnGate[row][col] = new Complex(-1 / Math.sqrt(2));
								}
							} else
								returnGate[row][col] = new Complex(0);
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CCH":
			if (Math.abs(wires.get(0) - wires.get(1)) == 1 && wires.get(2) > Math.max(wires.get(0), wires.get(1))) {
				int size = (int) Math.pow(2, wires.get(2) - Math.min(wires.get(0), wires.get(1)) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row < size * 3 / 4 || col < size * 3 / 4) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (Math.abs(row - col) == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(1 / Math.sqrt(2));
							} else {
								if (row % 2 == 0) {
									returnGate[row][col] = new Complex(1 / Math.sqrt(2));
								} else {
									returnGate[row][col] = new Complex(-1 / Math.sqrt(2));
								}
							}
						}
					}
				}
				return returnGate;
			}
			throw new UnsupportedOperationException("Gate not implemented yet!");
		case "CC00H":
			if (Math.abs(wires.get(0) - wires.get(1)) == 1 && wires.get(2) > Math.max(wires.get(0), wires.get(1))) {
				int size = (int) Math.pow(2, wires.get(2) - Math.min(wires.get(0), wires.get(1)) + 1);
				Complex[][] returnGate = new Complex[size][size];
				for (int row = 0; row < size; ++row) {
					for (int col = 0; col < size; ++col) {
						// build the diagonal on 3/4 of the grid.
						if (row >= size / 4 || col >= size / 4) {
							if (row == col) {
								returnGate[row][col] = new Complex(1);
							} else {
								returnGate[row][col] = new Complex(0);
							}
						} else {
							if (Math.abs(row - col) == 1 && Math.min(row, col) % 2 == 0) {
								returnGate[row][col] = new Complex(1 / Math.sqrt(2));
							} else {
								if (row % 2 == 0) {
									returnGate[row][col] = new Complex(1 / Math.sqrt(2));
								} else {
									returnGate[row][col] = new Complex(-1 / Math.sqrt(2));
								}
							}
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
}
