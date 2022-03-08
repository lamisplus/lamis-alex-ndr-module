package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

        JsonNode details = objectMapper.convertValue(patient.getDetails(), JsonNode.class);

        addressType.setAddressTypeCode("Home");
        String ndrCode = ndrCodesetService.getCode("COUNTRY", details.path("country").path("name").asText());
        if (!StringUtils.isEmpty(ndrCode)) addressType.setCountryCode(ndrCode);

        ndrCode = ndrCodesetService.getCode("STATE", details.path("state").path("name").asText());
        if(!StringUtils.isEmpty(ndrCode)) addressType.setStateCode(ndrCode);

        ndrCode = ndrCodesetService.getCode("LGA", details.path("province").path("name").asText());
        if(!StringUtils.isEmpty(ndrCode))  addressType.setLGACode(ndrCode);

        addressType.setOtherAddressInformation(StringUtils.trimToEmpty(details.get("street").asText()));
        //addressType.setOtherAddressInformation(String.valueOf("/details/street"));
        return addressType;
    }
}
