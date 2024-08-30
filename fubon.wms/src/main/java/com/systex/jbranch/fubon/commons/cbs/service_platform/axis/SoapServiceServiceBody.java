/**
 * SoapServiceServiceBody.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.systex.jbranch.fubon.commons.cbs.service_platform.axis;

public class SoapServiceServiceBody  implements java.io.Serializable {
    private String txnString;

    private String dataType;

    private Boolean shouldRender;

    public SoapServiceServiceBody() {
    }

    public SoapServiceServiceBody(
           String txnString,
           String dataType,
           Boolean shouldRender) {
           this.txnString = txnString;
           this.dataType = dataType;
           this.shouldRender = shouldRender;
    }


    /**
     * Gets the txnString value for this SoapServiceServiceBody.
     * 
     * @return txnString
     */
    public String getTxnString() {
        return txnString;
    }


    /**
     * Sets the txnString value for this SoapServiceServiceBody.
     * 
     * @param txnString
     */
    public void setTxnString(String txnString) {
        this.txnString = txnString;
    }


    /**
     * Gets the dataType value for this SoapServiceServiceBody.
     * 
     * @return dataType
     */
    public String getDataType() {
        return dataType;
    }


    /**
     * Sets the dataType value for this SoapServiceServiceBody.
     * 
     * @param dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }


    /**
     * Gets the shouldRender value for this SoapServiceServiceBody.
     * 
     * @return shouldRender
     */
    public Boolean getShouldRender() {
        return shouldRender;
    }


    /**
     * Sets the shouldRender value for this SoapServiceServiceBody.
     * 
     * @param shouldRender
     */
    public void setShouldRender(Boolean shouldRender) {
        this.shouldRender = shouldRender;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SoapServiceServiceBody)) return false;
        SoapServiceServiceBody other = (SoapServiceServiceBody) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.txnString==null && other.getTxnString()==null) || 
             (this.txnString!=null &&
              this.txnString.equals(other.getTxnString()))) &&
            ((this.dataType==null && other.getDataType()==null) || 
             (this.dataType!=null &&
              this.dataType.equals(other.getDataType()))) &&
            ((this.shouldRender==null && other.getShouldRender()==null) || 
             (this.shouldRender!=null &&
              this.shouldRender.equals(other.getShouldRender())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getTxnString() != null) {
            _hashCode += getTxnString().hashCode();
        }
        if (getDataType() != null) {
            _hashCode += getDataType().hashCode();
        }
        if (getShouldRender() != null) {
            _hashCode += getShouldRender().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SoapServiceServiceBody.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", ">>SoapService>ServiceBody"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("txnString");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", "TxnString"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dataType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", "DataType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shouldRender");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", "ShouldRender"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
