package FileTransferClient;

import Tools.Command;
import Tools.Protocol;

import java.io.*;
import java.net.Socket;

public class FileTransferClient extends Socket {
	private Protocol protocol;

	public FileTransferClient(String ServerIP, int ServerPort, String path) throws Exception {
		super(ServerIP, ServerPort);
		protocol = new Protocol(path, this.getOutputStream());
	}

	public void SendFile() throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(this.getOutputStream()));
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
		String sendStr, recvStr;

		// �����ļ��б�
		sendStr = protocol.readCmd(Command.FILELISTREQ.getCmd());
//		System.out.println(sendStr);
		writer.write(sendStr);
		writer.flush();

		// ���ܷ�����������Ϣ �������
		recvStr = reader.readLine();
//		System.out.println(recvStr);
		sendStr = protocol.readCmd(recvStr);
//		System.out.println(recvStr);
		if (recvStr.equals(Command.SENDEND.getCmd()))
			System.out.println("û����Ҫ���µ��ļ���");
		writer.write(sendStr);
		writer.flush();
		protocol.SendFileStream();
		System.out.println("����ɹ���");
		try {
			writer.close();
		} catch (Exception e) {
		}
		try {
			reader.close();
		} catch (Exception e) {
		}
	}
}