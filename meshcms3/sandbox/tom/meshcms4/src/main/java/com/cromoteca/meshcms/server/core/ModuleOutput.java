package com.cromoteca.meshcms.server.core;

import com.cromoteca.meshcms.server.core.Context.RequestContext;
import com.cromoteca.meshcms.server.toolbox.Strings;
import com.cromoteca.meshcms.server.toolbox.Web;
import java.io.Serializable;

public class ModuleOutput implements Serializable {
	private String notes;
	private String title;
	private StringBuilder content;

	public ModuleOutput() {
		content = new StringBuilder();
	}

	public String getContent() {
		String html = content.toString();
		RequestContext rc = Context.getRequestContext();

		if (rc.getWebSite().getSiteConfiguration().isReplaceThumbnails()) {
			html = WebUtils.replaceThumbnails(html, Context.getContextPath(),
					rc.getPagePath());
		}

		return Web.convertToHTMLEntities(html);
	}

	public void addContent(String content) {
		if (content != null) {
			this.content.append(content.trim());
		}
	}

	public boolean isEmpty() {
		return content.length() == 0 && Strings.isNullOrEmpty(title)
				&& Strings.isNullOrEmpty(notes);
	}

	public String getNotes() {
		return Web.convertToHTMLEntities(notes);
	}

	public void setNotes(String notes) {
		if (this.notes == null) {
			this.notes = notes == null ? null : notes.trim();
		}
	}

	public String getTitle() {
		return Web.convertToHTMLEntities(title);
	}

	public void setTitle(String title) {
		this.title = title == null ? null : title.trim();
	}
}
