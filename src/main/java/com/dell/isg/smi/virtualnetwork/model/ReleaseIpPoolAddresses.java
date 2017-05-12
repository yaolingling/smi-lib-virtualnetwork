/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Class ReleaseIpPoolAddresses.
 */
@ApiModel
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "ipAddresses" })
@XmlRootElement(name = "ReleaseIpPoolAddresses")
public class ReleaseIpPoolAddresses {

    @XmlList
    protected List<String> ipAddresses;


    /**
     * Gets the value of the ipAddresses property.
     * 
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ipAddresses property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getIpAddresses().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list {@link String }
     *
     * @return the ip addresses
     */
    public List<String> getIpAddresses() {
        if (ipAddresses == null) {
            ipAddresses = new ArrayList<>();
        }
        return this.ipAddresses;
    }

}
