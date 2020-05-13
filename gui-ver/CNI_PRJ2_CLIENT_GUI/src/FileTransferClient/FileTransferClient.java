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
//		System.out.println("�ɹ����ӷ��������˿ںţ�" + this.getLocalPort());
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
				if (str.equals("�ϴ�")) {
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

		// �����ļ��б�
		FileTransferClient.setUpLoadBtnText("�Ա��ļ��б���");
		sendStr = protocol.readCmd(Command.FILELISTREQ.getCmd());
		writer.write(sendStr);
		writer.flush();

		// ���ܷ�����������Ϣ �������
		recvStr = reader.readLine();
		System.out.println(recvStr);
		sendStr = protocol.readCmd(recvStr);
		System.out.println(recvStr);
		if (recvStr.equals(Command.SENDEND.getCmd())) {
			System.out.println("û����Ҫ���µ��ļ���");
			FileTransferClient.setUpLoadBtnText("�ϴ�");
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Alert _alert = new Alert(Alert.AlertType.INFORMATION);
					_alert.setTitle("�ɹ�");
					_alert.setHeaderText("����ɹ�");
					_alert.setContentText("û����Ҫ���µ��ļ���");
					_alert.initOwner(ClientGUI.stage);
					_alert.show();
				}
			});
		}
		writer.write(sendStr);
		writer.flush();
		FileTransferClient.setUpLoadBtnText("�ϴ���");
		long StartTime = System.currentTimeMillis();
		protocol.SendFileStream();
		long EndTime = System.currentTimeMillis();
		System.out.println("����ɹ���");
		FileTransferClient.setUpLoadBtnText("�ϴ�");
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Alert _alert = new Alert(Alert.AlertType.INFORMATION);
				_alert.setTitle("�ɹ�");
				_alert.setHeaderText("����ɹ�");
				_alert.setContentText(
						"�����ٶȣ�" + (float)ClientGUI.filesize / 1024 / 1024 / (EndTime-StartTime) * 1000 + "M/s");
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
