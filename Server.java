import java.io.File;
import javax.imageio.ImageIO;
import java.net.DatagramSocket;

class Server implements Runnable {

  private Snapshot snapshot;
  private ServerCanvas serverCanvas;
  FileCom fileCom = null;
  FileComImage fileComImage = null;
  ClientList clientList;
  private DatagramSocket dsCheckDummy;
  private boolean receiveFlag = false;
  private boolean junkaiFlag = true;
  private final int sleepPeriod = 100;
  private final int sleepJunkaiCountNumDoing = 3*10; // 約3秒
  private final int sleepJunkaiCountNumWaiting = 10*60*10; // 約10分
  private int sleepJunkaiCountNum = sleepJunkaiCountNumDoing;
  private int sleepJunkaiCount = 0;
  InfoDialog info;

  String[][] infoSa = {
    { "A", "1" },
    { "B", "2" },
    { "C", "3" },
    { "D", "4" },
    { "E", "5" },
    { "Z", "キャンセル" }
  };

  public Server(Snapshot _snapshot) throws Exception{
    try{
      snapshot = _snapshot;
      try{
        dsCheckDummy =  new DatagramSocket(snapshot.ini.getUdpPortForCheck());
      }catch(Exception e){
        C.error("Server: Server: 既に起動しています。" + e.toString());
        C.exit();
      }
      serverCanvas = new ServerCanvas(snapshot);
      clientList = new ClientList(snapshot);
      fileCom = new FileCom(snapshot);
      fileCom.deleteFiles();
      fileComImage = new FileComImage(snapshot);
      fileComImage.deleteTempFiles();
      if(C.confirm(snapshot.execDir + "\n\n巡回を開始しますか？")){
        junkaiFlag = true;
        C.info("巡回を開始しました。\n学生に注意を喚起して下さい。\nヘルプはHキーです。");
      }else{
        junkaiFlag = false;
        C.info("巡回を停止しました。\nJキーで開始できます。\nヘルプはHキーです。");
      }
      info = new InfoDialog(snapshot, infoSa);
      new Thread(this).start();
    }catch(Exception e){
      C.throwException("Server: Server:", e.toString());
    }
  }

  public boolean junkaiToggle(){
    if(junkaiFlag){
      junkaiFlag = false;
    }else{
      clientList.resetNextIndex();
      sleepJunkaiCountNum = sleepJunkaiCountNumDoing;
      sleepJunkaiCount = sleepJunkaiCountNum;
      junkaiFlag = true;
    }
    return junkaiFlag;
  }

  public boolean receiveOff(){
    if(receiveFlag){
      try{
        fileComImage.setReceiveCommand("");
        receiveFlag = false;
      }catch(Exception e){
        C.error("Server: receiveToggle: " + C.cr + e.toString());
      }
      return true;
    }
    return false;
  }

  public boolean receiveToggle(String userId){
    try{
      if(receiveFlag){
        fileComImage.setReceiveCommand("");
        receiveFlag = false;
      }else{
        fileComImage.deleteTempFiles();
        fileComImage.setReceiveCommand(userId);
        receiveFlag = true;
      }
    }catch(Exception e){
      C.error("Server: receiveToggle: " + C.cr + e.toString());
      return false;
    }
    return receiveFlag;
  }

  public void run(){
    try{
      while(true){
        if(receiveFlag){
          paintReceiveFileT();
        }
        if(junkaiFlag){
          if(++sleepJunkaiCount > sleepJunkaiCountNum){
            sleepJunkaiCount = 0;
            junkaiNextT();
          }
        }
        Thread.sleep(sleepPeriod);
      }
    }catch(Exception e){
      C.error("Server: run:" + C.cr + e.toString());
    }
  }

  void paintReceiveFileT(){
    try{
      String path = fileComImage.getTempFilePathAndDelete();
      if(path != "")
        serverCanvas.readAndPaintNetImage(path);
    }catch(Exception e){
      receiveFlag = false;
      C.error("Server: paintReceiveFileT: " + C.cr + e.toString());
    }
  }

  void junkaiNextT() throws Exception{
    fileCom.read();
    String userId = clientList.getNextUserId();
    sleepJunkaiCountNum = sleepJunkaiCountNumDoing;
    if(userId == ""){
      sleepJunkaiCountNum = sleepJunkaiCountNumWaiting;
      return;
    }
    try{
      fileComImage.setJunkaiCommand(userId);
    }catch(Exception e){
      C.throwException("Server: junkaiNextT: ", e);
    }
  }

  public ServerCanvas getServerCanvas(){ return serverCanvas; }

  public void captureAndSave(){
    try{
      snapshot.capture();
      writeCaptureFile();
    }catch(Exception e){
      C.error("Server: captureAndSave:" + C.cr + e.toString());
    }
  }

  public void writeCaptureFile() throws Exception{
    String path = "";
    try{
      path = String.format(snapshot.PATH_FORMAT, snapshot.ini.getCaptureDir(), C.getDateTimeString());
      ImageIO.write(snapshot.captureImage, snapshot.IMAGE_TYPE, new File(path));
    }catch(Exception e){
      C.throwException("Server: writeCaptureFile: " + path, e);
    }
  }

}
