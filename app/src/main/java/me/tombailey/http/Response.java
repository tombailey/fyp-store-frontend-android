package me.tombailey.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.tombailey.http.internal.Pair;

/**
 * Created by Tom on 20/01/2017.
 */
public class Response {

    public static int CARRIAGE_RETURN = 0x0D;
    public static int LINE_FEED = 0x0A;


    private int mStatusCode;
    private String mStatusText;

    private Header[] mHeaders;
    private byte[] mMessageBody;

    public Response(int statusCode, String statusText, Header[] headers, byte[] messageBody) {
        mStatusCode = statusCode;
        mStatusText = statusText;

        mHeaders = headers;
        mMessageBody = messageBody;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public String getStatusText() {
        return mStatusText;
    }

    public Header[] getHeaders() {
        return mHeaders;
    }

    public byte[] getMessageBody() {
        return mMessageBody;
    }


    public static Response fromInputStream(InputStream inputStream) throws IOException {
        byte[] response = getByteResponseFromInputStream(inputStream);

        Pair<Integer, String[]> metaData = getMetaData(response);
        int metaDataLength = metaData.first();
        String[] metaDataParts = metaData.second();


        String[] statusLineParts = metaData.second()[0].split(" ");
        int statusCode = Integer.parseInt(statusLineParts[1]);
        String statusText = statusLineParts[2];

        Header[] headers = getHeaders(metaDataParts);

        byte[] messageBody = Arrays.copyOfRange(response, metaDataLength, response.length);


        return new Response(statusCode, statusText, headers, messageBody);
    }

    private static byte[] getByteResponseFromInputStream(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[4096]; //4kb buffer

        //allocate 1KB buffer for response
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(10 * 1024);
        int bytesRead = bufferedInputStream.read(buffer);
        while (bytesRead != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
            bytesRead = bufferedInputStream.read(buffer);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static Pair<Integer, String[]> getMetaData(byte[] response) {
        int metaDataLength = 0;
        List<String> metaDataParts = new ArrayList<String>(16);
        ByteArrayOutputStream metaDataPartByteArray = new ByteArrayOutputStream(256);

        for (int index = 0; index + 3 < response.length; index++) {
            if (response[index] == CARRIAGE_RETURN && response[index + 1] == LINE_FEED) {
                if (response[index + 2] == CARRIAGE_RETURN && response[index + 3] == LINE_FEED) {
                    metaDataLength = index + 4;
                    //double CRLF indicates end of headers
                    break;
                } else {
                    try {
                        metaDataParts.add(metaDataPartByteArray.toString("ISO-8859-1"));
                    } catch (UnsupportedEncodingException uee) {
                        //TODO: is re-throwing as a runtime exception acceptable here?
                        throw new RuntimeException(uee);
                    }
                    metaDataPartByteArray = new ByteArrayOutputStream(256);
                }
            } else {
                metaDataPartByteArray.write(response, index, 1);
            }
        }

        return new Pair<Integer, String[]>(metaDataLength,
                metaDataParts.toArray(new String[metaDataParts.size()]));
    }

    private static Header[] getHeaders(String[] metaDataParts) {
        Header[] headers = new Header[metaDataParts.length - 1];
        for (int index = 1; index < metaDataParts.length; index++) {
            String[] nameValue = metaDataParts[index].split(": ");
            headers[index - 1] = new Header(nameValue[0], nameValue[1]);
        }
        return headers;
    }

}
