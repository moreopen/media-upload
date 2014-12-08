package com.moreopen.media.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class MultiThreadDownloadTest {

     public static void main(String[] args) throws Exception {
    	 
//         String url = "http://127.0.0.1:8081/media/get.htm?mid=ZNQZ34Q2txUVbtIpNW3XQ1sLSC3krIjbNsX4wUJZ9YkQ1psdzb93dw==";
    	 String url = "http://127.0.0.1/media/static/2014/12/5/42R70h0y0h1417748502180.jpg";
         new MultiThreadDownloadTest().download(url, 3);
     }
 
     /**
      * 下载文件
      *
      * @param path网络文件路径
      * @throws Exception
      */
     private void download(String path, int threadsize) throws Exception {
         URL url = new URL(path);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setConnectTimeout(5000);
         conn.setReadTimeout(5000);
         conn.setRequestMethod("GET");
         if (conn.getResponseCode() == 200) {
             int length = conn.getContentLength(); // 获取网络文件的长度
             System.out.println("========================= total length : " + length);
             File file = new File(getFilename(path));
             RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");// 在本地生成一个长度相等的文件
             accessFile.setLength(length);
             accessFile.close();
 
             // 计算每条线程负责下载的数据量
             int block = length % threadsize == 0 ? length / threadsize : length / threadsize + 1;
             for (int threadid = 0; threadid < threadsize; threadid++) {
                 new DownloadThread(threadid, block, url, file).start();
             }
         } else {
             System.out.println("下载失败！");
         }
 
     }
 
     private class DownloadThread extends Thread {
 
         private int threadid;
         private int block;
         private URL url;
         private File file;
 
         public DownloadThread(int threadid, int block, URL url, File file) {
             this.threadid = threadid;
             this.block = block;
             this.url = url;
             this.file = file;
         }
 
         @Override
         public void run() {
             int start = threadid * block; // 计算该线程从网络文件的什么位置开始下载
             int end = (threadid + 1) * block - 1; // 下载到网络文件的什么位置结束
 
             try {
                 RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");// 在本地生成一个长度相等的文件
                 accessFile.seek(start);
                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                 conn.setConnectTimeout(5000);
                 conn.setReadTimeout(5000);
                 conn.setRequestMethod("GET");
                 conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
                 System.out.println("============response code : " + conn.getResponseCode());
                 if (conn.getResponseCode() == 206) {
                     InputStream inStream = conn.getInputStream();
                     byte[] buffer = new byte[1024];
                     int len = 0;
                     while ((len = inStream.read(buffer)) != -1) {
                         accessFile.write(buffer, 0, len);
                     }
                     accessFile.close();
                     inStream.close();
                     System.out.println("write bytes : " + conn.getContentLength());
                 }
                 System.out.println("第" + (threadid + 1) + "条线程已经下载完成！");
             } catch (IOException e) {
                 e.printStackTrace();
             }
 
         }
 
     }
 
     private String getFilename(String path) {
         return path.substring(path.lastIndexOf("/") + 1);
     }
 }