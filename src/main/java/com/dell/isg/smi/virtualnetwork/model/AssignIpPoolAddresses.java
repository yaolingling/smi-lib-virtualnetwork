/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * The Class AssignIpPoolAddresses.
 */
@ApiModel(value = "AssignIpPoolAddresses", description = "A data transfer object for containing the usageid and a list of ip addresses to be assigned")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "usageId", "ipAddresses" })
@XmlRootElement(name = "AssignIpPoolAddresses")
public class AssignIpPoolAddresses {

    @ApiModelProperty(example = "A1B2C3D")
    protected String usageId;

    @XmlList
    protected List<String> ipAddresses;


    /**
     * Gets the value of the usageId property.
     *
     * @return the usage id
     */
    public String getUsageId() {
        return usageId;
    }


    /**
     * Sets the value of the usageId property.
     *
     * @param value the new usage id
     */
    public void setUsageId(String value) {
        this.usageId = value;
    }


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
