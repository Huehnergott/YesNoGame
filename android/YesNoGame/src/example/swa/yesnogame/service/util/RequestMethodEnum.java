package example.swa.yesnogame.service.util;

public enum RequestMethodEnum {
	/**
	 * Request method GET (find)
	 */
	METHOD_GET,

	/**
	 * Request method PUT (update)
	 */
	METHOD_PUT,

	/**
	 * Request method POST (create)
	 */
	METHOD_POST,

	/**
	 * Request method DELETE (delete)
	 */
	METHOD_DELETE,

	/**
	 * Request method POST PUT (does a PUT/update via a POST/create request)
	 */
	METHOD_POST_PUT,

}
