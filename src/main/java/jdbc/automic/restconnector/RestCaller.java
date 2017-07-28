package jdbc.automic.restconnector;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class RestCaller {
	private final Logger logger = Logger.getLogger(RestCaller.class);
	public enum Method { GET , POST }
	
	private CloseableHttpClient httpclient;
	private HttpRequestBase httpRequestBase;
	private CloseableHttpResponse response;
	private final HttpContext httpcontext = null;
	
	private final String url;
	private final List<NameValuePair> nameValueList;
	private final List<Header> headerList;
	private String body;
	private CredentialsProvider credentialsProvider;

	/**
	 * <P>Initializes the RestCaller with the url and the method</P>
	 * @param url URL to the Rest Service
	 * @param method {@link RestCaller.Method} Set the Method for the {@link org.apache.http.HttpRequest}
	 */
	public RestCaller(String url, Method method) {
		this.httpclient = HttpClients.createDefault();
		this.url = url;
		this.nameValueList = new ArrayList<>();
		this.headerList = new ArrayList<>();
		setMethod(method);
		logger.debug("HttpClient Method "+ method.name());
	}

	/**
	 * <P>Sets the Method for the {@link HttpRequestBase}</P>
	 * @param method {@link RestCaller.Method}
	 */
	public void setMethod(Method method) {
		switch(method) {
		case GET:
			httpRequestBase = new HttpGet(url);
			break;
		case POST:
			httpRequestBase = new HttpPost(url);
			break;
		}
	}

	/**
	 * <P>Adds Header to the HeaderList of the {@link HttpRequestBase}</P>
	 * @param key Header Key
	 * @param value Header Value
	 */
	public void addHeader(String key, String value) {
		this.addHeader(new BasicHeader(key, value));
	}

	/**
	 * <P>Adds Header to the HeaderList of the {@link HttpRequestBase}</P>
	 * @param header Header to add
	 */
	public void addHeader(Header header) {
		this.headerList.add(header);
	}

	/**
	 * <P>Adds Basic Authentication to the {@link HttpRequestBase}</P>
	 */
	public void setAuthentication(String username, String password) {
		setAuthentication(new UsernamePasswordCredentials(username, password));
	}

	/**
	 * <P>Adds the Credentials to the {@link HttpRequestBase}</P>
	 * @param credentials {@link org.apache.http.auth.Credentials} this Credentials get added
	 */
	public void setAuthentication(Credentials credentials) {
		this.credentialsProvider = new BasicCredentialsProvider();
		this.credentialsProvider.setCredentials(AuthScope.ANY, credentials);
	}

	/**
	 * Appends Key-Value-Pair to the body of the {@link HttpRequestBase}
	 * @param key for the entry
	 * @param value for the entry
	 */
	public void appendAttributes(String key, String value) {
		appendAttributes(new BasicNameValuePair(key, value));
	}

	/**
	 * <P>Appends Key-Value-Pair to the body of the {@link HttpRequestBase}</P>
	 * @param nvp {@link NameValuePair}
	 */
	public void appendAttributes(NameValuePair nvp) {
		nameValueList.add(nvp);
	}


	/**
	 * <P>Builds the {@link org.apache.http.client.HttpClient}</P>
	 * <P>Only call this if the Header or the Credentials have changed</P>
	 * <P>Note: this Method already calls {@link RestCaller#addParametersToRequest()}</P>
	 */
	public void build() throws UnsupportedEncodingException, URISyntaxException {
		//Adding Credentials to HttpClient & Adding Headers to HttpClient
		if(!headerList.isEmpty() || !(credentialsProvider == null)) {
			this.httpclient = HttpClientBuilder.create().
					setDefaultCredentialsProvider(credentialsProvider).
					setDefaultHeaders(headerList).build();
		}
		addParametersToRequest();
		logger.debug("Building HttpRequest");
	}

	/**
	 * <P>Adds all the Parameters to the Request</P>
	 * <P>Only call this if the Parameters have changed</P>
	 */
	public void addParametersToRequest() throws URISyntaxException, UnsupportedEncodingException {
		//Adding Parameters to Http Request
		if(httpRequestBase instanceof HttpGet) {
			httpRequestBase.setURI(new URIBuilder(httpRequestBase.getURI()).addParameters(nameValueList).build());
		}else if(httpRequestBase instanceof HttpPost) {
			if(body == null)
				((HttpPost)httpRequestBase).setEntity(new UrlEncodedFormEntity(nameValueList));
			else
				((HttpPost)httpRequestBase).setEntity(new ByteArrayEntity(body.getBytes()));
		}
	}

	/**
	 * Sets the Body for an Http Post Request
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * <P>Executes the built {@link HttpRequestBase}</P>
	 * <P>To get the Response call {@link RestCaller#getResponse()}</P>
	 */
	public void execute() throws IOException {
		response = httpclient.execute(httpRequestBase);
		logger.debug("Sending HttpRequest");
	}

	/**
	 * Returns the {@link org.apache.http.HttpResponse} from the previous execution
	 * @return {@link InputStream} of the returned Response
	 */
	public InputStream getResponse() throws UnsupportedOperationException, IOException {
		return response.getEntity().getContent();
	}


	/**
	 * Return the ResponseCode from the previous execution
	 * @return Response code
	 */
	public int getResponseCode(){
		return response.getStatusLine().getStatusCode();
	}

	/**
	 * <P>After reading the Response you have to call this Method</P>
	 */
	public void closeResponse() {
		try {
			EntityUtils.consume(response.getEntity());
			response.close();
		} catch (IOException e) {
			logger.debug("Cannot close HttpResponse");
			logger.trace("", e);
		}
		logger.debug("Closing the HttpResponse");
	}

	/**
	 * <P>After working with the RestCaller you need to close it</P>
	 */
	public void closeClient() {
		try {
			httpclient.close();
		} catch (IOException e) {
			logger.debug("Cannot close HttpClient");
			logger.trace("",e);
		}
		logger.debug("Closing HttpClient");
	}
}
