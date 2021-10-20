package org.lamisplus.modules.ndr;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.web.AcrossWebModule;
import org.lamisplus.modules.base.BaseModule;
import org.lamisplus.modules.bootstrap.BootstrapModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@AcrossApplication(
		modules = {
				AcrossHibernateJpaModule.NAME,
				AcrossWebModule.NAME, NdrModule.NAME//,BaseModule.NAME
		}//,modulePackageClasses = {BaseModule.class}
		)
public class NdrApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(NdrApplication.class, args);
	}
}