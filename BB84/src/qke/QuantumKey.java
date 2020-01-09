package qke;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class represents a quantum key used for key exchange between two (or
 * more) instances of this class.
 * 
 * @author cberkstresser
 *
 */
public class QuantumKey {
	/**
	 * Flag set to true when two instances of QuantumKey share a key and agree on
	 * common polarizations.
	 */
	private boolean agreedOnCommonPolarizations;
	/**
	 * An array of indices of the initial polarizations that match the other
	 * sender/receiver.
	 */
	private List<Integer> agreedPolarizationIndices = new ArrayList<>();
	/**
	 * Flag set to true when classical bits are initialized in the key. This can
	 * occur when the current instance of the class generates a key to send or when
	 * a key is received.
	 */
	private boolean bitsInitialized;
	/**
	 * These are initial qubit vectors initialized when polarizations and classical
	 * bits are known.
	 */
	private Qubit[] initialQubits;
	/**
	 * These are classical bit values that will be encoded in the qubits when
	 * polarizations are known.
	 */
	private int[] initialClassicalBits;
	/**
	 * Polarizations as a basis for taking measurements on classical bits.
	 */
	private boolean[] polarizations;

	/**
	 * Constructor.
	 * 
	 * @param pSizeOfInitialExchange
	 *            How many bits with which to start negotiating.
	 */
	public QuantumKey(final int pSizeOfInitialExchange) {
		initialClassicalBits = new int[pSizeOfInitialExchange];
		polarizations = new boolean[pSizeOfInitialExchange];
		for (int n = 0; n < pSizeOfInitialExchange; ++n) {
			polarizations[n] = (int) (Math.random() * 2) == 0 ? false : true;
		}
		initialQubits = new Qubit[pSizeOfInitialExchange];
	}

	/**
	 * Generate and send a key of qubits to the recipient. First step of key
	 * exchange.
	 * 
	 * @return A qubit array of qubits to send to the recipient.
	 */
	public Qubit[] generateAndSendKey() {
		bitsInitialized = true;
		for (int n = 0; n < initialQubits.length; ++n) {
			initialClassicalBits[n] = (int) (Math.random() * 2);
			initialQubits[n] = new Qubit(initialClassicalBits[n], polarizations[n]);
		}
		return initialQubits;
	}

	/**
	 * Get the agreed key of classical bits. These are the bits that we measured in
	 * the same polarization. This would be called after
	 * receiveAndAgreeOnPolarizations()
	 * 
	 * @return The agreed key.
	 */
	public int[] getAgreedKey() {
		int[] key = new int[agreedPolarizationIndices.size()];
		for (int n = 0; n < key.length; ++n) {
			key[n] = initialClassicalBits[agreedPolarizationIndices.get(n)];
		}
		return key;
	}

	/**
	 * Get the polarizations of the sender of the key. We will use these values to
	 * determine an agreed key of classical bits.
	 * 
	 * @param pPolarizations
	 *            The polarizations used to send the message to an instance of this
	 *            class.
	 * @return An array of indices in which we both measured by the same
	 *         polarization.
	 */
	public boolean[] receiveAndAgreeOnPolarizations(final boolean[] pPolarizations) {
		agreedOnCommonPolarizations = true;
		for (int n = 0; n < polarizations.length; ++n) {
			if (pPolarizations[n] == polarizations[n]) {
				agreedPolarizationIndices.add(n);
			}
		}
		return pPolarizations;
	}

	/**
	 * Get a key proposal of qubits from someone wishing to agree upon a key of
	 * classical bits.
	 * 
	 * @param receivedKey
	 *            The qubits the sender wants us to measure.
	 * @return The qubits I measure in my own random polarizations.
	 */
	public Qubit[] receiveKey(final Qubit[] receivedKey) {
		bitsInitialized = true;
		for (int n = 0; n < initialQubits.length; ++n) {
			initialClassicalBits[n] = receivedKey[n].takeMeasurement(polarizations[n]);
			initialQubits[n] = new Qubit(initialClassicalBits[n], polarizations[n]);
		}
		return initialQubits;
	}

	/**
	 * Send the polarizations I use to measure qubits.
	 * 
	 * @return An array of polarizations.
	 */
	public boolean[] sendPolarizations() {
		return polarizations;
	}

	/**
	 * 
	 * @return String representation of the Quantum Key.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (bitsInitialized) {
			sb.append("Bits        : " + Arrays.toString(initialClassicalBits));
			sb.append("\n");
		}
		sb.append("Polarization: ");
		sb.append(Arrays.toString(polarizations).replaceAll("false", "_").replaceAll("true", "P"));
		if (agreedOnCommonPolarizations) {
			sb.append("\nAgreed Polarizations:\n");
			sb.append("Positions: " + agreedPolarizationIndices);
		}
		return sb.toString();
	}

	/**
	 * After agreeing on a key, you can encode and send a plain text message.
	 * 
	 * @param plaintextMessage
	 *            The plain text message to encode.
	 * @return A byte array representing the encoded message.
	 */
	public byte[] encodeAndSendMessage(final String plaintextMessage) {
		MessageDigest digest;
		byte[] encryptedData = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(Arrays.toString(getAgreedKey()).getBytes(StandardCharsets.UTF_8));

			byte[] key = encodedhash;
			byte[] dataToSend = plaintextMessage.getBytes();
			Cipher c = Cipher.getInstance("AES");
			SecretKeySpec k = new SecretKeySpec(key, "AES");
			c.init(Cipher.ENCRYPT_MODE, k);
			encryptedData = c.doFinal(dataToSend);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			e.printStackTrace();
		}
		return encryptedData;
	}

	/**
	 * Decode an encrypted message send using the agreed upon key.
	 * 
	 * @param cipherText
	 *            The encoded message.
	 * @return The plain text message decoded using my key.
	 */
	public String decodeMessage(final byte[] cipherText) {
		MessageDigest digest;
		String returnValue = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(Arrays.toString(getAgreedKey()).getBytes(StandardCharsets.UTF_8));

			byte[] key = encodedhash;
			Cipher c = Cipher.getInstance("AES");
			SecretKeySpec k = new SecretKeySpec(key, "AES");
			c.init(Cipher.DECRYPT_MODE, k);
			byte[] decryptedData = c.doFinal(cipherText);
			returnValue = new String(decryptedData);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			throw new InvalidParameterException(
					"Our keys don't seem to be matching :(  Perhaps Eve intercepted our key in transit!?");
		}
		return returnValue;
	}
}
