package example.swa.yesnogame.service.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.util.Log;
import example.swa.yesnogame.service.util.RequestUrlParams.IResponseListener;

/**
 * Defines a task to be run in the background requesting a URL and returning the
 * result (String).
 * 
 * @author Hendrik.Stilke@siemens.com
 * 
 */
public class RequestUrlTask extends AsyncTask<RequestUrlParams, Void, String> {

	RequestUrlParams params;

	/**
	 * Build the http entity to be sent with HTTP PUT or POST
	 * 
	 * @throws Exception
	 */
	private HttpEntity buildHttpEntity(String data, EntityFormatEnum format) throws Exception {
		StringEntity ret = null;
		try {
			switch (format) {
			case FORMAT_JSON_ENCODED:
				ret = new StringEntity(data);
				ret.setContentType("application/json");
				break;
			case FORMAT_URL_FORM_ENCODED:
				// application/x-www-urlformencoded
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("json", data));
				ret = new UrlEncodedFormEntity(nameValuePairs);
				break;
			case UNDEFINED:
			default:
				throw new Exception("please provide entity format for encoding");
			}
		} catch (UnsupportedEncodingException ex) {
			Log.e("service", ex.toString());
		}

		return ret;
	}

	@Override
	protected String doInBackground(RequestUrlParams... paramArray) {
		this.params = paramArray[0];
		String ret = null;

		try {
			ret = doRequest(this.params);
		} catch (Exception ex) {
			Log.e("service", ex.toString());
		}

		return ret;
	}

	/**
	 * Requests with the params given. Runs synchronously if used directly
	 * (blocking thread!) Runs asynchronously when run via doInBackground.
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public String doRequest(RequestUrlParams params) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpUriRequest request = null;

		switch (this.params.getMethod()) {
		case METHOD_GET:
			request = new HttpGet(this.params.getUri());
			break;
		case METHOD_POST_PUT:
			/*
			 * PUT DOES NOT WORK FOR UNKNOWN REASON WITH THE PHP based service
			 * HttpPut put = new HttpPut(this.params.getUri());
			 * put.setEntity(buildHttpEntity(this.params.getPutData()));//
			 * request = put;
			 */
			HttpPost postput = new HttpPost(this.params.getUri());
			postput.setEntity(buildHttpEntity(this.params.getPutData(), params.getEntityFormat()));
			request = postput;
			break;
		case METHOD_PUT:
			HttpPut put = new HttpPut(this.params.getUri());
			put.setEntity(buildHttpEntity(this.params.getPutData(), params.getEntityFormat()));
			request = put;
			break;
		case METHOD_POST:
			HttpPost post = new HttpPost(this.params.getUri());
			post.setEntity(buildHttpEntity(this.params.getPostData(), params.getEntityFormat()));
			request = post;
			break;
		case METHOD_DELETE:
			request = new HttpDelete(this.params.getUri());
			break;
		default:
			request = new HttpGet(this.params.getUri());
		}

		String text = null;
		try {
			HttpResponse response = httpClient.execute(request, localContext);
			HttpEntity entity = response.getEntity();
			text = getASCIIContentFromEntity(entity);

		} catch (Exception e) {
			Log.e("RequestUrlTask", e.toString());
		}
		return text;
	}

	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

		InputStream in = entity.getContent();

		StringBuffer out = new StringBuffer();
		int n = 1;
		while (n > 0) {
			byte[] b = new byte[4096];
			n = in.read(b);
			if (n > 0) {
				out.append(new String(b, 0, n));
			}
		}
		return out.toString();
	}

	@Override
	protected void onPostExecute(String result) {
		// this method runs in the UI thread!
		IResponseListener listener = this.params.getListener();
		listener.onResponse(result);
	}
}
