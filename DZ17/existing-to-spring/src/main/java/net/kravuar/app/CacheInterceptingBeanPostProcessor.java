package net.kravuar.app;

import net.kravuar.cache.annotations.Cached;
import net.kravuar.cache.proxy.CacheProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
class CacheInterceptingBeanPostProcessor implements BeanPostProcessor {
    private final CacheProxyFactory cacheProxyFactory;

    @Lazy
    CacheInterceptingBeanPostProcessor(CacheProxyFactory cacheProxyFactory) {
        this.cacheProxyFactory = cacheProxyFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        for (Method method : beanClass.getDeclaredMethods()) {
            Cached cached = AnnotationUtils.findAnnotation(method, Cached.class);
            if (cached != null) { // If any method marked with Cached, proxy it
                bean = cacheProxyFactory.cache(bean, beanClass.getClassLoader());
                break;
            }
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }
}
