package dev.kyma.samples.easyfranchise.day2.rest.jmx;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.AttributeList;


public class JmxMBeanClient extends AbstractJmxMBeanClient {

	JMXConnector jmxc;

	protected String remoteHost;

	protected int remotePort;

	protected String username;

	protected String password;

	public JmxMBeanClient(String remoteHost, int remotePort) {
		super();
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
	}

	public JmxMBeanClient(String remoteHost, int remotePort, String username, String password) {
		super();
		this.remoteHost = remoteHost;
		this.remotePort = remotePort;
		this.username = username;
		this.password = password;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T doRetrieveAttributeValue(String queryObject, String queryAttribute, boolean isComposite,
			String queryCompositeKey, Class<T> type) throws AttributeNotFoundException, InstanceNotFoundException,
			MalformedObjectNameException, MBeanException, ReflectionException, IOException {
		Object o = jmxc.getMBeanServerConnection().getAttribute(new ObjectName(queryObject), queryAttribute);

		if (isComposite) {
			CompositeData cd = (CompositeData) o;
			return (T) cd.get(queryCompositeKey);
		} else
			return (T) o;
	}

	@Override
	public Object doRetrieveAttributeValue(String queryObject, String queryAttribute, boolean isComposite,
			String queryCompositeKey) throws Exception {

		Object o = jmxc.getMBeanServerConnection().getAttribute(new ObjectName(queryObject), queryAttribute);

		if (isComposite) {
			CompositeData cd = (CompositeData) o;
			return cd.get(queryCompositeKey);
		} else
			return o;
	}

	@Override
	public void doCloseConnection() throws IOException {
		jmxc.close();
	}

	@Override
	public void doOpenConnection() throws IOException {
		JMXServiceURL url = new JMXServiceURL(
				"service:jmx:rmi:///jndi/rmi://" + remoteHost + ":" + remotePort + "/jmxrmi");

		if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
			String[] creds = { username, password };
			Map<String, Object> env = new HashMap<String, Object>();
			env.put(JMXConnector.CREDENTIALS, creds);

			jmxc = JMXConnectorFactory.connect(url, env);
		} else
			jmxc = JMXConnectorFactory.connect(url, null);

	}

	@Override
	public List<String> doRetrieveBeanNames(String domainName) throws Exception {

		ObjectName queryName = null;
		if( domainName != null) {
			queryName = new ObjectName(domainName);
		}

		Set<ObjectName> names = jmxc.getMBeanServerConnection().queryNames(queryName, null);

		List<String> results = new ArrayList<String>(names.size());
		for(ObjectName name : names){
			results.add(name.getCanonicalName());
		}
		Collections.sort(results);
		return results;
	}

	@Override
	public AttributeList doRetrieveAttributeValue(String queryObject, String[] attributes) throws Exception{
		ObjectName queryName = null;
		if(queryObject != null){
			queryName = new ObjectName(queryObject);
		}

		AttributeList attributeList = jmxc.getMBeanServerConnection().getAttributes(queryName, attributes);
		return attributeList;
	}
}