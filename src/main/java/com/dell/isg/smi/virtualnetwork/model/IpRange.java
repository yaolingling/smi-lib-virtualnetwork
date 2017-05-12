/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Class IpRange.
 */
@ApiModel
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IpRange", propOrder = { "id", "startingIp", "endingIp", "ipAddressesInUse", "ipAddressesFree" })
public class IpRange {

    @ApiModelProperty(hidden = true)
    protected Long id;

    @ApiModelProperty(position = 1, example = "172.31.142.1")
    @XmlElement(required = true)
    protected String startingIp;

    @ApiModelProperty(position = 2, example = "172.31.142.254")
    @XmlElement(required = true)
    protected String endingIp;

    @ApiModelProperty(hidden = true)
    protected int ipAddressesInUse;

    @ApiModelProperty(hidden = true)
    protected int ipAddressesFree;


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


    /**
     * Gets the value of the ipAddressesInUse property.
     *
     * @return the ip addresses in use
     */
    public int getIpAddressesInUse() {
        return ipAddressesInUse;
    }


    /**
     * Sets the value of the ipAddressesInUse property.
     *
     * @param value the new ip addresses in use
     */
    public void setIpAddressesInUse(int value) {
        this.ipAddressesInUse = value;
    }


    /**
     * Gets the value of the ipAddressesFree property.
     *
     * @return the ip addresses free
     */
    public int getIpAddressesFree() {
        return ipAddressesFree;
    }


    /**
     * Sets the value of the ipAddressesFree property.
     *
     * @param value the new ip addresses free
     */
    public void setIpAddressesFree(int value) {
        this.ipAddressesFree = value;
    }

}
