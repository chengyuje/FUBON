// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   WebWorkflow.java

package com.systex.jbranch.platform.server.workflow.osworkflow;

import com.opensymphony.workflow.Workflow;

public interface WebWorkflow
    extends Workflow
{

    public abstract void setCaller(String s);
}
