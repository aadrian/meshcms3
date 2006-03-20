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

package com.cromoteca.meshcms;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import com.cromoteca.util.*;

public class ResizedThumbnail extends AbstractThumbnail {
  int reqW, reqH;
  
  public ResizedThumbnail(String requestedWidth, String requestedHeight) {
    reqW = Utils.parseInt(requestedWidth, -1);
    reqH = Utils.parseInt(requestedHeight, -1);
  }
  
  public String getSuggestedFileName() {
    return (reqW < 1 ? "" : Integer.toString(reqW)) + "x" +
           (reqH < 1 ? "" : Integer.toString(reqH)) + ".jpg";
  }

  protected boolean createThumbnail(File imageFile, File thumbnailFile) {
    BufferedImage image = null;
    
    try {
      image = ImageIO.read(imageFile);
    } catch (Exception ex) {
      return false;
    }

    if (image == null || image.getWidth() < 1) {
      return false;
    }

    int w = image.getWidth();
    int h = image.getHeight();
    int w0, h0;
    
    if (reqW < 1 && reqH < 1) {
      w0 = h0 = 100;
    } else if (reqW < 1) {
      h0 = reqH;
      w0 = w * h0 / h;
    } else if (reqH < 1) {
      w0 = reqW;
      h0 = h * w0 / w;
    } else {
      w0 = reqW;
      h0 = reqH;
    }

    BufferedImage thumb = new BufferedImage(w0, h0, BufferedImage.TYPE_INT_RGB);
    Graphics g = thumb.getGraphics();
    g.drawImage(image, 0, 0, w0, h0, null);
    image.flush();
    
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
}
