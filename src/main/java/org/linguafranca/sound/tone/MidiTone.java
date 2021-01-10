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

import static java.lang.Math.*;

/**
 * Adaptor class to allow tones to be used with the MIDI interface
 * <p>
 * Documentation
 * http://tomscarff.110mb.com/midi_analyser/midi_note_numbers_for_octaves.htm
 */
public class MidiTone extends Tone {
    /**
     * Lowest Midi noteNumber
     */
    public static final int MIDI_LOW_NOTE_NUMBER = 0;
    /**
     * Highest Midi noteNumber
     */
    public static final int MIDI_HIGH_NOTE_NUMBER = 127;
    /**
     * Lowest Midi Velocity
     */
    public static final int MIDI_LOW_VELOCITY = 0;
    /**
     * Highest Midi Velocity
     */
    public static final int MIDI_HIGH_VELOCITY = 127;
    /**
     * The average sound has velocity 64
     */
    public static int defaultVelocity = 64;
    /**
     * reference midi noteNumber number corresponding to the {@link #referenceFrequency} reference frequency
     */
    public static int referenceNoteNumber = 69;

    /**
     * Private constructor to prevent subclassing
     *
     * @param frequency  the frequency in Hertz
     * @param amplitude  the amplitude
     * @throws IllegalArgumentException if any of the parameters is out of range
     */
    protected MidiTone(double frequency, double amplitude) throws IllegalArgumentException {
        super(frequency, amplitude);
    }

    /**
     * get the note number represented by this class
     *
     * @return a Midi note number
     * @throws NoteNumberOutOfRangeException if the frequency can't be represented as a note number
     */
    public int getNoteNumber() {
        return getNoteNumberFromFrequency(frequency);
    }

    /**
     * get the velocity represented by this class
     *
     * @return a Midi velocity
     * @throws VelocityOutOfRangeException if the amplitude can't be represented as a velocity
     */
    public int getVelocity() {
        return getVelocityFromAmplitude(amplitude);
    }


    /**
     * Exception class for this class  - subclassed to provide specific exceptions
     */
    public static class ToneCreationException extends IllegalArgumentException {
        public ToneCreationException(String message) {
            super(message);
        }
    }

    /**
     * Invalid note number
     */
    public static class NoteNumberOutOfRangeException extends ToneCreationException {
        public NoteNumberOutOfRangeException(String message) {
            super(message);
        }
    }

    /**
     * Invalid velocity
     */
    public static class VelocityOutOfRangeException extends ToneCreationException {
        public VelocityOutOfRangeException(String message) {
            super(message);
        }
    }
    /**
     * Factory method for creating a {@link Tone}
     *
     * @param noteNumber the midi note number
     * @param velocity   the midi velocity
     * @return a {@link Tone}
     * @throws ToneCreationException if either of the parameters is an invalid Midi value
     */
    public static Tone makeToneNV(int noteNumber, int velocity) throws ToneCreationException {
        if (!isValidNoteNumber(noteNumber)) {
            throw new NoteNumberOutOfRangeException(
                    String.format("%d is not a valid Midi note number", noteNumber));
        }
        if (!isValidVelocity(velocity)) {
            throw new VelocityOutOfRangeException(
                    String.format("%d is not a valid Midi velocity", velocity));
        }
        return new MidiTone(getFrequencyFromNoteNumber(noteNumber), getAmplitudeFromVelocity(velocity));
    }

    /**
     * Factory to create a Midi Tome from a Tone
     * @param tone the source
     * @return the newly created MidiTone
     */
    public static Tone makeToneFromTone(Tone tone) {
        return new MidiTone(tone.frequency, tone.amplitude);
    }

    /**
     * Calculates the closest Midi Note Number to a given frequency in Hertz - without
     * checking that the result is a valid Midi note number.
     * <p>
     * Calculation is performed by reference to {@link #referenceFrequency} and {@link #referenceNoteNumber} assuming
     * an equal temper 12 semi-tone scale.
     *
     * @param frequency the frequency in Hertz to get a note number for
     * @return the closest integer value yielded by the calculation
     */
    public static int getNoteNumberFromFrequency(double frequency) {
        return round((float) (referenceNoteNumber + 12 * (log(frequency / referenceFrequency) / log(2))));
    }


    /**
     * Calculates what frequency this note number is, according to  {@link #referenceFrequency} and {@link #referenceNoteNumber}
     *
     * @param noteNumber a Midi Note Number
     * @return a frequency in Hertz
     */
    public static double getFrequencyFromNoteNumber(int noteNumber) {
        return referenceFrequency * pow(2, (double)(noteNumber - referenceNoteNumber) / 12);
    }

    /**
     * Get the amplitude value corresponding to a Midi velocity - the algorithm is rather dodgy, kind of works
     *
     * @param velocity a midi velocity
     * @return an amplitude
     */
    public static double getAmplitudeFromVelocity(int velocity) {
        // return velocity * HIGH_AMPLITUDE / MIDI_HIGH_VELOCITY;
        return Math.log(2.0 * velocity/127.0);
    }

    /**
     * Return a velocity for an amplitude
     *
     * @param amplitude the amplitude
     * @return a velocity
     * @throws VelocityOutOfRangeException if the amplitude can't be represented as a velocity
     */
    public static int getVelocityFromAmplitude(double amplitude) {
        //int velocity = (int) round(MIDI_HIGH_VELOCITY * amplitude / HIGH_AMPLITUDE);

        return (int) Math.round(127.0/2 * Math.pow(2, amplitude));
    }

    /**
     * Checks that the passed velocity is in range for Midi
     *
     * @param velocity a midi velocity value
     * @return true if the passed value lies between {@link #MIDI_LOW_VELOCITY} and {@link #MIDI_HIGH_VELOCITY}
     */
    public static boolean isValidVelocity(int velocity) {
        return (velocity >= MIDI_LOW_VELOCITY && velocity <= MIDI_HIGH_VELOCITY);
    }


    /**
     * Checks that the passed note number is in range for Midi
     *
     * @param noteNumber a Midi note number
     * @return true if the passed value lies between {@link #MIDI_LOW_NOTE_NUMBER} and {@link #MIDI_HIGH_NOTE_NUMBER}
     */
    public static boolean isValidNoteNumber(int noteNumber) {
        return (noteNumber >= MIDI_LOW_NOTE_NUMBER && noteNumber <= MIDI_HIGH_NOTE_NUMBER);
    }

}
