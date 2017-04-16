package jm.instruments;

import jm.audio.AOException;
import jm.audio.AudioObject;
import jm.audio.io.SampleOut;
import jm.audio.synth.Add;
import jm.audio.synth.Envelope;
import jm.audio.synth.Oscillator;
import jm.audio.synth.Value;
import jm.audio.synth.Volume;

/**
 * A basic vibrato from FM synthesis
 *
 * @author Andrew Brown
 */

public final class VibratoInst extends jm.audio.Instrument {
  //----------------------------------------------
  // Attributes
  //---------------------------------------------
  /**
   * The number of channels
   */
  private int channels;
  /**
   * The deviation index
   */
  private int modIndex;
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
  public VibratoInst(int sampleRate) {
    this(sampleRate, 1);
  }

  /**
   * A constructor to set an initial
   * sampling rate and number of channels.
   *
   * @param channels The number of channels
   */
  public VibratoInst(int sampleRate, int channels) {
    this.sampleRate = sampleRate;
    this.channels = channels;
  }

  //----------------------------------------------
  // Methods
  //----------------------------------------------

  /**
   * Initialisation method used to build the objects that
   * this instrument will use
   */
  public void createChain() throws AOException {
    // modulator
    Oscillator modulator = new Oscillator(this, Oscillator.SINE_WAVE,
        sampleRate, channels,
        Oscillator.FREQUENCY, 5.0);
    modulator.setAmp((float) 8.0);
    // constant
    Value constFreq = new Value(this, this.sampleRate,
        this.channels, Value.NOTE_PITCH);
    Add add = new Add(new AudioObject[]{constFreq, modulator});
    // carrier
    Oscillator carrier = new Oscillator(add,
        Oscillator.SINE_WAVE, Oscillator.FREQUENCY);
    Envelope env2 = new Envelope(carrier,
        new double[]{0.0, 0.0, 0.1, 1.0, 1.0, 0.0});
    Volume amp = new Volume(env2);
    SampleOut sout = new SampleOut(amp);
  }
}

