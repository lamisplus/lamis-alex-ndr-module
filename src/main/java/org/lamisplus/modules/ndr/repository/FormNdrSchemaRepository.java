package org.lamisplus.modules.ndr.repository;

import org.lamisplus.modules.ndr.domain.entity.FormNdrSchema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormNdrSchemaRepository extends JpaRepository<FormNdrSchema, Long> {

   FormNdrSchema findByFormCode(String formCode);
   List<FormNdrSchema> findAllByFormCode(String formCode);
}
