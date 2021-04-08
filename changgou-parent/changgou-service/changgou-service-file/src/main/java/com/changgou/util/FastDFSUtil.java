package com.changgou.util;

import com.changgou.file.FastDFSFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chx
 * @version 1.0
 * @description: TODO
 * @date 2020/10/7 0007 17:45
 */
public class FastDFSUtil {
    static {
        try {
            //查找classpath下的文件路径
            String filename=new ClassPathResource("fdfs_client.conf").getPath();
            //加载Tracker链接信息
            ClientGlobal.init(filename);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param fastDFSFile
     * @return
     */
    public static String[] upload(FastDFSFile fastDFSFile) throws Exception{
        //附加参数
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author",fastDFSFile.getAuthor());

        TrackerServer trackerServer = getTrackerServer();

        //通过TrackerServer的链接信息可以获取Storage的链接信息,创建StorageClient对象存储Storage的链接信息
        StorageClient storageClient = getStorageClient(trackerServer);

        //通过StorageClient访问Storage,实现文件上传，并且获取文件上传后的存储信息
        String[] uploads = storageClient.upload_file(fastDFSFile.getContent(),fastDFSFile.getExt(),meta_list);

        return uploads;
    }

    /**
     * 文件下载
     * @param groupName
     * @param remoteFileName
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) throws Exception{
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务获取连接对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //通过TrackerServer的链接信息可以获取Storage的链接信息,创建StorageClient对象存储Storage的链接信息
        StorageClient storageClient = new StorageClient(trackerServer, null);

        byte[] buffer = storageClient.download_file(groupName,remoteFileName);
        return new ByteArrayInputStream(buffer);
    }

    /**
     * 文件删除
     * @param groupName
     * @param remoteFileName
     */
    public static void deleteFile(String groupName, String remoteFileName) throws Exception{
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务获取连接对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //通过TrackerServer的链接信息可以获取Storage的链接信息,创建StorageClient对象存储Storage的链接信息
        StorageClient storageClient = new StorageClient(trackerServer, null);

        storageClient.delete_file(groupName, remoteFileName);
    }

    /**
     * 获取文件信息
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static FileInfo getFile(String groupName, String remoteFileName)throws Exception{
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务获取连接对象
        TrackerServer trackerServer = trackerClient.getConnection();

        //通过TrackerServer的链接信息可以获取Storage的链接信息,创建StorageClient对象存储Storage的链接信息
        StorageClient storageClient = new StorageClient(trackerServer, null);

        return storageClient.get_file_info(groupName,remoteFileName);
    }

    /**
     * 获取Storage信息
     * @return
     */
    public static StorageServer getStorages()throws Exception{
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务获取连接对象
        TrackerServer trackerServer = trackerClient.getConnection();

        return trackerClient.getStoreStorage(trackerServer);
    }

    /**
     * 获取storage的IP和端口
     * @param groupName
     * @param remoteFileName
     * @return
     * @throws Exception
     */
    public static ServerInfo[] getServerInfo(String groupName, String remoteFileName)throws Exception{
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务获取连接对象
        TrackerServer trackerServer = trackerClient.getConnection();

        return trackerClient.getFetchStorages(trackerServer,groupName,remoteFileName);
    }

    /**
     * 获取Tracker信息
     * @return
     * @throws Exception
     */
    public static String getTrackerInfo()throws Exception{
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务获取连接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        String ip = trackerServer.getInetSocketAddress().getHostString();
        int tracker_http_port = ClientGlobal.getG_tracker_http_port();
        String url = "http://"+ip+":"+tracker_http_port;
        return url;

    }

    /**
     * 获取TrackerServer
     * @return
     * @throws Exception
     */
    public static TrackerServer getTrackerServer()throws Exception{
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务获取连接对象
        TrackerServer trackerServer = trackerClient.getConnection();
        
        return trackerServer;
    }

    /**
     * 获取StorageClient
     * @return
     * @throws Exception
     */
    public static StorageClient getStorageClient(TrackerServer trackerServer)throws Exception{
       StorageClient storageClient = new StorageClient(trackerServer,null);
        return storageClient;
    }


    public static void main(String[] args) throws Exception{
//        FileInfo fileInfo = getFile("group1","M00/00/00/wKjThF9-15iAIxm4AARpoppe7fE201.jpg");
//        System.out.println(fileInfo.getSourceIpAddr());
//        System.out.println(fileInfo.getFileSize());

//        InputStream is = downloadFile("group1","M00/00/00/wKjThF9-15iAIxm4AARpoppe7fE201.jpg");
//        FileOutputStream os = new FileOutputStream("D:/1.jpg");
//
//        byte[] buffer = new byte[1024];
//        while (is.read(buffer)!=-1){
//            os.write(buffer);
//        }
//        os.flush();
//        os.close();
//        is.close();

        //文件删除
//        deleteFile("group1","M00/00/00/wKjThF9-15iAIxm4AARpoppe7fE201.jpg");

//        StorageServer storageServer = getStorages();
//        System.out.println(storageServer.getStorePathIndex());
//        System.out.println(storageServer.getInetSocketAddress().getHostString());

//        ServerInfo[] groups= getServerInfo("group1","M00/00/00/wKjThF9_D5GAanI1AARpoppe7fE870.jpg");
//        for (ServerInfo group : groups) {
//            System.out.println(group.getIpAddr());
//            System.out.println(group.getPort());
//        }

        System.out.println(getTrackerInfo());
    }

}