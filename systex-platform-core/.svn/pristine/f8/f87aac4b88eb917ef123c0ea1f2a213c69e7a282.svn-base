package com.systex.jbranch.platform.common.security.privilege;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysorgVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysprojectVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysuserVO;
import com.systex.jbranch.platform.common.security.privilege.vo.OrgVO;
import com.systex.jbranch.platform.common.security.privilege.vo.ProjectVO;
import com.systex.jbranch.platform.common.security.privilege.vo.UserVO;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Alex Lin
 * @version 2010/01/25 4:37:53 PM
 */
@SuppressWarnings("unchecked")
@Transactional
public class ProjectManagement {
// ------------------------------ FIELDS ------------------------------

    private DataAccessManager dataAccessManager;

// -------------------------- OTHER METHODS --------------------------

    /**
     * 新增組織
     *
     * @param projectId 專案代號
     * @param divNos    組織代號
     * @throws JBranchException jbranchexception
     */
    public void addOrgs(String projectId, String... divNos) throws JBranchException {
        if (!ArrayUtils.isEmpty(divNos)) {
            List<TbsysorgVO> orgs = dataAccessManager.getHibernateCriteria(TbsysorgVO.TABLE_UID).add(Restrictions.in("divNo", divNos)).list();

            TbsysprojectVO project = (TbsysprojectVO) dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID).add(Restrictions.eq("projectId", projectId)).setFetchMode("orgs", FetchMode.JOIN).uniqueResult();
            project.getOrgs().addAll(orgs);
            dataAccessManager.update(project);
        }
    }

    /**
     * 新增專案
     *
     * @param id   專案代號
     * @param name 專案名稱
     * @param desc 專案描述
     * @throws JBranchException jbranchexception
     */
    public void addProject(String id, String name, String desc) throws JBranchException {
        TbsysprojectVO vo = new TbsysprojectVO(id, name, desc);
        dataAccessManager.create(vo);
    }

    /**
     * 新增使用者
     *
     * @param projectId 專案代號
     * @param userIds   使用者代號
     * @throws JBranchException jbranchexception
     */
    public void addUsers(String projectId, String... userIds) throws JBranchException {
        if (!ArrayUtils.isEmpty(userIds)) {
            List<TbsysuserVO> users = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID).add(Restrictions.in("tellerid", userIds)).list();

            TbsysprojectVO project = (TbsysprojectVO) dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID).add(Restrictions.eq("projectId", projectId)).setFetchMode("users", FetchMode.JOIN).uniqueResult();
            project.getUsers().addAll(users);
            dataAccessManager.update(project);
        }
    }

    /**
     * 取得完整專案資訊，包含使用者及組織
     *
     * @param projectId 專案代號
     * @return 專案
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public ProjectVO getFullProjectVO(String projectId) throws JBranchException {
        TbsysprojectVO project = (TbsysprojectVO) dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID)
                .add(Restrictions.eq("projectId", projectId))
                .setFetchMode("users", FetchMode.JOIN)
                .setFetchMode("orgs", FetchMode.JOIN).uniqueResult();
        ProjectVO vo = new ProjectVO(project.getProjectId(), project.getProjectName(), project.getDescription());

        Set<UserVO> users = new HashSet<UserVO>(project.getUsers().size());
        for (TbsysuserVO user : project.getUsers()) {
            users.add(new UserVO(user.getTellerid(), user.getName(), user.getStatus(), user.getUseflag()));
        }
        vo.setUsers(users);

        Set<OrgVO> orgs = new HashSet<OrgVO>();
        for (TbsysorgVO org : project.getOrgs()) {
            orgs.add(new OrgVO(org.getDivNo(), org.getDivName(), org.getDescription()));
        }
        vo.setOrgs(orgs);

        return vo;
    }

    /**
     * 取得專案資訊，不包含使用者及組織
     *
     * @param projectId 專案代號
     * @return 專案
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public ProjectVO getProject(String projectId) throws JBranchException {
        TbsysprojectVO project = (TbsysprojectVO) dataAccessManager.findByPKey(TbsysprojectVO.TABLE_UID, projectId);
        return new ProjectVO(project.getProjectId(), project.getProjectName(), project.getDescription());
    }

    /**
     * 取得專案資訊，包含組織
     *
     * @param projectId 專案代號
     * @return 專案
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public ProjectVO getProjectWithOrgs(String projectId) throws JBranchException {
        TbsysprojectVO project = (TbsysprojectVO) dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID)
                .add(Restrictions.eq("projectId", projectId))
                .setFetchMode("orgs", FetchMode.JOIN).uniqueResult();
        ProjectVO vo = new ProjectVO(project.getProjectId(), project.getProjectName(), project.getDescription());
        Set<OrgVO> orgs = new HashSet<OrgVO>();
        for (TbsysorgVO org : project.getOrgs()) {
            orgs.add(new OrgVO(org.getDivNo(), org.getDivName(), org.getDescription()));
        }
        vo.setOrgs(orgs);
        return vo;
    }

    /**
     * 取得專案資訊，包含使用者
     *
     * @param projectId 專案代號
     * @return 專案
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public ProjectVO getProjectWithUsers(String projectId) throws JBranchException {
        TbsysprojectVO project = (TbsysprojectVO) dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID)
                .add(Restrictions.eq("projectId", projectId))
                .setFetchMode("users", FetchMode.JOIN).uniqueResult();
        ProjectVO vo = new ProjectVO(project.getProjectId(), project.getProjectName(), project.getDescription());
        Set<UserVO> users = new HashSet<UserVO>(project.getUsers().size());
        for (TbsysuserVO user : project.getUsers()) {
            users.add(new UserVO(user.getTellerid(), user.getName(), user.getStatus(), user.getUseflag()));
        }
        vo.setUsers(users);
        return vo;
    }

    /**
     * 以組織代號取得專案
     *
     * @param divNo 組織代號
     * @return 專案清單
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<ProjectVO> getProjectsByOrg(String divNo) throws JBranchException {
        List<TbsysprojectVO> projects = dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID)
                .createCriteria("orgs")
                .add(Restrictions.eq("divNo", divNo)).list();
        List<ProjectVO> vos = new ArrayList<ProjectVO>(projects.size());
        for (TbsysprojectVO project : projects) {
            vos.add(new ProjectVO(project.getProjectId(), project.getProjectName(), project.getDescription()));
        }
        return vos;
    }

    /**
     * 以使用者代號取得專案
     *
     * @param userId 使用者代號
     * @return 專案清單
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<ProjectVO> getProjectsByUser(String userId) throws JBranchException {
        List<TbsysprojectVO> projects = dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID)
                .createCriteria("users")
                .add(Restrictions.eq("tellerid", userId)).list();
        List<ProjectVO> vos = new ArrayList<ProjectVO>(projects.size());
        for (TbsysprojectVO project : projects) {
            vos.add(new ProjectVO(project.getProjectId(), project.getProjectName(), project.getDescription()));
        }
        return vos;
    }

    /**
     * 設定組織，將刪除原設定
     *
     * @param projectId 專案代號
     * @param divNos    組織代號
     * @throws JBranchException jbranchexception
     */
    public void setOrgs(String projectId, String... divNos) throws JBranchException {
        List<TbsysorgVO> orgs;
        if (!ArrayUtils.isEmpty(divNos)) {
            orgs = dataAccessManager.getHibernateCriteria(TbsysorgVO.TABLE_UID).add(Restrictions.in("divNo", divNos)).list();
        }
        else {
            orgs = Collections.emptyList();
        }
        TbsysprojectVO project = (TbsysprojectVO) dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID)
                .add(Restrictions.eq("projectId", projectId))
                .setFetchMode("orgs", FetchMode.JOIN).uniqueResult();
        project.setOrgs(new HashSet(orgs));
        dataAccessManager.update(project);
    }

    /**
     * 設定使用者，將刪除原設定
     *
     * @param projectId 專案代號
     * @param userIds   使用者代號
     * @throws JBranchException jbranchexception
     */
    public void setUsers(String projectId, String... userIds) throws JBranchException {
        List<TbsysuserVO> users;
        if (!ArrayUtils.isEmpty(userIds)) {
            users = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID).add(Restrictions.in("tellerid", userIds)).list();
        }
        else {
            users = Collections.emptyList();
        }

        TbsysprojectVO project = (TbsysprojectVO) dataAccessManager.getHibernateCriteria(TbsysprojectVO.TABLE_UID)
                .add(Restrictions.eq("projectId", projectId))
                .setFetchMode("users", FetchMode.JOIN).uniqueResult();
        project.setUsers(new HashSet(users));
        dataAccessManager.update(project);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }
}
