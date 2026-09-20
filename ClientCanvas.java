import java.io.File;
import java.io.FileWriter;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ClientCanvas extends BaseCanvas {

  private String helpStringNormal =
    "N ... No ÀÈ”Ô†‚ğÄ“x“ü—Í‚·‚é\n" +
    "I ... Info î•ñ‚ğ“ü—Í‚·‚é\n";

  public ClientCanvas(Snapshot _snapshot) throws Exception{
    super(_snapshot);
  }

  void viewHelp(){
    JOptionPane.showMessageDialog(null, helpString + helpStringNormal, "ƒwƒ‹ƒv", JOptionPane.PLAIN_MESSAGE);
  }

  public void keyReleasedNormal(int key, int modifiers){
    switch(key){
    case KeyEvent.VK_I:
      try{
        snapshot.client.info.select();
        snapshot.client.fileCom.writeInfo(snapshot.client.info.get());
      }catch(Exception e){
        C.error("ClientCanvas: keyReleasedNormal: " + e.toString());
      }
      break;
    case KeyEvent.VK_N:
      try{
        snapshot.client.seat.select();
        snapshot.client.fileCom.writeSeat(snapshot.client.seat.get());
      }catch(Exception e){
        C.error("ClientCanvas: keyReleasedNormal: " + e.toString());
      }
      break;
    default:
      super.keyReleasedNormal(key, modifiers);
      break;
    }
  }

}
