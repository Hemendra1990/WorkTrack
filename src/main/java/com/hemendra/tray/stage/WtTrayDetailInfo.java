package com.hemendra.tray.stage;




import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WtTrayDetailInfo {

    

    public static void launchStage() {
        log.info("launchStage method called");
        if (Platform.isFxApplicationThread()) {
            JavaFXApplication.showStage();
        } else {
            Platform.runLater(JavaFXApplication::showStage);
        }
    }


    
}