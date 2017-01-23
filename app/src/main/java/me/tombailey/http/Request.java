package me.tombailey.http;

import com.msopentech.thali.toronionproxy.OnionProxyManager;
import com.msopentech.thali.toronionproxy.Utilities;

import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tom on 20/01/2017.
 */
public class Request {

    private static final Map<String, Socket> HOST_SOCKETS_MAP = new HashMap<String, Socket>(16);


    private OnionProxyManager mOnionProxyManager;

    private String mUrl;
    private String mMethod;
    private List<Header> mHeaders;

    private FormBody mFormBody;

    private Request(OnionProxyManager onionProxyManager, String url, String method, List<Header> headers, FormBody formBody) {
        mOnionProxyManager = onionProxyManager;

        mUrl = url;
        mMethod = method;
        mHeaders = headers;
        mFormBody = formBody;
    }

    public Response execute() throws IOException {
        //TODO: check for existing keep-alive connection for this host
        Socket socket = createSocket();
        writeRequest(socket);
        Response response = getResponse(socket);

        //TODO: check if response headers contain keep-alive
        if (!socket.isClosed()) {
            socket.close();
        }

        return response;
    }

    private Socket createSocket() throws IOException {
        URL url = new URL(mUrl);
        String host = url.getHost();

        Socket s = HOST_SOCKETS_MAP.get(host);
        if (s != null && !s.isClosed()) {
            return s;
        } else {
            int port = url.getPort();

            Socket socket = Utilities.socks4aSocketConnection(host, port, "127.0.0.1", mOnionProxyManager.getIPv4LocalHostSocksPort());
            if (port == 443 && mUrl.toLowerCase().startsWith("https://")) {
                socket = ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(socket, host, port, true);
            }

            return socket;
        }
    }

    private void writeRequest(Socket socket) throws IOException {
        URL url = new URL(mUrl);
        String host = url.getHost();
        String file = url.getFile();

        PrintWriter pw = new PrintWriter(socket.getOutputStream());
        pw.println(mMethod + " " + (file.length() > 0 ? file : "/") + " HTTP/1.1");

        pw.println("Host: " + host);
        for (Header header : mHeaders) {
            pw.println(header.getName() + ": " + header.getValue());
        }

        pw.println("");
        pw.flush();

        if (mFormBody != null) {
            //TODO: send form body

            pw.println("");
            pw.flush();
        }
    }

    private Response getResponse(Socket socket) throws IOException {
        return Response.fromInputStream(socket.getInputStream());
    }

    public static class Builder {

        private OnionProxyManager mOnionProxyManager;

        private String mUrl;
        private Method mMethod;
        private List<Header> mHeaders;

        private FormBody mFormBody;

        public Builder() {

        }

        public Builder onionProxyManager(OnionProxyManager onionProxyManager) {
            mOnionProxyManager = onionProxyManager;
            return this;
        }

        public Builder url(String url) {
            mUrl = url;
            return this;
        }

        public Builder get() {
            mMethod = Method.GET;
            return this;
        }

        public Builder post() {
            return post(null);
        }

        public Builder post(FormBody formBody) {
            mMethod = Method.POST;
            mFormBody = formBody;
            return this;
        }

        public Builder delete() {
            return delete(null);
        }

        public Builder delete(FormBody formBody) {
            mMethod = Method.DELETE;
            mFormBody = formBody;
            return this;
        }

        public Builder put() {
            return put(null);
        }

        public Builder put(FormBody formBody) {
            mMethod = Method.PUT;
            mFormBody = formBody;
            return this;
        }

        public Builder header(String name, String value) {
            if (mHeaders == null) {
                mHeaders = new ArrayList<Header>(4);
            }
            mHeaders.add(new Header(name, value));
            return this;
        }

        public Request build() {
            if (mOnionProxyManager == null || mUrl == null || mMethod == null) {
                //TODO throw an appropriate exception
            }
            return new Request(mOnionProxyManager, mUrl, mMethod.getValue(), mHeaders, mFormBody);
        }

    }

}
