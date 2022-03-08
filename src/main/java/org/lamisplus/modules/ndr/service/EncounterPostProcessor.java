package org.lamisplus.modules.ndr.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.domain.entity.FormNdrSchema;
import org.lamisplus.modules.ndr.domain.entity.NdrMessage;
import org.lamisplus.modules.ndr.domain.mappers.*;
import org.lamisplus.modules.ndr.domain.schema.*;
import org.lamisplus.modules.ndr.repository.*;
import org.lamisplus.modules.base.repository.PatientRepository;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class EncounterPostProcessor {
    private static final String REGISTRATION_FORM_CODE = "bbc01821-ff3b-463d-842b-b90eab4bdacd";
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

    private final ClientRepository clientRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    //repeat NDR message generation every 6 hours  "0 0 0/6 * * ?"
    @Scheduled(cron="0 0/5 * * * ?")
    public void process() {
        System.out.println("Generating NDR messages......");

        //List<Patient> patientList = only enrollment into HIV  and HTS services
        //List<Patient> patientList = this.patientRepository.findAll();

        List<Patient> patientList = this.clientRepository.findOrderedByNumberLimitedTo(50);

        patientList.forEach(patient -> {
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

            AtomicBoolean partner = new AtomicBoolean(false);

            Container container = new Container();
            List<Encounter> encounterList;

            //Check if this patient has NDR message ever generated and deserialize the message to Container object
            //Then retrieve all encounter records added or modified since the last time NDR message was generated
            Optional<NdrMessage> ndrMessage = this.ndrMessageRepository.findByPatientId(patient.getId());
            if (ndrMessage.isPresent()){
                // Read container from database and get all encounter since after the last ndr message generation
                container = objectMapper.convertValue(ndrMessage.get().getContainer(), Container.class);
                encounterList = this.encounterNdrRepository.findByPatientByPatientIdAndDateModifiedIsAfter(patient, ndrMessage.get().getDateLastGenerated());
            } else {
                //https://www.javadevjournal.com/java/java-8-optional/
                ndrMessage = Optional.of(new NdrMessage());
                // This patient does not have an ndr message built. Get all encounter and build an ndr container
                encounterList = this.encounterNdrRepository.findByPatientId(patient.getId());
            }
            // We are generating NDR messages for HIV related disease, therefore we set the CONDITION CODE to HIV
            conditionType.get().setConditionCode(ndrCodeSetService.getCode("CONDITION_CODE", "HIV_CODE"));
            ProgramAreaType programAreaType = new ProgramAreaType();
            programAreaType.setProgramAreaCode(ndrCodeSetService.getCode("PROGRAM_AREA", "HIV"));
            conditionType.get().setProgramArea(programAreaType);

            // Set address information
            conditionType.get().setPatientAddress(this.addressTypeMapper.map(patient));

            encounterList.forEach(encounter -> {
                // For each encounter get the ndr schema that needs to be populated with its form data
                List<FormNdrSchema> formNdrSchemaList = this.formNdrSchemaRepository.findAllByFormCode(encounter.getFormCode());
                formNdrSchemaList.forEach(formNdrSchema -> {

                    // Common questions
                    if (formNdrSchema.getNdrSchema().equals("CommonQuestionsType")){
                       commonQuestionsType.set(this.commonQuestionsTypeMapper.map(encounter));
                       if (commonQuestionsType.get() != null) {
                           conditionType.get().setCommonQuestions(commonQuestionsType.get());
                       }
                    }

                    // Condition specific questions
                    if (formNdrSchema.getNdrSchema().equals("HIVQuestionsType")){
                        hivQuestionsType.set(this.hivQuestionsTypeMapper.map(encounter));
                        if (hivQuestionsType.get() != null){
                            conditionSpecificQuestionsType.get().setHIVQuestions(hivQuestionsType.get());
                            conditionType.get().setConditionSpecificQuestions(conditionSpecificQuestionsType.get());
                        }
                    }

                    // HIV clinic encounter data
                    if (formNdrSchema.getNdrSchema().equals("HIVEncounterType")){
                        hivEncounterType.set(this.hivEncounterTypeMapper.map(encounter));
                        if (hivEncounterType.get().getVisitID() != null) {
                            if (conditionType.get().getEncounters() != null) {
                                conditionType.get().getEncounters().getHIVEncounter().removeIf(hivEncounterType1 -> hivEncounterType1.getVisitID().equals(hivEncounterType.get().getVisitID()));
                                conditionType.get().getEncounters().getHIVEncounter().add(hivEncounterType.get());
                            } else {
                                EncountersType encountersType = new EncountersType();
                                encountersType.getHIVEncounter().add(hivEncounterType.get());
                                conditionType.get().setEncounters(encountersType);
                            }
                        }
                    }

                    // Laboratory order and report data
                    if (formNdrSchema.getNdrSchema().equals("LaboratoryReportType")){
                        laboratoryReportType.set(this.laboratoryReportTypeMapper.map(encounter));
                        //To ensure no duplicate lab record check if this lab order and result exist and delete before adding this lab report type
                        if (laboratoryReportType.get().getVisitID() != null) {
                            conditionType.get().getLaboratoryReport().removeIf(laboratoryReportType1 -> laboratoryReportType1.getVisitID().equals(laboratoryReportType.get().getVisitID()));
                            conditionType.get().getLaboratoryReport().add(laboratoryReportType.get());
                        }
                    }

                    // Regimen dispensing data
                    if (formNdrSchema.getNdrSchema().equals("RegimenType")){
                        regimenType.set(this.regimenTypeMapper.map(encounter));
                        // To ensure no duplicate regimen record, check if this regimen dispensing and delete before adding this regimen type
                        if (regimenType.get().getVisitID() != null) {
                            conditionType.get().getRegimen().removeIf(regimenType1 -> regimenType1.getVisitID().equals(regimenType.get().getVisitID()));
                            conditionType.get().getRegimen().add(regimenType.get());
                        }
                    }

                    // HIV testing service data
                    if (formNdrSchema.getNdrSchema().equals("HIVTestingReportType")){
                        hivTestingReportType.set(this.hivTestingReportTypeMapper.map(encounter));
                        if (hivTestingReportType.get().getVisitID() != null) {
                            individualReportType.get().getHIVTestingReport().add(hivTestingReportType.get());
                        }
                    }

                    // Index and contact tracing data
                    if (formNdrSchema.getNdrSchema().equals("PartnerNotificationType") && !partner.get()){
                        hivTestingReportType.get().getIndexNotificationServices().setPartner(this.partnerNotificationTypeMapper.map(encounter));
                        // All patient partners are populated at the first time when a partner notification encounter is true
                        partner.set(true);
                    }

                    // Recency testing data
                    if (formNdrSchema.getNdrSchema().equals("RecencyTestingType")){
                        hivTestingReportType.get().getHIVTestResult().setRecencyTesting(this.recencyTestingTypeMapper.map(encounter));
                        //hivTestingReportType.get().getHIVTestResult().setTestResult(testResultType);
                    }
                });
            });

            //end of patient encounters processing

            // Iterate the condition type in the individual report object and remove if current condition code i.e HIV exist
            // and replace with the condition type object we have populated with patient's current clinic, pharmacy, lab, hts, hiv etc records
            if (container.getIndividualReport() != null){
                individualReportType.get().getCondition().removeIf(conditionType1 -> !conditionType1.getConditionCode().equals(conditionType.get().getConditionCode()));
            }
            individualReportType.get().getCondition().add(conditionType.get());

            // Set patient demographics
            patientDemographicsType.set(this.patientDemographicsTypeMapper.map(patient));

            // Get patient fingerprint templates
            FingerPrintType fingerPrintType = fingerPrintTypeMapper.map(patient);
            if (fingerPrintType != null) {
                patientDemographicsType.get().setFingerPrints(fingerPrintType);
            }
            individualReportType.get().setPatientDemographics(patientDemographicsType.get());
            container.setIndividualReport(individualReportType.get());

            // Write ndr container to database
            try {
                ndrMessage.get().setContainer(objectMapper.writeValueAsString(container));

                //LocalDate date = LocalDate.parse(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                ndrMessage.get().setDateLastGenerated(LocalDateTime.now());
                ndrMessage.get().setPatientId(patient.getId());
                ndrMessage.get().setOrganisationUnitId(patient.getOrganisationUnitId());
                ndrMessage.get().setMarshalled(false);

                ndrMessageService.save(ndrMessage.get());
                System.out.println("Message saved: " + ndrMessage.get().getPatientId());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }
}

/*

// HIV clinic encounter data
    if (formNdrSchema.getNdrSchema().equals("HIVEncounterType")){
            hivEncounterType.set(this.hivEncounterTypeMapper.map(encounter));
            if (hivEncounterType.get().getVisitID() != null) {
            EncountersType encountersType = new EncountersType();
            List<HIVEncounterType> hivEncounterTypeList;
    if (conditionType.get().getEncounters() != null) {
        hivEncounterTypeList = conditionType.get().getEncounters().getHIVEncounter();
        if (hivEncounterTypeList != null){
        hivEncounterTypeList = hivEncounterTypeList.stream()
        .filter(hivEncounterType1 -> !hivEncounterType1.getVisitDate().equals(hivEncounterType.get().getVisitDate()))
        .collect(Collectors.toList());

        //Rebuild the encounter object an replace in condition type
        hivEncounterTypeList.forEach(hivEncounterType1 -> {
        encountersType.getHIVEncounter().add(hivEncounterType1);
        });
        }
        }
        encountersType.getHIVEncounter().add(hivEncounterType.get());
        conditionType.get().setEncounters(encountersType);
        }
     }

  */
