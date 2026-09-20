import java.io.FileWriter;

public class Log {

  private FileWriter fw;

  public Log(String path) throws Exception{
    try{
      fw = new FileWriter(path, true);
    }catch(Exception e){
      C.throwException("Log: Log:", e);
    }
  }

  public void write(String msg) throws Exception{
    try{
      String s = String.format("%s %s%s", C.getDateTimeString(), msg, C.cr);
      fw.write(s, 0, s.length());
      fw.flush();
    }catch(Exception e){
      C.throwException("Log: write:", e);
    }
  }

}
