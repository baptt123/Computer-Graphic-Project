package Level2;

class Projectile {
    public int x, y, vy;
    public boolean isPlayer;  // True if projectile belongs to player, false if from boss

    public Projectile(int x, int y, int vy, boolean isPlayer) {
        this.x = x;
        this.y = y;
        this.vy = vy;
        this.isPlayer = isPlayer;
    }
}
