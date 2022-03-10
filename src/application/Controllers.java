package application;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controllers implements Initializable {

	static String fileContent = "";


	static String realContent = " ";


	static String pythonOutput = "";
	private static final File FILE = new File(System.getProperty("user.dir") + "/src/pythonOutput.txt");

	File parseFile;
	final static LexicalAnalyzer la = new LexicalAnalyzer(); //Java Token Hashmap initialization
	private int listLength;
	private Object currentObject;


	@FXML
	private void handleButtonAction(ActionEvent event) {

		final DirectoryChooser dirchooser = new DirectoryChooser();

		Stage stage = (Stage) anchor.getScene().getWindow();

		File file = dirchooser.showDialog(stage);

		if(file != null) {
			System.out.println("Path: " + file.getAbsolutePath());
			javaCode.setText(file.getAbsolutePath());
		}
	}

	
	 public void clearButton (ActionEvent event) {
		 
		 if (javaCode.getText().isEmpty() && pythonCode.getText().isEmpty()) {
			 Alert alertEmpty = new Alert(AlertType.CONFIRMATION);
			 alertEmpty.setTitle("NULL");
			 alertEmpty.setHeaderText("Text Area is already empty.");   
			 alertEmpty.show();
			 return;
			 
		 }
		 
		 fileContent = "";
		 realContent = "";
		 
		 javaCode.setText("");
		 pythonCode.setText("");
		 
		 LexicalAnalyzer.getDeletedPyStr();
	 }

	public void fileOpen (ActionEvent event) throws FileNotFoundException {


		Stage stage = (Stage) anchor.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll();
				//new FileChooser.ExtensionFilter("Text Files", "*.txt")

		
		File selectedFile = fileChooser.showOpenDialog(stage);
		parseFile = selectedFile;

		if (selectedFile != null) {

			javaCode.setText(fileReader(selectedFile));
			realContent = fileContent;
			fileContent = " ";

			//System.out.println(realContent);
		}



	}



	public void translateButton (ActionEvent event) throws FileNotFoundException {

		if (javaCode.getText().isEmpty()) {
			Alert alertEmpty = new Alert(AlertType.CONFIRMATION);
			alertEmpty.setTitle("NULL");
			alertEmpty.setHeaderText("Nothing to Translate");
			alertEmpty.show();
			return;
		}

		List<Object> tokens = null;

		try {
			/* Pass strings in the file through James Ring's stream tokenizer to tokenize the strings before analysis
				of the LexicalAnalyzer and PythonConverter classes.
			 */
			tokens = LexicalAnalyzer.streamTokenizer(new FileReader(parseFile));

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(tokens);
		listLength = tokens.size();

		for(int i = 0; i < listLength; i++) {
			currentObject = tokens.get(i);
			if(LexicalAnalyzer.isNumber(currentObject) == false) {
				LexicalAnalyzer.translateToPython(currentObject);
			} else { //If the token is a number
				LexicalAnalyzer.directNumber(currentObject);
			}
		}

		LexicalAnalyzer.printLookAheadFunction();
		LexicalAnalyzer.printTranslator();

		pythonCode.setText(LexicalAnalyzer.getPythonStr());

	}


	public String javaTextArea() {

		if (javaCode.getText().isEmpty()) {
			return null;
		}


		String javatext = "";
		return javatext = javaCode.getText();


	}

	public String pythonTextArea() {

		if (pythonCode.getText().isEmpty()) {
			return null;
		}

		String pythonText = "";

		return pythonText = pythonCode.getText();


	}


	//new save file buttons
	public void saveJavaFile(ActionEvent event) {

		Alert alertEmpty = new Alert(AlertType.CONFIRMATION);
		alertEmpty.setTitle("NULL");
		alertEmpty.setHeaderText("Text area is Empty");

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Succsses");
		alert.setHeaderText("Java file saved.");

		if (javaCode.getText().isEmpty()) {
			alertEmpty.show();
			return;
		}

		Stage stage = (Stage) anchor.getScene().getWindow();
		FileChooser fileChoose = new FileChooser();
		fileChoose.getExtensionFilters().addAll();

		File file = fileChoose.showSaveDialog(stage);

		if (file != null) {
			saveTextFile(javaCode.getText(), file);
			alert.show();
		}

	}

	public void savePythonFile(ActionEvent event) throws FileNotFoundException {
		Alert alertNull = new Alert(AlertType.CONFIRMATION);
		alertNull.setTitle("NULL");
		alertNull.setHeaderText("Text area is Empty");

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Success");
		alert.setHeaderText("Python file saved succesfully");

		if (pythonCode.getText().isEmpty()) {

			alertNull.show();
			return;

		}

		Stage stage = (Stage) anchor.getScene().getWindow();
		FileChooser fileChoose = new FileChooser();
		fileChoose.getExtensionFilters().addAll();

		File file = fileChoose.showSaveDialog(stage);

		if (file != null) {
			saveTextFile(pythonCode.getText(), file);
			alert.show();
		}

	}

	private void saveTextFile(String content, File file) {

		try {
			PrintWriter writer;
			writer = new PrintWriter(file);
			writer.println(content);
			writer.close();
		} catch (IOException ex) {

		}
	}

	/*
	public void createFile (ActionEvent event) {


		fileWriterJava(fileContent);



	}

	public void createPythonFile (ActionEvent event) {

		fileWriterPython(pythonTextArea());


	}
	*/

	public static String fileReader (File file) throws FileNotFoundException {

		Scanner scan = new Scanner(file);

		while(scan.hasNextLine()) {

			fileContent = fileContent.concat(scan.nextLine() + "\n");

		}

		return fileContent;


	}

	
	public void fileWriterJava (String str) {

		String fileName = "translatedJava.java";



		Alert alertEmpty = new Alert(AlertType.CONFIRMATION);
		alertEmpty.setTitle("NULL");
		alertEmpty.setHeaderText("Text area is Empty");

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Succsses");
		alert.setHeaderText("Java file saved.");


		try {

			if (javaCode.getText().isEmpty() ) {

				alertEmpty.show();
				return;

			}


				/*if (str.contains("{") ) {
					PrintWriter outputStream = new PrintWriter(fileName);
					outputStream.println(str);
					outputStream.close();

				 	alert.show();*/


			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println(javaTextArea());
			outputStream.close();

			alert.show();







		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	
	public void fileWriterPython (String str) {

		String fileName = "translatedPython.py";

		Alert alertNull = new Alert(AlertType.CONFIRMATION);
		alertNull.setTitle("NULL");
		alertNull.setHeaderText("Text area is Empty");

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Success");
		alert.setHeaderText("Python file succesfully saved");

		try {

			if (pythonCode.getText().isEmpty() ) {

				alertNull.show();
				return;

			}



			PrintWriter outputStream = new PrintWriter(fileName);
			outputStream.println(pythonTextArea());
			outputStream.close();
			alert.show();




		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	



	//mockup reader for python output
	public static String fileReader2 (File file) throws FileNotFoundException {

		Scanner scan = new Scanner(file);

		while(scan.hasNextLine()) {

			pythonOutput = pythonOutput.concat(scan.nextLine() + "\n");

		}

		return pythonOutput;


	}







	@FXML
	private MenuItem fileOpen;

	@FXML
	private   TextArea javaCode;

	@FXML
	private TextArea pythonCode;

	@FXML
	private AnchorPane anchor;



	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}

