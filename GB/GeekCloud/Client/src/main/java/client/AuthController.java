package client;

import common.AbstractMessage;
import common.AuthMessage;
import common.AuthOk;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AuthController implements Initializable {
    public TextField Login;

    public TextField Password;

    public Button Enter;
    public AnchorPane fxWindow;
    String login;
    String password;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        pressOnEnterBtn();
        Thread t=new Thread(()->{
            try {

                AbstractMessage am = Network.readObject();
                if (am instanceof AuthOk) {

                    Platform.runLater(this::fxWindow);

                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        });
        t.start();

        }

    @FXML
    public void fxWindow() {
        try{
        Stage stage=(Stage) fxWindow.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("DropBox");
        Scene scene = new Scene(root);
        stage.setScene(scene);

    }catch (IOException e){
            e.printStackTrace();
        }

}

    public void pressOnEnterBtn() {
        Enter.setOnMouseClicked(b->{
           if (b.getClickCount()==1) {
               login=Login.getText();
               password=Password.getText();
                try{
                  Network.sendMsg(new AuthMessage(login, password));
                  Login.clear();
                  Password.clear();
            }catch (Exception e){
                e.printStackTrace();
                }}});}
        }


