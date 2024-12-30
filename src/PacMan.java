import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

//هنا بنعمل import
//لمكتبة ال input- array - collection
// score information will be stored in an array
import java.io.*;
import java.util.ArrayList;

// collectoins library provides sorting functionality for the array
import java.util.Collections;

public class PacMan extends JPanel implements ActionListener, KeyListener {
    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        //constructor
        Block(Image image,int x, int y, int width, int height){
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char direction){
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;    
            for(Block wall : walls){
                if(collision(this, wall)){
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            if(this.direction == 'U'){
                this.velocityX = 0;
                this.velocityY= -tileSize/4; //negative cuz we going up to 0
            }
            else if(this.direction == 'D'){
                this.velocityX = 0;
                this.velocityY = tileSize/4; 
            }
            else if(this.direction == 'L'){
                this.velocityX = -tileSize/4;
                this.velocityY = 0; 
            }
            else if(this.direction == 'R'){
                this.velocityX = tileSize/4;
                this.velocityY = 0; 
            }
        }

        void reset(){
            this.x = this.startX;
            this.y = this.startY;
        }
    }

    private int rowCount = 21;
    private int colunCount = 19;
    private int tileSize = 32;
    private int boardWidth = colunCount * tileSize;
    private int boardeHeight = rowCount * tileSize;

    //add the images
    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;
    
    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    // New power-up image
    private Image cherryImage;

    private static final String SCORES_FILE = "scores.txt";
    private static final int MAX_HIGH_SCORES = 5;
    private ArrayList<Integer> highScores;

    //Arrays of strings for the tile map
     //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X                 X",
        "X XX X XXXXX X XX X",
        "X    X       X    X",
        "XXXX XXXX XXXX XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXrXX X XXXX",
        "O       bpo       O",
        "XXXX X XXXXX X XXXX",
        "OOOX X       X XOOO",
        "XXXX X XXXXX X XXXX",
        "X        X        X",
        "X XX XXX X XXX XX X",
        "X  X     P     X  X",
        "XX X X XXXXX X X XX",
        "X    X   X   X    X",
        "X XXXXXX X XXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX" 
    };

    //Using HashSets instead of arrays - better for perfrmance and checking for collision
    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;

    // New power-up related fields
    Block powerUp;
    boolean isPowerUpActive = false;
    int powerUpDuration = 200;
    int powerUpTimer = 0;
    Random powerUpRandom = new Random();

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

// Initializing high scores
    private void initializeHighScores() {
        highScores = new ArrayList<>();
        loadHighScores();
    }

    private void loadHighScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    highScores.add(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid score in file: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not load high scores: " + e.getMessage());
            // Create the file if it doesn't exist
            try {
                new File(SCORES_FILE).createNewFile();
            } catch (IOException ex) {
                System.err.println("Could not create scores file: " + ex.getMessage());
            }
        }
    }

    private void saveHighScore() {
        // Add current score to the list
        highScores.add(score);
        
        // Sort in descending order
        Collections.sort(highScores, Collections.reverseOrder());
        
        // Keep only the top scores
        while (highScores.size() > MAX_HIGH_SCORES) {
            highScores.remove(highScores.size() - 1);
        }
        
        // Save to file
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORES_FILE))) {
            for (Integer score : highScores) {
                writer.println(score);
            }
        } catch (IOException e) {
            System.err.println("Could not save high scores: " + e.getMessage());
        }
    }
    

    private void drawHighScores(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("High Scores:", boardWidth - 100, tileSize);
        
        for (int i = 0; i < highScores.size(); i++) {
            g.drawString((i + 1) + ". " + highScores.get(i), 
                        boardWidth - 100, 
                        tileSize * 2 + i * 20);
        }
    }

    // Add public access modifier to the constructor
    public PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardeHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        //load images in a constructor
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();

        // Load cherry image
        cherryImage = new ImageIcon(getClass().getResource("./cherry2.png")).getImage();

        loadMap();
        initializeHighScores();
        for (Block ghost : ghosts){
            char newDirection = directions[random.nextInt(4)];//randomizing ghosts movement
            ghost.updateDirection(newDirection);
        }
        gameLoop = new Timer(50, this);    //20fps (1000/50)
        gameLoop.start();

        // Random power-up spawn
        if (powerUpRandom.nextInt(100) < 10) { // 10% chance to spawn power-up
            spawnPowerUp();
        }
    }

    // New method to spawn power-up
    private void spawnPowerUp() {
        // Find an empty tile to spawn power-up
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colunCount; c++) {
                int x = c * tileSize;
                int y = r * tileSize;
                
                // Check if the tile is empty (no walls, no ghosts, no food)
                boolean isTileEmpty = true;
                for (Block wall : walls) {
                    if (wall.x == x && wall.y == y) {
                        isTileEmpty = false;
                        break;
                    }
                }
                
                if (isTileEmpty) {
                    powerUp = new Block(cherryImage, x, y, tileSize, tileSize);
                    return;
                }
            }
        }
    }

    public void loadMap(){
        //initialize  all HashSets
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();
        ghosts = new HashSet<Block>();

        //Iterate through the map
        for(int r = 0; r < rowCount; r++){
            for(int c = 0; c < colunCount; c++){
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                //to get tile position
                int x = c*tileSize;
                int y = r*tileSize;

                if(tileMapChar == 'X'){//block wall
                    Block wall = new Block(wallImage, x, y, tileSize, tileSize);
                    walls.add(wall);
                }
                else if (tileMapChar == 'b'){//blue ghost
                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'o'){//orange ghost
                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'p'){//pink ghost
                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'r'){//red ghost
                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize);
                    ghosts.add(ghost);
                }
                else if (tileMapChar == 'P') {//pacman
                    pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                }
                else if (tileMapChar == ' ') {//food
                    Block food = new Block(null, x + 14, y + 14, 4, 4);
                    foods.add(food);
                }
            }
        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g){
        //pacman
        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        //ghosts
        for (Block ghost : ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        //walls
        for (Block wall : walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        //foods
        g.setColor(Color.white);
        for (Block food : foods){
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        // Draw power-up if it exists
        if (powerUp != null) {
            g.drawImage(powerUp.image, powerUp.x, powerUp.y, powerUp.width, powerUp.height, null);
        }

        //score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver){
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: "  + String.valueOf(score), tileSize/2, tileSize/2);
        }

        // Display power-up status
        if (isPowerUpActive) {
            g.setColor(Color.YELLOW);
            g.drawString("POWER UP!", boardWidth - 100, tileSize/2);
        }
        drawHighScores(g);
    }

    public void move(){
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall collision
        for (Block wall : walls){
            if(collision(pacman, wall)){
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        // Power-up duration and ghost eating logic
        if (isPowerUpActive) {
            powerUpTimer++;
            if (powerUpTimer >= powerUpDuration) {
                // Power-up expired
                isPowerUpActive = false;
                powerUpTimer = 0;
            }

            // Check if Pac-Man can eat ghosts
            HashSet<Block> ghostsToRemove = new HashSet<>();
            for (Block ghost : ghosts) {
                if (collision(ghost, pacman)) {
                    ghostsToRemove.add(ghost);
                    score += 100; // Bonus points for eating a ghost
                }
            }
            ghosts.removeAll(ghostsToRemove);
        }


        if (ghosts.isEmpty()) {
            // Reset the game or start a new level
            loadMap();
            resetPositions();

            score += 500; // Bonus points for clearing all ghosts
            
            // Respawn ghosts
            for (Block ghost : ghosts){
                char newDirection = directions[random.nextInt(4)];
                ghost.updateDirection(newDirection);
            }
            
            // Optionally reset power-up state
            isPowerUpActive = false;
            powerUpTimer = 0;
            powerUp = null;
        }


        //check ghost collisions
        for (Block ghost : ghosts){
            if (collision(ghost, pacman)){
                if (!isPowerUpActive) {
                    lives -= 1;
                    if (lives == 0){
                        gameOver = true;
                        return;
                    }
                    resetPositions();
                }
            }

            if(ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D' ){
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls){
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        // Power-up collection logic
        if (powerUp != null && collision(pacman, powerUp)) {
            // Activate power-up
            isPowerUpActive = true;
            powerUpTimer = 0;
            powerUp = null;
        }

        // Randomly spawn new power-up if none exists
        if (powerUp == null && powerUpRandom.nextInt(100) < 5) { // 5% chance each move
            spawnPowerUp();
        }

        //check food collision
        Block foodEaten = null;
        for (Block food : foods){
            if (collision(pacman, food)){
               foodEaten = food;
               score += 10;
            }
        }
        foods.remove(foodEaten);

        //win scenario - next lever / reset
        if (foods.isEmpty()){
            loadMap();
            resetPositions();
        }
    }

    //check for collision
    public boolean collision(Block a, Block b){
        return  a.x < b.x + b.width &&   
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        //update the possition then redraw

        if (gameOver){
            saveHighScore();
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { //key with character
        }

    @Override
    public void keyPressed(KeyEvent e) { //any key even arrows / hold on to key
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(gameOver){
                loadMap();
                resetPositions();
                lives = 3;
                score = 0;
                gameOver = false;
                gameLoop.start();
            }
        
            if(e.getKeyCode() == KeyEvent.VK_UP){
                pacman.updateDirection('U');
            }
            else if(e.getKeyCode() == KeyEvent.VK_DOWN){
                pacman.updateDirection('D');
            }
            else if(e.getKeyCode() == KeyEvent.VK_LEFT){
                pacman.updateDirection('L');
            }
            else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
                pacman.updateDirection('R');
            }
        
            if (pacman.direction == 'U'){
                pacman.image = pacmanUpImage;
            }
            else if (pacman.direction == 'D'){
                pacman.image = pacmanDownImage;
            }
            else if (pacman.direction == 'L'){
                pacman.image = pacmanLeftImage;
            }
            else if (pacman.direction == 'R'){
                pacman.image = pacmanRightImage;
            }
        }
    }