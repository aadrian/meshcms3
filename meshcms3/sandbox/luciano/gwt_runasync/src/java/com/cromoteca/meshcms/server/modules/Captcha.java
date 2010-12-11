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
import javax.servlet.http.HttpServletRequest;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class Captcha extends FormField {
	private String privateKey;
	private String publicKey;
	private String theme;
	private boolean error;
	private boolean showToKnownUsers;

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public void setShowToKnownUsers(boolean showToKnownUsers) {
		this.showToKnownUsers = showToKnownUsers;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	@Override
	protected void onPost(Form form) {
		if (isShow()) {
			try {
				System.setProperty("networkaddress.cache.ttl", "30");
			} catch (SecurityException ex) {
				Context.log(ex);
			}

			HttpServletRequest request = Context.getRequest();
			String remoteAddr = request.getRemoteAddr();
			ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
			reCaptcha.setPrivateKey(privateKey);

			String challenge = request.getParameter("recaptcha_challenge_field");
			String uresponse = request.getParameter("recaptcha_response_field");
			ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr,
					challenge, uresponse);

			if (error = !reCaptchaResponse.isValid()) {
				setInvalid(true);
				form.setValid(false);
			}
		}
	}

	public boolean isShow() {
		// TODO: verify the logic of this method
		return (showToKnownUsers || Context.getUser() == null)
				&& Form.get().isSelfAction();
	}

	public boolean isError() {
		return error;
	}

	public String getErrorMessage() {
		return Context.getConstants().getString("reCaptchaError");
	}

	public String getMarkup() {
		ReCaptcha c = ReCaptchaFactory.newReCaptcha(publicKey, privateKey, true);

		return c.createRecaptchaHtml("ERROR", theme, null);
	}
}
