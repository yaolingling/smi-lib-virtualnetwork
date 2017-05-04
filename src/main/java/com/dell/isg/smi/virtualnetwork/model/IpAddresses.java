/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IpAddresses", propOrder = { "id", "startingIp", "endingIp" })
public class IpAddresses {

    protected Long id;
    @XmlElement(required = true)
    protected String startingIp;
    @XmlElement(required = true)
    protected String endingIp;


    /**
     * Gets the value of the id property.
     *
     * @return possible object is {@link Long }
     *
     */
    public Long getId() {
        return id;
    }


    /**
     * Sets the value of the id property.
     *
     * @param value allowed object is {@link Long }
     *
     */
    public void setId(Long value) {
        this.id = value;
    }


    /**
     * Gets the value of the startingIp property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getStartingIp() {
        return startingIp;
    }


    /**
     * Sets the value of the startingIp property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setStartingIp(String value) {
        this.startingIp = value;
    }


    /**
     * Gets the value of the endingIp property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getEndingIp() {
        return endingIp;
    }


    /**
     * Sets the value of the endingIp property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setEndingIp(String value) {
        this.endingIp = value;
    }

}
