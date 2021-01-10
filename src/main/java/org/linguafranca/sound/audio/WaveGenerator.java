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

package org.linguafranca.sound.audio;

import java.util.Random;

/**
 * @author jo
 */
public class WaveGenerator {
    static private final Random random = new Random();

    /**
     * Creates a waveform
     */
    @FunctionalInterface
    public interface WaveGen {
        /**
         * Generate a value for the waveform
         *
         * @param position 0 <= position <= 1 in the cycle
         * @return -1 <= value <= +1 for the amplitude at that position
         */
        double getValue(double position);
    }

    /**
     * Some predefined WaveGens
     * See https://www.perfectcircuit.com/signal/difference-between-waveforms
     */
    public enum Waveform implements WaveGen {
        SINE(position -> Math.sin(position * 2.0 * Math.PI)),
        SQUARE(position -> position < 0.5 ? 1.0 : -1.0),
        TRIANGLE(position -> position < 0.25 ? 4.0 * position :
                position < 0.75 ? -4.0 * (position - 0.5) :
                        4.0 * (position - 1.0)),
        SAWTOOTH(position -> position < 0.5 ? 2.0 * position : 2.0 * (position - 1.0)),
        RANDOM(position -> random.nextDouble() * 2.0 - 1.0);

        private final WaveGen waveGen;

        Waveform(WaveGen waveGen) {
            this.waveGen = waveGen;
        }

        public WaveGen getWaveGen() {
            return waveGen;
        }

        public double getValue(double position) {
            return waveGen.getValue(position);
        }
    }

    /**
     * Apply amplitude modulation to combine two waves
     */
    @FunctionalInterface
    public interface Modulator {
        /**
         * Return a value for this position in the modulator's cycle
         *
         * @param position the position 0 <= position < 1 in the modulator's cycle
         * @param value the value being modulated
         * @return the modulated value
         */
        double getValue(double position, double value);
    }

    public static final Modulator SINE_MODULATOR = (position, value) -> Waveform.SINE.getValue(position) * value;

    public static final Modulator IDENTITY_MODULATOR = (position, value) -> value;
}
