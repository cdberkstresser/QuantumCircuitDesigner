package controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.Part;

import model.Complex;
import model.ControlledQuantumGate;
import model.ControlledQuantumGateWithParameter;
import model.QuantumCircuit;
import model.QuantumGate;
import model.SingleQuantumGate;
import model.SingleQuantumGateWithParameter;
import net.bootsfaces.component.canvas.Drawing;

@ManagedBean
@ViewScoped
public class QuantumCircuitService {
	/** font. */
	public static final String FONT = "2em Arial";
	/** font color. */
	public static final String FONT_COLOR = "white";
	/** height and width of the gates. */
	public static final int GATE_HEIGHT = 25;
	/** height of canvas. */
	public static final int HEIGHT = 500;
	/**
	 * The maximum number of quantum wires we want to potentially support on the
	 * canvas.
	 */
	public static final int MAX_WIRES_AND_STATES = 10;
	/** 3/5 of the gate height. */
	public static final int THREE_FIFTHS_HEIGHT = GATE_HEIGHT * 3 / 5;
	/** width of canvas. */
	public static final int WIDTH = 1600;
	/** length of each wire segment. */
	public static final int WIRE_SEGMENT_WIDTH = 184;
	/** Used for reading files into the circuit. */
	private InputStream file;
	/** The gate type used for adding to the circuit. */
	private String gateType = "I";
	/**
	 * The parameter value used for adding a gate to the circuit. Only used with
	 * gates that require a parameter.
	 */
	private double parameterValue;

	/**
	 * The quantum circuit we will be manipulating for the view.
	 */
	private QuantumCircuit qc = new QuantumCircuit();
	/** a list of pending wires for controlled qubits. */
	private List<Integer> wires = new ArrayList<>();
	/** a pending position for the qubit controls. */
	private int position;
	/** The vertical spacing between wires depending on how many wires there are. */
	private int wireSpacing;
	/** A running error message for output to the user. */
	private String errorMessage = "";

	/**
	 * Set up with a new quantum circuit.
	 */
	public void clear() {
		qc = new QuantumCircuit();
	}

	/**
	 * Output the circuit as a file.
	 * 
	 * @throws IOException Exception if crash.
	 */
	public void getCircuitAsFile() throws IOException {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		ec.responseReset();
		ec.setResponseContentType("application/octet-stream");
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"circuit.qcd\"");

		OutputStream output = ec.getResponseOutputStream();
		ObjectOutputStream circuitWriter = new ObjectOutputStream(output);
		circuitWriter.writeObject(qc);

		fc.responseComplete();
	}

	/**
	 * Output the circuit as a file.
	 * 
	 * @throws IOException Exception if crash.
	 */
	public void getStateAsFile() throws IOException {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		ec.responseReset();
		ec.setResponseContentType("application/octet-stream");
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"states.csv\"");

		OutputStream output = ec.getResponseOutputStream();
		PrintWriter csv = new PrintWriter(output);
		csv.println(
				"\"Qubits\",\"State 0\",\"State 1\",\"State 2\",\"State 3\",\"State 4\",\"State 5\",\"State 6\",\"State 7\",\"State 8\"");
		for (QuantumStateViewer qsv : getStatesTable()) {
			csv.println("\"" + qsv.getQubits() + "\"," + qsv.getState0() + "," + qsv.getState1() + "," + qsv.getState2()
					+ "," + qsv.getState3() + "," + qsv.getState4() + "," + qsv.getState5() + "," + qsv.getState6()
					+ "," + qsv.getState7() + "," + qsv.getState8());
		}
		csv.close();
		fc.responseComplete();
	}

	/**
	 * @return The drawing for the canvas in the view.
	 */
	public QuantumDrawing getDrawing() {
		QuantumDrawing canvas = new QuantumDrawing();
		setFillStyle("white", canvas);
		if (qc != null && qc.getWires().size() > 0) {
			for (int wire = 0; wire < qc.getWires().size(); ++wire) {
				setQubitLabel(qc.getWires().get(wire).getStart().toString(), wire, canvas);
				for (int position = 0; position < qc.getMaxWireGatePosition() + 2; ++position) {
					setNextWireSegment(wire, position, canvas);
					if (position == this.position && wires.contains(wire)) {
						setControlDot(wire, position, canvas);
					} else if (qc.getGate(wire, position) == null) {
						setEmptyGate(wire, position, canvas);
					} else {
						String gateType = qc.getGate(wire, position).getGateType();
						// if single simple gate
						if (SingleQuantumGate.getGateTypes().contains(gateType)) {
							setGateLabel(" " + qc.getGate(wire, position).getGateType(), wire, position, canvas);
							// if single parameter gate
						} else if (SingleQuantumGateWithParameter.getGateTypes().contains(gateType)) {
							String gateTypeSublabel = "("
									+ ((SingleQuantumGateWithParameter) qc.getGate(wire, position)).getValue() + ")";
							setGateLabel(gateType, wire, position, canvas);
							setGateSublabel(gateTypeSublabel, wire, position, canvas);
							// if controlled gate
						} else if (ControlledQuantumGate.getGateTypes().contains(gateType)) {
							int targetWire = qc.getGate(wire, position).getWires()
									.get(qc.getGate(wire, position).getWires().size() - 1);
							// if control bit
							if (targetWire != wire) {
								setControlDot(wire, position, canvas);
								if (qc.getGate(wire, position).getGateType().contains("0")) {
									setGateSublabel("(On 0)", wire, position, canvas);
								}
							} else { // if not control bit
								if (gateType.contains("NOT")) {
									setCNOTTargetDot(wire, position, canvas);
								} else {
									setGateLabel("|H|", wire, position, canvas);
								}
								setControlWire(
										qc.getGate(wire, position).getWires().stream().min(Comparator.naturalOrder())
												.get(),
										qc.getGate(wire, position).getWires().stream().max(Comparator.naturalOrder())
												.get(),
										position, canvas);

							}
						} else if (ControlledQuantumGateWithParameter.getGateTypes().contains(gateType)) {
							int targetWire = qc.getGate(wire, position).getWires()
									.get(qc.getGate(wire, position).getWires().size() - 1);
							// if control bit
							if (targetWire != wire) {
								setControlDot(wire, position, canvas);
								if (qc.getGate(wire, position).getGateType().contains("0")) {
									setGateSublabel("(On 0)", wire, position, canvas);
								}
							} else { // if not control bit
								setGateLabel(gateType.replace("C", "").replace("0", ""), wire, position, canvas);
								String gateTypeSublabel = "("
										+ ((ControlledQuantumGateWithParameter) qc.getGate(wire, position)).getValue()
										+ ")";
								setGateSublabel(gateTypeSublabel, wire, position, canvas);
								setControlWire(
										qc.getGate(wire, position).getWires().stream().min(Comparator.naturalOrder())
												.get(),
										qc.getGate(wire, position).getWires().stream().max(Comparator.naturalOrder())
												.get(),
										position, canvas);
							}
						}
					}
				}
			}
		}
		canvas.setToolTip(gateType.equals("I") ? "" : gateType);
		return canvas;
	}

	/**
	 * This is a bit of a hack to set the fill style color to match the theme.
	 * 
	 * @param string The fill style color to set the style to.
	 * @param canvas The graphics context on which to draw the object.
	 */
	private void setFillStyle(final String string, final Drawing canvas) {
		canvas.filledCircle(0, 0, 0, string + "';ctx.strokeStyle='" + string);
	}

	/**
	 * File property for binding with the "Open" menu.
	 * 
	 * @return null;
	 */
	public Part getFile() {
		return null;
	}

	/**
	 * @return Get the quantum circuit associated with this.
	 */
	public QuantumCircuit getQuantumCircuit() {
		return qc;
	}

	/**
	 * @return Get a table for displaying the qubits.
	 */
	public List<QuantumQubitViewer> getQubitsTable() {
		List<QuantumQubitViewer> table = new ArrayList<>();

		if (qc != null && qc.getWires().size() > 0) {
			for (int n = 0; n < qc.getQubitProbabilities(0).size(); ++n) {
				List<Complex> states = new ArrayList<>();
				for (int state = 0; state < MAX_WIRES_AND_STATES; ++state) {
					states.add(qc.getQubitProbabilities(state).get(n));
				}

				table.add(new QuantumQubitViewer(n, states));
			}
		}
		return table;
	}

	/**
	 * @return Get a table for displaying the qubit states.
	 */
	public List<QuantumStateViewer> getStatesTable() {
		List<QuantumStateViewer> table = new ArrayList<>();

		if (qc != null && qc.getWires().size() > 0) {
			for (int n = 0; n < qc.getState(0).length; ++n) {
				List<Complex> states = new ArrayList<>();
				for (int state = 0; state < MAX_WIRES_AND_STATES; ++state) {
					states.add(qc.getState(state)[n][0]);
				}

				table.add(new QuantumStateViewer(qc.getWires().size(), n, states));
			}

		}
		return table;
	}

	/**
	 * Handle when the user clicks on the canvas (to add a gate, usually).
	 * 
	 * @param e Not used.
	 */
	public void handleCanvasClick(final ActionEvent e) {
		double rawX = Double
				.parseDouble(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("x"));
		double rawY = Double
				.parseDouble(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("y"));
		double width = Double.parseDouble(
				FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("width"));

		int x = (int) (rawX * WIDTH / width);
		int y = (int) (rawY * WIDTH / width);
		if ((x + 5) % WIRE_SEGMENT_WIDTH <= 2 * GATE_HEIGHT && (y + 34) % wireSpacing <= 2 * GATE_HEIGHT) {
			int gatePosition = (int) ((x + 5) / WIRE_SEGMENT_WIDTH) - 1;
			int wire = (int) ((y + 34) / wireSpacing - 1);
			if (gatePosition == -1) { // clicked on a qubit
				qc.getWires().get(wire).xStart();
			} else {
				if (SingleQuantumGate.getGateTypes().contains(gateType)) {
					qc.setGate(new SingleQuantumGate(gateType, gatePosition, Arrays.asList(wire)));
				} else if (SingleQuantumGateWithParameter.getGateTypes().contains(gateType)) {
					qc.setGate(new SingleQuantumGateWithParameter(gateType, parameterValue, gatePosition,
							Arrays.asList(wire)));
				} else if (ControlledQuantumGate.getGateTypes().contains(gateType)) {
					try {
						wires.add(wire);
						this.position = gatePosition;
						if (wires.size() > QuantumGate.getNumberOfControls(gateType)) {
							qc.setGate(new ControlledQuantumGate(gateType, gatePosition, new ArrayList<>(wires)));
							wires.clear();
						}
					} catch (UnsupportedOperationException err) {
						wires.clear();
						errorMessage = "That particular gate configuration is not supported!";
					}

				} else if (ControlledQuantumGateWithParameter.getGateTypes().contains(gateType)) {
					try {
						wires.add(wire);
						this.position = gatePosition;
						if (wires.size() > QuantumGate.getNumberOfControls(gateType)) {
							qc.setGate(new ControlledQuantumGateWithParameter(gateType, parameterValue, gatePosition,
									new ArrayList<>(wires)));
							wires.clear();
						}
					} catch (UnsupportedOperationException err) {
						wires.clear();
						errorMessage = "That particular gate configuration is not supported!";
					}

				}
			}
		}

	}

	/**
	 * Open a quantum circuit from file.
	 */
	public void open() {
		qc = new QuantumCircuit();
		try (ObjectInputStream circuitReader = new ObjectInputStream(file)) {
			qc = (QuantumCircuit) circuitReader.readObject();
			wireSpacing = (int) (HEIGHT / (qc.getWires().size() + 1));
		} catch (Exception e) {
			errorMessage = "Error opening file!";
			qc = new QuantumCircuit();
		}
	}

	/**
	 * Provides functionality for loading several pre-configured circuits.
	 * 
	 * @param circuitType The pre-configured circuit name to load.
	 */
	public void setCircuit(final String circuitType) {
		qc = new QuantumCircuit();
		switch (circuitType) {
		case "Plus State":
			setNumberOfQubits(1);
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			break;
		case "Minus State":
			setNumberOfQubits(1);
			qc.getWires().get(0).xStart();
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			break;
		case "SWAP":
			setNumberOfQubits(2);
			qc.getWires().get(0).xStart();
			qc.setGate(new ControlledQuantumGate("CNOT", 0, Arrays.asList(0, 1)));
			qc.setGate(new ControlledQuantumGate("CNOT", 1, Arrays.asList(1, 0)));
			qc.setGate(new ControlledQuantumGate("CNOT", 2, Arrays.asList(0, 1)));
			break;
		case "Bell State 0":
			setNumberOfQubits(2);
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			qc.setGate(new ControlledQuantumGate("CNOT", 1, Arrays.asList(0, 1)));
			break;
		case "Bell State 1":
			setNumberOfQubits(2);
			qc.getWires().get(0).xStart();
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			qc.setGate(new ControlledQuantumGate("CNOT", 1, Arrays.asList(0, 1)));
			break;
		case "Bell State 2":
			setNumberOfQubits(2);
			qc.getWires().get(1).xStart();
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			qc.setGate(new ControlledQuantumGate("CNOT", 1, Arrays.asList(0, 1)));
			break;
		case "Bell State 3":
			setNumberOfQubits(2);
			qc.getWires().get(0).xStart();
			qc.getWires().get(1).xStart();
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			qc.setGate(new ControlledQuantumGate("CNOT", 1, Arrays.asList(0, 1)));
			break;
		case "GHZ State 3":
			setNumberOfQubits(3);
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			for (int position = 1; position < qc.getWires().size(); ++position) {
				qc.setGate(new ControlledQuantumGate("CNOT", position, Arrays.asList(0, position)));
			}
			break;
		case "GHZ State 4":
			setNumberOfQubits(4);
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			for (int position = 1; position < qc.getWires().size(); ++position) {
				qc.setGate(new ControlledQuantumGate("CNOT", position, Arrays.asList(0, position)));
			}
			break;
		case "GHZ State 5":
			setNumberOfQubits(5);
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			for (int position = 1; position < qc.getWires().size(); ++position) {
				qc.setGate(new ControlledQuantumGate("CNOT", position, Arrays.asList(0, position)));
			}
			break;
		case "GHZ State 6":
			setNumberOfQubits(6);
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			for (int position = 1; position < qc.getWires().size(); ++position) {
				qc.setGate(new ControlledQuantumGate("CNOT", position, Arrays.asList(0, position)));
			}
			break;
		case "GHZ State 7":
			setNumberOfQubits(7);
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			for (int position = 1; position < qc.getWires().size(); ++position) {
				qc.setGate(new ControlledQuantumGate("CNOT", position, Arrays.asList(0, position)));
			}
			break;
		case "GHZ State 8":
			setNumberOfQubits(8);
			qc.setGate(new SingleQuantumGate("H", 0, Arrays.asList(0)));
			for (int position = 1; position < qc.getWires().size(); ++position) {
				qc.setGate(new ControlledQuantumGate("CNOT", position, Arrays.asList(0, position)));
			}
			break;
		case "W State 3":
			setNumberOfQubits(3);
			qc.setGate(new SingleQuantumGateWithParameter("Ry", 1.23096, 0, Arrays.asList(0)));
			qc.setGate(new ControlledQuantumGate("C0H", 1, Arrays.asList(0, 1)));
			qc.setGate(new ControlledQuantumGate("CC00NOT", 2, Arrays.asList(0, 1, 2)));
			break;
		case "W State 4":
			setNumberOfQubits(4);
			qc.setGate(new SingleQuantumGateWithParameter("Ry", 1.04712, 0, Arrays.asList(0)));
			qc.setGate(new ControlledQuantumGateWithParameter("C0Ry", 1.23096, 1, Arrays.asList(0, 1)));
			qc.setGate(new ControlledQuantumGate("CC00H", 2, Arrays.asList(0, 1, 2)));
			qc.setGate(new ControlledQuantumGate("CCC000NOT", 3, Arrays.asList(0, 1, 2, 3)));
			break;
		default:
			System.out.println("That circuit type is not available.");
			break;

		}
	}

	/**
	 * Creates a controlled not gate target dot on the canvas.
	 * 
	 * @param wire            The wire on which to draw the gate.
	 * @param position        The position on the wire to draw the gate.
	 * @param graphicsContext The graphics context on which to draw the object.
	 */
	private void setCNOTTargetDot(final int wire, final int position, final Drawing graphicsContext) {
		/*
		 * graphicsContext.text(WIRE_SEGMENT_WIDTH * (position + 1) - 4, (wire + 1) *
		 * wireSpacing + GATE_HEIGHT / 3, "⊕", "50px Arial");
		 */

		graphicsContext.circle(WIRE_SEGMENT_WIDTH * (position + 1) + 3 * GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing - GATE_HEIGHT / 3, GATE_HEIGHT);
		// vertical line
		graphicsContext.line(WIRE_SEGMENT_WIDTH * (position + 1) + 3 * GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing - 4 * GATE_HEIGHT / 3,
				WIRE_SEGMENT_WIDTH * (position + 1) + 3 * GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing + 2 * GATE_HEIGHT / 3);
		// horizontal line
		graphicsContext.line(WIRE_SEGMENT_WIDTH * (position + 1) - GATE_HEIGHT / 4, (wire + 1) * wireSpacing - 10,
				WIRE_SEGMENT_WIDTH * (position + 1) + 7 * GATE_HEIGHT / 4, (wire + 1) * wireSpacing - 10);

	}

	/**
	 * Creates a control dot on the canvas for controlled gates.
	 * 
	 * @param wire            The wire on which to draw the gate.
	 * @param position        The position on the wire to draw the gate.
	 * @param graphicsContext The graphics context on which to draw the object.
	 */
	private void setControlDot(final int wire, final int position, final Drawing graphicsContext) {
		graphicsContext.filledCircle(WIRE_SEGMENT_WIDTH * (position + 1) + 3 * GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing - GATE_HEIGHT / 3, GATE_HEIGHT / 3, FONT_COLOR);
	}

	/**
	 * Draws a wire between two points.
	 * 
	 * @param wire1           The first wire from which to draw the control wire.
	 * @param wire2           The second wire from which to draw the control wire.
	 * @param position        The position on the wires to connect.
	 * @param graphicsContext The graphics context on which to draw the object.
	 */
	public void setControlWire(final int wire1, final int wire2, final int position, final Drawing graphicsContext) {
		graphicsContext.line(WIRE_SEGMENT_WIDTH * (position + 1) + 3 * GATE_HEIGHT / 4,
				(wire1 + 1) * wireSpacing - GATE_HEIGHT / 2, WIRE_SEGMENT_WIDTH * (position + 1) + 3 * GATE_HEIGHT / 4,
				(wire2 + 1) * wireSpacing - GATE_HEIGHT / 2);
	}

	/**
	 * Creates an empty gate on the canvas.
	 * 
	 * @param wire            The wire on which to draw the gate.
	 * @param position        The position on the wire to draw the gate.
	 * @param graphicsContext The graphics context on which to draw the object.
	 */
	private void setEmptyGate(final int wire, final int position, final Drawing graphicsContext) {
		graphicsContext.text(WIRE_SEGMENT_WIDTH * (position + 1), (wire + 1) * wireSpacing, " ☐", FONT);
	}

	/**
	 * Set the file the user wants to open.
	 * 
	 * @param file The file to open.
	 */
	public void setFile(final Part file) {
		try {
			this.file = file.getInputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets a gate label for a given gate.
	 * 
	 * @param gateLabel       The gate label to put on the gate.
	 * @param wire            The wire on which to draw the gate.
	 * @param position        The position on the wire to draw the gate.
	 * @param graphicsContext The graphics context on which to draw the object.
	 */
	private void setGateLabel(final String gateLabel, final int wire, final int position,
			final Drawing graphicsContext) {
		graphicsContext.text(WIRE_SEGMENT_WIDTH * (position + 1), (wire + 1) * wireSpacing, gateLabel, FONT);
	}

	/**
	 * Sets a gate sub-label for a given gate.
	 * 
	 * @param subLabel        The gate label to put on the gate.
	 * @param wire            The wire on which to draw the gate.
	 * @param position        The position on the wire to draw the gate.
	 * @param graphicsContext The graphics context on which to draw the object.
	 */
	public void setGateSublabel(final String subLabel, final int wire, final int position,
			final Drawing graphicsContext) {
		graphicsContext.text(WIRE_SEGMENT_WIDTH * (position + 1) + GATE_HEIGHT / 4,
				(wire + 1) * wireSpacing + THREE_FIFTHS_HEIGHT + GATE_HEIGHT / 4, subLabel, FONT);
	}

	/**
	 * Set the gate type and the parameter for adding to the gate.
	 * 
	 * @param e Not used.
	 */
	public void setGateType(final ActionEvent e) {
		gateType = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("gateType");
		parameterValue = Double.parseDouble(
				FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("value"));
		wires.clear();
	}

	/**
	 * Creates a wire segment going from this wire/position on down the score.
	 * 
	 * @param wire            The wire on which to draw the gate.
	 * @param position        The position on the wire to draw the gate.
	 * @param graphicsContext The graphics context on which to draw the object.
	 */
	private void setNextWireSegment(final int wire, final int position, final Drawing graphicsContext) {
		graphicsContext.line(GATE_HEIGHT + WIRE_SEGMENT_WIDTH * position + GATE_HEIGHT, (wire + 1) * wireSpacing - 10,
				WIRE_SEGMENT_WIDTH * (position + 1) - GATE_HEIGHT / 2, (wire + 1) * wireSpacing - 10);
	}

	/**
	 * Sets the number of qubits in the circuit.
	 * 
	 * @param numberOfQubits The number of qubits to set.
	 */
	public void setNumberOfQubits(final int numberOfQubits) {
		qc.setNumberOfQubits(numberOfQubits);
		wireSpacing = (int) (HEIGHT / (qc.getWires().size() + 1));
	}

	/**
	 * Sets the circuit.
	 * 
	 * @param quantumCircuit The circuit to set.
	 */
	public void setQuantumCircuit(final QuantumCircuit quantumCircuit) {
		this.qc = quantumCircuit;
	}

	/**
	 * Creates a controlled not gate target dot on the canvas.
	 * 
	 * @param qubitLabel      The label for the qubit to draw.
	 * @param wire            The wire on which to draw the gate.
	 * @param graphicsContext The graphics context on which to draw the object.
	 */
	private void setQubitLabel(final String qubitLabel, final int wire, final Drawing graphicsContext) {
		graphicsContext.text(0, (wire + 1) * wireSpacing, qubitLabel, FONT);
	}

	/**
	 * @return A running status bar of instructions to the user.
	 */
	public String getStatusTip() {
		String returnValue;
		if (qc.getWires().size() == 0) {
			returnValue = "First, add qubits to your circuit from the Qubits menu";
		} else if (gateType.startsWith("C")) {
			if (wires.size() < QuantumGate.getNumberOfControls(gateType)) {
				returnValue = "Select an empty gate position for the "
						+ (wires.size() == 0 ? "first" : (wires.size() == 1 ? "second" : "third")) + " control.";
			} else {
				returnValue = "Select an empty gate position for the " + gateType + " target.";
			}
		} else if (qc.getGates().size() == 0) {
			if (gateType.equals("I")) {
				returnValue = "Select a gate from the menu and then click on an empty gate position to add it to the circuit.";
			} else {
				returnValue = "Choose an empty gate position on the circuit on which to place your " + gateType
						+ " gate or choose a different gate type.";
			}
		} else {
			if (gateType.equals("I")) {
				returnValue = "I gates are just identities but can be used to remove an existing gate.";
			} else {
				returnValue = "Choose an empty gate position on the circuit on which to place your " + gateType
						+ " gate or choose a different gate type.";
			}
		}
		errorMessage = "";
		return returnValue;
	}

	/**
	 * @return Any error messages associated with the previous state.
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return The number of qubits in this circuit.
	 */
	public int getNumberOfQubits() {
		return qc.getWires().size();
	}
}
