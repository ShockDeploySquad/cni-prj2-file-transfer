package FileTransferClient;

import java.io.File;

public class ClientCMD {
	public static void main(String[] args) {
		// 以下四句在eclipse中使用 方便调试 打包时去掉
		args = new String[3];
		args[1] = "localhost";
		args[2] = "9527";
		args[0] = "Z:\\计网\\课程项目二\\CNI_PRJ2_CMDVER\\ClientFile\\";

		if (args.length != 3) {
			System.out.println("调用方式应为:\n"//
					+ "args[0]:文件夹路径\n"//
					+ "args[1]:服务器地址\n"//
					+ "args[2]:服务器端口");
			return;
		}
		String FilePath = args[0];
		String ServerIP = args[1];
		// 判断文件夹是否存在
		File file = new File(args[0]);
		if (file.exists()) {
			if (!file.isDirectory()) {
				System.out.println("存在同名文件，无法创建文件夹");
				return;
			}
		} else {
			System.out.println("不存在该文件夹，已帮您自动创建");
			file.mkdirs();
			return;
		}
		// 判断端口号是否正确
		int ServerPort;
		try {
			ServerPort = Integer.parseInt(args[2]);
			if (ServerPort < 0 || ServerPort > 65535) {
				System.out.println("端口号只能为0~65535之间的整数！");
				return;
			}
		} catch (NumberFormatException e) {
			System.out.println("端口号只能为0~65535之间的整数！");
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
