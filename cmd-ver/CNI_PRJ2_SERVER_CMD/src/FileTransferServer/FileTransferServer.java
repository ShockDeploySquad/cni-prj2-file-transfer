package FileTransferServer;

import Tools.Protocol;
import java.io.*;
import java.net.*;

class FileTransferServer extends ServerSocket {
	private String filePath;

	public FileTransferServer(String ServerIP, int ServerPort, String FilePath) throws IOException {
		super(ServerPort);
		this.filePath = FilePath;
	}

	public void runServer() {
		Socket socket;
		while (true) {
			try {
				socket = this.accept();
				new RECVTask(socket, filePath).start();
			} catch (IOException e) {
			}
		}
	}
}

class RECVTask extends Thread {
	private Socket socket;
	private String filePath;

	public RECVTask(Socket socket, String filePath) {
		this.socket = socket;
		this.filePath = filePath;
	}

	public void run() {
		try {
			Protocol protocol = new Protocol(filePath);
			protocol.setIs(socket.getInputStream());
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			String sendStr, recvStr;

			// 返回需要的文件列表、处理后返回
			recvStr = reader.readLine();
//			System.out.println(recvStr);
			sendStr = protocol.readCmd(recvStr);

			writer.write(sendStr);
//			System.out.println(sendStr);
			writer.flush();
			// 接收文件
			recvStr = reader.readLine();
//			System.out.println(recvStr);
			protocol.readCmd(recvStr);
			protocol.RecvFileStream();

			reader.close();
			writer.close();
			socket.close();
		} catch (Exception e) {
		}
	}
}