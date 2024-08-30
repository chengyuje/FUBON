package com.systex.jbranch.platform.common.security.privilege;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysorgVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysuserVO;
import com.systex.jbranch.platform.common.security.privilege.vo.OrgVO;
import com.systex.jbranch.platform.common.security.privilege.vo.UserVO;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex Lin
 * @version 2010/03/10 5:08:48 PM
 */
@SuppressWarnings("unchecked")
@Transactional
public class UserManagement {
// ------------------------------ FIELDS ------------------------------

    private DataAccessManager dataAccessManager;

// -------------------------- OTHER METHODS --------------------------

    /**
     * 新增使用者
     *
     * @param vo       使用者VO
     * @param password 密碼
     * @param divNo    單位代號
     * @throws JBranchException jbranchexception
     */
    public void addUser(UserVO vo, String password, String divNo) throws JBranchException {
        TbsysuserVO tbsysuserVO = new TbsysuserVO();
        tbsysuserVO.setTellerid(vo.getTellerId());
        tbsysuserVO.setName(vo.getName());
        tbsysuserVO.setStatus(vo.getStatus());
        tbsysuserVO.setErrlevel("");
        tbsysuserVO.setSessnum((short) 1);
        tbsysuserVO.setUseflag(vo.getUseflag());
        tbsysuserVO.setPassword(password);

        TbsysorgVO tbsysorgVO = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, divNo);
        tbsysuserVO.setOrg(tbsysorgVO);
        dataAccessManager.create(tbsysuserVO);
    }

    /**
     * 　以單位代號取得使用者
     *
     * @param divNo 單位代號
     * @return 使用者
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersByDivNo(String divNo) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID);
        List<TbsysuserVO> users = criteria.add(Restrictions.eq("org.divNo", divNo)).list();
        return transformUser(users);
    }

    /**
     * 以角色取得使用者
     *
     * @param roleId 角色代號
     * @return 使用者
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersByRole(String roleId) throws JBranchException {
        List<TbsysuserVO> users = findUserByRole(roleId);
        return transformUser(users);
    }

    private List<UserVO> transformUser(List<TbsysuserVO> vos) {
        List<UserVO> users = new ArrayList<UserVO>(vos.size());
        for (TbsysuserVO vo : vos) {
            users.add(new UserVO(vo));
        }
        return users;
    }

    @Transactional(readOnly = true)
    public List<UserVO> getUsersWithBranchByRole(String roleId) throws JBranchException {
        List<TbsysuserVO> users = findUserByRole(roleId);
        return transformUserWithBranch(users);
    }

    private List<TbsysuserVO> findUserByRole(String roleId) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID);
        return (List<TbsysuserVO>) criteria.createCriteria("roles").add(Restrictions.eq("ROLEID", roleId)).list();
    }

    private List<UserVO> transformUserWithBranch(List<TbsysuserVO> vos) {
        List<UserVO> users = new ArrayList<UserVO>(vos.size());
        for (TbsysuserVO vo : vos) {
            UserVO userVO = new UserVO(vo);
            userVO.setBranch(new OrgVO(vo.getOrg()));
            users.add(userVO);
        }
        return users;
    }

    /**
     * 取得使用者，包含單位資訊
     *
     * @param tellerIds 使用者代號
     * @return 使用者
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<UserVO> getUsersWithOrg(String... tellerIds) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID);
        List<TbsysuserVO> vos = criteria.add(Restrictions.in("tellerid", tellerIds)).list();
        List<UserVO> users = new ArrayList<UserVO>(vos.size());
        for (TbsysuserVO vo : vos) {
            UserVO user = new UserVO(vo);
            user.setBranch(new OrgVO(vo.getOrg()));
            users.add(user);
        }
        return users;
    }

    /**
     * 加入單位
     *
     * @param tellerId 使用者代號
     * @param divNo    單位代號
     * @throws JBranchException jbranchexception
     */
    public void joinBranch(String tellerId, String divNo) throws JBranchException {
        TbsysuserVO tbsysuserVO = (TbsysuserVO) dataAccessManager.findByPKey(TbsysuserVO.TABLE_UID, tellerId);
        TbsysorgVO tbsysorgVO = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, divNo);
        tbsysuserVO.setOrg(tbsysorgVO);
        dataAccessManager.update(tbsysuserVO);
    }

    /**
     * 修改使用者
     *
     * @param vo       使用者VO
     * @param password 密碼
     * @throws JBranchException jbranchexception
     */
    public void updateUser(UserVO vo, String password) throws JBranchException {
        TbsysuserVO tbsysuserVO = (TbsysuserVO) dataAccessManager.findByPKey(TbsysuserVO.TABLE_UID, vo.getTellerId());
        tbsysuserVO.setName(vo.getName());
        tbsysuserVO.setStatus(vo.getStatus());
        tbsysuserVO.setUseflag(vo.getUseflag());
        tbsysuserVO.setPassword(password);

        dataAccessManager.update(tbsysuserVO);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }
}
