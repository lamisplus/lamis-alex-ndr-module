package org.lamisplus.modules.ndr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.base.domain.entity.OrganisationUnit;
import org.lamisplus.modules.ndr.domain.dto.FileInfo;
import org.lamisplus.modules.ndr.domain.dto.FacilityIdDTO;
import org.lamisplus.modules.ndr.domain.dto.FileInfoDTO;
import org.lamisplus.modules.ndr.domain.entity.NdrMessage;
import org.lamisplus.modules.ndr.domain.mappers.MessageHeaderTypeMapper;
import org.lamisplus.modules.ndr.domain.schema.Container;
import org.lamisplus.modules.ndr.domain.schema.MessageHeaderType;
import org.lamisplus.modules.ndr.repository.NdrMessageRepository;

import org.lamisplus.modules.ndr.repository.OrganisationUnitNdrRepository;
import org.lamisplus.modules.ndr.utility.NdrFileUtil;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class NdrMessageService {
    private final NdrMessageRepository ndrMessageRepository;
    private final MessageHeaderTypeMapper messageHeaderTypeMapper;
    private final OrganisationUnitNdrRepository organisationUnitNdrRepository;
    private final NdrFileUtil fileUtils;

    private final static ExecutorService executorService = Executors.newFixedThreadPool(50);
    ObjectMapper objectMapper = new ObjectMapper();

    public void generate(FacilityIdDTO facilityIdDTO) {
        facilityIdDTO.getFacilityIds().forEach(id -> {
            String folder = ("transfer/temp/").concat(Long.toString(id)).concat("/");
            fileUtils.makeDir(folder);
            if(!fileUtils.isLocked(folder)){
                fileUtils.deleteFileWithExtension(folder, ".xml");
                fileUtils.deleteFileWithExtension(folder, ".zip");
                fileUtils.lockFolder(folder);
                NdrThread processorThread = new NdrThread(id);

                executorService.execute(processorThread);
            }
        });
        executorService.shutdown();
        while (!executorService.isTerminated()) {
        }
    }

    public void generate(Long facilityId) {
        //It is important to set the date of last message generation to the time when the NDR message container
        //is retrieved from database, to ensure that next message generated on the database is not missed
        LocalDateTime localDateTime = LocalDateTime.now();
        // Generate ndr message file for this facility
        List<NdrMessage> ndrMessageList = ndrMessageRepository.findByOrganisationUnitIdAndMarshalled(facilityId, false);
        if(!ndrMessageList.isEmpty()) {
            ndrMessageList.forEach(ndrMessage -> {
                generate(ndrMessage, localDateTime);
            });
        }
        // Zip generated ndr files for this facility
        zipFolder(facilityId);
    }

    public void generate(NdrMessage ndrMessage, LocalDateTime localDateTime) {
        try {
            //Represents the Container (highest level of the schema)
            Container container = objectMapper.convertValue(ndrMessage.getContainer(), Container.class);

            //Set the Header Information
            MessageHeaderType header = messageHeaderTypeMapper.messageHeaderType(ndrMessage);
            //Set the Header to the Container
            container.setMessageHeader(header);

            JAXBContext jaxbContext = JAXBContext.newInstance("org.lamisplus.modules.ndr.domain.schema");
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

            //Validate message against NDR schema (Version 1.6.2)
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File("NDR 1.6.2.xsd"));
            //jaxbMarshaller.setSchema(schema);
            //Call Validator class to perform the validation
            //jaxbMarshaller.setEventHandler(new Validator());

            Thread.sleep(1000);     //Delay for some milli seconds
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss.ms");

            //Create a temporary folder to store the each patient generated XML files, before zipping into a facility file in the ndr folder
            String folder = ("transfer/temp/").concat(Long.toString(ndrMessage.getOrganisationUnitId())).concat("/");
            fileUtils.makeDir(folder);

            //Each patient message file naming convention is IP name_date generated_time generated
            String fileName = header.getMessageSendingOrganization().getFacilityID() + "_" + dateFormat.format(date) + "_" + timeFormat.format(date) + ".xml";
            File file = new File(folder.concat(fileName));

            System.out.println("Marshalling messages........");
            jaxbMarshaller.marshal(container, file);

/*
            Path root = Paths.get(this.getClass().getResource("/").getPath());
            Path path = Paths.get(root.toAbsolutePath() + "/ndr/");
            Files.createDirectories(path);
*/
            //update the date message was generated for client
            ndrMessage.setDateLastGenerated(localDateTime);
            ndrMessage.setMarshalled(true);
            ndrMessageRepository.save(ndrMessage);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public NdrMessage save(NdrMessage ndrMessage) {
        return ndrMessageRepository.save(ndrMessage);
    }

    private void zipFolder(Long facilityId) {
        Optional<OrganisationUnit> organisationUnit = organisationUnitNdrRepository.findById(facilityId);
        if(organisationUnit.isPresent()) {
            String sourceFolder = ("transfer/temp/").concat(Long.toString(organisationUnit.get().getId())).concat("/");
            String destinationFolder = "transfer/ndr/";
            fileUtils.makeDir(destinationFolder);

            long timestamp = new Date().getTime();
            String fileName = organisationUnit.get().getName().concat("_").concat(String.valueOf(timestamp)).concat(".zip");
            String outputZipFile = destinationFolder.concat(fileName);

            fileUtils.deleteFileWithExtension(sourceFolder, ".ser");
            fileUtils.zipDirectory(sourceFolder, outputZipFile);
            fileUtils.unlockFolder(fileName);
        }

    }

    public FileInfoDTO download() {
        FileInfoDTO fileInfos = new FileInfoDTO();
        String dir = "transfer/ndr/";
        Set<String> listFiles = fileUtils.listFiles(dir);
        for (String file : listFiles) {
            //Remove all hidden files
            if(!(new File(dir.concat(file)).isHidden())) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(file);
                fileInfo.setUrl(new File("transfer/ndr/").getAbsolutePath());
                fileInfo.setDateGenerated(LocalDate.now());
                fileInfo.setNumberRecords(20);
                fileInfos.getFileInfos().add(fileInfo);
            }
        }
        return fileInfos;
    }

    public class NdrThread implements Runnable {
        private Long facilityId;
        NdrThread(Long facilityId) {
            this.facilityId = facilityId;
        }

        @Override
        public void run() {
            generate(facilityId);
        }
    }

}

