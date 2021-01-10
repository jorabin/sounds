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

package org.linguafranca.sound.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.linguafranca.sound.audio.WaveGenerator.IDENTITY_MODULATOR;
import static org.linguafranca.sound.audio.WaveGenerator.SINE_MODULATOR;

/**
 * Create audio from waveforms
 */
public class AudioEncoder {
    // class is static - hide constructor
    private AudioEncoder(){}

    public static Logger logger = LoggerFactory.getLogger(AudioEncoder.class);

    private static final float SAMPLE_RATE = 44100.0F;
    private static final float FRAME_RATE = SAMPLE_RATE;

    public static final AudioFormat DEFAULT_AUDIO_FORMAT = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            SAMPLE_RATE,
            16,            // sample size in bits
            2,             // channels
            4,             // frame size
            FRAME_RATE,     // frame rate = sample rate
            false          // little endian
    );
    // this is the scaling factor for encoding: inputs are -1 < 0 < 1, outputs need to be scaled
    protected static final float MAX_ENCODED_AMPLITUDE = (float) Math.pow(2, DEFAULT_AUDIO_FORMAT.getSampleSizeInBits() - 1);


    /**
     * Stereo, 16 bit little-endian writing
     */
    protected static void writeFrame(float amplitude, ByteArrayOutputStream audioStream) {
        int adjustedValue = Math.round(amplitude * MAX_ENCODED_AMPLITUDE);
        audioStream.write(adjustedValue & 0xFF);
        audioStream.write((adjustedValue >>> 8) & 0xFF);
        audioStream.write(adjustedValue & 0xFF);
        audioStream.write((adjustedValue >>> 8) & 0xFF);
    }


    /**
     * Constructs a new audio sample, using DEFAULT_AUDIO_FORMAT and SineWaves, creates
     * chosen number of periods of the selected modulated frequencies
     *
     * @param signalFrequency       the signal frequency in Hertz
     * @param signalAmplitude       the amplitude between 0 and 1
     * @param periods               number of periods of sample to create
     */
    public static AudioInputStream createSample(double signalFrequency, double signalAmplitude, int periods){
        return createSample(WaveGenerator.Waveform.SINE, signalFrequency, signalAmplitude, periods);
    }
    public static AudioInputStream createSample(WaveGenerator.WaveGen waveGen, double signalFrequency, double signalAmplitude, int periods) {

        logger.trace("Making {}@{} periods {}", signalFrequency, signalAmplitude, periods);

        // the greater of the modulation period and the length of the signal period in frames
        long signalPeriodLengthInFrames = Math.round(DEFAULT_AUDIO_FORMAT.getFrameRate() / signalFrequency);

        // get a new output stream to write to
        ByteArrayOutputStream audioStream = new ByteArrayOutputStream((int) signalPeriodLengthInFrames * DEFAULT_AUDIO_FORMAT.getFrameSize() * periods);

        for (int frameNumber = 0; frameNumber < signalPeriodLengthInFrames * periods; frameNumber++) {

            // The relative position inside the period of the waveform. 0.0 = beginning, 1.0 = end
            float positionInSignalPeriod = (float) (frameNumber % signalPeriodLengthInFrames) / signalPeriodLengthInFrames;
            float amplitude = (float) signalAmplitude * (float) waveGen.getValue(positionInSignalPeriod);

            writeFrame(amplitude, audioStream);
        }

        logger.trace("Finished making wave form: {} frames", signalPeriodLengthInFrames * periods);
        return new AudioInputStream(new ByteArrayInputStream(audioStream.toByteArray()), DEFAULT_AUDIO_FORMAT, signalPeriodLengthInFrames * periods);
    }
}
