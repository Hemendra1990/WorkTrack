package com.hemendra.activity.systemreset.factory;

import com.hemendra.activity.systemreset.SystemResetListener;
import com.hemendra.activity.systemreset.impl.LinuxSystemResetListener;
import com.hemendra.activity.systemreset.impl.MacOsSystemResetListener;
import com.hemendra.activity.systemreset.impl.WindowsSystemResetListener;
import com.hemendra.util.BeanUtils;
import com.hemendra.util.WorkTrackUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemResetEventListenerFactory implements InitializingBean {
    private final WorkTrackUtils workTrackUtils;

    private String os;

    @Override
    public void afterPropertiesSet() {
        os = workTrackUtils.getOsName();
    }

    public SystemResetListener getOsSpecificSystemResetListener() {
        if (os.contains("win")) {
            return BeanUtils.getBean(WindowsSystemResetListener.class);
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return BeanUtils.getBean(LinuxSystemResetListener.class);
        } else if (os.contains("mac")) {
            return BeanUtils.getBean(MacOsSystemResetListener.class);
        } else {
            log.error("OS not supported.");
            return null;
        }
    }
}
