package jm.instruments;

import jm.audio.io.SampleOut;
import jm.audio.synth.Envelope;
import jm.audio.synth.Oscillator;
import jm.audio.synth.StereoPan;
import jm.audio.synth.Volume;

/**
 * A basic additive synthesis jMusic instrument implementation
 * which implements envelope and volume control
 *
 * @author Andrew Sorensen
 */

public final class SineInst extends jm.audio.Instrument {
  //----------------------------------------------
  // Attributes
  //----------------------------------------------

  /**
   * The number of channels
   */
  private int channels;
  /**
   * the sample rate passed to the instrument
   */
  private int sampleRate;
  /**
   * The pitch in hertz of a fixed frequency oscillator
   */
  private float frequency = -1.0f;
  /**
   * The bereak point values fo the amplitude envelope
   */
  private double[] envPoints;
  private SampleOut sout;

  //----------------------------------------------
  // Constructor
  //----------------------------------------------

  /**
   * Basic default constructor that creates
   * a stereo signal at 44100 hz.
   */
  public SineInst() {
    this(44100, 2);
  }

  /**
   * Basic default constructor to set an initial
   * sampling rate.
   */
  public SineInst(int sampleRate) {
    this(sampleRate, 2);
  }

  /**
   * A constructor to set an initial
   * sampling rate and number of channels.
   *
   * @param channels the number of channels in the sound 1 = mono, 2 = stereo, etc
   */
  public SineInst(int sampleRate, int channels) {
    this(sampleRate, channels, new double[]{
        0.0, 0.0,
        0.15, 1.0,
        0.3, 0.5,
        1.0, 0.0});
  }

  /**
   * A constructor to set sampleRate, channels, and
   * the pitch of the sine wave in hertz.
   *
   * @param channels the number of channels in the sound 1 = mono, 2 = stereo, etc
   * @param points The bereak point values fo the amplitude envelope
   */
  public SineInst(int sampleRate, int channels, double[] points) {
    this(sampleRate, channels, points, -1.0f);
  }

  /**
   * A constructor to set sampleRate, channels, and
   * the pitch of the sine wave in hertz.
   *
   * @param channels the number of channels in the sound 1 = mono, 2 = stereo, etc
   * @param points The bereak point values fo the amplitude envelope
   * @param frequency a positive float value specifying a fixed pitch
   */
  public SineInst(int sampleRate, int channels, double[] points, float frequency) {
    this.sampleRate = sampleRate;
    this.channels = channels;
    this.frequency = frequency;
    this.envPoints = points;
  }

  //----------------------------------------------
  // Methods
  //----------------------------------------------

  /**
   * Specify a fixed frequency oscillator.
   *
   * @param The frequency in hertz
   */
  public void setFrequency(float freq) {
    this.frequency = freq;
  }

  /**
   * Specify a particular envelope.
   *
   * @param newValues The array of time percent, amplitude pairs of double values.
   */
  public void setEnvPoints(double[] newValues) {
    this.envPoints = newValues;
  }

  /**
   * Initialisation method used to build the objects that
   * this instrument will use.
   */
  public void createChain() {
    Oscillator osc = new Oscillator(this, Oscillator.SINE_WAVE, this.sampleRate, this.channels);
    // set a fixed, rather the note determined, frequency
    if (frequency != -1.0f) {
      osc.setChoice(0);
      osc.setFrq(frequency);
    }
    Envelope env = new Envelope(osc, envPoints);
    Volume vol = new Volume(env);
    StereoPan span = new StereoPan(vol);
    if (output == RENDER) {
      sout = new SampleOut(span);
    }
  }
}

