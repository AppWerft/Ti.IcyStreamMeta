package de.appwerft.icymetaclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.PatternSyntaxException;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.apache.commons.io.IOUtils;

import android.os.AsyncTask;
import java.nio.ByteBuffer;

// This proxy can be created by calling Icystreammeta.createExample({message: "hello world"})
@Kroll.proxy(creatableInModule = IcymetaclientModule.class)
public class IcyMetaClientProxy extends KrollProxy {
	// Standard Debugging variables
	private static final String LCAT = "ICYMETA=============";
	private URL url = null;
	private int interval = 0; // sec
	private boolean autoStart = false;
	private String charset = "UTF-8";

	IcyStreamMeta metaClient = null;
	KrollFunction loadCallback = null;
	KrollFunction errorCallback = null;

	// Constructor
	public IcyMetaClientProxy() {
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
		if (options.containsKey("interval")) {
			interval = options.getInt("interval");
		}
		if (options.containsKey("charset")) {
			charset = options.getString("charset");
		}
		if (options.containsKey("autoStart")) {
			this.autoStart = options.getBoolean("autoStart");
			Log.d(LCAT, "autoStart=" + this.autoStart);
			if (this.autoStart)
				metaClient.startTimer();
		}
		if (options.containsKey(TiC.PROPERTY_ONLOAD)) {
			Object cb = options.get(TiC.PROPERTY_ONLOAD);
			if (cb instanceof KrollFunction) {
				loadCallback = (KrollFunction) cb;
			} else {
				Log.e(LCAT, "onload is not KrollFunction");
			}
			if (autoStart == true) {
				try {
					metaClient.refreshMeta();
				} catch (IOException e) {
					e.printStackTrace();
				}
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
	public void start() {
		metaClient.startTimer();
	}

	@Kroll.method
	public void stop() {
		metaClient.stopTimer();
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
		private boolean isRunning;
		private Timer timer;
		private int oldHash = 0;

		public IcyStreamMeta() {
			// setStreamUrl(streamUrl);
			isError = false;
			timer = new Timer();
		}

		public void startTimer() {
			retreiveMetadata();
			if (interval != 0) {
				timer.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						retreiveMetadata();
					}
				}, 0, interval * 1000);
				isRunning = true;
			}
		}

		public void stopTimer() {
			timer.cancel();
			isRunning = false;
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

		private String Stream2String(InputStream stream, int metaDataOffset) {
			// http://www.smackfu.com/stuff/programming/shoutcast.html
			final int BLOCKSIZE = 16;
			final int EOSTREAM = -1;
			/*
			 * StringWriter writer = new StringWriter(); try {
			 * IOUtils.copy(stream, writer, "UTF-8"); } catch (IOException e) {
			 * e.printStackTrace(); }
			 */

			int b; // 0...255
			int count = 0;
			int metaDataLength = BLOCKSIZE * 255; // 4080 is the max length (16
			ByteBuffer bb = ByteBuffer.allocate(metaDataLength);
			for (int i = 0; i < metaDataLength; i++) {
				bb.put(i, (byte) 0x0);
			}
			boolean inData = false;
			try {
				while ((b = stream.read()) != EOSTREAM) {
					count++;
					if (count == metaDataOffset + 1) {
						metaDataLength = b * BLOCKSIZE;
					}
					if (count > metaDataOffset + 1
							&& count < (metaDataOffset + metaDataLength)) {
						inData = true;
					} else {
						inData = false;
					}
					if (inData) {
						bb.put((byte) b);
					}
					if (count > (metaDataOffset + metaDataLength)) {
						break;
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String result = new String(bb.array(), Charset.forName(charset));
			int stringLength = result.lastIndexOf(";");
			bb.clear();
			if (stringLength != -1)
				return result.substring(0, stringLength);
			else
				return null;
		}

		private void retreiveMetadata() {
			AsyncTask<Void, Void, Void> doRequest = new AsyncTask<Void, Void, Void>() {
				protected Void doInBackground(Void[] dummy) {
					// http://www.javased.com/?api=java.net.URLConnection
					URLConnection con = null;
					try {
						con = streamUrl.openConnection();
					} catch (IOException e) {
						sendError(e.getMessage());
						return null;
					}
					con.setRequestProperty("Icy-MetaData", "1");
					con.setRequestProperty("Connection", "close");
					try {
						con.connect();
					} catch (IOException e) {
						sendError(e.getMessage());
						return null;
					}
					int metaDataOffset = 0;
					Map<String, List<String>> headers = con.getHeaderFields();
					InputStream stream = null;
					try {
						stream = con.getInputStream();
					} catch (IOException e) {
						sendError(e.getMessage());
					}
					if (headers.containsKey("icy-metaint")) {
						metaDataOffset = Integer.parseInt(headers.get(
								"icy-metaint").get(0));
					}
					if (metaDataOffset == 0) {
						isError = true;

					}
					String metaString = Stream2String(stream, metaDataOffset);
					if (metaString == null)
						return null;
					String[] metaParts = metaString.split(";");
					try {
						stream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					KrollDict resultDict = new KrollDict();
					for (int i = 0; i < metaParts.length; i++) {
						String line = metaParts[i];
						String[] keyval = line.split("=");
						if (keyval != null) {
							String key = keyval[0];
							String val = keyval[1];
							String sanitizedValue = "";
							try {
								sanitizedValue = val.substring(1,
										val.length() - 1).replaceAll("\r", "");
								resultDict.put(key, sanitizedValue);
							} catch (PatternSyntaxException ex) {
								Log.d(LCAT,
										"PatternSyntaxException"
												+ ex.getDescription());
							} catch (IllegalArgumentException ex) {
							} catch (IndexOutOfBoundsException ex) {
							}
						}
					}
					if (resultDict.hashCode() != oldHash) {
						if (loadCallback != null)
							loadCallback.call(getKrollObject(), resultDict);
						else
							Log.e(LCAT, "loadCallback is null");
						oldHash = resultDict.hashCode();
					}
					return null;
				} // do in background
			};// async task
			doRequest.execute();
		} // retreiveMetadata
	}// private class

} // main class