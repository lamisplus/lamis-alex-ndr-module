package org.lamisplus.modules.ndr.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"org.lamisplus.modules.ndr.repository", "org.lamisplus.modules.base.repository"})
public class DomainConfiguration {
}
