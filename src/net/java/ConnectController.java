/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.java;

import game.java.CaroHub;
import game.java.CaroState;
import game.java.MainApp;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class ConnectController implements Initializable {

    @FXML
    private String DEFAULT_PORT;
    @FXML
    private TextField hostInput;
    @FXML
    private TextField connectPortInput;
    @FXML
    private RadioButton serverMode;
    @FXML
    private RadioButton clientMode;
    @FXML
    private TextField listeningPortInput;
    private MainApp application;

    //private boolean openAsServer = serverMode.isSelected(); 
    @FXML
    private Label message;
    private CaroState state;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listeningPortInput.setDisable(false);
        hostInput.setDisable(true);
        connectPortInput.setDisable(true);
        listeningPortInput.setEditable(true);
        hostInput.setEditable(false);
        connectPortInput.setEditable(false);
        // TODO
    }

    @FXML
    private void selectServerMode(ActionEvent event) {
        listeningPortInput.setDisable(false);
        hostInput.setDisable(true);
        connectPortInput.setDisable(true);
        listeningPortInput.setEditable(true);
        hostInput.setEditable(false);
        connectPortInput.setEditable(false);
    }

    @FXML
    private void selectClientMode(ActionEvent event) {
        listeningPortInput.setDisable(true);
        hostInput.setDisable(false);
        connectPortInput.setDisable(false);
        listeningPortInput.setEditable(false);
        hostInput.setEditable(true);
        connectPortInput.setEditable(true);
    }

    @FXML
    private void cancel(ActionEvent event) {
        if (application == null) {
        } else {
            application.userCancelRemote(true);
        }
    }

    private void errorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.showAndWait();
    }

    @FXML
    private void doOK(ActionEvent event) {
        boolean openAsServer = serverMode.isSelected();
        if (openAsServer) {
            int port;
            try {
                port = Integer.parseInt(listeningPortInput.getText().trim());
                System.out.println("port3333" + port);
                if (port <= 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                errorMessage("The value in the \"Listen on port\" box \nis not a legal positive Integer!");
                message.setText("Illegal port number. Please try again!");
                listeningPortInput.selectAll();
                listeningPortInput.requestFocus();
                return;
            }
            Hub hub;
            try {
                hub = new CaroHub(port);
            } catch (Exception e) {
                errorMessage("Sorry, could not listen on port number " + port);
                message.setText("Please try a different port number.");
                listeningPortInput.selectAll();
                listeningPortInput.requestFocus();
                return;
            }
            if (application == null) {
            } else {
                application.beClient = true;
                application.hubName = "localhost";
                application.hubPort = port;
                application.userPlayingRemote(true);
                System.out.println("CC " + "hubName " + application.hubName);
                System.out.println("CC " + "hubport " + application.hubPort);
                System.out.println("CC " + "hubisListen" + application.beClient);

            }
        } else {
            String host;
            int port;
            host = hostInput.getText().trim();
            if (host.length() == 0) {
                errorMessage("You must enter the name or IP address of\n the computer that hosting the game.");
                message.setText("You must enter the computer name");
                hostInput.requestFocus();
                return;

            }
            try {
                port = Integer.parseInt(connectPortInput.getText().trim());
                if (port <= 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                errorMessage("The value in the \"Port Number\" box\nis not a legal positive integer!");
                message.setText("Illegal port number.  Please try again!");
                connectPortInput.selectAll();
                connectPortInput.requestFocus();
                return;
            }
            if (application == null) {
            } else {
                application.beClient = true;
                application.hubName = host;
                application.hubPort = port;
                application.userPlayingRemote(true);

            }
        }
    }

    public void setApp(MainApp application) {
        this.application = application;
    }

}
