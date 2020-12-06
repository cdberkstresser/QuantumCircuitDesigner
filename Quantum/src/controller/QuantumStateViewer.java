package controller;

import java.util.List;

import model.Complex;

/**
 * Support file for viewing states and their probabilities of measuring to one
 * at any given time. This class will basically cross-tab the results.
 * 
 * @author cdberkstresser
 *
 */
public final class QuantumStateViewer {
	/**
	 * Number of qubits of which this class will represent one state. Used for left
	 * padding the binary representations of qubits with zeros.
	 */
	private final int numberOfQubits;
	/** The qubit states. For example 5, which will be represented as |101>. */
	private final int qubits;
	/**
	 * List of states at any given time associated with this qubit arrangement. Will
	 * be cross-tabbed.
	 */
	private final List<Complex> states;

	/**
	 * Constructor.
	 * 
	 * @param numberOfQubits Total number of qubits to consider in all the
	 *                       arrangement.
	 * @param qubits         The qubit arrangement.
	 * @param states         An array of states to be crosstabbed with this qubit
	 *                       arrangement.
	 */
	public QuantumStateViewer(final int numberOfQubits, final int qubits, final List<Complex> states) {
		this.numberOfQubits = numberOfQubits;
		this.qubits = qubits;
		this.states = states;
	}

	/**
	 * @return Qubit arrangment as an int.
	 */
	public int getQubitsAsInt() {
		return qubits;
	}

	/**
	 * @return Qubit arrangment as a string.
	 */
	public String getQubits() {
		String qString = Integer.toBinaryString(qubits);
		return "|" + "0".repeat(numberOfQubits - qString.length()) + qString + ">";
	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point zero.
	 */
	public Complex getState0() {
		if (states.size() > 0) {
			return states.get(0);
		}
		return new Complex();
	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point one.
	 */
	public Complex getState1() {
		if (states.size() > 1) {
			return states.get(1);
		}
		return new Complex();
	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point two.
	 */
	public Complex getState2() {
		if (states.size() > 2) {
			return states.get(2);
		}
		return new Complex();

	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point three.
	 */
	public Complex getState3() {
		if (states.size() > 3) {
			return states.get(3);
		}
		return new Complex();

	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point four.
	 */
	public Complex getState4() {
		if (states.size() > 4) {
			return states.get(4);
		}
		return new Complex();

	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point five.
	 */
	public Complex getState5() {
		if (states.size() > 5) {
			return states.get(5);
		}
		return new Complex();

	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point six.
	 */
	public Complex getState6() {
		if (states.size() > 6) {
			return states.get(6);
		}
		return new Complex();

	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point seven.
	 */
	public Complex getState7() {
		if (states.size() > 7) {
			return states.get(7);
		}
		return new Complex();

	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point eight.
	 */
	public Complex getState8() {
		if (states.size() > 8) {
			return states.get(8);
		}
		return new Complex();

	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point eight.
	 */
	public Complex getState9() {
		if (states.size() > 9) {
			return states.get(9);
		}
		return new Complex();

	}

	/**
	 * Crosstab.
	 * 
	 * @return Get state at point eight.
	 */
	public Complex getState10() {
		if (states.size() > 10) {
			return states.get(10);
		}
		return new Complex();

	}
}
