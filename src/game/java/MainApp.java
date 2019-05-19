/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.java;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import net.java.ConnectController;

public class MainApp extends Application {
    
//    @Override
//    public void start(Stage primary_stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("Scene.fxml"));
//        Scene scene = new Scene(root);
//        scene.getStylesheets().add(getClass().getResource("Scene.css").toExternalForm());
//        primary_stage.setTitle("JavaFX");
//        primary_stage.setScene(scene);
//        primary_stage.show();
//    }

    /**
     * @param args the command line arguments
     */
    private Group root = new Group(); 
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(createContent()));
        primaryStage.show(); 
    }
    
    public static void main(String[] args) {
        Application.launch(args);
    }

    private Parent createContent() {
        gotoGameScene(); 
        return root; 
    }

    private void gotoGameScene() {
        try {
            SceneController gameScene = 
                    (SceneController)replaceSceneContent("/game/resources/Scene.fxml");
            gameScene.setApp(this);
        } catch(Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex); 
        } 
    }
    public boolean userChoseRemote(boolean ok_selected) {
        if (ok_selected) {
            gotoNetworkedScene();
            return true; 
        } else 
        {
            return false; 
        }
    }
    
    private void gotoNetworkedScene() {
        try {
            ConnectController networkedScene = 
                    (ConnectController)replaceSceneContent("/net/resources/Connect.fxml");
            networkedScene.setApp(this);
        } catch(Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex); 
        } 
    }
    
    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = MainApp.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(MainApp.class.getResource(fxml));
        URL url = MainApp.class.getResource(fxml); 
        System.out.println("url3333" + url);
        
        BorderPane page; 
        try {
            page = (BorderPane)loader.load(in);
        } finally {
            in.close();
        }
        root.getChildren().clear(); 
        root.getChildren().add(page); 
        return (Initializable)loader.getController(); 
    }
}
