package qke;
/**
 * This class represents the polarized classical bits in BB84 key exchange.
 * 
 * @author cberkstresser
 *
 */
public class Qubit {
	/** Constant used for superpositions created using a Hadamard date. */
	public static final double ONE_OVER_ROOT_TWO = 1 / Math.sqrt(2);
	/** The qubit vector respresentations of our polarized qubits. */
	private double[] stateVector = new double[2];
	/**
	 * Is the measurement going to be taken in a polarized or non-polarized system?
	 */
	private boolean polarized;

	/**
	 * Constructor.
	 * 
	 * @param pValue
	 *            The classical bit value to encode in the qubit.
	 * @param pPolarized
	 *            Should we encode pValue in a classical basis or superimposed
	 *            basis?
	 */
	public Qubit(final int pValue, final boolean pPolarized) {
		polarized = pPolarized;
		stateVector[0] = 1 - pValue;
		stateVector[1] = pValue;
		if (polarized) {
			stateVector = applyHadamard(stateVector);
		}
	}

	/**
	 * Take a measurement of the qubit in either a polarized or non-polarized basis.
	 * 
	 * @param pPolarized
	 *            The basis in which to measure the qubit.
	 * @return The classical 1 or 0 value of the measurement.
	 */
	public int takeMeasurement(final boolean pPolarized) {
		if (pPolarized && polarized) {
			return (int) Math.round(applyHadamard(stateVector)[1]);
		} else if (!pPolarized && !polarized) {
			return (int) stateVector[1];
		} else if (polarized && !pPolarized) {
			polarized = pPolarized;
			stateVector[1] = (int) (Math.random() * 2);
			stateVector[0] = 1 - stateVector[1];
			return takeMeasurement(pPolarized);
		} else {
			polarized = pPolarized;
			stateVector[1] = (int) (Math.random() * 2);
			stateVector[0] = 1 - stateVector[1];
			stateVector = applyHadamard(stateVector);
			return takeMeasurement(pPolarized);
		}
	}

	/**
	 * This is the typical hadamard gate.
	 * 
	 * @param initialVector
	 *            The initial vector to which to apply the hadamard operation.
	 * @return The vector after the hadamard transform has been applied.
	 */
	private double[] applyHadamard(final double[] initialVector) {
		double[] result = new double[2];
		result[0] = ONE_OVER_ROOT_TWO * (initialVector[0] + initialVector[1]);
		result[1] = ONE_OVER_ROOT_TWO * (initialVector[0] - initialVector[1]);
		return result;
	}
}
