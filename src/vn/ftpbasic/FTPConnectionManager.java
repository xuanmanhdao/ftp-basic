package vn.ftpbasic;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

public class FTPConnectionManager {

  private FTPClient ftpClient;

  public FTPConnectionManager() {
    ftpClient = new FTPClient();
  }

  public FTPClient getFtpClient() {
    return ftpClient;
  }

  /**
   * Connect to FTP server
   *
   * @param serverAddress FTP server address
   * @param portNumber    FTP server port number
   * @param username      FTP username
   * @param password      FTP password
   * @param timeout       Connection timeout in milliseconds
   * @throws IOException if an I/O error occurs during connection
   */
  public void connectFTPServer(String serverAddress, int portNumber, String username,
      String password, int timeout) throws IOException {
    System.out.println("Connecting to FTP server ....");
    ftpClient.setDefaultTimeout(timeout);
    ftpClient.connect(serverAddress, portNumber);
    if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
      disconnectFTPServer();
      throw new IOException("FTP server not responding!");
    } else {
      ftpClient.setSoTimeout(timeout);
      if (!ftpClient.login(username, password)) {
        throw new IOException("Incorrect username or password!");
      }
      System.out.println("Connected to FTP server");
    }
  }

  /**
   * Disconnect from FTP server
   *
   * @throws IOException if an I/O error occurs during disconnection
   */
  public void disconnectFTPServer() throws IOException {
    if (ftpClient.isConnected()) {
      ftpClient.logout();
      ftpClient.disconnect();
      System.out.println("Disconnected from FTP server");
    }
  }
}