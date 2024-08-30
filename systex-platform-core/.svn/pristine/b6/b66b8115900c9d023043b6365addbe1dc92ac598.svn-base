package com.systex.jbranch.platform.common.dataManager;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

import java.util.Map;

/**
 * @author Alex Lin
 * @version 2010/11/10 11:00 AM
 */
public interface DataManagerIF {
    String getRoot();

    void setRoot(String r);

    void setRealPath(String value);

    String getRealPath();

    Map<String, Branch> getBranch();

    System getSystem();

    void setSystem(System system);

    Branch getBranch(String branchID);

    Branch getBranch(UUID uuid);

    void setBranch(String brID, Branch brch);

    void setBranch(UUID uuid, Branch brch);

    void setWorkStation(UUID uuid, WorkStation ws);

    void setWorkStation(String brchID, String wsID, WorkStation ws);

    WorkStation getWorkStation(String brchID, String wsID);

    WorkStation getWorkStation(UUID uuid);

    boolean deleteWorkStation(UUID uuid);

    boolean existWorkStation(UUID uuid);

    boolean existWorkStation(String branchID, String wsID);

    void setSection(UUID uuid, Section section);

    Section getSection(UUID uuid);

    boolean deleteSection(UUID uuid);

    ServerTransaction getServerTransaction(UUID uuid);

    void setServerTransaction(UUID uuid, ServerTransaction st);

    boolean deleteServerTransaction(UUID uuid);

    ConversationVO getConversationVO(UUID uuid);

    void setConversationVO(UUID uuid, ConversationVO conversationvo);

    ClientTransaction getClientTransaction(UUID uuid);

    void setClientTransaction(UUID uuid, ClientTransaction ct);

    boolean deleteClientTransaction(UUID uuid);

    User getUser(UUID uuid);

    void setUser(UUID uuid, User user);

    boolean existUser(UUID uuid);

    TransactionDefinition getTransactionDefinition(String txnCode) throws JBranchException;

    TransactionDefinition getTransactionDefinition(UUID uuid) throws JBranchException;

    void cloneObjects(UUID oldUuid, UUID newUuid);
}
