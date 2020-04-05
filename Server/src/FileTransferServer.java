import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferServer extends ServerSocket {

	private static final int ServerPort = 9527;

	public FileTransferServer() throws IOException {
		super(ServerPort);
	}

	public void RunServer() throws Exception{
		while (true) {  
            Socket socket = this.accept();  
            new Thread(new RECVTask(socket)).start();  
        } 
	}
}

class RECVTask implements Runnable {

	private Socket socket;
	private DataInputStream InputStream;
	private FileOutputStream OutputStream;

	public RECVTask(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			InputStream = new DataInputStream(socket.getInputStream());
			String fileName = InputStream.readUTF();
			File file = new File(fileName);
			OutputStream = new FileOutputStream(file);

			// 开始接收文件
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = InputStream.read(bytes, 0, bytes.length)) != -1) {
				OutputStream.write(bytes, 0, length);
				OutputStream.flush();
			}
			System.out.println("文件接收成功 文件名：" + fileName);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (OutputStream != null)
					OutputStream.close();
				if (InputStream != null)
					InputStream.close();
				socket.close();
			} catch (Exception e) {
			}
		}
	}
}