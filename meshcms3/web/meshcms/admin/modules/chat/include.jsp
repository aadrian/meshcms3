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
 
 Ajax Chat Module inspired by http://www.linuxuser.at/index.php?title=Most_Simple_Ajax_Chat_Ever
 Pierre Metras - 20060222
 See admin/chatserv.jsp for the server side.
 TODO:
 * Localisation of messages
 * Set the chat room dimensions from Module parameters
 * Support for different chat rooms (obtained from the Module parameters)
 * Degrade gracefully with old browsers (No XMLHTTP object)
 * Support for naughty words and spam detection.
 * Whatever you want to add...
--%>

<%@ page import="java.util.*" %>
<%@ page import="org.meshcms.core.*" %>

<%--
  Advanced parameters for this module:
  - css = (name of a css class)
--%>

<%
  String moduleCode = request.getParameter("modulecode");
  ModuleDescriptor md = null;
  
  if (moduleCode != null) {
    md = (ModuleDescriptor) request.getAttribute(moduleCode);
  }

  if (md == null) {
    if (!response.isCommitted()) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    return;
  }

  String cp = request.getContextPath();
  String style = md.getFullCSSAttribute("css");

  if (application.getAttribute("chatRoom") == null) {
    List room = Collections.synchronizedList(new LinkedList());
	application.setAttribute("chatRoom", room);
  }
%>

<script type="text/javascript">
// <![CDATA[
  /* Chat room id */
  var roomId = 'public';

  /* Timer id */
  var timeoutID;
  /* Refresh rate of chat room */
  var waitTime = 0;
  /* Add 2s to every display refresh rate when the user doesn't participate in discussion */
  var waitInc = 2000;


  /* Create and send a request with the url given, and call the callback function
  when the server answers. */
  function sendRequest(url, callback) {
    var request = false;
    if (window.XMLHttpRequest) {
      request = new XMLHttpRequest();
    } else if(window.ActiveXObject) {
      try {
              request = new ActiveXObject('Msxml2.XMLHTTP');
            } catch (e) {
              try {
                request = new ActiveXObject('Microsoft.XMLHTTP');
              } catch (e) {
              }
      }
    }
    if (!request) {
      writeStatus('Your browser does not support XMLHTTP object; please upgrade to use Ajax Chat');
      return;
    }

    request.onreadystatechange = function () {
      if (request.readyState == 4) {
        if (request.status == 200) {
          callback(request.responseText);
        } else {
          writeStatus('HTTP error; Status=' + request.status);
        }
      }
    };
    request.open('GET', url, true);
    request.send(null);
  }


  /* The more a user chats, the more often the display is refreshed. Users who don't
  participate in the discussion get slower and slower display refreshes to avoid
  to kill the server with the load. */
  function refreshChatRoom(reset) {
    if (reset) {
      waitTime = 2000;
    } else if (waitTime < 30000) {
      waitTime += waitInc;
    }
    clearTimeout(timeoutID);
    timeoutID = window.setTimeout('getChatRoom()', waitTime);
    writeStatus('Refresh in ' + (waitTime / 1000) + 's...');
  }


  /* Response acknowledge from server.jsp for a new message */
  function msgReceived(dummy) {
  }


  /* Send entered message */
  function submitMsg() {
    var url = '<%= cp + '/' + md.getModulePath() %>/server.jsp?u=' + encodeURIComponent(document.getElementById('chatuser').value) + '&m=' + encodeURIComponent(document.getElementById('chatmsg').value);
    sendRequest(url, msgReceived);
    document.getElementById('chatmsg').value = '';
    refreshChatRoom(true);
  }


  /* Response from server.jsp from the request for updated chat room content */
  function chatRoomReceived(content) {
    if (content != '') {
      //content = content.replace(/\n/g, '');
      if (document.getElementById('chatwindow').value != content) {
        document.getElementById('chatwindow').value = content;
            }
    }
    refreshChatRoom(false);
  }


  /* Request updated chat room content from chat server */
  function getChatRoom() { 
    var url = '<%= cp + '/' + md.getModulePath() %>/server.jsp?r=' + roomId;
    sendRequest(url, chatRoomReceived);
  }


  /* Validate the message when the user presses [Enter] key */
  function keyup(keyCode) {
    if (keyCode == 13 || keyCode == 3) {
      submitMsg();
    }
  }


  /* Write a message to the chat status line */
  function writeStatus(msg) {
    document.getElementById('chatstatus').innerHTML = msg;
  }
// ]]>
</script>

<textarea id="chatwindow" rows="10" cols="80" class="chatWindow" <%= style %> readonly></textarea>
<br>
<input id="chatuser" type="text" size="10" maxlength="20" value="Anonymous" class="chatUser" <%= style %>>&gt;&nbsp;
<input id="chatmsg" type="text" size="60" class="chatMsg" <%= style %> onkeyup="keyup(event.keyCode);">
<input type="button" value="OK" class="chatOK" <%= style %> onclick="submitMsg()">
<br>
<span id="chatstatus" class="chatStatus"> </span>
<br>

<script type="text/javascript">
// <![CDATA[
  /* Start access to chat server */
  writeStatus('Connecting to chat room...');
  timeoutID = window.setTimeout('getChatRoom()', waitTime);
// ]]>
</script>

