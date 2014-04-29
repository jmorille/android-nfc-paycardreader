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

import com.jaccal.command.iso.ISOCommand;
import com.jaccal.command.Command;

/**
 * *WARNING* This is incomplete
 * @author Chang Sau Sheong
 */
public class EMVGetProcessingOptions extends Command {

  public EMVGetProcessingOptions() {
    cla = (byte)0x80;
    ins = (byte)0xA8;
    p1 = (byte)0x00;
    p2 = (byte)0x00;
    lc = (byte)0x02;
    data = new byte[]{(byte) 0x83};
    le = (byte)0x00;

  }

}
