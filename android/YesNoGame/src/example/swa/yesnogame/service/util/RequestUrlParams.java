package example.swa.yesnogame.service.util;

import java.net.URI;
import java.net.URISyntaxException;

import android.util.Log;

/**
 * Represents all parameters needed to request the remote service via
 * RequestUrlTask. Immutable Type.
 */
public class RequestUrlParams {

	public interface IResponseListener {
		void onResponse(String text);
	}

	/**
	 * Method type. see static constants METHOD_...
	 */
	private RequestMethodEnum method;

	/**
	 * The data to be posted.
	 */
	private String postData;

	/**
	 * The URI to be called.
	 */
	private URI uri;

	/**
	 * The listener to be called when the background task finished.
	 */
	private IResponseListener listener;

	/**
	 * The data to be put in the PUT method call.
	 */
	private String putData;

	private EntityFormatEnum format;

	public RequestUrlParams(RequestMethodEnum method, EntityFormatEnum format, String url, IResponseListener listener) {
		super();
		this.method = method;
		this.format = format;
		try {
			this.uri = new URI(url);
		} catch (URISyntaxException ex) {
			Log.e("YesNo", ex.toString());
		}
		this.listener = listener;
	}

	public RequestUrlParams(RequestMethodEnum method, String url, IResponseListener listener) {
		super();
		this.method = method;
		this.format = EntityFormatEnum.UNDEFINED;
		try {
			this.uri = new URI(url);
		} catch (URISyntaxException ex) {
			Log.e("YesNo", ex.toString());
		}
		this.listener = listener;
	}

	public EntityFormatEnum getEntityFormat() {
		return this.format;
	}

	public IResponseListener getListener() {
		return this.listener;
	}

	public RequestMethodEnum getMethod() {
		return this.method;
	}

	public String getPostData() {
		return this.postData;
	}

	public String getPutData() {
		return this.putData;
	}

	public URI getUri() {
		return this.uri;
	}

	public void setEntityFormat(EntityFormatEnum format) {
		this.format = format;
	}

	public void setListener(IResponseListener listener) {
		this.listener = listener;
	}

	public void setMethod(RequestMethodEnum method) {
		this.method = method;
	}

	public void setPostData(String postData) {
		this.postData = postData;
	}

	public void setPutData(String putData) {
		this.putData = putData;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

}
