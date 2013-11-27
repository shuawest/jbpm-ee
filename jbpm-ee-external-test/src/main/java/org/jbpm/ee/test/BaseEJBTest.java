package org.jbpm.ee.test;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebMethod;

import org.drools.core.command.runtime.process.StartProcessCommand;
import org.jbpm.ee.services.AsyncCommandExecutor;
import org.jbpm.ee.test.exception.TestRuntimeException;
import org.slf4j.Logger;

public abstract class BaseEJBTest extends BaseTest {

	private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(BaseEJBTest.class);
	
	protected abstract AsyncCommandExecutor getAsyncCommandExecutor();
	
	/**
	 * List number of tasks; creates a new process; rolls back.
	 * Number of tasks should remain consistent.
	 */
	@WebMethod
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void startProcessThenRollback() {
		try {
			this.startProcess();
			generateException();
		}
		catch(RuntimeException e) {
			LOG.info("Started process, then rolled back.  Number of tasks should remain the same before this test was executed.");
			throw e;
		}
	}
	

	@WebMethod
	public void startProcessAsync() {
		StartProcessCommand startProcessCommand = new StartProcessCommand();
		startProcessCommand.setProcessId(taskTestProcessId);
		
		final String variableKey = "processString";
		
		Map<String, Object> processVariables = new HashMap<String, Object>();
		processVariables.put(variableKey, "Initial");
		startProcessCommand.setParameters(processVariables);		
		
		getAsyncCommandExecutor().execute(taskTestReleaseId, startProcessCommand);
	}
	
	private void generateException() {
		throw new TestRuntimeException("Exception to show rollback.");
	}
}
