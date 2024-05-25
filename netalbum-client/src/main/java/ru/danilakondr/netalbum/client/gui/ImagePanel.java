package ru.danilakondr.netalbum.client.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

class ImagePanel extends JPanel {
  private Dimension imageSize = new Dimension(400, 300);
  private double posX;
  private double posY;
  private Image img;

  public ImagePanel() {
      super();
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    setBackground(Color.BLACK);

    if (isImageLoaded()) {
      MediaTracker mt = new MediaTracker(this);
      mt.addImage(img, 0);
      try {
        mt.waitForAll();
      }
      catch (InterruptedException e){;}

      calcImage();
      g.drawImage(img, (int)posX, (int)posY, imageSize.width, imageSize.height, this);
    }
  }

  public void calcImage() {
    double imgWidth = img.getWidth(this);
    double imgHeight = img.getHeight(this);
    
    double width = getWidth();
    double height = getHeight();
    
    double widthScale = width / imgWidth;
    double heightScale = height / imgHeight;
    
    imgHeight *= widthScale;
    if (imgHeight > height) {
        imgHeight = height;
        imgWidth = img.getWidth(this) * heightScale;
    }
    else
        imgWidth = width;
    
    posX = width/2 - imgWidth/2;
    posY = height/2 - imgHeight/2;
    imageSize = new Dimension((int)imgWidth, (int)imgHeight);
  }

  public void loadImage(BufferedImage image) {
    this.imageSize = getSize();
    img = image;
    
    revalidate();
    repaint();
  }

  public boolean isImageLoaded() {
    return img != null;
  }
}
