

/**
 * Created by Nikolay on 27.11.2015 г..
 */

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Map;

public class BasicUI extends Application {

    public static final double SCENE_WIDTH = 1000;
    public static final double SCENE_HEIGHT = 700;
    
    private static final String COLOR_WHITE = "rgb(255,255,255)";

    private TreeView<String> tree;
    private VBox charProbabilityPane;
    private StackPane treePane;
    private ScrollPane fileContentsPane;
    private Text fileContentsAsText;
    private TextField path;

    private Stage mainStage;

    private Map<Character,Double> charMap;
    private File file;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        primaryStage.setTitle("Character Probability Visualizer");
        
        BorderPane layout = new BorderPane();
        layout.setStyle("-fx-background-color: #91a67c");
        
        createFileChooserArea(layout);
        
        createFileContentsDisplayArea(layout);
        
        treePane = new StackPane();//Print here a tree
        layout.setCenter(treePane);
        
        charProbabilityPane = new VBox();//Print here chars and their probability
        layout.setRight(charProbabilityPane);
        
        // We do not want the panes to be fixed in size. If they are, this would mean that
        // when the stage is resized, the panes will remain with their initial sizes and the UI
        // will look broken. Also, dynamic sizing removes the need for specifying measurements and matching
        // the measurements of each element.
        layout.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				double newContentsPaneWidth = newValue.doubleValue() * 0.3;
				fileContentsPane.setPrefWidth(newContentsPaneWidth);
		        fileContentsAsText.setWrappingWidth(newContentsPaneWidth * 0.9);
		        
		        treePane.setPrefWidth(newContentsPaneWidth);
		        
		        charProbabilityPane.setPrefWidth(newContentsPaneWidth);
			}
		});
        
        // Set margins for the scroll pane.
        BorderPane.setMargin(fileContentsPane, new Insets(10,15,10,15));
        BorderPane.setMargin(treePane, new Insets(10,15,10,15));
        
        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    /**
     * Creates a file chooser display, set as the top element of the BorderPane layout.
     * @param layout
     */
    private void createFileChooserArea(BorderPane layout) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 15, 15, 15));
        hBox.setSpacing(5);
        
        path = new TextField();
        path.setEditable(false);

        Button browseButton = new Button("Browse");
        browseButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				openFileChooser();
				updatePaneContents();
			}
		});

        Button actionButton = new Button("Do some cool stuff");
        actionButton.setOnAction(event -> processData());

        hBox.getChildren().addAll(path, browseButton, actionButton);
        layout.setTop(hBox);
    }
    
    /**
     * Creates a pane that displays the contents of the text file that was selected. The pane is
     * set as the left child of the BorderPane layout.
     */
    private void createFileContentsDisplayArea(BorderPane layout) {
    	// Create a scroll pane to allow for scrolling of long texts.
    	fileContentsPane = new ScrollPane();
        fileContentsPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        // We do not wish to scroll the text horizontally and should disable the property.
        fileContentsPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        //IMPORTANT: This is the only working solution to setting the background color of a scroll pane.
        fileContentsPane.setStyle("-fx-background: " + COLOR_WHITE);
        fileContentsPane.setPadding(new Insets(5,10,5,10));
        
        // Create the text element which will contain the file's contents as text information.
        fileContentsAsText = new Text();
        fileContentsAsText.setFont(new Font("Arial", 14));
        
        fileContentsPane.setContent(fileContentsAsText);
        layout.setLeft(fileContentsPane);
    }


    private void processData() {
        if(file != null) {
            charMap = TextFile.obtainCharactersProbability(file);
            CharacterList list = new CharacterList(charMap);
            try {
                System.out.println(list.getString(0,list.size()-1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        //Configure FileChooser filter
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text Documents", "*.txt"),
                new FileChooser.ExtensionFilter("All files (not recommended)", "*.*")
        );

        file = fileChooser.showOpenDialog(mainStage);
    }
    
    /**
     * Updates the contents of the panes to reflect the acquired probability data from the file's textual contents.
     */
    private void updatePaneContents() {
    	if(file == null) {
    		return;
        }
    	
    	path.setText(file.getAbsolutePath());
    	fileContentsAsText.setText(TextFile.readFileContents(file, true));
    	fileContentsPane.setContent(fileContentsAsText);
    }
}

