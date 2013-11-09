package org.jbpm.ee.rest.impl;

import javax.inject.Inject;

import org.jbpm.ee.ejb.local.RuleRuntimeBean;
import org.jbpm.ee.rest.RuleRuntimeRest;

public class RuleRuntimeRestImpl implements RuleRuntimeRest {

	@Inject
	private RuleRuntimeBean ruleRuntime;
	
	@Override
	public int fireAllRules(Long processInstanceId) {
		return ruleRuntime.fireAllRules(processInstanceId);
	}

	@Override
	public int fireAllRules(Long processInstanceId, int max) {
		return ruleRuntime.fireAllRules(processInstanceId, max);
	}

	@Override
	public void insert(Long processInstanceId, Object object) {
		ruleRuntime.insert(processInstanceId, object);
	}

}