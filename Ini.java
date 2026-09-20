import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class Ini {

  private String logFilePath = "";
  private String captureDir = "";
  private String comDir = "";
  private int udpPortForCheck = -1;
  private String execMode = "";
  private String userListFilePath = "";
  private String shussekiFilePath = "";

  public Ini(String fileName) throws Exception{
    int cnt = 1;
    try{
      FileReader fr = new FileReader(fileName);
      BufferedReader br = new BufferedReader(fr);
      String line;
      while((cnt > 0) && ((line = br.readLine()) != null)){
        switch(cnt++){
        case 1: logFilePath = line; break;
        case 2: captureDir = line; break;
        case 3: comDir = line; break;
        case 4: udpPortForCheck = Integer.parseInt(line); break;
        case 5:
          execMode = line;
          if(execMode == "")
            cnt = -1;
          break;
        case 6: userListFilePath = line; break;
        case 7: shussekiFilePath = line; break;
        default: cnt = -1; break;
        }
      }
      br.close();
      fr.close();
    }catch(Exception e){
      C.throwException("Ini: Ini: cnt=" + cnt + ": ", e);
    }
  }

  public String getLogFilePath(){ return logFilePath; }
  public String getCaptureDir(){ return captureDir; }
  public String getComDir(){ return comDir; }
  public int getUdpPortForCheck(){ return udpPortForCheck; }
  public String getExecMode(){ return execMode; }
  public String getUserListFilePath(){ return userListFilePath; }
  public String getShussekiFilePath(){ return shussekiFilePath; }

}
