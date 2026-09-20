import java.util.ArrayList;
import java.util.Arrays;

public class ClientList {

  static final String FIELD_SEP = ",", INFO_SEP = " ", TIMES_SEP = " ", IPADDRESS_SEP = " ";
  static final int SORT_MODE_USERID = 0, SORT_MODE_SEATNO = 1, SORT_MODE_SAVE = 2;
  class Client {
    public String userId = "";
    public String info = "";
    public String times = "";
    public String ipAddress = "";
  }
  private ArrayList<Client> clientList;
  private Snapshot snapshot;
  private UserList userList;
  private int nextIndex = 0;

  public ClientList(Snapshot _snapshot) throws Exception{
    try{
      snapshot = _snapshot;
      clientList = new ArrayList<Client>();
      userList = new UserList(snapshot.ini.getUserListFilePath());
    }catch(Exception e){
      C.throwException("ClientList: ClientList:", e);
    }
  }

  public void resetNextIndex(){ nextIndex = 0; }

  public String getNextUserId(){
    if(nextIndex >= clientList.size()){
      nextIndex = 0;
      return "";
    }
    return clientList.get(nextIndex++).userId;
  }

  public String[] getUserList(int sortMode){
    switch(sortMode){
    case SORT_MODE_USERID: return getUserListSortUserId();
    case SORT_MODE_SEATNO: return getUserListSortSeatNo();
    case SORT_MODE_SAVE: return getUserListSortSave();
    }
    return null;
  }

  public String[] getUserListSortSave(){
    int n = clientList.size();
    if(n == 0)
      return null;
    String[] sa = new String[n];
    for(int i = 0; i < n; i++){
      Client cl = clientList.get(i);
      String s = "";
      s += cl.userId + FIELD_SEP;
      s += userList.get(cl.userId) + FIELD_SEP;
      s += cl.times + FIELD_SEP;
      s += cl.info + FIELD_SEP;
      s += cl.ipAddress;
      sa[i] = s;
    }
    Arrays.sort(sa);
    return sa;
  }

  public String[] getUserListSortSeatNo(){
    int n = clientList.size();
    if(n == 0)
      return null;
    String[] sa = new String[n];
    for(int i = 0; i < n; i++){
      Client cl = clientList.get(i);
      String s = "";
      if(cl.info != ""){
        String[] sa2 = cl.info.split(INFO_SEP);
        String s2 = "00";
        for(int i2 = sa2.length -1; i2 >= 0; i2--){
          int ch = sa2[i2].charAt(0);
          if(ch >= '0' && ch <= '9'){
            s2 = sa2[i2];
            break;
          }
        }
        s += s2; // sa2[sa2.length -1];
      }
      s += FIELD_SEP;
      s += cl.userId + FIELD_SEP;
      s += userList.get(cl.userId) + FIELD_SEP;
      s += cl.times + FIELD_SEP;
      s += cl.info + FIELD_SEP;
      s += cl.ipAddress;
      sa[i] = s;
    }
    Arrays.sort(sa);
    return sa;
  }

  public String[] getUserListSortUserId(){
    int n = clientList.size();
    if(n == 0)
      return null;
    String[] sa = new String[n];
    for(int i = 0; i < n; i++){
      Client cl = clientList.get(i);
      String s = "";
      s += cl.userId + FIELD_SEP;
      if(cl.info != ""){
        String[] sa2 = cl.info.split(INFO_SEP);
        String s2 = "00";
        for(int i2 = sa2.length -1; i2 >= 0; i2--){
          int ch = sa2[i2].charAt(0);
          if(ch >= '0' && ch <= '9'){
            s2 = sa2[i2];
            break;
          }
        }
        s += s2; // sa2[sa2.length -1];
      }
      s += FIELD_SEP;
      s += userList.get(cl.userId) + FIELD_SEP;
      s += cl.times + FIELD_SEP;
      s += cl.info + FIELD_SEP;
      s += cl.ipAddress;
      sa[i] = s;
    }
    Arrays.sort(sa);
    return sa;
  }

  public void addClientAndMessage(String userId, String hcm, String message, String ipAddress){
    Client cl = null;
    int index = getIndexByUserId(userId);
    if(index == -1){
      cl = new Client();
      cl.userId = userId;
      cl.ipAddress = ipAddress;
      clientList.add(cl);
      C.d("ClientList: addClientAndMessage: 1: "  + " " + userId + " " + hcm + " " + message + " " + ipAddress);
    }else{
      cl = clientList.get(index);
      if(cl.ipAddress.indexOf(ipAddress) == -1)
        cl.ipAddress += IPADDRESS_SEP + ipAddress;
      C.d("ClientList: addClientAndMessage: 2: "  + " " + userId + " " + hcm + " " + message + " " + ipAddress);
    }
    if(cl.times != "")
      cl.times += TIMES_SEP;
    switch(message.charAt(0)){
    case FileCom.MESSAGE_TYPE_SEAT:
    case FileCom.MESSAGE_TYPE_INFO:
      cl.info += message.substring(1) + INFO_SEP;
      cl.times += hcm + message; // .substring(0, 1);
      break;
    case FileCom.MESSAGE_TYPE_ACK:
    case FileCom.MESSAGE_TYPE_RECEIVE:
    case FileCom.MESSAGE_TYPE_DOWN:
      cl.times += hcm + message; // .substring(0, 1);
      break;
    default:
      C.e("ClientList: addClientAndMessage: " + message);
      break;
    }
  }

  int getIndexByUserId(String userId){
    int i = -1, n = clientList.size();
    for(i = 0; i < n; i++){
      Client cl = clientList.get(i);
      if(cl.userId.equals(userId))
        break;
    }
    if(i >= 0 && i < n)
      return i;
    return -1;
  }

}
