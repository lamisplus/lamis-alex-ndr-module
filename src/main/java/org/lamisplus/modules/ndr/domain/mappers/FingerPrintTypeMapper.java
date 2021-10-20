package org.lamisplus.modules.ndr.domain.mappers;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.domain.schema.FingerPrintType;
import org.lamisplus.modules.ndr.domain.schema.LeftHandType;
import org.lamisplus.modules.ndr.domain.schema.RightHandType;
import org.lamisplus.modules.base.repository.BiometricRepository;
import org.lamisplus.modules.base.domain.entity.Patient;
import org.lamisplus.modules.base.domain.entity.Biometric;

import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FingerPrintTypeMapper {
    private final BiometricRepository biometricRepository;

    public FingerPrintType map(Patient patient) {
        Integer fingerprintQuality = null;
        FingerPrintType fingerPrintType = new FingerPrintType();
        LeftHandType leftHandType = new LeftHandType();
        RightHandType rightHandType = new RightHandType();
        try {
            List<Biometric> biometricList = biometricRepository.findBiometricByPatient(patient);
            biometricList.forEach(biometric -> {
                if(biometric.getBiometricType().equals("Left Thumb")) {
                    //byte[] encoded = Base64Utils.encode(biometric.getTemplate());
                    leftHandType.setLeftThumb(new String(Base64Utils.encode(biometric.getTemplate())));
                    leftHandType.setLeftThumbQuality(fingerprintQuality);
                }
                if(biometric.getBiometricType().equals("Left Index Finger")) {
                    leftHandType.setLeftIndex(new String(Base64Utils.encode(biometric.getTemplate())));
                    leftHandType.setLeftIndexQuality(fingerprintQuality);
                }
                if(biometric.getBiometricType().equals("Left Middle Finger")) {
                    leftHandType.setLeftMiddle(new String(Base64Utils.encode(biometric.getTemplate())));
                    leftHandType.setLeftMiddleQuality(fingerprintQuality);
                }

                if(biometric.getBiometricType().equals("Right Thumb")) {
                    rightHandType.setRightThumb(new String(Base64Utils.encode(biometric.getTemplate())));
                    rightHandType.setRightThumbQuality(fingerprintQuality);
                }
                if(biometric.getBiometricType().equals("Right Index Finger")) {
                    rightHandType.setRightIndex(new String(Base64Utils.encode(biometric.getTemplate())));
                    rightHandType.setRightIndexQuality(fingerprintQuality);
                }
                if(biometric.getBiometricType().equals("Right Middle Finger")) {
                    rightHandType.setRightMiddle(new String(Base64Utils.encode(biometric.getTemplate())));
                    rightHandType.setRightMiddleQuality(fingerprintQuality);
                }

                try {
                    fingerPrintType.setDateCaptured(DatatypeFactory.newInstance().newXMLGregorianCalendar(String.valueOf(biometric.getDateEnrollment())));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
            });
            fingerPrintType.setLeftHand(leftHandType);
            fingerPrintType.setRightHand(rightHandType);
        }
        catch(NullPointerException  e){
            e.printStackTrace();
        }
        return fingerPrintType;
    }
}
