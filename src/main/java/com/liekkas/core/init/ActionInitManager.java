package com.liekkas.core.init;

import com.liekkas.core.action.BaseAction;
import com.liekkas.core.message.Path;
import com.liekkas.core.ActionInvoker;
import com.liekkas.core.InitManager;
import com.liekkas.core.MessageDispatcher;
import com.liekkas.core.RpcInvoker;

import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class ActionInitManager extends ApplicationObjectSupport implements InitManager {

    @Override
    public void init() {
        String[] allBeanNames = getApplicationContext().getBeanDefinitionNames();
        for (String beanName : allBeanNames) {
            Object bean = getApplicationContext().getBean(beanName);
            if (bean instanceof BaseAction) {
                Class clazz = bean.getClass();
                for (Method method : clazz.getDeclaredMethods()) {
                    Path path = method.getAnnotation(Path.class);
                    if (path != null) {
                        ActionInvoker actionInvoker;
                        if (path.rpc()) {
                            actionInvoker = new ActionInvoker(method, bean);
                        } else {
                            actionInvoker = new RpcInvoker(method, bean);
                        }
                        MessageDispatcher.commandHandleMap.put(path.value(), actionInvoker);
                    }
                }
            }
        }


    }
}
