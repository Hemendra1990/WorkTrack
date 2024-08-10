package com.hemendra.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TextComp {

    @Value("${wt.app-name: Not Working}")
    private String text;

    public String getText() {
        return text;
    }

}
