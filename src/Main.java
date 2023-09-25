import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTPClient;

public class Main {

  public static void main(String[] args) {
    System.out.println("Hello world!");

    String serverAddress = "192.168.1.23";
    int serverPort = 21;
    String username = "ftp-user";
    String password = "1qazZAQ!";
    int timeout = 60000;

    FTPConnectionManager ftpConnectionManager = new FTPConnectionManager();
    try {
      // Connect to FTP server
      ftpConnectionManager.connectFTPServer(serverAddress, serverPort, username, password, timeout);
      FTPClient ftpClient = ftpConnectionManager.getFtpClient();

      FTPTransfer ftpTransfer = new FTPTransfer();

      // Get list file FTP Server
      ftpTransfer.getListFile(ftpClient);

      // Download file multiple thread
      List<String> listFileNameNeedDownload = new ArrayList<>();
      listFileNameNeedDownload.add("anh-test-web-manhdx.jpg");
      listFileNameNeedDownload.add("retro-2.dfb6ef929b11684e5827.png");
      ftpTransfer.downloadFiles(ftpClient, listFileNameNeedDownload);

      // Upload file to FTP Server
      String localFilePath = "F:\\Pictures\\AnhSuuTam\\stringio.jpg";
      ftpTransfer.uploadFile(ftpClient, localFilePath);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        // Disconnect from FTP server
        ftpConnectionManager.disconnectFTPServer();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}