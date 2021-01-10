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

package org.linguafranca.sound.cadence;

import org.junit.Test;
import org.linguafranca.sound.tone.Tone;

import static org.junit.Assert.*;

/**
 * @author jo
 */
public class ToneScriptParserTest {

    @Test
    public void parseToneScript() {
        CadenceScript script = ToneScriptParser.parseToneScript("test", "60(2/4)");
        assertEquals(1, script.getCadences().size());
        assertTrue(script.isRinger());
    }

    @Test
    public void parseToneScript2() {
        CadenceScript script = ToneScriptParser.parseToneScript("test", "60(.2/.2,.2/.2,.2/.2,1/4)");
        assertEquals(1, script.getCadences().size());
        assertTrue(script.isRinger());
        CadenceSection section = script.getCadences().get(0);
        assertEquals(4, section.size());
        assertEquals(200, section.get(0).getOnDuration());
        assertEquals(200, section.get(0).getOffDuration());
        assertEquals(200, section.get(1).getOnDuration());
        assertEquals(200, section.get(1).getOffDuration());
        assertEquals(200, section.get(2).getOnDuration());
        assertEquals(200, section.get(2).getOffDuration());
        assertEquals(1000, section.get(3).getOnDuration());
        assertEquals(4000, section.get(3).getOffDuration());
        assertEquals(6200, section.getInherentLength());
    }

    @Test
    public void parseToneScript3() {
        CadenceScript script = ToneScriptParser.parseToneScript("sit tone", "985@-16,1428@-16,1777@-16;20(.380/0/1,.380/0/2,.380/0/3,0/4/0)");
        assertEquals(1, script.getCadences().size());
        assertFalse(script.isRinger());
        assertEquals(3, script.getTones().size());

        CadenceSection section = script.getCadences().get(0);
        assertEquals(20000, section.getDuration());
        assertEquals(4, section.size());

        {
            CadenceSegment segment = section.get(0);
            assertEquals(380, segment.getOnDuration());
            assertEquals(0, segment.getOffDuration());
            assertEquals(1, segment.getTones().size());
            assertEquals(985, segment.getTones().get(0).getFrequency(), 0.1);
            assertEquals(-16, Tone.getDbFromAmplitude(segment.getTones().get(0).getAmplitude()), 0.01);

        }

        {
            CadenceSegment segment = section.get(1);
            assertEquals(380, segment.getOnDuration());
            assertEquals(0, segment.getOffDuration());
            assertEquals(1, segment.getTones().size());
            assertEquals(1428, segment.getTones().get(0).getFrequency(), 0.1);
            assertEquals(-16, Tone.getDbFromAmplitude(segment.getTones().get(0).getAmplitude()), 0.01);

        }

        {
            CadenceSegment segment = section.get(2);
            assertEquals(380, segment.getOnDuration());
            assertEquals(0, segment.getOffDuration());
            assertEquals(1, segment.getTones().size());
            assertEquals(1777, segment.getTones().get(0).getFrequency(), 0.1);
            assertEquals(-16, Tone.getDbFromAmplitude(segment.getTones().get(0).getAmplitude()), 0.01);

        }

        {
            CadenceSegment segment4 = section.get(3);
            assertEquals(0, segment4.getOnDuration());
            assertEquals(4000, segment4.getOffDuration());
            assertEquals(0, segment4.getTones().size());
        }

    }

    @Test
    public void parseToneScript4() {
        CadenceScript script = ToneScriptParser.parseToneScript("stutter tone", "350@-19,440@-19;2(.1/.1/1+2);10(*/0/1+2)");
        assertEquals(2, script.getCadences().size());
        assertEquals(2, script.getTones().size());
        assertFalse(script.isRinger());

        {
            CadenceSection section = script.getCadences().get(0);
            assertEquals(2000, section.getDuration());
            assertEquals(1, section.size());
            {
                CadenceSegment segment = section.get(0);
                assertEquals(100, segment.getOnDuration());
                assertEquals(100, segment.getOffDuration());
                assertEquals(2, segment.getTones().size());

                assertEquals(350, segment.getTones().get(0).getFrequency(), 0.0001);
                assertEquals(-19, Tone.getDbFromAmplitude(segment.getTones().get(0).getAmplitude()), 0.01);
                assertEquals(440, segment.getTones().get(1).getFrequency(), 0.0001);
                assertEquals(-19, Tone.getDbFromAmplitude(segment.getTones().get(1).getAmplitude()), 0.01);
            }
        }

        {
            CadenceSection section = script.getCadences().get(1);
            assertEquals(10000, section.getDuration());
            assertEquals(1, section.size());
            {
                CadenceSegment segment = section.get(0);
                assertEquals(-1, segment.getOnDuration());
                assertEquals(0, segment.getOffDuration());
                assertEquals(2, segment.getTones().size());

                assertEquals(350, segment.getTones().get(0).getFrequency(), 0.0001);
                assertEquals(-19, Tone.getDbFromAmplitude(segment.getTones().get(0).getAmplitude()), 0.01);
                assertEquals(440, segment.getTones().get(1).getFrequency(), 0.0001);
                assertEquals(-19, Tone.getDbFromAmplitude(segment.getTones().get(1).getAmplitude()), 0.01);
            }
        }
     }

    @Test
    public void parseToneScript5() {
        CadenceScript script = ToneScriptParser.parseToneScript("test", "350@-19,440@-19;10(*/0/1+2)");
        assertEquals(1, script.getCadences().size());
        assertEquals(2, script.getTones().size());
        assertFalse(script.isRinger());

        {
            CadenceSection section = script.getCadences().get(0);
            assertEquals(10000, section.getDuration());
            assertEquals(1, section.size());
            {
                CadenceSegment segment = section.get(0);
                assertEquals(-1, segment.getOnDuration());
                assertEquals(0, segment.getOffDuration());
                assertEquals(2, segment.getTones().size());

                assertEquals(350, segment.getTones().get(0).getFrequency(), 0.0001);
                assertEquals(-19, Tone.getDbFromAmplitude(segment.getTones().get(0).getAmplitude()), 0.01);
                assertEquals(440, segment.getTones().get(1).getFrequency(), 0.0001);
                assertEquals(-19, Tone.getDbFromAmplitude(segment.getTones().get(1).getAmplitude()), 0.01);
            }
        }
    }

    @Test
    public void parseToneScript5a() {
        // does it work with lots of spaces?
        CadenceScript script = ToneScriptParser.parseToneScript("test", "350 @ -19 , 440 @ -19 ; 10 (* / 0 / 1 + 2 )");
        assertEquals(1, script.getCadences().size());
        assertEquals(2, script.getTones().size());
        assertFalse(script.isRinger());

        {
            CadenceSection section = script.getCadences().get(0);
            assertEquals(10000, section.getDuration());
            assertEquals(1, section.size());
            {
                CadenceSegment segment = section.get(0);
                assertEquals(-1, segment.getOnDuration());
                assertEquals(0, segment.getOffDuration());
                assertEquals(2, segment.getTones().size());

                assertEquals(350, segment.getTones().get(0).getFrequency(), 0.0001);
                assertEquals(-19, Tone.getDbFromAmplitude(segment.getTones().get(0).getAmplitude()), 0.01);
                assertEquals(440, segment.getTones().get(1).getFrequency(), 0.0001);
                assertEquals(-19, Tone.getDbFromAmplitude(segment.getTones().get(1).getAmplitude()), 0.01);
            }
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript6() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript7() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript8() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript9() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10()");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript10() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(a)");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript11() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(3/2/0)");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript12() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(1/2/3)");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript13() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(1/2/1+2)hello world");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript14() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(1/2/1+2, hello)");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript15() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(1/2/1+2),()");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript16() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(1/2/1+2))");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript16a() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(1/2/1+2/1)");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript17() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(a/2/1+2)");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test (expected = IllegalArgumentException.class)
    public void parseToneScript18() {
        try {
            ToneScriptParser.parseToneScript("fails", "350@-19,440@-19;10(1/b/1+2)");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test
    public void parseFreqScript() {
        Tone.List list = ToneScriptParser.parseFreqScript("440@-10");
        assertEquals(1, list.size());
        assertEquals(440, list.get(0).getFrequency(), 0.0001);
        assertEquals(-10, Tone.getDbFromAmplitude(list.get(0).getAmplitude()), 0.01);
    }

    @Test
    public void parseFreqScript2() {
        Tone.List list = ToneScriptParser.parseFreqScript("350@-19,440@-19");
        assertEquals(2, list.size());
        assertEquals(350, list.get(0).getFrequency(), 0.0001);
        assertEquals(-19, Tone.getDbFromAmplitude(list.get(0).getAmplitude()), 0.01);
        assertEquals(440, list.get(1).getFrequency(), 0.0001);
        assertEquals(-19, Tone.getDbFromAmplitude(list.get(1).getAmplitude()), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseFreqScript3() {
        try {
            ToneScriptParser.parseFreqScript("ab@-19,440@-19");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    @Test(expected = IllegalArgumentException.class)
    public void parseFreqScript4() {
        try {
            ToneScriptParser.parseFreqScript("350@-19,440");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseFreqScript5() {
        try {
            ToneScriptParser.parseFreqScript("350@-19,");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    @Test
    public void parseCadenceSection() {
        CadenceSection section = ToneScriptParser.parseCadenceSection("60(2/4)", new Tone.List());
        assertEquals(60000, section.getDuration());
        assertEquals(1, section.size());
        assertEquals(2000, section.get(0).getOnDuration());
        assertEquals(4000, section.get(0).getOffDuration());
        assertEquals(6000, section.getInherentLength());

    }
    @Test
    public void parseCadenceSection2() {
        CadenceSection section = ToneScriptParser.parseCadenceSection("60(.2/.2,.2/.2,.2/.2,1/4)", new Tone.List());
        assertEquals(60000, section.getDuration());
        assertEquals(4, section.size());
        assertEquals(200, section.get(0).getOnDuration());
        assertEquals(200, section.get(0).getOffDuration());
        assertEquals(200, section.get(1).getOnDuration());
        assertEquals(200, section.get(1).getOffDuration());
        assertEquals(200, section.get(2).getOnDuration());
        assertEquals(200, section.get(2).getOffDuration());
        assertEquals(1000, section.get(3).getOnDuration());
        assertEquals(4000, section.get(3).getOffDuration());
        assertEquals(6200, section.getInherentLength());
    }
}