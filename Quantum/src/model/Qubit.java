package model;

import java.io.Serializable;

/**
 * A Qubit in a circuit.
 * 
 * @author cdberkstresser
 *
 */
public final class Qubit implements Serializable {
	/** Serializable ID. */
	private static final long serialVersionUID = 6049197754054542798L;
	/** The first value of the matrix for this qubit. */
	private Complex x;
	/** The second value of the matrix for this qubit. */
	private Complex y;

	/**
	 * Constructor. Defaults to |0>.
	 */
	public Qubit() {
		this(0);
	}

	/**
	 * Constructor.
	 * 
	 * @param x The x value of this qubit.
	 * @param y The x value of this qubit.
	 */
	public Qubit(final Complex x, final Complex y) {
		this.x = x.clone();
		this.y = y.clone();
	}

	/**
	 * Constructor.
	 * 
	 * @param value The value of this qubit.
	 */
	public Qubit(final int value) {
		if (value == 0) {
			x = new Complex(1);
			y = new Complex(0);
		} else if (value == 1) {
			x = new Complex(0);
			y = new Complex(1);
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Qubit other = (Qubit) obj;
		if (x == null) {
			if (other.x != null) {
				return false;
			}
		} else if (!x.equals(other.x)) {
			return false;
		}
		if (y == null) {
			return other.y == null;
		} else return y.equals(other.y);
	}

	/**
	 * @return The ket of this qubit.
	 */
	public Complex[][] getState() {
		return new Complex[][] { { x }, { y } };
	}

	/**
	 * @return The first value of the matrix for this qubit.
	 */
	public Complex getX() {
		return x;
	}

	/**
	 * @return The second value of the matrix for this qubit.
	 */
	public Complex getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public String toString() {
		if (x.equals(new Complex(1))) {
			return "|0>";
		} else {
			return "|1>";
		}
	}
}
