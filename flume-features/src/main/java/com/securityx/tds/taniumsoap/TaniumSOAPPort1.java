
package com.securityx.tds.taniumsoap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "TaniumSOAPPort1", targetNamespace = "urn:TaniumSOAP")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TaniumSOAPPort1 {


    /**
     * 
     * @param taniumSoapRequest
     * @return
     *     returns taniumsoap.TaniumSOAPResult
     */
    @WebMethod(operationName = "Request", action = "urn:TaniumSOAPAction")
    @WebResult(targetNamespace = "urn:TaniumSOAP", partName = "return")
    public TaniumSOAPResult request(
        @WebParam(name = "tanium_soap_request", targetNamespace = "urn:TaniumSOAP", partName = "tanium_soap_request")
        TaniumSOAPRequest taniumSoapRequest);

}
