package de.tum.in.fedsparql.inference.dummy;

public class JenaParser {


	/**
	 * Ugly method that parses a literal
	 * @param literal
	 * @return
	 */
	public static ParsedLiteral parseLiteral(String literal){

		int begin_value = -1;
		int end_value = -1;
		int begin_language = -1;
		int end_language = -1;
		int begin_datatype = -1;
		int end_datatype = -1;

		// Find indices
		char[] chars = literal.toCharArray();

		// Find start of literal
		for (int i=0; i<chars.length; i++){
			if (chars[i]=='\"'){
				begin_value=i+1;
				break;
			}
		}

		// Find end of literal
		for (int i=chars.length-1; i>=0; i--){
			if (chars[i]=='\"'){
				end_value=i-1;
				break;
			}
		}

		// Find language
		if (end_value+2 < chars.length && chars[end_value+2]=='@'){
			begin_language=end_value+3;
			end_language=chars.length-1;
		}
		// Find datatype
		else if (end_value+3 < chars.length && chars[end_value+2]=='^' && chars[end_value+3]=='^'){
			begin_datatype=end_value+5;
			end_datatype=chars.length-2;
		}

		// Validate
		if (begin_value==-1 || end_value==-1){
			throw new RuntimeException("Error parsing literal: "+literal);
		}

		// Define
		String datatype = null;
		String language = null;
		String value = new String(chars, begin_value, end_value - begin_value+1);

		// Set
		if (begin_language!=-1 && end_language!=-1) language = new String(chars, begin_language, end_language - begin_language+1);
		else if (begin_datatype!=-1 && end_datatype!=-1) datatype = new String(chars, begin_datatype, end_datatype - begin_datatype+1);
		// In case of no language or no datatype assume type "string" with language "english"
		//else language = "en";
		else language = null;
		// TODO: language=null ok??? otherwise it won't be inserted as string but as "com.hp.hpl.jena.datatypes.BaseDatatype$TypedValue@58a62e3"^^<en>

		// Return
		if (datatype!=null) {
			return new ParsedLiteral(value, datatype, true);
		}
		else if (language!=null){
			return new ParsedLiteral(value, language, true);
		} else {
			return new ParsedLiteral(value, "string", false);
		}
		//else throw new RuntimeException("Error parsing literal: "+literal);
	}

	/**
	 * Represents a parsed literal
	 * @author prasser
	 */
	public static class ParsedLiteral{
		public String value;
		public String type;
		public boolean datatype;
		public ParsedLiteral(String value, String type, boolean datatype) {
			this.value = value;
			this.type = type;
			this.datatype = datatype;
		}
	}
}
