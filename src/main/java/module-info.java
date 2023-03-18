module   africa.jopen  {
    requires javafx.graphics;
    requires java.sql;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires javafx.controls;
    requires com.jfoenix;


    requires jakarta.ws.rs;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires reactfx;
    exports africa.jopen to  javafx.graphics;




    opens africa.jopen to javafx.fxml, javafx.graphics, javafx.base;
    opens africa.jopen.controllers to javafx.fxml, javafx.graphics, javafx.base;
    exports africa.jopen.controllers to javafx.graphics, javafx.fxml, javafx.base;



}