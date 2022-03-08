package org.lamisplus.modules.ndr.controller;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entity.OrganisationUnit;
import org.lamisplus.modules.ndr.domain.dto.FacilityIdDTO;
import org.lamisplus.modules.ndr.domain.dto.FileInfoDTO;
import org.lamisplus.modules.ndr.service.EncounterPostProcessor;
import org.lamisplus.modules.ndr.service.NdrMessageService;
import org.lamisplus.modules.ndr.service.OrganisationUnitNdrService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ndr")
public class NdrMessageController {
    private final NdrMessageService ndrMessageService;
    private final EncounterPostProcessor encounterPostProcessor;
    private final OrganisationUnitNdrService organisationUnitService;

    //https://stackoverflow.com/questions/18920770/restful-webservice-spring-xml-in-stead-of-json/30201259
    //Spring will marshall the bean to xml when it sees:
    //1.Object annotated with JAXB
    //2.JAXB library existed in classpath
    //3.“mvc:annotation-driven” is enabled
    //4.Return method annotated with @ResponseBody

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity<FileInfoDTO> download() {
        return ResponseEntity.ok(this.ndrMessageService.download());
        //return ResponseEntity.status(HttpStatus.OK).body(fileInfos);
    }

    @PostMapping("/generate")
    public  ResponseEntity<String>  generate(@RequestBody FacilityIdDTO facilityIdDTO) {
        System.out.println("Facilities to process: "+facilityIdDTO);
        ndrMessageService.generate(facilityIdDTO);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/process")
    public ResponseEntity<String> test() {
        encounterPostProcessor.process();
        return ResponseEntity.ok("Successful");
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<OrganisationUnit>> getAllOrganizationUnit() {
        return ResponseEntity.ok(organisationUnitService.findOrganisationUnitWithRecords());
    }

}

