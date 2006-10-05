<%--
 MeshCMS - A simple CMS based on SiteMesh
 Copyright (C) 2004-2006 Luciano Vernaschi

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

 You can contact the author at http://www.cromoteca.com
 and at info@cromoteca.com
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%
  final int MAX_MESSAGES = 100;
  final String RESET_CHAT_CMD = "clean room!";
  
  List room = (List) application.getAttribute("chatRoom");
  if (room == null) {
    room = Collections.synchronizedList(new LinkedList());
	  application.setAttribute("chatRoom", room);
  }
  
  String msg = request.getParameter("m");
  String user = request.getParameter("u");
  String roomId = request.getParameter("r");
  
  // Content requested
  if (roomId != null) {
    out.clear();
    String chatRoomText = (String) application.getAttribute("chatRoomCache");
	
	  // If we don't already have the content of the chat room in the cache, we
	  // generate it. Old Javascript (namely Konqueror3) doesn't support to receive
	  // raw Unicode characters (as sent by the HTTP stream), so we have to encode them...
    if (chatRoomText == null) {
      synchronized (room) {
        final StringBuffer sb = new StringBuffer(MAX_MESSAGES * 30);
	      int i = 0;
        for (final Iterator it = room.iterator(); it.hasNext() && i < MAX_MESSAGES; i++) {
		      final String s = (String) it.next();
		      final int length = s.length();
		      for (int j = 0; j < length; j++) {
		        final String hex = "0000" + Integer.toHexString((int) s.charAt(j));
		        sb.append("\\u");
		        sb.append(hex.substring(hex.length() - 4, hex.length()));
		      }
		      sb.append("\\u000A");
        }
		    chatRoomText = sb.toString();
        application.setAttribute("chatRoomCache", chatRoomText);
	    }
    }
    out.print(chatRoomText);
    out.flush();
  }
  
  // Message posted
  else if (msg != null) {
	  // Clear chat room cache
    application.setAttribute("chatRoomCache", null);
	
    synchronized (room) {
	    // Special command to clean room
      if (RESET_CHAT_CMD.equals(msg)) {
	    room.clear();
		
	    // Else accept message, even empty ones
      } else {
        room.add(0, ((user == null || "".equals(user)) ? "Anonymous" : user) + "> " + msg);
	  
        while (room.size() > MAX_MESSAGES) {
          room.remove(room.size() - 1);
        }
      }
    }
  }
%>

