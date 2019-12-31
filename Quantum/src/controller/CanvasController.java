package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import model.Complex;
import model.ControlledQuantumGate;
import model.ControlledQuantumGateWithParameter;
import model.QuantumCircuit;
import model.QuantumGate;
import model.SingleQuantumGate;
import model.SingleQuantumGateWithParameter;

/**
 * Controller for the main view.
 * 
 * @author cdberkstresser
 *
 */
public final class CanvasController {
	/** height and width of the gates. */
	public static final int GATE_HEIGHT = 50;
	/**
	 * The maximum number of quantum wires we want to potentially support on the
	 * canvas.
	 */
	public static final int MAX_WIRES_AND_STATES = 11;
	/** 3/5 of the gate height. */
	public static final int THREE_FIFTHS_HEIGHT = GATE_HEIGHT * 3 / 5;
	/** length of each wire segment. */
	public static final int WIRE_SEGMENT_WIDTH = 150;

	/**
	 * Main canvas.
	 */
	@FXML
	private Canvas canvas;
	/** Graphics context of the main canvas. */
	private QuantumGraphicsContext graphicsContext;
	/** Quantum circuit backing the drawing on the canvas. */
	private QuantumCircuit qc = new QuantumCircuit();

	/**
	 * Table for displaying the probability that a qubit measures one. Has
	 * numberOfQubits entries.
	 */
	@FXML
	private TableView<QuantumQubitViewer> tbvQubits;
	/** Table for displaying the quantum states. Has 2^(qubits) entries. */
	@FXML
	private TableView<QuantumStateViewer> tbvStates;
	/** Toggle group for choosing which gate to place on the view. */
	@FXML
	private ToggleGroup tgGates;
	/** a list of pending wires for controlled qubits. */
	private List<Integer> wires = new ArrayList<>();
	/** a pending position for controlled qubits. */
	private int position;

	/**
	 * Draw the circuit on the canvas.
	 */
	private void drawCanvas() {
		graphicsContext.clearAll();
		if (qc != null && qc.getWires().size() > 0) {
			for (int wire = 0; wire < qc.getWires().size(); ++wire) {
				graphicsContext.setQubitLabel(qc.getWires().get(wire).getStart().toString(), wire);
				for (int position = 0; position < qc.getMaxWireGatePosition() + 2; ++position) {
					graphicsContext.setNextWireSegment(wire, position);
					if (position == this.position && wires.contains(wire)) {
						graphicsContext.setControlDot(wire, position);
					} else if (qc.getGate(wire, position) == null) {
						graphicsContext.setEmptyGate(wire, position);
					} else {
						String gateType = qc.getGate(wire, position).getGateType();
						// if single simple gate
						if (SingleQuantumGate.getGateTypes().contains(gateType)) {
							graphicsContext.setGateLabel("  " + qc.getGate(wire, position).getGateType(), wire,
									position);
							// if single parameter gate
						} else if (SingleQuantumGateWithParameter.getGateTypes().contains(gateType)) {
							String gateTypeSublabel = "("
									+ ((SingleQuantumGateWithParameter) qc.getGate(wire, position)).getValue() + ")";
							graphicsContext.setGateLabel(gateType, wire, position);
							graphicsContext.setGateSublabel(gateTypeSublabel, wire, position);
							// if controlled gate
						} else if (ControlledQuantumGate.getGateTypes().contains(gateType)) {
							int targetWire = qc.getGate(wire, position).getWires()
									.get(qc.getGate(wire, position).getWires().size() - 1);
							// if control bit
							if (targetWire != wire) {
								graphicsContext.setControlDot(wire, position);
								if (qc.getGate(wire, position).getGateType().contains("0")) {
									graphicsContext.setGateSublabel("(On 0)", wire, position);
								}
							} else { // if not control bit
								if (gateType.contains("NOT")) {
									graphicsContext.setCNOTTargetDot(wire, position);
								} else {
									graphicsContext.setGateLabel("|H|", wire, position);
								}
								graphicsContext.setControlWire(
										qc.getGate(wire, position).getWires().stream().min(Comparator.naturalOrder())
												.get(),
										qc.getGate(wire, position).getWires().stream().max(Comparator.naturalOrder())
												.get(),
										position);

							}
						} else if (ControlledQuantumGateWithParameter.getGateTypes().contains(gateType)) {
							int targetWire = qc.getGate(wire, position).getWires()
									.get(qc.getGate(wire, position).getWires().size() - 1);
							// if control bit
							if (targetWire != wire) {
								graphicsContext.setControlDot(wire, position);
								if (qc.getGate(wire, position).getGateType().contains("0")) {
									graphicsContext.setGateSublabel("(On 0)", wire, position);
								}
							} else { // if not control bit
								graphicsContext.setGateLabel(gateType.replace("C", "").replace("0", ""), wire,
										position);
								String gateTypeSublabel = "("
										+ ((ControlledQuantumGateWithParameter) qc.getGate(wire, position)).getValue()
										+ ")";
								graphicsContext.setGateSublabel(gateTypeSublabel, wire, position);
								graphicsContext.setControlWire(
										qc.getGate(wire, position).getWires().stream().min(Comparator.naturalOrder())
												.get(),
										qc.getGate(wire, position).getWires().stream().max(Comparator.naturalOrder())
												.get(),
										position);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Handles when a user clicks on the canvas.
	 * 
	 * @param event The event source.
	 */
	@FXML
	void handleCanvasClick(final MouseEvent event) {
		int wireSpacing = (int) (canvas.getHeight() / (qc.getWires().size() + 1));
		String gateType = ((RadioMenuItem) tgGates.getSelectedToggle()).getText();
		try {
			if (event.getX() % WIRE_SEGMENT_WIDTH <= GATE_HEIGHT
					&& (event.getY() - 15) % wireSpacing >= wireSpacing - GATE_HEIGHT) {
				int gatePosition = (int) (event.getX() / WIRE_SEGMENT_WIDTH) - 1;
				int wire = (int) ((event.getY() - 15) / wireSpacing);
				if (gatePosition == -1) { // clicked on a qubit
					qc.getWires().get(wire).xStart();
				} else {
					if (SingleQuantumGate.getGateTypes().contains(gateType)) {
						qc.setGate(new SingleQuantumGate(gateType, gatePosition, Arrays.asList(wire)));
					} else if (SingleQuantumGateWithParameter.getGateTypes().contains(gateType)) {
						TextInputDialog angleDialog = new TextInputDialog();
						angleDialog.setTitle("Angle of Rotation");
						// angleDialog.setText("Angle of Rotation");
						angleDialog.setHeaderText("Please enter the angle of the rotation in radians as a decimal.");
						Optional<String> theta = angleDialog.showAndWait();
						try {
							qc.setGate(new SingleQuantumGateWithParameter(gateType, Double.parseDouble(theta.get()),
									gatePosition, Arrays.asList(wire)));
						} catch (Exception e) {

						}
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
							String errorMessage = "That particular gate configuration is not supported!";
							Alert error = new Alert(AlertType.ERROR);
							error.setTitle("Error");
							error.setContentText(errorMessage);
							error.showAndWait();
						}

					} else if (ControlledQuantumGateWithParameter.getGateTypes().contains(gateType)) {
						try {
							wires.add(wire);
							this.position = gatePosition;
							if (wires.size() > QuantumGate.getNumberOfControls(gateType)) {
								TextInputDialog angleDialog = new TextInputDialog();
								angleDialog.setTitle("Angle of Rotation");
								// angleDialog.setText("Angle of Rotation");
								angleDialog.setHeaderText(
										"Please enter the angle of the rotation in radians as a decimal.");
								Optional<String> theta = angleDialog.showAndWait();
								try {
									qc.setGate(new ControlledQuantumGateWithParameter(gateType,
											Double.parseDouble(theta.get()), gatePosition, Arrays.asList(wire)));
								} catch (Exception e) {

								}

							}
						} catch (UnsupportedOperationException err) {
							wires.clear();
							String errorMessage = "That particular gate configuration is not supported!";
							Alert error = new Alert(AlertType.ERROR);
							error.setTitle("Error");
							error.setContentText(errorMessage);
							error.showAndWait();

						}

					}
				}
				rebind();
			}
		} catch (UnsupportedOperationException e) {
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("Error in gate");
			error.setContentText("That gate configuration is not allowed yet!");
			error.showAndWait();
			wires.clear();
		}
	}

	/**
	 * Handles clearing the circuit.
	 * 
	 * @param event The event source.
	 */
	@FXML
	private void handleNew(final ActionEvent event) {
		qc = new QuantumCircuit();
		rebind();
	}

	/**
	 * Handles setting the number of qubits on the canvas.
	 * 
	 * @param event The event source.
	 */
	@FXML
	void handleSetQubits(final ActionEvent event) {
		setNumberOfQubits(Integer.parseInt(((MenuItem) event.getSource()).getText()));
	}

	/**
	 * Rebinds the whole canvas.
	 */
	private void rebind() {
		graphicsContext = new QuantumGraphicsContext(canvas.getGraphicsContext2D(), qc.getWires().size());
		drawCanvas();
		setStatesTable();
		setQubitsTable();
	}

	/**
	 * Sets the number of qubits to draw on the canvas.
	 * 
	 * @param numberOfQubits The number of qubits.
	 */
	private void setNumberOfQubits(final int numberOfQubits) {
		qc.setNumberOfQubits(numberOfQubits);
		rebind();
	}

	private void setQubitsTable() {
		List<QuantumQubitViewer> table = new ArrayList<>();
		tbvQubits.getColumns().clear();

		if (qc.getWires().size() > 0) {
			// add qubits row header
			tbvQubits.getColumns().add(new TableColumn<>("Qubit"));
			tbvQubits.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("qubit"));

			// add state changes for each qubit configuration
			for (int n = 0; n < MAX_WIRES_AND_STATES; ++n) {
				tbvQubits.getColumns().add(new TableColumn<>("State" + n));
				tbvQubits.getColumns().get(n + 1).setCellValueFactory(new PropertyValueFactory<>("state" + n));
			}

			// hide unused wire configurations
			for (int n = qc.getMaxWireGatePosition() + 3; n <= MAX_WIRES_AND_STATES; ++n) {
				tbvQubits.getColumns().get(n).setVisible(false);
			}
			tbvQubits.getColumns().forEach(x -> x.setPrefWidth(WIRE_SEGMENT_WIDTH));

			for (int n = 0; n < qc.getQubitProbabilities(0).size(); ++n) {
				List<Complex> states = new ArrayList<>();
				for (int state = 0; state < MAX_WIRES_AND_STATES; ++state) {
					states.add(qc.getQubitProbabilities(state).get(n));
				}

				table.add(new QuantumQubitViewer(n, states));
			}
			tbvQubits.getItems().setAll(table);
		}
	}

	private void setStatesTable() {
		List<QuantumStateViewer> table = new ArrayList<>();
		tbvStates.getColumns().clear();

		if (qc.getWires().size() > 0) {
			// add qubits row header
			tbvStates.getColumns().add(new TableColumn<>("Qubits"));
			tbvStates.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("qubits"));

			// add state changes for each qubit configuration
			for (int n = 0; n < MAX_WIRES_AND_STATES; ++n) {
				tbvStates.getColumns().add(new TableColumn<>("State" + n));
				tbvStates.getColumns().get(n + 1).setCellValueFactory(new PropertyValueFactory<>("state" + n));
			}

			// hide unused wire configurations
			for (int n = qc.getMaxWireGatePosition() + 3; n <= MAX_WIRES_AND_STATES; ++n) {
				tbvStates.getColumns().get(n).setVisible(false);
			}
			tbvStates.getColumns().forEach(x -> x.setPrefWidth(WIRE_SEGMENT_WIDTH));

			for (int n = 0; n < qc.getState(0).length; ++n) {
				List<Complex> states = new ArrayList<>();
				for (int state = 0; state < MAX_WIRES_AND_STATES; ++state) {
					states.add(qc.getState(state)[n][0]);
				}

				table.add(new QuantumStateViewer(qc.getWires().size(), n, states));
			}
			tbvStates.getItems().setAll(table);
		}
	}

	@FXML
	private void handleFileOpen(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open Quantum Circuit");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"Quantum Circuit Designer Files (*.qcdxml)", "*.qcdxml");
		fc.getExtensionFilters().add(extFilter);
		File file = fc.showOpenDialog(canvas.getScene().getWindow());
		if (file != null) {
			try {
				qc.loadFromXML(new FileInputStream(file));
			} catch (Exception e) {
				Alert error = new Alert(AlertType.ERROR);
				error.setTitle("Invalid file format");
				error.setContentText("That file is not recognized as a quantum circuit designer file!");
				error.showAndWait();
			}
		}
		rebind();
	}

	@FXML
	private void handleFileSave(ActionEvent event) {
		FileChooser fc = new FileChooser();
		fc.setTitle("Open Quantum Circuit");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"Quantum Circuit Designer Files (*.qcdxml)", "*.qcdxml");
		fc.getExtensionFilters().add(extFilter);
		File file = fc.showSaveDialog(canvas.getScene().getWindow());
		if (!file.getName().endsWith(".qcdxml")) {
			file = new File(file.getAbsolutePath() + ".qcdxml");
		}
		try {
			qc.getAsXML(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			Alert error = new Alert(AlertType.ERROR);
			error.setTitle("File not found");
			error.setContentText("File not found!");
			error.showAndWait();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
