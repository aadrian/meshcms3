package org.meshcms.core;

import java.util.Iterator;
import org.meshcms.util.Path;

public class SiteInfoCleaner extends Thread {

	private WebSite webSite;
	private Thread waitThread;

	public SiteInfoCleaner(WebSite webSite) {
		this.webSite = webSite;
	}

	public SiteInfoCleaner(WebSite webSite, Thread waitThread) {
		this.webSite = webSite;
		this.waitThread = waitThread;
	}

	/**
	 * Starts processing (in a separate thread if instantiated properly)
	 * and optionally waits for another thread to complete if that was
	 * specified in its constructor.
	 */
	public void run() {
		boolean success = true;
		if (waitThread != null) {
			try {
				waitThread.join();
			} catch (InterruptedException ex) {
				success = false;
			}
		}
		if (success)
			process();
	}

	/**
	 * Starts processing.
	 */
	public void process() {
 		SiteInfo siteInfo = cleanupSiteInfo();
 		siteInfo.store();
 		webSite.setSiteInfo(siteInfo);
	}

	/**
	 * Returns a new SiteInfo object that cleans-up the SiteInfo
	 * by removing info for pagePaths that no longer exist.
	 * Method is package private.
	 */
	private SiteInfo cleanupSiteInfo() {
		SiteInfo newSiteInfo = new SiteInfo();
		newSiteInfo.setWebSite(webSite);
		SiteInfo oldSiteInfo = webSite.getSiteInfo();
		Iterator iter = webSite.getSiteMap().getPagesList().iterator();
		while (iter.hasNext()) {
			Path pagePath = ((PageInfo) iter.next()).getPath();
			newSiteInfo.setPageTitle(pagePath, oldSiteInfo.getPageTitle(pagePath));
			newSiteInfo.setPageTheme(pagePath, oldSiteInfo.getPageTheme(pagePath));
			newSiteInfo.setPageScore(pagePath, oldSiteInfo.getPageScore(pagePath));
			newSiteInfo.setHideSubmenu(pagePath, oldSiteInfo.getHideSubmenu(pagePath));
		}
		return newSiteInfo;
	}
}
