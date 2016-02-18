package media;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Logo {

    private final String logo;

    public Logo(String logo) {
        this.logo = logo;

    }

    public File create() {
        File logoFile = new File(System.getProperty("java.io.tmpdir"), "logo.png");
        Font font = new Font("SansSerif", Font.PLAIN, 15);
        FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);

        BufferedImage bufferedImage = new BufferedImage(fontMetrics.stringWidth(logo), fontMetrics.getAscent() + fontMetrics.getDescent(), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setFont(font);
        graphics.setColor(Color.RED);
        graphics.drawString(logo, 0, fontMetrics.getAscent());
        try {
            ImageIO.write(bufferedImage, "png", logoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logoFile;
    }

}
