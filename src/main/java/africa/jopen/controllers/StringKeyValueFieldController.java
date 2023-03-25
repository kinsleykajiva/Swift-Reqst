package africa.jopen.controllers;

import africa.jopen.models.FieldState;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.Pair;

import java.net.URL;
import java.util.ResourceBundle;

public class StringKeyValueFieldController implements Initializable {



    @FXML
    private TextField keyField, valueField;
    @FXML
    private JFXCheckBox checkBox;
    @FXML
    protected JFXButton deleteButton;
    private boolean uncheckedAlready = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkBox.disableProperty()
                .bind(Bindings.or(keyField.textProperty().isEmpty(), valueField.textProperty().isEmpty()));
        checkBox.disableProperty()
                .addListener(observable -> {
                    if (isChecked() && (keyField.getText().equals("") || valueField.getText().equals(""))) {
                        checkBox.setSelected(false);
                        uncheckedAlready = false;
                    }
                });
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == true && newValue == false)
                uncheckedAlready = true;
        });
        valueField.textProperty()
                .addListener(observable -> {
                    if (!keyField.getText().equals("") && !valueField.getText().equals("") && !uncheckedAlready)
                        checkBox.selectedProperty().set(true);
                });
        keyField.textProperty()
                .addListener(observable -> {
                    if (!keyField.getText().equals("") && !valueField.getText().equals("") && !uncheckedAlready)
                        checkBox.selectedProperty().set(true);
                });

    }

    public Pair<String, String> getHeader() {
        return new Pair<>(keyField.getText(), valueField.getText());
    }

    public boolean isChecked() {
        return checkBox.isSelected();
    }

    public void setKeyField(String key) {
        keyField.setText(key);
    }

    public void setValueField(String value) {
        valueField.setText(value);
    }

    public boolean isKeyFieldEmpty() {
        return keyField.getText().isEmpty();
    }

    public boolean isValueFieldEmpty() {
        return valueField.getText().isEmpty();
    }

    public FieldState getState() {
        return new FieldState(keyField.getText(), valueField.getText(), checkBox.isSelected());
    }

    public void setChecked(boolean checked) {
        checkBox.setSelected(checked);
    }

    public void setKeyHandler(EventHandler<KeyEvent> handler) {
        keyField.setOnKeyPressed(handler);
        valueField.setOnKeyPressed(handler);
    }

    public BooleanProperty getSelectedProperty() {
        return checkBox.selectedProperty();
    }

    public StringProperty getKeyProperty() {
        return keyField.textProperty();
    }

    public StringProperty getValueProperty() {
        return valueField.textProperty();
    }
}
