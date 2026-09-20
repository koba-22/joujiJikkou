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

public class SeatDialog extends JDialog implements ActionListener {

  SeatPanel seat;
  
  public SeatDialog(Frame _owner){
    super(_owner, "席番号選択", true);
    setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    setLayout(new BorderLayout());
    add(new JLabel("着席した席番号を選択して下さい。"), BorderLayout.NORTH);
    seat = new SeatPanel();
    add(seat, BorderLayout.CENTER);
    JButton ok = new JButton("OK");
    add(ok, BorderLayout.SOUTH);
    ok.addActionListener(this);
    setSize(800, 600);
  }

  public String get(){
    ButtonModel bm = seat.group.getSelection();
    if(bm == null)
      return "ng";
    return bm.getActionCommand();
  }

  public void select(){
    seat.group.clearSelection();
    setVisible(true);
  }

  public void actionPerformed(ActionEvent e){
    if(seat.group.getSelection() == null){
      C.info("席番号を選択後にOKボタンをクリックして下さい。");
      return;
    }
    setVisible(false);
  }
  
  class SeatPanel extends JPanel{
    ButtonGroup group;

    SeatPanel(){
      group = new ButtonGroup();
      setLayout(new GridLayout(8, 10));
      for(int i = 1; i <= 80; i++){
        String s = String.format("%02d", i);
        JRadioButton rb = new JRadioButton(s);
        rb.setActionCommand(s);
        add(rb);
        group.add(rb);
      }
    }
  }

}
