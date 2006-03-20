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
import java.util.*;
import com.cromoteca.util.*;

/**
 * Profile of a user. Modifications made by calling the set methods are not
 * stored until you use {@link #save}.
 */
public class UserInfo implements Serializable {
  /**
   * Permission to add other users.
   */
  public static final int CAN_ADD_USERS = 1;

  /**
   * Permission to edit pages (in the home path of the user profile).
   */
  public static final int CAN_EDIT_PAGES = 2;

  /**
   * Permission to manage files.
   */
  public static final int CAN_MANAGE_FILES = 4;

  /**
   * Permission to view other user profiles.
   */
  public static final int CAN_VIEW_OTHER_USERINFO = 8;

  /**
   * Permission to do maintainance operations.
   */
  public static final int CAN_DO_ADMINTASKS = 16;

  /**
   * Permission to browse files.
   */
  public static final int CAN_BROWSE_FILES = 32;
  
  /**
   * Permissions for guest user (non-logged in).
   */
  public static final int GUEST = 0;

  /**
   * Permissions for a member (can't edit files).
   */
  public static final int MEMBER = CAN_BROWSE_FILES |
                                   CAN_VIEW_OTHER_USERINFO;

  /**
   * Permissions for an editor (can edit files, but is not an administrator).
   */
  public static final int EDITOR = CAN_EDIT_PAGES |
                                   CAN_MANAGE_FILES |
                                   CAN_VIEW_OTHER_USERINFO |
                                   CAN_BROWSE_FILES;
  /**
   * Permissions for an administrator (full permissions).
   */
  public static final int ADMIN = 0x00FFFFFF;

  protected static final String USERNAME = "P_USN";
  protected static final String PASSWORD = "P_PWS";
  protected static final String HOME_PATH = "P_HPT";
  protected static final String PERMISSIONS = "P_PRM";
  protected static final String E_MAIL = "P_EML";
  protected static final String LANGUAGE = "P_LNG";

  /**
   * Names for user detail fields.
   */
  public static String[] DETAILS = {
    "salutation",
    "name",
    "surname",
    "company",
    "address",
    "zip",
    "city",
    "state",
    "country",
    "phone_number",
    "fax_number",
    "mobile_phone_number"
  };

  /**
   * Characters allowed in a username.
   */
  protected static String VALID_USERNAME_CHARS =
    "abcdefghijklmnopqrstuvwxyz._0123456789";
  protected static String SALT = "LV";

  protected Properties info;

  /**
   * Creates a new empty instance. Use {@link #load} to load a defined user.
   */
  public UserInfo() {
    loadGuest();
  }

  /**
   * Sets the username for this user.
   */
  public void setUsername(String username) {
    if (username != null) {
      info.setProperty(USERNAME, username);
    }
  }

  /**
   * Returns the user's username.
   */
  public String getUsername() {
    return getValue(USERNAME);
  }

  /**
   * Sets the password for this user. The password will be encrypted.
   */
  public void setPassword(String password) {
    info.setProperty(PASSWORD, cryptPassword(password));
  }

  /**
   * Sets the password for this user after verification of the old password.
   * The password will be encrypted.
   *
   * @return the result of the operation
   */
  public boolean updatePassword(String oldPassword, String newPassword) {
    if (verifyPassword(oldPassword)) {
      setPassword(newPassword);
      return true;
    }

    return false;
  }

  /**
   * Returns the user's (encrypted) password.
   */
  public String getPassword() {
    return getValue(PASSWORD);
  }

  /**
   * Sets the e-mail address of this user.
   * {@link com.cromoteca.util.Utils#checkAddress} is used to verify the new
   * address.
   *
   * @return the result of the operation
   */
  public boolean setEmail(String email) {
    if (Utils.checkAddress(email)) {
      info.setProperty(E_MAIL, email);
      return true;
    }

    return false;
  }

  /**
   * Returns the user's e-mail address.
   */
  public String getEmail() {
    return getValue(E_MAIL);
  }

  /**
   * Sets the home path for the user. A user can't edit files outside his own
   * home path.
   */
  public void setHomePath(Path homePath) {
    if (homePath != null) {
      info.setProperty(HOME_PATH, homePath.toString());
    }
  }

  /**
   * Returns the user's home path.
   */
  public Path getHomePath() {
    return new Path(getValue(HOME_PATH));
  }

  /**
   * Sets permissions for the user. This method should be called when creating
   * the user.
   */
  public void setPermissions(int permissions) {
    info.setProperty(PERMISSIONS, Integer.toHexString(permissions));
  }

  /**
   * Returns the user's permissions.
   */
  public int getPermissions() {
    try {
      return Integer.parseInt(getValue(PERMISSIONS), 16);
    } catch (Exception ex) {}

    return GUEST;
  }
  
  /**
   * Returns the preferred locale for the user, in a form like
   * <code>en_US</code>, <code>it</code> or similar.
   */
  public String getPreferredLocaleCode() {
    return getValue(LANGUAGE);
  }
  
  /**
   * Sets the preferred locale for the user.
   */
  public void setPreferredLocaleCode(String localeCode) {
    if (localeCode == null || localeCode.length() < 2) {
      localeCode = "en_US";
    }

    info.setProperty(LANGUAGE, localeCode);
  }

  /**
   * Loads the guest user.
   */
  public void loadGuest() {
    info = new Properties();
  }

  /**
   * Loads a specific user.
   */
  public boolean load(WebApp webApp, String username, String password) {
    if (Utils.isNullOrEmpty(username)) {
      return false;
    }

    File userFile = getUserFile(webApp, username);

    if (!userFile.exists()) {
      return false;
    }

    FileInputStream fis = null;

    try {
      fis = new FileInputStream(userFile);
      Properties p = new Properties();
      p.load(fis);

      if (!p.getProperty(PASSWORD).equals(cryptPassword(password))) {
        return false;
      }

      info = p;
    } catch (IOException ex) {
      webApp.log("Can't load user file for " + username, ex);
      return false;
    } catch (Exception ex) {
      return false;
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException ex) {
          webApp.log("Can't close user file for " + username, ex);
        }
      }
    }

    return true;
  }

  /**
   * Stores the user's profile in a file.
   */
  public boolean save(WebApp webApp) {
    File userFile = getUserFile(webApp, getUsername());
    FileOutputStream fos = null;

    try {
      fos = new FileOutputStream(userFile);
      info.store(fos, "Properties for " + getUsername());
    } catch (Exception ex) {
      ex.printStackTrace();
      return false;
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException ex) {}
      }
    }

    return true;
  }

  /**
   * Crypts the password if it has not been encrypted yet.
   */
  private String cryptPassword(String password) {
    if (Utils.isNullOrEmpty(password)) {
      return "";
    }

    if (password.startsWith(SALT)) {
      return password;
    }

    return com.kingwoodcable.locutus.jfd.JCrypt.crypt(SALT, password);
  }

  /**
   * Checks if the username is valid (i.e. contains characters in
   * {@link #VALID_USERNAME_CHARS} only).
   */
  public static boolean verifyUsername(String username) {
    if (Utils.isNullOrEmpty(username)) {
      return false;
    }

    for (int i = 0; i < username.length(); i++) {
      if (VALID_USERNAME_CHARS.indexOf(username.charAt(i)) == -1) {
        return false;
      }
    }

    return true;
  }

  /**
   * Verifies the given password agains the one in the current profile.
   */
  public boolean verifyPassword(String password) {
    return this.getPassword().equals(cryptPassword(password));
  }

  private File getUserFile(WebApp webApp, String username) {
    return new File(webApp.getUsersDirectory(), username + ".txt");
  }

  /**
   * Verifies the permissions to do a certain thing. Example:
   * <code>user.canDo(UserInfo.CAN_EDIT_PAGES)</code>
   */
  public boolean canDo(int what) {
    return (getPermissions() & what) != 0;
  }

  /**
   * Verifies all permissions to write the file at a certain path in the web
   * application.
   */
  public boolean canWrite(WebApp webApp, Path filePath) {
    if (filePath == null || !canDo(CAN_EDIT_PAGES) ||
        filePath.isContainedIn(webApp.getAdminPath())) {
      return false;
    }

    return filePath.isContainedIn(getHomePath());
  }

  /**
   * Sets a user's detail. Available details are specified in
   * {@link #DETAILS}. Other details can be set, but they will not be stored
   * when {@link #save} is called.
   *
   * @see #getValue
   */
  public boolean setDetail(String name, String value) {
    if (Utils.searchString(DETAILS, name, false) != -1) {
      info.setProperty(name, value);
      return true;
    }

    return false;
  }

  /**
   * Returns the value of a specific property. It is used internally, but can
   * be use to retrieve the value of user's details.
   *
   * @see #setDetail
   */
  public String getValue(String name) {
    return Utils.noNull(info.getProperty(name));
  }

  /**
   * Returns the value of the given user detail.
   */
  public String getDetailValue(String name) {
    if (name != null) {
      name = name.toLowerCase();
      
      if (Utils.searchString(DETAILS, name, false) != -1) {
        return getValue(name);
      }
    }
    
    return null;
  }
  
  /**
   * Returns the name of the user detail at the given index.
   */
  public String getDetailName(int index) {
    return DETAILS[index];
  }

  /**
   * Returns a string suitable to describe the user. It can be his full name,
   * partial name or username, according to the available data.
   */
  public String getDisplayName() {
    String name = getValue(DETAILS[1]);
    String surname = getValue(DETAILS[2]);

    if (name.equals("") && surname.equals("")) {
      return isGuest() ? "guest" : getUsername();
    }

    if (name.equals("")) {
      return surname;
    }

    if (surname.equals("")) {
      return name;
    }

    return name + " " + surname;
  }

  /**
   * Checks if the user is a guest.
   */
  public boolean isGuest() {
    return getPermissions() == GUEST;
  }

  /**
   * Checks if the user exists. A user exists when the corresponding file
   * exists.
   */
  public boolean exists(WebApp webApp, String username) {
    return getUserFile(webApp, username).exists();
  }
}
