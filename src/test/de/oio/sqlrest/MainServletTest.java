package de.oio.sqlrest;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import junit.framework.TestCase;

public class MainServletTest extends TestCase {

	public static final String POST_NEW_PRODUCT="<resource> <ID>1</ID> <NAME>car</NAME> <PRICE>2000</PRICE> </resource>";
	public static final String PUT_NEW_PRODUCT="<resource> <NAME>car</NAME> <PRICE>2000</PRICE> </resource>";
	public static final String POST_ALTERING_PRODUCT="<resource> <PRICE>20</PRICE> </resource>";
	public static final HttpClient client=new HttpClient();
	
	public void testGetTables() throws IOException{
		
		
		HttpMethod method=new GetMethod("http://localhost:8080/sqlrest");
		int status=client.executeMethod(method);
		assertEquals(200, status);
		byte[] response =method.getResponseBody();
		assertNotNull(response);
		assertTrue(response.length>0);
		
	}
	
	public void testGetTable() throws HttpException, IOException{
		
		HttpMethod method=new GetMethod("http://localhost:8080/sqlrest/PRODUCT/");
		int status=client.executeMethod(method);
		assertEquals(200, status);
		byte[] response =method.getResponseBody();
		assertNotNull(response);
		assertTrue(response.length>0);
	}
	
	public void testGetTableItem() throws HttpException, IOException{
		
		HttpMethod method=new GetMethod("http://localhost:8080/sqlrest/PRODUCT/3/");
		int status=client.executeMethod(method);
		assertEquals(200, status);
		byte[] response =method.getResponseBody();
		assertNotNull(response);
		assertTrue(response.length>0);
	}
	
	public void testDeleteProduct() throws HttpException, IOException{
		
		HttpMethod method=new DeleteMethod("http://localhost:8080/sqlrest/PRODUCT/1/");
		int status=client.executeMethod(method);
		assertEquals(200, status);
		byte[] response =method.getResponseBody();
		assertNotNull(response);
		assertTrue(response.length>0);
		System.out.println(new String(response));
		
	}
	
	public void testPostProduct() throws HttpException, IOException{
		
		PostMethod method=new PostMethod("http://localhost:8080/sqlrest/PRODUCT/");
		RequestEntity body=new StringRequestEntity(POST_NEW_PRODUCT);
		method.setRequestEntity(body);
		int status=client.executeMethod(method);
		assertEquals(201, status);
		byte[] response =method.getResponseBody();
		assertNotNull(response);
		assertTrue(response.length==0);
	}
	
	public void testPutProduct() throws HttpException, IOException{
		
		PutMethod method=new PutMethod("http://localhost:8080/sqlrest/PRODUCT/83/");
		RequestEntity body=new StringRequestEntity(PUT_NEW_PRODUCT);
		method.setRequestEntity(body);
		int status=client.executeMethod(method);
		assertEquals(201, status);
		byte[] response =method.getResponseBody();
		assertNotNull(response);
		assertTrue(response.length==0);
	}
	
	public void testPostAlteing() throws HttpException, IOException{
		
		PostMethod method=new PostMethod("http://localhost:8080/sqlrest/PRODUCT/3/");
		RequestEntity body=new StringRequestEntity(POST_ALTERING_PRODUCT);
		method.setRequestEntity(body);
		int status=client.executeMethod(method);
		assertEquals(200, status);
		byte[] response =method.getResponseBody();
		assertNotNull(response);
		assertTrue(response.length==0);
	}
}
