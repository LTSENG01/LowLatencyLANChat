import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.zeromq.ZMQ;
import java.util.Scanner;

public class Main extends Application {

    static Scanner userIn = new Scanner(System.in); // Console input
    static ZMQ.Context context = ZMQ.context(1);
    static ZMQ.Socket socket = context.socket(ZMQ.PAIR);

    static GridPane grid = new GridPane();
    static int startRow = 5;

    public static void main(String[] args) {

        System.out.println("Type something.");

        //  Socket to talk to clients
        socket.bind("tcp://*:5555");    // USE * for Person 1, ENTER IP ADDRESS OF PERSON 1 on PERSON 2's. Call socket.connect() instead for Person 2.

        // Puts receive and send message functionality on separate threads.
        new Thread(() -> {
            while (true) {
                receiveMessage();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                sendMessage("");
            }
        }).start();

        // Launch JavaFX GUI Window
        launch(args);
    }

    public static void sendMessage(String message) {

        if (message.equals("X")) {
            // Stops the connection
            endConnection();
        } else if (message.equals("")) {
            // Do Nothing
        } else {
            socket.send(message.getBytes(), 0);
            addMessageToScreen("You: " + message);
        }

    }

    public static void receiveMessage() {

        byte[] request = socket.recv(0);
        System.out.println("User 2: " + new String(request));

        addMessageToScreen("User 2: " + new String(request));

    }

    public static void endConnection() {

        socket.close();
        context.term();

    }

    public void start(Stage primaryStage) throws Exception {

        Text text = new Text("Message: ");

        TextField input = new TextField();

        Button btn = new Button();
        btn.setText("Send");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                sendMessage(input.getText());
            }
        });

        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));


        grid.add(text, 0, 0);
        grid.add(input, 1, 0);
        grid.add(btn, 4, 0);

        Scene scene = new Scene(grid, 350, 300);

        primaryStage.setTitle("Chat");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void addMessageToScreen(String message) {
        Platform.runLater(() -> {
            startRow = startRow+1;
            grid.add(new Text(message), 0, startRow);
        });
    }

}