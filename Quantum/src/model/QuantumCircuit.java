package model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	private final List<QuantumGate> gates = new ArrayList<>();

	/**
	 * Cache of states for speed.
	 */
	private final Map<Integer, List<Complex>> stateTransposeCache = new HashMap<>();

	/** The list of quantum wires associated with this circuit. */
	private final List<QuantumWire> wires = new ArrayList<>();

	/**
	 * Adds a wire to the circuit.
	 */
	public void addWire() {
		addWire(new QuantumWire());
		stateTransposeCache.clear();
	}

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
	 * Algorithm to decide if two gates collide.
	 * 
	 * @param gate1 The first gate to check if it collides with gate2.
	 * @param gate2 The second gate to check if it collides with gate1.
	 * @return True if the gates collide or take competing positions.
	 */
	public boolean gatesCollide(final QuantumGate gate1, final QuantumGate gate2) {
		if (gate1.getGatePosition() == gate2.getGatePosition()) {
			int gate1MinWire = gate1.getWires().stream().min(Comparator.naturalOrder()).get();
			int gate1MaxWire = gate1.getWires().stream().max(Comparator.naturalOrder()).get();
			int gate2MinWire = gate2.getWires().stream().min(Comparator.naturalOrder()).get();
			int gate2MaxWire = gate2.getWires().stream().max(Comparator.naturalOrder()).get();

			return gate1MaxWire >= gate2MinWire && gate2MaxWire >= gate1MinWire;
		}
		return false;
	}

	/**
	 * Gets the circuit as an xml file and puts it on the output stream.
	 * 
	 * @param output The output stream in which to load the xml file.
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public void getAsXML(final OutputStream output) throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		// build the document
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElementNS("https://cberkstresser.name/QuantumWeb", "Circuit");
		doc.appendChild(rootElement);

		Element meta = doc.createElement("Meta");
		meta.setAttribute("date", ZonedDateTime.now().toString());
		rootElement.appendChild(meta);

		Element initialState = doc.createElement("InitialState");
		for (int n = 0; n < getWires().size(); ++n) {
			Element qubit = doc.createElement("Qubit");
			qubit.setAttribute("wire", String.valueOf(n));
			qubit.setAttribute("xR", String.valueOf(getWires().get(n).getInitialValue().getX().getReal()));
			qubit.setAttribute("xI", String.valueOf(getWires().get(n).getInitialValue().getX().getImaginary()));
			qubit.setAttribute("yR", String.valueOf(getWires().get(n).getInitialValue().getY().getReal()));
			qubit.setAttribute("yI", String.valueOf(getWires().get(n).getInitialValue().getY().getImaginary()));
			initialState.appendChild(qubit);
		}
		rootElement.appendChild(initialState);

		Element gates = doc.createElement("Gates");
		for (QuantumGate g : getGates()) {
			Element gate = doc.createElement("Gate");
			gate.setAttribute("position", String.valueOf(g.getGatePosition()));
			gate.setAttribute("gateType", g.getGateType());
			if (g instanceof SingleQuantumGateWithParameter) {
				gate.setAttribute("parameterValue", String.valueOf(((SingleQuantumGateWithParameter) g).getValue()));
			} else if (g instanceof ControlledQuantumGateWithParameter) {
				gate.setAttribute("parameterValue",
						String.valueOf(((ControlledQuantumGateWithParameter) g).getValue()));
			} else {
				gate.setAttribute("parameterValue", "0.0");
			}

			for (int w : g.getWires()) {
				Element wire = doc.createElement("Wire");
				wire.setTextContent(String.valueOf(w));
				gate.appendChild(wire);
			}
			gates.appendChild(gate);
		}
		rootElement.appendChild(gates);

		// set up a transformer for dumping it to an output stream.
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		DOMSource source = new DOMSource(doc);

		StreamResult result = new StreamResult(output);
		transformer.transform(source, result);
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
		return wires.stream().map(QuantumWire::getStart).collect(Collectors.toList());
	}

	/**
	 * @return The last position of the longest wire in the circuit.
	 */
	public int getMaxWireGatePosition() {
		return gates.stream().map(QuantumGate::getGatePosition).max(Comparator.naturalOrder()).orElse(-1);
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
		if (wires.stream().anyMatch(QuantumWire::isDirty)) {
			stateTransposeCache.clear();
			wires.forEach(QuantumWire::resetDirty);
		}
		if (stateTransposeCache.containsKey(afterIndex)) {
			return stateFromCache(stateTransposeCache.get(afterIndex));
		} else {
			if (afterIndex == 0) { // afterIndex0 refers to the gates themselves
				for (QuantumWire wire : wires) {
					gateMatrix = tensor(gateMatrix, wire.getInitialValue().getState());
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

	/**
	 * @return The list of wires involved in this circuit.
	 */
	public List<QuantumWire> getWires() {
		return wires;
	}

	/**
	 * Loads the circuit from an xml file in an input stream.
	 * 
	 * @param xml The xml input stream to load.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void loadFromXML(final InputStream xml) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xml);
		gates.clear();
		wires.clear();

		NodeList qubits = doc.getElementsByTagName("Qubit");
		for (int n = 0; n < qubits.getLength(); ++n) {
			Element qubit = (Element) qubits.item(n);
			if (Integer.parseInt(qubit.getAttributes().getNamedItem("wire").getTextContent()) == n) {
				double xR = Double.parseDouble(qubit.getAttributes().getNamedItem("xR").getTextContent());
				double xI = Double.parseDouble(qubit.getAttributes().getNamedItem("xI").getTextContent());
				double yR = Double.parseDouble(qubit.getAttributes().getNamedItem("yR").getTextContent());
				double yI = Double.parseDouble(qubit.getAttributes().getNamedItem("yI").getTextContent());
				addWire(new QuantumWire(new Complex(xR, xI), new Complex(yR, yI)));
			} else {
				throw new IllegalArgumentException("The file format is corrupted!");
			}
		}
		NodeList gates = doc.getElementsByTagName("Gate");
		for (int n = 0; n < gates.getLength(); ++n) {
			Element gate = (Element) gates.item(n);
			String gateType = gate.getAttributes().getNamedItem("gateType").getTextContent();
			double parameterValue = Double
					.parseDouble(gate.getAttributes().getNamedItem("parameterValue").getTextContent());
			int position = Integer.parseInt(gate.getAttributes().getNamedItem("position").getTextContent());
			List<Integer> wires = new ArrayList<>();
			NodeList wireList = gate.getElementsByTagName("Wire");
			for (int w = 0; w < wireList.getLength(); ++w) {
				wires.add(Integer.parseInt(wireList.item(w).getTextContent()));
			}
			if (SingleQuantumGate.getGateTypes().contains(gateType)) {
				setGate(new SingleQuantumGate(gateType, position, wires));
			} else if (ControlledQuantumGate.getGateTypes().contains(gateType)) {
				setGate(new ControlledQuantumGate(gateType, position, wires));
			} else if (SingleQuantumGateWithParameter.getGateTypes().contains(gateType)) {
				setGate(new SingleQuantumGateWithParameter(gateType, parameterValue, position, wires));
			} else if (ControlledQuantumGateWithParameter.getGateTypes().contains(gateType)) {
				setGate(new ControlledQuantumGateWithParameter(gateType, parameterValue, position, wires));
			}
		}

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

		gates.removeIf(x -> gatesCollide(x, gate));

		gate.getGateMatrix();
		if (!gate.getGateType().equals("I") && gate.getGatePosition() < getMaxWireGatePosition() + 2) {
			this.gates.add(gate);
		}

	}

	/**
	 * Sets the number of qubits and preserves existing ones if possible.
	 * 
	 * @param numberOfQubits The number of qubits for the circuit to have.
	 */
	public void setNumberOfQubits(final int numberOfQubits) {
		while (wires.size() < numberOfQubits) {
			addWire(new QuantumWire());
		}
		while (wires.size() > numberOfQubits) {
			removeLastWire();
		}
	}

	/**
	 * Get a 2d array of the state from cache.
	 * 
	 * @param state The state to format from caching.
	 * @return Formatted state array.
	 */
	private Complex[][] stateFromCache(final List<Complex> state) {
		Complex[][] returnValue = new Complex[state.size()][1];
		for (int n = 0; n < state.size(); ++n) {
			returnValue[n][0] = state.get(n);
		}
		return returnValue;
	}

	/**
	 * Get an arraylist of the state to put to cache.
	 * 
	 * @param state The state to format for caching.
	 * @return Formatted state list.
	 */
	private List<Complex> stateToCache(final Complex[][] state) {
		List<Complex> returnValue = new ArrayList<>(state.length);
		for (Complex[] complexes : state) {
			returnValue.add(complexes[0]);
		}
		return returnValue;
	}
}
