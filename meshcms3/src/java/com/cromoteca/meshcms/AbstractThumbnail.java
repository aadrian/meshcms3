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

import java.io.*;
import com.cromoteca.util.*;

public abstract class AbstractThumbnail implements Finals {
  public abstract String getSuggestedFileName();
  protected abstract boolean createThumbnail(File imageFile, File thumbnailFile);
  
  public Path checkAndCreate(WebApp webApp, Path imagePath,
      String thumbnailFileName) {
    Path thumbnailPath = new Path(webApp.getConfiguration().getThumbnailsDir(),
        imagePath, getClass().getName());
    thumbnailPath = thumbnailPath.add(thumbnailFileName);
    
    File imageFile = webApp.getFile(imagePath);
    
    if (!imageFile.exists() || imageFile.isDirectory()) {
      return null;
    }
    
    File thumbnailFile = webApp.getFile(thumbnailPath);
    
    if (!thumbnailFile.exists() ||
        thumbnailFile.lastModified() < imageFile.lastModified()) {
      thumbnailFile.getParentFile().mkdirs();
      
      if (!createThumbnail(imageFile, thumbnailFile)) {
        return null;
      }
//      byte[] thumbData = createThumbnail(imageFile);
//      
//      if (thumbData == null || thumbData.length < 1) {
//        return null;
//      }
//      
//      OutputStream os = null;
//
//      try {
//        os = new BufferedOutputStream(new FileOutputStream(thumbnailFile));
//        Utils.copyStream(new ByteArrayInputStream(thumbData), os, true);
//      } catch (IOException ex) {
//        ex.printStackTrace();
//        return null;
//      } finally {
//        if (os != null) {
//          try {
//            os.close();
//          } catch (IOException ex) {}
//        }
//      }
    }
    
    return thumbnailPath;
  }
}
