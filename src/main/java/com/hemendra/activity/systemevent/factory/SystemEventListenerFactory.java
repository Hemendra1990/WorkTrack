package com.hemendra.activity.systemevent.factory;

import com.hemendra.activity.systemevent.SystemEventListener;
import com.hemendra.activity.systemevent.impl.LinuxSystemEventListener;
import com.hemendra.activity.systemevent.impl.MacOsSystemEventListener;
import com.hemendra.activity.systemevent.impl.WindowsSystemEventListener;
import com.hemendra.util.BeanUtils;
import com.hemendra.util.WorkTrackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemEventListenerFactory implements InitializingBean {
    private final WorkTrackUtils workTrackUtils;

    private String os;

    @Override
    public void afterPropertiesSet() throws Exception {
        os = workTrackUtils.getOsName();
    }

    public SystemEventListener getOsSpecificSystemEventListener() {
        if (os.contains("win")) {
            return BeanUtils.getBean(WindowsSystemEventListener.class);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return BeanUtils.getBean(LinuxSystemEventListener.class);
        } else if (os.contains("mac")) {
            return BeanUtils.getBean(MacOsSystemEventListener.class);
        } else {
            log.error("OS not supported.");
            return null;
        }
    }
}
