import java.net.DatagramSocket;

class Client implements Runnable {

  private Snapshot snapshot;
  private ClientCanvas clientCanvas;
  FileCom fileCom = null;
  FileComImage fileComImage = null;
  private DatagramSocket dsCheckDummy;
  SeatDialog seat;
  InfoDialog info;
  private boolean writeTempFileFlag = false;
  private final int sleepPeriod = 100;
  private final int sleepCountNum = 1*10; // 約1秒
  private int sleepCount = 0;

  String[][] infoSa = {
    { "a", "A" },
    { "b", "B" },
    { "c", "C" },
    { "d", "D" },
    { "e", "E" },
    { "Z", "キャンセル" }
  };

  public Client(Snapshot _snapshot) throws Exception{
    try{
      snapshot = _snapshot;
      try{
        dsCheckDummy =  new DatagramSocket(snapshot.ini.getUdpPortForCheck());
      }catch(Exception e){
        C.error("Client: Client: 既に起動しています。" + e.toString());
        C.exit();
      }
      clientCanvas = new ClientCanvas(snapshot);
      fileCom = new FileCom(snapshot);
      fileComImage = new FileComImage(snapshot);
      C.info(snapshot.execDir + "\n\n" +
             "授業終了まで起動したままにして下さい。\nヘルプはHキーです。" +
             "\n\n次に着席した席の番号を入力します。\n次の画面で、正しい席番号を選択後、OKを押して下さい。");
      seat = new SeatDialog(snapshot);
      seat.select();
      fileCom.writeSeat(seat.get());
      info = new InfoDialog(snapshot, infoSa);
      new Thread(this).start();
    }catch(Exception e){
      C.throwException("Client: Client:", e);
    }
  }

  public ClientCanvas getClientCanvas(){ return clientCanvas; }

  public void run(){
    try{
      while(true){
        if(writeTempFileFlag){
          snapshot.capture();
          fileComImage.writeTempFile();
        }
        if(++sleepCount > sleepCountNum){
          sleepCount = 0;
          doJunkaiCommandT();
          checkReceiveCommandT();
        }
        Thread.sleep(sleepPeriod);
      }
    }catch(Exception e){
      C.error("Client: run:" + C.cr + e.toString());
    }
  }

  void checkReceiveCommandT() throws Exception{
    try{
      String c = fileComImage.getReceiveCommand(), u = snapshot.userId;
      if(c.indexOf(u) != -1){
        if(writeTempFileFlag == false){
          fileCom.writeReceive();
          fileComImage.deleteTempFiles();
          writeTempFileFlag = true;
        }
      }else{
        writeTempFileFlag = false;
      }
    }catch(Exception e){
      writeTempFileFlag = false;
      C.throwException("Client: checkReceiveCommandT: ", e);
    }
  }

  void doJunkaiCommandT() throws Exception{
    try{
      String c = fileComImage.getJunkaiCommand(), u = snapshot.userId;
      if(c.indexOf(u) != -1){
        snapshot.getMousePointForCapture();
        snapshot.capture();
        fileComImage.writeJunkaiFile(u);
        fileCom.writeAck();
      }
    }catch(Exception e){
      C.throwException("Client: doCommandT: ", e);
    }
  }

}
