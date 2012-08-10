package de.tum.in.fedsparql.inference.framework.stuff;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Abstract PNG
 * defines an Interface + some methods for commonly used PNGs
 */
public abstract class APng {

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
