import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Spritesheet {
    private BufferedImage spritessheet;

    public Spritesheet (String path){
        try {
            spritessheet= ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getSprite(int x, int y, int width, int height){
        return spritessheet.getSubimage(x, y, width, height);
    }
}
