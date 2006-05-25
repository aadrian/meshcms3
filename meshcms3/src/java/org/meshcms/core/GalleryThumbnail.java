/*
 * MeshCMS - A simple CMS based on SiteMesh
 * Copyright (C) 2004-2006 Luciano Vernaschi
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * You can contact the author at http://www.cromoteca.com
 * and at info@cromoteca.com
 */

package org.meshcms.core;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class GalleryThumbnail extends AbstractThumbnail {
  /**
   * Width and height of the thumbnail.
   */
  public static int THUMB_SIZE = 96;

  /**
   * Width and height of the image in the thumbnail.
   */
  public static int THUMB_IMAGE_SIZE = 94;

  private boolean highQuality;

  public String getSuggestedFileName() {
    return highQuality ? "meshcms_hq_gallery.jpg" : "meshcms_gallery.jpg";
  }

  protected boolean createThumbnail(File imageFile, File thumbnailFile) {
    BufferedImage image;

    try {
      image = ImageIO.read(imageFile);
    } catch (Exception ex) {
      return false;
    }

    if (image == null || image.getWidth() < 1) {
      return false;
    }

    BufferedImage thumb = new BufferedImage(THUMB_SIZE, THUMB_SIZE,
      BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D) thumb.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, THUMB_SIZE, THUMB_SIZE);

    int w = image.getWidth();
    int h = image.getHeight();
    int ix, iy, w0, h0;
    ix = iy = (THUMB_SIZE - THUMB_IMAGE_SIZE) / 2;

    if (w <= THUMB_IMAGE_SIZE && h <= THUMB_IMAGE_SIZE) {
      w0 = w;
      h0 = h;
      ix += (THUMB_IMAGE_SIZE - w0) / 2;
      iy += (THUMB_IMAGE_SIZE - h0) / 2;
    } else if (w > h) {
      w0 = THUMB_IMAGE_SIZE;
      h0 = w0 * h / w;
      iy += (THUMB_IMAGE_SIZE - h0) / 2;
    } else {
      h0 = THUMB_IMAGE_SIZE;
      w0 = h0 * w / h;
      ix += (THUMB_IMAGE_SIZE - w0) / 2;
    }

    if (highQuality) {
      BufferedImage resized = AbstractThumbnail.resize(image, w0, h0);
      g.drawImage(resized, ix, iy, null);
      resized.flush();
    } else {
      g.drawImage(image, ix, iy, w0, h0, null);
    }

    image.flush();
    g.setColor(DEFAULT_BORDER_COLOR);
    g.drawRect(0, 0, THUMB_SIZE - 1, THUMB_SIZE - 1);

    OutputStream os = null;

    try {
      os = new BufferedOutputStream(new FileOutputStream(thumbnailFile));
      ImageIO.write(thumb, "jpeg", os);
    } catch (IOException ex) {
      ex.printStackTrace();
      return false;
    } finally {
      thumb.flush();
      g.dispose();

      if (os != null) {
        try {
          os.close();
        } catch (IOException ex) {}
      }
    }

    return true;
  }

  public boolean isHighQuality() {
    return highQuality;
  }

  public void setHighQuality(boolean highQuality) {
    this.highQuality = highQuality;
  }
}