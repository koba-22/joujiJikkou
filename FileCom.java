import java.io.File;
import java.util.Calendar;
import java.security.MessageDigest;

public class FileCom {

  static final char MESSAGE_TYPE_SEAT = 's', MESSAGE_TYPE_ACK = 'a', MESSAGE_TYPE_RECEIVE = 'r',
    MESSAGE_TYPE_DOWN = 'd', MESSAGE_TYPE_INFO = 'i';

  private final String COM_DIR = "_出席情報22時間保存";

  private final String password = "univ";
  private final String algorithm = "SHA1";

  private final String FORMAT_SEP = "_";
  private final String FORMAT = "%s\\%s_%s_%s_%s_";
  private final long DELETE_CHECK_TIME = 22*60*60*1000;
  private Snapshot snapshot;
  private String comDir = "";
  private String readList = "";

  public FileCom(Snapshot _snapshot){
    snapshot = _snapshot;
    comDir = snapshot.ini.getComDir() + "\\" + COM_DIR;
    mkdirOne(comDir);
  }

  String getMessageDigest(String str){
    String mds = "";
    try{
      byte[] mdb = MessageDigest.getInstance(algorithm).digest(str.getBytes());
      for(int i = 0; i < mdb.length; i++){
        mds += String.format("%02x", mdb[i]);
      }
    }catch(Exception e){
      mds = e.toString();
    }
    return mds;
  }

  void mkdirOne(String dir){
    File f = new File(dir);
    if(!f.exists()){
      if(!f.mkdir()){
        String s = "FileCom: mkdirOne: " + dir;
        C.error(s);
      }
    }
  }

  public void writeSeat(String s){
    try{
      C.d("FileCom: writeSeat: MESSAGE_TYPE_SEAT: " + s);
      write(Character.toString(MESSAGE_TYPE_SEAT) + s);
    }catch(Exception e){
      C.error("FileCom: writeSeat: " + e.toString());
    }
  }

  public void writeInfo(String s){
    try{
      C.d("FileCom: writeInfo: MESSAGE_TYPE_INFO: " + s);
      write(Character.toString(MESSAGE_TYPE_INFO) + s);
    }catch(Exception e){
      C.error("FileCom: writeSeat: " + e.toString());
    }
  }

  public void writeDown(){
    try{
      C.d("FileCom: writeDown: MESSAGE_TYPE_DOWN:");
      write(Character.toString(MESSAGE_TYPE_DOWN));
    }catch(Exception e){
      C.error("FileCom: writeDown: " + e.toString());
    }
  }

  public void writeAck(){
    try{
      C.d("FileCom: writeAck: MESSAGE_TYPE_ACK: ");
      write(Character.toString(MESSAGE_TYPE_ACK));
    }catch(Exception e){
      C.error("FileCom: writeAck: " + e.toString());
    }
  }

  public void writeReceive(){
    try{
      C.d("FileCom: writeReceive: MESSAGE_TYPE_RECEIVE: ");
      write(Character.toString(MESSAGE_TYPE_RECEIVE));
    }catch(Exception e){
      C.error("FileCom: writeReceive: " + e.toString());
    }
  }

  void write(String s) throws Exception{
    try{
      String userId = snapshot.userId, dateTime = C.getDateTimeString(),
        mds = getMessageDigest(userId + dateTime + password);
      String fn = String.format(FORMAT, comDir, userId, dateTime, s, snapshot.getIp());
      fn += mds;
      new File(fn).createNewFile();
    }catch(Exception e){
      C.throwException("FileCom: write:", e);
    }
  }

  public void addToClientList(String s){
    String[] sa = s.split(FORMAT_SEP);
    if(sa.length < 5){
      C.e("FileCom: addToClientList: sa.length < 5: " + s);
      return;
    }
    String mds = getMessageDigest(sa[0] + sa[1] + password);
    if(!sa[4].equals(mds)){
      C.e("FileCom: 不正ファイル: " + s);
      return;
    }
    String hcm = sa[1].substring(9, 11) + ":" + sa[1].substring(11, 13) + ":" + sa[1].substring(13, 15);
    snapshot.server.clientList.addClientAndMessage(sa[0], hcm, sa[2], sa[3]);
  }

  public void read(){
    try{
      File[] fa = (new File(comDir)).listFiles();
      for(int i = 0; i < fa.length; i++)
        if(fa[i].isFile()){
          String fn = fa[i].getName();
          if(readList.indexOf(fn) == -1){
            addToClientList(fn);
            readList += fn + " ";
          }
        }
    }catch(Exception e){
      C.error("FileCom: read: " + e.toString());
    }
  }

  public void deleteFiles(){
    try{
      File[] fa = (new File(comDir)).listFiles();
      long now = Calendar.getInstance().getTimeInMillis();
      for(int i = 0; i < fa.length; i++)
        if(fa[i].isFile() && (now - fa[i].lastModified() > DELETE_CHECK_TIME))
          fa[i].delete();
    }catch(Exception e){
      C.error("FileCom: deleteFiles: " + e.toString());
    }
  }

}
