/**
 * Created by Nikolay on 26.11.2015 г..
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BasicUI extends Application {

    public static final double SCENE_WIDTH = 400;
    public static final double SCENE_HEIGHT = 300;

    private TreeView<String> tree;
    private VBox vBox;
    private StackPane pane;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
        public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello Genoff!");

        BorderPane layout = new BorderPane();
        layout.setTop(createTop());

        vBox = new VBox();//Print here chars and their probability
        layout.setLeft(vBox);

        pane = new StackPane();//Print here a tree
        layout.setCenter(pane);

        Scene scene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private HBox createTop() {
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(15, 15, 15, 15));
        hBox.setSpacing(5);
        TextField path = new TextField();

        Button browseButton = new Button("Browse");
        browseButton.setOnAction(event -> openFileChooser());

        Button actionButton = new Button("Do some cool stuff");
        actionButton.setOnAction(event -> processData());

        hBox.getChildren().addAll(path, browseButton, actionButton);
        return hBox;
    }


    private void processData() {

    }

    private void openFileChooser() {

    }
}
