package com.systex.jbranch.platform.common.security.privilege;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsyssecuassVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysuserVO;
import com.systex.jbranch.platform.common.security.privilege.vo.AssignmentVO;
import com.systex.jbranch.platform.common.security.privilege.vo.PrivilegeVO;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 代理人管理類別，主要功能為建立、刪除、修改、查詢代理人記錄、
 *
 * @author Alex Lin
 * @version 2010/03/31
 */
@Transactional
@SuppressWarnings("unchecked")
public class AssignManagement {
// ------------------------------ FIELDS ------------------------------

    private DataAccessManager dataAccessManager;

// -------------------------- OTHER METHODS --------------------------

    /**
     * 將代理狀態改為生效
     *
     * @param assignmentId assignment id
     * @return 是否更新成功
     * @throws JBranchException jbranchexception
     */
    public boolean activate(String assignmentId) throws JBranchException {
        return updateStatus(assignmentId, Status.ACTIVE);
    }

    /**
     * 新增代理記錄
     *
     * @param assignmentId   assignment id
     * @param assignerId     要被代理的id
     * @param assignerRoleId 要被代理的角色id
     * @param receiverId     要代理的id
     * @param status         狀態
     * @param startDate      起始日
     * @param endDate        結束日
     * @throws JBranchException jbranchexception
     */
    public void createAssignment(String assignmentId, String assignerId, String assignerRoleId, String receiverId,
                                 Status status, Date startDate, Date endDate) throws JBranchException {
        TbsyssecuassVO vo = new TbsyssecuassVO();
        TbsysuserVO assigner = (TbsysuserVO) dataAccessManager.findByPKey(TbsysuserVO.TABLE_UID, assignerId);
        TbsysuserVO receiver = (TbsysuserVO) dataAccessManager.findByPKey(TbsysuserVO.TABLE_UID, receiverId);
        TBSYSSECUROLEVO assignerRole = (TBSYSSECUROLEVO) dataAccessManager.findByPKey(TBSYSSECUROLEVO.TABLE_UID, assignerRoleId);
        vo.setAssignmentId(assignmentId);
        vo.setStatus(String.valueOf(status.getStatus()));
        vo.setAssigner(assigner);
        vo.setAssignerRole(assignerRole);
        vo.setReceiver(receiver);
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);
        dataAccessManager.create(vo);
    }

    /**
     * 刪除代理人記錄
     *
     * @param assignmentId assignment id
     * @throws JBranchException jbranchexception
     */
    public void deleteAssignment(String assignmentId) throws JBranchException {
        TbsyssecuassVO vo = (TbsyssecuassVO) dataAccessManager.findByPKey(TbsyssecuassVO.TABLE_UID, assignmentId);
        dataAccessManager.delete(vo);
    }

    /**
     * 以代理人取得有效的被代理人的資料
     *
     * @param receiverId 代理人的id
     * @return 有效的被代理人的資料
     * @throws JBranchException 取得資料失敗
     */
    @Transactional(readOnly = true)
    public List<AssignmentVO> getActiveAssignmentByReceiverId(String receiverId) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsyssecuassVO.TABLE_UID);
        Date current = new Date();
        List vos = criteria.add(Restrictions.eq("receiver.tellerid", receiverId))
                .add(Restrictions.or(
                        Restrictions.eq("status", String.valueOf(Status.PERMANENT.getStatus())),
                        Restrictions.conjunction()
                                .add(Restrictions.eq("status", String.valueOf(Status.ACTIVE.getStatus())))
                                .add(Restrictions.le("startDate", current))
                                .add(Restrictions.ge("endDate", current))))
                .list();
        return transform(vos);
    }

    /**
     * 取得指定申請人的所有代理記錄
     *
     * @param userid 申請人代號
     * @return 指定申請人的所有代理記錄
     * @throws JBranchException 取得資料失敗
     */
    @Transactional(readOnly = true)
    public List<AssignmentVO> getAllAssignmentByAssigner(String userid) throws JBranchException {
        List vos = dataAccessManager.getHibernateCriteria(TbsyssecuassVO.TABLE_UID)
                .add(Restrictions.eq("assigner.tellerid", userid))
                .list();
        return transform(vos);
    }

    /**
     * 以代理人取得全部的被代理人的資料
     *
     * @param receiverId 代理人的id
     * @return 全部的被代理人的資料
     * @throws JBranchException 取得資料失敗
     */
    @Transactional(readOnly = true)
    public List<AssignmentVO> getAllAssignmentByReceiverId(String receiverId) throws JBranchException {
        List vos = dataAccessManager.getHibernateCriteria(TbsyssecuassVO.TABLE_UID)
                .add(Restrictions.eq("receiver.tellerid", receiverId))
                .addOrder(Order.desc("receiver.tellerid"))
                .list();
        return transform(vos);
    }

    /**
     * 取得代理人記錄
     *
     * @param assignmentid 記錄ID
     * @return 代理人記錄
     * @throws JBranchException 資料取得失敗
     */
    @Transactional(readOnly = true)
    public AssignmentVO getAssignment(String assignmentid) throws JBranchException {
        TbsyssecuassVO vo = (TbsyssecuassVO) dataAccessManager.findByPKey(TbsyssecuassVO.TABLE_UID, assignmentid);
        return new AssignmentVO(vo);
    }

    /**
     * 取得目前時間點內使用者可代理的代理記錄
     *
     * @param tellerId  receiver tellerId
     * @param startDate 開始時間
     * @param endDate   結束時間
     * @return 目前時間點內使用者可代理的代理記錄
     * @throws JBranchException 取得資料失敗
     */
    @Transactional(readOnly = true)
    public List<AssignmentVO> getAssignmentByReceiverId(String tellerId, Date startDate, Date endDate) throws JBranchException {
        List vos = dataAccessManager.getHibernateCriteria(TbsyssecuassVO.TABLE_UID)
                .add(Restrictions.eq("receiver.tellerid", tellerId))
                .add(Restrictions.le("startdate", startDate))
                .add(Restrictions.ge("endDate", endDate))
                .addOrder(Order.desc("receiver.tellerid"))
                .list();
        return transform(vos);
    }

    private List<AssignmentVO> transform(List<TbsyssecuassVO> vos) {
        List<AssignmentVO> roles = new ArrayList<AssignmentVO>(vos.size());
        for (TbsyssecuassVO vo : vos) {
            roles.add(new AssignmentVO(vo));
        }
        return roles;
    }

    /**
     * 取得指定代理設定記錄的對應代理權限記錄
     *
     * @param assignmentid 代理設定記錄ID
     * @return PrivilegeVO Collection 代理權限記錄
     * @throws JBranchException 取得資料失敗
     */
    @Transactional(readOnly = true)
    public Set<PrivilegeVO> getPrivilegeByAssignment(String assignmentid) throws JBranchException {
        Set<PrivilegeVO> set = new HashSet();
        TbsyssecuassVO TbsyssecuassVO2 = (TbsyssecuassVO) dataAccessManager.findByPKey(TbsyssecuassVO.TABLE_UID, assignmentid);
        PrivilegeManagement privilegeManagement = new PrivilegeManagement();
        String allPri = TbsyssecuassVO2.getStatus();
        PrivilegeVO privilegeVO = privilegeManagement.getPrivilege(allPri);
        set.add(privilegeVO);

        return set;
    }

    /**
     * 將代理狀態改為失效
     *
     * @param assignmentId assignment id
     * @return 是否更新成功
     * @throws JBranchException jbranchexception
     */
    public boolean passivate(String assignmentId) throws JBranchException {
        return updateStatus(assignmentId, Status.INACTIVE);
    }

    /**
     * 將代理狀態改成永久生效
     *
     * @param assignmentId assignment id
     * @return 是否更新成功
     * @throws JBranchException jbranchexception
     */
    public boolean permanentize(String assignmentId) throws JBranchException {
        return updateStatus(assignmentId, Status.PERMANENT);
    }

    private boolean updateStatus(String assignmentId, Status status) throws JBranchException {
        TbsyssecuassVO vo = (TbsyssecuassVO) dataAccessManager.findByPKey(TbsyssecuassVO.TABLE_UID, assignmentId, true);
        if (vo != null) {
            vo.setStatus(String.valueOf(status.getStatus()));
            dataAccessManager.update(vo);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 更新代理記錄
     *
     * @param assignmentId   assignment id
     * @param assignerId     要被代理的id
     * @param assignerRoleId 要被代理的角色id
     * @param receiverId     要代理的id
     * @param status         狀態
     * @param startDate      起始日
     * @param endDate        結束日
     * @throws JBranchException jbranchexception
     */
    public void updateAssignment(String assignmentId, String assignerId, String assignerRoleId, String receiverId,
                                 Status status, Date startDate, Date endDate) throws JBranchException {
        TbsyssecuassVO vo = (TbsyssecuassVO) dataAccessManager.findByPKey(TbsyssecuassVO.TABLE_UID, assignmentId, true);
        TbsysuserVO assigner = (TbsysuserVO) dataAccessManager.findByPKey(TbsysuserVO.TABLE_UID, assignerId);
        TbsysuserVO receiver = (TbsysuserVO) dataAccessManager.findByPKey(TbsysuserVO.TABLE_UID, receiverId);
        TBSYSSECUROLEVO assignerRole = (TBSYSSECUROLEVO) dataAccessManager.findByPKey(TBSYSSECUROLEVO.TABLE_UID, assignerRoleId);
        vo.setStatus(String.valueOf(status.getStatus()));
        vo.setAssigner(assigner);
        vo.setAssignerRole(assignerRole);
        vo.setReceiver(receiver);
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);
        dataAccessManager.update(vo);
    }

// -------------------------- ENUMERATIONS --------------------------

    public static enum Status {
        ACTIVE(0),
        PERMANENT(1),
        INACTIVE(2);
        private int status;

        Status(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public static Status valueOf(int id) {
            switch (id) {
                case 0:
                    return ACTIVE;
                case 1:
                    return PERMANENT;
                case 2:
                    return INACTIVE;
                default:
                    throw new IllegalArgumentException("no such Status");
            }
        }
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }
}
