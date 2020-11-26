package cn.tellsea.utils;

import java.io.*;

/**

 */
public class FileUtil {

    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "temp";



    /**
     * 
     * @Title: writerFile 
     * @Description: 写文件
     * @params @param fileName
     * @params @param content
     * @throws
     */
    public static void writerFile(String fileName, String content) {

        File dir = new File(ROOT_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ROOT_PATH +File.separator + fileName, true)));
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 
     * @Title: readFile 
     * @Description: 读文件
     * @params @param fileName
     * @params @return
     * @return String
     * @throws
     */
    public static String readFile(String fileName) {
        String result = null;
        try {
            File file = new File(System.getProperty("user.dir") + File.separator + "temp" + File.separator + fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer content = new StringBuffer();
            String line = "";
            while ((line=bufferedReader.readLine())!=null){
                content = content.append(line);
            }
            bufferedReader.close();
            result = content.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
