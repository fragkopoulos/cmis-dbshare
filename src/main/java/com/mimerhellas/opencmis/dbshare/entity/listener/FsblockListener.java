/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mimerhellas.opencmis.dbshare.entity.listener;

import com.mimerhellas.opencmis.dbshare.controller.exceptions.NonexistentEntityException;
import com.mimerhellas.opencmis.dbshare.controller.exceptions.RollbackFailureException;
import com.mimerhellas.opencmis.dbshare.entity.Datablock;
import com.mimerhellas.opencmis.dbshare.entity.Fsblock;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.PostRemove;
import javax.persistence.Query;

/**
 *
 * @author kostas
 */
public class FsblockListener {

    @PostRemove
    public void postRemove(Fsblock fsblock) throws RollbackFailureException, Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("NewRSPU");
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();

            Query q = em.createNamedQuery("Datablock.findByFsblockid");
            q.setParameter("fsblockid", fsblock.getFsid());
            List<Datablock> datablocklist = q.getResultList();
            for (Datablock datablock : datablocklist) {
                em.remove(datablock);
            }
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
}
