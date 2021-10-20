package org.lamisplus.modules.ndr.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class NdrMessageScheduler {
    private final NdrMessageHandler ndrMessageHandler;
    //repeat NDR message generation every 6 hours  "0 0 0/6 * * ?"
    @Scheduled(cron="0 0/5 * * * ?")
    public void job() throws Exception {
        //ndrMessageHandler.handle();
    }

}
