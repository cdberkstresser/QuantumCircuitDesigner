package model;

import java.io.Serializable;

/**
 * Represents a wire in a quantum circuit.
 * 
 * @author cdberkstresser
 *
 */
public class QuantumWire implements Serializable {
	/** Serializable ID. */
	private static final long serialVersionUID = -5515084749351383005L;
	/** The qubit that starts the wire out. */
	private Qubit start = new Qubit();
	private boolean dirty;

	/**
	 * Constructor. Defaults to |0>.
	 */
	public QuantumWire() {
		this(0);
	}

	/**
	 * Constructor.
	 * 
	 * @param initialValue The initial value of the qubit.
	 */
	public QuantumWire(final int initialValue) {
		start = new Qubit(initialValue);
	}

	/**
	 * @return Initial value of the qubit.
	 */
	public Qubit getInitialValue() {
		return start;
	}

	/**
	 * @return The start value of the qubit in this wire.
	 */
	public Qubit getStart() {
		return start;
	}

	/**
	 * Negates the start value of the qubit in this wire.
	 */
	public void xStart() {
		if (start.equals(new Qubit(0))) {
			start = new Qubit(1);
		} else {
			start = new Qubit();
		}
		dirty = true;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void resetDirty() {
		dirty = false;
	}
}
