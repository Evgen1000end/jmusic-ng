package jm.instruments;//==========================================================
// File:                 jm.instruments.NoiseInst.java
// Package:              inst
// Function:             basic implementation of a white noise generator
// Author:               Andrew Brown
// Environment:          JDK1.1
//==========================================================

import jm.audio.Instrument;
import jm.audio.io.SampleOut;
import jm.audio.synth.EnvPoint;
import jm.audio.synth.Envelope;
import jm.audio.synth.Noise;
import jm.audio.synth.StereoPan;
import jm.audio.synth.Volume;

/**
 * A basic white noise synthesis instrument implementation
 * which implements envelope, pan, and volume control
 *
 * @author Andrew Brown
 */

public final class NoiseInst extends Instrument {
  //----------------------------------------------
  // Attributes
  //----------------------------------------------

  /**
   * The points to use in the construction of Envelopes
   */
  private EnvPoint[] pointArray = new EnvPoint[10];
  private int channels;
  private int sampleRate;
  private int noiseType;

  //----------------------------------------------
  // Constructor
  //----------------------------------------------

  /**
   * Basic default constructor to set an initial
   * sampling rate.
   */
  public NoiseInst(int sampleRate) {
    this(sampleRate, 1);
  }

  /**
   * A second constructor to set an initial
   * sampling rate and number of channels.
   *
   * @param channels (i.e., 1 = mono, 2 = stereo)
   */
  public NoiseInst(int sampleRate, int channels) {
    this(sampleRate, channels, Noise.WHITE_NOISE);
  }

  /**
   * A constructor to set an initial
   * sampling rate, number of channels and type of noise.
   *
   * @param channels (i.e., 1 = mono, 2 = stereo)
   * @ param noiseType 0 = White noise etc.
   */
  public NoiseInst(int sampleRate, int channels, int noiseType) {
    this.sampleRate = sampleRate;
    this.channels = channels;
    this.noiseType = noiseType;
  }

  //----------------------------------------------
  // Methods
  //----------------------------------------------

  /**
   * Initialisation method used to build a chain of the objects that
   * this instrument will use.
   */
  public void createChain() {
    Noise noise = new Noise(this, noiseType, this.sampleRate, channels);
    Envelope env = new Envelope(noise,
      new double[]{0.0, 0.0, 0.05, 1.0, 0.95, 1.0, 1.0, 0.0});
    Volume vol = new Volume(env);
    StereoPan span = new StereoPan(vol);
    SampleOut sout = new SampleOut(span);
  }
}

