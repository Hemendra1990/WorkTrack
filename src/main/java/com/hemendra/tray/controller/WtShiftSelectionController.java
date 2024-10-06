package com.hemendra.tray.controller;

import com.hemendra.activity.systemreset.constant.Shift;
import com.hemendra.util.WtFxUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Component
public class WtShiftSelectionController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label loggedAsLabel;

    @FXML
    private Label loggedAtLabel;

    @FXML
    private ComboBox<Shift> shiftComboBox;

    @FXML
    private Label qualifiedShiftLabel;

    @FXML
    private Button okButton;

    private Stage stage;
    private Consumer<Shift> shiftUpdateCallback;

    @FXML
    private void initialize() {
        // Set welcome message
        welcomeLabel.setText("Welcome " + WtFxUtils.getUserName());

        // Set logged as email
        loggedAsLabel.setText(WtFxUtils.getUserEmail());

        // Set logged at time
        LocalDateTime now = LocalDateTime.now();
        loggedAtLabel.setText(now.format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a")));

        // Populate shift combo box
        shiftComboBox.getItems().addAll(Shift.values());

        // Customize ComboBox to display shift names
        customizeShiftComboBox();

        // Set current date for qualified shift
        qualifiedShiftLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " - Qualified shift as per rule");

        // Set up listener for shift selection
        shiftComboBox.setOnAction(event -> updateQualifiedShift());

        // Set up OK button action
        okButton.setOnAction(event -> handleOkButton());
    }

    private void customizeShiftComboBox() {
        shiftComboBox.setCellFactory(param -> new ListCell<Shift>() {
            @Override
            protected void updateItem(Shift item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });

        shiftComboBox.setButtonCell(new ListCell<Shift>() {
            @Override
            protected void updateItem(Shift item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });

        shiftComboBox.setConverter(new StringConverter<Shift>() {
            @Override
            public String toString(Shift shift) {
                return shift == null ? "" : shift.getDisplayName();
            }

            @Override
            public Shift fromString(String string) {
                return shiftComboBox.getItems().stream()
                        .filter(shift -> shift.getDisplayName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    private void updateQualifiedShift() {
        Shift selectedShift = shiftComboBox.getValue();
        if (selectedShift != null) {
            qualifiedShiftLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) +
                    " - Qualified shift as per rule: " + selectedShift.getDisplayName());
        }
    }

    private void handleOkButton() {
        Shift selectedShift = shiftComboBox.getValue();
        if (selectedShift != null) {

            if (shiftUpdateCallback != null) {
                shiftUpdateCallback.accept(selectedShift);
            }

            // Close the stage
            if (stage != null) {
                stage.close();
            }
        } else {
            qualifiedShiftLabel.setText("Please select a shift before confirming");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setShiftUpdateCallback(Consumer<Shift> shiftUpdateCallback) {
        this.shiftUpdateCallback = shiftUpdateCallback;
    }
}
