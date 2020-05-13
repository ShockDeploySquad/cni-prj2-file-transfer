package FileTransferClient;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ClientGUI extends Application {
	public static Stage stage;

	public static int PKGSIZE = 1024;
	private TextField ServerIPInput;// ������IP
	private TextField ServerPortInput;// �������˿�
	private TextField FilePathInput;// �ļ�·��
	public static Button UploadBtn;// �ϴ���ť
	public static ProgressBar FileUploadProgressBar;// ������
	public Button SelectFilePathBtn;// ѡ���ļ�
	public static long filesize;

	public static void main(String[] args) throws IOException {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		try {
			Parent root = FXMLLoader.load(getClass().getResource("MainWin.fxml"));
			init(root);
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("�ͻ���");
			primaryStage.show();
			// �̶����
			primaryStage.setMaxHeight(primaryStage.getHeight());
			primaryStage.setMinHeight(primaryStage.getHeight());
			primaryStage.setMaxWidth(primaryStage.getWidth());
			primaryStage.setMinWidth(primaryStage.getWidth());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init(Parent root) {
		// ��ʼ���ؼ�
		ServerIPInput = (TextField) root.lookup("#ServerIPInput");
		ServerPortInput = (TextField) root.lookup("#ServerPortInput");
		FilePathInput = (TextField) root.lookup("#FilePathInput");
		UploadBtn = (Button) root.lookup("#UploadBtn");
		FileUploadProgressBar = (ProgressBar) root.lookup("#FileUploadProgressBar");
		SelectFilePathBtn = (Button) root.lookup("#SelectFilePathBtn");
		FilePathInput.setEditable(false);
		UploadBtn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				SendFileBtn();
			}
		});
		SelectFilePathBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setTitle("��ѡ�����ϴ����ļ���");
				File selectedFile = directoryChooser.showDialog(stage);
				FilePathInput.setText(selectedFile.getAbsolutePath());
			}
		});
	}

	private FileTransferClient ConnServer() {
		try {
			int ServerPort = -1;
			try {
				ServerPort = Integer.parseInt(this.ServerPortInput.getText());
			} catch (NumberFormatException e) {
				SummonAlertDialog("�������", "�˿ڱ���Ϊ0~65535���������");
				return null;
			}
			if (ServerPort < 0 || ServerPort > 65535) {
				SummonAlertDialog("�������", "�˿ڱ���Ϊ0~65535���������");
				return null;
			}
			try {
				return new FileTransferClient(ServerIPInput.getText(), ServerPort);
			} catch (ConnectException e) {
				SummonAlertDialog("���Ӵ���", "���ӷ�����ʧ�ܣ�");
				return null;
			} catch (UnknownHostException e) {
				SummonAlertDialog("���Ӵ���", "��ַ��ʽ����");
				return null;
			}
		} catch (Exception e) {
		}
		return null;
	}

	private void SendFileBtn() {
		File file = new File(FilePathInput.getText());
		if (!file.exists() || file.isFile()) {
			SummonAlertDialog("��ȡ����", "�ļ��в����ڣ�");
			return;
		}
		filesize = file.length();
		try {
			FileTransferClient client = ConnServer();
			if (client != null)
				client.SendFile(FilePathInput.getText()+File.separator);
		} catch (IOException e) {
		}
	}

	public static void SummonAlertDialog(String Header, String Msg) {
		Alert _alert = new Alert(Alert.AlertType.WARNING);
		_alert.setTitle("����");
		_alert.setHeaderText(Header);
		_alert.setContentText(Msg);
		_alert.initOwner(stage);
		_alert.show();
	}
}