/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Class Network.
 */
@ApiModel
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "id", "name", "description", "type", "vlanId", "_static", "staticIpv4NetworkConfiguration", "createdTime", "createdBy", "updatedTime", "updatedBy", "link" })
@XmlRootElement(name = "Network")
public class Network {

    @ApiModelProperty(hidden = true)
    protected long id;

    @ApiModelProperty(position = 2, example = "Network1")
    @XmlElement(required = true)
    protected String name;

    @ApiModelProperty(position = 3, example = "Private Network 1")
    protected String description;

    @ApiModelProperty(position = 4, example = "PRIVATE_LAN", allowableValues = "PUBLIC_LAN,PRIVATE_LAN,STORAGE_ISCSI_SAN,STORAGE_FCOE_SAN,OOB_OR_INFRASTRUCTURE_MANAGEMENT,HYPERVISOR_MANAGEMENT,HYPERVISOR_MIGRATION,HYPERVISOR_CLUSTER_PRIVATE,PXE,FILESHARE,FIP_SNOOPING,HARDWARE_MANAGEMENT")
    @XmlElement(required = true)
    protected NetworkType type;

    @ApiModelProperty(position = 5, example = "1")
    protected Integer vlanId;

    @ApiModelProperty(position = 6, example = "true")
    @XmlElement(name = "static")
    protected boolean _static;

    @ApiModelProperty(position = 7)
    protected StaticIpv4NetworkConfiguration staticIpv4NetworkConfiguration;

    @ApiModelProperty(hidden = true)
    protected String createdTime;

    @ApiModelProperty(hidden = true)
    protected String createdBy;

    @ApiModelProperty(hidden = true)
    protected String updatedTime;

    @ApiModelProperty(hidden = true)
    protected String updatedBy;

    @ApiModelProperty(hidden = true)
    protected Link link;


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
     * Gets the value of the name property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getName() {
        return name;
    }


    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }


    /**
     * Gets the value of the description property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }


    /**
     * Gets the value of the type property.
     *
     * @return possible object is {@link NetworkType }
     *
     */
    public NetworkType getType() {
        return type;
    }


    /**
     * Sets the value of the type property.
     *
     * @param value allowed object is {@link NetworkType }
     *
     */
    public void setType(NetworkType value) {
        this.type = value;
    }


    /**
     * Gets the value of the vlanId property.
     *
     * @return possible object is {@link Integer }
     *
     */
    public Integer getVlanId() {
        return vlanId;
    }


    /**
     * Sets the value of the vlanId property.
     *
     * @param value allowed object is {@link Integer }
     *
     */
    public void setVlanId(Integer value) {
        this.vlanId = value;
    }


    /**
     * Gets the value of the static property.
     *
     * @return true, if is static
     */
    public boolean isStatic() {
        return _static;
    }


    /**
     * Sets the value of the static property.
     *
     * @param value the new static
     */
    public void setStatic(boolean value) {
        this._static = value;
    }


    /**
     * Gets the value of the staticIpv4NetworkConfiguration property.
     *
     * @return possible object is {@link StaticIpv4NetworkConfiguration }
     *
     */
    public StaticIpv4NetworkConfiguration getStaticIpv4NetworkConfiguration() {
        return staticIpv4NetworkConfiguration;
    }


    /**
     * Sets the value of the staticIpv4NetworkConfiguration property.
     *
     * @param value allowed object is {@link StaticIpv4NetworkConfiguration }
     *
     */
    public void setStaticIpv4NetworkConfiguration(StaticIpv4NetworkConfiguration value) {
        this.staticIpv4NetworkConfiguration = value;
    }


    /**
     * Gets the value of the createdTime property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCreatedTime() {
        return createdTime;
    }


    /**
     * Sets the value of the createdTime property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCreatedTime(String value) {
        this.createdTime = value;
    }


    /**
     * Gets the value of the createdBy property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCreatedBy() {
        return createdBy;
    }


    /**
     * Sets the value of the createdBy property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCreatedBy(String value) {
        this.createdBy = value;
    }


    /**
     * Gets the value of the updatedTime property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getUpdatedTime() {
        return updatedTime;
    }


    /**
     * Sets the value of the updatedTime property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setUpdatedTime(String value) {
        this.updatedTime = value;
    }


    /**
     * Gets the value of the updatedBy property.
     *
     * @return possible object is {@link String }
     *
     */
    public String getUpdatedBy() {
        return updatedBy;
    }


    /**
     * Sets the value of the updatedBy property.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setUpdatedBy(String value) {
        this.updatedBy = value;
    }


    /**
     * Gets the value of the link property.
     *
     * @return possible object is {@link Link }
     *
     */
    public Link getLink() {
        return link;
    }


    /**
     * Sets the value of the link property.
     *
     * @param value allowed object is {@link Link }
     *
     */
    public void setLink(Link value) {
        this.link = value;
    }

}
