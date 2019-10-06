package ru.vtarasov.mp.student;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Startup;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.inject.Singleton;

/**
 * @author vtarasov
 * @since 07.10.2019
 */
public class StartupExtension implements Extension {

    private List<Bean<?>> eagerBeansList = new ArrayList<Bean<?>>();

    public <T> void collect(@Observes ProcessBean<T> event) {
        if (event.getAnnotated().isAnnotationPresent(Startup.class)
            && event.getAnnotated().isAnnotationPresent(Singleton.class)) {
            eagerBeansList.add(event.getBean());
        }
    }

    public void load(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
        for (Bean<?> bean : eagerBeansList) {
            beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)).toString();
        }
    }
}
