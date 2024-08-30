package com.systex.jbranch.platform.common.security.privilege;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysorgVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysorgattrPK;
import com.systex.jbranch.platform.common.platformdao.table.TbsysorgattrVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysuserVO;
import com.systex.jbranch.platform.common.security.privilege.vo.OrgAttrVO;
import com.systex.jbranch.platform.common.security.privilege.vo.OrgVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author Alex Lin
 * @version 2010/01/20 4:39:33 PM
 */
@SuppressWarnings("unchecked")
@Transactional
public class OrganizationManagement {
// ------------------------------ FIELDS ------------------------------

    private DataAccessManager dataAccessManager;

// -------------------------- OTHER METHODS --------------------------

    /**
     * 新增組織屬性
     *
     * @param divNo 組織代號
     * @param attrs 組織屬性
     * @throws JBranchException jbranchexception。當傳入的divNo查無單位時，擲出NotFoundException
     */
    public void addAttrs(String divNo, OrgAttrVO... attrs) throws JBranchException {
        if (!ArrayUtils.isEmpty(attrs)) {
            TbsysorgVO tbsysorgVO = (TbsysorgVO) dataAccessManager.getHibernateCriteria(TbsysorgVO.TABLE_UID)
                    .setFetchMode("attributes", FetchMode.JOIN)
                    .add(Restrictions.eq("divNo", divNo)).uniqueResult();

            checkNotNullVO(tbsysorgVO);

            for (OrgAttrVO attrVO : attrs) {
                TbsysorgattrVO tbsysorgattrVO = new TbsysorgattrVO(new TbsysorgattrPK(tbsysorgVO.getDivNo(), attrVO.getAttrId()), attrVO.getValue(), attrVO.getDescription());
                tbsysorgVO.getAttributes().add(tbsysorgattrVO);
            }
        }
    }

    /**
     * 新增組織
     *
     * @param divNo   代號
     * @param divName 名稱
     * @param desc    描述
     * @throws JBranchException jbranchexception
     */
    public void addOrg(String divNo, String divName, String desc) throws JBranchException {
        this.addOrg(divNo, divName, desc, null);
    }

    /**
     * 新增組織
     *
     * @param divNo       代號
     * @param divName     名稱
     * @param desc        描述
     * @param parentDivNo 父組織代號
     * @throws JBranchException jbranchexception。當傳入的parentDivNo查無單位時，擲出NotFoundException
     */
    public void addOrg(String divNo, String divName, String desc, String parentDivNo) throws JBranchException {
        this.addOrgAndAttrs(divNo, divName, desc, parentDivNo);
    }

    /**
     * 新增組織
     *
     * @param divNo       代號
     * @param divName     名稱
     * @param desc        描述
     * @param parentDivNo 父組織代號
     * @param attrs       組織屬性
     * @throws JBranchException jbranchexception。當傳入的parentDivNo查無單位時，擲出NotFoundException
     */
    public void addOrgAndAttrs(String divNo, String divName, String desc, String parentDivNo, OrgAttrVO... attrs) throws JBranchException {
        TbsysorgVO org = new TbsysorgVO();
        org.setDivNo(divNo);
        org.setDivName(divName);
        org.setDescription(desc);
        if (parentDivNo != null) {
            TbsysorgVO parent = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, parentDivNo);
            checkNotNullVO(parent);
            org.setParent(parent);
        }

        Set<TbsysorgattrVO> attributes = new HashSet<TbsysorgattrVO>();
        for (OrgAttrVO attrVO : attrs) {
            TbsysorgattrVO attribute = new TbsysorgattrVO(new TbsysorgattrPK(divNo, attrVO.getAttrId()), attrVO.getValue(), attrVO.getDescription());
            attributes.add(attribute);
        }
        org.setAttributes(attributes);

        dataAccessManager.create(org);
    }

    /**
     * 新增組織
     *
     * @param divNo   代號
     * @param divName 名稱
     * @param desc    描述
     * @param attrs   組織屬性
     * @throws JBranchException jbranchexception
     */
    public void addOrgAndAttrs(String divNo, String divName, String desc, OrgAttrVO... attrs) throws JBranchException {
        this.addOrgAndAttrs(divNo, divName, desc, null, attrs);
    }

    /**
     * 新增子組織
     *
     * @param parentDivNo 父組織代號
     * @param childDivNo  子組織代號
     * @throws JBranchException jbranchexception。當傳入的parentDivNo或childDivNo查無單位時，擲出NotFoundException
     */
    public void addSubOrg(String parentDivNo, String childDivNo) throws JBranchException {
        TbsysorgVO parent = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, parentDivNo);
        checkNotNullVO(parent);
        TbsysorgVO child = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, childDivNo);
        checkNotNullVO(child);
        child.setParent(parent);
    }

    /**
     * 新增組織內使用者
     *
     * @param divNo   組織代號
     * @param userIds 使用者代號
     * @throws JBranchException jbranchexception。當傳入的divNo查無單位時，擲出NotFoundException
     */
    public void addUsers(String divNo, String... userIds) throws JBranchException {
        if (!ArrayUtils.isEmpty(userIds)) {
            List<TbsysuserVO> users = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID).add(Restrictions.in("tellerid", userIds)).list();
            TbsysorgVO tbsysorgVO = (TbsysorgVO) dataAccessManager.getHibernateCriteria(TbsysorgVO.TABLE_UID).add(Restrictions.eq("divNo", divNo)).uniqueResult();
            checkNotNullVO(tbsysorgVO);
            tbsysorgVO.getUsers().addAll(users);
        }
    }

    /**
     * 設定父組織
     *
     * @param childDivNo  子組織代號
     * @param parentDivno 父組織代號
     * @throws JBranchException jbranchexception。當傳入的childDivNo或parentDivNo查無單位時，擲出NotFoundException
     */
    public void attachParentOrg(String childDivNo, String parentDivno) throws JBranchException {
        TbsysorgVO child = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, childDivNo);
        TbsysorgVO parent = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, parentDivno);
        checkNotNullVO(child);
        checkNotNullVO(parent);
        child.setParent(parent);
    }

    /**
     * 刪除組織
     *
     * @param divNo 組織代號
     * @throws JBranchException jbranchexception
     */
    public void deleteOrg(String divNo) throws JBranchException {
        TbsysorgVO tbsysorgVO = (TbsysorgVO) dataAccessManager.getHibernateCriteria(TbsysorgVO.TABLE_UID).add(Restrictions.eq("divNo", divNo)).uniqueResult();
        if (tbsysorgVO != null) {
            dataAccessManager.delete(tbsysorgVO);
        }
    }

    /**
     * 離開父組織
     *
     * @param childDivNo 組織代號
     * @throws JBranchException jbranchexception。當傳入的childDivNo查無單位時，擲出NotFoundException
     */
    public void detachParentOrg(String childDivNo) throws JBranchException {
        TbsysorgVO child = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, childDivNo);
        checkNotNullVO(child);
        child.setParent(null);
    }

    /**
     * 取得組織下所有子組織
     *
     * @param divNo 組織代號
     * @return 所有子組織
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<OrgVO> getAllSubOrgByDivNo(String divNo) throws JBranchException {
        return getSubOrgByDivNo(divNo, new ArrayList<OrgVO>());
    }

    private List<OrgVO> getSubOrgByDivNo(String divNo, List<OrgVO> result) throws JBranchException {
        List<OrgVO> orgVOs = getSubOrgByDivNo(divNo);
        for (OrgVO orgVO : orgVOs) {
            result.add(orgVO);
            getSubOrgByDivNo(orgVO.getDivNo(), result);
        }
        return result;
    }

    /**
     * 取得組織屬性
     *
     * @param divNo  單位代號
     * @param attrId 組織屬性代號
     * @return 組織屬性
     * @throws JBranchException jbranch exception
     */
    @Transactional(readOnly = true)
    public OrgAttrVO getOrgAttribute(String divNo, String attrId) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsysorgattrVO.TABLE_UID);
        TbsysorgattrVO vo = (TbsysorgattrVO) criteria
                .add(Restrictions.eq("compId.divNo", divNo))
                .add(Restrictions.eq("compId.attrId", attrId))
                .uniqueResult();
        return vo == null ? null : new OrgAttrVO(vo);
    }

    /**
     * 以組織屬性取得組織
     *
     * @param attrs 組織屬性
     * @return 擁有該組織屬性的組織
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<OrgVO> getOrgByAttrs(OrgAttrVO... attrs) throws JBranchException {
        QueryConditionIF queryCondition = dataAccessManager.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_HQL);
        StringBuilder hql = new StringBuilder("select distinct a from com.systex.jbranch.platform.common.platformdao.table.TbsysorgVO as a ");
        //alias starting from b
        char alias = 'b';
        String joinTemplate = " inner join a.attributes as $alias$ with $alias$.compId.attrId = :attrId$alias$ and $alias$.value = :value$alias$ ";
        for (OrgAttrVO attr : attrs) {
            hql.append(joinTemplate.replaceAll("\\$alias\\$", String.valueOf(alias)));
            queryCondition.setObject("attrId" + alias, attr.getAttrId());
            queryCondition.setObject("value" + alias, attr.getValue());
            //change alias to next ascii code
            alias++;
        }
        hql.append(" order by a.divNo");
        queryCondition.setQueryString(hql.toString());
        List<TbsysorgVO> vos = (List<TbsysorgVO>) dataAccessManager.executeQuery(queryCondition);
        List<OrgVO> orgVOs = new ArrayList<OrgVO>(vos.size());
        for (TbsysorgVO vo : vos) {
            OrgVO orgVO = new OrgVO(vo);
            orgVOs.add(orgVO);
        }
        return orgVOs;
    }

    /**
     * 以組織代號取得組織
     *
     * @param divNo 組織代號
     * @return 組織
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public OrgVO getOrgByDivNo(String divNo) throws JBranchException {
        TbsysorgVO vo = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, divNo);
        return vo == null ? null : new OrgVO(vo);
    }

    /**
     * 取得該單位下所有組織樹
     *
     * @param divNo 單位代號
     * @return 單位包含子組織
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public OrgVO getOrgTree(String divNo) throws JBranchException {
        TbsysorgVO vo = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, divNo);
        return retrieveChildren(vo);
    }

    private OrgVO retrieveChildren(TbsysorgVO vo) {
        OrgVO orgVO = new OrgVO(vo);
        Set<TbsysorgVO> children = vo.getChildren();
        List<OrgVO> childrenVOs = new ArrayList<OrgVO>(children.size());
        for (TbsysorgVO child : children) {
            OrgVO childVO = retrieveChildren(child);
            childrenVOs.add(childVO);
        }
        orgVO.setChildren(childrenVOs);
        return orgVO;
    }

    /**
     * 取得組織且包含組織屬性
     *
     * @param divNo 組織代號
     * @return 組織
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public OrgVO getOrgWithAttrs(String divNo) throws JBranchException {
        List<OrgVO> orgVOs = getOrgsWithAttrs(divNo);
        return orgVOs.isEmpty() ? null : orgVOs.get(0);
    }

    /**
     * 取得組織且包含組織屬性
     *
     * @param divNos 組織代號
     * @return 組織
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<OrgVO> getOrgsWithAttrs(String... divNos) throws JBranchException {
        Criteria criteria = dataAccessManager.getHibernateCriteria(TbsysorgVO.TABLE_UID);
        criteria.add(Restrictions.in("divNo", divNos));
        criteria.setFetchMode("attributes", FetchMode.JOIN);
        List<TbsysorgVO> vos = (List<TbsysorgVO>) criteria.list();
        List<OrgVO> orgVOs = new ArrayList<OrgVO>(vos.size());
        for (TbsysorgVO vo : vos) {
            OrgVO orgVO = new OrgVO(vo);
            Set<OrgAttrVO> attributes = new HashSet<OrgAttrVO>();
            for (TbsysorgattrVO attr : vo.getAttributes()) {
                OrgAttrVO attrVO = new OrgAttrVO(attr);
                attributes.add(attrVO);
            }
            orgVO.setAttributes(attributes);
            orgVOs.add(orgVO);
        }
        return orgVOs;
    }

    /**
     * 取得父組織
     *
     * @param divNo 組織代號
     * @return 父組織
     * @throws JBranchException jbranchexception。當傳入的divNo查無單位時，擲出NotFoundException
     */
    @Transactional(readOnly = true)
    public OrgVO getParent(String divNo) throws JBranchException {
        TbsysorgVO tbsysorgVO = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, divNo);
        checkNotNullVO(tbsysorgVO);
        TbsysorgVO parent = tbsysorgVO.getParent();
        return parent == null ? null : new OrgVO(parent);
    }

    /**
     * 取得一層的子組織
     *
     * @param divNo 組織代號
     * @return 組織
     * @throws JBranchException jbranchexception
     */
    @Transactional(readOnly = true)
    public List<OrgVO> getSubOrgByDivNo(String divNo) throws JBranchException {
        List<TbsysorgVO> orgVOs = dataAccessManager.getHibernateCriteria(TbsysorgVO.TABLE_UID)
                .add(Restrictions.eq("parent.divNo", divNo))
                .addOrder(Order.asc("divNo")).list();
        List<OrgVO> vos = new ArrayList<OrgVO>(orgVOs.size());
        for (TbsysorgVO orgVO : orgVOs) {
            vos.add(new OrgVO(orgVO));
        }
        return vos;
    }

    /**
     * 設定組織屬性，原屬性將被刪除
     *
     * @param divNo 組織代號
     * @param attrs 組織屬性
     * @throws JBranchException jbranchexception。當傳入的divNo查無單位時，擲出NotFoundException
     */
    public void setAttrs(String divNo, OrgAttrVO... attrs) throws JBranchException {
        TbsysorgVO tbsysorgVO = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, divNo);
        checkNotNullVO(tbsysorgVO);
        tbsysorgVO.getAttributes().clear();
        for (OrgAttrVO attrVO : attrs) {
            TbsysorgattrVO tbsysorgattrVO = new TbsysorgattrVO(new TbsysorgattrPK(tbsysorgVO.getDivNo(), attrVO.getAttrId()), attrVO.getValue(), attrVO.getDescription());
            tbsysorgVO.getAttributes().add(tbsysorgattrVO);
        }
    }

    /**
     * 設定組織使用者，原設定將被刪除
     *
     * @param divNo   組織代號
     * @param userIds 使用者代號
     * @throws JBranchException jbranchexception。當傳入的divNo查無單位時，擲出NotFoundException
     */
    public void setUsers(String divNo, String... userIds) throws JBranchException {
        TbsysorgVO tbsysorgVO = (TbsysorgVO) dataAccessManager.getHibernateCriteria(TbsysorgVO.TABLE_UID)
                .add(Restrictions.eq("divNo", divNo)).uniqueResult();
        checkNotNullVO(tbsysorgVO);
        List<TbsysuserVO> users;
        if (!ArrayUtils.isEmpty(userIds)) {
            users = dataAccessManager.getHibernateCriteria(TbsysuserVO.TABLE_UID).add(Restrictions.in("tellerid", userIds)).list();
        }
        else {
            users = Collections.emptyList();
        }
        tbsysorgVO.getUsers().clear();
        tbsysorgVO.setUsers(new HashSet(users));
    }

    /**
     * 更新組織
     *
     * @param divNo   代號
     * @param divName 名稱
     * @param desc    描述
     * @throws JBranchException jbranchexception
     */
    public void updateOrg(String divNo, String divName, String desc) throws JBranchException {
        this.updateOrg(divNo, divName, desc, null);
    }

    /**
     * 更新組織
     *
     * @param divNo       代號
     * @param divName     名稱
     * @param desc        描述
     * @param parentDivNo 父組織代號
     * @throws JBranchException jbranchexception。當傳入的parentDivNo查無單位時，擲出NotFoundException
     */
    public void updateOrg(String divNo, String divName, String desc, String parentDivNo) throws JBranchException {
        this.updateOrgAndAttrs(divNo, divName, desc, parentDivNo);
    }

    /**
     * 更新組織
     *
     * @param divNo       代號
     * @param divName     名稱
     * @param desc        描述
     * @param parentDivNo 父組織代號
     * @param attrs       組織屬性
     * @throws JBranchException jbranchexception。當傳入的parentDivNo查無單位時，擲出NotFoundException
     */
    public void updateOrgAndAttrs(String divNo, String divName, String desc, String parentDivNo, OrgAttrVO... attrs) throws JBranchException {
        TbsysorgVO vo = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, divNo, true);
        vo.setDivName(divName);
        vo.setDescription(desc);
        if (parentDivNo != null) {
            TbsysorgVO parent = (TbsysorgVO) dataAccessManager.findByPKey(TbsysorgVO.TABLE_UID, parentDivNo);
            checkNotNullVO(parent);
            vo.setParent(parent);
        }

        final Map<String, OrgAttrVO> attrMap = new HashMap<String, OrgAttrVO>(attrs.length);
        for (OrgAttrVO attr : attrs) {
            attrMap.put(attr.getAttrId(), attr);
        }

        CollectionUtils.transform(vo.getAttributes(), new Transformer() {
            public Object transform(Object o) {
                TbsysorgattrVO attrVO = (TbsysorgattrVO) o;
                OrgAttrVO vo;
                if ((vo = attrMap.get(attrVO.getCompId().getAttrId())) != null) {
                    attrVO.setValue(vo.getValue());
                    attrVO.setDescription(vo.getDescription());
                }
                return attrVO;
            }
        });

        dataAccessManager.update(vo);
    }

    private void checkNotNullVO(Object o) throws NotFoundException {
        if (o == null) {
            throw new NotFoundException("ehl_01_common_001");
        }
    }

    /**
     * 更新組織
     *
     * @param divNo   代號
     * @param divName 名稱
     * @param desc    描述
     * @param attrs   組織屬性
     * @throws JBranchException jbranchexception
     */
    public void updateOrgAndAttrs(String divNo, String divName, String desc, OrgAttrVO... attrs) throws JBranchException {
        this.updateOrgAndAttrs(divNo, divName, desc, null, attrs);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setDataAccessManager(DataAccessManager dataAccessManager) {
        this.dataAccessManager = dataAccessManager;
    }
}
