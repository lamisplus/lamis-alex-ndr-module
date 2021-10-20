package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.domain.schema.CodedSimpleType;
import org.lamisplus.modules.ndr.domain.schema.RegimenType;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.lamisplus.modules.base.repository.RegimenLineRepository;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.base.domain.entity.RegimenLine;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegimenTypeMapper {
    private final NdrCodesetService ndrCodesetService;
    private final RegimenLineRepository regimenLineRepository;
    private final FormDataRepository formDataRepository;

    public RegimenType map(Encounter encounter)  {
        ObjectMapper objectMapper = new ObjectMapper();
        RegimenType regimenType = new RegimenType();

        List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
        FormData formData = null;
        if(!formDataList.isEmpty()) {
            formData = formDataList.get(formDataList.size() - 1);
            JsonNode pharmacy = objectMapper.convertValue(formData, JsonNode.class);
            System.out.println(".....pharmacy:  "+pharmacy);

            if(pharmacy.path("data").path("type").asInt() == 0) {
                try {
                    regimenType.setVisitDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(String.valueOf(encounter.getDateEncounter())));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
                regimenType.setVisitID(String.valueOf(encounter.getId()));

                String regimen = pharmacy.path("data").path("regimen").path("name").asText();
                Optional<RegimenLine> regimenLine = regimenLineRepository.getRegimenLineById(pharmacy.path("data").path("regimen").path("regimenLineId").asLong());
                CodedSimpleType cst = ndrCodesetService.getCodedSimpleType("ARV_REGIMEN", regimen);
                if (cst.getCode() != null){
                    regimenType.setPrescribedRegimen(cst);
                    regimenType.setPrescribedRegimenTypeCode(ndrCodesetService.getCode("REGIMEN_TYPE", "ART"));
                    regimenType.setPrescribedRegimenLineCode(ndrCodesetService.getCode("REGIMEN_LINE", regimenLine.get().getName()));

                    int durationInDays = durationInDays(pharmacy.path("data").path("duration_unit").asText(), pharmacy.path("data").path("duration").asInt());
                    regimenType.setPrescribedRegimenDuration(String.valueOf(durationInDays));

                    try {
                        regimenType.setPrescribedRegimenDispensedDate((DatatypeFactory.newInstance().newXMLGregorianCalendar(pharmacy.path("data").path("date_dispensed").asText())));
                    } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        else System.out.println("Encounter no form data (regimen): "+encounter.getFormCode() + "  for patient ID: " + encounter.getPatientId());
        return regimenType;
    }

    private int durationInDays(String durationUnit, int duration) {
        if(durationUnit.equals("weeks")) return duration * 7;
        if(durationUnit.equals("months")) return duration * 30;
       else return duration;
    }
}
