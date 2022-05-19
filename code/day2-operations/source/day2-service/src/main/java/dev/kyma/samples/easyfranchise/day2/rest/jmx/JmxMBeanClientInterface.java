package dev.kyma.samples.easyfranchise.day2.rest.jmx;

import java.io.IOException;
import java.util.List;
import javax.management.AttributeList;

public interface JmxMBeanClientInterface{


	public Object retrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey) throws Exception;
	
	public <T> T retrieveAttributeValue(String queryObject,String queryAttribute, boolean isComposite,String queryCompositeKey ,Class<T> type) throws Exception;

	public AttributeList retriveAttributeList(String queryObject, String[] attributes) throws Exception;

	public List<String> retrieveBeanNames(String domainName) throws Exception;

	public void openConnection() throws IOException;
	
	public void closeConnection() throws IOException;

}
