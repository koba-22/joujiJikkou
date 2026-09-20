setlocal
set CLASSPATH=.
set c1=Snapshot.class Ini.class C.class Log.class ClientList.class ClientList$Client.class UserList.class
set c2=Server.class ServerCanvas.class TimerThread.class
set c3=Client.class ClientCanvas.class SeatDialog.class SeatDialog$SeatPanel.class
set c4=BaseCanvas.class FileCom.class FileComImage.class
set c5=InfoDialog.class InfoDialog$InfoPanel.class 
jar cfm èÌéûé¿çs.jar snapshot.mf %c1% %c2% %c3% %c4% %c5%
endlocal
