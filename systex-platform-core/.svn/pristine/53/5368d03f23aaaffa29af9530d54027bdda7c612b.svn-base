package com.systex.jbranch.platform.common.dataManager;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 提供適當的存取DataManager的介面，讓內部使用者與相關人員針對系統資料、變數的傳遞有良好且控管的管道。<br>
 *
 * @author Eric.Lin   <br>
 *         <p/>
 *         <p/>
 *         *
 */
public class DataManager {
// ------------------------------ FIELDS ------------------------------

    private static DataManagerIF dataManager;
    private static Logger logger = LoggerFactory.getLogger(DataManager.class);

// -------------------------- STATIC METHODS --------------------------

    static {
        try {
            dataManager = (DataManagerIF) PlatformContext.getBean("dataManager");
        }
        catch (Throwable e) {
        	e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
    }

    public static String getRoot() {
        return dataManager.getRoot();
    }

    public static void setRoot(String r) {
        dataManager.setRoot(r);
    }

    public static void setRealPath(String value) {
        dataManager.setRealPath(value);
    }

    public static String getRealPath() {
        return dataManager.getRealPath();
    }

    public static Map<String, Branch> getBranch() {
        return dataManager.getBranch();
    }

    //=======================================================

    /**
     * 依分行代號取得該分行。<br>
     *
     * @param branchID:分行代號<br>
     * @return
     */
    public static Branch getBranch(String branchID) {
        return dataManager.getBranch(branchID);
    }

    /**
     * 依uuid中的分行代號，取得該分行。<br>
     *
     * @param uuid:UUID 的物件。<br>
     * @return
     */
    public static Branch getBranch(UUID uuid) {
        return dataManager.getBranch(uuid);
    }

    /**
     * 取得系統組態物件<br>
     *
     * @return<br>
     */
    public static com.systex.jbranch.platform.common.dataManager.System getSystem() {
        return dataManager.getSystem();
    }

    /**
     * 設定系統組態<br>
     *
     * @param system<br>
     */
    public static void setSystem(com.systex.jbranch.platform.common.dataManager.System system) {
        dataManager.setSystem(system);
    }

    /**
     * 設定分行於DataManager中。<br>
     *
     * @param brID           :分行代號<br>
     * @param brch:分行物件。<br>
     */
    public static void setBranch(String brID, Branch brch) {
        dataManager.setBranch(brID, brch);
    }

    /**
     * 設定分行於DataManager中。<br>
     *
     * @param uuid:UUID的物件<br>
     * @param brch:分行物件。<br>
     */
    public static void setBranch(UUID uuid, Branch brch) {
        dataManager.setBranch(uuid, brch);
    }

    //=======================================================

    /**
     * 設定工作站於分行中<br>
     *
     * @param uuid:UUID的物件。<br>
     * @param ws:工作站物件。<br>
     */
    public static void setWorkStation(UUID uuid, WorkStation ws) {
        dataManager.setWorkStation(uuid, ws);
    }

    /**
     * 設定工作站於分行中<br>
     *
     * @param brchID:分行代號<br>
     * @param wsID:工作站代號<br>
     * @param ws:工作站物件。<br>
     */
    public static void setWorkStation(String brchID, String wsID, WorkStation ws) {
        dataManager.setWorkStation(brchID, wsID, ws);
    }

    /**
     * 以UUID，取得工作站。<br>
     *
     * @param uuid<br>
     * @return<br>
     */
    public static WorkStation getWorkStation(UUID uuid) {
        return dataManager.getWorkStation(uuid);
    }

    /**
     * 以分行代號，及工作站代取得工作站。<br>
     *
     * @param brchID<br>
     * @param wsID<br>
     * @return<br>
     */
    public static WorkStation getWorkStation(String brchID, String wsID) {
        return dataManager.getWorkStation(brchID, wsID);
    }

    /**
     * 以UUID刪除工作站。
     *
     * @param uuid
     * @return :ture :刪除成功;false:刪除失敗。
     */
    public static boolean deleteWorkStation(UUID uuid) {
        return dataManager.deleteWorkStation(uuid);
    }

    /**
     * 工作站是否存在DataManager.Branch中。
     *
     * @param uuid:UUID的物件。
     * @return: true:存在;false :不存在。
     */
    public static boolean existWorkStation(UUID uuid) {
        return dataManager.existWorkStation(uuid);
    }

    /**
     * 工作站是否存在DataManager.Branch中。<br>
     *
     * @param branchID:分行代號<br>
     * @param wsID:工作站代號<br>
     * @return true:存在;false :不存在。<br>
     */
    public static boolean existWorkStation(String branchID, String wsID) {
        return dataManager.existWorkStation(branchID, wsID);
    }

    //=======================================================

    /**
     * 設定工作站中的工作區。<br>
     *
     * @param uuid:UUID物件。<br>
     * @param section:工作區代號。<br>
     */
    public static void setSection(UUID uuid, Section section) {
        dataManager.setSection(uuid, section);
    }

    /**
     * 取得工作站中的工作區。
     * 若存在，回傳原有的instance
     * 若不存在，則會new 一個instance回傳。
     *
     * @param uuid:UUID物件。
     * @return
     */
    public static Section getSection(UUID uuid) {
        return dataManager.getSection(uuid);
    }

    /**
     * 刪除工作站中的工作區。<br>
     *
     * @param uuid:UUID物件。<br>
     * @return true:正常刪除; false:刪除不正常，可能section不存在。<br>
     */
    public static boolean deleteSection(UUID uuid) {
        return dataManager.deleteSection(uuid);
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
    public static ServerTransaction getServerTransaction(UUID uuid) {
        return dataManager.getServerTransaction(uuid);
    }

    /**
     * 設定工作區中的ServerTransaction。
     *
     * @param uuid
     * @param st
     */
    public static void setServerTransaction(UUID uuid, ServerTransaction st) {
        dataManager.setServerTransaction(uuid, st);
    }

    /**
     * 刪除工作區中的ServerTransaction<br>
     *
     * @param uuid:UUID物件<br>
     * @return true:正常刪除;false:刪除異常，可能不存在。<br>
     */
    public static boolean deleteServerTransaction(UUID uuid) {
        return dataManager.deleteServerTransaction(uuid);
    }

    //=======================================================

    /**
     * 取得ServerTransaction中ConversationVO<br>
     *
     * @param uuid:UUID物件<br>
     * @return<br>
     */
    public static ConversationVO getConversationVO(UUID uuid) {
        return dataManager.getConversationVO(uuid);
    }

    /**
     * 設定ConversationVO於ServerTransaction<br>
     *
     * @param uuid:UUID物件<br>
     * @param conversationvo:ConversationVO的物件。<br>
     *
     */
    public static void setConversationVO(UUID uuid, ConversationVO conversationvo) {
        dataManager.setConversationVO(uuid, conversationvo);
    }

    //=======================================================

    /**
     * 取得工作區中的ClientTransaction。<br>
     *
     * @param uuid:UUID物件<br>
     * @return<br>
     */
    public static ClientTransaction getClientTransaction(UUID uuid) {
        return dataManager.getClientTransaction(uuid);
    }

    /**
     * 設定工作區中的ClientTransaction。
     *
     * @param uuid:UUID物件
     * @param ct:
     */
    public static void setClientTransaction(UUID uuid, ClientTransaction ct) {
        dataManager.setClientTransaction(uuid, ct);
    }

    /**
     * 刪除工作區中的ClientTransaction。
     *
     * @param uuid:UUID物件
     * @return true:正常刪除;false:不正常刪除，可能不存在。
     */
    public static boolean deleteClientTransaction(UUID uuid) {
        return dataManager.deleteClientTransaction(uuid);
    }

    //=======================================================

    /**
     * 取得使用者物件。<br>
     * uuid:UUID物件<br>
     *
     * @param uuid:UUID物件<br>
     */
    public static User getUser(UUID uuid) {
        return dataManager.getUser(uuid);
    }

    /**
     * 設定使用者物件<br>
     *
     * @param uuid:UUID物件。<br>
     * @param user:User物件。<br>
     */
    public static void setUser(UUID uuid, User user) {
        dataManager.setUser(uuid, user);
    }

    /**
     * 使用者是否存在工作站中。<br>
     *
     * @param uuid:UUID物件。<br>
     * @return true:存在;false:不存在。<br>
     */
    public static boolean existUser(UUID uuid) {
        return dataManager.existUser(uuid);
    }

    /**
     * 取得TransactionDefinition<br>
     *
     * @param txnCode:String。<br>
     * @return <br>
     * @throws JBranchException
     */
    public static TransactionDefinition getTransactionDefinition(String txnCode) throws JBranchException {
        return dataManager.getTransactionDefinition(txnCode);
    }

    /**
     * 取得TransactionDefinition<br>
     *
     * @param uuid:UUID物件。<br>
     * @return <br>
     * @throws JBranchException
     */
    public static TransactionDefinition getTransactionDefinition(UUID uuid) throws JBranchException {
        return dataManager.getTransactionDefinition(uuid);
    }

    /**
     * 複製以舊的UUID在DataManager的資料至新的UUID至DataManager中。
     * 時機點僅在工作站登入的時候
     *
     * @param oldUuid
     * @param newUuid
     * @author Eric.Lin
     */
    public static void cloneObjects(UUID oldUuid, UUID newUuid) {
        dataManager.cloneObjects(oldUuid, newUuid);
    }
}