module WorkTrack {
    requires com.github.kwhat.jnativehook;
    requires java.desktop;
    requires java.logging;
    requires java.net.http;
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires com.sun.jna;
    requires com.sun.jna.platform;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires static lombok;

    exports com.hemendra;
    exports com.hemendra.activity;
    exports com.hemendra.activity.apptracker;
    exports com.hemendra.activity.apptracker.browser;
    exports com.hemendra.activity.apptracker.screenshot;
    exports com.hemendra.activity.apptracker.trackerimpl;
    exports com.hemendra.component;
    exports com.hemendra.config;
    exports com.hemendra.dto;
    exports com.hemendra.enums;
    exports com.hemendra.http;
    exports com.hemendra.tray;
    exports com.hemendra.tray.controller;
    exports com.hemendra.tray.stage;
    exports com.hemendra.listener;

    //opens com.hemendra.config to spring.core;
    opens com.hemendra;
    opens com.hemendra.activity;
    opens com.hemendra.activity.apptracker;
    opens com.hemendra.activity.apptracker.browser;
    opens com.hemendra.activity.apptracker.screenshot;
    opens com.hemendra.activity.apptracker.trackerimpl;
    opens com.hemendra.activity.apptracker.trackerimpl.factory;
    opens com.hemendra.component;
    opens com.hemendra.config;
    opens com.hemendra.dto;
    opens com.hemendra.enums;
    opens com.hemendra.http;
    opens com.hemendra.tray;
    opens com.hemendra.tray.controller;
    opens com.hemendra.tray.stage;
    opens com.hemendra.listener;
    opens com.hemendra.util;

}