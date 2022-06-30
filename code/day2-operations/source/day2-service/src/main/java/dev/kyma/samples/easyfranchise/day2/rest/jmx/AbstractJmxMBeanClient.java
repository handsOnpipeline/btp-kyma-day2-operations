/**
 * This class implement interface JmxMBeanClientInterface, and define connection status of JMX client 
 */
package dev.kyma.samples.easyfranchise.day2.rest.jmx;

import java.io.IOException;
import java.util.List;
import javax.management.AttributeList;


public abstract class AbstractJmxMBeanClient implements JmxMBeanClientInterface {

	private enum Status {CONNECTION_OPENED,CONNECTION_CLOSED};
	
	private Status innerStatus = Status.CONNECTION_CLOSED;
	
	protected abstract <T> T doRetrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey ,Class<T> type) throws Exception;
	
	public abstract Object doRetrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey) throws Exception;

	protected abstract void doOpenConnection() throws IOException;
	
	protected abstract void doCloseConnection() throws IOException;
	
	public <T> T retrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey ,Class<T> type) throws Exception	{
		if( innerStatus!= Status.CONNECTION_OPENED)
			throw new RuntimeException("Connection must be opened before retrieving an MBean attribute");
		return doRetrieveAttributeValue(queryObject,queryAttribute,isComposite,queryCompositeKey,type);
	}
	
	public Object retrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey ) throws Exception	{
		if( innerStatus!= Status.CONNECTION_OPENED)
			throw new RuntimeException("Connection must be opened before retrieving an MBean attribute");
		return doRetrieveAttributeValue(queryObject,queryAttribute,isComposite,queryCompositeKey);
	}
	
	public AttributeList retriveAttributeList(String queryObject, String[] attributes) throws Exception{
		if( innerStatus!= Status.CONNECTION_OPENED)
			throw new RuntimeException("Connection must be opened before retrieving an MBean attribute");
		return doRetrieveAttributeValue(queryObject, attributes);
	}
	
	public abstract AttributeList doRetrieveAttributeValue(String queryObject, String[] attributes) throws Exception;
	
	public List<String> retrieveBeanNames(String domainName) throws Exception {
		if( innerStatus!= Status.CONNECTION_OPENED)
			throw new RuntimeException("Connection must be opened before retrieving Mbean name");
		return doRetrieveBeanNames(domainName);
	}

	public abstract List<String> doRetrieveBeanNames(String domainName) throws Exception;

	public void openConnection() throws IOException
	{
		doOpenConnection();
		innerStatus = Status.CONNECTION_OPENED;
	}
	
	public void closeConnection() throws IOException
	{
		doCloseConnection();
		innerStatus = Status.CONNECTION_CLOSED;
	}
}
