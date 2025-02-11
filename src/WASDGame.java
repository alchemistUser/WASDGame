
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WASDGame extends JFrame {

    // Player properties
    private double playerX = 200;  // Initial X position of the player
    private double playerY = 200;  // Initial Y position of the player
    private BufferedImage playerImage;  // Image for the player
    private boolean showHitbox = false; // Tracks if the hitbox should be highlighted
    private int movspeed = 3;

    // Movement flags
    private boolean moveUp = false;
    private boolean moveDown = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;

    // Timer for continuous movement
    private Timer movementTimer;

    // Dash ability
    private boolean isDashing = false; // Tracks if the player is currently dashing
    private long lastDashTime = 0;    // Tracks the last time the dash was used (in milliseconds)
    private final int DASH_COOLDOWN = 2000; // Cooldown duration in milliseconds (2 seconds)
    private final int DASH_DISTANCE = 100; // Distance to dash in pixels
    private BufferedImage skillIcon;

    private int dashSteps = 10;        // Number of steps for the dash animation
    private int currentDashStep = 0;   // Current step of the dash animation
    private int dashDirectionX = 0;    // X direction of the dash
    private int dashDirectionY = 0;    // Y direction of the dash

    private final int SKILL_UI_HEIGHT = 60; // Height of the skill UI area
    private final int SKILL_ICON_SIZE = 50; // Size of each skill icon
    private final int SKILL_PADDING = 10;   // Padding between skill icons

    public WASDGame() {
        initializeGameWindow();
        loadPlayerImage();
        setupKeyListener();
        startMovementTimer();
        setVisible(true);  // Make the window visible
    }

    // Initialize the game window
    private void initializeGameWindow() {
        setTitle("WASD Movement Game with Image");
        setSize(600, 600);  // Set the size of the game window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        add(new GamePanel()); // Add custom JPanel for drawing
    }

    // Load the player image from the directory
    private void loadPlayerImage() {
        try {
            playerImage = ImageIO.read(new File("player.png"));
            skillIcon = ImageIO.read(new File("skill_icon.png")); // Replace with your skill icon file
        } catch (IOException e) {
            System.out.println("Error loading images: " + e.getMessage());
            System.exit(1);
        }
    }

    // Dash skill draw
    private void drawSkillIcon(Graphics g) {
        if (skillIcon != null) {
            int iconX = 10; // X position of the skill icon
            int iconY = 10; // Y position of the skill icon
            int iconSize = 50; // Size of the skill icon

            // Draw the skill icon
            g.drawImage(skillIcon, iconX, iconY, iconSize, iconSize, this);

            // Calculate cooldown progress
            long currentTime = System.currentTimeMillis();
            long timeSinceLastDash = currentTime - lastDashTime;
            if (timeSinceLastDash < DASH_COOLDOWN) {
                double cooldownProgress = 1.0 - (double) timeSinceLastDash / DASH_COOLDOWN;

                // Draw cooldown overlay
                g.setColor(new Color(0, 0, 0, (float) cooldownProgress)); // Semi-transparent black
                g.fillRect(iconX, iconY, iconSize, (int) (iconSize * cooldownProgress));
            }
        }
    }

    // Setup key listener for WASD movement and hitbox toggle
    private void setupKeyListener() {
        addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPressed(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(e.getKeyCode());
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // Not used in this example
            }
        });
    }

    // Handle key press events
    private void handleKeyPressed(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W ->
                moveUp = true;
            case KeyEvent.VK_A ->
                moveLeft = true;
            case KeyEvent.VK_S ->
                moveDown = true;
            case KeyEvent.VK_D ->
                moveRight = true;
            case KeyEvent.VK_B ->
                toggleHitbox();
            case KeyEvent.VK_SPACE -> {
                if (!isDashing && System.currentTimeMillis() - lastDashTime >= DASH_COOLDOWN) {
                    performDash();
                    lastDashTime = System.currentTimeMillis(); // Record the time of the dash
                }
            }
        }
    }

    // Handle key release events
    private void handleKeyReleased(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_W ->
                moveUp = false;
            case KeyEvent.VK_A ->
                moveLeft = false;
            case KeyEvent.VK_S ->
                moveDown = false;
            case KeyEvent.VK_D ->
                moveRight = false;
        }
    }

    // Toggle hitbox visibility
    private void toggleHitbox() {
        showHitbox = !showHitbox;
        repaint(); // Repaint to update the screen
    }

    // Start the timer for continuous movement
    private void startMovementTimer() {
        movementTimer = new Timer(16, e -> {
            updatePlayerPosition();
            repaint(); // Repaint the screen after updating the position
        });
        movementTimer.start();
    }

    // Update player position based on movement flags
    private void updatePlayerPosition() {
        if (isDashing) {
            if (currentDashStep < dashSteps) {
                // Calculate the next position during the dash
                double nextX = playerX + dashDirectionX * DASH_DISTANCE / dashSteps;
                double nextY = playerY + dashDirectionY * DASH_DISTANCE / dashSteps;

                // Check boundaries before updating position
                if (nextX >= 0 && nextX <= getWidth() - playerImage.getWidth()) {
                    playerX = nextX;
                }
                if (nextY >= 0 && nextY <= getHeight() - SKILL_UI_HEIGHT - playerImage.getHeight()) {
                    playerY = nextY;
                }

                currentDashStep++;
            } else {
                // End the dash animation
                isDashing = false;
            }
        } else {
            // Normal movement
            if (moveUp && playerY > 0) {
                playerY -= movspeed;
            }
            if (moveDown && playerY < getHeight() - SKILL_UI_HEIGHT - playerImage.getHeight()) {
                playerY += movspeed;
            }
            if (moveLeft && playerX > 0) {
                playerX -= movspeed;
            }
            if (moveRight && playerX < getWidth() - playerImage.getWidth()) {
                playerX += movspeed;
            }
        }
    }

    private void performDash() {
        if (!isDashing && System.currentTimeMillis() - lastDashTime >= DASH_COOLDOWN) {
            isDashing = true;
            currentDashStep = 0;

            // Determine the dash direction based on movement flags
            dashDirectionX = 0;
            dashDirectionY = 0;
            if (moveUp) {
                dashDirectionY -= 1;
            }
            if (moveDown) {
                dashDirectionY += 1;
            }
            if (moveLeft) {
                dashDirectionX -= 1;
            }
            if (moveRight) {
                dashDirectionX += 1;
            }

            // Normalize the direction vector to prevent faster diagonal movement
            double length = Math.sqrt(dashDirectionX * dashDirectionX + dashDirectionY * dashDirectionY);
            if (length != 0) {
                dashDirectionX /= length; // Normalize X direction
                dashDirectionY /= length; // Normalize Y direction
            }

            // Debug output
            System.out.println("Dash Direction: (" + dashDirectionX + ", " + dashDirectionY + ")");
            lastDashTime = System.currentTimeMillis(); // Record the time of the dash
        }
    }

    // Custom JPanel for drawing the game elements
    private class GamePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Draw the background
            drawBackground(g);

            // Draw the player
            drawPlayer(g);

            // Draw the hitbox (if enabled)
            drawHitbox(g);

            // Draw the skill UI at the bottom
            drawSkillUI(g);
        }

        // Draw the skill UI at the bottom of the screen
        private void drawSkillUI(Graphics g) {
            int uiY = getHeight() - SKILL_UI_HEIGHT; // Y position of the skill UI
            g.setColor(new Color(50, 50, 50)); // Dark gray background for the skill UI
            g.fillRect(0, uiY, getWidth(), SKILL_UI_HEIGHT);

            // Draw the dash skill icon
            if (skillIcon != null) {
                int iconX = SKILL_PADDING; // X position of the first skill icon
                int iconY = uiY + (SKILL_UI_HEIGHT - SKILL_ICON_SIZE) / 2; // Center vertically

                // Draw the skill icon
                g.drawImage(skillIcon, iconX, iconY, SKILL_ICON_SIZE, SKILL_ICON_SIZE, this);

                // Calculate cooldown progress
                long currentTime = System.currentTimeMillis();
                long timeSinceLastDash = currentTime - lastDashTime;
                if (timeSinceLastDash < DASH_COOLDOWN) {
                    double cooldownProgress = 1.0 - (double) timeSinceLastDash / DASH_COOLDOWN;

                    // Draw cooldown overlay
                    g.setColor(new Color(0, 0, 0, (float) cooldownProgress)); // Semi-transparent black
                    g.fillRect(iconX, iconY, SKILL_ICON_SIZE, (int) (SKILL_ICON_SIZE * cooldownProgress));
                }
            }
        }

        // Draw the background
        private void drawBackground(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight() - SKILL_UI_HEIGHT); // Exclude skill UI area
        }

        // Draw the player image
        private void drawPlayer(Graphics g) {
            if (playerImage != null) {
                g.drawImage(playerImage, (int) playerX, (int) playerY, this);
            }
        }

        // Draw the hitbox if showHitbox is true
        private void drawHitbox(Graphics g) {
            if (showHitbox) {
                g.setColor(Color.RED);
                g.drawRect((int) playerX, (int) playerY, (int) playerImage.getWidth(), (int) playerImage.getHeight());
            }
        }
    }

    public static void main(String[] args) {
        // Run the game on the Event Dispatch Thread
        SwingUtilities.invokeLater(WASDGame::new);
    }
}
