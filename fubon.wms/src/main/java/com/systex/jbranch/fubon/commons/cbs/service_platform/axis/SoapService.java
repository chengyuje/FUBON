/**
 * SoapService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.systex.jbranch.fubon.commons.cbs.service_platform.axis;

public class SoapService  implements java.io.Serializable {
    private SoapServiceServiceHeader serviceHeader;

    private SoapServiceServiceBody serviceBody;

    private SoapServiceServiceError serviceError;

    public SoapService() {
    }

    public SoapService(
           SoapServiceServiceHeader serviceHeader,
           SoapServiceServiceBody serviceBody,
           SoapServiceServiceError serviceError) {
           this.serviceHeader = serviceHeader;
           this.serviceBody = serviceBody;
           this.serviceError = serviceError;
    }


    /**
     * Gets the serviceHeader value for this SoapService.
     * 
     * @return serviceHeader
     */
    public SoapServiceServiceHeader getServiceHeader() {
        return serviceHeader;
    }


    /**
     * Sets the serviceHeader value for this SoapService.
     * 
     * @param serviceHeader
     */
    public void setServiceHeader(SoapServiceServiceHeader serviceHeader) {
        this.serviceHeader = serviceHeader;
    }


    /**
     * Gets the serviceBody value for this SoapService.
     * 
     * @return serviceBody
     */
    public SoapServiceServiceBody getServiceBody() {
        return serviceBody;
    }


    /**
     * Sets the serviceBody value for this SoapService.
     * 
     * @param serviceBody
     */
    public void setServiceBody(SoapServiceServiceBody serviceBody) {
        this.serviceBody = serviceBody;
    }


    /**
     * Gets the serviceError value for this SoapService.
     * 
     * @return serviceError
     */
    public SoapServiceServiceError getServiceError() {
        return serviceError;
    }


    /**
     * Sets the serviceError value for this SoapService.
     * 
     * @param serviceError
     */
    public void setServiceError(SoapServiceServiceError serviceError) {
        this.serviceError = serviceError;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SoapService)) return false;
        SoapService other = (SoapService) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.serviceHeader==null && other.getServiceHeader()==null) || 
             (this.serviceHeader!=null &&
              this.serviceHeader.equals(other.getServiceHeader()))) &&
            ((this.serviceBody==null && other.getServiceBody()==null) || 
             (this.serviceBody!=null &&
              this.serviceBody.equals(other.getServiceBody()))) &&
            ((this.serviceError==null && other.getServiceError()==null) || 
             (this.serviceError!=null &&
              this.serviceError.equals(other.getServiceError())));
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
        if (getServiceHeader() != null) {
            _hashCode += getServiceHeader().hashCode();
        }
        if (getServiceBody() != null) {
            _hashCode += getServiceBody().hashCode();
        }
        if (getServiceError() != null) {
            _hashCode += getServiceError().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SoapService.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", ">SoapService"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", "ServiceHeader"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", ">>SoapService>ServiceHeader"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceBody");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", "ServiceBody"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", ">>SoapService>ServiceBody"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("serviceError");
        elemField.setXmlName(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", "ServiceError"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://fubon.com.tw/XSD/TCSSoapService", ">>SoapService>ServiceError"));
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
