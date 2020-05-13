package Tools;

public enum Command {
	LINKREQ("000"), FILELISTREQ("011"), SENDFILELIST("002"), FILEREQ("013"), FILESEND("004"), SENDEND("015"); // 第二位 1
																												// 表示
																												// 服务器端

	String cmd;

	Command(String cmd) {
		this.cmd = cmd;
	}

	public String getCmd() {
		return cmd;
	}
}