package de.tum.in.fedsparql.inference.framework;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class APNG {

	/* public methods */
	/**
	 * gets the PNG in byte representation
	 */
	public byte[] getBytes() {
		return _png;
	}

	/**
	 * saves the PNG to a specific file
	 * @param file
	 * @throws IOException
	 */
	public void save(File file) throws IOException {
		if (_png != null) {
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(_png);
			fos.close();
		}
	}


	/* protected member */
	protected byte[] _png=null;
}
