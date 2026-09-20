import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics2D;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Snapshot extends JFrame implements KeyListener {
  public static void main(String[] args){ new Snapshot(); }

  final String INI_FILE_NAME = "íŽžŽÀs.ini";
  final String EXEC_MODE_SERVER = "server";
  static final String IMAGE_TYPE = "png";
  static final String PATH_FORMAT = "%s\\%s." + IMAGE_TYPE;
  private boolean serverFlag = false;
  String userId = null;
  Ini ini = null;
  Toolkit toolkit = null;
  Robot robot = null;
  Rectangle windowSize = null;
  Server server = null;
  Client client = null;
  BufferedImage captureImage = null;
  Point mousePoint = null;
  String execDir = "";

  public Snapshot(){
    try{
      execDir = C.getExecDir();
      userId = C.getStudentNo(System.getenv("USERNAME"));
      ini = new Ini(INI_FILE_NAME);
      C.openLogFile(ini.getLogFilePath());
      C.log("start: " + userId + " " + execDir);
      if(ini.getExecMode().equals(EXEC_MODE_SERVER))
        serverFlag = true;
      toolkit = Toolkit.getDefaultToolkit();
      robot = new Robot();
      windowSize = new Rectangle(toolkit.getScreenSize());
      setTitle("íŽžŽÀs");
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      if(serverFlag){
        int w = 300, h = 100, x = (int)windowSize.getWidth() - w, y = (int)windowSize.getHeight() - h -50;
        setBounds(x, y, w, h);
        server = new Server(this);
        add(server.getServerCanvas());
      }else{
        setBounds(0, 0, 900, 700);
        client = new Client(this);
        add(client.getClientCanvas());
      }
      addKeyListener(this);
      setVisible(true);
    }catch(Exception e){
      C.error("Snapshot: Snapshot:" + C.cr + e.toString());
      C.exit();
    }
  }

  public void exit(){
    if(!serverFlag)
      client.fileCom.writeDown();
    C.exit();
  }

  public String getIp(){
    try {
      Enumeration en = NetworkInterface.getNetworkInterfaces();
      while(en.hasMoreElements()){
        NetworkInterface ni = (NetworkInterface)en.nextElement();
        if(ni.getHardwareAddress() == null)
          continue;
        Enumeration ea = ni.getInetAddresses();
        while(ea.hasMoreElements()){
          InetAddress ia = (InetAddress)ea.nextElement();
          String ip = ia.getHostAddress();
          if((ip.indexOf(".") >= 0) && !ip.equals("127.0.0.1"))
            return ip;
        }
      }
    }catch(Exception e){
      C.error("Snapshot: getIp: " + e.toString());
    }
    return "";
  }

  public void getMousePointForCapture(){
    try{
      mousePoint = MouseInfo.getPointerInfo().getLocation();
    }catch(Exception e){
      C.error("Snapshot: getMousePointForCapture: " + e.toString());
    }
  }

  void writeCursor() throws Exception{
    if(mousePoint == null || captureImage == null)
      return;
    try{
      Graphics2D g = captureImage.createGraphics();
      g.setPaint(new Color(0.0f, 1.0f, 0.0f, 0.5f));
      int d = 100, x = (int)(mousePoint.getX()), y = (int)(mousePoint.getY());
      g.fillOval(x - d /2, y - d /2, d, d);
    }catch(Exception e){
      C.throwException("Snapshot: writeCursor:", e);
    }
    mousePoint = null;
  }

  public void capture() throws Exception{
    try{
      captureImage = robot.createScreenCapture(windowSize);
      writeCursor();
    }catch(Exception e){
      C.throwException("Snapshot: capture:", e);
    }
  }

  public void keyReleased(KeyEvent e){
    int key = e.getKeyCode(), modifiers = e.getModifiersEx();
    if(serverFlag)      server.getServerCanvas().keyReleased(key, modifiers);
    else
      client.getClientCanvas().keyReleased(key, modifiers);
  }
  public void keyTyped(KeyEvent e){}
  public void keyPressed(KeyEvent e){}

}
