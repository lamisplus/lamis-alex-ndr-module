package org.lamisplus.modules.sync.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueManager {

    private final ObjectDeserializer objectDeserializer;
    public void queue(String data, String table, Long facilityId) throws IOException {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss.ms");

        String folder = ("sync/").concat(Long.toString(facilityId).concat("/"));
        String fileName = dateFormat.format(date) + "_" + timeFormat.format(date) + ".json";
        File file = new File(folder.concat(fileName));

        FileUtils.writeStringToFile(file, data, Charset.defaultCharset());
        System.out.println("table : "+  table);
        System.out.println("data : "+  data);
        objectDeserializer.deserialize(data, table);
    }

    public void process(String data, String table) {
        objectDeserializer.deserialize(data, table);
    }
}
