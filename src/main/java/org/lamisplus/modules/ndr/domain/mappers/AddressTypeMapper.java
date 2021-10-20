package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.domain.schema.AddressType;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.domain.entity.OrganisationUnit;
import org.lamisplus.modules.base.repository.OrganisationUnitRepository;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AddressTypeMapper {
    private final NdrCodesetService ndrCodesetService;

    public AddressType map(Patient patient) {
        ObjectMapper objectMapper = new ObjectMapper();

        AddressType addressType = new AddressType();

        JsonNode patientDetails = objectMapper.convertValue(patient.getDetails(), JsonNode.class);

        addressType.setAddressTypeCode("Home");
        addressType.setCountryCode(ndrCodesetService.getCode("COUNTRY", patientDetails.path("country").path("name").asText()));
        addressType.setStateCode(ndrCodesetService.getCode("STATE", patientDetails.path("state").path("name").asText()));
        //addressType.setLGACode(ndrCodesetService.getCode("LGA", patientDetails.path("province").path("name").asText()));
        addressType.setOtherAddressInformation(String.valueOf(patientDetails.get("street")));
        //addressType.setOtherAddressInformation(String.valueOf(patientDetails.get("/address/street")));
        return addressType;
    }
}
