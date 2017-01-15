import jm.audio.io.PrintOut;
import jm.audio.synth.Oscillator;


/**
 * A basic synthesis instrument that prints to the screen.
 *
 * @author Andrew Brown
 */


public final class PrintSineInst extends jm.audio.Instrument {

    //----------------------------------------------

    // Attributes

    //----------------------------------------------

    /**
     * the sample rate passed to the instrument
     */

    private int sampleRate;

    /**
     * the sample rate passed to the instrument
     */

    private int channels;


    //----------------------------------------------

    // Constructor

    //----------------------------------------------

    /**
     * Basic default constructor to set an initial
     * <p/>
     * sampling rate.
     *
     * @param sampleRate
     */

    public PrintSineInst(int sampleRate) {

        this.sampleRate = sampleRate;

        this.channels = 1;

    }


    //----------------------------------------------

    // Methods

    //----------------------------------------------


    /**
     * Initialisation method used to build the objects that
     * <p/>
     * this instrument will use.
     * <p/>
     * Declares the primary audio object array and the
     * <p/>
     * audio object(s) in that array. (One array element per channel)
     */

    public void createChain() {

        Oscillator wt = new Oscillator(this, Oscillator.SINE_WAVE,
                this.sampleRate, 1);

        PrintOut pout = new PrintOut(wt);

    }

}



