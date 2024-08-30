package com.systex.jbranch.fubon.commons.cbs.vo.basic;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "TxHead")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class TxHeadVO {
    /**
     * 部分電文 UUID 後面需要加上給定值
     **/
    @XmlTransient
    private String UUIDSuffix = "";

    @XmlElement
    private String Filler = "0";

    @XmlElement
    private String MessageType = "0";

    @XmlElement
    private String SegmentNumber = "0000";

    @XmlElement
    private String FrontEndNumber = "0000";

    /**
     * 部分電文沒有，需要設定為 ""
     **/
    @XmlElement
    private String FlagX = "Z";

    @XmlElement
    private String TerminalNumber = "0000";

    @XmlElement
    private String InstitutionNumber = "003";

    @XmlElement
    private String BranchNumber = "00000";

    @XmlElement
    private String WorkstationNumber = "000";

    @XmlElement
    private String TellerNumber;

    @XmlElement
    private String TransactionCode;

    @XmlElement
    private String JournalNumber = "000000000";

    @XmlElement
    private String FLAG1 = "4";

    @XmlElement
    private String FLAG2 = "0";

    @XmlElement
    private String FLAG3 = "0";

    @XmlElement
    private String FLAG4 = "Z";

    /**
     * 部分電文沒有
     **/
    @XmlElement
    private String SubsysChannel;

    @XmlElement
    private String SupervisorID = "00000000";

    @XmlElement
    private String UUID;

    @XmlElement
    private String SupervisorID2 = "00000000";

    @XmlElement(name = "SUPERR1-OVRD")
    private String SUPERR1_OVRD = "0000";

    @XmlElement(name = "SUPERR2-OVRD")
    private String SUPERR2_OVRD = "0000";

    @XmlElement
    private String Channel_ID = "31";

    public String getFiller() {
        return Filler;
    }

    public TxHeadVO setFiller(String filler) {
        Filler = filler;
        return this;
    }

    public String getMessageType() {
        return MessageType;
    }

    public TxHeadVO setMessageType(String messageType) {
        MessageType = messageType;
        return this;
    }

    public String getSegmentNumber() {
        return SegmentNumber;
    }

    public TxHeadVO setSegmentNumber(String segmentNumber) {
        SegmentNumber = segmentNumber;
        return this;
    }

    public String getFrontEndNumber() {
        return FrontEndNumber;
    }

    public TxHeadVO setFrontEndNumber(String frontEndNumber) {
        FrontEndNumber = frontEndNumber;
        return this;
    }

    public String getFlagX() {
        return FlagX;
    }

    public TxHeadVO setFlagX(String flagX) {
        FlagX = flagX;
        return this;
    }

    public String getTerminalNumber() {
        return TerminalNumber;
    }

    public TxHeadVO setTerminalNumber(String terminalNumber) {
        TerminalNumber = terminalNumber;
        return this;
    }

    public String getInstitutionNumber() {
        return InstitutionNumber;
    }

    public TxHeadVO setInstitutionNumber(String institutionNumber) {
        InstitutionNumber = institutionNumber;
        return this;
    }

    public String getBranchNumber() {
        return BranchNumber;
    }

    public TxHeadVO setBranchNumber(String branchNumber) {
        BranchNumber = branchNumber;
        return this;
    }

    public String getWorkstationNumber() {
        return WorkstationNumber;
    }

    public TxHeadVO setWorkstationNumber(String workstationNumber) {
        WorkstationNumber = workstationNumber;
        return this;
    }

    public String getTellerNumber() {
        return TellerNumber;
    }

    public TxHeadVO setTellerNumber(String tellerNumber) {
        TellerNumber = tellerNumber;
        return this;
    }

    public String getTransactionCode() {
        return TransactionCode;
    }

    public TxHeadVO setTransactionCode(String transactionCode) {
        TransactionCode = transactionCode;
        return this;
    }

    public String getJournalNumber() {
        return JournalNumber;
    }

    public TxHeadVO setJournalNumber(String journalNumber) {
        JournalNumber = journalNumber;
        return this;
    }

    public String getFLAG1() {
        return FLAG1;
    }

    public TxHeadVO setFLAG1(String FLAG1) {
        this.FLAG1 = FLAG1;
        return this;
    }

    public String getFLAG2() {
        return FLAG2;
    }

    public TxHeadVO setFLAG2(String FLAG2) {
        this.FLAG2 = FLAG2;
        return this;
    }

    public String getFLAG3() {
        return FLAG3;
    }

    public TxHeadVO setFLAG3(String FLAG3) {
        this.FLAG3 = FLAG3;
        return this;
    }

    public String getFLAG4() {
        return FLAG4;
    }

    public TxHeadVO setFLAG4(String FLAG4) {
        this.FLAG4 = FLAG4;
        return this;
    }

    public String getSupervisorID() {
        return SupervisorID;
    }

    public TxHeadVO setSupervisorID(String supervisorID) {
        SupervisorID = supervisorID;
        return this;
    }

    public String getUUID() {
        return UUID;
    }

    public TxHeadVO setUUID(String UUID) {
        this.UUID = UUID;
        return this;
    }

    public String getSupervisorID2() {
        return SupervisorID2;
    }

    public TxHeadVO setSupervisorID2(String supervisorID2) {
        SupervisorID2 = supervisorID2;
        return this;
    }

    public String getSUPERR1_OVRD() {
        return SUPERR1_OVRD;
    }

    public TxHeadVO setSUPERR1_OVRD(String SUPERR1_OVRD) {
        this.SUPERR1_OVRD = SUPERR1_OVRD;
        return this;
    }

    public String getSUPERR2_OVRD() {
        return SUPERR2_OVRD;
    }

    public TxHeadVO setSUPERR2_OVRD(String SUPERR2_OVRD) {
        this.SUPERR2_OVRD = SUPERR2_OVRD;
        return this;
    }

    public String getChannel_ID() {
        return Channel_ID;
    }

    public TxHeadVO setChannel_ID(String channel_ID) {
        Channel_ID = channel_ID;
        return this;
    }

    public String getSubsysChannel() {
        return SubsysChannel;
    }

    public TxHeadVO setSubsysChannel(String subsysChannel) {
        SubsysChannel = subsysChannel;
        return this;
    }

    public String getUUIDSuffix() {
        return UUIDSuffix;
    }

    public TxHeadVO setUUIDSuffix(String UUIDSuffix) {
        this.UUIDSuffix = UUIDSuffix;
        return this;
    }
}
