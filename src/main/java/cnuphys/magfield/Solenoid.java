/*
 * 
 */
package cnuphys.magfield;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.StringTokenizer;

/**
 * The Class Solenoid.
 *
 * @author Sebouh Paul
 * @version 1.0
 */
public final class Solenoid extends MagneticField {
	
	
	// private constructor
	/**
	 * Instantiates a new solenoid.
	 */
	private Solenoid() {
		setCoordinateNames("phi", "rho", "z");
	}

	/**
	 * Checks whether the field has been set to always return zero.
	 * 
	 * @return <code>true</code> if the field is set to return zero.
	 */
	@Override
	public final boolean isZeroField() {
		if (isActive()) {
			return super.isZeroField();
		} else {
			return true;
		}
	}
	
	/**
	 * Checks this field active. 
	 * @return <code>true</code> if this field is active;
	 */
	public boolean isActive() {
		return MagneticFields.getInstance().hasActiveSolenoid();
	}


	/**
	 * Obtain a solenoid object from a binary file, probably
	 * "clas12_solenoid_fieldmap_binary.dat"
	 *
	 * @param file the file to read
	 * @return the solenoid object
	 * @throws FileNotFoundException the file not found exception
	 */
	public static Solenoid fromBinaryFile(File file)
			throws FileNotFoundException {
		Solenoid solenoid = new Solenoid();
		solenoid.readBinaryMagneticField(file);
		// is the field ready to use?
		System.out.println(solenoid.toString());
		return solenoid;
	}
    
        /**
	 * Obtain a solenoid object from a binary file, probably
	 * "clas12_solenoid_fieldmap_binary.dat"
	 *
	 * @param file the file to read
	 * @return the solenoid object
	 * @throws FileNotFoundException the file not found exception
	 */
	public static Solenoid fromBinaryFile(InputStream ins, String filename)
			throws FileNotFoundException {
		Solenoid solenoid = new Solenoid();
		solenoid.readBinaryMagneticField(ins, filename);
		// is the field ready to use?
		System.out.println(solenoid.toString());
		return solenoid;
	}

	/**
	 * Get the name of the field
	 * 
	 * @return the name
	 */
	@Override
	public String getName() {
		return "Solenoid";
	}
		
	
	/**
	 * Get some data as a string.
	 * 
	 * @return a string representation.
	 */
	@Override
	public final String toString() {
		String s = "Solenoid\n";
		s += super.toString();
		return s;
	}
	
	/**
	 * Print the current configuration
	 * @param ps the print stream
	 */
	@Override
	public void printConfiguration(PrintStream ps) {
		ps.println(String.format("SOLENOID scale: %6.3f file: %s", _scaleFactor, MagneticFields.getInstance().getSolenoidBaseName()));
	}

	/**
	 * main method used for testing.
	 *
	 * @param arg command line arguments
	 */
	public static void main(String arg[]) {

		// covert the new ascii to binary
		File asciiFile = new File("../../../data/clas12SolenoidFieldMap.dat.txt");
		if (!asciiFile.exists()) {
			System.out.println("File not found: " + asciiFile.getPath());
		}
		else {
			System.out.println("File found: " + asciiFile.getPath());

			FileReader fileReader;
			try {
				fileReader = new FileReader(asciiFile);
				final BufferedReader bufferedReader = new BufferedReader(
						fileReader);

				// prepare the binary file
				String binaryFileName = "../../../data/clas12-fieldmap-solenoid.dat";
				// String binaryFileName = "data/solenoid-srr_V3.dat";
				int nPhi = 1;
				int nRho = 601;
				int nZ = 1201;
				float phimin = 0.0f;
				float phimax = 360.0f;
				float rhomin = 0.0f;
				float rhomax = 300.0f;
				float zmin = -300.0f;
				float zmax = 300.0f;

				DataOutputStream dos = new DataOutputStream(
						new FileOutputStream(binaryFileName));
				try {
					// write the header
					dos.writeInt(0xced);
					dos.writeInt(0);// cylindrical
					dos.writeInt(0);// cylindrical
					dos.writeInt(0);
					dos.writeInt(0);
					dos.writeInt(0);
					dos.writeFloat(phimin);
					dos.writeFloat(phimax);
					dos.writeInt(nPhi);
					dos.writeFloat(rhomin);
					dos.writeFloat(rhomax);
					dos.writeInt(nRho);
					dos.writeFloat(zmin);
					dos.writeFloat(zmax);
					dos.writeInt(nZ);
					dos.writeInt(0);
					dos.writeInt(0);
					dos.writeInt(0);
					dos.writeInt(0);
					dos.writeInt(0);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				boolean reading = true;
				while (reading) {
					String s = nextNonComment(bufferedReader);
					// System.out.println("s: [" + s + "]");

					if (s != null) {
						String tokens[] = tokens(s, " ");
						dos.writeFloat(0f);
						dos.writeFloat(10 * Float.parseFloat(tokens[2]));
						dos.writeFloat(10 * Float.parseFloat(tokens[3]));
						// System.out.println(s);
					} else {
						reading = false;
					}
				}

				dos.close();
				System.out.println("done");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private static String[] tokens(String str, String delimiter) {

		StringTokenizer t = new StringTokenizer(str, delimiter);
		int num = t.countTokens();
		String lines[] = new String[num];

		for (int i = 0; i < num; i++) {
			lines[i] = t.nextToken();
		}

		return lines;
	}

	/**
	 * Get the next non comment line
	 * 
	 * @param bufferedReader a buffered reader which should be linked to an
	 *            ascii file
	 * @return the next non comment line (or <code>null</code>)
	 */
	private static String nextNonComment(BufferedReader bufferedReader) {
		String s = null;
		try {
			s = bufferedReader.readLine();
			if (s != null) {
				s = s.trim();
			}
			while ((s != null) && (s.startsWith("<") || s.length() < 1)) {
				s = bufferedReader.readLine();
				if (s != null) {
					s = s.trim();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return s;
	}





}
