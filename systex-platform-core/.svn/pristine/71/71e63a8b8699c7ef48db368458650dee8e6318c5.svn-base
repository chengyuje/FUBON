package com.systex.jbranch.platform.server.bizLogic;

import java.lang.reflect.Method;
import java.util.Map;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

public abstract class AbstractServiceProvider implements FunctionProvider {
	
	public static final String ARGS_INPUT_PARAM_ID="inputParamId";
	public static final String ARGS_OUTPUT_PARAM_ID="outputParamId";
	public static final String ARGS_PARAMS_ID="paramsId";
	public static final String ARGS_METHOD_NAME="method";
	
	public static final String DEFAULT_INPUT_PARAM_ID="inputParam";
	public static final String DEFAULT_OUTPUT_PARAM_ID="outputParam";
	public static final String DEFAULT_PARAMS_ID="params";
	
	public static final String DEFAULT_METHOD_NAME="invoke";
	
	public void execute(Map transientVars, Map args, PropertySet propertySet)
			throws WorkflowException {

		try
		{
			IWorkFlowItem workFlowItem=new OSWorkFlowItem(transientVars,args,propertySet);
			
			Method method=getMethod(workFlowItem.getMethodName());
			Object input=workFlowItem.getInput();
			Object output=method.invoke(this,input,workFlowItem.getParams());
			workFlowItem.setOutput(output);
		}
		catch (Exception e)
		{
			throw new WorkflowException(e);
		}
	}

	private Method getMethod(String name) throws SecurityException, NoSuchMethodException
	{
		return this.getClass().getMethod(name,Object.class,Map.class);
	}
	
}

interface IWorkFlowItem
{
	String getMethodName();
	Map getParams();
	Object getInput();
	void setOutput(Object value);
}

class OSWorkFlowItem implements IWorkFlowItem
{
	private Map transientVars;
	private Map args;
	private PropertySet propertySet;
	
	public OSWorkFlowItem(Map transientVars, Map args, PropertySet propertySet)
	{
		this.transientVars=transientVars;
		this.args=args;
		this.propertySet=propertySet;
	}

	public String getMethodName()
	{
		if(args.containsKey(AbstractServiceProvider.ARGS_METHOD_NAME))
			return args.get(AbstractServiceProvider.ARGS_METHOD_NAME).toString();
		else
			return AbstractServiceProvider.DEFAULT_METHOD_NAME;
	}
	
	public Map getParams()
	{
		String key;
		if(args.containsKey(AbstractServiceProvider.ARGS_PARAMS_ID))
			key=args.get(AbstractServiceProvider.ARGS_PARAMS_ID).toString();
		else
			key=AbstractServiceProvider.DEFAULT_PARAMS_ID;
		
		return (Map)transientVars.get(key);
	}
	
	public Object getInput()
	{
		String key;
		if(args.containsKey(AbstractServiceProvider.ARGS_INPUT_PARAM_ID))
			key=args.get(AbstractServiceProvider.ARGS_INPUT_PARAM_ID).toString();
		else
			key=AbstractServiceProvider.DEFAULT_INPUT_PARAM_ID;
		
		return transientVars.get(key);
	}

	public void setOutput(Object value)
	{
		String key;
		if(args.containsKey(AbstractServiceProvider.ARGS_OUTPUT_PARAM_ID))
			key=args.get(AbstractServiceProvider.ARGS_OUTPUT_PARAM_ID).toString();
		else
			key=AbstractServiceProvider.DEFAULT_OUTPUT_PARAM_ID;
			
		transientVars.put(key, value);
		transientVars.put(AbstractServiceProvider.ARGS_OUTPUT_PARAM_ID, key);
	}
}