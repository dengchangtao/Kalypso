//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.10 at 05:19:05 PM CEST 
//


package de.tu_harburg.wb.kalypso.rrm.kalininmiljukov;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.tu_harburg.wb.kalypso.rrm.kalininmiljukov package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _KalininMiljukovGroup_QNAME = new QName("http://www.kalypso.wb.tu-harburg.de/rrm/kalininmiljukov", "KalininMiljukovGroup");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.tu_harburg.wb.kalypso.rrm.kalininmiljukov
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link KalininMiljukovType.Profile }
     * 
     */
    public KalininMiljukovType.Profile createKalininMiljukovTypeProfile() {
        return new KalininMiljukovType.Profile();
    }

    /**
     * Create an instance of {@link KalininMiljukovGroupType }
     * 
     */
    public KalininMiljukovGroupType createKalininMiljukovGroupType() {
        return new KalininMiljukovGroupType();
    }

    /**
     * Create an instance of {@link KalininMiljukovType }
     * 
     */
    public KalininMiljukovType createKalininMiljukovType() {
        return new KalininMiljukovType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KalininMiljukovGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.kalypso.wb.tu-harburg.de/rrm/kalininmiljukov", name = "KalininMiljukovGroup")
    public JAXBElement<KalininMiljukovGroupType> createKalininMiljukovGroup(KalininMiljukovGroupType value) {
        return new JAXBElement<KalininMiljukovGroupType>(_KalininMiljukovGroup_QNAME, KalininMiljukovGroupType.class, null, value);
    }

}
