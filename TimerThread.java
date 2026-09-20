import java.util.Calendar;

public class TimerThread extends Thread {

  private ServerCanvas serverCanvas;
  private int timerPeriod;
  private long startTime;
  boolean alive = true;

  public TimerThread(ServerCanvas _serverCanvas, int _timerPeriod){
    serverCanvas = _serverCanvas;
    timerPeriod = _timerPeriod;
  }

  public void run(){
    startTime = Calendar.getInstance().getTimeInMillis() /1000;
    while(alive){
      long now = Calendar.getInstance().getTimeInMillis() /1000;
      serverCanvas.repaintFromTimer(((int)(now - startTime)) % timerPeriod);
      C.sleep(500);
    }
  }

  public void end(){
    alive = false;
  }

}
