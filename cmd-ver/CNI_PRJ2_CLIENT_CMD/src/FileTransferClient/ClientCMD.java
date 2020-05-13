package FileTransferClient;

import java.io.File;

public class ClientCMD {
	public static void main(String[] args) {
		// �����ľ���eclipse��ʹ�� ������� ���ʱȥ��
		args = new String[3];
		args[1] = "localhost";
		args[2] = "9527";
		args[0] = "Z:\\����\\�γ���Ŀ��\\CNI_PRJ2_CMDVER\\ClientFile\\";

		if (args.length != 3) {
			System.out.println("���÷�ʽӦΪ:\n"//
					+ "args[0]:�ļ���·��\n"//
					+ "args[1]:��������ַ\n"//
					+ "args[2]:�������˿�");
			return;
		}
		String FilePath = args[0];
		String ServerIP = args[1];
		// �ж��ļ����Ƿ����
		File file = new File(args[0]);
		if (file.exists()) {
			if (!file.isDirectory()) {
				System.out.println("����ͬ���ļ����޷������ļ���");
				return;
			}
		} else {
			System.out.println("�����ڸ��ļ��У��Ѱ����Զ�����");
			file.mkdirs();
			return;
		}
		// �ж϶˿ں��Ƿ���ȷ
		int ServerPort;
		try {
			ServerPort = Integer.parseInt(args[2]);
			if (ServerPort < 0 || ServerPort > 65535) {
				System.out.println("�˿ں�ֻ��Ϊ0~65535֮���������");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("�˿ں�ֻ��Ϊ0~65535֮���������");
			return;
		}

		try {
			FileTransferClient client = new FileTransferClient(ServerIP, ServerPort, FilePath);
			if (client != null)
				client.SendFile();
			client.close();
		} catch (Exception e) {
		}
	}
}
