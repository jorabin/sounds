/*
 * Copyright (c) 2011 - 2021 Jo Rabin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.linguafranca.sound.tone;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.*;

/**
 * An immutable representation of a frequency and volume
 */
public class Tone {

    /**
     * A list of Tones, obviously!
     */
    public static class List extends ArrayList<Tone> {}

    /**
     * The lowest amplitude (off) is 0
     */
    public static final double LOW_AMPLITUDE = 0.0;
    /**
     * The highest amplitude is arbitrarily set to 1
     */
    public static final double HIGH_AMPLITUDE = 1.0;

    /**
     * The lowest deciBel
     */
    public static final double MIN_DB = Double.NEGATIVE_INFINITY;
    /**
     * The highest dB (that we are interested in)
     */
    public static final double MAX_DB = 0.0;

    /**
     * reference frequency is A above middle C
     */
    public static int referenceFrequency = 440;

    /**
     * Factory method for creating a {@link Tone}
     * <p>
     * The values passed are checked for validity (the amplitude must be in the range {@link #LOW_AMPLITUDE} to
     * {@link #HIGH_AMPLITUDE}
     *
     * @param frequency  a frequency in Hertz
     * @param amplitude  a loudness in 0-1
     * @return a {@link Tone}
     * @throws IllegalArgumentException if either of the parameters is out of range
     */
    public static Tone makeToneFA(double frequency, double amplitude) throws IllegalArgumentException {
        return new Tone(frequency, amplitude);
    }

    public static Tone makeToneFD(double frequency, double dBm) throws IllegalArgumentException {
        return makeToneFA(frequency, getAmplitudeFromDb(dBm));
    }

    protected final double frequency;
    protected final double amplitude;

    /**
     * Private constructor to prevent subclassing
     *
     * @param frequency  the frequency in Hertz
     * @param amplitude  the amplitude - range 0 to 1
     * @throws IllegalArgumentException if any of the parameters is out of range
     */
    protected Tone(double frequency, double amplitude) throws IllegalArgumentException {
        checkArgument(frequency > 0.0 && frequency < 40000, "Frequency is out of range");
        this.frequency = frequency;
        checkArgument(isValidAmplitude(amplitude),"Amplitude %f is out of range %f to %f", amplitude, LOW_AMPLITUDE, HIGH_AMPLITUDE);
        this.amplitude = amplitude;
    }

    /**
     * Get the frequency represented by this Tone
     *
     * @return a frequency in Hertz
     */
    public double getFrequency() {
        return frequency;
    }

    /**
     * Get the amplitude represented by this Tone
     *
     * @return an amplitude in the range in the range {@link #LOW_AMPLITUDE} to
     * {@link #HIGH_AMPLITUDE}
     */
    public double getAmplitude() {
        return amplitude;
    }

    /**
     * Is this a valid amplitude?
     *
     * @param amplitude a value between {@link #LOW_AMPLITUDE} and {@link #HIGH_AMPLITUDE}
     * @return true if the above condition holds
     */
    public static boolean isValidAmplitude(double amplitude) {
        return (amplitude >= LOW_AMPLITUDE && amplitude <= HIGH_AMPLITUDE);
    }


    /**
     * Get a wave amplitude from a dbM value. This is pretty random but some references that could
     * be relevant if you are interested are given below:
     * <a href="https://dspillustrations.com/pages/posts/misc/decibel-conversion-factor-10-or-factor-20.html">Factor 20 for waves</a>
     * <a href="http://www.theory.physics.ubc.ca/341-current/dB.pdf">Discussion of loudness</a>
     * @param dB - a dBm value assumed to be negative - i.e. 0 is the loudest possible dBm
     * @return an amplitude < 1 if dBm is < 0
     */
    public static double getAmplitudeFromDb(double dB) {
        checkArgument(MAX_DB >= dB && dB >= MIN_DB, "db Parameter must be in range %s to %s, was %s",
                MAX_DB, MIN_DB, dB);
        return Math.pow(10, dB / 20.0);
    }

    public static double getDbFromAmplitude(double amplitude) {
        checkArgument(HIGH_AMPLITUDE >= amplitude && amplitude >= LOW_AMPLITUDE,
                "damplitude parameter must be in range %s to %s, was %s",
                LOW_AMPLITUDE, HIGH_AMPLITUDE, amplitude);
        return 20 * Math.log10(amplitude);
    }


    @Override
    public String toString() {
        return String.format("%d@%d%%", round(frequency), round(100 * amplitude));
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object a) {
        return (a instanceof Tone && ((Tone) a).frequency == frequency && ((Tone) a).amplitude == amplitude);
    }
}
