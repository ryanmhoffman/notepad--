package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application {

	private File filePath = null;
	private TextArea textArea;

    // TODO: Refactor this into smaller methods.
    @Override
    public void start(Stage primaryStage) throws Exception{
		BorderPane root = new BorderPane();
        primaryStage.setTitle("Notepad--");
		primaryStage.getIcons()
                .add(new Image(Main.class.getResourceAsStream("icon.png")));

        Scene scene = new Scene(root, 675, 500);

        // The menu bar where dropdown menus and options will be added.
		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		root.setTop(menuBar);

		root.setCenter(createTextArea(primaryStage));

		// About menu
		Menu about = new Menu("About");
		MenuItem aboutItem = new MenuItem("About Notepad--");
		aboutItem.setOnAction(actionEvent -> about());
		about.getItems().addAll(aboutItem);

		menuBar.getMenus().addAll(createFileMenu(primaryStage), about);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextArea createTextArea(Stage primaryStage){
        // Create the text area for input and typing.
        textArea = new TextArea();
        textArea.prefWidthProperty().bind(primaryStage.widthProperty());
        textArea.prefHeightProperty().bind(primaryStage.heightProperty());
        textArea.setWrapText(true);
        return textArea
    }
    private Menu createFileMenu(Stage primaryStage){
        // File menu
		Menu file = new Menu("File");
		MenuItem newItem = new MenuItem("New");
		newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, 
                                KeyCombination.SHORTCUT_DOWN));
		newItem.setOnAction(actionEvent -> {
			try {
				runAnotherApp(Main.class);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
        // Create the menu item to open a new file.
        MenuItem openItem = new MenuItem("Open");
		openItem.setAccelerator(new KeyCodeCombination(KeyCode.O, 
                                KeyCombination.SHORTCUT_DOWN));
		openItem.setOnAction(actionEvent -> openFile(primaryStage));
		MenuItem saveItem = new MenuItem("Save");
		saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, 
                                KeyCombination.SHORTCUT_DOWN));
		saveItem.setOnAction(actionEvent -> {
			if(filePath != null){
				saveFile(textArea.getText(), filePath);
			}
		});
        // Create the menu item to save a currently unsaved file.
		MenuItem saveAsItem = new MenuItem("Save As");
		saveAsItem.setOnAction(actionEvent -> saveAs(primaryStage));
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setAccelerator(new KeyCodeCombination(KeyCode.Q, 
                                KeyCombination.SHORTCUT_DOWN));
		exitItem.setOnAction(actionEvent -> Platform.exit());
		file.getItems().addAll(newItem, openItem, saveItem, saveAsItem, exitItem);

        return file;
    }

    public static void main(String[] args) {
        launch(args);
    }

	private void about(){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("About Notepad--");
		alert.setHeaderText("Notepad--");
		alert.setContentText("Notepad-- is a cross-platform (near) clone of " + 
                        "the Windows only program Notepad. " +
		                "It was developed mainly so I could have the most " + 
                        "basic text editor ever on my Mac and Linux machines.");
		alert.showAndWait();
	}

    // Save a new, previously unsaved file.
    // The user is required to provide a file name, extension, and path.
	private void saveAs(Stage stage){
		FileChooser fileChooser = new FileChooser();
		filePath = fileChooser.showSaveDialog(stage);
		if(filePath != null){
			saveFile(textArea.getText(), filePath);
			stage.setTitle(filePath.toString());
		}
	}

    // Open a file explorer (Finder on Mac) to select a file to open. 
	private void openFile(Stage stage){
		FileChooser fileChooser = new FileChooser();
		filePath = fileChooser.showOpenDialog(stage);
		if(filePath != null){
			textArea.setText(readFile(filePath));
			stage.setTitle(filePath.toString());
		}
	}

    // Open another instance of Notepad--. 
    // This is tied to the current instance, so if one is closed,
    // they both get closed. I plan to fix this in the future so
    // they will be separate instances.
	private void runAnotherApp(Class<? extends Application> anotherAppClass) 
                    throws Exception {
		Application newApp = anotherAppClass.newInstance();
		Stage newStage = new Stage();
		newApp.start(newStage);
	}

    // Save the current file.
    // Only works if it has been previously saved, for example, saving
    // changes.
	private void saveFile(String content, File file){
		try {
			FileWriter fileWriter;
			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    // Read the contents of a file into the editor pane so the user
    // can modify them as plain text. 
	private String readFile(File file){
		StringBuilder stringBuffer = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String text;
			while ((text = bufferedReader.readLine()) != null) {
				stringBuffer.append(text).append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				assert bufferedReader != null;
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return stringBuffer.toString();
	}
}
