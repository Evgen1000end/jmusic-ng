import jm.audio.AOException;
import jm.audio.synth.Oscillator;

public final class SimplestInst extends jm.audio.Instrument {

  /**
   * The number of channels
   */
  private int channels;
  private int sampleRate;

  public SimplestInst() {
    this.sampleRate = 44100;
    this.channels = 1;
  }

  public void createChain() throws AOException {
    Oscillator modulator = new Oscillator(this, Oscillator.TRIANGLE_WAVE,
        this.sampleRate, this.channels);
    //SampleOut so = new SampleOut(modulator,"jmusic.tmp");
  }
}



