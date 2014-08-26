package com.renren.kafka.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

public class HttpUtils {

    /**
     * @param reqUrl post请求的地址
     * @param data post的数据
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    public static String sendPostRequest(String reqUrl, String data)
        throws UnsupportedEncodingException, IOException {
        URL url;
        url = new URL(reqUrl);
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true);// 开启输入输出
        connection.setDoInput(true);
        OutputStreamWriter out =
                new OutputStreamWriter(connection.getOutputStream(), "utf-8");
        out.write(data); // 向页面传递数据。post的关键所在！
        out.flush();
        out.close();

        // 一旦发送成功，用以下方法就可以得到服务器的回应：
        String sCurrentLine = "";
        String sTotalString = "";
        sCurrentLine = "";
        sTotalString = "";
        InputStream l_urlStream = connection.getInputStream();
        BufferedReader l_reader =
                new BufferedReader(new InputStreamReader(l_urlStream));
        while ((sCurrentLine = l_reader.readLine()) != null) {
            sTotalString += sCurrentLine + "/r/n";
        }
        return sTotalString;
    }

    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlName = url + "?" + param;
            URL realUrl = new URL(urlName);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 建立实际的连接
            conn.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = conn.getHeaderFields();
            // 遍历所有的响应头字段
            /*
             * for (String key : map.keySet()) { System.out.println(key + "--->"
             * + map.get(key)); }
             */
            // 定义BufferedReader输入流来读取URL的响应
            in =
                    new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "/n" + line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 向指定URL发送POST方法的请求
     * 
     * @param url 发送请求的URL
     * @param param 请求参数，请求参数应该是name1=value1&name2=value2的形式。
     * @return URL所代表远程资源的响应
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in =
                    new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += "/n" + line;
            }
        } catch (Exception e) {
            System.out.println("发送POST请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException,
        IOException {
        String reqUrl = "http://sms.notify.d.xiaonei.com:2000/receiver";
        String param = "number=13146751471&message=2 42 62222 4444";
        System.out.println(System.currentTimeMillis());
        String result = sendGet(reqUrl, param);
        System.out.println(result);
    }

}
