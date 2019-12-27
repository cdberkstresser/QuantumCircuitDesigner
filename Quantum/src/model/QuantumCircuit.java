package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The main Quantum Circuit class for processing quantum circuits.
 * 
 * @author cdberkstresser
 *
 */
public class QuantumCircuit implements Serializable {
	/** Generated Serializable ID. */
	private static final long serialVersionUID = 8646269267743668885L;

	/**
	 * Multiply two complex gates.
	 * 
	 * @param matrix1 The first matrix to multiply.
	 * @param matrix2 The second matrix to multiply.
	 * @return The matrix product of the two.
	 */
	public static Complex[][] multiply(final Complex[][] matrix1, final Complex[][] matrix2) {
		Complex[][] result = new Complex[matrix1.length][matrix2[0].length];
		for (int answerRow = 0; answerRow < matrix1.length; ++answerRow) {
			for (int answerColumn = 0; answerColumn < matrix2[0].length; ++answerColumn) {
				Complex sum = new Complex();
				for (int rowColumnwalker = 0; rowColumnwalker < matrix1[0].length; ++rowColumnwalker) {
					sum = sum.add(matrix1[answerRow][rowColumnwalker].multiply(matrix2[rowColumnwalker][answerColumn]));
				}
				result[answerRow][answerColumn] = sum;
			}
		}
		return result;
	}

	/**
	 * @param state1 The first state on which to conduct the tensor product.
	 * @param state2 The second state on which to conduct the tensor product.
	 * @return The tensor product of the states.
	 */
	public static Complex[][] tensor(final Complex[][] state1, final Complex[][] state2) {
		Complex[][] result = new Complex[state1.length * state2.length][state1[0].length * state2[0].length];
		for (int rowMe = 0; rowMe < state1.length; ++rowMe) {
			for (int columnMe = 0; columnMe < state1[0].length; ++columnMe) {
				for (int row2 = 0; row2 < state2.length; ++row2) {
					for (int column2 = 0; column2 < state2[0].length; ++column2) {
						int destinationRow = row2 + state2.length * rowMe;
						int destinationColumn = column2 + state2.length * columnMe;
						result[destinationRow][destinationColumn] = state1[rowMe][columnMe]
								.multiply(state2[row2][column2]);
					}
				}
			}
		}
		return result;
	}

	/** The list of quantum gates associated with this circuit. */
	private List<QuantumGate> gates = new ArrayList<>();

	/** The list of quantum wires associated with this circuit. */
	private List<QuantumWire> wires = new ArrayList<>();

	private Map<Integer, List<Complex>> stateTransposeCache = new HashMap<>();

	/**
	 * Adds a wire to the circuit.
	 * 
	 * @param wire The wire to add.
	 */
	public void addWire(final QuantumWire wire) {
		wires.add(wire);
		stateTransposeCache.clear();
	}

	/**
	 * Adds a wire to the circuit.
	 */
	public void addWire() {
		addWire(new QuantumWire());
		stateTransposeCache.clear();
	}

	/**
	 * @param wire     The wire index to get.
	 * @param position The position index to get.
	 * @return a gate from the circuit by wire and position.
	 */
	public QuantumGate getGate(final int wire, final int position) {
		return gates.stream().filter(x -> x.getGatePosition() == position && x.getWires().contains(wire)).findAny()
				.orElse(null);
	}

	/**
	 * @return A list of all gates from the circuit.
	 */
	public List<QuantumGate> getGates() {
		return gates;
	}

	/**
	 * @return The initial values of each wire in the circuit.
	 */
	public List<Qubit> getInitialValues() {
		return wires.stream().map(x -> x.getStart()).collect(Collectors.toList());
	}

	/**
	 * @return The last position of the longest wire in the circuit.
	 */
	public int getMaxWireGatePosition() {
		return gates.stream().map(x -> x.getGatePosition()).max(Comparator.naturalOrder()).orElse(-1);
	}

	/**
	 * @param afterIndex The index position of the state to get. Calculates the
	 *                   state after all gates at that index position have ran.
	 * @return A list of complex numbers associated with the probability of a qubit
	 *         measuring one.
	 */
	public List<Complex> getQubitProbabilities(final int afterIndex) {
		List<Complex> returnValue = new ArrayList<>();
		Complex[][] state = getState(afterIndex);

		for (int wire = wires.size() - 1; wire >= 0; --wire) {
			double runningProbability = 0.0;
			for (int row = 0; row < state.length; ++row) {
				if ((row & (int) Math.pow(2, wire)) != 0) {
					runningProbability += Math.pow(state[row][0].modulus(), 2);
				}
			}
			returnValue.add(new Complex(runningProbability));
		}
		return returnValue;
	}

	/**
	 * @param afterIndex The index position of the state to get. Calculates the
	 *                   state after all gates at that index position have ran.
	 * @return The state of the circuit at any index position.
	 */
	public Complex[][] getState(final int afterIndex) {
		Complex[][] gateMatrix = { { new Complex(1) } };
		if (wires.stream().filter(x -> x.isDirty()).count() > 0) {
			stateTransposeCache.clear();
			wires.forEach(x -> x.resetDirty());
		}
		if (stateTransposeCache.containsKey(afterIndex)) {
			return stateFromCache(stateTransposeCache.get(afterIndex));
		} else {
			if (afterIndex == 0) { // afterIndex0 refers to the gates themselves
				for (int n = 0; n < wires.size(); ++n) {
					gateMatrix = tensor(gateMatrix, wires.get(n).getInitialValue().getState());
				}
			} else { // afterIndex1 refers to gates on gate position zero, etc.
				for (int n = 0; n < wires.size();) {
					final int wirePosition = n;
					List<QuantumGate> thisStateGate = gates.stream().filter(x -> x.getGatePosition() == afterIndex - 1)
							.filter(x -> x.getWires().contains(wirePosition)).collect(Collectors.toList());
					if (thisStateGate.size() > 0) {
						gateMatrix = tensor(gateMatrix, thisStateGate.get(0).getGateMatrix());
						n += Math.round(Math.log(thisStateGate.get(0).getGateMatrix().length) / Math.log(2));
					} else {
						gateMatrix = tensor(gateMatrix, QuantumGate.getIdentityMatrix());
						n++;
					}
				}
				gateMatrix = multiply(gateMatrix, getState(afterIndex - 1));
			}
			stateTransposeCache.put(afterIndex, stateToCache(gateMatrix));
			return gateMatrix;
		}
	}

	private List<Complex> stateToCache(Complex[][] state) {
		List<Complex> returnValue = new ArrayList<>(state.length);
		for (int n = 0; n < state.length; ++n) {
			returnValue.add(state[n][0]);
		}
		return returnValue;
	}

	private Complex[][] stateFromCache(List<Complex> state) {
		Complex[][] returnValue = new Complex[state.size()][1];
		for (int n = 0; n < state.size(); ++n) {
			returnValue[n][0] = state.get(n);
		}
		return returnValue;
	}

	/**
	 * @return The list of wires involved in this circuit.
	 */
	public List<QuantumWire> getWires() {
		return wires;
	}

	/**
	 * Removes the last wire from the circuit.
	 */
	public void removeLastWire() {
		if (wires.size() > 0) {
			wires.remove(wires.size() - 1);
		}
		gates.removeIf(x -> x.getWires().contains(wires.size()));
		stateTransposeCache.clear();
	}

	/**
	 * Sets a gate to the circuit. Will remove any conflicting gates at that
	 * position.
	 * 
	 * @param gate The new gate to set.
	 */
	public void setGate(final QuantumGate gate) {
		int maxStateCached = stateTransposeCache.keySet().stream().max(Comparator.naturalOrder()).orElse(0);
		for (int state = gate.getGatePosition(); state <= maxStateCached; ++state) {
			stateTransposeCache.remove(state);
		}
		gates.removeIf(x -> x.getGatePosition() == gate.getGatePosition() && x.getWires().containsAll(gate.getWires()));
		try {
			gate.getGateMatrix();
			if (!gate.getGateType().equals("I") && gate.getGatePosition() < getMaxWireGatePosition() + 2) {
				this.gates.add(gate);
			}
		} catch (UnsupportedOperationException e) {
			throw e;
		}

	}

	public void setNumberOfQubits(int numberOfQubits) {
		while (wires.size() < numberOfQubits) {
			addWire(new QuantumWire());
		}
		while (wires.size() > numberOfQubits) {
			removeLastWire();
		}
	}
}
