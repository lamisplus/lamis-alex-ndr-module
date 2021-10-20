package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.schema.PartnerNotificationType;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PartnerNotificationTypeMapper {
    private final FormDataRepository formDataRepository;
    private final NdrCodesetService ndrCodesetService;

    public List<PartnerNotificationType> map(Encounter encounter) {
        ObjectMapper objectMapper = new ObjectMapper();
        PartnerNotificationType partnerNotificationType = new PartnerNotificationType();
        List<PartnerNotificationType> partnerNotificationTypeList = new ArrayList<>();

        List<FormData> formDataList = this.formDataRepository.findByFormCodeAndPatientId(encounter.getFormCode(), encounter.getPatientId());
        if(!formDataList.isEmpty()) {
            formDataList.forEach(formData -> {
                JsonNode partner = objectMapper.convertValue(formData.getData(), JsonNode.class);

                partnerNotificationType.setDescriptiveAddress(StringUtils.trimToEmpty(partner.get("address").asText()));
                partnerNotificationType.setIndexRelation(StringUtils.trimToEmpty(partner.get("relationship").asText()));
                partnerNotificationType.setPartnerGender((ndrCodesetService.getCode("SEX", String.valueOf(partner.get("gender")))));
                String partnerName = StringUtils.trimToEmpty(partner.get("surname").asText()) + " " + StringUtils.trimToEmpty(partner.get("other_names").asText());
                partnerNotificationType.setPartnername(partnerName);
                partnerNotificationType.setPhoneNumber(StringUtils.trimToEmpty(partner.get("phone").asText()));
                partnerNotificationTypeList.add(partnerNotificationType);
            });
        }
        return partnerNotificationTypeList;
    }
}
