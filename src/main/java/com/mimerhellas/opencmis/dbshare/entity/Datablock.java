/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimerhellas.opencmis.dbshare.entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kostas
 */
@Entity
@Table(name = "datablock")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Datablock.findAll", query = "SELECT d FROM Datablock d"),
    @NamedQuery(name = "Datablock.findByFsblockid", query = "SELECT d FROM Datablock d WHERE d.fsblockid = :fsblockid"),
    @NamedQuery(name = "Datablock.findByDid", query = "SELECT d FROM Datablock d WHERE d.did = :did"),
    @NamedQuery(name = "Datablock.findByDfiletype", query = "SELECT d FROM Datablock d WHERE d.dfiletype = :dfiletype"),
    @NamedQuery(name = "Datablock.findByDtextdata", query = "SELECT d FROM Datablock d WHERE d.dtextdata = :dtextdata")})
public class Datablock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(generator = "DatablockIdGenerator", strategy = GenerationType.TABLE)
    @TableGenerator(
            name = "DatablockIdGenerator",
            table = "id_gen",
            pkColumnName = "id_name",
            valueColumnName = "id_val",
            pkColumnValue = "datablock",
            initialValue = 0,
            allocationSize = 1)
    @Column(name = "did")
    private Integer did;
    @Column(name = "dfiletype")
    private Integer dfiletype;
    @Column(name = "dtextdata")
    private String dtextdata;
    @Lob
    @Column(name = "drawbindata")
    private byte[] drawbindata;
    private Long fsblockid;

    public Datablock() {
    }

    public Datablock(Integer did) {
        this.did = did;
    }

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }

    public Integer getDfiletype() {
        return dfiletype;
    }

    public void setDfiletype(Integer dfiletype) {
        this.dfiletype = dfiletype;
    }

    public String getDtextdata() {
        return dtextdata;
    }

    public void setDtextdata(String dtextdata) {
        this.dtextdata = dtextdata;
    }

    public byte[] getDrawbindata() {
        return drawbindata;
    }

    public void setDrawbindata(byte[] drawbindata) {
        this.drawbindata = drawbindata;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (did != null ? did.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Datablock)) {
            return false;
        }
        Datablock other = (Datablock) object;
        if ((this.did == null && other.did != null) || (this.did != null && !this.did.equals(other.did))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mimerhellas.opencmis.dbshare.entity.Datablock[ did=" + did + " ]";
    }

    /**
     * @return the fsblockid
     */
    public Long getFsblockid() {
        return fsblockid;
    }

    /**
     * @param fsblockid the fsblockid to set
     */
    public void setFsblockid(Long fsblockid) {
        this.fsblockid = fsblockid;
    }
    
}
