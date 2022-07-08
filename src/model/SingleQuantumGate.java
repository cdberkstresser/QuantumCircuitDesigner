package model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A single simple quantum gate.
 * 
 * @author cdberkstresser
 *
 */
public class SingleQuantumGate implements QuantumGate {
	/** List of gates supported by this class. */
	private static final Map<String, Complex[][]> gates = new HashMap<>();
	static {
		gates.put("I", new Complex[][] { { new Complex(1), new Complex(0) }, { new Complex(0), new Complex(1) } });
		gates.put("H", new Complex[][] { { new Complex(1 / Math.sqrt(2)), new Complex(1 / Math.sqrt(2)) },
				{ new Complex(1 / Math.sqrt(2)), new Complex(-1 / Math.sqrt(2)) } });
		gates.put("X", new Complex[][] { { new Complex(0), new Complex(1) }, { new Complex(1), new Complex(0) } });
		gates.put("Y",
				new Complex[][] { { new Complex(0), new Complex(0, -1) }, { new Complex(0, 1), new Complex(0) } });
		gates.put("Z", new Complex[][] { { new Complex(1), new Complex(0) }, { new Complex(0), new Complex(-1) } });
		gates.put("S", new Complex[][] { { new Complex(1), new Complex(0) }, { new Complex(0), new Complex(0, 1) } });
		gates.put("T", new Complex[][] { { new Complex(1), new Complex(0) },
				{ new Complex(0), new Complex(Math.cos(Math.PI / 4.0), Math.sin(Math.PI / 4.0)) } });
	}

	/** Serializable ID. */
	private static final long serialVersionUID = -2798838823193813073L;
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
	public SingleQuantumGate(final String type, final int gatePosition, final List<Integer> wires) {
		gateType = type;
		this.gatePosition = gatePosition;
		this.wires = wires;
	}

	/**
	 * Clone to return a copy. Make sure to override gateType, gatePosition, and
	 * wires.
	 */
	@Override
	public SingleQuantumGate clone() {
		return new SingleQuantumGate(gateType, gatePosition, wires);
	}

	/**
	 * Get the gate matrix for each gate. Must be done for each gate you accept as
	 * well as each wire configuration you wish to support for each gate.
	 */
	@Override
	public Complex[][] getGateMatrix() {
		return gates.get(gateType);
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
	public static Set<String> getGateTypes() {
		return gates.keySet();
	}
}
