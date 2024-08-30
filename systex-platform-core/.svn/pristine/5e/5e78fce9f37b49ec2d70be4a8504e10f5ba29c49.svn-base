package com.systex.jbranch.platform.common.security.privilege;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.helper.DataAccessHelper;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.platformdao.table.*;
import com.systex.jbranch.platform.common.security.privilege.vo.RoleAttrVO;
import com.systex.jbranch.platform.common.security.privilege.vo.RoleVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 角色管理類別，主要功能有：
 * 建立、修改、刪除、查詢角色，並提供角色權限的賦予與權限的取回功能
 *
 * @author Hong-jie
 * @version 1.0 2008/4/14
 */
@Transactional
@SuppressWarnings("unchecked")
public class RoleManagement {
// ------------------------------ FIELDS ------------------------------

    private DataAccessManager dataAccessManager;

// -------------------------- OTHER METHODS --------------------------

    /**
     * 新增角色屬性
     *
     * @param roleId 角色代號
     * @param attrs  角色屬性
     * @throws JBranchException jbranchexception。當傳入的roleId查無單位時，擲出NotFoundException
     */
    public void addAttrs(String roleId, RoleAttrVO... attrs) throws JBranchException {
        if (!ArrayUtils.isEmpty(attrs)) {
            TBSYSSECUROLEVO vo = (TBSYSSECUROLEVO) dataAccessManager.getHibernateCriteria(TBSYSSECUROLEVO.TABLE_UID)
                    .setFetchMode("attributes", FetchMode.JOIN)
                    .add(Restrictions.eq("ROLEID", roleId)).uniqueResult();

            checkNotNullVO(vo);

            for (RoleAttrVO attrVO : attrs) {
                TbsyssecuroleattrVO tbsyssecuroleattrVO = new TbsyssecuroleattrVO(new TbsyssecuroleattrPK(vo.getROLEID(), attrVO.getAttrId()), attrVO.getValue(), attrVO.getDescription());
                vo.getAttributes().add(tbsyssecuroleattrVO);
            }
            //dataAccessManager.update(vo);
        }
    }

    public void addUsers(String roleId, String... userIds) throws JBranchException {
        if (!ArrayUtils.isEmpty(userIds)) {
            List<TbsysuserVO> users = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID).add(Restrictions.in("tellerid", userIds)).list();
            TBSYSSECUROLEVO tbsyssecurolevo = (TBSYSSECUROLEVO) dataAccessManager.findByPKey(TBSYSSECUROLEVO.TABLE_UID, roleId);
            tbsyssecurolevo.getUsers().addAll(users);
        }
    }

    /**
     * 建立角色
     *
     * @param vo    角色VO
     * @param attrs 角色屬性
     * @throws JBranchException jbrnach exception
     */
    public void createRole(RoleVO vo, RoleAttrVO... attrs) throws JBranchException {
        TBSYSSECUROLEVO roleVO = new TBSYSSECUROLEVO();
        roleVO.setROLEID(vo.getRoleid());
        roleVO.setDESCRIPTION(vo.getDescription());
        roleVO.setNAME(vo.getName());
        Set<TbsyssecuroleattrVO> attributes = new HashSet<TbsyssecuroleattrVO>();
        for (RoleAttrVO attrVO : attrs) {
            TbsyssecuroleattrVO attribute = new TbsyssecuroleattrVO(new TbsyssecuroleattrPK(vo.getRoleid(), attrVO.getAttrId()), attrVO.getValue(), attrVO.getDescription());
            attributes.add(attribute);
        }
        roleVO.setAttributes(attributes);
        DataAccessManager dataAccessManager = new DataAccessManager();
        dataAccessManager.create(roleVO);
    }

    /**
     * 刪除角色
     *
     * @param roleid 欲刪除的角色ID
     * @throws JBranchException jbrnach exception
     */
    public void deleteRole(String roleid) throws JBranchException {
        TBSYSSECUROLEVO vo = (TBSYSSECUROLEVO) dataAccessManager.findByPKey(TBSYSSECUROLEVO.TABLE_UID, roleid);
        dataAccessManager.delete(vo);
    }

    /**
     * 刪除角色屬性
     *
     * @param roleId 角色ID
     * @param attrId 屬性ID
     * @throws JBranchException jbranch exception
     */
    public void deleteRoleAttribute(String roleId, String attrId) throws JBranchException {
        TBSYSSECUROLEVO vo = (TBSYSSECUROLEVO) dataAccessManager.getHibernateCriteria(TBSYSSECUROLEVO.TABLE_UID)
                .add(Restrictions.eq("ROLEID", roleId))
                .setFetchMode("attributes", FetchMode.JOIN)
                .uniqueResult();
        checkNotNullVO(vo);
        TbsyssecuroleattrVO attrVo = (TbsyssecuroleattrVO) dataAccessManager.getHibernateCriteria(TbsyssecuroleattrVO.TABLE_UID)
                .add(Restrictions.eq("compId.roleId", roleId))
                .add(Restrictions.eq("compId.attrId", attrId))
                .uniqueResult();
        vo.getAttributes().remove(attrVo);
    }

    /**
     * 取得所有賦予roleid權限的角色
     * 例：A將權限給B，C將權限給B，則getAllGivenRole(B)將回傳A與C
     *
     * @param roleid 欲查詢的角色ID
     * @return 所有賦予此角色權限的角色ID Collection
     */
    public java.util.Collection getAllGiveRole(String roleid) throws JBranchException {
        Set hashSet = new HashSet();
        hashSet.add(roleid);
        tmp(roleid, hashSet, dataAccessManager);
        return hashSet;
    }

    public Set tmp(String role, Set hashSet, DataAccessManager dataAccessManager) throws DAOException, JBranchException {
        QueryConditionIF queryCondition = dataAccessManager.getQueryCondition(DataAccessHelper.QUERY_LANGUAGE_TYPE_HQL);
        queryCondition.setQueryString("from TbsyssecurolpriassVO c where c.comp_id.roleid=" + role);
        List list = dataAccessManager.executeQuery(queryCondition);
        for (Object vo : list) {
            TbsyssecurolegivVO securolegivVO = (TbsyssecurolegivVO) vo;
            String giverID = securolegivVO.getComp_id().getGiver();
            if (!hashSet.contains(giverID)) {
                hashSet.add(giverID);
                tmp(giverID, hashSet, dataAccessManager);
            }
        }
        return hashSet;
    }

    /**
     * 取得角色屬性
     *
     * @param roleId 角色代號
     * @return 角色
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<RoleAttrVO> getAttrs(String roleId) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsyssecuroleattrVO.TABLE_UID);
        List<TbsyssecuroleattrVO> vos = criteria.add(Restrictions.eq("compId.roleId", roleId)).list();
        List<RoleAttrVO> attrVOs = new ArrayList<RoleAttrVO>(vos.size());
        for (TbsyssecuroleattrVO vo : vos) {
            attrVOs.add(new RoleAttrVO(vo));
        }
        return attrVOs;
    }

    /**
     * 取得角色VO
     * Getter of the property <tt>roleid</tt>
     *
     * @return 角色VO.
     */
    @Transactional(readOnly = true)
    public com.systex.jbranch.platform.common.security.privilege.vo.RoleVO getRole(String roleid) throws JBranchException {
        RoleVO roleVO = new RoleVO();
        TBSYSSECUROLEVO securoleVO;
        QueryConditionIF queryCondition = dataAccessManager.getQueryCondition(DataAccessHelper.QUERY_LANGUAGE_TYPE_HQL);
        queryCondition.setQueryString("from TbsyssecuroleVO vo where vo.comp_id.roleid='" + roleid + "'");
        List list = dataAccessManager.executeQuery(queryCondition);
        for (Object aList : list) {
            securoleVO = (TBSYSSECUROLEVO) aList;
            roleVO.setDescription(securoleVO.getDESCRIPTION());
            roleVO.setName(securoleVO.getNAME());
            roleVO.setRoleid(securoleVO.getROLEID());
        }

        return roleVO;
    }

    /**
     * 取得角色屬性值
     *
     * @param roleId 角色ID
     * @param attrId 屬性ID
     * @return 屬性值
     * @throws JBranchException jbranch exception
     */
    public RoleAttrVO getRoleAttribute(String roleId, String attrId) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsyssecuroleattrVO.TABLE_UID);
        TbsyssecuroleattrVO vo = (TbsyssecuroleattrVO) criteria
                .add(Restrictions.eq("compId.roleId", roleId))
                .add(Restrictions.eq("compId.attrId", attrId))
                .uniqueResult();
        return vo == null ? null : new RoleAttrVO(vo);
    }

    /**
     * 取得角色且包含角色屬性
     *
     * @param roleId 角色代號
     * @return 角色
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public RoleVO getRoleWithAttrs(String roleId) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TBSYSSECUROLEVO.TABLE_UID);
        criteria.add(Restrictions.eq("ROLEID", roleId));
        criteria.setFetchMode("attributes", FetchMode.JOIN);
        TBSYSSECUROLEVO vo = (TBSYSSECUROLEVO) criteria.uniqueResult();
        if (vo != null) {
            RoleVO roleVO = new RoleVO(vo);
            Set<RoleAttrVO> attributes = new HashSet<RoleAttrVO>();
            for (TbsyssecuroleattrVO attr : vo.getAttributes()) {
                RoleAttrVO attrVO = new RoleAttrVO(attr);
                attributes.add(attrVO);
            }
            roleVO.setAttributes(attributes);
            return roleVO;
        }
        else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<RoleVO> getRoles() throws JBranchException {
        List<TBSYSSECUROLEVO> securoleVOs = (List<TBSYSSECUROLEVO>) dataAccessManager.findAll(TBSYSSECUROLEVO.TABLE_UID);
        return transform(securoleVOs);
    }

    private List<RoleVO> transform(List<TBSYSSECUROLEVO> vos) {
        List<RoleVO> roles = new ArrayList<RoleVO>(vos.size());
        for (TBSYSSECUROLEVO vo : vos) {
            roles.add(new RoleVO(vo));
        }
        return roles;
    }

    @Transactional(readOnly = true)
    public Set<RoleVO> getRolesByUserId(String userId) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID);
        TbsysuserVO user = (TbsysuserVO) criteria.add(Restrictions.eq("tellerid", userId)).setFetchMode("roles", FetchMode.JOIN).uniqueResult();
        return transform(user.getRoles());
    }

    private Set<RoleVO> transform(Set<TBSYSSECUROLEVO> vos) {
        Set<RoleVO> roles = new HashSet<RoleVO>(vos.size());
        for (TBSYSSECUROLEVO vo : vos) {
            roles.add(new RoleVO(vo));
        }
        return roles;
    }

    /**
     * 賦予權限
     *
     * @param fromRole 權限來源角色ID
     * @param toRole   權限接收角色ID
     */
    public void givePrivilege(String fromRole, String toRole) throws JBranchException {
        TbsyssecurolegivVO vo = new TbsyssecurolegivVO();
        TbsyssecurolegivPK pk = new TbsyssecurolegivPK();
        pk.setGiver(fromRole);
        pk.setReceiver(toRole);
        vo.setComp_id(pk);
        /*
        TbsyssecurolegivVO.setCreatetime(new Timestamp(System.currentTimeMillis()));
        TbsyssecurolegivVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
        TbsyssecurolegivVO.setCreator("SYSTEM");
        TbsyssecurolegivVO.setModifier("SYSTEM");
        */
        dataAccessManager.create(vo);
    }

    /**
     * 設定角色屬性，原屬性將被刪除
     *
     * @param roleId 角色代號
     * @param attrs  角色屬性
     * @throws JBranchException jbranchexception。當傳入的roleId查無單位時，擲出NotFoundException
     */
    public void setAttrs(String roleId, RoleAttrVO... attrs) throws JBranchException {
        QueryConditionIF queryCondition = dataAccessManager.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_HQL);
        queryCondition.setQueryString("delete from com.systex.jbranch.platform.common.platformdao.table.TbsyssecuroleattrVO as a where a.compId.roleId = :roleId");
        queryCondition.setObject("roleId", roleId);
        dataAccessManager.executeUpdate(queryCondition);
        TBSYSSECUROLEVO vo = (TBSYSSECUROLEVO) dataAccessManager.findByPKey(TBSYSSECUROLEVO.TABLE_UID, roleId);
        checkNotNullVO(vo);
        vo.getAttributes().clear();
        for (RoleAttrVO attrVO : attrs) {
            TbsyssecuroleattrVO tbsyssecuroleattrVO = new TbsyssecuroleattrVO(new TbsyssecuroleattrPK(vo.getROLEID(), attrVO.getAttrId()), attrVO.getValue(), attrVO.getDescription());
            vo.getAttributes().add(tbsyssecuroleattrVO);
        }
    }

    private void checkNotNullVO(Object o) throws NotFoundException {
        if (o == null) {
            throw new NotFoundException("ehl_01_common_001");
        }
    }

    public void setUsers(String roleId, String... userIds) throws JBranchException {
        List<TbsysuserVO> users;
        if (!ArrayUtils.isEmpty(userIds)) {
            users = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID).add(Restrictions.in("tellerid", userIds)).list();
        }
        else {
            users = Collections.emptyList();
        }
        TBSYSSECUROLEVO tbsyssecurolevo = (TBSYSSECUROLEVO) dataAccessManager.findByPKey(TBSYSSECUROLEVO.TABLE_UID, roleId);
        tbsyssecurolevo.getUsers().clear();
        tbsyssecurolevo.getUsers().addAll(users);

        //dataAccessManager.update(tbsyssecurolevo);
    }

    /**
     * 取回被賦予的權限
     * 例：把A角色中，B角色的權限取回，則A不再具有B的權限takePrivilege(A,B)
     *
     * @param formRole    被取回權限的角色ID
     * @param subjectRole 取回的權限角色ID
     */
    public void takePrivilege(String fromRole, String subjectRole) throws DAOException {
        TbsyssecurolegivPK pk = new TbsyssecurolegivPK();
        pk.setGiver(fromRole);
        pk.setReceiver(subjectRole);
        TbsyssecurolegivVO vo = (TbsyssecurolegivVO) dataAccessManager.findByPKey(TbsyssecurolegivVO.TABLE_UID, pk);
        dataAccessManager.delete(vo);
    }

    /**
     * 更新角色
     *
     * @param roleVO role vo
     * @param attrs  角色屬性
     * @throws JBranchException jbrnach exception
     */
    public void updateRole(RoleVO roleVO, RoleAttrVO... attrs) throws JBranchException {
        TBSYSSECUROLEVO vo = (TBSYSSECUROLEVO) dataAccessManager.findByPKey(TBSYSSECUROLEVO.TABLE_UID, roleVO.getRoleid());
        vo.setNAME(roleVO.getName());
        vo.setDESCRIPTION(roleVO.getDescription());
        final Map<String, RoleAttrVO> attrMap = new HashMap<String, RoleAttrVO>(attrs.length);
        for (RoleAttrVO attr : attrs) {
            attrMap.put(attr.getAttrId(), attr);
        }

        CollectionUtils.transform(vo.getAttributes(), new Transformer() {
            public Object transform(Object o) {
                TbsyssecuroleattrVO attrVO = (TbsyssecuroleattrVO) o;
                RoleAttrVO vo;
                if ((vo = attrMap.get(attrVO.getCompId().getAttrId())) != null) {
                    attrVO.setValue(vo.getValue());
                    attrVO.setDescription(vo.getDescription());
                }
                return attrVO;
            }
        });
        dataAccessManager.update(vo);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }
}