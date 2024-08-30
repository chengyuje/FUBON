package com.systex.jbranch.platform.common.dataManager.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataManager.Branch;
import com.systex.jbranch.platform.common.dataManager.ClientTransaction;
import com.systex.jbranch.platform.common.dataManager.ConversationVO;
import com.systex.jbranch.platform.common.dataManager.DataManagerIF;
import com.systex.jbranch.platform.common.dataManager.Section;
import com.systex.jbranch.platform.common.dataManager.ServerTransaction;
import com.systex.jbranch.platform.common.dataManager.TransactionDefinition;
import com.systex.jbranch.platform.common.dataManager.TransactionDefinitionHashTable;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**
 * 提供適當的存取DataManager的介面，讓內部使用者與相關人員針對系統資料、變數的傳遞有良好且控管的管道。<br>
 *
 * @author Eric.Lin   <br>
 *         <p/>
 *         <p/>
 *         *
 */
public class DataManagerImpl implements DataManagerIF {
// ------------------------------ FIELDS ------------------------------

	private static final String FILE_SEPARATOR = java.lang.System.getProperties().getProperty("file.separator");
    private static char[] charArray = FILE_SEPARATOR.toCharArray();
    protected com.systex.jbranch.platform.common.dataManager.System system;
    protected Map<String, Branch> branch = new ConcurrentHashMap<String, Branch>();

    private String realPath;
    private String root;

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

    public Map<String, Branch> getBranch() {
        return Collections.unmodifiableMap(branch);
    }

    /**
     * 取得系統組態物件<br>
     * @return system
     */
    public com.systex.jbranch.platform.common.dataManager.System getSystem() {
        return system;
    }

    /**
     * 設定系統組態<br>
     * @param system system
     */
    public void setSystem(com.systex.jbranch.platform.common.dataManager.System system) {
        this.system = system;
        //threadLogger.println("DataManager.system:" + DataManager.system);
    }

    //=======================================================

    /**
     * 依分行代號取得該分行。<br>
     *
     * @param branchID:分行代號<br>
     * @return branch
     */
    public Branch getBranch(String branchID) {
        return branch.get(branchID);
    }

    /**
     * 依uuid中的分行代號，取得該分行。<br>
     *
     * @param uuid:UUID 的物件。<br>
     * @return branch
     */
    public Branch getBranch(UUID uuid) {
        if (uuid != null) {
        	Branch branch = getBranch(uuid.getBranchID());
            return branch;
        }
        return null;
    }

    /**
     * 設定分行於DataManager中。<br>
     *
     * @param brID           :分行代號<br>
     * @param brch:分行物件。<br>
     */
    public void setBranch(String brID, Branch brch) {
        branch.put(brID, brch);
    }

    /**
     * 設定分行於DataManager中。<br>
     *
     * @param uuid:UUID的物件<br>
     * @param brch:分行物件。<br>
     */
    public void setBranch(UUID uuid, Branch brch) {
        if (uuid != null) {
            setBranch(uuid.getBranchID(), brch);
        }
    }

    //=======================================================

    /**
     * 設定工作站於分行中<br>
     *
     * @param uuid:UUID的物件。<br>
     * @param ws:工作站物件。<br>
     */
    public void setWorkStation(UUID uuid, WorkStation ws) {
        if (uuid != null) {
            setWorkStation(uuid.getBranchID(), uuid.getWsId(), ws);
        }
    }

    /**
     * 設定工作站於分行中<br>
     *
     * @param brchID:分行代號<br>
     * @param wsID:工作站代號<br>
     * @param ws:工作站物件。<br>
     */
    public void setWorkStation(String brchID, String wsID, WorkStation ws) {
        Branch brh = getBranch(brchID);
        if (brh != null) {
            brh.setWorkStation(wsID, ws);
        }
    }

    /**
     * 以分行代號，及工作站代取得工作站。<br>
     *
     * @param brchID
     * @param wsID
     * @return workstation
     */
    public WorkStation getWorkStation(String brchID, String wsID) {
        Branch brh = getBranch(brchID);
        if (brh != null) {
            return brh.getWorkStation(wsID);
        }
        return null;
    }

    /**
     * 以UUID，取得工作站。<br>
     *
     * @param uuid
     * @return
     */
    public WorkStation getWorkStation(UUID uuid) {
        if (uuid != null) {
            return getWorkStation(uuid.getBranchID(), uuid.getWsId());
        }
        return null;
    }

    /**
     * 以UUID刪除工作站。
     *
     * @param uuid
     * @return :ture :刪除成功;false:刪除失敗。
     */
    public boolean deleteWorkStation(UUID uuid) {
        boolean bRet = false;
        Branch brh = getBranch(uuid);
        if (brh != null) {
            bRet = brh.deleteWorkStation(uuid.getWsId());
        }
        return bRet;
    }

    /**
     * 工作站是否存在DataManager.Branch中。
     *
     * @param uuid:UUID的物件。
     * @return: true:存在;false :不存在。
     */
    public boolean existWorkStation(UUID uuid) {
        Branch brh = getBranch(uuid);
        return brh != null && brh.existWorkStation(uuid.getWsId());
    }

    /**
     * 工作站是否存在DataManager.Branch中。<br>
     *
     * @param branchID:分行代號<br>
     * @param wsID:工作站代號<br>
     * @return true:存在;false :不存在。<br>
     */
    public boolean existWorkStation(String branchID, String wsID) {
        boolean bRet = false;
        Branch brh = getBranch(branchID);
        if (brh != null) {
            bRet = brh.existWorkStation(wsID);
        }
        return bRet;
    }

    //=======================================================

    /**
     * 設定工作站中的工作區。<br>
     *
     * @param uuid:UUID物件。<br>
     * @param section:工作區代號。<br>
     */
    public void setSection(UUID uuid, Section section) {
        Branch brh = getBranch(uuid);
        if (brh != null) {
            WorkStation wks = brh.getWorkStation(uuid.getWsId());
            if(wks == null){
            	wks = new WorkStation();
            	wks.setBrchID(uuid.getBranchID());
                wks.setWsID(uuid.getWsId());
                setWorkStation(uuid, wks);
            }
            wks.setSection(uuid.getSectionID(), section);
        }
    }

    /**
     * 取得工作站中的工作區。
     * 若存在，回傳原有的instance
     * 若不存在，則會new 一個instance回傳。
     *
     * @param uuid:UUID物件。
     * @return
     */
    public Section getSection(UUID uuid) {
        Section section = null;
        Branch brh = getBranch(uuid);
        if (brh != null) {
            WorkStation wks = brh.getWorkStation(uuid.getWsId());
            if (wks != null) {
                section = wks.getSection(uuid.getSectionID());
            }
        }
        return section;
    }

    /**
     * 刪除工作站中的工作區。<br>
     *
     * @param uuid:UUID物件。<br>
     * @return true:正常刪除; false:刪除不正常，可能section不存在。<br>
     */
    public boolean deleteSection(UUID uuid) {
        Branch brh = getBranch(uuid);
        if (brh != null) {
            WorkStation wks = brh.getWorkStation(uuid.getWsId());
            if (wks != null) {
                return wks.deleteSection(uuid.getSectionID());
            }
        }
        return false;
    }

    //=======================================================

    /**
     * 取得工作區中的ServerTransaction<br>
     * 若存在，回傳原有的instance<br>
     * 若不存在，new 一個instance 回傳之。<br>
     *
     * @param uuid:UUID物件。<br>
     * @return<br>
     */
    public ServerTransaction getServerTransaction(UUID uuid) {
        Section section = getSection(uuid);
        if (section != null) {
            return section.getServerTransaction();
        }
        return null;
    }

    /**
     * 設定工作區中的ServerTransaction。
     *
     * @param uuid
     * @param st
     */
    public void setServerTransaction(UUID uuid, ServerTransaction st) {
        Section section = getSection(uuid);
        if (section != null) {
            section.setServerTransaction(st);
        }
    }

    /**
     * 刪除工作區中的ServerTransaction<br>
     *
     * @param uuid:UUID物件<br>
     * @return true:正常刪除;false:刪除異常，可能不存在。<br>
     */
    public boolean deleteServerTransaction(UUID uuid) {
        Section section = getSection(uuid);
        return section != null && section.deleteServerTransaction();
    }

    //=======================================================

    /**
     * 取得ServerTransaction中ConversationVO<br>
     *
     * @param uuid:UUID物件<br>
     * @return<br>
     */
    public ConversationVO getConversationVO(UUID uuid) {
        ServerTransaction st = getServerTransaction(uuid);
        if (st != null) {
            return st.getConversationVO();
        }
        return null;
    }

    /**
     * 設定ConversationVO於ServerTransaction<br>
     *
     * @param uuid:UUID物件<br>
     * @param conversationvo:ConversationVO的物件。<br>
     *
     */
    public void setConversationVO(UUID uuid, ConversationVO conversationvo) {
        ServerTransaction st = getServerTransaction(uuid);
        if (st != null) {
            st.setConversationVO(conversationvo);
        }
    }

    //=======================================================

    /**
     * 取得工作區中的ClientTransaction。<br>
     *
     * @param uuid:UUID物件<br>
     * @return<br>
     */
    public ClientTransaction getClientTransaction(UUID uuid) {
        Section section = getSection(uuid);
        if (section != null) {
            return section.getClientTransaction();
        }
        return null;
    }

    /**
     * 設定工作區中的ClientTransaction。
     *
     * @param uuid:UUID物件
     * @param ct:
     */
    public void setClientTransaction(UUID uuid, ClientTransaction ct) {
        Section section = getSection(uuid);
        if (section != null) {
            section.setClientTransaction(ct);
        }
    }

    /**
     * 刪除工作區中的ClientTransaction。
     *
     * @param uuid:UUID物件
     * @return true:正常刪除;false:不正常刪除，可能不存在。
     */
    public boolean deleteClientTransaction(UUID uuid) {
        Section section = getSection(uuid);
        return section != null && section.deleteClientTransaction();
    }

    //=======================================================

    /**
     * 取得使用者物件。<br>
     * uuid:UUID物件<br>
     *
     * @param uuid:UUID物件<br>
     */
    public User getUser(UUID uuid) {
        WorkStation wks = getWorkStation(uuid);
        if (wks != null) {
            return wks.getUser();
        }
        return null;
    }

    /**
     * 設定使用者物件<br>
     *
     * @param uuid:UUID物件。<br>
     * @param user:User物件。<br>
     */
    public void setUser(UUID uuid, User user) {
        WorkStation wks = getWorkStation(uuid);
        if (wks != null) {
            wks.setUser(user);
        }
    }

    /**
     * 使用者是否存在工作站中。<br>
     *
     * @param uuid:UUID物件。<br>
     * @return true:存在;false:不存在。<br>
     */
    public boolean existUser(UUID uuid) {
        WorkStation wks = getWorkStation(uuid);
        if (wks != null) {
            if (wks.getUser() != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取得TransactionDefinition<br>
     *
     * @param txnCode:String。<br>
     * @return <br>
     * @throws com.systex.jbranch.platform.common.errHandle.JBranchException
     *
     */
    public TransactionDefinition getTransactionDefinition(String txnCode) throws JBranchException {
        return TransactionDefinitionHashTable.getTransactionDefinition(txnCode);
    }

    /**
     * 取得TransactionDefinition<br>
     *
     * @param uuid:UUID物件。<br>
     * @return <br>
     * @throws com.systex.jbranch.platform.common.errHandle.JBranchException
     *
     */
    public TransactionDefinition getTransactionDefinition(UUID uuid) throws JBranchException {
        /*
          ClientTransaction ct = getClientTransaction(uuid);
          if (ct != null) {
              return ct.getTxnDefinition();
          }
          */
        ServerTransaction st = getServerTransaction(uuid);
        if (st != null) {
            return st.getTxnDefinition();
        }
        return null;
    }

    /**
     * 複製以舊的UUID在DataManager的資料至新的UUID至DataManager中。
     * 時機點僅在工作站登入的時候
     *
     * @param oldUuid
     * @param newUuid
     * @author Eric.Lin
     */
    //TODO:survey一下有無整個class clone的方式
    public void cloneObjects(UUID oldUuid, UUID newUuid) {
        //---------------------ClientTransaction
        //threadLogger.println("cloneObjects from " + oldUuid + " to " + newUuid);
        //ClientTransaction oldCt = getClientTransaction(oldUuid);
        //setClientTransaction(newUuid, oldCt);

        //---------------------ServerTransaction
        //ServerTransaction oldSt = getServerTransaction(oldUuid);
        //DataManager.setServerTransaction(newUuid, oldSt);

        //---------------------Section
        Section oldSection = getSection(oldUuid);
        setSection(newUuid, oldSection);

        //---------------------WorkStation
        WorkStation oldWs = getWorkStation(oldUuid);
        oldWs.setBrchID(newUuid.getBranchID());
        oldWs.setWsID(newUuid.getWsId());
        oldWs.setSection(newUuid.getSectionID(), oldSection);
        setWorkStation(newUuid, oldWs);
    }
}