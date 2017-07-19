package jdbc.automic.restconnector;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
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
	
	public RestCaller(String url, Method method) {
		httpclient = HttpClients.createDefault();
		this.url = url;
		this.nameValueList = new ArrayList<>();
		this.headerList = new ArrayList<>();
		setMethod(method);
		logger.debug("HttpClient Method "+ method.name());
	}
	
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
	
	//Adding Header to HeaderList
	public void addHeader(String key, String value) {
		this.addHeader(new BasicHeader(key, value));
	}
	
	public void addHeader(Header header) {
		this.headerList.add(header);
	}
	//End Adding Header to HeaderList
	
	//Adding Authentication to CredentialProvider
	public void setAuthentication(String username, String password) {
		setAuthentication(new UsernamePasswordCredentials(username, password));
	}
	
	public void setAuthentication(Credentials credentials) {
		this.credentialsProvider = new BasicCredentialsProvider();
		this.credentialsProvider.setCredentials(AuthScope.ANY, credentials);
	}
	//End Adding Authentification to CredentialProvider
	
	//Adding Attributes to nameValueList
	public void appendAttributes(String key, String value) {
		appendAttributes(new BasicNameValuePair(key, value));
	}
	
	public void appendAttributes(NameValuePair nvp) {
		nameValueList.add(nvp);
	}
	//End Adding Attributes to nameValueList


	//if Authentification or header or Parameters changed you need to rebuild the client
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
	
	//Sets Body for Post HttpRequest
	public void setBody(String s) {
		this.body = s;
	}
	
	//Executes the HttpRequest
	public void execute() throws IOException {
		if(httpcontext != null) response = httpclient.execute(httpRequestBase,httpcontext);
		else response = httpclient.execute(httpRequestBase);
		logger.debug("Sending HttpRequest");
	}
	
	//Returnes the Response from the previous Execution
	public InputStream getResponse() throws UnsupportedOperationException, IOException {
		return response.getEntity().getContent();
	}
	
	//After reading the response you need the close it
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
	
	//if you finished working with the client you need to close the client
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
