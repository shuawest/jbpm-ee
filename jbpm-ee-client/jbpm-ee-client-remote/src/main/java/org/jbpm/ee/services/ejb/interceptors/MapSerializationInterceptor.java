package org.jbpm.ee.services.ejb.interceptors;

import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.drools.core.command.impl.GenericCommand;
import org.jboss.ejb.client.EJBClientInterceptor;
import org.jboss.ejb.client.EJBClientInvocationContext;
import org.jbpm.ee.services.model.LazyDeserializingMap;
import org.jbpm.ee.support.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapSerializationInterceptor implements EJBClientInterceptor {

	private static final Logger LOG = LoggerFactory.getLogger(MapSerializationInterceptor.class);
	
	@Override
	public void handleInvocation(EJBClientInvocationContext context) throws Exception {
		if(!InterceptorUtil.requiresClassloaderInterception(context.getViewClass(), context.getInvokedMethod())) {
			if(LOG.isDebugEnabled()) {
				LOG.debug("Method: "+context.getInvokedMethod().getName()+" does not require preprocessing.");
			}
			context.sendRequest();
			return;
		}
		LOG.info("Method: "+context.getInvokedMethod().getName()+" does require preprocessor; Parameter count: "+context.getParameters().length);
		
		
		//look for the map objects, and convert them to lazy deserializing map.
		for(int i=0, j=context.getParameters().length; i<j; i++) {
			Object parameter = context.getParameters()[i];
			if(parameter == null) {
				//skip.
				continue;
			}
			if(Map.class.isAssignableFrom(parameter.getClass())) {
				if(((Map)parameter).isEmpty())
				{
					//skip.
					continue;
				}
				
				LOG.info("Map found in parameters of method: "+context.getInvokedMethod().getName());
				LazyDeserializingMap map = new LazyDeserializingMap();
				map.putAll((Map<String, Object>)parameter);
				
				context.getParameters()[i] = map;
				LOG.debug("Replaced map with serializable map.");
			}
			if(GenericCommand.class.isAssignableFrom(parameter.getClass())) {
				LOG.info("Replace the command's map properties with lazy maps.");
				
				for(Field field : parameter.getClass().getDeclaredFields()) {
					LOG.info("Field: "+field);
					LOG.info("Field type: "+field.getType().toString());
					if(Map.class.isAssignableFrom(field.getType())) {
						LOG.info("Field is map.");
						LazyDeserializingMap map = new LazyDeserializingMap();
						Map<String, Object> vals = (Map<String, Object>)BeanUtils.getObjectViaGetter(field, parameter);
						LOG.info("Map reflected: "+ReflectionToStringBuilder.toString(vals));
						map.putAll(vals);
						BeanUtils.setObjectViaSetter(field, parameter, map);
						
						LOG.debug("Replaced map with lazy on: "+parameter.getClass().getName()+" with field: "+field.getName());
					}
				}
			}
			
			LOG.info("Reflected parameter: "+ReflectionToStringBuilder.toString(parameter));
		}

        context.sendRequest();
	}
	
	@Override
	public Object handleInvocationResult(EJBClientInvocationContext context) throws Exception {
		return context.getResult();
	}

}
