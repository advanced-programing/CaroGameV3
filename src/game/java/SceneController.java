/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.java;

import com.sun.javafx.collections.ObservableListWrapper;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.control.*; 
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class SceneController implements Initializable {
    private static double BOARD_WIDTH = 390; 
    private static double BOARD_HEIGHT = 390; 
    private static double CELL_SIZE = 25; 
    private Player[] players;
    @FXML
    private BorderPane pane_main;
    @FXML
    private Label lb_title;
    @FXML
    private ComboBox cb_size;
    @FXML
    private ComboBox cb_level;
    @FXML
    private TextField tf_name;
    @FXML
    private HBox pane_board;
    @FXML
    private Label lb_message;
    private int level;
    private Canvas board;
    private GraphicsContext board_gc;
    private int board_size;
    private static Color board_background = Color.AQUAMARINE;
    private static double pad = 8;
    private int[][] playingMap;
    private int turn = 1; 
    
    private MainApp application; 
    @FXML
    private Button bt_remote;
    
    public void setApp(MainApp application) {
        this.application = application;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        players = new Player[3]; 
        List<String> items = new ArrayList<>();
        items.add("7x7"); 
        items.add("9x9"); 
        items.add("11x11");
        items.add("13x13");
        cb_size.setItems(new ObservableListWrapper(items));
        cb_size.setValue("9x9");
        
        List<String> items2 = new ArrayList<>(); 
        items2.add("Easy"); 
        items2.add("Intermidiate"); 
        items2.add("Hard"); 
        cb_level.setItems(new ObservableListWrapper(items2));
        cb_level.setValue("Intermidiate");
        level = 2; 
        board = new Canvas(BOARD_WIDTH, BOARD_HEIGHT); 
        pane_board.getChildren().add(board); 
        board_gc = board.getGraphicsContext2D(); 
        board_size = 9;
        drawBoard(board_size, board_gc); 
        resetBoard(board_size); 
        
    }

    private void drawBoard(int n, GraphicsContext gc) {
        gc.clearRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        gc.setFill(board_background);
        double x = 0, y = 0; 
        //double w = 25, h = 25; 
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                gc.fillRect(pad + i * (CELL_SIZE + 4), pad + j*(CELL_SIZE + 4), CELL_SIZE, CELL_SIZE);
            }
        }
        board_gc.setLineWidth(3.0); 
        board_gc.setStroke(Color.RED);
        gc.strokeRect(2, 2, pad + n*(CELL_SIZE + 4), pad + n*(CELL_SIZE + 4));
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
        lb_message.setText("");
    }

    @FXML
    private void handleBoardSizeChanged(ActionEvent event) {
        if (cb_size.getValue().equals("7x7")){
            board_size = 7; 
        }
        if (cb_size.getValue().equals("9x9")) {
            board_size = 9; 
        }
        if (cb_size.getValue().equals("11x11")) {
            board_size = 11; 
        }        
        if (cb_size.getValue().equals("13x13")) {
            board_size = 13; 
        }
        drawBoard(board_size, board_gc);
        resetBoard(board_size);
    }

    @FXML
    private void handleLevelChanged(ActionEvent event) {
        if (cb_level.getValue().equals("Easy")){
            level = 1; 
        }
        if (cb_level.getValue().equals("Intermediate")){
            level = 1; 
        }
        if (cb_level.getValue().equals("Hard")){
            level = 1; 
        }
    }

    @FXML
    private void handleNewGame(ActionEvent event) {
        drawBoard(board_size, board_gc); 
        resetBoard(board_size);
        players[1] = new Player(); 
        players[2] = new Player(); 
        players[1].setName("Player 1");
        players[2].setName("Player 2");
        board.setOnMouseClicked((MouseEvent event1) -> {
            int r = (int) ((event1.getX() - pad) / (CELL_SIZE + 4));
            int c = (int) ((event1.getY() - pad) / (CELL_SIZE + 4));
            if (r < board_size && c < board_size){
                if (playingMap[r][c] == 0) {
                    if (turn == 1) {
                        drawCross(r, c);
                        playingMap[r][c] = turn;
                        if (checkWinner(r, c)){
                            lb_message.setText(players[turn].getName() + " wins!");
                            board.setOnMouseClicked(null);
                        }
                        if (checkDraw()){
                            lb_message.setText("Draw!");
                            board.setOnMouseClicked(null);
                        } else {
                            turn = 2;
                        }
                    } else {
                        drawCircle(r, c);
                        playingMap[r][c] = turn;
                        if (checkWinner(r, c)) {
                            lb_message.setText(players[turn].getName() + " wins!");
                            board.setOnMouseClicked(null);
                        }
                        if (checkDraw()) {
                            lb_message.setText("Draw!");
                            board.setOnMouseClicked(null);
                        } else {
                            turn = 1;
                        }
                    }
                }
            }
        });
    }
    
    private void drawCross(int row, int col){
        int inner_pad = 3; 
        int border_width = 4; 
        board_gc.clearRect(pad + row*(CELL_SIZE + border_width), pad + col*(CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setFill(board_background);
        board_gc.fillRect(pad + row *(CELL_SIZE + border_width), pad + col * (CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setStroke(Color.RED);
        board_gc.setLineWidth(3.0);
        board_gc.strokeLine(pad + row * (CELL_SIZE + border_width) + inner_pad, pad + col * (CELL_SIZE + border_width) + inner_pad, pad + row * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + col * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad);
        board_gc.strokeLine(pad + row * (CELL_SIZE + border_width) + inner_pad, pad + col * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + row * (CELL_SIZE + border_width) + CELL_SIZE - inner_pad, pad + col * (CELL_SIZE + border_width) + inner_pad);
    }
    private void drawCircle(int row, int col) {
        int inner_pad = 3; 
        int border_width = 4;
        board_gc.clearRect(pad + row *(CELL_SIZE + border_width), pad + col*(CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setFill(board_background);
        board_gc.fillRect(pad + row *(CELL_SIZE + border_width), pad + col*(CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
        board_gc.setStroke(Color.SLATEBLUE);
        board_gc.setLineWidth(3.0);
        board_gc.strokeOval(pad + row *(CELL_SIZE + border_width), pad + col*(CELL_SIZE + border_width), CELL_SIZE, CELL_SIZE);
    }
    private boolean checkWinner(int row, int col) {
        int[][] rc = {{0, -1, 0, 1}, {-1, 0, 1, 0}, {1, -1, -1, 1}, {-1, -1, 1, 1}};
        int i = row, j = col;
        int current = playingMap[row][col]; 
        for (int direction = 0; direction < 4; direction++) {
            int count = 0;
            i = row;
            j = col;
            while (i > 0 && i < playingMap.length && j > 0 && j < playingMap.length && playingMap[i][j] == current) {
                count++;
                if (count == 5) {
                    return true;
                }
                i += rc[direction][0];
                j += rc[direction][1];
            }
            count--;
            i = row;
            j = col;
            while (i > 0 && i < playingMap.length && j > 0 && j < playingMap.length && playingMap[i][j] == current) {
                count++;
                if (count == 5) {
                    return true;
                }
                i += rc[direction][2];
                j += rc[direction][3];
            }
        }
        return false;
    }
    private boolean checkDraw(){
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                if (playingMap[i][j] == 0) {
                    return false; 
                }
            }
        }
        return true; 
    }
    @FXML
    private void handleClosing(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void playRemote(ActionEvent event) {
        if (application == null) {
        } else {
            application.userChoseRemote(true); 
        }
    }
}