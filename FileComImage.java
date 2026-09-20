import java.io.File;
import javax.imageio.ImageIO;
import java.util.Arrays;

public class FileComImage {

  private final String JUNKAI_DIR = "__巡回画像";
  private final String JUNKAI_COMMAND_DIR = "_一時的巡回コマンド";
  private final String RECEIVE_COMMAND_DIR = "_一時的受信コマンド";
  private final String TEMP_DIR = "_一時的画像";

  private Snapshot snapshot;
  private String comDir = "", junkaiDir = "", junkaiCommandDir = "", receiveCommandDir = "", tempDir = "";
  private final int tempFileNum = 10;
  private int tempFileCount = 0;
  private String checkedTempFile = "";
  private String checkedJunkaiCommand = "";

  public FileComImage(Snapshot _snapshot){
    snapshot = _snapshot;
    comDir = snapshot.ini.getComDir();
    junkaiDir = comDir + "\\" + JUNKAI_DIR;
    junkaiCommandDir = comDir + "\\" + JUNKAI_COMMAND_DIR;
    receiveCommandDir = comDir + "\\" + RECEIVE_COMMAND_DIR;
    tempDir = comDir + "\\" + TEMP_DIR;
    mkdir();
  }

  void mkdirOne(String dir){
    File f = new File(dir);
    if(!f.exists()){
      if(!f.mkdir()){
        String s = "FileComImage: mkdirOne: " + dir;
        C.error(s);
      }
    }
  }

  public void mkdir(){
    mkdirOne(junkaiDir);
    mkdirOne(junkaiCommandDir);
    mkdirOne(receiveCommandDir);
    mkdirOne(tempDir);
  }

  public String getJunkaiCommand() throws Exception{
    try{
      String[] sa = new File(junkaiCommandDir).list(); // .と..は含まないとのこと。
      int len = sa.length;
      if(len > 0){
        String s = sa[0];
        if(checkedJunkaiCommand.indexOf(s) == -1){
          checkedJunkaiCommand += s + " ";
          return s;
        }
      }
    }catch(Exception e){
      C.throwException("FileComImage: getJunkaiCommand: ", e);
    }
    return "";
  }

  public void setJunkaiCommand(String userId) throws Exception{
    deleteFiles(junkaiCommandDir);
    if(userId == "")
      return;
    try{
      new File(junkaiCommandDir + "\\" + userId + "-" + C.getDateTimeString()).createNewFile();
    }catch(Exception e){
      C.throwException("FileComImage: setJunkaiCommand: ", e);
    }
  }

  public String getReceiveCommand() throws Exception{
    try{
      String[] sa = new File(receiveCommandDir).list(); // .と..は含まないとのこと。
      int len = sa.length;
      if(len > 0)
        return sa[0];
    }catch(Exception e){
      C.throwException("FileComImage: getJunkaiCommand: ", e);
    }
    return "";
  }

  public void setReceiveCommand(String userId) throws Exception{
    deleteFiles(receiveCommandDir);
    if(userId == "")
      return;
    try{
      new File(receiveCommandDir + "\\" + userId).createNewFile();
    }catch(Exception e){
      C.throwException("FileComImage: setReceiveCommand: ", e);
    }
  }

  void deleteFiles(String dir){
    File f = new File(dir);
    if(f.exists()){
      try{
        File[] fa = f.listFiles();
        for(int i = 0; i < fa.length; i++)
          if(fa[i].isFile())
            fa[i].delete();
      }catch(Exception e){
        C.error("FileComImage: deleteFiles: " + e.toString());
      }
    }
  }

  public void writeJunkaiFile(String userId) throws Exception{
    String path = String.format("%s\\%s-%s.%s", junkaiDir, userId,
                                C.getCustomDateTimeString(C.CUSTOM_DATE_TIME_TSUKI_BYOU), snapshot.IMAGE_TYPE);
    try{
      ImageIO.write(snapshot.captureImage, snapshot.IMAGE_TYPE, new File(path));
    }catch(Exception e){
      C.throwException("FileComImage: writeJunkaiFile: " + path, e);
    }
  }

  public void writeTempFile() throws Exception{
    String path = "";
    try{
      path = String.format("%s\\%05d.%s", tempDir, tempFileCount, snapshot.IMAGE_TYPE);
      tempFileCount++;
      ImageIO.write(snapshot.captureImage, snapshot.IMAGE_TYPE, new File(path));
    }catch(Exception e){
      C.throwException("FileComImage: writeTempFile: " + path, e);
    }
  }

  public void deleteTempFiles(){ deleteFiles(tempDir); tempFileCount = 0; checkedTempFile = ""; }

  public String getTempFilePathAndDelete() throws Exception{
    try{
      String[] sa = new File(tempDir).list(); // .と..は含まないとのこと。
      int len = sa.length;
      if(len >= 2){
        Arrays.sort(sa);
        for(int i = 0; i < len -2; i++)
          new File(tempDir + "\\" + sa[0]).delete();
        String s = sa[len -2];
        if(checkedTempFile.indexOf(s) == -1){
          checkedTempFile += s + " ";
          return tempDir + "\\" + s;
        }
      }
    }catch(Exception e){
      C.throwException("FileComImage: getTempFileName:", e);
    }
    return "";
  }

}
