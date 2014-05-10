package eu.ttbox.ecard.model;


import org.junit.Assert;
import org.junit.Test;

public class RecvTagTest {

    @Test
    public void testHashCode() {
        RecvTag tag1 = new RecvTag(new byte[]{0x6F}, 12);
        RecvTag tag2 = new RecvTag("6F");

        Assert.assertEquals("Same hashcode",tag1.hashCode(), tag2.hashCode());
        Assert.assertEquals(tag1, tag2);

    }

}
