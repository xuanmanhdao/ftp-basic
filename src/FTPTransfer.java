import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPTransfer {

  private static final String REMOTE_DIRECTORY = "/";
  private static final String LOCAL_PATH =
      "H:\\ProjectKiemAn\\JavaCore\\FTPClient\\Downloads\\";

  /**
   * Get list file FTP Server
   *
   * @param ftpClient
   * @return
   * @throws IOException
   */
  public List<FTPFile> getListFile(FTPClient ftpClient) throws IOException {
    List<FTPFile> files = List.of(ftpClient.listFiles());
    System.out.println("Files in FTP server:");
    for (FTPFile file : files) {
      System.out.println(file.getName());
    }
    return files;
  }

  /**
   * Download file multiple thread
   *
   * @param ftpClient
   * @param remoteFiles
   */
  public void downloadFiles(FTPClient ftpClient, List<String> remoteFiles) {
    // Tạo định dạng thời gian
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String timestamp = dateFormat.format(new Date());

    int numThreads = Math.min(remoteFiles.size(), Runtime.getRuntime().availableProcessors());
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

    List<CompletableFuture<String>> futures = remoteFiles.stream()
        .map(remoteFile -> {
          String localFilePath = LOCAL_PATH + timestamp + "_" + remoteFile;
          String remoteFilePath = REMOTE_DIRECTORY + remoteFile;

          return CompletableFuture.supplyAsync(() -> {
            try (OutputStream outputStream = new BufferedOutputStream(
                new FileOutputStream(localFilePath))) {
              ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
              boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
              if (success || ftpClient.getReplyCode() == 150 || ftpClient.getReplyCode() == 226) {
                return "File downloaded successfully: " + localFilePath;
              } else {
                return "Failed to download file: " + remoteFilePath + "-"
                    + ftpClient.getReplyCode();
              }
            } catch (IOException e) {
              throw new RuntimeException("Error occurred during file download", e);
            }
          }, executorService);
        })
        .toList();

    // Đợi cho tất cả các tác vụ tải xuống hoàn thành và in kết quả
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
        .thenAccept(v -> futures.forEach(future -> {
          try {
            String result = future.get();
            System.out.println(result);
          } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
          }
        }))
        .join();
  }

  /**
   * Upload file to FTP Server
   *
   * @param ftpClient
   * @param localFilePath
   * @throws IOException
   */
  public void uploadFile(FTPClient ftpClient, String localFilePath) throws IOException {
    File uploadFile = new File(localFilePath);
    // Tạo định dạng thời gian
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String timestamp = dateFormat.format(new Date());

    try (InputStream inputStream = new BufferedInputStream(new FileInputStream(uploadFile))) {
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
      boolean success = ftpClient.storeFile(
          REMOTE_DIRECTORY + "/" + timestamp + "_" + uploadFile.getName(),
          inputStream);
      if (success) {
        System.out.println("File uploaded successfully: " + localFilePath);
      } else {
        System.out.println(
            "Failed to upload file: " + localFilePath + " - " + ftpClient.getReplyCode());
      }
    }
  }
}