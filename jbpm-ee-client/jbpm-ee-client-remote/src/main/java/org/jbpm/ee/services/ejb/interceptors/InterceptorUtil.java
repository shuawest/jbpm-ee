package org.jbpm.ee.services.ejb.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.jbpm.ee.services.ejb.annotations.PreprocessClassloader;
import org.jbpm.ee.services.ejb.annotations.ProcessInstanceId;
import org.jbpm.ee.services.ejb.annotations.ReleaseId;
import org.jbpm.ee.services.ejb.annotations.TaskId;
import org.jbpm.ee.services.ejb.annotations.WorkItemId;
import org.jbpm.ee.services.model.KieReleaseId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterceptorUtil {

	private static final Set<String> noClassloaderRequired = new HashSet<String>(); 
	private static final Set<String> classloaderRequired = new HashSet<String>(); 
	
	private static final Logger LOG = LoggerFactory.getLogger(InterceptorUtil.class);
	
	private InterceptorUtil() {
		// seal
	}
	
	public static boolean requiresClassloaderInterception(Class clz, Method method) {
		//cache reflection.
		if(noClassloaderRequired.contains(clz.getName()+method.hashCode())) {
			return false;
		}
		if(classloaderRequired.contains(clz.getName()+method.hashCode())) {
			return true;
		}
		
		if(method.isAnnotationPresent(PreprocessClassloader.class)) {
			return true;
		}
		else {
			//check the other interfaces..
			for(Class clzz : clz.getInterfaces()) {
				try {
					Method superInterfaceMethod = clzz.getMethod(method.getName(), method.getParameterTypes());
					boolean response = requiresClassloaderInterception(clzz, superInterfaceMethod);
					
					if(response == true) {
						//add to cache.
						classloaderRequired.add(clz.getName()+method.hashCode());
						return true;
					}
				}
				catch(NoSuchMethodException e) {
					//do nothing.
				}
			}
		}
		//add to cache.
		noClassloaderRequired.add(clz.getName()+method.hashCode());
		return false;
	}
	
	public static KieReleaseId extractReleaseId(Class clz, Method method, Object[] parameters) {
		return (KieReleaseId)extractMethodParameterValue(clz, method, parameters, ReleaseId.class);
	}
	
	public static Long extractProcessInstanceId(Class clz, Method method, Object[] parameters) {
		return (Long)extractMethodParameterValue(clz, method, parameters, ProcessInstanceId.class);
	}
	
	public static Long extractTaskId(Class clz, Method method, Object[] parameters) {
		return (Long)extractMethodParameterValue(clz, method, parameters, TaskId.class);
	}
	
	public static Long extractWorkItemId(Class clz, Method method, Object[] parameters) {
		return (Long)extractMethodParameterValue(clz, method, parameters, WorkItemId.class);
	}
	
	public static Object extractMethodParameterValue(Class clz, Method method, Object[] parameters, Class annotation) {
		//annotations on all parameters
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		for(int i=0, j=parameters.length; i<j; i++) {
			//annotations on each parameter
			for(Annotation parameterAnnotation : parameterAnnotations[i]) {
				if(annotation.isAssignableFrom(parameterAnnotation.annotationType())) {
					return parameters[i];
				}
			}
		}
		
		//look to parent.
		for(Class parent : clz.getInterfaces()) {
			//get the method for the parent
			try {
				Method parentMethod = parent.getMethod(method.getName(), method.getParameterTypes());
				Object obj = extractMethodParameterValue(parent, parentMethod, parameters, annotation);
				if(obj != null) {
					return obj;
				}
			}
			catch(NoSuchMethodException e) {
				
			}
		}
		
		return null;
	}
}
