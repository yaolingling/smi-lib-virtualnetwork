/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StaticIpv4NetworkConfiguration", propOrder = { "gateway", "subnet", "primaryDns", "secondaryDns", "dnsSuffix", "ipRange" })
public class StaticIpv4NetworkConfiguration {

    @ApiModelProperty(position = 1, example = "172.31.143.254")
    @XmlElement(required = true)
    protected String gateway;

    @ApiModelProperty(position = 2, example = "255.255.240.0")
    @XmlElement(required = true)
    protected String subnet;

    @ApiModelProperty(position = 3, example = "172.31.143.250")
    @XmlElement(required = true)
    protected String primaryDns;

    @ApiModelProperty(position = 4, example = "172.31.143.251")
    protected String secondaryDns;

    @ApiModelProperty(position = 5, example = "customer.local")
    @XmlElement(required = true)
    protected String dnsSuffix;

    @ApiModelProperty(position = 6)
    @XmlElement(required = true)
    protected List<IpRange> ipRange;


    /**
     * Gets the value of the gateway property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getGateway() {
        return gateway;
    }


    /**
     * Sets the value of the gateway property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setGateway(String value) {
        this.gateway = value;
    }


    /**
     * Gets the value of the subnet property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getSubnet() {
        return subnet;
    }


    /**
     * Sets the value of the subnet property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setSubnet(String value) {
        this.subnet = value;
    }


    /**
     * Gets the value of the primaryDns property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getPrimaryDns() {
        return primaryDns;
    }


    /**
     * Sets the value of the primaryDns property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setPrimaryDns(String value) {
        this.primaryDns = value;
    }


    /**
     * Gets the value of the secondaryDns property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getSecondaryDns() {
        return secondaryDns;
    }


    /**
     * Sets the value of the secondaryDns property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setSecondaryDns(String value) {
        this.secondaryDns = value;
    }


    /**
     * Gets the value of the dnsSuffix property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDnsSuffix() {
        return dnsSuffix;
    }


    /**
     * Sets the value of the dnsSuffix property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDnsSuffix(String value) {
        this.dnsSuffix = value;
    }


    /**
     * Gets the value of the ipRange property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to the returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ipRange property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * 
     * <pre>
     * getIpRange().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link IpRange }
     *
     *
     */
    public List<IpRange> getIpRange() {
        if (ipRange == null) {
            ipRange = new ArrayList<>();
        }
        return this.ipRange;
    }

}
