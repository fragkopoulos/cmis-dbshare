/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimerhellas.opencmis.dbshare.controller;

import com.mimerhellas.opencmis.dbshare.controller.exceptions.NonexistentEntityException;
import com.mimerhellas.opencmis.dbshare.controller.exceptions.PreexistingEntityException;
import com.mimerhellas.opencmis.dbshare.controller.exceptions.RollbackFailureException;
import com.mimerhellas.opencmis.dbshare.entity.Datablock;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

/**
 *
 * @author kostas
 */
public class DatablockJpaController implements Serializable {

    private EntityManagerFactory emf;

    public DatablockJpaController() {
        emf = Persistence.createEntityManagerFactory("NewRSPU");
    }

    public DatablockJpaController(UserTransaction utx, EntityManagerFactory emf) {
        //this.utx = utx;
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Datablock datablock) throws PreexistingEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            //utx.begin();
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(datablock);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findDatablock(datablock.getDid()) != null) {
                throw new PreexistingEntityException("Datablock " + datablock + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Datablock datablock) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            datablock = em.merge(datablock);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = datablock.getDid();
                if (findDatablock(id) == null) {
                    throw new NonexistentEntityException("The datablock with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Datablock datablock;
            try {
                datablock = em.getReference(Datablock.class, id);
                datablock.getDid();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The datablock with id " + id + " no longer exists.", enfe);
            }
            em.remove(datablock);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
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

    public List<Datablock> findDatablockEntities() {
        return findDatablockEntities(true, -1, -1);
    }

    public List<Datablock> findDatablockEntities(int maxResults, int firstResult) {
        return findDatablockEntities(false, maxResults, firstResult);
    }

    private List<Datablock> findDatablockEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select object(o) from Datablock as o");
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Datablock findDatablock(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Datablock.class, id);
        } finally {
            em.close();
        }
    }

    public int getDatablockCount() {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createQuery("select count(o) from Datablock as o");
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public List<Datablock> findByFsblockid(Long fsblockid) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Datablock.findByFsblockid");
            q.setParameter("fsblockid", fsblockid);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

}
