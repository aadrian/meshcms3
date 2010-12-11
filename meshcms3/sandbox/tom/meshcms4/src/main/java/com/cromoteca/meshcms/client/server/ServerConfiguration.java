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
package com.cromoteca.meshcms.client.server;

import com.cromoteca.meshcms.client.toolbox.Time;
import java.io.Serializable;

/**
 * Manages the configuration parameters of a MeshCMS installation.
 */
public class ServerConfiguration implements Serializable {
	/**
	 * Contains the extensions of files that are visually editable by default.
	 */
	public static final String[] DEFAULT_VISUAL_EXTENSIONS = { "html", "htm" };
	private String mailServer;
	private String smtpPassword;
	private String smtpUsername;
	private String[] visualExtensions;
	private boolean highQualityThumbnails;
	private int backupLife;
	private int statsLength;

	public ServerConfiguration() {
		setHighQualityThumbnails(true);
		setBackupLife(15);
		setStatsLength(3);
		setMailServer("");
		setSmtpUsername("");
		setSmtpPassword("");
		setVisualExtensions(DEFAULT_VISUAL_EXTENSIONS);
	}

	/**
	 * Returns the minimum time before deleting a backup file, measured in
	 * milliseconds.
	 */
	public long getBackupLifeMillis() {
		return backupLife * (long) Time.DAY;
	}

	/**
	 * Sets the minimum time before deleting a backup file, measured in days.
	 */
	public void setBackupLife(int backupLife) {
		this.backupLife = Math.max(backupLife, 0);
	}

	/**
	 * Sets the length of stats (hit counts) measured in days. Please note that
	 * this value is fixed when the web application is initialized, so if the value
	 * is changed, the new value won't be used until the next restart of the web
	 * application.
	 */
	public void setStatsLength(int statsLength) {
		this.statsLength = Math.max(statsLength, 1);
	}

	public String getMailServer() {
		return mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String[] getVisualExtensions() {
		return visualExtensions;
	}

	public void setVisualExtensions(String[] visualExtensions) {
		this.visualExtensions = visualExtensions;
	}

	public boolean isHighQualityThumbnails() {
		return highQualityThumbnails;
	}

	public void setHighQualityThumbnails(boolean highQualityThumbnails) {
		this.highQualityThumbnails = highQualityThumbnails;
	}

	public int getBackupLife() {
		return backupLife;
	}

	public int getStatsLength() {
		return statsLength;
	}
}
