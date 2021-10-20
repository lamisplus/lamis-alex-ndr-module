package org.lamisplus.modules.ndr.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.ndr.domain.entity.NdrCodeset;
import org.lamisplus.modules.ndr.domain.schema.CodedSimpleType;
import org.lamisplus.modules.ndr.repository.NdrCodesetRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NdrCodesetService {
    private final NdrCodesetRepository ndrCodeSetRepository;

    public CodedSimpleType getCodedSimpleType(String codeSetGroup, String description) {
        CodedSimpleType cst = null;
        Optional<NdrCodeset> ndrCodeSet = this.ndrCodeSetRepository.findByCodesetGroupAndSysDescription(codeSetGroup, description);
        if (ndrCodeSet.isPresent()) {
            cst = new CodedSimpleType();
            cst.setCode(ndrCodeSet.get().getNdrCode());
            cst.setCodeDescTxt(ndrCodeSet.get().getNdrDescription());
        }
        return cst;
    }

    public String getCode(String codeSetGroup, String description) {
        String ndrCode = null;
        Optional<NdrCodeset> ndrCodeSet = this.ndrCodeSetRepository.findByCodesetGroupAndSysDescription(codeSetGroup, description);
        if(ndrCodeSet.isPresent()) {
            ndrCode = ndrCodeSet.get().getNdrCode();
        }
        return ndrCode;
    }
}
