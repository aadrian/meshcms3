/*
 * Copyright 2004-2010 Luciano Vernaschi
 *
 * This file is part of MeshCMS.
 *
 * MeshCMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MeshCMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MeshCMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cromoteca.meshcms.server.modules;

import com.cromoteca.meshcms.client.server.ZoneItem;
import com.cromoteca.meshcms.client.toolbox.Path;
import com.cromoteca.meshcms.server.core.BackingBean;
import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.Server;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.core.SessionUser;
import com.cromoteca.meshcms.server.core.WebUtils;
import com.cromoteca.meshcms.server.core.ZoneOutput;
import com.cromoteca.meshcms.server.core.ZoneOutput.ModuleOutput;
import com.cromoteca.meshcms.server.services.AvatarServlet;
import com.cromoteca.meshcms.server.storage.File;
import com.cromoteca.meshcms.server.toolbox.IO;
import com.cromoteca.meshcms.server.toolbox.ReverseComparator;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.toolbox.Web;
import com.cromoteca.meshcms.server.webview.Scope;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

public class Comments extends ServerModule {
	public static final Pattern DATE_REGEX = Pattern.compile("\\d{14}");
	private File commentsDir;
	private String notify;
	private boolean avoidUpperCaseComments;
	private boolean captcha;
	private boolean disable;
	private boolean disableAll;
	private boolean gravatars;
	private boolean newer;
	private boolean parseText;

	public void setDisableAll(boolean disableAll) {
		this.disableAll = disableAll;
	}

	public void setNotify(String notify) {
		this.notify = notify;
	}

	public void setAvoidUpperCaseComments(boolean avoidUpperCaseComments) {
		this.avoidUpperCaseComments = avoidUpperCaseComments;
	}

	public void setDisable(boolean disable) {
		this.disable = disable;
	}

	public void setCaptcha(boolean captcha) {
		this.captcha = captcha;
	}

	public void setNewer(boolean newer) {
		this.newer = newer;
	}

	public void setParseText(boolean parseText) {
		this.parseText = parseText;
	}

	public void setGravatars(boolean gravatars) {
		this.gravatars = gravatars;
	}

	private List<Comment> getComments() {
		List<Comment> comments = new ArrayList<Comment>();

		for (File commentFile : commentsDir.getChildren()) {
			Comment c = null;

			try {
				if ("txt".equals(Strings.getExtension(commentFile.getName(), false))) {
					String body = IO.readFully(commentFile);
					String title = "Anonymous";
					int nn = body.indexOf("\n\n");
					int nnLen = 2;

					if (nn < 0) {
						nn = body.indexOf("\r\n\r\n");
						nnLen = 4;
					}

					if (nn >= 0) {
						title = body.substring(0, nn);
						body = body.substring(nn + nnLen);
					}

					c = new Comment();
					c.setComment(body);
					c.setName(title);

					Matcher m = DATE_REGEX.matcher(commentFile.getName());

					try {
						if (m.find()) {
							c.setDate(WebUtils.numericDateFormatter.parse(m.group()));
						}
					} catch (ParseException ex) {}

					if (c.getDate() == null) {
						c.setDate(new Date(commentFile.getLastModified()));
					}
				} else {
					c = Context.loadFromJSON(Comment.class, commentFile);
				}

				if (c != null) {
					c.setParseText(parseText);
					c.setGravatars(gravatars);
					c.setAvoidUpperCaseComments(avoidUpperCaseComments);
					comments.add(c);
				}
			} catch (IOException ex) {}
		}

		Comparator<Comment> comparator = new Comparator<Comment>() {
				public int compare(Comment c1, Comment c2) {
					return c1.getDate().compareTo(c2.getDate());
				}
			};

		if (newer) {
			comparator = new ReverseComparator<Comment>(comparator);
		}

		Collections.sort(comments, comparator);

		return comments;
	}

	@Override
	public void run() throws IOException, ServletException {
		Path path = Context.getServer().getCMSPath().add("comments")
					.add(rc.getFilePath()).add(getZoneOutput().getZone().getName());
		commentsDir = rc.getWebSite().getFile(path);
		commentsDir.create(true);

		ZoneOutput zo = getZoneOutput();

		for (Comment comment : getComments()) {
			ModuleOutput mo = zo.add();
			String html = zo.runTemplate(getModule().getPath().add("comment.jsp"),
					comment);
			mo.addContent(html);
		}

		Context.setScopedSingleton(FormSubmitHandler.class, new CommentSubmitter(),
			Scope.REQUEST);

		ResourceBundle constants = Context.getConstants();
		ModuleOutput mo = zo.add();

		if (disable || disableAll) {
			mo.addContent(constants.getString("commentsClosed"));
		} else {
			SessionUser user = Context.getUser();
			List<ZoneItem> items = getZoneOutput().getZone().getItems();
			int idx = getPosition();

			ZoneItem formItem = new ZoneItem();
			formItem.setModuleName("form");
			formItem.getParameters().put("destination", "");
			formItem.getParameters().put("clientScript", "true");
			items.add(++idx, formItem);

			ZoneItem nameItem = new ZoneItem();
			nameItem.setModuleName("textfield");
			nameItem.getParameters().put("label", constants.getString("commentsName"));
			nameItem.getParameters().put("name", "name");
			nameItem.getParameters().put("required", "true");

			if (user != null) {
				nameItem.getParameters().put("value", user.getDisplayName());
			}

			items.add(++idx, nameItem);

			if (gravatars) {
				ZoneItem emailItem = new ZoneItem();
				emailItem.setModuleName("textfield");
				emailItem.getParameters()
						.put("label", constants.getString("commentsEmail"));
				emailItem.getParameters().put("name", "email");

				if (user != null) {
					emailItem.getParameters().put("value", user.getUser().getEmail());
				}

				items.add(++idx, emailItem);
			}

			ZoneItem commentItem = new ZoneItem();
			commentItem.setModuleName("textarea");
			commentItem.getParameters()
					.put("label", constants.getString("commentsText"));
			commentItem.getParameters().put("name", "comment");
			commentItem.getParameters().put("required", "true");
			commentItem.getParameters().put("columns", "60");
			commentItem.getParameters().put("rows", "8");
			items.add(++idx, commentItem);

			if (captcha) {
				ZoneItem captchaItem = new ZoneItem();
				captchaItem.setModuleName("recaptcha");
				items.add(++idx, captchaItem);
			}

			ZoneItem submitItem = new ZoneItem();
			submitItem.setModuleName("formsubmit");
			submitItem.getParameters()
					.put("buttonLabel", constants.getString("commentsSubmit"));
			submitItem.getParameters()
					.put("successMessage",
						"<em>" + constants.getString("commentsSubmitted") + "</em>");
			items.add(++idx, submitItem);
		}
	}

	public static class Comment extends BackingBean implements Serializable {
		private Date date;
		private String comment;
		private String email;
		private String name;
		private transient boolean avoidUpperCaseComments;
		private transient boolean gravatars;
		private transient boolean parseText;

		public void setGravatars(boolean gravatars) {
			this.gravatars = gravatars;
		}

		public void setParseText(boolean parseText) {
			this.parseText = parseText;
		}

		public void setAvoidUpperCaseComments(boolean avoidUpperCaseComments) {
			this.avoidUpperCaseComments = avoidUpperCaseComments;
		}

		public String getComment() {
			return comment;
		}

		public String getCommentText() {
			String text = comment.trim();
			text = Web.convertToHTMLEntities(text, IO.SYSTEM_CHARSET, true);

			if (avoidUpperCaseComments) {
				int cnt = 0;

				for (char c : text.toCharArray()) {
					if (Character.isUpperCase(c)) {
						cnt++;
					}
				}

				if (cnt > text.length() / 2) {
					text = text.toLowerCase();
				}
			}

			if (parseText) {
				text = Web.findLinks(text, true);
				text = Web.findEmails(text);
			}

			text = text.replace("\r", "");
			text = text.replaceAll("[ \\t]*\\n", "\n");
			text = text.replaceAll("\\n{2,}", "</p><p>");
			text = text.replace("\n", "<br />");
			text = "<p>" + text + "</p>";

			return text;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isGravatars() {
			return gravatars;
		}

		public String getGravatar() {
			// TODO: remove static path /meshcms
			if (Strings.isNullOrEmpty(email)) {
				String md5 = Strings.getMD5(name);

				return adjustPath("/meshcms/avatar/" + md5 + ".png");
			} else {
				String md5 = Strings.getMD5(email);

				return "http://www.gravatar.com/avatar/" + md5 + ".jpg?s="
				+ AvatarServlet.SIZE + "&d="
				+ Web.getContextHomeURL(Context.getRequest()) + "/meshcms/avatar/"
				+ md5 + ".png";
			}
		}
	}

	public class CommentSubmitter implements FormSubmitHandler {
		public FormSubmissionResult submit() {
			Comment c = new Comment();
			c.setDate(new Date());

			Form form = Form.get();
			List<FormField> fields = form.getFields();

			for (FormField field : fields) {
				String name = field.getName();

				if (name.equals("name")) {
					c.setName(field.getValue());
				} else if (name.equals("email")) {
					c.setEmail(field.getValue());
				} else if (name.equals("comment")) {
					c.setComment(field.getValue());
				}
			}

			String commentFileName = "mcc_"
				+ WebUtils.numericDateFormatter.format(new Date())
				+ Server.JSON_EXTENSION;

			try {
				Context.storeToJSON(c, commentsDir.getDescendant(commentFileName));
				rc.getWebSite().getFile(rc.getFilePath())
						.setLastModified(System.currentTimeMillis());
			} catch (IOException ex) {
				Context.log(ex);

				return FormSubmissionResult.getDefaultError(form.getId());
			}

			if (!(notify == null || Strings.isInvalidEmail(notify))) {
				HttpServletRequest request = Context.getRequest();
				StringBuilder sb = new StringBuilder();
				sb.append("A comment has been added to ");
				sb.append(rc.getURL());
				sb.append(" by ");
				sb.append(c.getName());
				sb.append(" (");
				sb.append(request.getRemoteAddr());
				sb.append("):\n\n");
				sb.append(c.getComment());

				try {
					InternetAddress address = new InternetAddress(notify);
					Session mailSession = WebUtils.getMailSession();
					MimeMessage outMsg = new MimeMessage(mailSession);
					outMsg.setFrom(address);
					outMsg.addRecipient(Message.RecipientType.TO, address);
					outMsg.setHeader("Content-Type", "text/plain; charset=utf-8");
					outMsg.setSubject("Comment added on "
						+ rc.getWebSite().getSiteConfiguration().getSiteHost(), "utf-8");
					outMsg.setText(sb.toString(), "utf-8");
					Transport.send(outMsg);
				} catch (Exception ex) {
					Context.log(ex);
				}
			}

			return new FormSubmissionResult(form.getId(),
				Context.getConstants().getString("commentsSubmitted"), false);
		}
	}
}
