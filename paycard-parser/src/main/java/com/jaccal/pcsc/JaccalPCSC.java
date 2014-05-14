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
package com.jaccal.pcsc;

import com.jaccal.util.NumUtil;

/**
 * @author Thomas Tarpin-Lyonnet
 *         <p/>
 *         <p>JaccalPCSC class provides a set of native methods allowing to power on, power down and
 *         send APDU command to a card. JaccalPCSC class is in charge of establish the context with
 *         the system and in charge of getting the list of the smart card readers installed in the system.</p>
 *         <p>How to use JaccalPCSC ?</p>
 *         Example:<br>
 *         <code><pre>
 *         try {
 *         	String[] readerList;
 *         <p/>
 *         	// Create a JaccalPCSC object and initialize it
 *         	// Initialisation means:
 *         	// - Allocating enough memory for storing all the reader names
 *         	// attached to the system
 *         	// - Establishing a context with the system
 *         	// - Retrieving the list of all the readers attached to the system
 *         	JaccalPCSC jaccalPcsc = new JaccalPCSC();
 *         <p/>
 *         	// Select MF APDU command
 *         	byte [] selectMF = {0x00,0xA4,0x00,0x00,0x02,0x3F,0x00};
 *         <p/>
 *         	// Retrieve the list of the readers bind to the system
 *         	readerList = jaccalPcsc.getReaderList();
 *         <p/>
 *         	// Connect to the first reader in the list
 *         	// (power on the card inserted in the first reader in the list
 *         	jaccalPcsc.connectCard(readerList[0],PCSC.SCARD_SHARE_SHARED);
 *         <p/>
 *         	// Send the select APDU command
 *         	byte [] resp = jaccalPcsc.transmitApdu(selectMF,0,selectMF.length);
 *         <p/>
 *         	// Power Off the card
 *         	jaccalPcsc.disconnectCard(PCSC.SCARD_UNPOWER_CARD);
 *         <p/>
 *         } catch (JaccalPCSCException e) {
 *         	System.out.println(e.getMessage());
 *         }
 *         </pre></code>
 */
public class JaccalPCSC {
  // Variables that needs to be unique for all the instances since
  // they are unique for one given system
  private static long contextHandle = 0;
  private static String[] readerList = new String[30];

  // These two variables are specific to one given session opened
  // A session will be then recognize mainly thanks to the name of the
  // card reader where the card has been inserted
  private long cardHandle;
  private String cardReader;

  // The card ATR
  private byte[] cardAtr;
  private long cardProtocol;

  // For chaining automatic get response, default value is true
  private boolean t0GetResponse = true;

  // Declaration of the native functions
  private native int establishContext(int dwScope);

  private native int connect(String readerName, int dwShareMode, long dwProtocol);

  private native int disconnect(int dwDisposition);

  private native int getReadersList();

  private native byte[] transmit(byte[] apduCmd, int offset, int cmdLen, byte[] errorCode);

  private native int getStatusChange();

  // Load the C library
  static {
    System.loadLibrary("jaccal-pcsc");
  }

  /**
   * Allocate enough space to contain 20 smart card readers in the cardReaderList
   */
  public JaccalPCSC() throws JaccalPCSCException {
    initialize();
  }

  /**
   * Initialize the context and get the list of the smart card readers bind to the system
   *
   * @throws JaccalPCSCException
   */
  private void initialize() throws JaccalPCSCException {
    int theRetValue;

    // The context handle equal to 0 means that it has not been initialised
    if (contextHandle == 0) {
      // Establish the context with the system and retrieve the list of readers
      // that will be used to populate readerList class variable
      theRetValue = establishContext(PCSC.SCARD_SCOPE_SYSTEM);
      if (theRetValue != 0)
        throw new JaccalPCSCException("PCSC Error (EstablishContext): 0x" + NumUtil.int2HexString(theRetValue));
    }
    
    theRetValue = getReadersList();
    if (theRetValue != 0)
      throw new JaccalPCSCException("PCSC Error (getReaderList): 0x" + NumUtil.int2HexString(theRetValue));
  }

  /**
   * Power on the card inserted in the reader name given in parameters.
   * shareMode can take the following values: SCARD_SHARE_EXCLUSIVE, SCARD_SHARE_SHARED or SCARD_SHARE_DIRECT
   *
   * @param readerName
   * @param shareMode
   * @throws JaccalPCSCException
   */
  public void connectCard(String readerName, int shareMode) throws JaccalPCSCException {
    if (readerName == null)
      throw new JaccalPCSCException("(connectCard) readerName buffer is null");
    if ((shareMode != PCSC.SCARD_SHARE_EXCLUSIVE) && (shareMode != PCSC.SCARD_SHARE_SHARED)
        && (shareMode != PCSC.SCARD_SHARE_DIRECT))
      throw new JaccalPCSCException("(connectCard) invalid parameter: set shareMode to SCARD_SHARE_EXCLUSIVE, SCARD_SHARE_SHARED or SCARD_SHARE_DIRECT");

    int theRetValue;

    theRetValue = connect(readerName, shareMode, PCSC.SCARD_PROTOCOL_T0 | PCSC.SCARD_PROTOCOL_T1);
    if (theRetValue != 0)
      throw new JaccalPCSCException("PCSC Error (connect): 0x" + NumUtil.int2HexString(theRetValue));

    theRetValue = getStatusChange();
    if (theRetValue != 0)
      throw new JaccalPCSCException("PCSC Error (getStatusChange): 0x" + NumUtil.int2HexString(theRetValue));
  }

  /**
   * Power on the card inserted in the reader name given in parameters.
   * <ul>
   * <li>shareMode can take the following values: SCARD_SHARE_EXCLUSIVE, SCARD_SHARE_SHARED or SCARD_SHARE_DIRECT</li>
   * <li>preferedProtocol can take the following values: SCARD_PROTOCOL_UNDEFINED, SCARD_PROTOCOL_T0, SCARD_PROTOCOL_T1,
   * SCARD_PROTOCOL_RAW, SCARD_PROTOCOL_DEFAULT</li>
   * </ul>
   *
   * @param readerName
   * @param shareMode
   * @param preferedProtocol
   * @throws JaccalPCSCException
   */
  public void connectCard(String readerName, int shareMode, long preferedProtocol) throws JaccalPCSCException {
    if (readerName == null)
      throw new JaccalPCSCException("(connectCard) readerName buffer is null");
    if ((shareMode != PCSC.SCARD_SHARE_EXCLUSIVE) && (shareMode != PCSC.SCARD_SHARE_SHARED)
        && (shareMode != PCSC.SCARD_SHARE_DIRECT))
      throw new JaccalPCSCException("(connectCard) invalid parameter: set shareMode to SCARD_SHARE_EXCLUSIVE, SCARD_SHARE_SHARED or SCARD_SHARE_DIRECT");
    if ((preferedProtocol != PCSC.SCARD_PROTOCOL_UNDEFINED) && (preferedProtocol != PCSC.SCARD_PROTOCOL_T0)
        && (preferedProtocol != PCSC.SCARD_PROTOCOL_T1) && (preferedProtocol != PCSC.SCARD_PROTOCOL_RAW)
        && (preferedProtocol != PCSC.SCARD_PROTOCOL_DEFAULT))
      throw new JaccalPCSCException("(connectCard) invalid parameter: set preferedProtocole to SCARD_PROTOCOL_UNDEFINED, SCARD_PROTOCOL_T0, SCARD_PROTOCOL_T1, SCARD_PROTOCOL_RAW or SCARD_PROTOCOL_DEFAULT");
    int theRetValue;

    theRetValue = connect(readerName, shareMode, preferedProtocol);
    if (theRetValue != 0)
      throw new JaccalPCSCException("PCSC Error (connect): 0x" + NumUtil.int2HexString(theRetValue));

    theRetValue = getStatusChange();
    if (theRetValue != 0)
      throw new JaccalPCSCException("PCSC Error (getStatusChange): 0x" + NumUtil.int2HexString(theRetValue));
  }

  /**
   * Power on the card in the reader name that has been set previously.
   * shareMode can take the following values: SCARD_SHARE_EXCLUSIVE, SCARD_SHARE_SHARED or SCARD_SHARE_DIRECT
   *
   * @param shareMode
   * @throws JaccalPCSCException
   */
  public void connectCard(int shareMode) throws JaccalPCSCException {
    connectCard(this.cardReader, shareMode);
  }

  /**
   * Power off the card.
   *
   * @param swDisposition tells what kind of power off to perform. Can be SCARD_LEAVE_CARD, SCARD_RESET_CARD, SCARD_UNPOWER_CARD or SCARD_EJECT_CARD
   * @throws JaccalPCSCException
   */
  public void disconnectCard(int swDisposition) throws JaccalPCSCException {

    if ((swDisposition != PCSC.SCARD_LEAVE_CARD) && (swDisposition != PCSC.SCARD_RESET_CARD)
        && (swDisposition != PCSC.SCARD_UNPOWER_CARD) && (swDisposition != PCSC.SCARD_EJECT_CARD))
      throw new JaccalPCSCException("(disconnectCard) Invalid parameter: set swDisposition to SCARD_LEAVE_CARD, SCARD_RESET_CARD, SCARD_UNPOWER_CARD or SCARD_EJECT_CARD");

    int theRetValue;

    theRetValue = disconnect(swDisposition);
    if (theRetValue != 0)
      throw new JaccalPCSCException("PCSC Error (disconnect): 0x" + NumUtil.int2HexString(theRetValue));
  }

  /**
   * Get the status of the card (inserted, removed etc ...)
   *
   * @throws JaccalPCSCException
   */
  public void getStatus() throws JaccalPCSCException {
    int theRetValue;

    theRetValue = getStatusChange();
    if (theRetValue != 0)
      throw new JaccalPCSCException("PCSC Error (getStatusChange): 0x" + NumUtil.int2HexString(theRetValue));
  }

  /**
   * Send a APDU command as defined in ISO7816-4 to the card that has been powered on by a previous connectCard call.
   *
   * @param cmd
   * @param offsetInCmdBuff
   * @param cmdLen
   * @return The card response composed of the response data + the card status words (SW1 & SW2)
   * @throws JaccalPCSCException
   */
  public byte[] transmitApdu(byte[] cmd, int offsetInCmdBuff, int cmdLen) throws JaccalPCSCException {
    if (cmd == null)
      throw new JaccalPCSCException("(transmitApdu) cmd buffer is null");
    if ((cmdLen < 0) || (cmdLen > cmd.length))
      throw new JaccalPCSCException("(transmitApdu) bad value for cmdLen");

    byte[] receivedData;
    byte[] pcscError = new byte[4];

    receivedData = transmit(cmd, offsetInCmdBuff, cmdLen, pcscError);
    if (receivedData == null)
      throw new JaccalPCSCException("PCSC Error (transmit): 0x" + NumUtil.hex2String(pcscError));

    if (t0GetResponse) {
    	// Check if get response is needed
    	if (NumUtil.getUnsignedValue(receivedData[0]) == 0x61) {
    		// Build the default Get Response command
    		byte[] getRespCmd = {(byte) 0x00, (byte) 0xC0, (byte) 0x00, (byte) 0x00, (byte) 0x00};
    		
    		// Set the length to be retrieved from the card IO buffer
    		getRespCmd[4] = receivedData[1];
    		// reset receivedData buffer
    		receivedData = null;
    		// Send Get Response command
    		receivedData = transmit(getRespCmd, 0, getRespCmd.length, pcscError);
    		if (receivedData == null)
    			throw new JaccalPCSCException("PCSC Error (transmit): 0x" + NumUtil.hex2String(pcscError));

      }
    }

    return receivedData;
  }

  /**
   * Returns the list of the smart card readers attached to the system.
   *
   * @return Returns the readerList.
   */
  public String[] getReaderList() {
	  int nbReaders = 0;
	  
	  while (JaccalPCSC.readerList[nbReaders] != null) {
		  nbReaders++;
	  }
	  
	  String[] locReaderList = new String[nbReaders];
	  
	  for (int i = 0; i < nbReaders; i++) {
		  locReaderList[i] = JaccalPCSC.readerList[i];
	  }
	  return locReaderList;
  }

  /**
   * Returns the handle of the card currently powered on
   *
   * @return Returns the cardHandle.
   */
  public long getCardHandle() {
    return cardHandle;
  }

  /**
   * Returns the ATR of the card currently powered on.
   *
   * @return Returns the cardAtr.
   */
  public byte[] getCardAtr() {
    return cardAtr;
  }

  /**
   * @param readerList The readerList to set.
   */
  /*private static void setReaderList(String[] readerList) {
    JaccalPCSC.readerList = readerList;
  }

  private void adjustReaderList() {
    int nbReaders = 0;

    while (JaccalPCSC.readerList[nbReaders] != null) {
      nbReaders++;
    }

    String[] locReaderList = new String[nbReaders];

    for (int i = 0; i < nbReaders; i++) {
      locReaderList[i] = JaccalPCSC.readerList[i];
    }
    setReaderList(locReaderList);
  }*/

  /**
   * Set the name of the JaccalPCSC active reader
   *
   * @param cardReader
   */
  public void setCardReader(String cardReader) {
    this.cardReader = cardReader;
  }

  /**
   * Get the name of the JaccalPCSC active reader
   *
   * @return The active reader name
   */
  public String getCardReader() {
    return cardReader;
  }

  /**
   * Indicates if Get Response apdu command is automatically chained whenever <br>
   * the card status words are 61xxh with xxh the number of bytes available in the card <br>
   * IO buffer.
   *
   * @return Returns the t0GetResponse.
   */
  public boolean isT0GetResponse() {
    return t0GetResponse;
  }

  /**
   * Set or unset the automatic chaining of Get Reponse APDU command whenever its needed.
   *
   * @param getResponse The t0GetResponse to set.
   */
  public void setT0GetResponse(boolean getResponse) {
    t0GetResponse = getResponse;
  }
}
