// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   WebBasicWorkflow.java

package com.systex.jbranch.platform.server.workflow.osworkflow;

import com.opensymphony.workflow.AbstractWorkflow;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.basic.BasicWorkflowContext;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;
import com.opensymphony.workflow.spi.Step;
import com.opensymphony.workflow.spi.WorkflowEntry;
import com.opensymphony.workflow.spi.WorkflowStore;
import com.systex.jbranch.platform.server.workflow.spi.memory.TransientWorkflowStore;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;

// Referenced classes of package ahxu.osworkflow.example:
//            WebWorkflow


@Transactional
public class WebBasicWorkflow extends AbstractWorkflow
    implements WebWorkflow
{

    public WebBasicWorkflow(String caller)
    {
        super.context = new BasicWorkflowContext(caller);
    }

    public WebBasicWorkflow()
    {
    }

    public void setCaller(String caller)
    {
        if(caller == null)
            throw new IllegalArgumentException("Argument caller is Null!");
        if(super.context == null)
            super.context = new BasicWorkflowContext(caller);
        else
            throw new IllegalArgumentException((new StringBuilder()).append("the workflow ").append(toString()).append(" has already init the Context.").toString());
    }

    @Override
    protected void checkImplicitFinish(ActionDescriptor action, long id) throws WorkflowException {
        WorkflowStore store = getPersistence();
        WorkflowEntry entry = store.findEntry(id);

        WorkflowDescriptor wf = getConfiguration().getWorkflow(entry.getWorkflowName());

        Collection currentSteps = store.findCurrentSteps(id);

        boolean isCompleted = true;

        for (Iterator iterator = currentSteps.iterator(); iterator.hasNext();) {
            Step step = (Step) iterator.next();
            StepDescriptor stepDes = wf.getStep(step.getStepId());

            // if at least on current step have an available action
            if (stepDes.getActions().size() > 0) {
                isCompleted = false;
            }
        }

        if (isCompleted) {
            WorkflowStore workflowStore = getPersistence();
            if (TransientWorkflowStore.class.isInstance(workflowStore)) {
                TransientWorkflowStore.clear(id);
            }
            else {
                completeEntry(action, id, currentSteps, WorkflowEntry.COMPLETED);
            }
        }
    }
}
