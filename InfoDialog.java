import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class InfoDialog extends JDialog implements ActionListener {

  InfoPanel info;
  String[][] infoSa;
  
  public InfoDialog(Frame _owner, String[][] _infoSa){
    super(_owner, "情報選択", true);
    infoSa = _infoSa;
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    setLayout(new BorderLayout());
    add(new JLabel("間違えた場合は「キャンセル」を選択後にOKボタンをクリックして下さい。"), BorderLayout.NORTH);
    info = new InfoPanel();
    add(info, BorderLayout.CENTER);
    JButton ok = new JButton("OK");
    add(ok, BorderLayout.SOUTH);
    ok.addActionListener(this);
    setSize(600, 300);
  }

  public String get(){
    ButtonModel bm = info.group.getSelection();
    if(bm == null)
      return "ng";
    return bm.getActionCommand();
  }

  public void select(){
    info.group.clearSelection();
    setVisible(true);
  }

  public void actionPerformed(ActionEvent e){
    if(info.group.getSelection() == null){
      C.info("選択後にOKボタンをクリックして下さい。");
      return;
    }
    setVisible(false);
  }
  
  class InfoPanel extends JPanel{
    ButtonGroup group;

    InfoPanel(){
      group = new ButtonGroup();
      setLayout(new GridLayout(infoSa.length, 1));
      for(int i = 0; i < infoSa.length; i++){
        JRadioButton rb = new JRadioButton(infoSa[i][1]);
        rb.setActionCommand(infoSa[i][0]);
        add(rb);
        group.add(rb);
      }
    }
  }

}
