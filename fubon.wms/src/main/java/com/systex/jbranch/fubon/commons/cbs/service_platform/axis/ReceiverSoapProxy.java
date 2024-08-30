package com.systex.jbranch.fubon.commons.cbs.service_platform.axis;

public class ReceiverSoapProxy implements ReceiverSoap {
  private String _endpoint = null;
  private ReceiverSoap receiverSoap = null;
  
  public ReceiverSoapProxy() {
    _initReceiverSoapProxy();
  }
  
  public ReceiverSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initReceiverSoapProxy();
  }
  
  private void _initReceiverSoapProxy() {
    try {
      receiverSoap = (new TCSSOAPEMFListenerLocator()).getSOAPListener();
      if (receiverSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)receiverSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)receiverSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (receiverSoap != null)
      ((javax.xml.rpc.Stub)receiverSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ReceiverSoap getReceiverSoap() {
    if (receiverSoap == null)
      _initReceiverSoapProxy();
    return receiverSoap;
  }
  
  public void operation(SoapServiceHolder parameters) throws java.rmi.RemoteException{
    if (receiverSoap == null)
      _initReceiverSoapProxy();
    receiverSoap.operation(parameters);
  }
}