package FileTransferClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import Tools.Command;
import Tools.Protocol;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

public class FileTransferClient extends Socket {
	@SuppressWarnings("unused")
	private static String ServerIP;

	@SuppressWarnings("unused")
	private static int ServerPort;

//	private Socket ClientSocket;
//	private FileInputStream InputStream;
//	private DataOutputStream OutputStream;

	@SuppressWarnings("static-access")
	public FileTransferClient(String ServerIP, int ServerPort) throws Exception {
		super(ServerIP, ServerPort);
		this.ServerIP = ServerIP;
		this.ServerPort = ServerPort;
//		this.ClientSocket = this;
//		System.out.println("成功连接服务器，端口号：" + this.getLocalPort());
	}

	public void SendFile(String path) throws IOException {
		ClientGUI.UploadBtn.setDisable(true);
		new Thread(new UploadFileTask(this, path)).start();
	}

	public static void setUpLoadBtnText(String str) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				ClientGUI.UploadBtn.setText(str);
				if (str.equals("上传")) {
					ClientGUI.UploadBtn.setDisable(false);
					ClientGUI.FileUploadProgressBar.setProgress(0);
				}
			}
		});
	}
}

class UploadFileTask extends Task<Void> {
	private Socket socket;
	private String path;

	UploadFileTask(Socket socket, String path) {
		this.socket = socket;
		this.path = path;
	}

	@Override
	protected Void call() throws Exception {
		Protocol protocol = new Protocol(path, socket.getOutputStream());
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String sendStr, recvStr;

		// 发送文件列表
		FileTransferClient.setUpLoadBtnText("对比文件列表中");
		sendStr = protocol.readCmd(Command.FILELISTREQ.getCmd());
		writer.write(sendStr);
		writer.flush();

		// 接受服务器返回信息 处理后发送
		recvStr = reader.readLine();
		System.out.println(recvStr);
		sendStr = protocol.readCmd(recvStr);
		System.out.println(recvStr);
		if (recvStr.equals(Command.SENDEND.getCmd())) {
			System.out.println("没有需要更新的文件！");
			FileTransferClient.setUpLoadBtnText("上传");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Alert _alert = new Alert(Alert.AlertType.INFORMATION);
					_alert.setTitle("成功");
					_alert.setHeaderText("传输成功");
					_alert.setContentText("没有需要更新的文件！");
					_alert.initOwner(ClientGUI.stage);
					_alert.show();
				}
			});
		}
		writer.write(sendStr);
		writer.flush();
		FileTransferClient.setUpLoadBtnText("上传中");
		long StartTime = System.currentTimeMillis();
		protocol.SendFileStream();
		long EndTime = System.currentTimeMillis();
		System.out.println("传输成功！");
		FileTransferClient.setUpLoadBtnText("上传");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert _alert = new Alert(Alert.AlertType.INFORMATION);
				_alert.setTitle("成功");
				_alert.setHeaderText("传输成功");
				_alert.setContentText(
						"传输速度：" + (float)ClientGUI.filesize / 1024 / 1024 / (EndTime-StartTime) * 1000 + "M/s");
				_alert.initOwner(ClientGUI.stage);
				_alert.show();
			}
		});
		try {
			writer.close();
		} catch (Exception e) {
		}
		try {
			reader.close();
		} catch (Exception e) {
		}
		return null;
	}
}
