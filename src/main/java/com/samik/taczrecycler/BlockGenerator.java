package com.samik.taczrecycler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class BlockGenerator {
    public static void main(String[] args) throws Exception {
        int size = 16;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Base color
        g.setColor(new Color(0x737373));
        g.fillRect(0, 0, size, size);
        
        // Border
        g.setColor(new Color(0x3B3B3B));
        g.drawRect(0, 0, size - 1, size - 1);
        
        // Hazard stripes
        g.setColor(new Color(0xE3A82B));
        g.fillRect(1, 1, 14, 2);
        g.fillRect(1, 13, 14, 2);
        
        g.setColor(new Color(0x2B2B2B));
        for(int i = 1; i < 15; i += 4) {
            g.fillRect(i, 1, 2, 2);
            g.fillRect(i, 13, 2, 2);
        }

        // Inner processing circle
        g.setColor(new Color(0x212121));
        g.fillOval(3, 4, 10, 8);
        
        g.setColor(new Color(0x5AEB4F)); // glowing green
        g.fillOval(5, 6, 6, 4);

        g.dispose();

        File out = new File("src/main/resources/assets/tacz_recycler/textures/block/recycler.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Successfully generated Block texture at: " + out.getAbsolutePath());
    }
}
