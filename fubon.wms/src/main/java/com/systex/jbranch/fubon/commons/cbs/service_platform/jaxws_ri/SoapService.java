
package com.systex.jbranch.fubon.commons.cbs.service_platform.jaxws_ri;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>anonymous complex type 的 Java 類別.
 * 
 * <p>下列綱要片段會指定此類別中包含的預期內容.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceHeader">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="HSYDAY" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="HSYTIME" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="SPName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="TxID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="HWSID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="HSTANO" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="OSERNO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="HTLID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="HFMTID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="HRETRN" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="Encoding" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ResponseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="UUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ServiceBody">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="TxnString" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="DataType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="ShouldRender" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ServiceError" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ErrorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="ErrorCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *                   &lt;element name="ErrorFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "serviceHeader",
    "serviceBody",
    "serviceError"
})
@XmlRootElement(name = "SoapService")
public class SoapService {

    @XmlElement(name = "ServiceHeader", required = true)
    protected SoapService.ServiceHeader serviceHeader;
    @XmlElement(name = "ServiceBody", required = true)
    protected SoapService.ServiceBody serviceBody;
    @XmlElement(name = "ServiceError")
    protected SoapService.ServiceError serviceError;

    /**
     * 取得 serviceHeader 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link SoapService.ServiceHeader }
     *     
     */
    public SoapService.ServiceHeader getServiceHeader() {
        return serviceHeader;
    }

    /**
     * 設定 serviceHeader 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link SoapService.ServiceHeader }
     *     
     */
    public void setServiceHeader(SoapService.ServiceHeader value) {
        this.serviceHeader = value;
    }

    /**
     * 取得 serviceBody 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link SoapService.ServiceBody }
     *     
     */
    public SoapService.ServiceBody getServiceBody() {
        return serviceBody;
    }

    /**
     * 設定 serviceBody 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link SoapService.ServiceBody }
     *     
     */
    public void setServiceBody(SoapService.ServiceBody value) {
        this.serviceBody = value;
    }

    /**
     * 取得 serviceError 特性的值.
     * 
     * @return
     *     possible object is
     *     {@link SoapService.ServiceError }
     *     
     */
    public SoapService.ServiceError getServiceError() {
        return serviceError;
    }

    /**
     * 設定 serviceError 特性的值.
     * 
     * @param value
     *     allowed object is
     *     {@link SoapService.ServiceError }
     *     
     */
    public void setServiceError(SoapService.ServiceError value) {
        this.serviceError = value;
    }


    /**
     * <p>anonymous complex type 的 Java 類別.
     * 
     * <p>下列綱要片段會指定此類別中包含的預期內容.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="TxnString" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="DataType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ShouldRender" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "txnString",
        "dataType",
        "shouldRender"
    })
    public static class ServiceBody {

        @XmlElement(name = "TxnString", required = true)
        protected String txnString;
        @XmlElement(name = "DataType", required = true)
        protected String dataType;
        @XmlElement(name = "ShouldRender")
        protected Boolean shouldRender;

        /**
         * 取得 txnString 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTxnString() {
            return txnString;
        }

        /**
         * 設定 txnString 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTxnString(String value) {
            this.txnString = value;
        }

        /**
         * 取得 dataType 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDataType() {
            return dataType;
        }

        /**
         * 設定 dataType 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDataType(String value) {
            this.dataType = value;
        }

        /**
         * 取得 shouldRender 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isShouldRender() {
            return shouldRender;
        }

        /**
         * 設定 shouldRender 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setShouldRender(Boolean value) {
            this.shouldRender = value;
        }

    }


    /**
     * <p>anonymous complex type 的 Java 類別.
     * 
     * <p>下列綱要片段會指定此類別中包含的預期內容.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ErrorCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="ErrorCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
     *         &lt;element name="ErrorFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "errorCode",
        "errorCategory",
        "errorMessage",
        "timestamp",
        "errorFrom"
    })
    public static class ServiceError {

        @XmlElement(name = "ErrorCode")
        protected String errorCode;
        @XmlElement(name = "ErrorCategory")
        protected String errorCategory;
        @XmlElement(name = "ErrorMessage")
        protected String errorMessage;
        @XmlElement(name = "Timestamp")
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar timestamp;
        @XmlElement(name = "ErrorFrom")
        protected String errorFrom;

        /**
         * 取得 errorCode 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getErrorCode() {
            return errorCode;
        }

        /**
         * 設定 errorCode 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setErrorCode(String value) {
            this.errorCode = value;
        }

        /**
         * 取得 errorCategory 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getErrorCategory() {
            return errorCategory;
        }

        /**
         * 設定 errorCategory 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setErrorCategory(String value) {
            this.errorCategory = value;
        }

        /**
         * 取得 errorMessage 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getErrorMessage() {
            return errorMessage;
        }

        /**
         * 設定 errorMessage 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setErrorMessage(String value) {
            this.errorMessage = value;
        }

        /**
         * 取得 timestamp 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getTimestamp() {
            return timestamp;
        }

        /**
         * 設定 timestamp 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setTimestamp(XMLGregorianCalendar value) {
            this.timestamp = value;
        }

        /**
         * 取得 errorFrom 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getErrorFrom() {
            return errorFrom;
        }

        /**
         * 設定 errorFrom 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setErrorFrom(String value) {
            this.errorFrom = value;
        }

    }


    /**
     * <p>anonymous complex type 的 Java 類別.
     * 
     * <p>下列綱要片段會指定此類別中包含的預期內容.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="HSYDAY" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="HSYTIME" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="SPName" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="TxID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="HWSID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="HSTANO" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="OSERNO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="HTLID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="HFMTID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="HRETRN" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="Encoding" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="ResponseCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="UUID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "hsyday",
        "hsytime",
        "userName",
        "password",
        "spName",
        "txID",
        "hwsid",
        "hstano",
        "oserno",
        "htlid",
        "hfmtid",
        "hretrn",
        "encoding",
        "responseCode",
        "uuid"
    })
    public static class ServiceHeader {

        @XmlElement(name = "HSYDAY", required = true)
        protected String hsyday;
        @XmlElement(name = "HSYTIME", required = true)
        protected String hsytime;
        @XmlElement(name = "UserName")
        protected String userName;
        @XmlElement(name = "Password")
        protected String password;
        @XmlElement(name = "SPName", required = true)
        protected String spName;
        @XmlElement(name = "TxID", required = true)
        protected String txID;
        @XmlElement(name = "HWSID", required = true)
        protected String hwsid;
        @XmlElement(name = "HSTANO", required = true)
        protected String hstano;
        @XmlElement(name = "OSERNO")
        protected String oserno;
        @XmlElement(name = "HTLID")
        protected String htlid;
        @XmlElement(name = "HFMTID")
        protected String hfmtid;
        @XmlElement(name = "HRETRN", required = true)
        protected String hretrn;
        @XmlElement(name = "Encoding", required = true)
        protected String encoding;
        @XmlElement(name = "ResponseCode")
        protected String responseCode;
        @XmlElement(name = "UUID", required = true)
        protected String uuid;

        /**
         * 取得 hsyday 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHSYDAY() {
            return hsyday;
        }

        /**
         * 設定 hsyday 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHSYDAY(String value) {
            this.hsyday = value;
        }

        /**
         * 取得 hsytime 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHSYTIME() {
            return hsytime;
        }

        /**
         * 設定 hsytime 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHSYTIME(String value) {
            this.hsytime = value;
        }

        /**
         * 取得 userName 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUserName() {
            return userName;
        }

        /**
         * 設定 userName 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUserName(String value) {
            this.userName = value;
        }

        /**
         * 取得 password 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPassword() {
            return password;
        }

        /**
         * 設定 password 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPassword(String value) {
            this.password = value;
        }

        /**
         * 取得 spName 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSPName() {
            return spName;
        }

        /**
         * 設定 spName 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSPName(String value) {
            this.spName = value;
        }

        /**
         * 取得 txID 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTxID() {
            return txID;
        }

        /**
         * 設定 txID 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTxID(String value) {
            this.txID = value;
        }

        /**
         * 取得 hwsid 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHWSID() {
            return hwsid;
        }

        /**
         * 設定 hwsid 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHWSID(String value) {
            this.hwsid = value;
        }

        /**
         * 取得 hstano 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHSTANO() {
            return hstano;
        }

        /**
         * 設定 hstano 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHSTANO(String value) {
            this.hstano = value;
        }

        /**
         * 取得 oserno 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOSERNO() {
            return oserno;
        }

        /**
         * 設定 oserno 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOSERNO(String value) {
            this.oserno = value;
        }

        /**
         * 取得 htlid 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHTLID() {
            return htlid;
        }

        /**
         * 設定 htlid 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHTLID(String value) {
            this.htlid = value;
        }

        /**
         * 取得 hfmtid 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHFMTID() {
            return hfmtid;
        }

        /**
         * 設定 hfmtid 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHFMTID(String value) {
            this.hfmtid = value;
        }

        /**
         * 取得 hretrn 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getHRETRN() {
            return hretrn;
        }

        /**
         * 設定 hretrn 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setHRETRN(String value) {
            this.hretrn = value;
        }

        /**
         * 取得 encoding 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEncoding() {
            return encoding;
        }

        /**
         * 設定 encoding 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEncoding(String value) {
            this.encoding = value;
        }

        /**
         * 取得 responseCode 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getResponseCode() {
            return responseCode;
        }

        /**
         * 設定 responseCode 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setResponseCode(String value) {
            this.responseCode = value;
        }

        /**
         * 取得 uuid 特性的值.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUUID() {
            return uuid;
        }

        /**
         * 設定 uuid 特性的值.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUUID(String value) {
            this.uuid = value;
        }

    }

}
