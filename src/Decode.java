import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Decode {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args){
		if(args.length<1)
		{
			System.exit(-1);
			System.out.println("argument must greater than 1!");
		}
		try {
			getFileInfo(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-2);
		}
		
//		try {
//			getFileInfo("newOfflineDict1/");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		getUnzipMD5("zip/");
	}
	
	public static void getFileInfo(String dir) throws Exception
	{
		File dirFile = new File(dir);
//		File[] fileList = dirFile.listFiles();
//		for(int i=0,len=fileList.length; i<len; i++)
//		{
//			getInfo(fileList[i]);
//		}
		
		String[] fileNames = dirFile.list();
		BufferedWriter out = null;
		out = new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(dirFile.getAbsolutePath()+File.separator+"dictsInfo.txt", true)));
		out.write("\r\n==================Generate Info===============\r\n");
		out.close();
		for(int i=0,len=fileNames.length; i<len; i++)
		{
			if(fileNames[i].endsWith(".dat"))
				getInfo(new File(dirFile.getAbsolutePath()+File.separator+fileNames[i]));
		}
	}
	
	public static void getUnzipMD5(String zipDir) throws IOException
	{
		File dirFile = new File(zipDir);
		File[] fileList = dirFile.listFiles();
		for(int i=0,len=fileList.length; i<len; i++)
		{
			Utils.unZip(zipDir, fileList[i].getPath());
		}
	}
	
	/**
	 * 获取.dat内的词典名字、单词个数、词典type、时间、MD5,
	 * 并将.dat压缩为.zip文件，并拼接成下载链接,获取.zip文件大小
	 * 生成的信息追加在当前目录dictsInfo.txt文件中
	 * */
	public static void getInfo(File file) throws Exception
	{
		byte[] arrayOfByte = new byte[256];
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		String datName = Utils.readString(dis, arrayOfByte);
		int type = dis.readByte();
		int wordCount = dis.readInt();
		fis.close();
		
		CompressFile cf = new CompressFile(file.getAbsolutePath().replace(".dat", "")+".zip");
		String zipFilePath = cf.compress(file.getAbsolutePath());
		File zipFile = new File(zipFilePath);
		long fileLength = zipFile.length();
		String zipFileName = zipFile.getName();
		String downloadUrl = "http://f1.m.hjfile.cn/resource/hjdict/dict/"+zipFileName;
		
		StringBuilder sb = new StringBuilder();
		sb.append(datName);
		sb.append("\t");
		sb.append(wordCount);
		sb.append("\t");
		sb.append(type);
		sb.append("\t");
		sb.append(downloadUrl);
		sb.append("\t");
		sb.append(fileLength);
		sb.append("\t");
		sb.append(Utils.tobeMD5(file.getAbsolutePath()));
		sb.append("\t");
		sb.append(Utils.getTime());
		sb.append("\r\n");
		
		String result = sb.toString();
		String parent = file.getParent();
		System.out.println(parent);
		BufferedWriter out = null;
		out = new BufferedWriter(
			new OutputStreamWriter(new FileOutputStream(parent+File.separator+"dictsInfo.txt", true)));
		out.write(result);
		out.close();
	}
	
	public static String[] ArrayOfFile = {"frcn.dat", "cnfr.dat", "krcn.dat", "cnkr.dat", "encn.dat", "jpcn.dat"};
	public static String[] ArrayOfYDFile = {"21dict.dat", "phrase.dat", "syno.dat", "wordform.dat"};
	
}
