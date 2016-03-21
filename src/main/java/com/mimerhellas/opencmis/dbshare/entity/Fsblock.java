/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimerhellas.opencmis.dbshare.entity;

import com.mimerhellas.opencmis.dbshare.entity.listener.FsblockListener;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kostas
 */
@Entity
@Table(name = "fsblock")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Fsblock.findAll", query = "SELECT f FROM Fsblock f"),
    @NamedQuery(name = "Fsblock.findByFsid", query = "SELECT f FROM Fsblock f WHERE f.fsid = :fsid"),
    @NamedQuery(name = "Fsblock.findByFstype", query = "SELECT f FROM Fsblock f WHERE f.fstype = :fstype"),
    @NamedQuery(name = "Fsblock.findByFscreatetime", query = "SELECT f FROM Fsblock f WHERE f.fscreatetime = :fscreatetime"),
    @NamedQuery(name = "Fsblock.findByFslastmodtime", query = "SELECT f FROM Fsblock f WHERE f.fslastmodtime = :fslastmodtime"),
    @NamedQuery(name = "Fsblock.findByFsfilesize", query = "SELECT f FROM Fsblock f WHERE f.fsfilesize = :fsfilesize"),
    @NamedQuery(name = "Fsblock.findByFsname", query = "SELECT f FROM Fsblock f WHERE f.fsname = :fsname"),
    @NamedQuery(name = "Fsblock.findByFspath", query = "SELECT f FROM Fsblock f WHERE f.fspath = :fspath"),
    @NamedQuery(name = "Fsblock.findByFsuuid", query = "SELECT f FROM Fsblock f WHERE f.fsuuid = :fsuuid"),
    @NamedQuery(name = "Fsblock.findByFsparent", query = "SELECT f FROM Fsblock f WHERE f.fsparent = :fsparent"),
    @NamedQuery(name = "Fsblock.findByFsnameFsparent", query = "SELECT f FROM Fsblock f WHERE f.fsparent = :fsparent AND f.fsname = :fsname")
})
@EntityListeners({FsblockListener.class})
public class Fsblock implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @GeneratedValue(generator = "FsblockIdGenerator", strategy = GenerationType.TABLE)
    @TableGenerator(
            name = "FsblockIdGenerator",
            table = "id_gen",
            pkColumnName = "id_name",
            valueColumnName = "id_val",
            pkColumnValue = "fsblock",
            initialValue = 0,
            allocationSize = 1)
    @Column(name = "fsid", nullable = false)
    private Long fsid;
    @Column(name = "fstype")
    private Integer fstype;
    @Column(name = "fscreatetime")
    private Long fscreatetime;
    @Column(name = "fslastmodtime")
    private Long fslastmodtime;
    @Column(name = "fsfilesize")
    private Long fsfilesize;
    @Column(name = "fsname")
    private String fsname;

    @Lob
    @Column(name = "fschild")
    private byte[] fschild;

    @Column(name = "fspath")
    private String fspath;

    @Column(name = "fsuuid")
    private String fsuuid;

    @OneToMany(mappedBy = "fsparent", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fsblock> fsblockList;
    @JoinColumn(name = "fsparent", referencedColumnName = "fsid")
    @ManyToOne(fetch = FetchType.LAZY)
    private Fsblock fsparent;

    public Fsblock() {
    }

    public Fsblock(Long fsid) {
        this.fsid = fsid;
    }

    public Long getFsid() {
        return fsid;
    }

    public void setFsid(Long fsid) {
        this.fsid = fsid;
    }

    public Integer getFstype() {
        return fstype;
    }

    public void setFstype(Integer fstype) {
        this.fstype = fstype;
    }

    public Long getFscreatetime() {
        return fscreatetime;
    }

    public void setFscreatetime(Long fscreatetime) {
        this.fscreatetime = fscreatetime;
    }

    public Long getFslastmodtime() {
        return fslastmodtime;
    }

    public void setFslastmodtime(Long fslastmodtime) {
        this.fslastmodtime = fslastmodtime;
    }

    public Long getFsfilesize() {
        return fsfilesize;
    }

    public void setFsfilesize(Long fsfilesize) {
        this.fsfilesize = fsfilesize;
    }

    public String getFsname() {
        return fsname;
    }

    public void setFsname(String fsname) {
        this.fsname = fsname;
    }

    public byte[] getFschild() {
        return fschild;
    }

    public void setFschild(byte[] fschild) {
        this.fschild = fschild;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fsid != null ? fsid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Fsblock)) {
            return false;
        }
        Fsblock other = (Fsblock) object;
        if ((this.fsid == null && other.fsid != null) || (this.fsid != null && !this.fsid.equals(other.fsid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mimerhellas.opencmis.dbshare.entity.Fsblock[ fsid=" + fsid + " ]";
    }

    /**
     * @return the fspath
     */
    public String getFspath() {
        return fspath;
    }

    /**
     * @param fspath the fspath to set
     */
    public void setFspath(String fspath) {
        this.fspath = fspath;
    }

    public boolean isDirectory() {
        return getFstype() == 0;
    }

    public boolean isFile() {
        return getFstype() == 1;
    }

    public String getAbsolutePath() {
        return getFspath();
    }

    /**
     * @return the fsuuid
     */
    public String getFsuuid() {
        return fsuuid;
    }

    /**
     * @param fsuuid the fsuuid to set
     */
    public void setFsuuid(String fsuuid) {
        this.fsuuid = fsuuid;
    }

    public List<Fsblock> getFsblockList() {
        return fsblockList;
    }

    public void setFsblockList(List<Fsblock> fsblockList) {
        this.fsblockList = fsblockList;
    }

    public Fsblock getFsparent() {
        return fsparent;
    }

    public void setFsparent(Fsblock fsparent) {
        this.fsparent = fsparent;
    }
}
