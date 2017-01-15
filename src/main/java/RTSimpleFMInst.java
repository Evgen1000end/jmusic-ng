import jm.audio.AOException;
import jm.audio.AudioObject;
import jm.audio.io.SampleOut;
import jm.audio.synth.Add;
import jm.audio.synth.EnvPoint;
import jm.audio.synth.Envelope;
import jm.audio.synth.Oscillator;
import jm.audio.synth.StereoPan;
import jm.audio.synth.Value;
import jm.audio.synth.Volume;
import jm.audio.synth.WaveTable;

/**
 * A basic FM synthesis instrument implementation
 *
 * @author Andrew Sorensen
 */

public final class RTSimpleFMInst extends jm.audio.Instrument {
  //----------------------------------------------
  // Attributes
  //----------------------------------------------
  /**
   * This is where it all gets added together
   */
  public SampleOut sout;
  /**
   * The wavetables to use for each frequency specified
   */
  private WaveTable wt1;
  /**
   * The wavetables to use for each frequency specified
   */
  private WaveTable wt2;
  /**
   * The envelope to apply to each wavetable's output
   */
  private Envelope env;
  /**
   * Add the streams together
   */
  private Add add;
  /**
   * The volume to apply to each envelopes output
   */
  private Volume vol;
  /**
   * Pan
   */
  private float pan;
  /**
   * The points to use in the construction of Envelopes
   */
  private EnvPoint[] pointArray = new EnvPoint[10];
  /**
   * The number of channels
   */
  private int channels;
  /**
   * The deviation index
   */
  private int dIndex;
  /**
   * The frequency ratio from the carrier frequency (the notes pitch)
   */
  private float frqRatio;

  private int sampleRate;

  //----------------------------------------------
  // Constructor
  //----------------------------------------------

  /**
   * Basic default constructor to set an initial
   * sampling rate.
   */
  public RTSimpleFMInst(int sampleRate, int dIndex, double ratio) {
    this(sampleRate, 1, dIndex, ratio);
  }

  /**
   * A constructor to set an initial
   * sampling rate and number of channels.
   */
  public RTSimpleFMInst(int sampleRate, int channels, int dIndex, double ratio) {
    this.sampleRate = sampleRate;
    EnvPoint[] tempArray = {
        new EnvPoint((float) 0.0, (float) 0.0),
        new EnvPoint((float) 0.1, (float) 1.0),
        new EnvPoint((float) 0.15, (float) 0.4),
        new EnvPoint((float) 0.9, (float) 0.3),
        new EnvPoint((float) 1.0, (float) 0.0)
    };
    pointArray = tempArray;
    this.channels = channels;
    this.dIndex = dIndex;
    this.frqRatio = (float) ratio;
  }

  //----------------------------------------------
  // Methods
  //----------------------------------------------

  public void setController(double[] index) {
    if (this.wt1 == null) {
      return;
    }
    this.wt1.setAmp((float) index[0]);
  }

  /**
   * Initialisation method used to build the objects that
   * this instrument will use
   */
  public void createChain() throws AOException {
    Oscillator osc = new Oscillator(this, Oscillator.SINE_WAVE, this.sampleRate, this.channels);
    osc.setAmp((float) this.dIndex);
    osc.setFrqRatio(this.frqRatio);
    Value cf = new Value(this, this.sampleRate, this.channels, 1);
    AudioObject[] grp1 = {cf, wt1};
    Add add = new Add(grp1);
    Oscillator osc2 = new Oscillator(add, Oscillator.SINE_WAVE, 1);
    Envelope env = new Envelope(osc2, pointArray);
    Volume vol = new Volume(env, (float) 1.0);
/*

		Splitter split = new Splitter(vol);
		Comb comb1 = new Comb(split,30,0.5);
		Comb comb2 = new Comb(split,40,0.5);
		Comb comb3 = new Comb(split,157,0.5);
		Comb comb4 = new Comb(split,90,0.5);
		AudioObject[] grp2 = {comb1,comb2,comb3,comb4};
		Add add2 = new Add(grp2);
		AllPass ap1 = new AllPass(add2,200,0.5);
		AllPass ap2 = new AllPass(ap1,50,0.5);
*/

    StereoPan span = new StereoPan(vol);
    //SampleOut sout = new SampleOut(span, "jmusic.tmp");
  }
}

