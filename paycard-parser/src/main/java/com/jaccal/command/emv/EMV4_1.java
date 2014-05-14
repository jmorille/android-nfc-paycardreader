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

/**
 * This encapsulates all the constants used for EMV4_1
 * @author Chang Sau Sheong
 */
public interface EMV4_1 {
  // refer to EMV 4.1 Book 1 - Section 12.2.2
  public static final String AID_1PAY_SYS_DDF01 = "315041592E5359532E4444463031";

  // refer to EMV 4.1 Book 1 - Section 11.3.4
  public static final byte [] TAG_DF_FCI = {(byte)0x6F};
  public static final byte [] TAG_DF_FCI_NAME = {(byte)0x84};
  public static final byte [] TAG_DF_FCI_PROPRIETARY = {(byte)0xA5};
  public static final byte [] TAG_DF_FCI_SFI = {(byte)0x88};
  public static final byte [] TAG_DF_FCI_LANG = {(byte)0x5F, (byte)0x2D};

  public static final byte [] TAG_PSE_ENTRY = {(byte)0x61};

  // refer to EMV 4.1 Book 1 - Section 12.2.3
  public static final byte [] TAG_DF_ADF_NAME = {(byte)0x4F};
  public static final byte [] TAG_DF_ADF_LABEL = {(byte)0x50};
  public static final byte [] TAG_DF_ADF_PREFERRED_NAME = {(byte)0x9F, (byte)0x12};
  public static final byte [] TAG_DF_ADF_PRIORITY = {(byte)0x87};

  // refer to EMV 4.1 Book 3 - Annex A - Data Elements Dictionary
  public static final byte [] TAG_TRACK2_EQUIV_DATA = {(byte)0x57};
  public static final byte [] TAG_CARDHOLDER_NAME = {(byte)0x5F, (byte)0x20};

}
