package qke;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Main presentation of the Quantum Key Exchange.
 * 
 * @author cberkstresser
 *
 */
public final class Main {
	/** Constructor. */
	private Main() {
	};

	/**
	 * Main entry point of the program.
	 * 
	 * @param args
	 *            Command line arguments.
	 * @throws NoSuchAlgorithmException
	 *             Key encryption/decryption error class.
	 * @throws NoSuchPaddingException
	 *             Key encryption/decryption error class.
	 * @throws InvalidKeyException
	 *             Key encryption/decryption error class.
	 * @throws IllegalBlockSizeException
	 *             Key encryption/decryption error class.
	 * @throws BadPaddingException
	 *             Key encryption/decryption error class.
	 */
	public static void main(final String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Scanner keyboard = new Scanner(System.in);
		int keyLength;
		System.out.println("This program simulates a quantum key exchange.");
		System.out.println("How many bits would you like the initial key to be: ");
		keyLength = keyboard.nextInt();
		System.out.println("Alice is going to send a proposal to Bob.\n" + "This means Alice will generate " + keyLength
				+ " random bits\nand " + keyLength + " random polarizations to create " + keyLength
				+ " random qubits to send to Bob.\n"
				+ "Bob will not know the polarizations in which the qubits were encoded when he measures them.");

		QuantumKey qkAlice = new QuantumKey(keyLength);
		QuantumKey qkBob = new QuantumKey(keyLength);
		QuantumKey qkEve = new QuantumKey(keyLength);

		System.out.println("Alice has finished generating qubits and is going to send them to Bob.  "
				+ "Would you like Eve to intercept the message en route? (y/n)");
		char eve = keyboard.next().toLowerCase().charAt(0);
		if (eve == 'y') {
			qkBob.receiveKey(qkEve.receiveKey(qkAlice.generateAndSendKey()));

			System.out.println("Keys have been send/received as: ");
			System.out.println("\nAlice:\n" + qkAlice);
			System.out.println("\nEve:\n" + qkEve);
			System.out.println("\nBob:\n" + qkBob);
		} else {
			qkBob.receiveKey(qkAlice.generateAndSendKey());

			System.out.println("Keys have been send/received as: ");
			System.out.println("\nAlice:\n" + qkAlice);
			System.out.println("\nBob:\n" + qkBob);
		}
		keyboard.nextLine();
		System.out.println("Press [Enter] to continue...");
		keyboard.nextLine();
		System.out.println("We're now going to send polarizations between Alice and Bob.\n"
				+ "This transmission could be assumed to be observed.");
		qkAlice.receiveAndAgreeOnPolarizations(qkBob.sendPolarizations());
		qkBob.receiveAndAgreeOnPolarizations(qkAlice.sendPolarizations());
		System.out.println("Press [Enter] to continue...");
		keyboard.nextLine();
		System.out.println("The polarizations tell us which bits should match in our measurements.\n"
				+ "We can now agree on the bits refered to by the positions of the polarizations on which we agree.");

		System.out.println("\nAlice:\n" + Arrays.toString(qkAlice.getAgreedKey()));
		System.out.println("\nBob:\n" + Arrays.toString(qkBob.getAgreedKey()));
		System.out.println("Press [Enter] to continue...");
		keyboard.nextLine();

		System.out.println("Please enter a message for Alice to send to Bob.");
		String message = keyboard.nextLine();

		try {
			System.out.println(
					"Bob receives Alice's message as: " + qkBob.decodeMessage(qkAlice.encodeAndSendMessage(message)));
		} catch (InvalidParameterException e) {
			System.err.println("\n" + e.getMessage() + "\n");
		}

		System.out.println("Please enter a message for Bob to send back to Alice.");
		message = keyboard.nextLine();

		try {
			System.out.println(
					"Alice receives Bob's message as: " + qkAlice.decodeMessage(qkBob.encodeAndSendMessage(message)));
		} catch (InvalidParameterException e) {
			System.err.println("\n" + e.getMessage() + "\n");
		}
		System.out.println("End of program.");
		keyboard.close();
	}
}
