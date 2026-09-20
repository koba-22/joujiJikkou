import java.io.File;
import java.io.FileWriter;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ServerCanvas extends BaseCanvas {

  private final int Ulw = 230, Ulp = 2, Uln = 18;
  private int sortMode = ClientList.SORT_MODE_USERID;
  private int currentUserIdIndex = 0;
  private int timerPeriod = 60;
  private int elapsedTime = -1;
  private TimerThread timerThread = null;
  private String[] userList = null;
  private String helpStringNormal =
    "スペースキー ... 学生情報画面に切り替える\n" +
    "                 受信中の時は、受信を停止し、ファイル閲覧表示にしてから、切り替える\n" +
    "I ... Info 情報を入力する\n" +
    "R ... Receive 受信停止\n" +
    "J ... Junkai 巡回開始または停止\n" +
    "F ... File/Net ファイル閲覧と、受信の表示を切替える\n" +
    "               受信中の時は、受信を停止し、ファイル閲覧表示にする\n" +
    "C ... Capture すぐに画面キャプチャ\n" +
    "K ... Keep 最小化せずにすぐに画面キャプチャ\n" +
    "W ... Wait 7秒後に画面キャプチャ\n" +
    "上カーソルキー ... ウィンドウの大きさを最大化\n" +
    "下カーソルキー ... ウィンドウの大きさを元に戻す\n";
  private String helpStringUserId =
    "スペースキー ... 画像表示画面に戻る\n" +
    "I ... Info 情報を表示する\n" +
    "T ... タイマー スタート/ストップ\n" + 
    "S ... Sort 学生ID順と席番号順の並べ替え\n" +
    "R ... Receive 受信開始または停止\n" +
    "              選択した学生の画面を受信し続ける\n" +
    "              開始の時は、自動的に画像表示画面に戻る\n" +
    "              停止の時は、画像表示画面をファイル閲覧表示にする\n" +
    "C ... Capture すぐに画面キャプチャ\n" +
    "K ... Keep 最小化せずにすぐに画面キャプチャ\n" +
    "W ... Wait 7秒後に画面キャプチャ\n" +
    "Shift+S ... Save 出席ファイルを書き込み\n" +
    "カーソルキー ... 学生を選択するために上下左右に移動\n" +
    "マウスクリック ... 学生を選択\n";

  public ServerCanvas(Snapshot _snapshot) throws Exception{
    super(_snapshot);
  }

  public void capture(int mode){
    switch(mode){
    case 0:
      snapshot.getMousePointForCapture();
      snapshot.setExtendedState(JFrame.ICONIFIED);
      C.sleep(100);
      break;
    case 1:
      break;
    case 2:
      snapshot.setExtendedState(JFrame.ICONIFIED);
      if(!C.confirm("7秒後に画面をキャプチャします。" + C.cr + "準備はできましたか？")){
        C.info("画面をキャプチャしませんでした。");
        return;
      }
      C.sleep(7*1000);
      snapshot.getMousePointForCapture();
      break;
    }
    snapshot.server.captureAndSave();
    lastImage();
    drawMode = DRAW_FILE;
    snapshot.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }

  String userInfo(int mode, int index){
    if(userList == null)
      return "";
    switch(sortMode){
    case ClientList.SORT_MODE_USERID: return userInfoUserId(mode, index);
    case ClientList.SORT_MODE_SEATNO: return userInfoSeatNo(mode, index);
    }
    return "";
  }

  String userInfoUserId(int mode, int index){
    String ret = "";
    String[] sa = userList[index].split(ClientList.FIELD_SEP);
    switch(mode){
    case 0:
      ret += sa[0];
      for(int i = 1; i < sa.length; i++)
        ret += ClientList.FIELD_SEP + sa[i];
      break;
    case 1:
      ret += sa[1] + " ";
      ret += sa[0];
      if(sa.length > 2){
        ret += " ";
        switch(sa[2].length()){
        case 0: ret += "　　　"; break;
        case 1: ret += sa[2] + "　　"; break;
        case 2: ret += sa[2] + "　"; break;
        default: ret += sa[2].substring(0, 3); break;
        }
      }
      break;
    case 2:
      ret = sa[0];
      break;
    case 3:
      ret = sa[3];
      ret = ret.substring(ret.length() -1);
      break;
    }
    return ret;
  }

  String userInfoSeatNo(int mode, int index){
    String ret = "";
    String[] sa = userList[index].split(ClientList.FIELD_SEP);
    switch(mode){
    case 0:
      ret += sa[0] + ClientList.FIELD_SEP;
      ret += sa[1];
      for(int i = 2; i < sa.length; i++)
        ret += ClientList.FIELD_SEP + sa[i];
      break;
    case 1:
      ret += sa[0] + " ";
      ret += sa[1];
      if(sa.length > 2){
        ret += " ";
        switch(sa[2].length()){
        case 0: ret += "　　　"; break;
        case 1: ret += sa[2] + "　　"; break;
        case 2: ret += sa[2] + "　"; break;
        default: ret += sa[2].substring(0, 3); break;
        }
      }
      break;
    case 2:
      ret = sa[1];
      break;
    case 3:
      ret = sa[3];
      ret = ret.substring(ret.length() -1);
      break;
    }
    return ret;
  }

  void paintUserId(Graphics g){
    FontMetrics fm = g.getFontMetrics();
    int fh = fm.getHeight(), fd = fm.getDescent();
    snapshot.setTitle("学生選択");
    paintForTimer(g);
    String s = "" + userList.length + " 人 ";
    g.drawString(s, Ulp, fh + Ulp - fd);
    g.drawString(userInfo(0, currentUserIdIndex), Ulp + fm.stringWidth(s), fh + Ulp - fd);
    for(int i = 0; i < userList.length; i++){
      if(userInfo(3, i).charAt(0) == FileCom.MESSAGE_TYPE_DOWN) g.setColor(Color.RED);
      g.drawString(userInfo(1, i), (i / Uln) * (Ulw + Ulp *2) + Ulp, ((i % Uln) +1 +1)  * (fh + Ulp *2) - fd - Ulp);
      g.setColor(Color.BLACK);
    }
    g.drawRect((currentUserIdIndex / Uln) * (Ulw + Ulp *2) + Ulp,
               (currentUserIdIndex % Uln +1) * (fh + Ulp *2) + Ulp, Ulw - Ulp *2, fh - Ulp *2);
  }

  public void mousePressedUserId(){
    if((userList == null) || (font == null))
      return;
    int h = getFontMetrics(font).getHeight() + Ulp *2;
    if(mouseY >= h){
      int i = (mouseX / Ulw) * Uln + (mouseY - h) / h;
      if(i < userList.length){
        currentUserIdIndex = i;
        repaint();
      }
    }
  }

  private void paintForTimer(Graphics g){
    if(timerThread == null)
      return;
    Color c = g.getColor();
    if(timerPeriod - elapsedTime <= 10) g.setColor(Color.red);
    else g.setColor(Color.green);
    g.fillRect(0, 0, getWidth() * (elapsedTime +1) / timerPeriod, getHeight());
    g.setColor(c);
  }

  public void repaintFromTimer(int _elapsedTime){
    if(elapsedTime == _elapsedTime)
      return;
    elapsedTime = _elapsedTime;
    repaint();
  }

  public void setTimer(){
    if(timerThread != null){
      timerThread.end();
      timerThread = null;
    }
    String v = JOptionPane.showInputDialog(null, "10<=タイマー間隔(秒)<=5400", "" + timerPeriod);
    if(v == null)
      return;
    try{
      int tp = Integer.parseInt(v);
      if(tp < 10)
        C.info("タイマー間隔は10秒以上にして下さい。" + tp);
      if(tp > 5400)
        C.info("タイマー間隔は5400秒以下にして下さい。" + tp);
      timerPeriod = tp;
      timerThread = new TimerThread(this, timerPeriod);
      timerThread.start();
    }catch(Exception e){
      C.info("タイマー間隔には整数を入力して下さい。" + v);
    }
  }

  public void keyReleasedUserId(int key, int modifiers){
    int shift = KeyEvent.SHIFT_DOWN_MASK;
    int len = userList.length;
    switch(key){
    case KeyEvent.VK_H: case KeyEvent.VK_ESCAPE: keyReleasedNormal(key, modifiers); break;
    case KeyEvent.VK_SPACE:
      menuMode = MENU_NORMAL;
      canvasClearFlag = false;
      break;
    case KeyEvent.VK_I:
      C.info(userInfo(0, currentUserIdIndex) );
      break;
    case KeyEvent.VK_T:
      setTimer();
      break;
    case KeyEvent.VK_S:
      if((modifiers & shift) == shift){
        if(C.confirm("本当に出席ファイルに書き込みますか？"))
          writeUserListFile();
      }else{
        sortMode++;
        if(sortMode > ClientList.SORT_MODE_SEATNO)
          sortMode = ClientList.SORT_MODE_USERID;
        if(snapshot.server.fileCom != null)
          snapshot.server.fileCom.read();
        userList = snapshot.server.clientList.getUserList(sortMode);
        repaint();
      }
      break;
    case KeyEvent.VK_R:
      if(snapshot.server.receiveToggle(userInfo(2, currentUserIdIndex))){
        menuMode = MENU_NORMAL;
        canvasClearFlag = false;
        drawMode = DRAW_NET;
        C.info("受信を開始しました。");
      }else{
        drawMode = DRAW_FILE;
        C.info("受信を停止しました。");
      }
      repaint();
      break;
    case KeyEvent.VK_C: capture(0); repaint(); break;
    case KeyEvent.VK_K: capture(1); repaint(); break;
    case KeyEvent.VK_W: capture(2); repaint(); break;
    case KeyEvent.VK_UP:
      currentUserIdIndex--;
      if(currentUserIdIndex < 0)
        currentUserIdIndex = len -1;
      break;
    case KeyEvent.VK_DOWN:
      currentUserIdIndex++;
      if(currentUserIdIndex >= len)
        currentUserIdIndex = 0;
      break;
    case KeyEvent.VK_LEFT:
      currentUserIdIndex -= Uln;
      if(currentUserIdIndex < 0){
        currentUserIdIndex += (len / Uln +1) * Uln;
        if(currentUserIdIndex >= len)
          currentUserIdIndex = len -1;
        if(currentUserIdIndex < 0)
          currentUserIdIndex = 0;
      }
      break;
    case KeyEvent.VK_RIGHT:
      currentUserIdIndex += Uln;
      if(currentUserIdIndex >= len){
        currentUserIdIndex -= (len / Uln +1) * Uln;
        if(currentUserIdIndex >= len)
          currentUserIdIndex = len -1;
        if(currentUserIdIndex < 0)
          currentUserIdIndex = 0;
      }
      break;
    }
    repaint();
  }

  public void keyReleasedNormal(int key, int modifiers){
    switch(key){
    case KeyEvent.VK_ESCAPE:
      if(C.confirm("本当に終了しますか？")){
        if(C.confirm("終了すると出席情報は破棄されます。" + C.cr
                     + "出席ファイルに書き込みますか？")){
          writeUserListFile();
        }
        try{
          snapshot.server.fileComImage.setJunkaiCommand("");
        }catch(Exception e){
          C.e("ServerCanvas: KeyReleasedNormal: VK_ESCAPE: fileComImage.setJunkaiCommand");
        }
        C.d("ServerCanvas: KeyReleasedNormal: VK_ESCAPE: end");
        C.exit();
      }
      break;
    case KeyEvent.VK_SPACE:
      if(snapshot.server.receiveOff()){
        drawMode = DRAW_FILE;
        C.info("受信を停止しました。");
      }
      snapshot.server.fileCom.read();
      userList = snapshot.server.clientList.getUserList(sortMode);
      if(userList == null){
        C.info("ユーザが登録されていません。");
        break;
      }
      menuMode = MENU_USERID;
      canvasClearFlag = true;
      repaint();
      break;
    case KeyEvent.VK_I:
      try{
        snapshot.server.info.select();
        snapshot.server.fileCom.writeInfo(snapshot.server.info.get());
      }catch(Exception e){
        C.error("ServerCanvas: keyReleasedNormal: " + e.toString());
      }
      break;
    case KeyEvent.VK_R:
      if(snapshot.server.receiveOff()){
        drawMode = DRAW_FILE;
        repaint();
        C.info("受信を停止しました。");
      }else{
        C.info("受信は既に停止しています。");
      }
      break;
    case KeyEvent.VK_J:
      if(snapshot.server.junkaiToggle())
        C.info("巡回を再開しました。");
      else
        C.info("巡回を中止しました。");
      break;
    case KeyEvent.VK_F:
      switch(drawMode){
      case DRAW_FILE: drawMode = DRAW_NET; break;
      case DRAW_NET:
        if(snapshot.server.receiveOff())
          C.info("受信を停止しました。");
        drawMode = DRAW_FILE; break;
      } repaint(); break;
    case KeyEvent.VK_C: capture(0); repaint(); break;
    case KeyEvent.VK_K: capture(1); repaint(); break;
    case KeyEvent.VK_W: capture(2); repaint(); break;
    case KeyEvent.VK_UP: snapshot.setExtendedState(JFrame.MAXIMIZED_BOTH); repaint(); break;
    case KeyEvent.VK_DOWN: snapshot.setExtendedState(JFrame.NORMAL); repaint(); break;
    default: super.keyReleasedNormal(key, modifiers); break;
    }
  }

  void viewHelp(){
    switch(menuMode){
    case MENU_NORMAL:
      JOptionPane.showMessageDialog(null, helpString + helpStringNormal, "ヘルプ", JOptionPane.PLAIN_MESSAGE);
      break;
    case MENU_USERID:
      JOptionPane.showMessageDialog(null, helpString2 + helpStringUserId, "ヘルプ", JOptionPane.PLAIN_MESSAGE);
      break;
    }
  }

  void writeUserListFile(){
    if(snapshot.server.fileCom != null)
      snapshot.server.fileCom.read();
    userList = snapshot.server.clientList.getUserList(ClientList.SORT_MODE_SAVE);
    if(userList == null){
      C.info("ユーザが登録されていません。");
      return;
    }
    String path = snapshot.ini.getShussekiFilePath();
    try{
      File f = new File(path);
      f.setWritable(true);
      FileWriter fw = new FileWriter(f, true);
      String ts = C.getCustomDateTimeString(C.CUSTOM_DATE_TIME_COMMA) + ",", s = "";
      for(int i = 0; i < userList.length; i++){
        s = ts + userList[i] + C.cr;
        fw.write(s, 0, s.length());
      }
      fw.close();
      f.setWritable(false);
    }catch(Exception e){
      C.e("ServerCanvas: writeUserListFile: 1: " + e.toString());
      C.info("ファイル " + path + " に正常に書き込みができませんでした。");
      return;
    }
    try{
      Runtime.getRuntime().exec("notepad.exe " + path);
    }catch(Exception e){
      C.e("ServerCanvas: writeUserListFile: 2: " + e.toString());
      C.info("notepad.exeを起動できませんでした。");
    }
  }

  public void readAndPaintNetImage(String path){
    netImage = snapshot.toolkit.getImage(path);
    repaint();
  }

}
