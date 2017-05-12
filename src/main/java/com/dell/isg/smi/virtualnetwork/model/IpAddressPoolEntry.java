/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * The Class IpAddressPoolEntry.
 */
@ApiModel
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IpAddressPoolEntry", propOrder = { "id", "ipAddress", "ipAddressState", "usageId", "expiryDate" })
@XmlSeeAlso({ ExportIpPoolData.class })
public class IpAddressPoolEntry {

    @ApiModelProperty(value = "identifier", dataType = "long", required = true)
    protected long id;

    @ApiModelProperty(value = "IP Address", dataType = "string", required = true)
    protected String ipAddress;

    @ApiModelProperty(value = "IP Address State", dataType = "string", required = true)
    @XmlElement(required = true)
    protected IpAddressState ipAddressState;

    @ApiModelProperty(value = "An identifier of the process or consumer for which the the addresses should be assigned", dataType = "string", required = false)
    protected String usageId;
    protected String expiryDate;


    /**
     * Gets the value of the id property.
     *
     * @return the id
     */
    public long getId() {
        return id;
    }


    /**
     * Sets the value of the id property.
     *
     * @param value the new id
     */
    public void setId(long value) {
        this.id = value;
    }


    /**
     * Gets the value of the ipAddress property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getIpAddress() {
        return ipAddress;
    }


    /**
     * Sets the value of the ipAddress property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setIpAddress(String value) {
        this.ipAddress = value;
    }


    /**
     * Gets the value of the ipAddressState property.
     *
     * @return possible object is {@link IpAddressState }
     *
     */
    public IpAddressState getIpAddressState() {
        return ipAddressState;
    }


    /**
     * Sets the value of the ipAddressState property.
     *
     * @param value allowed object is {@link IpAddressState }
     *
     */
    public void setIpAddressState(IpAddressState value) {
        this.ipAddressState = value;
    }


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
     * Gets the value of the expiryDate property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getExpiryDate() {
        return expiryDate;
    }


    /**
     * Sets the value of the expiryDate property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setExpiryDate(String value) {
        this.expiryDate = value;
    }

}
