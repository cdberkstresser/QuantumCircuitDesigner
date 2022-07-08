package model;

import java.io.Serializable;

/**
 * Complex number class.
 * 
 * @author cdberkstresser
 *
 */
public final class Complex implements Serializable {
	/** The number of digits to which to round numbers. */
	public static final int PRECISION = 3;
	/** Serializable ID. */
	private static final long serialVersionUID = 7676764277764311560L;
	/** The imaginary part of the complex number. */
	private final double imaginary;
	/** The real part of the complex number. */
	private final double real;

	/**
	 * Constructor for a complex number of magnitude zero.
	 */
	public Complex() {
		this(0.0, 0.0);
	}

	/**
	 * Constructor for a real-only complex number.
	 * 
	 * @param real Real part of the complex number.
	 */
	public Complex(final double real) {
		this(real, 0.0);
	}

	/**
	 * Constructor.
	 * 
	 * @param real      Real part of the complex number.
	 * @param imaginary Imaginary part of the complex number.
	 */
	public Complex(final double real, final double imaginary) {
		this.real = real;
		this.imaginary = imaginary;
	}

	/**
	 * Adds two complex numbers.
	 * 
	 * @param numberToAdd The number to which to add to this instance.
	 * @return A complex number representing the sum of the two numbers.
	 * @implSpec Does not change the value of instance.
	 */
	public Complex add(final Complex numberToAdd) {
		return new Complex(this.real + numberToAdd.real, this.imaginary + numberToAdd.imaginary);
	}

	/**
	 * Clones the object with a new reference.
	 */
	@Override
	public Complex clone() {
		return new Complex(real, imaginary);
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
		Complex other = (Complex) obj;
		if (Double.doubleToLongBits(imaginary) != Double.doubleToLongBits(other.imaginary)) {
			return false;
		}
		return Double.doubleToLongBits(real) == Double.doubleToLongBits(other.real);
	}

	/**
	 * @return Imaginary portion of the complex number.
	 */
	public double getImaginary() {
		return imaginary;
	}

	/**
	 * @return Real portion of the complex number.
	 */
	public double getReal() {
		return real;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		final int bitshift = 32;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(imaginary);
		result = prime * result + (int) (temp ^ (temp >>> bitshift));
		temp = Double.doubleToLongBits(real);
		result = prime * result + (int) (temp ^ (temp >>> bitshift));
		return result;
	}

	/**
	 * @return Modulus (distance from the pole) of the complex number.
	 */
	public double modulus() {
		return Math.sqrt(real * real + imaginary * imaginary);
	}

	/**
	 * Multiplies two complex numbers.
	 * 
	 * @param numberToMultiply The number to which to multiply by this instance.
	 * @return A complex number representing the product of the two numbers.
	 * @implSpec Does not change the value of instance.
	 */
	public Complex multiply(final Complex numberToMultiply) {
		return new Complex(this.real * numberToMultiply.real - this.imaginary * numberToMultiply.imaginary,
				this.imaginary * numberToMultiply.real + this.real * numberToMultiply.imaginary);
	}

	@Override
	public String toString() {
		final int basis = 10;
		if (this.imaginary != 0) {
			return "(" + Math.round(this.real * Math.pow(basis, PRECISION)) / Math.pow(basis, PRECISION) + " + "
					+ Math.round(this.imaginary * Math.pow(basis, PRECISION)) / Math.pow(basis, PRECISION) + "i)";
		} else {
			return "" + Math.round(this.real * Math.pow(basis, PRECISION)) / Math.pow(basis, PRECISION);
		}
	}

}
