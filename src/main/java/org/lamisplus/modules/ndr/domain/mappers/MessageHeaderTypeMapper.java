package org.lamisplus.modules.ndr.domain.mappers;

import org.apache.commons.lang3.RandomStringUtils;
import org.lamisplus.modules.ndr.domain.entity.NdrMessage;
import org.lamisplus.modules.ndr.domain.schema.FacilityType;
import org.lamisplus.modules.ndr.domain.schema.MessageHeaderType;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MessageHeaderTypeMapper {
    public MessageHeaderType messageHeaderType(NdrMessage ndrMessage)  {
        MessageHeaderType header = new MessageHeaderType();
        try {
            //Set the Header Information
            header.setMessageCreationDateTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date())));
            header.setMessageSchemaVersion(new BigDecimal("1.6"));
            header.setMessageUniqueID(RandomStringUtils.randomAlphanumeric(10));

            String messageCode = ndrMessage.getDateLastGenerated() == null ? "INITIAL" : "UPDATED";
            header.setMessageStatusCode(messageCode);

            //Set the Sending Organization in the Header
            //In this scenario we are using a an IP
            FacilityType sendingOrganization = new FacilityType();
            sendingOrganization.setFacilityName("Family Health International (360)");
            sendingOrganization.setFacilityID("FHI360");
            sendingOrganization.setFacilityTypeCode("IP");
            header.setMessageSendingOrganization(sendingOrganization);
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        return header;
    }
}
