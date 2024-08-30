package com.systex.jbranch.platform.common.platformdao.table;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.sql.Timestamp;
import java.util.Date;

/** @author Hibernate CodeGenerator */
public class TbsyssecuassVO extends VOBase {
// ------------------------------ FIELDS ------------------------------

    public static final String TABLE_UID = "com.systex.jbranch.platform.common.platformdao.table.Tbsyssecuass";
    private String assignmentId;
    private TbsysuserVO assigner;
    private TbsysuserVO receiver;
    private TBSYSSECUROLEVO assignerRole;
    private Date startDate;
    private Date endDate;
    private String status;

// --------------------------- CONSTRUCTORS ---------------------------

    public TbsyssecuassVO() {
    }

    public TbsyssecuassVO(String assignmentId, TbsysuserVO assigner, TbsysuserVO receiver, TBSYSSECUROLEVO assignerRole,
                          Timestamp startDate, Timestamp endDate, String status) {
        this.assignmentId = assignmentId;
        this.assigner = assigner;
        this.receiver = receiver;
        this.assignerRole = assignerRole;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public TbsyssecuassVO(String assignmentId, TbsysuserVO assigner, TbsysuserVO receiver, TBSYSSECUROLEVO assignerRole,
                          Timestamp startDate, Timestamp endDate, String status,
                          String creator, Timestamp createtime, Timestamp lastupdate, String modifier, Long version) {
        this(assignmentId, assigner, receiver, assignerRole, startDate, endDate, status);
        this.creator = creator;
        this.createtime = createtime;
        this.lastupdate = lastupdate;
        this.modifier = modifier;
        this.version = version;
    }

// -------------------------- OTHER METHODS --------------------------

    public String getTableuid() {
        return TABLE_UID;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public TbsysuserVO getAssigner() {
        return assigner;
    }

    public void setAssigner(TbsysuserVO assigner) {
        this.assigner = assigner;
    }

    public TBSYSSECUROLEVO getAssignerRole() {
        return assignerRole;
    }

    public void setAssignerRole(TBSYSSECUROLEVO assignerRole) {
        this.assignerRole = assignerRole;
    }

    public String getAssignmentId() {
        return this.assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public TbsysuserVO getReceiver() {
        return receiver;
    }

    public void setReceiver(TbsysuserVO receiver) {
        this.receiver = receiver;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

// ------------------------ CANONICAL METHODS ------------------------

    public String toString() {
        return new ToStringBuilder(this)
                .append("assignmentId", getAssignmentId())
                .toString();
    }
}
