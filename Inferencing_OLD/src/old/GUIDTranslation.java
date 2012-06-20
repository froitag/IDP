package old;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains a GUID translation table for a specific database.
 * Used when merging databases that contain similar information but use different GUIDs for it.
 * 
 * TODO: serializing + unserializing it
 */
public class GUIDTranslation {

	/**
	 * constructor
	 * 
	 * @param dbName
	 */
	public GUIDTranslation(String dbName) {
		_dbName = dbName;
	}


	/* public getter */
	/**
	 * get the name of the database this GUIDTranslation belongs to.
	 * 
	 * @return string
	 */
	public String getDBName() {
		return _dbName;
	}

	/**
	 * gets the translations.
	 * 
	 * @return Map<originalGUID,Set<NewGUIDs>>
	 */
	public Map<String,Set<String>> getTranslations() {
		return new HashMap<String,Set<String>>(_translations);
	}


	/* public modifier */
	/**
	 * adds a translation.
	 * 
	 * @param originalGUID
	 * @param newGUID
	 * @return this for fluent interface
	 */
	public GUIDTranslation addTranslation(String originalGUID, String newGUID) {
		if (!_translations.containsKey(originalGUID)) {
			_translations.put(originalGUID, new HashSet<String>());
		}

		_translations.get(originalGUID).add(newGUID);

		return this;
	}

	/**
	 * removes a translation.
	 * 
	 * @param originalGUID
	 * @param newGUID
	 * @return this for fluent interface
	 */
	public GUIDTranslation removeTranslation(String originalGUID, String newGUID) {
		if (!_translations.containsKey(originalGUID)) return this;

		Set<String> set = _translations.get(originalGUID);
		set.remove(newGUID);
		if (set.size() <= 0) {
			_translations.remove(set);
		}

		return this;
	}

	/**
	 * adds all translation of `translation` to this instance.
	 * 
	 * @param translation
	 * @return this for fluent interface
	 */
	public GUIDTranslation addTranslation(GUIDTranslation translation) {
		if (translation==null) return this;

		Map<String,Set<String>> translations = translation.getTranslations();
		for (String origGUID: translations.keySet()) {
			Set<String> newGUIDs = translations.get(origGUID);
			if (newGUIDs == null) continue;

			for (String newGUID: newGUIDs) {
				this.addTranslation(origGUID, newGUID);
			}
		}

		return this;
	}

	/**
	 * removes all translations that `translation` has from this instance.
	 * 
	 * @param translation
	 * @return this for fluent interface
	 */
	public GUIDTranslation removeTranslation(GUIDTranslation translation) {
		if (translation==null) return this;

		Map<String,Set<String>> translations = translation.getTranslations();
		for (String origGUID: translations.keySet()) {
			Set<String> newGUIDs = translations.get(origGUID);
			if (newGUIDs == null) continue;

			for (String newGUID: newGUIDs) {
				this.removeTranslation(origGUID, newGUID);
			}
		}

		return this;
	}



	/* protected member */
	protected String _dbName;
	protected Map<String,Set<String>> _translations=new HashMap<String,Set<String>>();
}
