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

import org.junit.Before;
import org.junit.Test;
import org.linguafranca.sound.cadence.CadenceSection;
import org.linguafranca.sound.cadence.DTMF;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

/**
 * @author jo
 */
public class ScratchFileTest {

    @Before
    public void setUp() throws Exception {
    }


    @Test
    public void getMixerInfos(){
        Mixer.Info [] mixerInfos = AudioSystem.getMixerInfo();
        for (Mixer.Info mixerInfo : mixerInfos) {
            System.out.println(mixerInfo);
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void getMixerTest() throws LineUnavailableException {
        Mixer mixer = AudioSystem.getMixer(null);
        System.out.println(mixer.getMixerInfo());

        Clip clip1 = AudioSystem.getClip(mixer.getMixerInfo());
        Clip clip2 = AudioSystem.getClip(mixer.getMixerInfo());
        System.out.println("Synchronization is supported: " + mixer.isSynchronizationSupported(new Clip[]{clip1, clip2}, false));
        mixer.synchronize(new Clip[]{clip1, clip2}, false);
    }

    @Test
    public void testRun() {
    }

}