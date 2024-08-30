package com.systex.jbranch.fubon.commons.cbs.inf;

import com.systex.jbranch.fubon.commons.cbs.entity.CBSIDPattern;
import junit.framework.TestCase;

public class IDPatternTest extends TestCase {

    public void testMOI() {
        IDPattern moi = CBSIDPattern.MOI;
        assertEquals("19", moi.getCode());
        assertTrue(moi.match("1234567"));
        assertTrue(moi.match("1234567890"));
        assertFalse(moi.match("12345b7890"));
        assertFalse(moi.match("12345678900"));
        assertFalse(moi.match("123456789"));
    }

    public void testINC() {
        IDPattern inc = CBSIDPattern.INC;
        assertEquals("21", inc.getCode());

        assertTrue(inc.match("00000000"));
        assertTrue(inc.match("00000001"));
        assertTrue(inc.match("00000002"));
        assertTrue(inc.match("00000206"));
        assertTrue(inc.match("00000233"));
        assertTrue(inc.match("000000  1"));
        assertFalse(inc.match("00000002  2"));
        assertFalse(inc.match("00000002AB1"));
        assertFalse(inc.match("00000002__1"));
        assertFalse(inc.match("00000002  B"));
    }

    public void testOINC() {
        IDPattern oinc = CBSIDPattern.OINC;
        assertEquals("22", oinc.getCode());
        assertTrue(oinc.match("ABAB1234"));
        assertTrue(oinc.match("XXXX9999"));
        assertFalse(oinc.match("ABABA1234"));
        assertFalse(oinc.match("ABAB12345"));
        assertFalse(oinc.match("1235ABAB"));
        assertFalse(oinc.match("AAA1234"));
    }

    public void testOBU() {
        IDPattern obu = CBSIDPattern.OBU;
        assertEquals("29", obu.getCode());
        assertTrue(obu.match("123AB456"));
        assertTrue(obu.match("999XX999"));
        assertFalse(obu.match("1232AB456"));
        assertFalse(obu.match("123AB3456"));
        assertFalse(obu.match("123A3456"));
        assertFalse(obu.match("1233B456"));
        assertFalse(obu.match("12 AB456"));
        assertFalse(obu.match("123 B456"));
    }

    public void testMT() {
        IDPattern mt = CBSIDPattern.MT;
        assertEquals("32", mt.getCode());
        assertTrue(mt.match("88ABCDEF"));
        assertTrue(mt.match("88ABCDEFGH"));
        assertTrue(mt.match("88ABCDEFGHI"));
        assertFalse(mt.match("88ABCDE"));
        assertFalse(mt.match("88ABCDEFGHIJ"));
        assertFalse(mt.match("88ABCD1FGHI"));
        assertFalse(mt.match("8XABCDEFGHI"));
        assertFalse(mt.match("88ABCD FGHI"));
        assertFalse(mt.match("88ABCD_FGHI"));
    }

    public void testID() {
        IDPattern id = CBSIDPattern.ID;
        assertEquals("11", id.getCode());
        assertTrue(id.match("A000000000"));
        assertTrue(id.match("A000000001"));
        assertTrue(id.match("A000309934"));
        assertTrue(id.match("A001899991"));
        assertTrue(id.match("A001899993"));
        assertTrue(id.match("A0120000001"));
        assertFalse(id.match("10120000001"));
        assertFalse(id.match("A01200000011"));
        assertFalse(id.match("A0120000"));
        assertFalse(id.match("A0120B0000"));
        assertFalse(id.match("A0120 0000"));
        assertFalse(id.match("AC03624103"));
        assertFalse(id.match("A980001231"));
    }

    public void testARC() {
        IDPattern arc = CBSIDPattern.ARC;
        assertEquals("12", arc.getCode());
        assertTrue(arc.match("AB12345678"));
        assertFalse(arc.match("AB123456789"));
        assertFalse(arc.match("AB1234567"));
        assertFalse(arc.match("A12345678"));
        assertFalse(arc.match("ABC12345678"));
        assertFalse(arc.match("A 12345678"));
        assertFalse(arc.match("AB1234B678"));
        assertTrue(arc.match("A812345678"));
        assertTrue(arc.match("A912345678"));
    }

    public void testOID() {
        IDPattern oid = CBSIDPattern.OID;
        assertEquals("13", oid.getCode());
        assertTrue(oid.match("19530726SH3"));
        assertFalse(oid.match("1953072SH"));
        assertFalse(oid.match("19530726SHB"));
        assertFalse(oid.match("1953B726HB"));
        assertFalse(oid.match("1953 726SB"));
    }

    public void testGRP() {
        IDPattern grp = CBSIDPattern.GRP;
        assertEquals("31", grp.getCode());
        assertTrue(grp.match("GRP12345678"));
        assertFalse(grp.match("GRP123456789"));
        assertFalse(grp.match("AGRP12345678"));
        assertFalse(grp.match("GRP1234B678"));
    }

    public void testSWIFT() {
        IDPattern swift = CBSIDPattern.SWIFT;
        assertEquals("23", swift.getCode());

        assertTrue(swift.match("XXXXXXZZ"));
        assertTrue(swift.match("XXXXXXZZZZZ"));
        assertTrue(swift.match("XXXXXX11ZZZ"));
        assertTrue(swift.match("XXXXXXZZ333"));
    }

    public void testOTH() {
        IDPattern oth = CBSIDPattern.OTH;
        assertEquals("39", oth.getCode());
    }
}