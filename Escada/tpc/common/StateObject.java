package Escada.tpc.common;

/** It is an abstract class that defines the states through which 
* our emulator will pass during each interaction, or in other words, 
* during each transaction's execution. 
* 
**/
public abstract class StateObject {
  protected StateInfo stInfo = new StateInfo();
  protected OutInfo outInfo = new OutInfo();
  protected InInfo inInfo = new InInfo();
  protected int prob = 0;
  protected long thinktime = 0;
  protected long keyingtime = 0;

  /**
  * It is the main point in order to processes the simulation. Generally,
  * it would be used to call prepareProcess, initProcess, requestProcess.
  *
  * @param Emulation emulator requesting the processing
  * @param String host to which the emulator is attached to
  **/
  public abstract Object requestProcess(Emulation em,String hid);

  /**
  * It executes something after the main processing, for example
  * it could be used to unset some variables.
  *
  * @param Emulation emulator requesting the processing
  * @param String host to which the emulator is attached to
  **/
  public abstract void postProcess(Emulation em,String hid);

  /**
  * It is used in order to execute the process.
  *
  * @param Emulation emulator requesting the processing
  * @param String host to which the emulator is attached to
  **/
  public abstract void initProcess(Emulation em,String hid);

  /**
  * It executes something before the main processing, for example
  * it could be used to set up some variables.
  * 
  * @param Emulation emulator requesting the processing
  * @param String host to which the emulator is attached to
  **/
  public abstract void prepareProcess(Emulation em,String hid);

  /**
  * It defines if this state can leads the processing to the intial 
  * state, called "home". In fact, this feature is not always
  * implemented.
  *
  * @return true indicates that this step can lead to the home step, otherwise not.
  **/
  public boolean toHome() {
    return (true);
  }
                                                                                
  /**
  * It defines the probability of occurrence of this state.
  **/
  public abstract void setProb();

  /**
  * It defines the probability of occurrence of this state.
  *
  * @param int the probability of occurrence of this state
  * @see getProb
  **/
  public void setProb(int prob) {
    this.prob = prob;
  }

 /**
  * It returns the probability of occurrence of this state. 
  *
  * @return the probability of occurrence of this state
  * @see setProb
  **/
  public int getProb() {
    return (prob);
  }

 /**
  * It defines a base value used to calculated the emulated thinktime, meaning 
  * the time used by the operator to take a decision.
  **/
  public abstract void setThinkTime();

 /**
  * It defines a base value used to calculated the emulated thinktime, meaning
  * the time used by the operator to take a decision.
  *
  * @param long the thinktime
  * @see getThinkTime
  **/
  public void setThinkTime(long thinktime) {
    this.thinktime = thinktime;
  }
                                                                                
  /**
  * It returns the base value used to calculate the emulated thinktime, meaning
  * the time used by the operator to take a decision.
  *
  * @return the thinktime
  * @see setThinkTime
  **/
  public long getThinkTime() {
     return(thinktime);
  }

 /**
  * It defines a base value used to calculated the emulated thinktime, meaning
  * the time used by the operator to take a decision.
  **/
  public abstract void setKeyingTime();

  /**
  * It defines a base value used to calculate the emulated keyingtime, meaning
  * the time used by the operator to fill a form.
  *
  * @param long the keyingtime
  * @see getKeyingTime
  **/
  public void setKeyingTime(long keyingtime) {
    this.keyingtime = keyingtime;
  }

  /**
  * It returns the base value used to calculate the emulated keyingtime, meaning
  * the time used by the operator to fill a form.
  *
  * @return the keyingtime
  * @see setKeyingTime
  **/
  public long getKeyingTime() {
     return(keyingtime);
  }
}
// arch-tag: 4148e402-606b-4d4d-a834-f87419ad1648
