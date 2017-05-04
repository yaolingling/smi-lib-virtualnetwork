/**
 * Copyright © 2017 DELL Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.isg.smi.virtualnetwork.entity;

import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.BASE_ENTITY_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_DESCRIPTION;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_DNS_SUFFIX;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_GATE_WAY;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_IS_STATIC;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_NAME;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_PRIMARY_DNS;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_SECONDARY_DNS;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_SUBNET;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_TYPE;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NC_VLAN_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NETWORK_CONFIGURATION;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.NETWORK_CONFIGURATION_ID;
import static com.dell.isg.smi.virtualnetwork.configuration.NetworkTableConstants.T_NETWORK_CONFIGURATION;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = T_NETWORK_CONFIGURATION)
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = NETWORK_CONFIGURATION_ID, referencedColumnName = BASE_ENTITY_ID)
public class NetworkConfiguration extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7034944160046001060L;

    @Column(name = NC_VLAN_ID)
    private Integer vlanId;

    @Column(name = NC_IS_STATIC)
    private boolean isStatic;

    @Column(name = NC_NAME)
    private String name;

    @Column(name = NC_TYPE)
    private String type;

    @Column(name = NC_GATE_WAY)
    private String gateway;

    @Column(name = NC_SUBNET)
    private String subnet;

    @Column(name = NC_PRIMARY_DNS)
    private String primaryDns;

    @Column(name = NC_SECONDARY_DNS)
    private String secondaryDns;

    @Column(name = NC_DNS_SUFFIX)
    private String dnsSuffix;

    @Column(name = NC_DESCRIPTION)
    private String description;

    @OneToMany(mappedBy = NETWORK_CONFIGURATION, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = NETWORK_CONFIGURATION_ID)
    @Fetch(FetchMode.SELECT)
    private Set<IpAddressRange> ipAddressRanges;


    /**
     * @return the vlanId
     */
    public Integer getVlanId() {
        return vlanId;
    }


    /**
     * @param vlanId the vlanId to set
     */
    public void setVlanId(Integer vlanId) {
        this.vlanId = vlanId;
    }


    /**
     * @return the isStatic
     */
    public boolean isStatic() {
        return isStatic;
    }


    /**
     * @param isStatic the isStatic to set
     */
    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * @return the type
     */
    public String getType() {
        return type;
    }


    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * @return the gateway
     */
    public String getGateway() {
        return gateway;
    }


    /**
     * @param gateway the gateway to set
     */
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }


    /**
     * @return the subnet
     */
    public String getSubnet() {
        return subnet;
    }


    /**
     * @param subnet the subnet to set
     */
    public void setSubnet(String subnet) {
        this.subnet = subnet;
    }


    /**
     * @return the primaryDns
     */
    public String getPrimaryDns() {
        return primaryDns;
    }


    /**
     * @param primaryDns the primaryDns to set
     */
    public void setPrimaryDns(String primaryDns) {
        this.primaryDns = primaryDns;
    }


    /**
     * @return the secondaryDns
     */
    public String getSecondaryDns() {
        return secondaryDns;
    }


    /**
     * @param secondaryDns the secondaryDns to set
     */
    public void setSecondaryDns(String secondaryDns) {
        this.secondaryDns = secondaryDns;
    }


    /**
     * @return the dnsSuffix
     */
    public String getDnsSuffix() {
        return dnsSuffix;
    }


    /**
     * @param dnsSuffix the dnsSuffix to set
     */
    public void setDnsSuffix(String dnsSuffix) {
        this.dnsSuffix = dnsSuffix;
    }


    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }


    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return the ipAddressRanges
     */
    public Set<IpAddressRange> getIpAddressRanges() {
        return ipAddressRanges;
    }


    /**
     * @param ipAddressRanges the ipAddressRanges to set
     */
    public void setIpAddressRanges(Set<IpAddressRange> ipAddressRanges) {
        this.ipAddressRanges = ipAddressRanges;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dnsSuffix == null) ? 0 : dnsSuffix.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((gateway == null) ? 0 : gateway.hashCode());
        result = prime * result + ((ipAddressRanges == null) ? 0 : ipAddressRanges.hashCode());
        result = prime * result + (isStatic ? 1231 : 1237);
        result = prime * result + ((primaryDns == null) ? 0 : primaryDns.hashCode());
        result = prime * result + ((secondaryDns == null) ? 0 : secondaryDns.hashCode());
        result = prime * result + ((subnet == null) ? 0 : subnet.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((vlanId == null) ? 0 : vlanId.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NetworkConfiguration other = (NetworkConfiguration) obj;
        if (dnsSuffix == null) {
            if (other.dnsSuffix != null)
                return false;
        } else if (!dnsSuffix.equals(other.dnsSuffix))
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (gateway == null) {
            if (other.gateway != null)
                return false;
        } else if (!gateway.equals(other.gateway))
            return false;
        if (ipAddressRanges == null) {
            if (other.ipAddressRanges != null)
                return false;
        } else if (!ipAddressRanges.equals(other.ipAddressRanges))
            return false;
        if (isStatic != other.isStatic)
            return false;
        if (primaryDns == null) {
            if (other.primaryDns != null)
                return false;
        } else if (!primaryDns.equals(other.primaryDns))
            return false;
        if (secondaryDns == null) {
            if (other.secondaryDns != null)
                return false;
        } else if (!secondaryDns.equals(other.secondaryDns))
            return false;
        if (subnet == null) {
            if (other.subnet != null)
                return false;
        } else if (!subnet.equals(other.subnet))
            return false;

        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (vlanId == null) {
            if (other.vlanId != null)
                return false;
        } else if (!vlanId.equals(other.vlanId))
            return false;
        return true;
    }

}
