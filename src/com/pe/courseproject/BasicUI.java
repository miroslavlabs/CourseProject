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

import com.pe.courseproject.Tree.Node;

public class BasicUI extends Application {

    public static final double SCENE_WIDTH = 1000;
    public static final double SCENE_HEIGHT = 700;
    public static final double MIN_WIDTH = 600;
    public static final double MIN_HEIGHT = 450;
    
    private static final String RGB_WHITE = "rgb(255,255,255)";
    private static final String HEX_MID_DARK_GRAY = "#545454";
    
    private static final Font STANDARD_FONT = new Font("Arial", 14);
    private static final Font TITLES_FONT = new Font("Arial", 18);

    private TreeView<String> treeView;
    private TreeItem<String> treeRootItem;
    private Tree charTree;
    
    private TableView<TableEntry> charProbabilityTable;
    private TableColumn<TableEntry, String> characterTableColumn;
    private TableColumn<TableEntry, String> probabilityTableColumn;
    
    private ScrollPane fileContentsPane;
    private Text fileContentsAsText;
    private TextField filePath;
    private Button browseButton;

    private Stage mainStage;

    private Map<Character,Double> charMap;
    private File file;
    
    public static class TableEntry {
    	private final StringProperty character;
    	private final StringProperty probability;
    	
    	public TableEntry(String character, String probability) {
    		this.character = new SimpleStringProperty(character);
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
        
        // Initialize the separate components of the layout - the panes, where all
        // the data from the file will be represented.
        createFileChooserArea(layout);        
        createFileContentsDisplayArea(layout);        
        createBinaryTreeView(layout);
        createCharDisplayArea(layout);

        
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
		        treeView.setPrefWidth(newDoubleValue * 0.5);

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
				treeView.setPrefHeight(newValue.doubleValue());
			}
		});
        
        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
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

        hBox.getChildren().addAll(filePath, browseButton);
        layout.setTop(hBox);
    }  
    
    /**
     * Creates a pane that displays the contents of the text file that was selected. The pane is
     * set as the left child of the BorderPane layout.
     */
    private void createFileContentsDisplayArea(BorderPane layout) {
    	Label label = new Label("File Contents");
    	label.setFont(TITLES_FONT);
    	label.setTextFill(Color.web(HEX_MID_DARK_GRAY));
    	
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
    
    /**
     * Creates and initializes the UI for the tree. The tree is initially empty.
     * @param layout
     */
    private void createBinaryTreeView(BorderPane layout) {
    	Label label = new Label("Probability Binary Tree");
    	label.setFont(TITLES_FONT);
    	label.setTextFill(Color.web(HEX_MID_DARK_GRAY));
    	
    	treeRootItem = new TreeItem<String>();		
		treeView = new TreeView<String>(treeRootItem);
   		
		VBox innerLayout = new VBox();
		innerLayout.setSpacing(5);
		innerLayout.getChildren().addAll(label, treeView);
		innerLayout.setPadding(new Insets(10,15,10,15));
		 
		layout.setCenter(innerLayout);
    }
       
   
    /**
     * Creates the right-sided pane that systemises and prints the character probability data.
     * @param layout
     */
    @SuppressWarnings("unchecked")
  	private void createCharDisplayArea(BorderPane layout) {
      	Label label = new Label("Probability Table");
      	label.setFont(TITLES_FONT);
      	label.setTextFill(Color.web(HEX_MID_DARK_GRAY));
      	
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
     * Opens a file chooser dialog that allows the user to select a file, the cotents of which will be processed.
     */
    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        // Configure FileChooser which will suggest to the user to use a .txt file. Using text
        // files with other extensions is beyond the purpose of the project. 
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
    	
    	// Update the file's path
    	filePath.setText(file.getAbsolutePath());
    	
    	String fileContents = TextFile.readFileContents(file, true);
    	
    	charMap = TextFile.obtainCharactersProbability(fileContents);
        
        // Update each of the panes accordingly.
    	processFileContents(fileContents);    	
    	processCharcterProbabilityTree();
    	processCharacterProbabilityTableData();
    }
    
    /**
     * In the appropriate pane, the contents of the text file are displayed.
     * @param fileContents
     */
	private void processFileContents(String fileContents) {
		fileContentsAsText.setText(fileContents);
    	fileContentsPane.setContent(fileContentsAsText);
	}
    
	/**
	 * Displays the character and its probability in a table to allow for an easy way to 
	 * visualize the character probability data.
	 */
    private void processCharacterProbabilityTableData() {
    	if(file != null) {
    		// To make the data more readable, the probability data is formatted.
            DecimalFormat format = new DecimalFormat("0.0000000000");
	    	List<TableEntry> charList = new ArrayList<TableEntry>();
	    	
	    	for(Map.Entry<Character, Double> entry : charMap.entrySet()) {

                if(Character.isWhitespace(entry.getKey())){
                    String whiteSpace = null;
                    char ch = entry.getKey();
                    switch(ch){
                        case ' ': whiteSpace = "Space";
                            break;
                        case '\t': whiteSpace = "Horizontal Tab";
                            break;
                        case '\u000B': whiteSpace = "Vertical Tab";
                            break;
                        case '\n': whiteSpace = "Line Feed";
                            break;
                        case '\f': whiteSpace = "Form Feed";
                            break;
                        case '\r': whiteSpace = "Carriage Return";
                            break;
                        case '\u001C': whiteSpace = "File Separator";
                            break;
                        case '\u001D': whiteSpace = "Group Separator";
                            break;
                        case '\u001E': whiteSpace = "Record Separator";
                            break;
                        case '\u001F': whiteSpace = "Unit Separator";
                            break;

                    }
                    if(whiteSpace != null){
                        charList.add(new TableEntry(whiteSpace, format.format(entry.getValue())));
                    }
                }else {
                    charList.add(new TableEntry(String.valueOf(entry.getKey()), format.format(entry.getValue())));
                }
	    	}
	    	
	    	charProbabilityTable.setItems(FXCollections.observableArrayList(charList));
            probabilityTableColumn.setSortType(TableColumn.SortType.DESCENDING);
            charProbabilityTable.getSortOrder().add(probabilityTableColumn);
    	}
    }
    
    /**
     * Creates the tree that will visually represent the data, acquired by processing the file contnts.
     */
	private void processCharcterProbabilityTree() {
		if(charMap != null && !charMap.isEmpty()) {
			charTree = new Tree(charMap);
			
			treeRootItem.setValue(charTree.getRoot().getKey());
			addTreeViewNode(charTree.getRoot().getLeftNode(), treeRootItem);
			addTreeViewNode(charTree.getRoot().getRightNode(), treeRootItem);
		}
	}
    
	/**
	 * Recursively create the child nodes of the binary tree (a TreeView represents it graphically). Every node inside the TreeView is
	 * created as expanded so that the data is immediately availbale.
	 * @param childNode The node which contains the processed data from the file.
	 * @param nodeItem The TreeItem UI element which will visualize the data.
	 */
    private void addTreeViewNode(Tree.Node childNode, TreeItem<String> nodeItem) {
    	if(childNode == null) return;

        TreeItem<String> child = new TreeItem<String>(childNode.getKey());
        nodeItem.getChildren().add(child);
        nodeItem.setExpanded(true);

        addTreeViewNode(childNode.getLeftNode(), child);
        addTreeViewNode(childNode.getRightNode(), child);
    }
}

