import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipOutputStream;

import org.apache.tools.zip.ZipEntry;


public class CompressFile {
	/*
	    * inputFileName 输入一个文件夹
	    * zipFileName 输出一个压缩文件夹
	    */
//	public String zip(String inputFileName) throws Exception {
//		File file = new File(inputFileName);
////		String fileName = file.getAbsolutePath();
//		String fileName = file.getName();
//		
//		String zipFileName = fileName.replace(".dat", "")+".zip";
//		
//		zip(zipFileName, new File(inputFileName));
//		return zipFileName;
//	}
//
//	private void zip(String zipFileName, File inputFile) throws Exception {
//	    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
//	    zip(out, inputFile, "");
//	    out.close();
//	}
//
//    private void zip(ZipOutputStream out, File file, String base) throws Exception {
//        if(file.isDirectory())
//        {
//			File[] fileList = file.listFiles();
//			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base + "/"));
//			base = base.length() == 0 ? "" : base + "/";
//			for (int i=0,len=fileList.length; i<len; i++)
//			{
//				zip(out, fileList[i], base+fileList[i].getName());
//			}
//        }
//        else
//        {
//			out.putNextEntry(new org.apache.tools.zip.ZipEntry(base));
//			FileInputStream in = new FileInputStream(file);
//			int oneByte;
//			while ((oneByte = in.read())!=-1) {
//				out.write(oneByte);
//			}
//			in.close();
//		}
//    }
	
	private static final int BUFFER_SIZE = 8192;
	private File zipFile;
	public CompressFile(String pathName)
	{
		zipFile = new File(pathName);
	}
	
	public String compress(String srcPathName)
	{
		File file = new File(srcPathName);
		if(!file.exists())
			throw new RuntimeException(srcPathName+"file isn't exist!");
		try{
			FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
			CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
			ZipOutputStream out = new ZipOutputStream(cos);
			String basedir = "";
			compress(file, out, basedir);
			out.close();
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return zipFile.getAbsolutePath();
	}
	
	private void compress(File file, ZipOutputStream out, String basedir)
	{
		if(file.isDirectory())
			this.compressDirectory(file, out, basedir);
		else
			this.compressFile(file, out, basedir);
	}
	
	private void compressDirectory(File dir, ZipOutputStream out, String basedir)
	{
		if(!dir.exists())
			return;
		File[] files = dir.listFiles();
		for(int i=0,len=files.length; i<len; i++)
		{
			compress(files[i], out, basedir+dir.getName()+File.separator);
		}
	}
	
	private void compressFile(File file, ZipOutputStream out, String basedir)
	{
		if(!file.exists())
			return;
		try{
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			ZipEntry entry = new ZipEntry(basedir+file.getName());
			out.putNextEntry(entry);
			int readLen = 0;
			byte[] data = new byte[BUFFER_SIZE];
			while((readLen=bis.read(data, 0, BUFFER_SIZE))!=-1)
			{
				out.write(data, 0, readLen);
			}
			bis.close();
		} catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
