/*
 * Copyright (c) 2011 - 2021 Jo Rabin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.linguafranca.sound.player;

import org.linguafranca.sound.tone.MidiTone;
import org.linguafranca.sound.tone.Tone;
import org.linguafranca.sound.cadence.CadenceSection;
import org.linguafranca.sound.cadence.CadenceSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.midi.*;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Plays {@link org.linguafranca.sound.cadence.CadenceSection}s via the Midi system.
 * <p>
 * The sounds are simulated by their nearest Midi note and velocity - if there is one. So playing valid cadences can
 * cause exceptions during the playout of tones if the information in the Tone can't be rendered in Midi
 * <p>
 * @see MidiTone#getNoteNumber() org.linguafranca.sound.tone.Tone#getVelocity()
 *
 */
// TODO this doesn't obey the contract of completion or async queueing

public class MidiCadencePlayer extends CadencePlayer.Skeleton {
    /** the instrument to use to play telephone bell */
    public static Patch bellSound = new Patch(0, 124);
    /** the instrument to use for generating sine waves (by default the ocarina */
    public static Patch toneSound = new Patch(0, 79);

    public Patch patch = toneSound;

    // TODO make this back into an instance member
    static protected volatile Sequencer sequencer;
    //TODO make this back to instance member
    static protected Synthesizer synthesizer;

    static Logger logger =LoggerFactory.getLogger(MidiCadencePlayer.class);

    public MidiCadencePlayer () {
        super();
    }
    /**
     * Prepare the Midi System
     * 
     * @throws Exception if a new synthesizer could not be created
     */
    @Override
    public void openSound() throws Exception {

        if (synthesizer == null) {
            if ((synthesizer = MidiSystem.getSynthesizer()) == null) {
                throw new Exception("getSynthesizer() failed");
            }
        }

        synthesizer.open();
        Soundbank sb = synthesizer.getDefaultSoundbank();
        if (sb != null) {
            if (!synthesizer.loadInstruments(sb, new Patch[]{bellSound, toneSound})) {
                throw new Exception("Instruments could not be loaded");
            }
        }

        sequencer = MidiSystem.getSequencer();
        sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
    }

    /**
     * Play the cadences
     * 
     * @param cadences The {@link org.linguafranca.sound.cadence.CadenceSection} to queue
     * @param block    True to wait for the playout to complete before returning
     */
    @Override
    public void queue(QueueItem<CadenceSection> cadences, boolean block) {
        play(cadences.getItem(), patch, block);
        cadences.setFinished();
    }
    @Override
    public QueueItem<CadenceSection> queue(CadenceSection cadences, boolean block) {
        QueueItem<CadenceSection> item = new QueueItem<>(cadences);
        queue(item,block);
        return item;
    }


    /**
     * Stop whatever sound is happening right now
     */
    @Override
    public boolean stopSound(int timeToWaitInMillis) {
        logger.debug("MIDI stop sound called");
        logger.debug("sequencer.isRunning() is {} sequencer is {}",sequencer.isRunning(), sequencer);
        if (sequencer.isRunning()){
            logger.debug("Sequencer is running, calling stop");
            sequencer.stop();
        }
        return !sequencer.isRunning();
    }

    /**
     * Shut down the Midi sound system
     */
    @Override
    public boolean closeSound(int timeToStopMillis) {
        logger.debug("MIDI close sound called");
        if (sequencer != null) {
            sequencer.close();
        }
        if (synthesizer != null) {
            synthesizer.close();
        }
        sequencer = null;
        synthesizer = null;
        return true;
    }


    /**
     * set each quarter note to last 1 sec
     */
    private static final int MICROSECS_PER_QUARTER_NOTE = 1000000;
    /**
     * 1000 pulses per quarter note is 1 millisec per pulse if each quarter note is 1 sec
     */
    private static final int PULSES_PER_QUARTER_NOTE = 1000;

    /**
     * Play the sequences for the defined duration
     *
     * @param cadenceSection the cadence list to queue
     * //@param sequencer   the sequencer to use
     * @param patch       the patch to queue it on
     * @param block       wait for playout to complete before returning
     */
    public static void play(CadenceSection cadenceSection, Patch patch, boolean block) {
        checkArgument(block, "Non-blocking not supported in play method for %s", MidiCadencePlayer.class);
        final int channelID = 0;
        MetaEventListener listener = null;
        try {
            Sequence sequence = new Sequence(Sequence.PPQ, PULSES_PER_QUARTER_NOTE);
            Track track = sequence.createTrack();

            addEventToTrack(track, channelID, 0, ShortMessage.PROGRAM_CHANGE, patch.getProgram(), 0);

            long stop = 0;
            for (CadenceSegment cadence : cadenceSection) {
                stop = addToTrack(cadence, track, channelID, stop);
            }

            // seems like the sequencer needs to be closed and reopened to maintain tempo
            sequencer.close();
            // TODO why does this have to be a new sequencer each time?
            sequencer = MidiSystem.getSequencer();
            sequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
            sequencer.open();
            if (block) {
                listener = new MetaEventListener() {
                    private final Thread callingThread = Thread.currentThread();

                    @Override
                    public void meta(MetaMessage meta) {
                        if (meta.getType() == 47) {
                            callingThread.interrupt();
                        }
                    }
                };
                sequencer.addMetaEventListener(listener);
            }
            sequencer.setTempoInMPQ(MICROSECS_PER_QUARTER_NOTE);
            sequencer.setSequence(sequence);
            sequencer.setLoopCount(cadenceSection.getRepeatCount());
            sequencer.setLoopStartPoint(0);
        } catch (MidiUnavailableException | InvalidMidiDataException m) {
            throw new RuntimeException(m.getMessage(), m);
        }
        logger.debug("Starting sound");
        sequencer.start();
        logger.debug("sequencer.isRunning() is {} sequencer is {}",sequencer.isRunning(), sequencer);
        if (block) {
            try {
                Thread.sleep(cadenceSection.getDuration() + cadenceSection.getInherentLength() + 100);
                logger.debug("Returned from sleep (abnormal)");
                sequencer.stop();
/*
                sequencer.removeMetaEventListener(listener);
                Thread.interrupted();
                throw new RuntimeException("Sequencer did not complete in time");
*/
            } catch (InterruptedException e) {
                logger.debug("Interrupted back (normal)");
            }
            sequencer.removeMetaEventListener(listener);
        }
    }

    /**
     * Add a Midi Event to a Track / Channel Combo
     * @param track      The Track to add to
     * @param channelId  The Channel to add to
     * @param offset     How many pulses in the message should be
     * @param messageId  The type of message
     * @param data1      Data associated with the message
     * @param data2      More data associated with the message
     * @throws InvalidMidiDataException  if the Midi system doesn't like it
     */
    public static void addEventToTrack(Track track, int channelId, long offset, int messageId, int data1, int data2) throws InvalidMidiDataException {
        ShortMessage message = new ShortMessage();
        message.setMessage(messageId, channelId, data1, data2);
        MidiEvent event = new MidiEvent(message, offset);
        track.add(event);
    }

    /**
     * Adds a CadenceSegment to a Track / Channel Combo
     * @param cadence   the cadence to add
     * @param track     the track to add to
     * @param channelId the channel to add to
     * @param offset    the number of pulses after the start the cadence should begin
     * @return          the next offset in - i.e. the offset plus the length of the CadenceSegment
     * @throws InvalidMidiDataException  if the Midi system didn't like the data being put in
     */
    public static long addToTrack(CadenceSegment cadence, Track track, int channelId, long offset) throws InvalidMidiDataException {
        // Insert to tones as Note>ON messages
        if (cadence.getOnDuration() != 0) for (Tone tone : cadence.getTones()) {
                addEventToTrack(track, channelId, offset, ShortMessage.NOTE_ON,
                        MidiTone.getNoteNumberFromFrequency(tone.getFrequency()),
                        MidiTone.getVelocityFromAmplitude(tone.getAmplitude()));
        }
        // Turn the notes off
        for (Tone tone : cadence.getTones()) {
                addEventToTrack(track, channelId, offset + cadence.getOnDuration(), ShortMessage.NOTE_OFF,
                        MidiTone.getNoteNumberFromFrequency(tone.getFrequency()),
                        MidiTone.getVelocityFromAmplitude(tone.getAmplitude()));
        }
        // add a message at the end of the period - corresponding to the silence
        addEventToTrack(track, channelId, offset + cadence.getDuration(), ShortMessage.NOTE_OFF, 0, 0);
        return offset + cadence.getDuration();
    }
}
