package org.lamisplus.modules.ndr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.lamisplus.modules.base.repository.PatientRepository;
import org.lamisplus.modules.ndr.domain.entity.FormNdrSchema;
import org.lamisplus.modules.ndr.domain.entity.NdrMessage;
import org.lamisplus.modules.ndr.domain.mappers.*;
import org.lamisplus.modules.ndr.domain.schema.*;
import org.lamisplus.modules.ndr.repository.EncounterNdrRepository;
import org.lamisplus.modules.ndr.repository.FormDataRepository;
import org.lamisplus.modules.ndr.repository.FormNdrSchemaRepository;
import org.lamisplus.modules.ndr.repository.NdrMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class NdrMessagePreProcessor {

    private final PatientRepository patientRepository;
    private final NdrMessageRepository ndrMessageRepository;
    private final EncounterNdrRepository encounterNdrRepository;
    private final FormDataRepository formDataRepository;
    private final FormNdrSchemaRepository formNdrSchemaRepository;
    private final NdrMessageService ndrMessageService;
    private final NdrCodesetService ndrCodeSetService;
    private final PatientDemographicsTypeMapper patientDemographicsTypeMapper;
    private final AddressTypeMapper addressTypeMapper;
    private final CommonQuestionsTypeMapper commonQuestionsTypeMapper;
    private final HIVEncounterTypeMapper hivEncounterTypeMapper;
    private final LaboratoryReportTypeMapper laboratoryReportTypeMapper;
    private final RegimenTypeMapper regimenTypeMapper;
    private final HIVQuestionsTypeMapper hivQuestionsTypeMapper;
    private final HIVTestingReportTypeMapper hivTestingReportTypeMapper;
    private final RecencyTestingTypeMapper recencyTestingTypeMapper;
    private final PartnerNotificationTypeMapper partnerNotificationTypeMapper;
    private final FingerPrintTypeMapper fingerPrintTypeMapper;

    AtomicReference<IndividualReportType> individualReportType = new AtomicReference<>(new IndividualReportType());

    AtomicReference<PatientDemographicsType> patientDemographicsType = new AtomicReference<>(new PatientDemographicsType());
    AtomicReference<AddressType> addressType = new AtomicReference<>(new AddressType());

    AtomicReference<ConditionType> conditionType = new AtomicReference<>(new ConditionType());

    //Died, pregnant, edd, current age
    AtomicReference<CommonQuestionsType> commonQuestionsType = new AtomicReference<>(new CommonQuestionsType());

    //ART enrollment & initiation
    AtomicReference<ConditionSpecificQuestionsType> conditionSpecificQuestionsType = new AtomicReference<>(new ConditionSpecificQuestionsType());
    AtomicReference<HIVQuestionsType> hivQuestionsType = new AtomicReference<>(new HIVQuestionsType());

    //Clinic, lab & pharmacy encounters
    AtomicReference<HIVEncounterType> hivEncounterType = new AtomicReference<>(new HIVEncounterType());
    AtomicReference<LaboratoryReportType> laboratoryReportType = new AtomicReference<>(new LaboratoryReportType());
    AtomicReference<RegimenType> regimenType = new AtomicReference<>(new RegimenType());

    AtomicReference<HIVTestingReportType> hivTestingReportType = new AtomicReference<>(new HIVTestingReportType());
    AtomicReference<PartnerNotificationType> partnerNotificationType = new AtomicReference<>(new PartnerNotificationType());
    AtomicReference<RecencyTestingType> recencyTestingType = new AtomicReference<>(new RecencyTestingType());

    ObjectMapper objectMapper = new ObjectMapper();

    public void encounter(Encounter encounter) {

        List<FormNdrSchema> formNdrSchemaList = this.formNdrSchemaRepository.findAllByFormCode(encounter.getFormCode());
        formNdrSchemaList.forEach(formNdrSchema -> {
            switch (formNdrSchema.getNdrSchema()) {
                case "HIVQuestionsType" :
                    mapper1 (encounter);
                    break;
            }
        });

    }

    private void mapper1 (Encounter encounter) {
        hivQuestionsType.set(this.hivQuestionsTypeMapper.map(encounter));
        if (hivQuestionsType.get() != null){
            conditionSpecificQuestionsType.get().setHIVQuestions(hivQuestionsType.get());
            conditionType.get().setConditionSpecificQuestions(conditionSpecificQuestionsType.get());

            Container container = new Container();
            Optional<NdrMessage> ndrMessage = this.ndrMessageRepository.findByPatientId(encounter.getPatientId());
            if (ndrMessage.isPresent()){
                //Read container from database and get all encounter since after the last ndr message generation
                container = objectMapper.convertValue(ndrMessage.get().getContainer(), Container.class);
            } else {
                ndrMessage = Optional.of(new NdrMessage());
            }
            //container.getIndividualReport().getCondition().
        }

    }
}
