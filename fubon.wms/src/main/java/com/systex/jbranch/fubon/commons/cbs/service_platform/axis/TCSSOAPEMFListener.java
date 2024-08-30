/**
 * TCSSOAPEMFListener.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.systex.jbranch.fubon.commons.cbs.service_platform.axis;

public interface TCSSOAPEMFListener extends javax.xml.rpc.Service {
    public String getSOAPListenerAddress();

    public ReceiverSoap getSOAPListener() throws javax.xml.rpc.ServiceException;

    public ReceiverSoap getSOAPListener(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
