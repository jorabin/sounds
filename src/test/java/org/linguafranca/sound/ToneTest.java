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

package org.linguafranca.sound;

import org.junit.Test;

import org.linguafranca.sound.tone.Tone;

import static org.junit.Assert.assertEquals;
import static org.linguafranca.sound.tone.MidiTone.getVelocityFromAmplitude;
import static org.linguafranca.sound.tone.Tone.getAmplitudeFromDb;
import static org.linguafranca.sound.tone.Tone.getDbFromAmplitude;

/**
 * @author jo
 */
public class ToneTest {

    @Test
    public void makeToneFA() {
    }

    @Test
    public void makeToneFD() {

        System.out.format("%3s: %10s %10s\n", "db", "amp fr dB", "v from a1");
        for (int db = 0; db >= -30; db -=2){
            double amplitude = Tone.getAmplitudeFromDb(db);
            double velocity = getVelocityFromAmplitude(amplitude);
            System.out.format("%3d: %10.1f %10.1f\n", db, amplitude, velocity);
        }

    }

    @Test
    public void checkCreate() {
        Tone.makeToneFA(440.0, 0.5);
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkCreate1() {
        Tone.makeToneFA(-3, 0.5);
    }
    @Test(expected = IllegalArgumentException.class)
    public void checkCreate2() {
        Tone.makeToneFA(440.0, 12);
    }
    @Test
    public void checkCreate3() {
        Tone.makeToneFA(440.0, 0.5);
    }

    @Test
    public void testDbAmplitude() {
        for (double amp = 0.0; amp <= 1.0; amp += 0.05){
            System.out.format("%f %f %f\n", amp, getDbFromAmplitude(amp), getAmplitudeFromDb(getDbFromAmplitude(amp)));
            assertEquals(amp, getAmplitudeFromDb(getDbFromAmplitude(amp)), 0.000001);
        }
    }
}