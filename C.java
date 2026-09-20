import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

public class C {

  static final String cr = "\r\n";
  static final String cr2 = cr + cr;
  static final int CUSTOM_DATE_TIME_HCM = 0, CUSTOM_DATE_TIME_COMMA = 1, CUSTOM_DATE_TIME_TSUKI_BYOU = 2;
  private static Log log = null;
  
  static void p(String msg){ System.out.println(msg); }
  static void pd(String msg){ C.p("debug: " + msg); }
  static void throwException(String msg) throws Exception{ throw new Exception(msg + cr); }
  static void throwException(String msg, String s) throws Exception{ throw new Exception(msg + " " + s + cr); }
  static void throwException(String msg, Exception e) throws Exception{
    throw new Exception(msg + " " + e.toString() + cr); }
  static void exit(){ System.exit(0); }

  static void openLogFile(String path) throws Exception{
    try{
      log = new Log(path);
    }catch(Exception e){
      C.throwException("C: openLogFile:", e);
    }
  }

  static boolean confirm(String msg){
    return (JOptionPane.showConfirmDialog(null, msg, "確認", JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION) ? true : false;
  }

  static void info(String msg){
    JOptionPane.showMessageDialog(null, msg, "情報", JOptionPane.INFORMATION_MESSAGE);
  }

  static void error(String msg){
    C.log("Error: " + msg);
    JOptionPane.showMessageDialog(null, msg, "エラー", JOptionPane.ERROR_MESSAGE);
  }

  static void d(String msg){ C.log("Debug: " + msg); }
  static void e(String msg){ C.log("Error: " + msg); }

  static void log(String msg){
    try{
      log.write(msg);
    }catch(Exception e){
      JOptionPane.showMessageDialog(null, "C: log: " + cr2 + e.toString(), "エラー", JOptionPane.ERROR_MESSAGE);
    }
  }

  static public void sleep(int msec){
    try{
      Thread.sleep(msec);
    }catch(Exception e){}
  }

  static String getExecDir() throws Exception{
    String ret = null;
    try{
      String s = C.class.getClassLoader().getResource("C.class").toString();
      // debug: jar:file:/C:/tmp/snapshot.jar!/C.class
      Matcher m = Pattern.compile("jar:file:/(.+)/.+\\.jar").matcher(s);
      if(m.find())
        ret = URLDecoder.decode(m.group(1).replace("/", "\\"), "UTF-8");
    }catch(Exception e){
      C.throwException("C: getExecDir:", e);
    }
    return ret;
  }

  static String getDateTimeString(){
    Calendar c = Calendar.getInstance();
    int y = c.get(Calendar.YEAR), mo = c.get(Calendar.MONTH) +1 , d = c.get(Calendar.DATE),
        h = c.get(Calendar.HOUR_OF_DAY), m = c.get(Calendar.MINUTE), s = c.get(Calendar.SECOND);
    return String.format("%04d%02d%02d-%02d%02d%02d", y, mo, d, h, m, s);
  }

  static String getCustomDateTimeString(int customDateTime){
    Calendar c = Calendar.getInstance();
    int y = c.get(Calendar.YEAR), mo = c.get(Calendar.MONTH) +1 , d = c.get(Calendar.DATE),
      h = c.get(Calendar.HOUR_OF_DAY), m = c.get(Calendar.MINUTE), s = c.get(Calendar.SECOND);
    switch(customDateTime){
    case CUSTOM_DATE_TIME_HCM: return String.format("%02d:%02d", h, m);
    case CUSTOM_DATE_TIME_COMMA: return String.format("%04d年%02d月%02d日,%02d時%02d分%02d秒", y, mo, d, h, m, s);
    case CUSTOM_DATE_TIME_TSUKI_BYOU: return String.format("%02d月%02d日%02d時%02d分%02d秒", mo, d, h, m, s);
    }
    return "";
  }

  static public String getHardwareInfo(){
    String str = "";
    try {
      str += "Hardware Info ---" + cr;
      Enumeration en = NetworkInterface.getNetworkInterfaces();
      while(en.hasMoreElements()){
        NetworkInterface ni = (NetworkInterface)en.nextElement();
        byte[] ma = ni.getHardwareAddress();
        if(ma != null){
          str += "Hardware Address: ";
          for(int i = 0; i < ma.length; i++)
            str += String.format("%02x", ma[i]);
          str += cr;
        }
        str += "Name: " + ni.getName() + cr;
        str += "Display Name: " + ni.getDisplayName() + C.cr;
        Enumeration ea = ni.getInetAddresses();
        while(ea.hasMoreElements()){
          InetAddress ia = (InetAddress)ea.nextElement();
          String ip = ia.getHostAddress();
          str += "Host Address: " + ip + C.cr;
        }
        str += "---" + cr;
      }
    }catch(Exception e){
      str += "error 2" + e.toString();
    }
    return str;
  }
  
  static public String getStudentNo(String userId){ // 適切に変更のこと。!!!    
    String no = userId;
    return no;
  }

}
