import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class BaseCanvas extends Canvas implements KeyListener, MouseListener, MouseMotionListener {

  protected Snapshot snapshot;
  protected int drawX = 0, drawY = 0, mouseX = 0, mouseY = 0;
  protected boolean canvasClearFlag = false;
  protected String imageDir;
  protected final int MENU_NORMAL = 0, MENU_USERID = 1;
  protected int menuMode = MENU_NORMAL;
  protected int currentImageFileNameIndex = 0;
  protected Image currentFileImage = null;
  protected Image netImage = null;
  protected final int DRAW_FILE = 0, DRAW_NET = 1;
  protected int drawMode = DRAW_FILE;
  protected boolean magFlag = false;
  protected String[] imageFileNameList = null;
  protected Font font = null;
  protected String helpString1 = 
    "カーソル移動キー\n" +
    "　左 ... 1つ前のキャプチャ画面を表示\n" +
    "　右 ... 1つ後のキャプチャ画面を表示\n" +
    "　Shift+左 ... 最初のキャプチャ画面を表示\n" +
    "　Shift+右 ... 最新の状態に更新後、最後のキャプチャ画面を表示\n" +
    "マウスドラッグ ... 表示しているキャプチャ画面などの表示位置を変更\n" +
    "A ... 表示しているキャプチャ画面などを元の位置に戻す\n" +
    "M ... Mag 表示しているキャプチャ画面などを拡大/縮小\n";
  protected String helpString2 = 
    "H ... Help ヘルプを表示\n" +
    "ESC ... Escape 終了\n";
  protected String helpString = helpString1 + helpString2;
  
  public BaseCanvas(Snapshot _snapshot) throws Exception{
    try{
      snapshot = _snapshot;
      imageDir = snapshot.ini.getCaptureDir();
      addKeyListener(this);
      addMouseListener(this);
      addMouseMotionListener(this);
      setBackground(Color.WHITE);
      setForeground(Color.BLACK);
      font = new Font(Font.MONOSPACED, Font.PLAIN, 24);
      setFont(font);
      makeImageFileNameList();
      if(imageFileNameList.length > 0)
        getCurrentImageFromFile();
      setVisible(true);
    }catch(Exception e){
      C.throwException("BaseCanvas: BaseCanvas:", e.toString());
    }
  }

  void getCurrentImageFromFile(){
    currentFileImage = snapshot.toolkit.getImage(imageDir + "\\" + imageFileNameList[currentImageFileNameIndex]);
  }

  void prevImage(){
    if(imageFileNameList.length == 0)
      return;
    currentImageFileNameIndex--;
    if(currentImageFileNameIndex < 0)
      currentImageFileNameIndex = imageFileNameList.length -1;
    getCurrentImageFromFile();
  }

  void nextImage(){
    if(imageFileNameList.length == 0)
      return;
    currentImageFileNameIndex++;
    if(currentImageFileNameIndex >= imageFileNameList.length)
      currentImageFileNameIndex = 0;
    getCurrentImageFromFile();
  }
 
  void firstImage(){
    if(imageFileNameList.length == 0)
      return;
    currentImageFileNameIndex = 0;
    getCurrentImageFromFile();
  }

  void lastImage(){
    makeImageFileNameList();
    if(imageFileNameList.length == 0)
      return;
    currentImageFileNameIndex = imageFileNameList.length -1;
    getCurrentImageFromFile();
  }

  void makeImageFileNameList(){
    File[] fa = (new File(imageDir)).listFiles();
    int i = 0, n = 0;
    for(i = 0; i < fa.length; i++)
      if(fa[i].getName().endsWith("." + Snapshot.IMAGE_TYPE))
        n++;
    imageFileNameList = new String[n];
    n = 0;
    for(i = 0; i < fa.length; i++){
      String s = fa[i].getName();
      if(s.endsWith("." + Snapshot.IMAGE_TYPE))
        imageFileNameList[n++] = s;
    }
    if(n != imageFileNameList.length)
      C.error("BaseCanvas: makeImageFileNameList: n != imageFileNameList.length: "
              + n + "," + imageFileNameList.length);
    Arrays.sort(imageFileNameList);
  }

  public void paintNormal(Graphics g){
    switch(drawMode){
    case DRAW_FILE:
      if(currentFileImage != null){
        snapshot.setTitle("ファイル閲覧画面 " + imageFileNameList[currentImageFileNameIndex]);
        if(!magFlag)
          g.drawImage(currentFileImage, drawX, drawY, this);
        else
          g.drawImage(currentFileImage, drawX, drawY,
                      currentFileImage.getWidth(this) *2, currentFileImage.getHeight(this) *2, Color.BLACK, this);
      }else{
        snapshot.setTitle("ファイル閲覧画面 閲覧ファイル無し");
      }
      break;
    case DRAW_NET:
      if(netImage != null){
        snapshot.setTitle("受信画面 ");
        if(!magFlag)
          g.drawImage(netImage, drawX, drawY, this);
        else
          g.drawImage(netImage, drawX, drawY,
                      netImage.getWidth(this) *2, currentFileImage.getHeight(this) *2, Color.BLACK, this);
      }else{
        snapshot.setTitle("受信画面 受信画像無し");
      }
      break;
    }
  }

  void viewHelp(){
    JOptionPane.showMessageDialog(null, helpString, "ヘルプ", JOptionPane.PLAIN_MESSAGE);
  }

  void paintUserId(Graphics g){}

  public void paint(Graphics g){
    switch(menuMode){
    case MENU_NORMAL: paintNormal(g); break;
    case MENU_USERID: paintUserId(g); break;
    }
  }

  public void update(Graphics g){
    if(canvasClearFlag)
      g.clearRect(0, 0, (int)(snapshot.windowSize.getWidth()), (int)(snapshot.windowSize.getHeight()));
    paint(g);
  }

  public void keyReleasedNormal(int key, int modifiers){
    int shift = KeyEvent.SHIFT_DOWN_MASK;
    switch(key){
    case KeyEvent.VK_LEFT:
      drawMode = DRAW_FILE;
      if((modifiers & shift) == shift)
        firstImage();
      else
        prevImage();
      repaint();
      break;
    case KeyEvent.VK_RIGHT:
      drawMode = DRAW_FILE;
      if((modifiers & shift) == shift)
        lastImage();
      else
        nextImage();
      repaint();
      break;
    case KeyEvent.VK_A: drawX = 0; drawY = 0; repaint(); break;
    case KeyEvent.VK_M: magFlag = !magFlag; repaint(); break;
    case KeyEvent.VK_H: viewHelp(); break;
    case KeyEvent.VK_ESCAPE:
      if(C.confirm("欠席扱いになるかもしれませんが、" + C.cr + "本当に終了しますか？")){
        C.d("BaseCanvas: KeyReleasedNormal: VK_ESCAPE: end");
        snapshot.exit();
      } break;
    }
  }

  void keyReleasedUserId(int key, int modifiers){}

  void keyReleased(int key, int modifiers){
    switch(menuMode){
    case MENU_NORMAL:
      keyReleasedNormal(key, modifiers);
      break;
    case MENU_USERID: 
      keyReleasedUserId(key, modifiers);
      break;
    }
  }

  public void keyReleased(KeyEvent e){
    keyReleased(e.getKeyCode(), e.getModifiersEx());
  }
  public void keyTyped(KeyEvent e){}
  public void keyPressed(KeyEvent e){}

  public void mousePressedUserId(){}

  public void mousePressed(MouseEvent e){
    switch(e.getButton()){
    case MouseEvent.BUTTON1:
      mouseX = e.getX();
      mouseY = e.getY();
      mousePressedUserId();
      break;
    }
  }
  public void mouseClicked(MouseEvent e){}
  public void mouseReleased(MouseEvent e){}
  public void mouseEntered(MouseEvent e){}
  public void mouseExited(MouseEvent e){}

  public void mouseDragged(MouseEvent e){
    int x = e.getX(), y = e.getY();
    drawX += (x - mouseX); mouseX = x;
    drawY += (y - mouseY); mouseY = y;
    repaint();
  }
  public void mouseMoved(MouseEvent e){}

}
