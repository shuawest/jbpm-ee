package org.jbpm.ee.client.adapter;

import java.util.List;
import java.util.Map;

import org.jbpm.ee.services.WorkItemService;
import org.jbpm.ee.services.ws.WorkItemServiceWS;
import org.jbpm.ee.services.ws.request.JaxbMapRequest;
import org.kie.api.runtime.process.WorkItem;

public class WorkItemServiceAdapter implements WorkItemService {

	private WorkItemServiceWS workItemService;
	
	public WorkItemServiceAdapter(WorkItemServiceWS workItemService) {
		this.workItemService = workItemService;
	}
	
	@Override
	public void completeWorkItem(long id, Map<String, Object> results) {
		this.workItemService.completeWorkItem(id, new JaxbMapRequest(results));
	}

	@Override
	public void abortWorkItem(long id) {
		this.workItemService.abortWorkItem(id);
	}

	@Override
	public WorkItem getWorkItem(long id) {
		return (WorkItem)this.workItemService.getWorkItem(id);
	}

	@Override
	public List<WorkItem> getWorkItemByProcessInstance(long processInstanceId) {
		return (List)this.workItemService.getWorkItemByProcessInstance(processInstanceId);
	}

}
