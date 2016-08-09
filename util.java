import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

/* this utility class is used to created a QR code bitmap */
public class Utils {
    private static final String QRCODE_API = "http://chart.apis.google.com/chart?cht=qr&chs=250x250&chl=%s";


    // ip4 address
    public static String getLocalIpAddress() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> mylist = Collections
                    .list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface ni : mylist) {

                ArrayList<InetAddress> ialist = Collections.list(ni
                        .getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ipv4 = address
                            .getHostAddress())) {
                        return ipv4;
                    }
                }
            }

        } catch (SocketException ex) {

        }
        return null;
    }


    public String getFTPIpAddress() {
        String strIP = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        strIP= inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("msg", ex.toString());
        }
        return strIP;
    }


    public static boolean isSdCardMounted() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static boolean isNetworkActive(ConnectivityManager connMgr) {

        if (HttpService.DEBUG) {
            return true;
        }

        NetworkInfo network = connMgr.getActiveNetworkInfo();
        if (network == null) {
            return false;
        }
        return network.isAvailable();
    }

    public static Bitmap Create2DCode(String str) {

        HttpGet httpRequest = new HttpGet(String.format(QRCODE_API, str));

        HttpClient httpclient = new DefaultHttpClient();
        Bitmap bitmap = null;
        try {

            HttpResponse httpResponse = httpclient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                HttpEntity httpEntity = httpResponse.getEntity();

                InputStream is = httpEntity.getContent();

                bitmap = BitmapFactory.decodeStream(is);
                is.close();

            }

        } catch (ClientProtocolException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return bitmap;

    }

    public static Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = 800f;
        float ww = 480f;

        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }


    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMacAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception e) {
        }
        return "";
    }
}



