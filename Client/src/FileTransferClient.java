import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class FileTransferClient extends Socket {
	private static final String ServerIP = "localhost";//"129.204.152.91";//这是本人（3170）的腾讯云
	private static final int ServerPort = 9527;
	private Socket ClientSocket;
	private FileInputStream InputStream;
	private DataOutputStream OutputStream;

	public FileTransferClient() throws Exception {
		super(ServerIP, ServerPort);
		this.ClientSocket = this;
		System.out.println("成功连接服务器，端口号：" + this.getLocalPort());
	}
	public void SendFile(String path) throws Exception{
		try {  
            File file = new File(path);  
            if(file.exists()) {
            	InputStream = new FileInputStream(file);  
                OutputStream = new DataOutputStream(ClientSocket.getOutputStream());  
                OutputStream.writeUTF(file.getName());  
                OutputStream.flush();
                System.out.println("开始传输文件");  
                byte[] bytes = new byte[1024];  
                int length = 0;  
                while((length = InputStream.read(bytes, 0, bytes.length)) != -1) {  
                    OutputStream.write(bytes, 0, length);  
                    OutputStream.flush();                     
                }  
                System.out.println();  
                System.out.println("文件传输成功");  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if(InputStream != null)  
                InputStream.close();  
            if(OutputStream != null)  
                OutputStream.close();  
            ClientSocket.close();  
        } 
	}
}
