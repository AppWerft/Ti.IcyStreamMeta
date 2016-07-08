/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package de.appwerft.icystreammeta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import java.util.regex.PatternSyntaxException;

// This proxy can be created by calling Icystreammeta.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = IcyMetaModule.class)
public class IcyMetaProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "ICYMETA=============";
	private URL url = null;
	IcyStreamMeta metaClient = null;
	KrollFunction loadCallback = null;
	KrollFunction errorCallback = null;

	// Constructor
	public IcyMetaProxy() {
		super();
		metaClient = new IcyStreamMeta();
	}

	// Handle creation options
	@Override
	public void handleCreationDict(KrollDict options) {
		Log.d(LCAT, "Start handleCreationDict");
		super.handleCreationDict(options);
		if (options.containsKey(TiC.PROPERTY_URL)) {
			try {
				url = new URL(options.getString(TiC.PROPERTY_URL));
				metaClient.setStreamUrl(url);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (options.containsKey(TiC.PROPERTY_ONLOAD)) {
			Object cb = options.get(TiC.PROPERTY_ONLOAD);
			if (cb instanceof KrollFunction) {
				loadCallback = (KrollFunction) cb;
			} else {
				Log.e(LCAT, "onload is not KrollFunction");
			}
			try {
				metaClient.refreshMeta();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (options.containsKey(TiC.PROPERTY_ONERROR)) {
			Object cb = options.get(TiC.PROPERTY_ONERROR);
			if (cb instanceof KrollFunction) {
				errorCallback = (KrollFunction) cb;
			} else {
				Log.e(LCAT, "onerroris not KrollFunction");
			}
		}
	}

	@Kroll.method
	public String getStreamURL() {
		return this.url.toString();
	}

	@Kroll.method
	public String getStreamTitle() throws IOException {
		return metaClient.getStreamTitle();
	}

	@Kroll.method
	public String getArtist() throws IOException {
		return metaClient.getArtist();
	}

	@Kroll.method
	public void setStreamUrl(String url) throws IOException {
		metaClient.setStreamUrl(new URL(url));
	}

	@Kroll.method
	public boolean isError() {
		return metaClient.isError();
	}

	@Kroll.method
	public String getTitle() throws IOException {
		return metaClient.getTitle();
	}

	@Kroll.method
	public void refreshMeta() throws IOException {
		metaClient.refreshMeta();
	}

	private class IcyStreamMeta {
		private URL streamUrl;
		private Map<String, String> metadata;
		private boolean isError;

		public IcyStreamMeta() {
			// setStreamUrl(streamUrl);
			isError = false;
		}

		@SuppressWarnings("unused")
		public URL getStreamUrl() {
			return streamUrl;
		}

		public String getStreamTitle() throws IOException {
			Map<String, String> data = getMetadata();
			if (data == null) {
				return "";
			}
			if (!data.containsKey("StreamTitle"))
				return "";
			return data.get("StreamTitle").trim();
		}

		public String getArtist() throws IOException,
				StringIndexOutOfBoundsException {
			Map<String, String> data = getMetadata();
			if (data == null || !data.containsKey("StreamTitle")) {
				return "";
			}
			String streamTitle = data.get("StreamTitle");
			String title = streamTitle.substring(0, streamTitle.indexOf("-"));
			return title.trim();
		}

		public String getTitle() throws IOException,
				StringIndexOutOfBoundsException {
			Map<String, String> data = getMetadata();

			if (data == null || !data.containsKey("StreamTitle")) {
				return "";
			}
			String streamTitle = data.get("StreamTitle");
			String artist = streamTitle.substring(streamTitle.indexOf("-") + 1);
			return artist.trim();
		}

		public void refreshMeta() throws IOException {
			retreiveMetadata();
		}

		private Map<String, String> getMetadata() throws IOException {
			if (metadata == null) {
				refreshMeta();
			}

			return metadata;
		}

		private void retreiveMetadata() {
			URLConnection con = null;
			try {
				con = streamUrl.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				sendError(e.getMessage());
			}
			con.setRequestProperty("Icy-MetaData", "1");
			con.setRequestProperty("Connection", "close");
			try {
				con.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				sendError(e.getMessage());
			}
			int metaDataOffset = 0;
			Map<String, List<String>> headers = con.getHeaderFields();
			InputStream stream = null;
			try {
				stream = con.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				sendError(e.getMessage());
			}
			if (headers.containsKey("icy-metaint")) {
				// Headers are sent via HTTP
				metaDataOffset = Integer.parseInt(headers.get("icy-metaint")
						.get(0));
			}
			// In case no data was sent
			if (metaDataOffset == 0) {
				isError = true;
				return;
			}
			int b;
			int count = 0;
			int metaDataLength = 4080; // 4080 is the max length
			boolean inData = false;
			StringBuilder metaData = new StringBuilder();
			try {
				while ((b = stream.read()) != -1) {
					count++;
					if (count == metaDataOffset + 1) {
						metaDataLength = b * 16;
					}
					if (count > metaDataOffset + 1
							&& count < (metaDataOffset + metaDataLength)) {
						inData = true;
					} else {
						inData = false;
					}
					if (inData) {
						if (b != 0) {
							metaData.append((char) b);
						}
					}
					if (count > (metaDataOffset + metaDataLength)) {
						break;
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			KrollDict resultDict = new KrollDict();
			String[] metaParts = metaData.toString().split(";");
			for (int i = 0; i < metaParts.length; i++) {
				String line = metaParts[i];
				String[] keyval = line.split("=");
				String key = keyval[0];
				String val = keyval[1];
				String sanitizedValue = "";
				try {
					sanitizedValue = val.substring(1, val.length() - 1)
							.replaceAll("\r", "");
					resultDict.put(key, sanitizedValue);
				} catch (PatternSyntaxException ex) {
					Log.d(LCAT, "PatternSyntaxException" + ex.getDescription());
				} catch (IllegalArgumentException ex) {
				} catch (IndexOutOfBoundsException ex) {
				}
			}
			if (loadCallback != null)
				loadCallback.call(getKrollObject(), resultDict);
			else
				Log.e(LCAT, "loadCallback is null");
		}

		public boolean isError() {
			return isError;
		}

		public void setStreamUrl(URL streamUrl) {
			this.metadata = null;
			this.streamUrl = streamUrl;
			this.isError = false;
		}

		private void sendError(String msg) {
			KrollDict resultDict = new KrollDict();
			resultDict.put("error", msg);
			if (errorCallback != null)
				errorCallback.call(getKrollObject(), resultDict);
			else
				Log.e(LCAT, "errorCallback is null");

		}
	}
}