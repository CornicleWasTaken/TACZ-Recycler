package com.samik.taczrecycler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class GuiGenerator {
    public static void main(String[] args) throws Exception {
        int width = 256;
        int height = 256;
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        // Transparent background
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, width, height);
        g.setComposite(AlphaComposite.SrcOver);

        // Standard GUI size 176x166
        int guiWidth = 176;
        int guiHeight = 166;

        // Draw outer border (black)
        g.setColor(new Color(0x000000));
        g.fillRect(0, 0, guiWidth, guiHeight);

        // Draw inner background
        g.setColor(new Color(0xC6C6C6));
        g.fillRect(1, 1, guiWidth - 2, guiHeight - 2);

        // Draw highlights (Top and Left)
        g.setColor(new Color(0xFFFFFF));
        g.fillRect(1, 1, guiWidth - 2, 1);
        g.fillRect(1, 1, 1, guiHeight - 2);

        // Draw shadows (Bottom and Right)
        g.setColor(new Color(0x555555));
        g.fillRect(1, guiHeight - 2, guiWidth - 2, 1);
        g.fillRect(guiWidth - 2, 1, 1, guiHeight - 2);

        // Draw player inventory (8, 84 to 8+162, 84+54)
        drawSlotGrid(g, 8, 84, 9, 3);
        // Draw hotbar (8, 142)
        drawSlotGrid(g, 8, 142, 9, 1);

        // Draw Input Slot (56, 34)
        drawSlot(g, 56, 34);

        // Draw Output Slots (116, 18), (116, 34), (116, 50)
        drawSlot(g, 116, 18);
        drawSlot(g, 116, 34);
        drawSlot(g, 116, 50);

        // Draw Empty Arrow at (85, 34)
        drawEmptyArrow(g, 85, 34);

        // Draw Full Arrow at (176, 14) (This is where RecyclerScreen reads it from!)
        drawFullArrow(g, 176, 14);

        g.dispose();

        File out = new File("src/main/resources/assets/tacz_recycler/textures/gui/recycler_gui.png");
        out.getParentFile().mkdirs();
        ImageIO.write(img, "PNG", out);
        System.out.println("Successfully generated GUI texture at: " + out.getAbsolutePath());
    }

    private static void drawSlotGrid(Graphics2D g, int x, int y, int cols, int rows) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                drawSlot(g, x + i * 18, y + j * 18);
            }
        }
    }

    private static void drawSlot(Graphics2D g, int x, int y) {
        int sx = x - 1;
        int sy = y - 1;
        
        g.setColor(new Color(0x373737)); // Dark shadow
        g.fillRect(sx, sy, 17, 1);
        g.fillRect(sx, sy, 1, 17);

        g.setColor(new Color(0xFFFFFF)); // White highlight
        g.fillRect(sx + 1, sy + 17, 17, 1);
        g.fillRect(sx + 17, sy + 1, 1, 17);

        g.setColor(new Color(0x8B8B8B)); // Slot background
        g.fillRect(sx + 1, sy + 1, 16, 16);
    }

    private static void drawEmptyArrow(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x373737)); 
        g.fillRect(x, y + 4, 15, 9);
        g.fillPolygon(new int[]{x + 15, x + 15, x + 24}, new int[]{y, y + 17, y + 8}, 3);
        
        g.setColor(new Color(0x8B8B8B));
        g.fillRect(x + 1, y + 5, 13, 7);
        g.fillPolygon(new int[]{x + 15, x + 15, x + 22}, new int[]{y + 2, y + 15, y + 8}, 3);
    }

    private static void drawFullArrow(Graphics2D g, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y + 4, 15, 9);
        g.fillPolygon(new int[]{x + 15, x + 15, x + 24}, new int[]{y, y + 17, y + 8}, 3);
    }
}
