package org.jbpm.ee.client.adapter;

import org.jbpm.ee.services.RuleService;
import org.jbpm.ee.services.ws.RuleServiceWS;

public class RuleServiceAdapter implements RuleService {

	private final RuleServiceWS ruleService;
	
	public RuleServiceAdapter(RuleServiceWS ruleService) {
		this.ruleService = ruleService;
	}
	
	@Override
	public int fireAllRules(Long processInstanceId) {
		return this.ruleService.fireAllRules(processInstanceId);
	}

	@Override
	public int fireAllRules(Long processInstanceId, int max) {
		return this.ruleService.fireAllRules(processInstanceId, max);
	}

	@Override
	public void insert(Long processInstanceId, Object object) {
		this.ruleService.insert(processInstanceId, object);
	}

}
