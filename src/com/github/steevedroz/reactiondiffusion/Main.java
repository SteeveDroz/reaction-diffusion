package com.github.steevedroz.reactiondiffusion;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
	try {
	    BorderPane root = new BorderPane();
	    Scene scene = new Scene(root);
	    scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	    ReactionDiffusion rd = new ReactionDiffusion(400, 400);
	    root.setCenter(rd);

	    AnimationTimer timer = new AnimationTimer() {

		@Override
		public void handle(long now) {
		    rd.update();
		    rd.show();
		}
	    };

	    HBox menu = new HBox();
	    Button timerButton = new Button("Start");
	    timerButton.setOnAction(event -> {
		if (timerButton.getText().equals("Start")) {
		    timer.start();
		    timerButton.setText("Stop");
		} else {
		    timer.stop();
		    timerButton.setText("Start");
		}
	    });
	    menu.getChildren().add(timerButton);

	    ScrollBar dB = new ScrollBar();
	    Label dBValue = new Label("" + rd.dB);

	    dB.setMin(10);
	    dB.setMax(50);
	    dB.setValue(rd.dB * 100);
	    dB.setPrefWidth(200);
	    dB.valueProperty().addListener((observableValue, oldValue, newValue) -> {
		rd.dB = (double) (newValue.intValue() / 100.0);
		dBValue.setText("" + rd.dB);
	    });

	    dBValue.setMaxWidth(50);

	    menu.getChildren().add(dB);
	    menu.getChildren().add(dBValue);

	    Button clear = new Button("Clear");
	    clear.setOnAction(event -> rd.clear());
	    menu.getChildren().add(clear);

	    root.setBottom(menu);

	    primaryStage.setScene(scene);
	    primaryStage.setTitle("Reaction-Diffusion");
	    primaryStage.show();

	    rd.show();
	    primaryStage.setOnCloseRequest(event -> {
		timer.stop();
	    });
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public static void main(String[] args) {
	launch(args);
    }
}
