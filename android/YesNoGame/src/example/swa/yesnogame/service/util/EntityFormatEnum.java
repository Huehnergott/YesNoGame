package example.swa.yesnogame.service.util;

/**
 * Enum for entity encoding formats do be used by POST and PUT methods.
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public enum EntityFormatEnum {
	/**
	 * Use application/x-www-urlformencoding
	 */
	FORMAT_URL_FORM_ENCODED,

	/**
	 * Use json-encoded string
	 */
	FORMAT_JSON_ENCODED,

	/**
	 * Use any
	 */
	UNDEFINED,
}
