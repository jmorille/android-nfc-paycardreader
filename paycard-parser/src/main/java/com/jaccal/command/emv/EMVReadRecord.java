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
package com.jaccal.command.emv;

import com.jaccal.CardException;
import com.jaccal.command.iso.ISOReadRecord;
import com.jaccal.util.NumUtil;

/**
 * @author Chang Sau Sheong
 */
public class EMVReadRecord extends ISOReadRecord {

  public EMVReadRecord(String fileSFI, String recordNumber, String le) throws CardException {
    super(fileSFI, recordNumber, le);

    byte [] b;
    if(fileSFI.length() == 0) {
        setP2((byte) 0);
    } else{
      b = NumUtil.toStringHex(fileSFI);
      if(NumUtil.getUnsignedValue(b[0]) >= (byte)0x1F)
        throw new CardException("(ReadRecord) fileSFI is invalid");
      setP2(b[0]);
    }
  }
}
