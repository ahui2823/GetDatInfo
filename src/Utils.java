import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class Utils {
	public static String tobeMD5(String fileName) throws NoSuchAlgorithmException, IOException
	{
		byte[] buffer = new byte[8192];
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		FileInputStream fis = new FileInputStream(fileName);
		int readLen = 0;
		while((readLen=fis.read(buffer))!=-1)
			messageDigest.update(buffer, 0, readLen);
		fis.close();
		return toHex(messageDigest.digest());
	}
	
	public static String MD5(String str) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(str.getBytes());
			return toHex(messageDigest.digest()).substring(8, 24);
		} catch (Exception e) {
		}
		return "";
	}

	private static String toHex(byte[] buffer) {
		StringBuffer sb = new StringBuffer(buffer.length * 2);
		for (int i = 0; i < buffer.length; i++) {
			sb.append(Character.forDigit((buffer[i] & 0xf0) >> 4, 16));
			sb.append(Character.forDigit(buffer[i] & 0xf, 16));
		}
		return sb.toString();
	}

	public static String decode(String paramString) throws UnsupportedEncodingException {
		if (paramString==null || paramString.equals("")) {
			return "";
		}
		byte[] arrayOfByte = convert(Base64.decode(paramString));
		return new String(arrayOfByte, "utf-8");
	}
	
	public static byte[] convert(byte[] paramArrayOfByte) {

		if (paramArrayOfByte == null) {
			return null;
		}
		int len = paramArrayOfByte.length;
		byte[] arrayOfByte = new byte[len];
		for (int k = 0;; ++k) {
			if (k >= len) {
				return arrayOfByte;
			}
			arrayOfByte[k] = (byte) (paramArrayOfByte[k] ^ 0xFFFFFFFF);
		}
		// return arrayOfByte;
	}

	public static String longToFormatDate(long time)
	{
		Date date = new Date(time);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
	
	public static String readString(DataInput paramDataInput, 
    		byte[] paramArrayOfByte) throws IOException
    {
		int i = paramDataInput.readUnsignedByte();
		return readAscii(paramDataInput, i, paramArrayOfByte);
    }
	
	public static String readAscii(DataInput paramDataInput,
    		int paramInt, byte[] paramArrayOfByte) throws IOException
    {
		paramDataInput.readFully(paramArrayOfByte, 0, paramInt);
		return new String(paramArrayOfByte, 0, paramInt, "utf-8");
    }
	
	public static String getTime()
	{
		Date d = new Date();
		return Utils.longToFormatDate(d.getTime());
	}
	
	/** 
    * 解压缩功能. 
    * 将ZIP_FILENAME文件解压到ZIP_DIR目录下. 
    * @throws Exception 
    */
	public static void unZip(String dir, String zipFileName) throws IOException
	{
		ZipFile zfile = new ZipFile(zipFileName);
		Enumeration<?> zList = zfile.entries();
		ZipEntry ze = null;
		int bufferLen = 8192;
		byte[] buf = new byte[bufferLen];
		while(zList.hasMoreElements()){
			ze = (ZipEntry)zList.nextElement();
			if(ze.isDirectory()){
				File f = new File(dir+ze.getName());
				if(!f.exists())
					f.mkdir();
				continue;
			}
			OutputStream os = new BufferedOutputStream(
					new FileOutputStream(new File(dir+ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readlen = 0;
			while ((readlen=is.read(buf, 0, bufferLen))!=-1) {
				os.write(buf, 0, readlen);
			}
			is.close();
			os.close();
		}
		File file = new File(zipFileName);
		if(file.exists())
			file.delete();
	}
}
