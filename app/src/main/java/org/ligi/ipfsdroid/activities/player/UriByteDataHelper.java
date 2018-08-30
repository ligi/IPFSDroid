package org.ligi.ipfsdroid.activities.player;

import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Created by WillowTree on 8/29/18.
 */
public class UriByteDataHelper {

    public Uri getUri(byte[] data) {

        try {
            URL url = new URL(null, "bytes:///" + "audio", new BytesHandler(data));
            return Uri.parse( url.toURI().toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    class BytesHandler extends URLStreamHandler {
        byte[] mData;
        public BytesHandler(byte[] data) {
            mData = data;
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            return new ByteUrlConnection(u, mData);
        }
    }

    class ByteUrlConnection extends URLConnection {
        byte[] mData;
        public ByteUrlConnection(URL url, byte[] data) {
            super(url);
            mData = data;
        }

        @Override
        public void connect() throws IOException {
        }

        @Override
        public InputStream getInputStream() throws IOException {

            return new ByteArrayInputStream(mData);
        }
    }

}
