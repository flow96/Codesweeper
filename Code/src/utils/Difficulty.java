package utils;

/**
 * @author Florian Lutze
 *
 * Defines the size of the game field
 */
public enum Difficulty {
    BEGINNER(9, 10, 10),
    ADVANCED(16, 16, 40),
    PROFESSIONAL(30, 16, 170),
    CUSTOM(0, 0, 0);


    private int width, height, mines;

    Difficulty(int width, int height, int mines){
        this.width = width;
        this.height = height;
        this.mines = mines;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setMines(int mines) {
        this.mines = mines;
    }

    public int getMines() {
        return mines;
    }
}
