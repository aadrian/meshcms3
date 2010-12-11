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

import com.cromoteca.meshcms.server.core.Context;
import com.cromoteca.meshcms.server.core.ServerModule;
import com.cromoteca.meshcms.server.core.WebUtils;
import com.cromoteca.meshcms.server.webview.Scope;
import java.util.Date;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

public class FormSubmit extends ServerModule {
	private Form form;
	private FormSubmissionResult result;
	private String buttonLabel;
	private String successMessage;

	@Override
	public String process() {
		super.process();
		form = Form.get();

		if (form.isPost()) {
			if (form.isValid()) {
				FormSubmitHandler submitHandler = Context.getScopedSingleton(FormSubmitHandler.class,
						Scope.REQUEST);

				if (submitHandler != null) {
					Context.removeScopedSingleton(FormSubmitHandler.class, Scope.REQUEST);
				} else if (form.isEmail()) {
					submitHandler = new EmailSubmitter();
				}

				if (submitHandler != null) {
					result = submitHandler.submit();

					if (!result.isError()) {
						if (result.getMessage() != null) {
							Context.getRequestContext().storeFlashObject(result);
						}

						return getRelativeLink(rc.getPagePath());
					}
				}
			}
		} else {
			result = FormSubmissionResult.getPreviousResult(form.getId());
		}

		return null;
	}

	public boolean isShowMessage() {
		return result != null && result.getMessage() != null;
	}

	public FormSubmissionResult getResult() {
		return result;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}

	public Form getForm() {
		return form;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	private class EmailSubmitter implements FormSubmitHandler {
		public FormSubmissionResult submit() {
			StringBuilder sb = new StringBuilder();

			for (FormField field : form.getFields()) {
				String label = field.getLabel();

				if (label != null) {
					sb.append(label).append('\n');
					sb.append(field.getValue()).append("\n\n");
				}
			}

			HttpServletRequest request = Context.getRequest();

			try {
				InternetAddress senderAddress = new InternetAddress(form.getSender());
				InternetAddress recipientAddress = new InternetAddress(form
								.getDestination());
				Session mailSession = WebUtils.getMailSession();
				MimeMessage outMsg = new MimeMessage(mailSession);
				outMsg.setFrom(senderAddress);
				outMsg.addRecipient(Message.RecipientType.TO, recipientAddress);
				outMsg.setSubject("Message from " + request.getServerName());
				outMsg.setHeader("Content-Transfer-Encoding", "8bit");
				outMsg.setHeader("X-MeshCMS-Log",
					"Sent from " + request.getRemoteAddr() + " at " + new Date()
					+ " using page /" + rc.getPagePath());
				outMsg.setText(sb.toString());
				Transport.send(outMsg);
			} catch (Exception ex) {
				Context.log(ex);

				return FormSubmissionResult.getDefaultError(form.getId());
			}

			return new FormSubmissionResult(form.getId(), successMessage, false);
		}
	}
}
