import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ParallelTCPServer {
    private static final int PORT = 12345;
    private static final int MAX_THREADS = 10;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            ) {
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    System.out.println("Received: " + inputLine);
                    String reversedString = reverseString(inputLine);
                    // Simulate some processing time
                    Thread.sleep(1000);
                    writer.println(reversedString);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        private String reverseString(String str) {
            return new StringBuilder(str).reverse().toString();
        }
    }
}
