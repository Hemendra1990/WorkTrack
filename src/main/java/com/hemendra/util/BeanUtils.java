package com.hemendra.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author : Hemendra Sethi
 * @Date : 10/08/2024
 */
@Component
public class BeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtils.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName) {
        return BeanUtils.applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return BeanUtils.applicationContext.getBean(clazz);
    }
}
