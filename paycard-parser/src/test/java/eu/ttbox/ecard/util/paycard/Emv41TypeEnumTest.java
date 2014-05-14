package eu.ttbox.ecard.util.paycard;

import org.junit.Assert;
import org.junit.Test;

import eu.ttbox.ecard.util.ByteHelper;

/**
 * Created by jmorille on 15/05/2014.
 */
public class Emv41TypeEnumTest {

    @Test
    public void toStringString(){
        byte[] recv = ByteHelper.hex2Byte("A0 00 00 00 42 10 10");
        String val =  Emv41TypeEnum.STRING.toString(recv);

        System.out.println(val);
        Assert.assertNotNull(val);
        Assert.assertFalse(val.startsWith("Error"));
    }
}
