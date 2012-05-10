package de.tum.in.fedsparql.inference.framework;

import java.util.HashMap;
import java.util.Map;

import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Represents a Script used to normalise triple objects.
 * E.g. used to convert Date or Name representations into one unique format.
 */
public class NormalisationScript {

	/* constructor */
	/**
	 * Creates NormalisationScript from raw input script.
	 * 
	 * @param name
	 * @param dbName
	 * @param predicateGUID
	 * @param jsNormalisationScript JavaScript that gets fed with the variable `$val` containing the current row's value and must `return` the normalised value of this field.
	 * @throws ScriptException
	 */
	public NormalisationScript(String name, String dbName, String predicateGUID, String jsNormalisationScript) throws ScriptException {
		_name = name;
		_dbName = dbName;
		_predicateGUID = predicateGUID;

		_jsonRepr = _genJsonRepr(name, jsNormalisationScript);
		_cScript = _genScript(_jsonRepr);
	}
	/**
	 * Creates a NormalisationScript using a JSON representation of a script (this.getJSONRepresentation()) for the given database and predicateGUID.
	 * 
	 * @param jsonRepresentation
	 * @param dbName
	 * @param predicateGUID
	 * @throws ScriptException
	 */
	protected NormalisationScript(String jsonRepresentation, String dbName, String predicateGUID) throws ScriptException {
		JSONObject jObj = (JSONObject) JSONValue.parse(jsonRepresentation);
		if (!jObj.containsKey("name") || !jObj.containsKey("script")) {
			throw new ScriptException("Couldn't parse jsonRepresentation!");
		}

		_name=jObj.get("name").toString();
		_dbName = dbName;
		_predicateGUID = predicateGUID;

		_jsonRepr = jsonRepresentation;
		_cScript = _genScript(_jsonRepr);
	}


	/* public methods */
	/**
	 * gets name of this script
	 * 
	 * @return string, name
	 */
	public String getName() {
		return _name;
	}
	/**
	 * gets DB name this script is for
	 * 
	 * @return string, dbName
	 */
	public String getDBName() {
		return _dbName;
	}
	/**
	 * gets predicate GUID this script is for
	 * 
	 * @return string, predicateGUID
	 */
	public String getPredicateGUID() {
		return _predicateGUID;
	}
	/**
	 * gets the compiled Javascript
	 * 
	 * @return CompiledScript
	 */
	public CompiledScript getScript() {
		return _cScript;
	}

	/**
	 * Normalizes a given value with the script this NormalisationScript represents.
	 * 
	 * @param value
	 * @return String, normalised value
	 * @throws ScriptException
	 */
	public String normalise(String value) throws ScriptException {
		Map<String,Object> jsArguments = new HashMap<String,Object>();
		jsArguments.put("$VALUE", value);

		return JSHelper.eval(_cScript, jsArguments).toString();
	}



	/**
	 * Gets the general JSON representation of this script.
	 * This representation only contains the script's name and its code.
	 * The JSON representation of a script can be converted to a NormalisationScript using NormalisationScript.fromJsonRepresentation().
	 * 
	 * @return JSON String
	 */
	public String toJSONRepresentation() {
		return _jsonRepr;
	}
	/**
	 * Creates a NormalisationScript using a JSON representation of a script (this.getJSONRepresentation()) for the given database and predicateGUID.
	 * 
	 * @param jsonRepresentation
	 * @param dbName
	 * @param predicateGUID
	 * @return NormailsationScript
	 * @throws ScriptException
	 */
	public static NormalisationScript fromJSONRepresentation(String jsonRepresentation, String dbName, String predicateGUID) throws ScriptException {
		return new NormalisationScript(jsonRepresentation, dbName, predicateGUID);
	}



	/* protected helper */
	protected static String _genJsonRepr(String name, String rawScript) {
		// $val can be used inside the script to reference the value (=object) of the current DB entry
		// $VALUE need be passed to the script
		String script = "{" +
				"name: '" + name.replace("'", "\\'") + "'," +
				"script: function ($val) {" + rawScript + "}" +
				"}";
		return script;
	}
	protected static CompiledScript _genScript(String jsonRepr) throws ScriptException {
		String script = "var x="+jsonRepr + "; x.script($VALUE);";
		System.out.println(script);
		return JSHelper.compile(script);
	}

	/* protected member */
	protected String _name;
	protected String _predicateGUID;
	protected String _dbName;
	protected String _jsonRepr;
	protected CompiledScript _cScript;
}
