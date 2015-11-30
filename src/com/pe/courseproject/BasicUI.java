package com.pe.courseproject;

/**
 * Created by Nikolay on 27.11.2015 г..
 */

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasicUI extends Application {

    public static final double SCENE_WIDTH = 1000;
    public static final double SCENE_HEIGHT = 700;
    public static final double MIN_WIDTH = 600;
    public static final double MIN_HEIGHT = 450;
    
    private static final String RGB_WHITE = "rgb(255,255,255)";
    private static final String HEX_MID_DARK_GRAY = "#545454";
    private static final String HEX_WHITE = "#FFFFFF";
    
    private static final Font STANDARD_FONT = new Font("Arial", 14);
    private static final Font TITLES_FONT = new Font("Arial", 18);

    private TreeView<String> treeView;
    private Tree charTree;
    
    private TableView<TableEntry> charProbabilityTable;
    private TableColumn<TableEntry, String> characterTableColumn;
    private TableColumn<TableEntry, String> probabilityTableColumn;
    
    private StackPane treePane;
    private ScrollPane fileContentsPane;
    private Text fileContentsAsText;
    private TextField filePath;
    private Button browseButton, actionButton;

    private Stage mainStage;

    private Map<Character,Double> charMap;
    private File file;
    
    public static class TableEntry {
    	private final StringProperty character;
    	private final StringProperty probability;
    	
    	public TableEntry(Character character, String probability) {
    		this.character = new SimpleStringProperty(Character.toString(character));
    		this.probability = new SimpleStringProperty(probability);
    	}
    	
    	public StringProperty characterProperty() {
            return character; 
        }

    	public StringProperty probabilityProperty() {
            return probability;
        }

    	public String getCharacter() {
    		return character.get();
    	}

    	public String getProbability() {
    		return probability.get();
    	}
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        mainStage = primaryStage;
        primaryStage.setTitle("Character Probability Visualizer");
        
        BorderPane layout = new BorderPane();
        
        createFileChooserArea(layout);
        
        createFileContentsDisplayArea(layout);
        
        treePane = new StackPane();//Print here a tree
        layout.setCenter(treePane);

        createCharDisplayArea(layout);//Print here chars and their probability

        
        // We do not want the panes to be fixed in size. If they are, this would mean that
        // when the stage is resized, the panes will remain with their initial sizes and the UI
        // will look broken. Also, dynamic sizing removes the need for specifying measurements and matching
        // the measurements of each element.
        layout.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				double newDoubleValue = newValue.doubleValue();

                //Top Pane
                filePath.setPrefWidth(newDoubleValue * 0.5);

                //Left Pane
                fileContentsPane.setPrefWidth(newDoubleValue * 0.3);
		        fileContentsAsText.setWrappingWidth(newDoubleValue * 0.9 * 0.3);

                //Center Pane
		        treePane.setPrefWidth(newDoubleValue * 0.5);

                //Right Pane
		        double tableWidth = newDoubleValue * 0.2;
		        charProbabilityTable.setPrefWidth(tableWidth);
		        characterTableColumn.setPrefWidth(tableWidth * 0.35);
		        probabilityTableColumn.setPrefWidth(tableWidth * 0.65);
			}
		});
        
        layout.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				fileContentsPane.setPrefHeight(newValue.doubleValue());
				charProbabilityTable.setPrefHeight(newValue.doubleValue());
			}
		});
        
        // Set margins for the scroll pane.
        BorderPane.setMargin(treePane, new Insets(10,15,10,15));
        BorderPane.setMargin(charProbabilityTable, new Insets(10,15,10,15));
        
        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @SuppressWarnings("unchecked")
	private void createCharDisplayArea(BorderPane layout) {
    	Label label = new Label("Probability");
    	label.setFont(TITLES_FONT);
    	label.setTextFill(Color.web(HEX_MID_DARK_GRAY));
    	
    	//FIXME: remove
        charProbabilityTable = new TableView<TableEntry>();
        charProbabilityTable.setEditable(false);
        
        characterTableColumn = new TableColumn<TableEntry, String>("Character");
        characterTableColumn.setCellValueFactory(new PropertyValueFactory<TableEntry, String>("character"));

        probabilityTableColumn = new TableColumn<TableEntry, String>("Probability");
        probabilityTableColumn.setCellValueFactory(new PropertyValueFactory<TableEntry, String>("probability"));
        
        charProbabilityTable.getColumns().addAll(characterTableColumn, probabilityTableColumn);
        
        VBox innerLayout = new VBox();
        innerLayout.setSpacing(5);
        innerLayout.getChildren().addAll(label, charProbabilityTable);
        innerLayout.setPadding(new Insets(10,15,10,15));

        layout.setRight(innerLayout);
    }

    /**
     * Creates a file chooser display, set as the top element of the BorderPane layout.
     * @param layout
     */
    private void createFileChooserArea(BorderPane layout) {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 15, 15, 15));
        hBox.setSpacing(5);
        
        filePath = new TextField();
        filePath.setEditable(false);


        browseButton = new Button("Browse");
        browseButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				openFileChooser();
				updatePaneContents();
			}
		});

        actionButton = new Button("Do some cool stuff");
        actionButton.setOnAction(event -> {
            processData();
            processTableData();
        });

        hBox.getChildren().addAll(filePath, browseButton, actionButton);
        layout.setTop(hBox);
    }
    
    private void processTableData() {
    	if(file != null) {
            DecimalFormat format = new DecimalFormat("0.0000000000");
	    	List<TableEntry> charList = new ArrayList<TableEntry>();
	    	for(Map.Entry<Character, Double> entry : charMap.entrySet()) {
	    		charList.add(new TableEntry(entry.getKey(), format.format(entry.getValue())));
	    	}
	    	
	    	charProbabilityTable.setItems(FXCollections.observableArrayList(charList));
            probabilityTableColumn.setSortType(TableColumn.SortType.DESCENDING);
            charProbabilityTable.getSortOrder().add(probabilityTableColumn);
    	}
    }

    /**
     * Creates a pane that displays the contents of the text file that was selected. The pane is
     * set as the left child of the BorderPane layout.
     */
    private void createFileContentsDisplayArea(BorderPane layout) {
    	Label label = new Label("File Contents");
    	label.setFont(TITLES_FONT);
    	label.setTextFill(Color.web(HEX_MID_DARK_GRAY));
    	//label.setStyle("-fx-font-color: " + HEX_WHITE);
    	// Create a scroll pane to allow for scrolling of long texts.
    	fileContentsPane = new ScrollPane();
        fileContentsPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        // We do not wish to scroll the text horizontally and should disable the property.
        fileContentsPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        //IMPORTANT: This is the only working solution to setting the background color of a scroll pane.
        fileContentsPane.setStyle("-fx-background: " + RGB_WHITE);
        fileContentsPane.setPadding(new Insets(5,10,5,10));
        
        // Create the text element which will contain the file's contents as text information.
        fileContentsAsText = new Text();
        fileContentsAsText.setFont(STANDARD_FONT);
        
        fileContentsPane.setContent(fileContentsAsText);
        
        VBox innerLayout = new VBox();
        innerLayout.setSpacing(5);
        innerLayout.getChildren().addAll(label, fileContentsPane);
        innerLayout.setPadding(new Insets(10,15,10,15));
        
        layout.setLeft(innerLayout);
    }
    
    private void processData() {
        if(file != null) {
            charMap = TextFile.obtainCharactersProbability(file);
            CharacterList list = new CharacterList(charMap);
            charTree = new Tree(list);
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
    	
    	filePath.setText(file.getAbsolutePath());
    	fileContentsAsText.setText(TextFile.readFileContents(file, true));
    	fileContentsPane.setContent(fileContentsAsText);
    }
}

