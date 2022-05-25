package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.*;

public class LoggedInController{
	@FXML
	private Button logInButton, regButton;
	@FXML
	private TextField userField, passField;
	@FXML
	private Label labelId;
	
	private FileWriter acc;
	private FileReader test;
	private BufferedReader br;
	private String accDetails;
	private Stage stage;
	private Scene scene;
	private Parent root;
	private Alert alert;
	
	
	
	public void log(ActionEvent event) throws IOException
	{
		test = new FileReader("accData.txt");
		br = new BufferedReader(test);
		int contor = 0;
		int logareEfectuata = 0;
		while((accDetails = br.readLine()) != null)
		{
			if((contor % 2) == 0) {
				if(userField.getText().equals(accDetails))
				{
					logareEfectuata = 1;
				}
				else
					logareEfectuata = 0;
			}
			else
			{
				if(passField.getText().equals(accDetails))
					logareEfectuata = 1;
				else
					logareEfectuata = 0;
			}
			contor = contor + 1;
		}
		if (logareEfectuata == 1) {
			root = FXMLLoader.load(getClass().getResource("Scene.fxml"));
			stage = (Stage)((Node)event.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
		}
		else {
			alert = new Alert(AlertType.WARNING);
			alert.showAndWait();
		}
		br.close();
	}
	
	public void register(ActionEvent event) throws IOException
	{
		acc = new FileWriter("accData.txt");
		PrintWriter pw = new PrintWriter(acc);
		pw.println(userField.getText());
		pw.println(passField.getText());
		//acc.write(passField.getText());
		System.out.print(userField.getText());
		pw.close();
		
	}
}
