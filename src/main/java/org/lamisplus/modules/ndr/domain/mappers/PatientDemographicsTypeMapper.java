package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.base.domain.entity.OrganisationUnit;
import org.lamisplus.modules.base.repository.OrganisationUnitRepository;
import org.lamisplus.modules.ndr.domain.schema.FacilityType;
import org.lamisplus.modules.ndr.domain.schema.PatientDemographicsType;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PatientDemographicsTypeMapper {
    private final NdrCodesetService ndrCodesetService;
    private final OrganisationUnitRepository organisationUnitRepository;

    public PatientDemographicsType map(Patient patient) {
        ObjectMapper objectMapper = new ObjectMapper();
        PatientDemographicsType patientDemographicsType = new PatientDemographicsType();

        JsonNode patientDetails = objectMapper.convertValue(patient.getDetails(), JsonNode.class);

        patientDemographicsType.setPatientIdentifier(String.valueOf(patientDetails.get("hospitalNumber")));
        try {
            patientDemographicsType.setPatientDateOfBirth(DatatypeFactory.newInstance().newXMLGregorianCalendar(patientDetails.get("dob").asText()));
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        patientDemographicsType.setPatientEducationLevelCode(ndrCodesetService.getCode("EDUCATIONAL_LEVEL", String.valueOf(patientDetails.get("/education/display"))));
        patientDemographicsType.setPatientOccupationCode((ndrCodesetService.getCode("OCCUPATION_STATUS", String.valueOf(patientDetails.get("/occupation/display")))));
        patientDemographicsType.setPatientSexCode((ndrCodesetService.getCode("SEX", String.valueOf(patientDetails.get("/gender/display")))));
        patientDemographicsType.setStateOfNigeriaOriginCode((ndrCodesetService.getCode("STATES", String.valueOf(patientDetails.get("/state/display")))));
        patientDemographicsType.setPatientMaritalStatusCode((ndrCodesetService.getCode("MARITAL_STATUS", String.valueOf(patientDetails.get("/maritalStatus/display")))));
/*
        patientDemographicsType.setPatientDeceasedDate();
        patientDemographicsType.setPatientDeceasedIndicator();
*/

        FacilityType treatmentFacility = new FacilityType();
        Optional<OrganisationUnit> organisationUnit = organisationUnitRepository.findById(patient.getOrganisationUnitId());
        treatmentFacility.setFacilityID(Long.toString(organisationUnit.get().getId()));
        treatmentFacility.setFacilityName(organisationUnit.get().getName());
        treatmentFacility.setFacilityTypeCode("FAC");
        patientDemographicsType.setTreatmentFacility(treatmentFacility);
        //patientDemographicsType.setO("MR");

        return patientDemographicsType;

    }
}
