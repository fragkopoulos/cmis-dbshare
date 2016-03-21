/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimerhellas.opencmis.dbshare.controller;

//import com.eaio.uuid.UUID;
import com.mimerhellas.opencmis.dbshare.controller.exceptions.NonexistentEntityException;
import com.mimerhellas.opencmis.dbshare.controller.exceptions.RollbackFailureException;
import com.mimerhellas.opencmis.dbshare.entity.Datablock;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import com.mimerhellas.opencmis.dbshare.entity.Fsblock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

/**
 *
 * @author kostas
 */
public class FsblockJpaController implements Serializable {

    private EntityManagerFactory emf;

    public FsblockJpaController() {
        emf = Persistence.createEntityManagerFactory("NewRSPU");
    }

    public FsblockJpaController(UserTransaction utx, EntityManagerFactory emf) {
        //this.utx = utx;
        this.emf = emf;
    }
    //private UserTransaction utx = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    private String generatePath(Fsblock entity) {
        String retvalue = "";
        while (entity.getFsparent() != null) {
            retvalue = "/" + entity.getFsname() + retvalue;
            entity = entity.getFsparent();
        }
        return retvalue;
    }

    public Fsblock createFolder(Fsblock parent, String name) throws RollbackFailureException, Exception {
        Fsblock newFsblock = new Fsblock();
        newFsblock.setFstype(0);
        newFsblock.setFsparent(parent);
        newFsblock.setFsname(name);
        newFsblock.setFsfilesize(new Long(0));
        return create(newFsblock);
    }

    public Fsblock createDocument(Fsblock parent, String name) throws RollbackFailureException, Exception {
        Fsblock newFsblock = new Fsblock();
        newFsblock.setFstype(1);
        newFsblock.setFsparent(parent);
        newFsblock.setFsname(name);
        newFsblock.setFsfilesize(new Long(0));
        return create(newFsblock);
    }

    public Fsblock create(Fsblock fsblock) throws RollbackFailureException, Exception {

        fsblock.setFscreatetime(new Date().getTime());
        fsblock.setFslastmodtime(fsblock.getFscreatetime());
        fsblock.setFsuuid(UUID.randomUUID().toString());
        fsblock.setFspath(generatePath(fsblock));

        if (fsblock.getFsblockList() == null) {
            fsblock.setFsblockList(new ArrayList<Fsblock>());
        }
        EntityManager em = null;
        try {
            //utx.begin();
            em = getEntityManager();
            em.getTransaction().begin();
            Fsblock fsparent = fsblock.getFsparent();
            if (fsparent != null) {
                fsparent = em.getReference(fsparent.getClass(), fsparent.getFsid());
                fsblock.setFsparent(fsparent);
            }
            List<Fsblock> attachedFsblockList = new ArrayList<Fsblock>();
            for (Fsblock fsblockListFsblockToAttach : fsblock.getFsblockList()) {
                fsblockListFsblockToAttach = em.getReference(fsblockListFsblockToAttach.getClass(), fsblockListFsblockToAttach.getFsid());
                attachedFsblockList.add(fsblockListFsblockToAttach);
            }
            fsblock.setFsblockList(attachedFsblockList);
            em.persist(fsblock);
            if (fsparent != null) {
                fsparent.getFsblockList().add(fsblock);
                fsparent = em.merge(fsparent);
            }
            for (Fsblock fsblockListFsblock : fsblock.getFsblockList()) {
                Fsblock oldFsparentOfFsblockListFsblock = fsblockListFsblock.getFsparent();
                fsblockListFsblock.setFsparent(fsblock);
                fsblockListFsblock = em.merge(fsblockListFsblock);
                if (oldFsparentOfFsblockListFsblock != null) {
                    oldFsparentOfFsblockListFsblock.getFsblockList().remove(fsblockListFsblock);
                    oldFsparentOfFsblockListFsblock = em.merge(oldFsparentOfFsblockListFsblock);
                }
            }
            //utx.commit();
            em.getTransaction().commit();
            return fsblock;
        } catch (Exception ex) {
            try {
                //utx.rollback();
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public Fsblock edit(Fsblock fsblock) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            fsblock.setFslastmodtime(new Date().getTime());
            fsblock.setFspath(generatePath(fsblock));

            //utx.begin();
            em = getEntityManager();
            em.getTransaction().begin();
            Fsblock persistentFsblock = em.find(Fsblock.class, fsblock.getFsid());
            Fsblock fsparentOld = persistentFsblock.getFsparent();
            Fsblock fsparentNew = fsblock.getFsparent();
            List<Fsblock> fsblockListOld = persistentFsblock.getFsblockList();
            List<Fsblock> fsblockListNew = fsblock.getFsblockList();
            if (fsparentNew != null) {
                fsparentNew = em.getReference(fsparentNew.getClass(), fsparentNew.getFsid());
                fsblock.setFsparent(fsparentNew);
            }
            List<Fsblock> attachedFsblockListNew = new ArrayList<Fsblock>();
            for (Fsblock fsblockListNewFsblockToAttach : fsblockListNew) {
                fsblockListNewFsblockToAttach = em.getReference(fsblockListNewFsblockToAttach.getClass(), fsblockListNewFsblockToAttach.getFsid());
                attachedFsblockListNew.add(fsblockListNewFsblockToAttach);
            }
            fsblockListNew = attachedFsblockListNew;
            fsblock.setFsblockList(fsblockListNew);
            fsblock = em.merge(fsblock);
            if (fsparentOld != null && !fsparentOld.equals(fsparentNew)) {
                fsparentOld.getFsblockList().remove(fsblock);
                fsparentOld = em.merge(fsparentOld);
            }
            if (fsparentNew != null && !fsparentNew.equals(fsparentOld)) {
                fsparentNew.getFsblockList().add(fsblock);
                fsparentNew = em.merge(fsparentNew);
            }
            for (Fsblock fsblockListOldFsblock : fsblockListOld) {
                if (!fsblockListNew.contains(fsblockListOldFsblock)) {
                    fsblockListOldFsblock.setFsparent(null);
                    fsblockListOldFsblock = em.merge(fsblockListOldFsblock);
                }
            }
            for (Fsblock fsblockListNewFsblock : fsblockListNew) {
                if (!fsblockListOld.contains(fsblockListNewFsblock)) {
                    Fsblock oldFsparentOfFsblockListNewFsblock = fsblockListNewFsblock.getFsparent();
                    fsblockListNewFsblock.setFsparent(fsblock);
                    fsblockListNewFsblock = em.merge(fsblockListNewFsblock);
                    if (oldFsparentOfFsblockListNewFsblock != null && !oldFsparentOfFsblockListNewFsblock.equals(fsblock)) {
                        oldFsparentOfFsblockListNewFsblock.getFsblockList().remove(fsblockListNewFsblock);
                        oldFsparentOfFsblockListNewFsblock = em.merge(oldFsparentOfFsblockListNewFsblock);
                    }
                }
            }
            //utx.commit();
            em.getTransaction().commit();
            return fsblock;
        } catch (Exception ex) {
            try {
                //utx.rollback();
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = fsblock.getFsid();
                if (findFsblock(id) == null) {
                    throw new NonexistentEntityException("The fsblock with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            //utx.begin();
            em = getEntityManager();
            em.getTransaction().begin();
            Fsblock fsblock;
            try {
                fsblock = em.getReference(Fsblock.class, id);
                fsblock.getFsid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The fsblock with id " + id + " no longer exists.", enfe);
            }
            Fsblock fsparent = fsblock.getFsparent();
            if (fsparent != null) {
                fsparent.getFsblockList().remove(fsblock);
                fsparent = em.merge(fsparent);
            }
            List<Fsblock> fsblockList = fsblock.getFsblockList();
            for (Fsblock fsblockListFsblock : fsblockList) {
                fsblockListFsblock.setFsparent(null);
                fsblockListFsblock = em.merge(fsblockListFsblock);
            }
            em.remove(fsblock);

            Query q = em.createNamedQuery("Datablock.findByFsblockid");
            q.setParameter("fsblockid", fsblock.getFsid());
            List<Datablock> datablocklist = q.getResultList();
            for (Datablock datablock : datablocklist) {
                em.remove(datablock);
            }
            //utx.commit();
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                //utx.rollback();
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Fsblock> findFsblockEntities() {
        return findFsblockEntities(true, -1, -1);
    }

    public List<Fsblock> findFsblockEntities(int maxResults, int firstResult) {
        return findFsblockEntities(false, maxResults, firstResult);
    }

    private List<Fsblock> findFsblockEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Fsblock as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Fsblock findFsblock(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Fsblock.class, id);
        } finally {
            em.close();
        }
    }

    public int getFsblockCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Fsblock as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Fsblock findByPath(String path) {
        EntityManager em = getEntityManager();
        try {
            if ((path.length() > 1) && (path.endsWith("/"))) {
                path = path.substring(0, path.length() - 1);
            }
            Query q = getEntityManager().createNamedQuery("Fsblock.findByFspath");
            q.setParameter("fspath", path);
            List<Fsblock> res = q.getResultList();
            if (!res.isEmpty()) {
                return res.get(0);
            } else {
                return null;
            }
        } finally {
            em.close();
        }
    }

    public Fsblock findByUUID(String uuid) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Fsblock.findByFsuuid");
            q.setParameter("fsuuid", uuid);
            List<Fsblock> res = q.getResultList();
            if (!res.isEmpty()) {
                return res.get(0);
            } else {
                return null;
            }
        } finally {
            em.close();
        }
    }

    public Fsblock findByNameParent(String name, Fsblock parentId) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Fsblock.findByFsnameFsparent");
            q.setParameter("fsname", name);
            q.setParameter("fsparent", parentId);
            List<Fsblock> res = q.getResultList();
            if (!res.isEmpty()) {
                return res.get(0);
            } else {
                return null;
            }
        } finally {
            em.close();
        }
    }

}
