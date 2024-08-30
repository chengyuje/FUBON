package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.dataManager.BizlogicVO;
import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.ClientTransaction;
import com.systex.jbranch.platform.common.dataManager.ConversationVO;
import com.systex.jbranch.platform.common.dataManager.DataManagerIF;
import com.systex.jbranch.platform.common.dataManager.PlatFormVO;
import com.systex.jbranch.platform.common.dataManager.Section;
import com.systex.jbranch.platform.common.dataManager.ServerTransaction;
import com.systex.jbranch.platform.common.dataManager.TransactionDefinition;
import com.systex.jbranch.platform.common.dataManager.TransactionDefinitionHashTable;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TbsysdmbranchVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysdmclienttransactionVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysdmservertransactionVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysdmuserVO;
import com.systex.jbranch.platform.common.platformdao.table.TbsysdmworkstationVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Alex Lin
 * @version 2011/03/03 3:31 PM
 */
@SuppressWarnings("unchecked")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class DataManagerDB implements DataManagerIF {
// ------------------------------ FIELDS ------------------------------

    private static final String FILE_SEPARATOR = java.lang.System.getProperties().getProperty("file.separator");
    private static char[] charArray = FILE_SEPARATOR.toCharArray();
    protected com.systex.jbranch.platform.common.dataManager.System system;
    private HibernateTemplate template;
    private String realPath;
    private String root;
	private Logger logger = LoggerFactory.getLogger(DataManagerDB.class);
    private Map<String, Map<String, Section>> workstations = new ConcurrentHashMap<String, Map<String, Section>>();

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface DataManagerIF ---------------------

    public String getRoot() {
        return root;
    }

    public void setRoot(String r) {
        root = r;
    }

    public void setRealPath(String value) {
        realPath = value;
    }

    public String getRealPath() {
        realPath = realPath.replace('\\', charArray[0]);
        realPath = realPath.replace('/', charArray[0]);

        return realPath;
    }

    //@Transactional(readOnly = true)
    public Map<String, Branch> getBranch() {
        List<TbsysdmbranchVO> vos = (List<TbsysdmbranchVO>) template.find("from TbsysdmbranchVO");
        Map<String, Branch> branches = new HashMap<String, Branch>(vos.size());
        for (TbsysdmbranchVO vo : vos) {
            Branch branch = convert(vo);
            branches.put(branch.getBrchID(), branch);
        }

        return branches;
    }

    /**
     * 取得系統組態物件<br>
     *
     * @return system
     */
    public com.systex.jbranch.platform.common.dataManager.System getSystem() {
        try {
            return new SystemDB(system);
        }
        catch (JBranchException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 設定系統組態<br>
     *
     * @param system system
     */
    public void setSystem(com.systex.jbranch.platform.common.dataManager.System system) {
        this.system = system;
    }

    //@Transactional(readOnly = true)
    public Branch getBranch(String branchID) {
        if (logger.isDebugEnabled()) {
            logger.debug("branchID = " + branchID);
        }
        TbsysdmbranchVO vo = template.get(TbsysdmbranchVO.class, branchID);
        if (vo != null) {
            return convert(vo);
        }
        else {
            return null;
        }
    }

    //@Transactional(readOnly = true)
    public Branch getBranch(UUID uuid) {
        return getBranch(uuid.getBranchID());
    }

    public void setBranch(String brID, Branch brch) {
        if (logger.isDebugEnabled()) {
            logger.debug("brID = " + brID);
            logger.debug("brch = " + brch);
        }
        TbsysdmbranchVO vo = template.get(TbsysdmbranchVO.class, brID);
        boolean exist = vo != null;
        vo = exist ? vo : new TbsysdmbranchVO();
        vo.setName(brch.getName());
        vo.setOpDate(brch.getOpDate());
        vo.setTxnFlag(brch.getTxnFlag());
        vo.setBrchID(brch.getBrchID());
        if (exist) {
            template.update(vo);
        }
        else {
            template.save(vo);
        }
    }

    public void setBranch(UUID uuid, Branch brch) {
        setBranch(uuid.getBranchID(), brch);
    }

    public void setWorkStation(UUID uuid, WorkStation ws) {
        setWorkStation(uuid.getBranchID(), uuid.getWsId(), ws);
    }

    public void setWorkStation(String brchID, String wsID, WorkStation ws) {
        if (logger.isDebugEnabled()) {
            logger.debug("brchID = " + brchID);
            logger.debug("wsID = " + wsID);
            logger.debug("ws = " + ws);
        }
        TbsysdmworkstationVO vo = template.get(TbsysdmworkstationVO.class, wsID);
        TbsysdmbranchVO branchVO = template.load(TbsysdmbranchVO.class, brchID);
        //String userID = ws.getUser().getUserID();
        //TbsysdmuserVO userVO = null;
        //if (userID != null) {
        //    userVO = template.get(TbsysdmuserVO.class, userID);
        //}
        boolean exist = vo != null;
        vo = exist ? vo : new TbsysdmworkstationVO();
        vo.setBranch(branchVO);
        vo.setApplicationID(ws.getApplicationID());
        //vo.getUser().add(userVO);
        vo.setDevelopMode(ws.getDevelopMode());
        vo.setLocalMode(ws.getLocalMode());
        vo.setSignOnBranchID(ws.getSignOnBranchID());
        vo.setSignOnWsID(ws.getSignOnWsID());
        vo.setTouchedTime(ws.getTouchedTime());
        vo.setWsID(wsID);
        vo.setWsIP(ws.getWsIP());
        //if (exist) {
        template.saveOrUpdate(vo);
        //}
        //else {
        //    template.save(vo);
        //}
        //template.flush();
    }

    //@Transactional(readOnly = true)
    public WorkStation getWorkStation(String brchID, String wsID) {
        if (logger.isDebugEnabled()) {
            logger.debug("brchID = " + brchID);
            logger.debug("wsID = " + wsID);
        }
        TbsysdmworkstationVO vo = template.get(TbsysdmworkstationVO.class, wsID);
        if (vo != null) {
            return convert(vo);
        }
        else {
            return null;
        }
    }

    //@Transactional(readOnly = true)
    public WorkStation getWorkStation(UUID uuid) {
        return getWorkStation(uuid.getBranchID(), uuid.getWsId());
    }

    public boolean deleteWorkStation(UUID uuid) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
        }
        TbsysdmworkstationVO vo = template.load(TbsysdmworkstationVO.class, uuid.getWsId());
        template.delete(vo);
        return true;
    }

    //@Transactional(readOnly = true)
    public boolean existWorkStation(UUID uuid) {
        return existWorkStation(uuid.getBranchID(), uuid.getWsId());
    }

    //@Transactional(readOnly = true)
    public boolean existWorkStation(String branchID, String wsID) {
        if (logger.isDebugEnabled()) {
            logger.debug("branchID = " + branchID);
            logger.debug("wsID = " + wsID);
        }
        TbsysdmworkstationVO vo = template.get(TbsysdmworkstationVO.class, wsID);
        return vo != null;
    }

    public void setSection(UUID uuid, Section section) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
            logger.debug("section = " + section);
        }
        Map<String, Section> sectionMap = workstations.get(uuid.getWsId());
        if (sectionMap == null) {
            sectionMap = new HashMap<String, Section>();
            workstations.put(uuid.getWsId(), sectionMap);
        }
        sectionMap.put(uuid.getSectionID(), section);
        /*TbsysdmsectionVO vo = template.get(TbsysdmsectionVO.class, uuid.getSectionID());
        boolean exist = vo != null;
        if (!exist) {
            vo = new TbsysdmsectionVO();
            TbsysdmworkstationVO wsVO = template.load(TbsysdmworkstationVO.class, uuid.getWsId());
            vo.setWorkstation(wsVO);
            TbsysdmclienttransactionVO clientTransaction = new TbsysdmclienttransactionVO();
            clientTransaction.setSectionID(uuid.getSectionID());
            clientTransaction.setBizlogicVO(new BizlogicVO());
            clientTransaction.setPlatformVO(new PlatFormVO());
            template.save(clientTransaction);
            vo.setClientTransaction(clientTransaction);
            TbsysdmservertransactionVO serverTransaction = new TbsysdmservertransactionVO();
            serverTransaction.setHost(new HostVO());
            serverTransaction.setBizlogicVO(new BizlogicVO());
            serverTransaction.setPlatformVO(new PlatFormVO());
            serverTransaction.setConversationVO(new ConversationVO());
            serverTransaction.setSectionID(uuid.getSectionID());
            template.save(serverTransaction);
            vo.setServerTransaction(serverTransaction);
        }
        vo.setLuNo(section.getLuNo());
        vo.setSectionID(section.getSectionID());
        vo.setTxnCode(section.getTxnCode());
        vo.setTxnName(section.getTxnName());

        if (exist) {
            template.update(vo);
        }
        else {
            template.save(vo);
        }*/
    }

    //@Transactional(readOnly = true)
    public Section getSection(UUID uuid) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
        }
        Map<String, Section> sectionMap = workstations.get(uuid.getWsId());
        return sectionMap == null ? null : sectionMap.get(uuid.getSectionID());
        /*TbsysdmsectionVO vo = template.get(TbsysdmsectionVO.class, uuid.getSectionID());
        if (vo != null) {
            Section section = new Section();
            section.setLuNo(vo.getLuNo());
            section.setSectionID(vo.getSectionID());
            section.setTxnCode(vo.getTxnCode());
            section.setTxnName(vo.getTxnName());

            TbsysdmservertransactionVO serverTxVO = vo.getServerTransaction();
            ServerTransaction serverTransaction = convert(serverTxVO);
            section.setServerTransaction(serverTransaction);

            TbsysdmclienttransactionVO clientTxVO = vo.getClientTransaction();
            ClientTransaction clientTransaction = convert(clientTxVO);
            section.setClientTransaction(clientTransaction);

            return section;
        }
        else {
            return null;
        }*/
    }

    public boolean deleteSection(UUID uuid) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
        }
        Map<String, Section> sectionMap = workstations.get(uuid.getWsId());
        if (sectionMap != null) {
            sectionMap.remove(uuid.getSectionID());
        }
        return true;
        /*TbsysdmsectionVO vo = template.load(TbsysdmsectionVO.class, uuid.getSectionID());
        template.delete(vo);
        return true;*/
    }

    public ServerTransaction getServerTransaction(UUID uuid) {
        return workstations.get(uuid.getWsId()).get(uuid.getSectionID()).getServerTransaction();
        //return convert(template.get(TbsysdmservertransactionVO.class, uuid.getSectionID()));
    }

    public void setServerTransaction(UUID uuid, ServerTransaction st) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
            logger.debug("st = " + st);
        }

        workstations.get(uuid.getWsId()).get(uuid.getSectionID()).setServerTransaction(st);

        /*TbsysdmservertransactionVO vo = template.load(TbsysdmservertransactionVO.class, uuid.getSectionID());
        vo.setHost(st.getHost());
        vo.setBizCode(st.getBizCode());
        vo.setBizlogicVO(st.getBizLogicVO());
        vo.setConversationVO(st.getConversationVO());
        vo.setPlatformVO(st.getPlatFormVO());
        vo.setTxnCode(st.getTxnCode());
        template.update(vo);*/
    }

    public boolean deleteServerTransaction(UUID uuid) {
        //TbsysdmservertransactionVO vo = template.load(TbsysdmservertransactionVO.class, uuid.getSectionID());
        //template.delete(vo);
        workstations.get(uuid.getWsId()).get(uuid.getSectionID()).setServerTransaction(null);
        return true;
    }

    public ConversationVO getConversationVO(UUID uuid) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
        }
        /*TbsysdmservertransactionVO vo = template.load(TbsysdmservertransactionVO.class, uuid.getSectionID());
        return vo.getConversationVO();*/
        return workstations.get(uuid.getWsId()).get(uuid.getSectionID()).getServerTransaction().getConversationVO();
    }

    public void setConversationVO(UUID uuid, ConversationVO conversationvo) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
            logger.debug("conversationvo = " + conversationvo);
        }
        /*TbsysdmservertransactionVO vo = template.load(TbsysdmservertransactionVO.class, uuid.getSectionID());
        vo.setConversationVO(conversationvo);
        template.update(vo);*/
        workstations.get(uuid.getWsId()).get(uuid.getSectionID()).getServerTransaction().setConversationVO(conversationvo);
    }

    public ClientTransaction getClientTransaction(UUID uuid) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
        }
        //return convert(template.get(TbsysdmclienttransactionVO.class, uuid.getSectionID()));
        return workstations.get(uuid.getWsId()).get(uuid.getSectionID()).getClientTransaction();
    }

    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setClientTransaction(UUID uuid, ClientTransaction ct) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
            logger.debug("ct = " + ct);
        }
        /*TbsysdmclienttransactionVO vo = template.load(TbsysdmclienttransactionVO.class, uuid.getSectionID());
        vo.setBizlogicVO(ct.getBizLogicVO());
        vo.setPlatformVO(ct.getPlatFormVO());
        template.update(vo);*/
        workstations.get(uuid.getWsId()).get(uuid.getSectionID()).setClientTransaction(ct);
    }

    public boolean deleteClientTransaction(UUID uuid) {
        //TbsysdmclienttransactionVO vo = template.load(TbsysdmclienttransactionVO.class, uuid.getSectionID());
        //template.delete(vo);
        workstations.get(uuid.getWsId()).get(uuid.getSectionID()).setClientTransaction(null);
        return true;
    }

    //@Transactional(readOnly = true)
    public User getUser(UUID uuid) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
        }
        String wsId = uuid.getWsId();
        if (logger.isDebugEnabled()) {
            logger.debug("wsId = " + wsId);
        }
        TbsysdmuserVO vo = template.get(TbsysdmuserVO.class, wsId);
        if (logger.isDebugEnabled()) {
            logger.debug("vo = " + vo);
        }
        if (vo != null) {
            User user = null;
            try {
                user = new UserDB(wsId);
                user.setStatus(vo.getStatus());
                user.setUserName(vo.getUserName());
                user.setUserID(vo.getUserID());
                user.setLevel(vo.getLevel());
                user.setSection(uuid.getSectionID());
                user.setErrLevel(vo.getErrLevel());
                user.setProxiedUserAuth(vo.getProxiedUserAuth());
                user.setProxiedUserID(vo.getProxiedUserID());
                user.setUserAuth(vo.getUserAuth());
            }
            catch (JBranchException e) {
                logger.error(e.getMessage(), e);
            }
            return user;
        }
        else {
            return null;
        }
    }

    public void setUser(UUID uuid, User user) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
            logger.debug("user = " + user);
        }
        if (user != null) {
            TbsysdmworkstationVO wsVO = template.load(TbsysdmworkstationVO.class, uuid.getWsId());
            TbsysdmuserVO vo;
            Set<TbsysdmuserVO> vos = wsVO.getUser();
            boolean exist = !vos.isEmpty();
            vo = exist ? vos.iterator().next() : new TbsysdmuserVO();
            vo.setUserID(user.getUserID());
            vo.setStatus(user.getStatus());
            vo.setUserName(user.getUserName());
            vo.setErrLevel(user.getErrLevel());
            vo.setLevel(user.getLevel());
            vo.setProxiedUserAuth(user.getProxiedUserAuth());
            vo.setProxiedUserID(user.getProxiedUserID());
            vo.setUserAuth(user.getUserAuth());
            vo.setWorkstation(wsVO);
            if (!exist) {
                PlatFormVO platFormVO = user.getPlatFormVO();
                vo.setPlatformVars(platFormVO.getVars());
                BizlogicVO bizlogicVO = user.getBizlogicVO();
                vo.setBizlogicVars(bizlogicVO.getVars());
            }
            template.saveOrUpdate(vo);
        }
        else {
            TbsysdmuserVO vo = template.get(TbsysdmuserVO.class, uuid.getWsId());
            if (vo != null) {
                template.delete(vo);
            }
        }
        //template.flush();
    }

    //@Transactional(readOnly = true)
    public boolean existUser(UUID uuid) {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
        }
        TbsysdmuserVO vo = null;
        if (uuid != null) {
            vo = template.get(TbsysdmuserVO.class, uuid.getWsId());
        }
        return vo != null;
    }

    public TransactionDefinition getTransactionDefinition(String txnCode) throws JBranchException {
        if (logger.isDebugEnabled()) {
            logger.debug("txnCode = " + txnCode);
        }
        return TransactionDefinitionHashTable.getTransactionDefinition(txnCode);
    }

    public TransactionDefinition getTransactionDefinition(UUID uuid) throws JBranchException {
        if (logger.isDebugEnabled()) {
            logger.debug("uuid = " + uuid);
        }
        ServerTransaction serverTransaction = getServerTransaction(uuid);
        return serverTransaction.getTxnDefinition();
    }

    public void cloneObjects(UUID oldUuid, UUID newUuid) {
        if (logger.isDebugEnabled()) {
            logger.debug("oldUuid = " + oldUuid);
            logger.debug("newUuid = " + newUuid);
        }
        TbsysdmworkstationVO workstationVO = template.load(TbsysdmworkstationVO.class, oldUuid.getWsId());
        TbsysdmbranchVO branchVO = template.load(TbsysdmbranchVO.class, newUuid.getBranchID());
        workstationVO.setBranch(branchVO);
        template.save(workstationVO);
    }

    private Branch convert(TbsysdmbranchVO vo) {
        Branch branch = null;
        if (vo != null) {
            try {
                branch = new BranchDB(vo.getBrchID());
                branch.setName(vo.getName());
                branch.setOpDate(vo.getOpDate());
                branch.setTxnFlag(vo.getTxnFlag());
                branch.setBrchID(vo.getBrchID());
            }
            catch (JBranchException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return branch;
    }

    private WorkStation convert(TbsysdmworkstationVO vo) {
        WorkStationDB workStation = null;
        if (vo != null) {
            try {
                workStation = new WorkStationDB();
                workStation.setApplicationID(vo.getApplicationID());
                workStation.setDevelopMode(vo.getDevelopMode());
                workStation.setLocalMode(vo.getLocalMode());
                workStation.setSignOnBranchID(vo.getSignOnBranchID());
                workStation.setSignOnWsID(vo.getSignOnWsID());
                workStation.setTouchedTime(vo.getTouchedTime());
                workStation.setWsID(vo.getWsID());
                workStation.setWsIP(vo.getWsIP());
                Set<TbsysdmuserVO> users = vo.getUser();
                if (!users.isEmpty()) {
                    User user = convert(users.iterator().next());
                    workStation.setUserVO(user);
                }
            }
            catch (JBranchException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return workStation;
    }

    private User convert(TbsysdmuserVO vo) throws JBranchException {
        User user = null;
        if (vo != null) {
            user = new UserDB(vo.getWsID());
            user.setUserName(vo.getUserName());
            user.setProxiedUserAuth(vo.getProxiedUserAuth());
            user.setProxiedUserID(vo.getProxiedUserID());
            user.setErrLevel(vo.getErrLevel());
            user.setLevel(vo.getLevel());
            user.setStatus(vo.getStatus());
            user.setUserID(vo.getUserID());
            user.setUserAuth(vo.getUserAuth());
        }
        return user;
    }

    private ClientTransaction convert(TbsysdmclienttransactionVO vo) {
        ClientTransaction clientTransaction = null;
        if (vo != null) {
            clientTransaction = new ClientTransaction();
            clientTransaction.setBizlogicVO(vo.getBizlogicVO());
            clientTransaction.setPlatFormVO(vo.getPlatformVO());
        }
        return clientTransaction;
    }

    private ServerTransaction convert(TbsysdmservertransactionVO vo) {
        ServerTransaction serverTransaction = null;
        if (vo != null) {
            serverTransaction = new ServerTransaction();
            serverTransaction.setHost(vo.getHost());
            serverTransaction.setBizCode(vo.getBizCode());
            serverTransaction.setTxnCode(vo.getTxnCode());
            serverTransaction.setBizlogicVO(vo.getBizlogicVO());
            serverTransaction.setConversationVO(vo.getConversationVO());
            serverTransaction.setPlatFormVO(vo.getPlatformVO());
        }
        return serverTransaction;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }
}
