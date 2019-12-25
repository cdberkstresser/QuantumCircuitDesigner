package model;

import java.util.List;

/**
 * This interface provides all the basic quantum gate functionality.
 * 
 * @author cdberkstresser
 *
 */
public interface QuantumGate extends Comparable<QuantumGate> {
	/**
	 * @return A list of wires associated with this gate.
	 */
	List<Integer> getWires();

	/**
	 * @return The gates position horizontally in the circuit.
	 */
	int getGatePosition();

	/**
	 * Sets the gate position horizontally in the circuit.
	 * 
	 * @param value The value to which to set the gate position.
	 */
	void setGatePosition(int value);

	/** @return The gate type. */
	String getGateType();

	/**
	 * Sets the gate type.
	 * 
	 * @param value The value to which to set the gate type.
	 */
	void setGateType(String value);

	/** @return The mathematical matrix associated with this gate. */
	Complex[][] getGateMatrix();

	/** @return The identity matrix. */
	static Complex[][] getIdentityMatrix() {
		return new Complex[][] { { new Complex(1), new Complex(0) }, { new Complex(0), new Complex(1) } };
	}
}
