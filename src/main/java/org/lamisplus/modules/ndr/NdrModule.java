package org.lamisplus.modules.ndr;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.context.configurer.ComponentScanConfigurer;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.web.AcrossWebModule;
import org.lamisplus.modules.base.BaseModule;
import org.lamisplus.modules.bootstrap.BootstrapModule;

@AcrossApplication(
        modules = {
                AcrossHibernateJpaModule.NAME,
                AcrossWebModule.NAME//,BaseModule.NAME
        }//, modulePackageClasses = {BaseModule.class}
        )
public class NdrModule extends AcrossModule {
    public static final String NAME = "NdrModule";

    public NdrModule() {
        super();
        addApplicationContextConfigurer(new ComponentScanConfigurer(
                getClass().getPackage().getName() +".config",
                getClass().getPackage().getName() +".controller",
                getClass().getPackage().getName() +".domain",
                getClass().getPackage().getName() +".repository",
                getClass().getPackage().getName() +".scheduler",
                getClass().getPackage().getName() +".service",
                getClass().getPackage().getName() +".util",
                getClass().getPackage().getName() +".domain.mappers"));
    }

    public String getName() {
        return NAME;
    }
}