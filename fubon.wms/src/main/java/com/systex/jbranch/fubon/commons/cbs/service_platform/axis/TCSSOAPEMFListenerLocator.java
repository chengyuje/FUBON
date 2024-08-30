/**
 * TCSSOAPEMFListenerLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.systex.jbranch.fubon.commons.cbs.service_platform.axis;

public class TCSSOAPEMFListenerLocator extends org.apache.axis.client.Service implements TCSSOAPEMFListener {

    public TCSSOAPEMFListenerLocator() {
    }


    public TCSSOAPEMFListenerLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TCSSOAPEMFListenerLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SOAPListener
    private String SOAPListener_address = "http://localhost:9214/ListenerServices/SOAP/Listener/TCSSOAPEMFListener";

    public String getSOAPListenerAddress() {
        return SOAPListener_address;
    }

    // The WSDD service name defaults to the port name.
    private String SOAPListenerWSDDServiceName = "SOAPListener";

    public String getSOAPListenerWSDDServiceName() {
        return SOAPListenerWSDDServiceName;
    }

    public void setSOAPListenerWSDDServiceName(String name) {
        SOAPListenerWSDDServiceName = name;
    }

    public ReceiverSoap getSOAPListener() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SOAPListener_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSOAPListener(endpoint);
    }

    public ReceiverSoap getSOAPListener(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SOAPListenerBindingStub _stub = new SOAPListenerBindingStub(portAddress, this);
            _stub.setPortName(getSOAPListenerWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSOAPListenerEndpointAddress(String address) {
        SOAPListener_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ReceiverSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                SOAPListenerBindingStub _stub = new SOAPListenerBindingStub(new java.net.URL(SOAPListener_address), this);
                _stub.setPortName(getSOAPListenerWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("SOAPListener".equals(inputPortName)) {
            return getSOAPListener();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://fubon.org/OperationImpl/ListenerServices/SOAP/Listener", "TCSSOAPEMFListener");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://fubon.org/OperationImpl/ListenerServices/SOAP/Listener", "SOAPListener"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("SOAPListener".equals(portName)) {
            setSOAPListenerEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
