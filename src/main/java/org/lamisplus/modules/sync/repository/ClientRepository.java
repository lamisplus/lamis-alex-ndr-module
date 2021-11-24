package org.lamisplus.modules.sync.repository;

import org.lamisplus.modules.sync.domain.entity.Patient;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ClientRepository  {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Patient> findOrderedByNumberLimitedTo(int limit) {
        return entityManager.createQuery("SELECT p FROM Patient p ORDER BY p.patientNumber",
                Patient.class).setMaxResults(limit).getResultList();
    }
}
