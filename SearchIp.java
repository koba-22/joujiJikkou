import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.net.*;

class SearchIp extends Thread {

  final int BUF_SIZE = 16;
  final String ASK = "?";
  boolean serverFlag;
  int port;
  final int HOST = 0, BROADCAST = 1;
  String[] myIp = null; // host, broadcast
  String answeredServerIp = "";
  static final String cr = "\r\n", cr2 = cr + cr;

  public SearchIp(boolean _serverFlag, int _port){
    serverFlag = _serverFlag;
    port = _port;
    myIp = getIp();
  }

  public String getServerIp(){
    return answeredServerIp;
  }

  public void run(){
    if(serverFlag)
      server();
    else
      client();
  }

  void server(){
    try{
      DatagramSocket rcvDs = new DatagramSocket(port);    
      DatagramSocket sndDs = new DatagramSocket();    
      byte[] sndB = myIp[HOST].getBytes();
      byte[] rcvB = new byte[BUF_SIZE];
      DatagramPacket rcvDp = new DatagramPacket(rcvB, rcvB.length);
      DatagramPacket sndDp = new DatagramPacket(sndB, sndB.length, new InetSocketAddress(myIp[BROADCAST], port));
      while(true){
        rcvDs.receive(rcvDp);
        String s = new String(rcvDp.getData(), rcvDp.getLength());
        if(s.equals(ASK))
          sndDs.send(sndDp);
      }
    }catch(Exception e){
      JOptionPane.showMessageDialog(null, "エラー: SearchIp: server:" + cr2 + e.toString());
    }
  }

  void client(){
    try{
      DatagramSocket rcvDs = new DatagramSocket(port);    
      DatagramSocket sndDs = new DatagramSocket();    
      byte[] rcvB = new byte[BUF_SIZE];
      byte[] sndB = ASK.getBytes();
      DatagramPacket rcvDp = new DatagramPacket(rcvB, rcvB.length);
      DatagramPacket sndDp = new DatagramPacket(sndB, sndB.length, new InetSocketAddress(myIp[BROADCAST], port));
      sndDs.send(sndDp);
      sndDs.send(sndDp);
      while(true){
        rcvDs.receive(rcvDp);
        String s = new String(rcvDp.getData(), rcvDp.getLength());
        if(s.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")){
          answeredServerIp = s;
          break;
        }
      }
    }catch(Exception e){
      JOptionPane.showMessageDialog(null, "エラー: SearchIp: client:" + cr2 + e.toString());
    }

  }

  String[] getIp() {
    String[] ip = { "", "" }; // host, broadcast
    try {
      Enumeration en = NetworkInterface.getNetworkInterfaces();
      while(en.hasMoreElements()){
        NetworkInterface ni = (NetworkInterface)en.nextElement();
        if(ni.getHardwareAddress() == null)
          continue;
        InterfaceAddress ia = ni.getInterfaceAddresses().get(0);
        ip[HOST] = ia.getAddress().getHostAddress();
        ip[BROADCAST] = ia.getBroadcast().getHostAddress();
        break;
      }
    }catch(Exception e){
      JOptionPane.showMessageDialog(null, "エラー: SearchIp: getIp:" + cr2 + e.toString());
    }
    return ip;
  }

  /*
  String getBroadcastIp(String ip){
    String[] a = ip.split("\\.");
    if(a[0].equals("10"))
      return a[0] + ".255.255.255";
    if(a[0].equals("172"))
      return a[0] + "." + a[1] + ".255.255";
    if(a[0].equals("168"))
      return a[0] + "." + a[1] + "." + a[2] + ".255";
    return "";
  }

  String getMyIp(){
    try {
      return InetAddress.getLocalHost().getHostAddress();
    }catch(Exception e){
      JOptionPane.showMessageDialog(null, "エラー: SearchIp: getMyIp:" + cr2 + e.toString());
    }
    return "";
  }
  */


}