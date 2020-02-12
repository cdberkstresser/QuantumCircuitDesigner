package model;

import java.io.Serializable;
import java.util.List;

/**
 * This interface provides all the basic quantum gate functionality.
 * 
 * @author cdberkstresser
 *
 */
public interface QuantumGate extends Comparable<QuantumGate>, Serializable {
	
	/**
	 * @return A list of wires associated with this gate.
	 */
	List<Integer> getWires();

	/**
	 * @return The gates position horizontally in the circuit.
	 */
	int getGatePosition();

	/** @return The gate type. */
	String getGateType();

	/** @return The mathematical matrix associated with this gate. */
	Complex[][] getGateMatrix();

	/** @return The identity matrix. */
	static Complex[][] getIdentityMatrix() {
		return new Complex[][] { { new Complex(1), new Complex(0) }, { new Complex(0), new Complex(1) } };
	}

	static int getNumberOfControls(String gateType) {
		return gateType.replaceAll("[^C]+", "").length();
	}
}
