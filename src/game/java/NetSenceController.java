/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.java;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import net.java.Client;

/**
 * FXML Controller class
 *
 * @author admin
 */
public class NetSenceController implements Initializable {

    public final static int DEFAULT_SIZE = 9; 
    private static double BOARD_WIDTH = 390;
    private static double BOARD_HEIGHT = 390;
    private static double CELL_SIZE = 25;
    @FXML
    private HBox pane_board;
    @FXML
    private Label lb_message;
    private MainApp application;
    @FXML
    private Label lb_turn;

    public void setApp(MainApp application) {
        this.application = application;
        System.out.println("NC " + "hubName " + application.hubName);
        System.out.println("NC " + "hubport " + application.hubPort);
        System.out.println("NC " + "hubisListen" + application.beClient);
        this.hostName = application.hubName;
        this.serverPortNumber = application.hubPort;
        System.out.println("SetApp was set up");
        Thread connector = new Thread(() -> connect(hostName, serverPortNumber));
        connector.setDaemon(true);
        connector.start();
    }
    /**
     * Initializes the controller class.
     */
    
    private volatile boolean connecting; 
    private Canvas board; 
    private GraphicsContext board_gc; 
    private int [][] playingMap;
    private static double pad = 8; 
    private static Color board_background = Color.AQUAMARINE; 
    private String hostName;
    private int serverPortNumber; 
    private int board_size; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        board_size = DEFAULT_SIZE;
        lb_message.setText("Waiting for connection.");
        board = new Canvas(BOARD_WIDTH, BOARD_HEIGHT); 
        
        pane_board.getChildren().add(board); 
        board_gc = board.getGraphicsContext2D(); 
        pane_board.setOnMouseClicked((MouseEvent event1) -> doMouseClick(event1.getX(), event1.getY())); 
        resetBoard(board_size);
        //drawBoard(board_size, board_gc); 
        System.out.println("NC2 " + "hubName " + hostName);
        System.out.println("NC2 " + "hubport " + serverPortNumber);

    }   
    
    private void resetBoard(int size) {
        if (playingMap != null) {
            playingMap = null;
        }
        playingMap = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                playingMap[i][j] = 0;
            }
        }

    }

    private CaroState state;
    private CaroClient connection;
    private int myID;

    private void connect(String hostName, int serverPortNumber) {
        CaroClient c;
        int id;
        try {
            c = new CaroClient(hostName, serverPortNumber);
            id = c.getID();
            Platform.runLater(() -> {
                connecting = false;
                connection = c;
                myID = id;
                resetBoard(board_size);
                //drawBoard(board_size);
                lb_message.setText("Wating for 2 players connect.");
            });
        } catch (IOException e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sorry, could not connect to\n"
                        + hostName + " on port " + serverPortNumber + "\nShutting down.");
                alert.showAndWait();
                Platform.exit();
            });
        }
    }
    private void drawBoard(int n) {
        System.out.println("Player X = " + state.playerPlayingX); 
        System.out.println("Player O = " + state.playerPlayingO);
        board_gc.clearRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        board_gc.setFill(board_background);
        board_gc.setLineWidth(3.0);
        board_gc.setStroke(Color.RED);
        board_gc.strokeRect(2, 2, pad + n * (CELL_SIZE + 4), pad + n * (CELL_SIZE + 4));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board_gc.fillRect(pad + i * (CELL_SIZE + 4), pad + j * (CELL_SIZE + 4), CELL_SIZE, CELL_SIZE);
                if (state.playingMap[i][j] == state.playerPlayingX) {
                    drawCross(i, j);
                } 
                if (state.playingMap[i][j] == state.playerPlayingO) {
                    drawCircle(i, j);
                }
            }
        }
        
    }
        private void drawCross(int row, int col) {
        int inner_pad = 3;
        int border_width = 4;
        board_gc.clearRect(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setFill(board_background);
        board_gc.fillRect(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setStroke(Color.RED);
        board_gc.setLineWidth(3.0);
        board_gc.strokeLine(pad + row * (CELL_SIZE + border_width) + inner_pad, pad + col * (CELL_SIZE + border_width) + inner_pad, pad + row * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + col * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad);
        board_gc.strokeLine(pad + row * (CELL_SIZE + border_width) + inner_pad, pad + col * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + row * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + col * (CELL_SIZE + border_width) + inner_pad);
    }

    private void drawCircle(int row, int col) {
        int inner_pad = 3;
        int border_width = 4;
        board_gc.clearRect(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setFill(board_background);
        board_gc.fillRect(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setStroke(Color.SLATEBLUE);
        board_gc.setLineWidth(3.0);
        board_gc.strokeOval(pad + row * (CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
    }


    private void newState(CaroState state) {
        if (state.playerDisconnected) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Your opponent has disconnected.\nThe game is ended.");
            alert.showAndWait();
            //Platform.exit(); 
        }
        this.state = state;
        for (int i = 0; i < board_size; i++){ 
            for (int j = 0; j < board_size; j++) 
            {
                System.out.print(state.playingMap[j][i] + "\t");
            }
            System.out.print("\n"); 
        }
        drawBoard(DEFAULT_SIZE);
        if (state.playingMap == null) {
            return; 
        }
        else if (!state.gameInProgress) {
            lb_turn.setText("Game over!");
            if (state.gameEndedInDraw) 
                lb_message.setText("Game ended in draw. Click to start again.");
            else if (myID == state.winner) 
                lb_message.setText("You won! Click to start a new game.");
            else 
                lb_message.setText("You lost. Waiting for new game");
        } else {
            if (myID == state.playerPlayingX)
                lb_turn.setText("You are playing X's"); 
            else 
                lb_turn.setText("You are playing O's");
            if (myID == state.currentPlayer) 
                lb_message.setText("Your move");
            else 
                lb_message.setText("Waiting for opponent's move");
        }     
    }

    private void doMouseClick(double x, double y) {
        System.out.println("x = " + x);
        System.out.println("y = " + y);
        if (state == null || state.playingMap == null) {
            return; 
        } 
        if (!state.gameInProgress) {
            if (state.gameEndedInDraw || myID == state.winner) {
                connection.send("newgame"); 
                System.out.println("new game message just send");
            }
            return; 
        }
        if (myID != state.currentPlayer) {
            return; 
        }
        int row = (int) ((x - pad) / (CELL_SIZE + 4));
        int col = (int) ((y - pad) / (CELL_SIZE + 4));
        if (row >= 0 && row < board_size && col >= 0 && col < board_size && state.playingMap[row][col] == 0) {
            connection.send(new int[] {row, col}); 
        }
    }

    private class CaroClient extends Client {

        /**
         * Connect to the hub at a specified host
         */
        public CaroClient(String hubHostName, int hubPort) throws IOException {
            super(hubHostName, hubPort);
        }

        /**
         * Responds to a message received from the hub
         */
        @Override
        protected void messageReceived(Object message) {
            if (message instanceof CaroState) {
                Platform.runLater(() -> newState((CaroState) message));
                System.out.println("New message received."); 
            }
        }
        
        protected void serverShutdown(String message) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            "Your opponent has disconnected.\nThe game is ended.");
                alert.showAndWait(); 
                System.exit(0);
            });
        }
    }
    
}
