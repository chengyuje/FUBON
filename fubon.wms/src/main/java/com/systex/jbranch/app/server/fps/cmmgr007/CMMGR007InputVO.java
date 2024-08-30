package com.systex.jbranch.app.server.fps.cmmgr007;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class CMMGR007InputVO extends PagingInputVO {

    public CMMGR007InputVO() {
    }

    private String jobid;
    private String updatejobid;    //2016.08.01; Sebastian; 加入被更新的JOBID
    private String jobname;
    private String description;
    private String parameters;
    private String precondition;
    private String postcondition;
    private String type;
    private String classname;
    private String beanid;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getBeanid() {
        return beanid;
    }

    public void setBeanid(String beanid) {
        this.beanid = beanid;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public String getUpdatejobid() {
        return updatejobid;
    }

    public void setUpdatejobid(String updatejobid) {
        this.updatejobid = updatejobid;
    }

    public String getJobname() {
        return jobname;
    }

    public void setJobname(String jobname) {
        this.jobname = jobname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getPrecondition() {
        return precondition;
    }

    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }

    public String getPostcondition() {
        return postcondition;
    }

    public void setPostcondition(String postcondition) {
        this.postcondition = postcondition;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
