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
package com.jaccal.command;

import com.jaccal.CardException;
import com.jaccal.command.iso.ISO7816;
import com.jaccal.util.NumUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Thomas Tarpin-Lyonnet
 * @author Chang Sau Sheong
 * Encapsulates an ISO 7816 command that is sent to the card.
 */
public abstract class Command {

  protected byte cla;
  protected byte ins;
  protected byte p1;
  protected byte p2;
  protected byte lc;
  protected byte le;
  protected byte[] data;

  public Command(){
      this.cla = (byte)0;
      this.ins = (byte)0;
      this.p1 = (byte)0;
      this.p2 = (byte)0;
      this.lc = (byte)0;
      this.data = null; 
      this.le = (byte)-1;      
  }
  public Command(byte cla, byte ins){
      this.cla = (byte)cla;
      this.ins = (byte)ins;
      this.p1 = (byte)0;
      this.p2 = (byte)0;
      this.lc = (byte)0;
      this.data = null; 
      this.le = (byte)-1;
  }
  public Command(byte cla, byte ins, byte p1, byte p2, byte lc, byte[] data, byte le) {

    this.cla = cla;
    this.ins = ins;
    this.p1 = p1;
    this.p2 = p2;
    this.lc = lc;
    this.data = data;
    this.le = le;
  }
  
  public Command(String cmdStr) throws CardException{
   
  	byte [] cmdBytes = NumUtil.toStringHex(cmdStr);
  	
    cla = cmdBytes[ISO7816.OFFSET_CLA];
    ins = cmdBytes[ISO7816.OFFSET_INS];
    p1 = cmdBytes[ISO7816.OFFSET_P1];
    p2 = cmdBytes[ISO7816.OFFSET_P2];
    lc = cmdBytes[ISO7816.OFFSET_P3];
    
    // Check if the command is out or incoming
    if(cmdBytes.length > 5){
        int theLc = NumUtil.getUnsignedValue(lc);
        // Check the validity of Lc regarding the data field length
        if(theLc > (cmdBytes.length - ISO7816.LENGTH_APDU_HEADER))
            throw new CardException("(Command) Bad command format");
        
        byte [] dataField = new byte[theLc];
        for(int i=0;i<theLc;i++){
        	dataField[i] = cmdBytes[ISO7816.OFFSET_DATA_FIELD+i];
        }
        data = dataField;  
        
        if(cmdBytes.length > data.length + ISO7816.LENGTH_APDU_HEADER)
        	le = cmdBytes[cmdBytes.length-1];
        else
        	le = -1;       
    }
    else{
        le = -1;
        data = null;
    }

  }
  
  public Command(String cmdHeader, String cmdDataField) throws CardException{
    
   	byte [] cmdBytes = NumUtil.toStringHex(cmdHeader);
   	
     cla = cmdBytes[ISO7816.OFFSET_CLA];
     ins = cmdBytes[ISO7816.OFFSET_INS];
     p1 = cmdBytes[ISO7816.OFFSET_P1];
     p2 = cmdBytes[ISO7816.OFFSET_P2];
     lc = cmdBytes[ISO7816.OFFSET_P3];
     
     if(cmdDataField.length() == 0)
         throw new CardException("(Command) The data field is empty");

     int theLc = NumUtil.getUnsignedValue(lc);
     
     // Convert the data field in a byte array
     cmdBytes = NumUtil.toStringHex(cmdDataField);     
     
     // Check the validity of Lc regarding the data field length
     if(theLc > cmdBytes.length)
         throw new CardException("(Command) Bad command format");
     
     byte [] dataField = new byte[theLc];
     
     for(int i=0;i<theLc;i++){
     	dataField[i] = cmdBytes[i];
     }
     data = dataField;  
     
     if(cmdBytes.length > data.length)
     	le = cmdBytes[cmdBytes.length - 1];
     else
     	le = -1;
   }  
  
  public final byte[] getBytes() throws IOException {

    byte[] byteArray;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    baos.write(cla);
    baos.write(ins);
    baos.write(p1);
    baos.write(p2);
    baos.write(lc);

    if (data != null) {
      baos.write(data);
    }
    if (le != -1) {
      baos.write(le);
    }

    byteArray = baos.toByteArray();
    baos.close();

    return byteArray;
  }

  public byte getCla() {
    return cla;
  }

  public byte getIns() {
    return ins;
  }

  public byte getP1() {
    return p1;
  }

  public byte getP2() {
    return p2;
  }

  public byte getLc() {
    return lc;
  }

  public byte getLe() {
    return le;
  }

  public void setP1(byte p1) {
    this.p1 = p1;
  }

  public void setP2(byte p2) {
    this.p2 = p2;
  }

  public void setLc(byte lc) {
    this.lc = lc;
  }

  public void setLe(byte le) {
    this.le = le;
  }

  public byte[] getData() {
    return data;
  }

  /**
   * 
   * @param data The data to set
   */
  public void setData(byte[] data) {
    this.data = data;
  }

  /**
   * @param cla The cla to set.
   */
  public void setCla(byte cla) throws CardException{
      this.cla = cla;
  }

  
  
  public String toString() {
    String str = null;

    try {
      str = "[S] " + NumUtil.toHexString(getBytes());
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    return str;
  }

}
