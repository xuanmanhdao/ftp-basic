package vn.ftpbasic;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;
    private static FTPConnectionManager ftpConnectionManager;
    private static FTPTransfer ftpTransfer;
    private static boolean isConnected;
    private static FTPClient ftpClient;

    public static void main(String[] args) throws IOException {
        System.out.println("FTP Application");

        scanner = new Scanner(System.in);
        ftpConnectionManager = new FTPConnectionManager();
        ftpTransfer = new FTPTransfer();
        isConnected = false;
        ftpClient = null;

        while (true) {
            displayMenu();

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Đọc dòng mới sau khi đọc số

            switch (choice) {
                case 1:
                    connectToFTPServer();
                    break;
                case 2:
                    getListOfFiles();
                    break;
                case 3:
                    downloadFilesFromFTPServer();
                    break;
                case 4:
                    uploadFileToFTPServer();
                    break;
                case 0:
                    exitProgram();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("========== Menu ==========");
        System.out.println("1. Connect to FTP server");
        System.out.println("2. Get list of files on FTP server");
        System.out.println("3. Download files from FTP server");
        System.out.println("4. Upload file to FTP server");
        System.out.println("0. Exit");
    }

    private static void connectToFTPServer() {
        if (!isConnected) {
            System.out.print("Enter FTP server address: ");
            String serverAddress = scanner.nextLine();

            System.out.print("Enter FTP server port: ");
            int serverPort = scanner.nextInt();
            scanner.nextLine(); // Đọc dòng mới sau khi đọc số

            System.out.print("Enter FTP server username: ");
            String username = scanner.nextLine();

            System.out.print("Enter FTP server password: ");
            String password = scanner.nextLine();

            System.out.print("Enter connection timeout (in milliseconds): ");
            int timeout = scanner.nextInt();
            scanner.nextLine(); // Đọc dòng mới sau khi đọc số

            try {
                ftpConnectionManager.connectFTPServer(serverAddress, serverPort, username, password, timeout);
                ftpClient = ftpConnectionManager.getFtpClient();
                isConnected = true;
                System.out.println("Connected to FTP server successfully.");
            } catch (IOException e) {
                System.out.println("Failed to connect to FTP server: " + e.getMessage());
            }
        } else {
            System.out.println("Already connected to FTP server.");
        }
    }

    private static void getListOfFiles() throws IOException {
        if (!isConnected) {
            System.out.println("Please connect to FTP server first.");
        } else {
            ftpTransfer.getListFile(ftpClient);
        }
    }

    private static void downloadFilesFromFTPServer() {
        if (!isConnected) {
            System.out.println("Please connect to FTP server first.");
        } else {
            System.out.print("Enter the number of files to download: ");
            int numFiles = scanner.nextInt();
            scanner.nextLine(); // Đọc dòng mới sau khi đọc số

            List<String> listFileNameNeedDownload = new ArrayList<>();
            for (int i = 0; i < numFiles; i++) {
                System.out.print("Enter the file name to download: ");
                String fileName = scanner.nextLine();
                listFileNameNeedDownload.add(fileName);
            }

            ftpTransfer.downloadFiles(ftpClient, listFileNameNeedDownload);
        }
    }

    private static void uploadFileToFTPServer() throws IOException {
        if (!isConnected) {
            System.out.println("Please connect to FTP server first.");
        } else {
            System.out.print("Enter the local file path to upload: ");
            String localFilePath = scanner.nextLine();
            ftpTransfer.uploadFile(ftpClient, localFilePath);
        }
    }

    private static void exitProgram() {
        System.out.println("Exiting...");
        scanner.close();
        if (isConnected) {
            try {
                // Disconnect from FTP server
                ftpConnectionManager.disconnectFTPServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}