package controller;

import java.util.List;

import model.Complex;

/**
 * Support file for viewing qubits and their probabilities of measuring to one
 * at any given time. This class will basically cross-tab the results.
 * 
 * @author cdberkstresser
 *
 */
public final class QuantumQubitViewer {
	/**
	 * The qubit number on which we are measuring the probability of collapsing to
	 * one.
	 */
	private final int qubit;
	/** An arraylist of probabilities associated with each state . */
	private final List<Complex> states;

	/**
	 * Constructor.
	 * 
	 * @param qubit  The qubit number.
	 * @param states The probability of collapse to one for each state associated
	 *               with this qubit.
	 */
	public QuantumQubitViewer(final int qubit, final List<Complex> states) {
		this.qubit = qubit;
		this.states = states;
	}

	/**
	 * @return The qubit.
	 */
	public int getQubit() {
		return qubit;
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
