package Tools;

public enum Command {
	LINKREQ("000"), FILELISTREQ("011"), SENDFILELIST("002"), FILEREQ("013"), FILESEND("004"), SENDEND("015"); // �ڶ�λ 1
																												// ��ʾ
																												// ��������

	String cmd;

	Command(String cmd) {
		this.cmd = cmd;
	}

	public String getCmd() {
		return cmd;
	}
}