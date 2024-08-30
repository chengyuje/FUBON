package com.systex.jbranch.app.server.fps.prd176;

import java.math.BigDecimal;
import java.util.Date;

public class PRD176DataVO {
    private BigDecimal serialNum;       // 序號
    private String cname;               // 保險公司中文名稱
    private String shortname;           // 保險公司簡稱
    private String ename;               // 保險公司英文名稱
    private String zipcode;             // 保險總公司地址郵遞區碼
    private String address;             // 地址
    private String contactPersonName;   // 業務聯絡人姓名
    private String contactPersonPhone;  // 聯絡人電話
    private String idNum;               // 統一編號
    private String contactPersonEmail;  // 聯絡人 e-mail
    private String admContactPersonName;// 行政聯絡人姓名
    private String admContactPersonPhone; // 行政聯絡人電話
    private String admContactPersonEmail; // 行政聯絡人e-mail
    private String indContactPersonName; // 理賠聯絡人姓名
    private String indContactPersonPhone; // 理賠聯絡人電話
    private String indContactPersonEmail; // 理賠聯絡人e-mail
    private Date contractDate;            // 簽約日期
    private Date renewDate;               // 續約日期
    private Date termDate;                // 終止合約日期

    public BigDecimal getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(BigDecimal serialNum) {
        this.serialNum = serialNum;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactPersonPhone() {
        return contactPersonPhone;
    }

    public void setContactPersonPhone(String contactPersonPhone) {
        this.contactPersonPhone = contactPersonPhone;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getAdmContactPersonName() {
        return admContactPersonName;
    }

    public void setAdmContactPersonName(String admContactPersonName) {
        this.admContactPersonName = admContactPersonName;
    }

    public String getAdmContactPersonPhone() {
        return admContactPersonPhone;
    }

    public void setAdmContactPersonPhone(String admContactPersonPhone) {
        this.admContactPersonPhone = admContactPersonPhone;
    }

    public String getAdmContactPersonEmail() {
        return admContactPersonEmail;
    }

    public void setAdmContactPersonEmail(String admContactPersonEmail) {
        this.admContactPersonEmail = admContactPersonEmail;
    }

    public String getIndContactPersonName() {
        return indContactPersonName;
    }

    public void setIndContactPersonName(String indContactPersonName) {
        this.indContactPersonName = indContactPersonName;
    }

    public String getIndContactPersonPhone() {
        return indContactPersonPhone;
    }

    public void setIndContactPersonPhone(String indContactPersonPhone) {
        this.indContactPersonPhone = indContactPersonPhone;
    }

    public String getIndContactPersonEmail() {
        return indContactPersonEmail;
    }

    public void setIndContactPersonEmail(String indContactPersonEmail) {
        this.indContactPersonEmail = indContactPersonEmail;
    }

    public Date getContractDate() {
        return contractDate;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public Date getRenewDate() {
        return renewDate;
    }

    public void setRenewDate(Date renewDate) {
        this.renewDate = renewDate;
    }

    public Date getTermDate() {
        return termDate;
    }

    public void setTermDate(Date termDate) {
        this.termDate = termDate;
    }
}
