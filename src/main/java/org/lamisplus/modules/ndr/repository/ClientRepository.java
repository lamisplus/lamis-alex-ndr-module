package org.lamisplus.modules.ndr.repository;

import org.lamisplus.modules.base.domain.entity.Patient;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ClientRepository  {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Patient> findOrderedByNumberLimitedTo(int limit) {
        return entityManager.createQuery("SELECT p FROM Patient p ORDER BY p.hospitalNumber",
                Patient.class).setMaxResults(limit).getResultList();
    }
}
