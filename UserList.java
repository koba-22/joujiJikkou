import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;

class UserList {

  private HashMap<String, String> userIdMap;

  public UserList(String userListFilePath) throws Exception{
    try{
      userIdMap = new HashMap<String, String>();
      makeUserIdMap(userListFilePath);
    }catch(Exception e){
      C.throwException("UserList: UserList:", e);
    }
  }

  public String get(String key){
    String s = userIdMap.get(key);
    if(s == null) s = "";
    return s;
  }
  
  void makeUserIdMap(String path) throws Exception{
    try{
      FileReader fr = new FileReader(path);
      BufferedReader br = new BufferedReader(fr);
      String line;
      Pattern p = Pattern.compile("^([^,]+),(.+)$");
      while((line = br.readLine()) != null){
        Matcher m = p.matcher(line);
        if(m.find())
          userIdMap.put(C.getStudentNo(m.group(1)), m.group(2).replaceAll("[ Å@]", ""));
      }
      br.close();
      fr.close();
    }catch(Exception e){
      C.throwException("UserList: makeUserIdMap:", e);
    }
  }

}
