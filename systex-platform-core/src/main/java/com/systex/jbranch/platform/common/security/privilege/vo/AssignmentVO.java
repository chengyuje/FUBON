package com.systex.jbranch.platform.common.security.privilege.vo;

import com.systex.jbranch.platform.common.platformdao.table.TbsyssecuassVO;

import java.util.Date;

/**
 * 代理人記錄VO
 *
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
public class AssignmentVO {
// ------------------------------ FIELDS ------------------------------

    /** 代理人記錄ID */
    private String id;

    /** 申請人 */
    private UserVO assigner;

    /** 申請人角色 */
    private RoleVO assignerRole;

    /** 代理人 */
    private UserVO receiver;

    /** 代理狀態 */
    private String status;

    /** 結束日期 */
    private Date endDate;

    /** 起始日期 */
    private Date startDate;

// --------------------------- CONSTRUCTORS ---------------------------

    public AssignmentVO() {
    }

    public AssignmentVO(TbsyssecuassVO vo) {
        this(vo.getAssignmentId(), new UserVO(vo.getAssigner()),
                new RoleVO(vo.getAssignerRole()), new UserVO(vo.getReceiver()),
                vo.getStatus(), vo.getStartDate(), vo.getEndDate());
    }

    public AssignmentVO(String id, UserVO assigner, RoleVO assignerRole, UserVO receiver,
                        String status, Date startDate, Date endDate) {
        this.id = id;
        this.assigner = assigner;
        this.assignerRole = assignerRole;
        this.receiver = receiver;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public UserVO getAssigner() {
        return assigner;
    }

    public void setAssigner(UserVO assigner) {
        this.assigner = assigner;
    }

    public RoleVO getAssignerRole() {
        return assignerRole;
    }

    public void setAssignerRole(RoleVO assignerRole) {
        this.assignerRole = assignerRole;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserVO getReceiver() {
        return receiver;
    }

    public void setReceiver(UserVO receiver) {
        this.receiver = receiver;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.security.privilege.vo.AssignmentVO{" +
                "id='" + id + '\'' +
                ", assigner=" + assigner +
                ", assignerRole=" + assignerRole +
                ", receiver=" + receiver +
                ", status='" + status + '\'' +
                ", endDate=" + endDate +
                ", startDate=" + startDate +
                '}';
    }
}