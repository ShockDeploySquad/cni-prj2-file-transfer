package Tools;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;

import FileTransferClient.ClientGUI;

import java.security.MessageDigest;

public class Protocol {
	private OutputStream os;
	private InputStream is;
	private String rootPath;
	private ArrayList<File> childFiles;
	private String md5s[], paths[];

	public static ArrayList<File> readDir(File root) {
		ArrayList<File> childDir = new ArrayList<>();
		File tmpChildDir[] = root.listFiles();
		for (File f : tmpChildDir) {
			childDir.add(f);
			if (f.isDirectory()) {
				childDir.addAll(readDir(f));
			}
			ClientGUI.filesize += f.length();
		}
		return childDir;
	}

	public static ArrayList<File> deleteDir(ArrayList<File> childDir) {
		ArrayList<File> ChildFileDir = new ArrayList<File>();
		for (File f : childDir) {
			if (f.isFile()) {
				ChildFileDir.add(f);
			}
		}
		return ChildFileDir;
	}

	public Protocol(String rootPath, InputStream is) {
		this(rootPath);
		this.is = is;
	}

	public Protocol(String rootPath, OutputStream os) {
		this(rootPath);
		this.os = os;
	}

	public Protocol(String rootPath) {
		this.rootPath = rootPath; // 使用该构造函数将无法处理文件流
		childFiles = new ArrayList<File>();
	}

	public void setOs(OutputStream os) {
		this.os = os;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public void setIs(InputStream is) {
		this.is = is;
	}

	// 可能需要再加上每个文件的校验码
	public String readCmd(String stream) {
		String cmd = stream.substring(0, 3);
		// System.out.println(cmd);
		if (cmd.equals(Command.FILELISTREQ.getCmd())) {
			File root = new File(rootPath);
			childFiles = readDir(root);
			// childFiles=deleteDir(childFiles);
			if (childFiles.isEmpty()) {
				throw new NullPointerException("该文件夹下没有文件");
			} else {
				StringBuffer retString = new StringBuffer(Command.SENDFILELIST.getCmd());
				for (File chFile : childFiles) {
					String tmpPath = chFile.getAbsolutePath();
					tmpPath = tmpPath.substring(rootPath.length());
					if (chFile.isDirectory()) {
						retString.append(tmpPath + File.separator + File.pathSeparator);
						continue;
					}
					String md5 = MD5CalUtil.getMD5(chFile);
					retString.append(tmpPath + File.pathSeparator + md5 + File.pathSeparator);
				}
				return retString.toString() + "\n";
			}
		}
		if (cmd.equals(Command.SENDFILELIST.getCmd())) {
//			System.out.println(readDir(new File(rootPath)));
			// 可能需要比较文件
			String infos[];// tmp in order to run;
			String info = stream.substring(3);
			infos = info.split(File.pathSeparator);
			int cntDirPath = 0, cntFilePath = 0;

			md5s = new String[infos.length];
			paths = new String[infos.length];
			for (int i = 0; i < infos.length; i++) {
				if (!infos[i].endsWith(File.separator)) {
					paths[cntFilePath] = infos[i];
					md5s[cntFilePath] = infos[++i];
					cntFilePath++;
				}
			}
			for (int i = 0; i < infos.length; i++) {
				if (infos[i].endsWith(File.separator)) {
					paths[cntFilePath + cntDirPath] = infos[i];
					cntDirPath++;
				}
				// System.out.println(paths[i]);
			}
			// ++这里进行文件比较 留下需要更新的文件
			StringBuffer retString = new StringBuffer(Command.FILEREQ.getCmd());
			boolean isComplete = true;
			for (int i = 0; i < cntFilePath; i++) {
				String absolutePath = rootPath + paths[i];
				File checkFile = new File(absolutePath);
				if (!checkFile.exists()) {
					isComplete = false;
					retString.append(paths[i] + File.pathSeparator);
					childFiles.add(new File(absolutePath));
					continue;
				}
				String md5 = MD5CalUtil.getMD5(checkFile);
				if (!md5.equals(md5s[i])) {
					isComplete = false;
					retString.append(paths[i] + File.pathSeparator);
					childFiles.add(new File(absolutePath));
				}
			}
			for (int i = cntFilePath; i < cntDirPath + cntFilePath; i++) {
				String absolutePath = rootPath + paths[i];
				File CheckFile = new File(absolutePath);
				if (!CheckFile.exists()) {
					isComplete = false;
					retString.append(paths[i] + File.pathSeparator);
				}
			}
			if (!isComplete)
				return retString.toString() + "\n";
			else
				return Command.SENDEND.getCmd() + "\n";
			// 暂时不需要回应 发送完毕后 结束程序即可
			// 可能需要加入服务器端进行校验的信息 也可能需要中间进行校验
		}
		if (cmd.equals(Command.FILEREQ.getCmd())) {
			String path = stream.substring(3);
			String retString = Command.FILESEND.getCmd();
			String paths[] = path.split(File.pathSeparator);
			childFiles.clear();
			for (String p : paths) {
				File f = new File(rootPath + p);
				childFiles.add(f);
			}
			// 需要在retString后面加入文件内容
//			FileZipUtil.toZip(childFiles, os, rootPath);
			return retString + "\n";
		}
		if (cmd.equals(Command.FILESEND.getCmd())) {
			// 请读取到指令后外部处理接受流
			// 处理文件内容
//			FileZipUtil.unZip(rootPath, is);
			return Command.SENDEND.getCmd() + "\n";
		}
		return null; // 此处没有读到任何指令 传输一定出现了问题
	}

	public void SendFileStream() {
		FileZipUtil.toZip(childFiles, os, rootPath);
	}

	public void RecvFileStream() throws IOException {
		FileZipUtil.unZip(rootPath, is);
	}
}

class MD5CalUtil {
	public static String getMD5(File file) {
		FileInputStream finStream = null;
		try {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			finStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int length;
			while ((length = finStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}
			String md5Code = new BigInteger(1, MD5.digest()).toString(16);
			return md5Code;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		} finally {
			try {
				if (finStream != null)
					finStream.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}