package net.skora.eccardinfos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.jaccal.CardException;
import com.jaccal.CardResponse;
import com.jaccal.StatusWord;
import com.jaccal.command.Command;
import com.jaccal.util.NumUtil;

import net.skora.eccardinfos.error.Err;
import net.skora.eccardinfos.error.Errors;
import net.skora.eccardinfos.iso7816.Iso7816Commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import eu.ttbox.ecard.model.AflRecord;
import eu.ttbox.ecard.model.GetAFL;
import eu.ttbox.ecard.model.RecvTag;
import eu.ttbox.ecard.model.SelectApplication;
import eu.ttbox.ecard.util.ApplicationFileLocatorParser;
import eu.ttbox.ecard.util.AscciHelper;
import eu.ttbox.ecard.util.TLVParser;
import eu.ttbox.ecard.util.paycard.Emv41Enum;
import eu.ttbox.ecard.util.paycard.Emv41TypeEnum;
import eu.ttbox.ecard.util.paycard.PayCardTLVParser;
import eu.ttbox.io7816.Application;
import eu.ttbox.io7816.PseDirectory;

/**
 * http://blog.saush.com/2006/09/08/getting-information-from-an-emv-chip-card/
 * http://www.nfc.cc/2012/04/02/android-app-reads-paypass-and-paywave-creditcards/
 * <p/>
 * http://www.acbm.com/inedits/cartes-bancaires-sans-contact.html
 * <p/>
 * JS: http://www.openscdp.org/scripts/tutorial/emv/index.html
 */
public class CreditCardInfosActivity extends Activity {
    // Dialogs
    private static final int DIALOG_NFC_NOT_AVAIL = 0;

    private static final String LOGTAG = "CreditCardInfosActivity";

    private NfcAdapter nfc;
    private Tag tag;
    private IsoDep tagcomm;
    //private String[][] nfctechfilter = new String[][]{new String[]{NfcA.class.getName()}};
    private String[][] nfctechfilter = new String[][]{new String[]{IsoDep.class.getName()}};
    private PendingIntent nfcintent;

    private ListView nfcid;
    private ArrayAdapter<String> adpater;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console);

        nfcid = (ListView) findViewById(R.id.textView_card);
        adpater = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        nfcid.setAdapter(adpater);


        nfc = NfcAdapter.getDefaultAdapter(this);
        if (nfc == null) {
            showDialog(DIALOG_NFC_NOT_AVAIL);
        }
        nfcintent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfc.enableForegroundDispatch(this, nfcintent, null, nfctechfilter);
        // adpater.clear();
        addText("Ready to Scan");
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfc.disableForegroundDispatch(this);
    }

    private void addText(String text) {
        log(text);
        adpater.add(text);
    }

    private void addText(String key, String value) {
        adpater.add(key + " = " + value);
    }

    private void addTextSeparation() {
        // log("---------------------------------------------------------------------");
        addText("---------------------------------------------------------------------");
    }


    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        log("Tag detected!");
        adpater.clear();


        byte[] id = tag.getId();
        addText("Tag Id", SharedUtils.byte2Hex(id));

        tagcomm = IsoDep.get(tag);
        if (tagcomm == null) {
            toastError(getResources().getText(R.string.error_nfc_comm));
            return;
        }
        try {
            tagcomm.connect();
        } catch (IOException e) {
            toastError(getResources().getText(R.string.error_nfc_comm_cont) + (e.getMessage() != null ? e.getMessage() : "-"));
            return;
        }

        try {
            // Exemple : https://code.google.com/p/javaemvreader/wiki/ExampleOutput

            //selectMasterFile( );

            PseDirectory pseDirectory = selectPseDirectory();
            log(pseDirectory.toString());
            // SFI data
            Application app = readPseRecord(pseDirectory);
            // Aid  Record
            readAllAidRecord(pseDirectory);

//            SelectApplication selectApp = selectApplication(app);

//            GetAFL afl = getGetProcessingOptions(selectApp);
            // getRecordInformation(afl);

            //6F 1A = 26
            // 84 0E = 14
            //  31 50 41 59 2E 53 59 53 2E 44 44 46 30 31
            // A5 08 =8
            // 88 01 01
            // 5F 2D  = key language
            // 02 65 6E
            tagcomm.close();
        } catch (IOException e) {
            toastError(getResources().getText(R.string.error_nfc_comm_cont) + (e.getMessage() != null ? e.getMessage() : "-"));
        }
    }

    private PseDirectory selectPseDirectoryNavigo() throws IOException {
        // https://github.com/pterjan/cardpeek-navigo/tree/master/dot_cardpeek_dir/scripts
        String fileName = "1ADDF010";
        return selectPseDirectory(fileName);
    }

    private PseDirectory selectPseDirectory() throws IOException {
        // http://dexterous-programmer.blogspot.fr/2012/04/emv-transaction-step-1-application.html
        //In case the card is an NFC card then it will have PPSE (Paypass Payment System Environment) as
        // "2PAY.SYS.DDF01" and not "1PAY.SYS.DDF01"
        //  String fileName = "1PAY.SYS.DDF01";
        String fileName = "2PAY.SYS.DDF01";
        // String fileName = "1ADDF010";
        // new ISOSelect(ISOSelect.SELECT_AID, EMV4_1.AID_1PAY_SYS_DDF01);

        return selectPseDirectory(fileName);
    }

    private void selectMasterFile() throws IOException {
        // [Step 1] Select 1PAY.SYS.DDF01 to get the PSE directory
        addTextSeparation();
        addText("[Step 0] SELECT FILE Master File (if available)");
        addTextSeparation();


        CardResponse fcp = transceive( "00 A4 04 00");
        byte[] recv = fcp.getData();
        StatusWord sw = fcp.getStatusWord();


    }

    /**
     * http://www.openscdp.org/scripts/tutorial/emv/Application%20Selection.html
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    private PseDirectory selectPseDirectory(String fileName) throws IOException {
        // [Step 1] Select 1PAY.SYS.DDF01 to get the PSE directory
        addTextSeparation();
        addText("[Step 1] Select " + fileName + " to get the PSE directory");
        addTextSeparation();

        byte[] fileNameAsBytes = AscciHelper.toAsciiString2Bytes(fileName);
        String fileNameSize = NumUtil.toHexString(new byte[]{(byte) fileNameAsBytes.length});
        String fileNameAsHex = NumUtil.toHexString(fileNameAsBytes);
//        byte[] recv = transceive("00 A4 04 00 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31");
        String cmd = "00 A4 04 00 " + fileNameSize + " " + fileNameAsHex + " 00";

        CardResponse card = transceive(cmd);
        byte[] recv = card.getData();

        //addText("[Step 1] Select 2PAY.SYS.DDF01 to get the PSE directory");
        //log("[Step 1] Select 2PAY.SYS.DDF01 to get the PSE directory");
        //byte[] recv = transceive("00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00");

        addText(NumUtil.toHexString(recv));

        // Parse Pse Direcory
        // -------------------
        HashMap<RecvTag, byte[]> parsedRecv = PayCardTLVParser.parsePayCardTVLInDept(recv);
        addText(parsedRecv);

        // DF Name
        byte[] dfName = TLVParser.getTlvValue(parsedRecv, "84");
        String dfNameString = AscciHelper.toAsciiByte2String(dfName);
        log("DF Name : " + NumUtil.toHexString(dfName) + " ==> " + dfNameString);
        addText("DF Name", dfNameString);


        byte[] sfi = TLVParser.getTlvValue(parsedRecv, "88");
        byte[] lang = TLVParser.getTlvValue(parsedRecv, "5F2D");
        String langValue = AscciHelper.toAsciiByte2String(lang);
        addText("Lang", langValue);
        addText("sfi", NumUtil.toHexString(sfi));

        PseDirectory result = new PseDirectory(parsedRecv);
        result.lang = langValue;
        result.dfName = dfNameString;
        result.fsi = sfi[0];

        log("[Step 1] END");
        return result;
    }

    /**
     * http://dexterous-programmer.blogspot.fr/2012/04/emv-transaction-step-1-application.html
     */
    public void readAllAidRecord(PseDirectory pseDirectory) throws IOException {
        byte[] aid = pseDirectory.getAid();
        if (aid != null) {
            String aidSize = NumUtil.toHexString(new byte[]{(byte) aid.length});
            String aidAsHex = NumUtil.toHexString(aid);

            addTextSeparation();
            addText("[Step 1] Select Aid " + aidAsHex  );
            addTextSeparation();

            String cmd = "00 A4 04 00 " + aidSize + " " + aidAsHex + " 00";
            CardResponse card = transceive(cmd);

            // TODO PayCardTLVParser
            PayCardTLVParser aidResponse =  new PayCardTLVParser(card);
            log(aidResponse);

            if (card.isSuccess()) {
                addTextSeparation();
                addText("[Step 1] Read All Aid Record of Aid " + aidAsHex  );
                addTextSeparation();

                for (int sfi = 1; sfi <= 31; sfi++) {
                    for (int rec = 1; rec <= 16; rec++) {
                        byte[] readCmd = new byte[] {0x00, (byte)0xB2, (byte)rec, (byte)((sfi << 3) | 4), 0x00 };
                        CardResponse tlv = transceive(readCmd);
                        if (tlv.isSuccess()) {
                           addText("SFI " + sfi  + " record #" + rec);
                           byte[] tlvData =  tlv.getData();
                           HashMap<RecvTag, byte[]> record =  PayCardTLVParser.parsePayCardTVLInDept(tlvData);
                           log(record);
                        }
                    }
                }
            }
        }

    }


    public Application readPseRecord(PseDirectory pseDirectory) throws IOException {
        // TODO select in funtion of EMV_v4.3_Book_1_ICC_to_Terminal_Interface_2012060705394541.pdf page 129
        // TODO http://dexterous-programmer.blogspot.fr/2012/04/emv-transaction-step-1-application.html
        addTextSeparation();
        addText("[Step 2] Send READ RECORD with 0 to find out where the record is");
        addTextSeparation();

        String sfi = NumUtil.hex2String((byte) ((pseDirectory.fsi << 3) | 4));
        String cmd = "00 B2 01 " + sfi + " 00";
        CardResponse card = transceive(cmd);
        byte[] recv = card.getData();

        addText(NumUtil.toHexString(recv));

        // Parse Read Pse Record
        // -------------------
        //  recv[0] == 0x70
        //  recv[1] == Lenght
        byte[] application = Arrays.copyOfRange(recv, 2, 2 + recv[1]);
        log("Application : " + NumUtil.toHexString(application));

        // Lenght of directory entry 1
        //  application[0] == 0x61
        byte lenghtDirectoryOne = application[1];
        // Application Id
        //  application[2] == 0x4F
        byte appIdSize = application[3];
        //  addText("appIdSize", NumUtil.toHexString(new byte[]{application[3]}));
        int appIdLenght = 4 + appIdSize;
        byte[] appId = Arrays.copyOfRange(application, 4, appIdLenght);
        addText("App ID", NumUtil.toHexString(appId));

        // application[appIdLenght+1] == 0x50
        byte appLabelSize = application[appIdLenght + 1];
        //  addText("appLabelSize", NumUtil.toHexString(new byte[]{appLabelSize}));
        int appLabelLenght = appIdLenght + appLabelSize + 2;

        byte[] appLabel = Arrays.copyOfRange(application, appIdLenght + 2, appLabelLenght);
        String appLabelString = AscciHelper.toAsciiByte2String(appLabel);
        // addText("App Label", NumUtil.toHexString(appLabel));
        addText("App Label", appLabelString);

        Application app = new Application();
        app.appLabel = appLabelString;
        app.appid = appId;
        return app;
    }

    public SelectApplication selectApplication(Application app) throws IOException {
        addTextSeparation();
        addText("[Step 4] Now that we know the AID, select the application");
        addTextSeparation();

        CardResponse card = transceive("00 A4 04 00 07 " + NumUtil.toHexString(app.appid));
        byte[] recv = card.getData();
        addText(NumUtil.toHexString(recv));

        HashMap<RecvTag, byte[]> parsedRecv = PayCardTLVParser.parsePayCardTVLInDept(recv);
        addText(parsedRecv);

        // Parse FCI Template
        // -------------------
        // DF Name
        byte[] dfName = TLVParser.getTlvValue(parsedRecv, Emv41Enum.DF_FCI_NAME);
        String dfNameString = null;//AscciHelper.toAsciiByte2String(dfName);
        log("DF Name : " + NumUtil.toHexString(dfName) + " ==> " + dfNameString);
        //DF Name : A0 00 00 00 42 10 10 ==> null
        //addText("DF Name", dfNameString);

        // Parse FCI Proprietary
        // -------------------
        byte[] appLabel = TLVParser.getTlvValue(parsedRecv, Emv41Enum.DF_ADF_LABEL);
        byte[] appPriorityIndicator = TLVParser.getTlvValue(parsedRecv, Emv41Enum.DF_ADF_PRIORITY);
        byte[] lang = TLVParser.getTlvValue(parsedRecv, Emv41Enum.DF_FCI_LANG);
        byte[] appPrefName = TLVParser.getTlvValue(parsedRecv, Emv41Enum.DF_ADF_PREFERRED_NAME);
        String appPrefNameString = AscciHelper.toAsciiByte2String(appPrefName);
        addText("App Pref Name", appPrefNameString);
        byte[] iserDicretionnarayData = TLVParser.getTlvValue(parsedRecv, "BF0C");

        // Parse PDOL
        // -------------------
        byte[] pdol = TLVParser.getTlvValue(parsedRecv, Emv41Enum.PDOL);
        log("PDOL : " + NumUtil.toHexString(pdol));
        addText("PDOL", NumUtil.toHexString(pdol));

        SelectApplication selectApp = new SelectApplication();
        selectApp.pdol = pdol;
        return selectApp;
    }

    public GetAFL getGetProcessingOptions(SelectApplication selectApp) throws IOException {
        //http://stackoverflow.com/questions/15059580/reading-emv-card-using-ppse-and-not-pse
        // http://www.acbm.com/inedits/cartes-bancaires-sans-contact.html
        addTextSeparation();
        addText("[Step 5] Send GET PROCESSING OPTIONS command");
        addTextSeparation();

        byte[] pdolTlv = new byte[]{(byte) 0x83};
        if (selectApp != null && selectApp.pdol != null) {
            byte[] pdolValues = selectApp.generatePdolRequestData();
            // Pdol TLV
            pdolTlv = new byte[pdolValues.length + 2];
            pdolTlv[0] = (byte) 0x83;
            pdolTlv[1] = (byte) pdolValues.length;
            System.arraycopy(pdolValues, 0, pdolTlv, 2, pdolValues.length);
//            pdol = "83 " + NumUtil.toHexString(new byte[] {(byte)pdolValues.length}) + " "  + NumUtil.toHexString(pdolValues);
        }
        // Lc-Data-Le
        byte[] lcDataLe = new byte[pdolTlv.length + 2];
        lcDataLe[0] = (byte) (pdolTlv.length);
        lcDataLe[lcDataLe.length - 1] = 0x00;
        System.arraycopy(pdolTlv, 0, lcDataLe, 1, pdolTlv.length);


        //       80A800000483020804
        CardResponse card = transceive("80 A8 00 00 " + NumUtil.toHexString(lcDataLe));
        byte[] recv = card.getData();
        // byte[] recv = transceive("80 A8 00 00 " + pdol + " 00");
        //     byte[] recv = transceive("80 A8 00 00 21 83 " +  pdol+ " 00" );
        addText(NumUtil.toHexString(recv));

        GetAFL afl = ApplicationFileLocatorParser.parseAFL(recv);
        return afl;
        //
    }

    public void getRecordInformation(GetAFL afl) throws IOException {
        addTextSeparation();
        addText("[Step 6] Send READ RECORD with 0 to find out where the record is");
        addTextSeparation();

        for (AflRecord record : afl.records) {
            int sfi = record.sfi;
            int begin = record.recordNumberBegin;
            byte[] cmd = Iso7816Commands.readRecord(begin, sfi);
            CardResponse card = transceive(cmd);
            byte[] recv = card.getData();
            addText(NumUtil.toHexString(recv));
        }

    }


    private void log(PayCardTLVParser parsed) {
        log(parsed.getParsed());
    }

    private void log(HashMap<RecvTag, byte[]> parsed) {
        for (Map.Entry<RecvTag, byte[]> entry : parsed.entrySet()) {
            log("" + NumUtil.hex2String(entry.getKey().key) + " = " + NumUtil.toHexString(entry.getValue()));
        }
    }

    private void addText(HashMap<RecvTag, byte[]> parsed) {
        for (Map.Entry<RecvTag, byte[]> entry : parsed.entrySet()) {
            RecvTag tag = entry.getKey();
            byte[] tagValue = entry.getValue();
            // Search Label
            Emv41Enum emv = Emv41Enum.getByTag(tag);
            String keyLabel;
            String valueLabel;
            if (emv == null) {
                keyLabel = tag.toString();
                valueLabel = Emv41TypeEnum.UNNKOWN.toString(tagValue);
            } else {
                keyLabel = emv.name() + "(" + NumUtil.hex2String(tag.key) + ")";
                valueLabel = emv.toString(tagValue);
            }
            log(keyLabel + " = " + valueLabel);
            addText(keyLabel, valueLabel);
        }
    }


    private void readMasterFileByIdentifier() {
        log("Select Master File ");
        try {
            byte[] cmd = Iso7816Commands.selectMasterFileByIdentifier();
            CardResponse card = transceive(cmd);
            byte[] recv = card.getData();
        } catch (IOException e) {
            Log.e(LOGTAG, "Error transmiting : " + e.getMessage());
        }

    }

    private void readAllRecord() {
        for (int sfi = 0; sfi <= 30; sfi++) {
            readAllRecord(sfi);
        }
    }

    private void readAllRecord(int sfi) {
        ArrayList<String> responses = new ArrayList<String>();
        int recordLimit = 255; //= 2; //
        for (int i = 1; i <= recordLimit; i++) {
            try {
                log("Read Record : " + i);
                //  for (int sfi = 0; sfi<=30; sfi++) {
                //  int sfi = 1;
                byte[] cmd = Iso7816Commands.readRecord(i, sfi);
                CardResponse card = transceive(cmd);
                byte[] recv = card.getData();
                if (recv.length > 2) {
                    responses.add("--- record=" + i + "/ sfi=" + sfi + " : " + SharedUtils.byte2Hex(cmd)
                            + " ==> (" + recv.length + ")" + SharedUtils.byte2Hex(recv) //
                            + "\n ==> " + new String(Arrays.copyOfRange(recv, 0, recv.length - 2), "ISO-8859-1") //
                    );
                }
                //  }
            } catch (IOException e) {
                Log.e(LOGTAG, "Error transmiting : " + e.getMessage());
            }
        }
        // print reposne
        for (String response : responses) {
            log(response);
        }
    }

    public CardResponse execute(Command command) throws CardException {
        CardResponse res = null;
        try {
              res = transceive(command.getBytes());
        } catch (IOException e) {
            throw new CardException(e);
        }
        return res;
    }

    protected CardResponse transceive(String hexstr) throws IOException {
        String[] hexbytes = hexstr.split("\\s");
        byte[] bytes = new byte[hexbytes.length];
        for (int i = 0; i < hexbytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexbytes[i], 16);
        }
        return transceive(bytes);
    }

    protected CardResponse transceive(byte[] bytes) throws IOException {
        log("Send: " + SharedUtils.byte2Hex(bytes));
        byte[] recv = tagcomm.transceive(bytes);

        // Create CardResponse
        CardResponse res = new CardResponse();
        StatusWord sw = new StatusWord(recv[recv.length - 2], recv[recv.length - 1]);
        res.setData(Arrays.copyOfRange(recv, 0, recv.length - 2));
        res.setStatusWord(sw);

        // --> error list http://www.eftlab.co.uk/index.php/site-map/knowledge-base/118-apdu-response-list
        // Parse Error
        ArrayList<Err> errors = Errors.getError(recv);
        if (!errors.isEmpty()) {
            for (Err err : errors) {
                log("Received: " + SharedUtils.byte2Hex(recv) + " ==> " + err);
            }
        } else {
            log("Received: " + SharedUtils.byte2Hex(recv));
        }
        if (recv.length > 2) {
            log("Received: " + AscciHelper.toAsciiByte2String(TLVParser.getData(recv)));
        }
        return res;
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog;

        switch (id) {
            case DIALOG_NFC_NOT_AVAIL:
                dialog = new AlertDialog.Builder(this)
                        .setMessage(getResources().getText(R.string.error_nfc_not_avail))
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CreditCardInfosActivity.this.finish();
                            }
                        })
                        .create();
                break;
            default:
                dialog = null;
                break;
        }

        return dialog;
    }

    private View.OnClickListener transactions_click = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(CreditCardInfosActivity.this, TransactionsActivity.class);
            try {
                // Read all EF_BLOG records
                for (int i = 1; i <= 15; i++) {
                    CardResponse  res = transceive(String.format("00 B2 %02x EC 00", i));
                    byte[] recv = res.getBytes();
                    intent.putExtra(String.format("blog_%d", i), recv);
                }
                startActivity(intent);
            } catch (IOException e) {
                toastError(getResources().getText(R.string.error_nfc_comm_cont) + (e.getMessage() != null ? e.getMessage() : "-"));
            }
        }

    };


    protected void log(String msg) {
        Log.d(LOGTAG, msg);
    }

    protected void toastError(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}