import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Lights extends Application{

    private Label response;

    public static void main(String[] args) {
        // Start the JavaFX application by calling launch().
        launch(args);
    }

    @Override
    public void start(Stage myStage) {
        // Give the stage a title.
        myStage.setTitle("Use Images with Buttons");
        // Use a FlowPane for the root node. In this case,
        // vertical and horizontal gaps of 10.
        FlowPane rootNode = new FlowPane(10, 10);
        // Center the controls in the scene.
        rootNode.setAlignment(Pos.CENTER);
        // Create a scene.
        Scene myScene = new Scene(rootNode, 250, 450);
        // Set the scene on the stage.
        myStage.setScene(myScene);
        // Create a label.
        response = new Label("Push a Button");
        // Create two image-based buttons.
        Button btnHourglass = new Button("Hourglass");
        Button btnAnalogClock = new Button("Analog Clock");

        // Position the text under the image.
        btnHourglass.setContentDisplay(ContentDisplay.TOP);
        btnAnalogClock.setContentDisplay(ContentDisplay.TOP);

        // Handle the action events for the hourglass button.
        btnHourglass.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                response.setText("Hourglass Pressed");
            }
        });

        // Handle the action events for the analog clock button.
        btnAnalogClock.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent ae) {
                response.setText("Analog Clock Pressed");
            }
        });

        // Add the label and buttons to the scene graph.
        rootNode.getChildren().addAll(btnHourglass, btnAnalogClock, response);
        // Show the stage and its scene.
        myStage.show();
    }
//
//    @Override
//    public void start(Stage primaryStage) {
//        System.out.println("thread = " + Thread.currentThread() + ": start(" + primaryStage + ") invoked.");
//
//        primaryStage.setTitle("Road Lights");
//        Group root = new Group();
//        Canvas canvas = new Canvas(300, 250);
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        drawShapes(gc);
//        root.getChildren().add(canvas);
//
//        primaryStage.setScene(new Scene(root));
//        primaryStage.show();
//    }
//
//    /**
//     * Draws a series of basic shapes on the specified GraphicsContext.
//     * @param gc The GraphicsContext object to draw on
//     */
//    private void drawShapes(GraphicsContext gc) {
//        System.out.println(Thread.currentThread()+ ": drawShapes(" + gc + ")");
//        gc.setFill(Color.GRAY);
//        gc.setStroke(Color.BLACK);
//        gc.setLineWidth(2);
//
//        gc.fillOval(10, 10, 30, 30);
//        gc.fillOval(10, 50, 30, 30);
//        gc.fillOval(10, 90, 30, 30);
//        gc.strokeOval(10, 10, 30, 30);
//        gc.strokeOval(10, 50, 30, 30);
//        gc.strokeOval(10, 90, 30, 30);
//
//    }
//
//    void makeNext(){
//
//    }
//
//    @Override
//    public void stop() throws Exception {
//        System.out.println(Thread.currentThread() + ": stop() invoked...");
//        super.stop();
//    }
//
//    public static void main(String[] args) {
//        System.out.println("thread = " + Thread.currentThread());
//        launch(args);
//    }
//
//    private int state = 0;
}