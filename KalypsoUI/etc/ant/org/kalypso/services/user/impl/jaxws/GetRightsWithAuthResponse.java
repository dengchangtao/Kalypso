
package org.kalypso.services.user.impl.jaxws;

import javax.xml.bind.annotation.AccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.kalypso.services.user.impl.jaxws.GetRightsWithAuthResponse;

@XmlRootElement(name = "getRightsWithAuthResponse", namespace = "http://impl.user.services.kalypso.org/")
@XmlAccessorType(AccessType.FIELD)
@XmlType(name = "getRightsWithAuthResponse", namespace = "http://impl.user.services.kalypso.org/")
public class GetRightsWithAuthResponse {

    @XmlElement(name = "return", namespace = "")
    private String[] _return;

    /**
     * 
     * @return
     *     returns String[]
     */
    public String[] get_return() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void set_return(String[] _return) {
        this._return = _return;
    }

}
