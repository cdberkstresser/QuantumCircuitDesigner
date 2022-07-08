package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A single quantum gate that requires an argument for a paramter.
 * 
 * @author cdberkstresser
 *
 */
public class SingleQuantumGateWithParameter implements QuantumGateWithParameter {
	/** Serializable ID. */
	private static final long serialVersionUID = -7753721998037365439L;
	/** List of gates supported by this class. */
	private static final List<String> gateTypes = new ArrayList<>(Arrays.asList("Rx", "Ry", "Rz"));
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
	 * @param gateType     The type of circuit.
	 * @param value        The value required for this gate type.
	 * @param gatePosition The horizontal position of the gate in the circuit. Zero
	 *                     based.
	 * @param wires        The wires involved in this gate.
	 */
	public SingleQuantumGateWithParameter(final String gateType, final double value, final int gatePosition,
			final List<Integer> wires) {
		this.gateType = gateType;
		this.value = value;
		this.gatePosition = gatePosition;
		this.wires = wires;
	}

	/**
	 * Clone to return a copy. Make sure to override gateType, gatePosition, and
	 * wires.
	 */
	@Override
	public SingleQuantumGateWithParameter clone() {
		return new SingleQuantumGateWithParameter(gateType, value, gatePosition, wires);
	}

	/**
	 * Get the gate matrix for each gate. Must be done for each gate you accept as
	 * well as each wire configuration you wish to support for each gate.
	 */
	@Override
	public Complex[][] getGateMatrix() {
		switch (gateType) {
		case "Rx":
			return new Complex[][] { { new Complex(Math.cos(value / 2), 0), new Complex(0, -Math.sin(value / 2)) },
					{ new Complex(0, -Math.sin(value / 2)), new Complex(Math.cos(value / 2), 0) } };
		case "Ry":
			return new Complex[][] { { new Complex(Math.cos(value / 2)), new Complex(-Math.sin(value / 2)) },
					{ new Complex(Math.sin(value / 2)), new Complex(Math.cos(value / 2)) } };
		case "Rz":
			return new Complex[][] { { new Complex(Math.cos(value / 2), -Math.sin(value / 2)), new Complex(0) },
					{ new Complex(0), new Complex(Math.cos(value / 2), Math.sin(value / 2)) } };
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
	 * @return The parameter value associated with this gate type.
	 */
	public double getValue() {
		return value;
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
