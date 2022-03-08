package org.lamisplus.modules.ndr.domain.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.lamisplus.modules.ndr.domain.schema.*;
import org.lamisplus.modules.ndr.service.NdrCodesetService;
import org.lamisplus.modules.base.repository.FormDataRepository;
import org.lamisplus.modules.base.domain.entity.FormData;
import org.lamisplus.modules.base.domain.entity.Encounter;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HIVTestingReportTypeMapper {
    private final FormDataRepository formDataRepository;
    private final NdrCodesetService ndrCodesetService;

    public HIVTestingReportType map(Encounter encounter) {
        ObjectMapper objectMapper = new ObjectMapper();
        HIVTestingReportType hivTestingReportType = new HIVTestingReportType();

        List<FormData> formDataList = this.formDataRepository.findByEncounterId(encounter.getId());
        if(!formDataList.isEmpty()) {
            FormData formData = formDataList.get(formDataList.size() - 1);
            JsonNode hts = objectMapper.convertValue(formData, JsonNode.class);

            try {
                hivTestingReportType.setVisitDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat("yyyy-MM-dd").format(encounter.getDateEncounter())));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
            hivTestingReportType.setVisitID(String.valueOf(encounter.getUuid()));

            //required
            String firstTimeVisit = StringUtils.trimToEmpty(hts.get("first_time_visit").asText());

            String isIndexClient = StringUtils.trimToEmpty(hts.get("index_client").asText());
            String indexType = StringUtils.trimToEmpty(hts.get("type_index").asText());
            String indexClientId = StringUtils.trimToEmpty(hts.get("index_client_code").asText());
            String reTestingForResultVerification = null;

            //hivTestingReportType.setClientCode(clientCode);

            firstTimeVisit = firstTimeVisit.equals("Yes") ? "Y" : "N";
            hivTestingReportType.setFirstTimeVisit(firstTimeVisit);

            hivTestingReportType.setSetting(ndrCodesetService.getCode("HTS_SETTING", hts.get("testing_setting").asText()));
            hivTestingReportType.setSessionType(ndrCodesetService.getCode("SESSION_TYPE", hts.get("type_counseling").asText()));
            hivTestingReportType.setReferredFrom(ndrCodesetService.getCode("REFERRED_FROM", hts.get("referred_from").asText()));
            hivTestingReportType.setMaritalStatus(ndrCodesetService.getCode("MARITAL_STATUS", hts.get("marital_status").asText()));
            hivTestingReportType.setNoOfOwnChildrenLessThan5Years(hts.get("num_children").asInt());
            hivTestingReportType.setNoOfAllWives(hts.get("num_wifes").asInt());

            //HIV knowledge assessment
            KnowledgeAssessmentType knowledgeAssessmentType = new KnowledgeAssessmentType();
            Boolean knowledge = hts.get("knowledge_assessment1").asInt() == 1 ? true : false;
            knowledgeAssessmentType.setClientInformedAboutHIVTransmissionRoutes(knowledge);
            knowledge = hts.get("knowledge_assessment2").asInt() == 1 ? true : false;
            knowledgeAssessmentType.setClientInformedAboutPossibleTestResults(knowledge);
            knowledge = hts.get("knowledge_assessment3").asInt() == 1 ? true : false;
            knowledgeAssessmentType.setClientInformedAboutPreventingHIV(knowledge);
            knowledge = hts.get("knowledge_assessment4").asInt() == 1 ? true : false;
            knowledgeAssessmentType.setClientInformedOfHIVTransmissionRiskFactors(knowledge);
            knowledge = hts.get("knowledge_assessment5").asInt() == 1 ? true : false;
            knowledgeAssessmentType.setClientPregnant(knowledge);
            knowledge = hts.get("knowledge_assessment6").asInt()== 1 ? true : false;
            knowledgeAssessmentType.setInformedConsentForHIVTestingGiven(knowledge);
            knowledge = hts.get("knowledge_assessment7").asInt() == 1 ? true : false;
            knowledgeAssessmentType.setPreviouslyTestedHIVNegative(knowledge);

            //HIV risk assessment
            HIVRiskAssessmentType hivRiskAssessmentType = new HIVRiskAssessmentType();
            Boolean risk = hts.get("risk_assessment1").asInt() == 1 ? true : false;
            hivRiskAssessmentType.setEverHadSexualIntercourse(risk);
            risk = hts.get("risk_assessment2").asInt() == 1 ? true : false;
            hivRiskAssessmentType.setBloodTransfussionInLast3Months(risk);
            risk = hts.get("risk_assessment3").asInt() == 1 ? true : false;
            hivRiskAssessmentType.setUnprotectedSexWithCasualPartnerinLast3Months(risk);
            risk = hts.get("risk_assessment4").asInt() == 1 ? true : false;
            hivRiskAssessmentType.setUnprotectedSexWithRegularPartnerInLast3Months(risk);
            risk = hts.get("risk_assessment5").asInt() == 1 ? true : false;
            hivRiskAssessmentType.setSTIInLast3Months(risk);
            risk = hts.get("risk_assessment6").asInt() == 1 ? true : false;
            hivRiskAssessmentType.setMoreThan1SexPartnerDuringLast3Months(risk);

            //TB Screening
            ClinicalTBScreeningType clinicalTBScreeningType = new ClinicalTBScreeningType();
            Boolean cough = hts.get("tb_screening1").asInt() == 1 ? true : false;
            Boolean weightLoss = hts.get("tb_screening2").asInt() == 1 ? true : false;
            Boolean fever = hts.get("tb_screening3").asInt() == 1 ? true : false;
            Boolean sweat = hts.get("tb_screening4").asInt() == 1 ? true : false;
            clinicalTBScreeningType.setFever(fever);
            clinicalTBScreeningType.setCurrentlyCough(cough);
            clinicalTBScreeningType.setNightSweats(sweat);
            clinicalTBScreeningType.setWeightLoss(weightLoss);

            //STI Screening
            SyndromicSTIScreeningType syndromicSTIScreeningType = new SyndromicSTIScreeningType();
            Boolean sti = hts.get("sti_screening1").asInt() == 1 ? true : false;
            syndromicSTIScreeningType.setUrethralDischargeOrBurningWhenUrinating(sti);
            sti = hts.get("sti_screening2").asInt() == 1 ? true : false;
            syndromicSTIScreeningType.setLowerAbdominalPainsWithOrWithoutVaginalDischarge(sti);
            sti = hts.get("sti_screening2").asInt() == 1 ? true : false;
            syndromicSTIScreeningType.setScrotalSwellingAndPain(sti);
            sti = hts.get("sti_screening3").asInt() == 1 ? true : false;
            syndromicSTIScreeningType.setGenitalSoreOrSwollenInguinalLymphNodes(sti);
            sti = hts.get("sti_screening4").asInt() == 1 ? true : false;
            syndromicSTIScreeningType.setVaginalDischargeOrBurningWhenUrinating(sti);

            //Pre-testing information
            PreTestInformationType preTestInformation = new PreTestInformationType();
            preTestInformation.setClinicalTBScreening(clinicalTBScreeningType);
            preTestInformation.setHIVRiskAssessment(hivRiskAssessmentType);
            preTestInformation.setKnowledgeAssessment(knowledgeAssessmentType);
            preTestInformation.setSyndromicSTIScreening(syndromicSTIScreeningType);

            //Hiv test result type
            HIVTestResultType hivTestResult = new HIVTestResultType();
            TestResultType testResultType = new TestResultType();
            String testResult = StringUtils.trimToEmpty(hts.get("hiv_test_result").asText()).equals("Positive")
                    ? "Positive" : "Negative";
            testResultType.setFinalTestResult(testResult);
            testResultType.setConfirmatoryTestResult("NR");
            // testResultType.setConfirmatoryTestResultDate(dateVisit);
            testResultType.setScreeningTestResult("R");
            // testResultType.setScreeningTestResultDate(dateVisit);
            testResultType.setTieBreakerTestResult("NR");
            // testResultType.setTieBreakerTestResultDate(visitDate);
            hivTestResult.setTestResult(testResultType);


            //Post-test counselling type
            PostTestCounsellingType postTestCounselling = new PostTestCounsellingType();
            Boolean postTest = hts.get("post_test1").asInt() == 1 ? true : false;
            postTestCounselling.setHIVRequestAndResultFormSignedByTester(postTest);
            postTest = hts.get("post_test2").asInt() == 1 ? true : false;
            postTestCounselling.setHIVRequestAndResultFormFilledWithCTIForm(postTest);
            postTest = hts.get("post_test3").asInt() == 1 ? true : false;
            postTestCounselling.setClientRecievedHIVTestResult(postTest);
            postTest = hts.get("post_test4").asInt() == 1 ? true : false;
            postTestCounselling.setPostTestCounsellingDone(postTest);
            postTest = hts.get("post_test5").asInt() == 1 ? true : false;
            postTestCounselling.setRiskReductionPlanDeveloped(postTest);
            postTest = hts.get("post_test6").asInt() == 1 ? true : false;
            postTestCounselling.setPostTestDisclosurePlanDeveloped(postTest);
            postTest = hts.get("post_test7").asInt() == 1 ? true : false;
            postTestCounselling.setWillBringPartnerForHIVTesting(postTest);
            postTest = hts.get("post_test8").asInt() == 1 ? true : false;
            postTestCounselling.setWillBringOwnChildrenForHIVTesting(postTest);
            postTest = hts.get("post_test9").asInt() == 1 ? true : false;
            postTestCounselling.setProvidedWithInformationOnFPandDualContraception(postTest);
            postTest = hts.get("post_test10").asInt() == 1 ? true : false;
            postTestCounselling.setClientOrPartnerUseFPMethodsOtherThanCondoms(postTest);
            postTest = hts.get("post_test11").asInt() == 1 ? true : false;
            postTestCounselling.setClientOrPartnerUseCondomsAsOneFPMethods(postTest);
            postTest = hts.get("post_test12").asInt() == 1 ? true : false;
            postTestCounselling.setCorrectCondomUseDemonstrated(postTest);
            postTest = hts.get("post_test13").asInt() == 1 ? true : false;
            postTestCounselling.setCondomsProvidedToClient(postTest);
            postTest = hts.get("post_test14").asInt() == 1 ? true : false;
            postTestCounselling.setClientReferredToOtherServices(postTest);

            String syphilisTestResult = StringUtils.trimToEmpty(hts.get("syphilis_test_result").asText())
                    .equals("Non-Reactive") ? "NR" : "R";
            String hbvTestResult = StringUtils.trimToEmpty(hts.get("hepatitisb_test_result").asText())
                    .equals("Positive") ? "Pos" : "Neg";
            String hcvTestResult = StringUtils.trimToEmpty(hts.get("hepatitisc_test_result").asText())
                    .equals("Positive") ? "Pos" : "Neg";

            if (isIndexClient != null) {
                isIndexClient = isIndexClient.equals("Yes") ? "Y" : "N";
                hivTestingReportType.setIsIndexClient(isIndexClient);
            }
            if (!indexClientId.isEmpty()) {
                hivTestingReportType.setIndexClientId(indexClientId);
            }

            if (preTestInformation != null) {
                hivTestingReportType.setPreTestInformation(preTestInformation);
            }
            if (postTestCounselling != null) {
                hivTestingReportType.setPostTestCounselling(postTestCounselling);
            }
            if (syphilisTestResult != null) {
                hivTestingReportType.setSyphilisTestResult(syphilisTestResult);
            }
            if (hbvTestResult != null) {
                hivTestingReportType.setHBVTestResult(hbvTestResult);
            }
            if (hcvTestResult != null) {
                hivTestingReportType.setHCVTestResult(hcvTestResult);
            }
        }

        return hivTestingReportType;
    }
}
