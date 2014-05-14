/*
 * Copyright (c) 2005 Chang Sau Sheong, Thomas Tarpin-Lyonnet.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jaccal.command.gsm;

import com.jaccal.CardException;
import com.jaccal.command.iso.ISOVerifyChv;
import com.jaccal.util.NumUtil;
 
/**
 * 
 * @author Thomas Tarpin-Lyonnet
 *
 */
public class GSMVerifyChv extends ISOVerifyChv{
    public final static String CHV1 = "01";
    public final static String CHV2 = "02";
   
    public GSMVerifyChv(String chvRefId, String chv) throws CardException{
        super(chvRefId,chv);
        super.setCla(GSM11_11.CLA_GSM11_11);
        
        // Check chv length. must be >= 4
        if((chv.length() < 4) || (chv.length() > 8))
        	throw new CardException("CHV value should be at least 4 byte long and less than 8 byte long");
         
        // Check the CHV reference ID value
        if((chvRefId.compareTo(CHV1) != 0) && (chvRefId.compareTo(CHV2) != 0))
        	throw new CardException("Bad CHV reference ID");
        
        // Check if the CHV is decimal digits only (0-9)
        try {
        	Integer.parseInt(chv,10);
		} catch (NumberFormatException e) {
			throw new CardException("CHV value has to be decimal digits only (0-9)");
		}
		
		// Convert the chv in string
		String paddedChv = new String();
		for(int i=0; i < chv.length(); i++){
			paddedChv += "3" + chv.substring(i,i+1);
		}
		
		// Add the padding to FF until 16 digits
		while(paddedChv.length() < 16){
			paddedChv += "FF";
		}
		
		byte[] b = NumUtil.toStringHex(paddedChv);
		setData(b);
		setLc((byte)b.length);		
    }

    /* (non-Javadoc)
     * @see com.jaccal.command.Command#setCla(byte)
     */
    public void setCla(byte cla){
    }      
}
