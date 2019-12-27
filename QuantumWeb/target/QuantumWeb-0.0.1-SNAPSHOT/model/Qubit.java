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
	public String toString() {
		if (x.equals(new Complex(1))) {
			return "|0>";
		} else {
			return "|1>";
		}
	}

	/**
	 * @return The ket of this qubit.
	 */
	public Complex[][] getState() {
		return new Complex[][] { { x }, { y } };
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
			if (other.y != null) {
				return false;
			}
		} else if (!y.equals(other.y)) {
			return false;
		}
		return true;
	}
}
