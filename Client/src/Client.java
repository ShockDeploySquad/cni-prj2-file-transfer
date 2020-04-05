import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
    	try {  
            FileTransferClient client = new FileTransferClient();
            client.SendFile("1.txt");
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
}