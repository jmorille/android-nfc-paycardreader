
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
import eu.ttbox.ecard.util.ApplicationFileLocatorParser;
import eu.ttbox.ecard.util.AscciHelper;
import eu.ttbox.ecard.util.TLVParser;
import eu.ttbox.io7816.Application;
import eu.ttbox.io7816.PseDirectory;
import eu.ttbox.ecard.model.SelectApplication;

/**
 * http://blog.saush.com/2006/09/08/getting-information-from-an-emv-chip-card/
 * http://www.nfc.cc/2012/04/02/android-app-reads-paypass-and-paywave-creditcards/
 *
 * http://www.acbm.com/inedits/cartes-bancaires-sans-contact.html
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
        adpater.add(text);
    }

    private void addText(String key, String value) {
        adpater.add(key + " = " + value);
    }

    private void addTextSeparation() {
        addText("___________________________");
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

            PseDirectory pseDirectory = selectPseDirectory();
            log(pseDirectory.toString());

            Application app = readPseRecord(pseDirectory);
            SelectApplication selectApp =  selectApplication(app);

            GetAFL afl = getGetProcessingOptions(selectApp);
            getRecordInformation(  afl);
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

    private PseDirectory selectPseDirectory() throws IOException {
        // [Step 1] Select 1PAY.SYS.DDF01 to get the PSE directory
        addText("[Step 1] Select 1PAY.SYS.DDF01 to get the PSE directory");
         log("[Step 1] Select 1PAY.SYS.DDF01 to get the PSE directory");
         byte[] recv = transceive("00 A4 04 00 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31");

        //addText("[Step 1] Select 2PAY.SYS.DDF01 to get the PSE directory");
        //log("[Step 1] Select 2PAY.SYS.DDF01 to get the PSE directory");
        //byte[] recv = transceive("00 A4 04 00 0E 32 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00");

        addText(NumUtil.toHexString(recv));

        // Parse Pse Direcory
        // -------------------
        HashMap<RecvTag, byte[]> parsedRecv = TLVParser.parseTVL(recv);
        byte[] fciTemplate = TLVParser.getTlvValue(parsedRecv, "6F");
        log("FCI Template : " + NumUtil.toHexString(fciTemplate));
        //addText("FCI Template", NumUtil.toHexString(fciTemplate));

        // Parse FCI Template
        // -------------------
       // addText("Parse FCI Template");
        log("Parse FCI Template");
        HashMap<RecvTag, byte[]> parsedFciTemplate = TLVParser.parseTVL(fciTemplate);
        log(parsedFciTemplate);
        //addText(parsedFciTemplate);


        // DF Name
        byte[] dfName = TLVParser.getTlvValue(parsedFciTemplate, "84");
        String dfNameString = AscciHelper.toAscciString(dfName);
        log("DF Name : " + NumUtil.toHexString(dfName) + " ==> " + dfNameString);
        addText("DF Name", dfNameString);

        // Parse FCI Proprietary
        // -------------------
        byte[] fciProprietary = TLVParser.getTlvValue(parsedFciTemplate, "A5");
        log("FCI Proprietary : " + NumUtil.toHexString(fciProprietary));
        //addText("FCI Proprietary", NumUtil.toHexString(fciProprietary));

       // addText("Parse FCI Proprietary");
        HashMap<RecvTag, byte[]> parsedFciProprietary = TLVParser.parseTVL(fciProprietary);
        log(parsedFciProprietary);
        byte[] sfi = TLVParser.getTlvValue(parsedFciProprietary, "88");
        byte[] lang = TLVParser.getTlvValue(parsedFciProprietary, "5F2D");
        String langValue = AscciHelper.toAscciString(lang);
        addText("Lang", langValue);
        addText("sfi", NumUtil.toHexString(sfi));

        PseDirectory result = new PseDirectory();
        result.lang = langValue;
        result.dfName = dfNameString;
        result.fsi = sfi[0];

        log("[Step 1] END");
        return result;
    }


    public Application readPseRecord(PseDirectory pseDirectory) throws IOException {
        log("[Step 2] Send READ RECORD with 0 to find out where the record is");
        addText("[Step 2] Send READ RECORD with 0 to find out where the record is");
        String sfi = NumUtil.hex2String((byte) ((pseDirectory.fsi << 3) | 4));
        String cmd = "00 B2 01 " + sfi + " 00";
        byte[] recv = transceive(cmd);
        addText(NumUtil.toHexString(recv));

        // Parse Read Pse Record
        // -------------------
        //  recv[0] == 0x70
        //  recv[1] == Lenght
        byte[] application = Arrays.copyOfRange(recv, 2, 2+ recv[1] );
        log("Application : " + NumUtil.toHexString(application));

        // Lenght of directory entry 1
        //  application[0] == 0x61
        byte lenghtDirectoryOne  = application[1];
        // Application Id
        //  application[2] == 0x4F
        byte appIdSize = application[3];
      //  addText("appIdSize", NumUtil.toHexString(new byte[]{application[3]}));
        int appIdLenght = 4+appIdSize;
        byte[] appId = Arrays.copyOfRange(application, 4, appIdLenght);
        addText("App ID", NumUtil.toHexString(appId));

        // application[appIdLenght+1] == 0x50
        byte appLabelSize = application[appIdLenght+1];
      //  addText("appLabelSize", NumUtil.toHexString(new byte[]{appLabelSize}));
        int appLabelLenght = appIdLenght+appLabelSize+2;

        byte[] appLabel = Arrays.copyOfRange(application, appIdLenght+2, appLabelLenght);
        String appLabelString =  AscciHelper.toAscciString(appLabel);
       // addText("App Label", NumUtil.toHexString(appLabel));
        addText("App Label",appLabelString);

        Application app = new Application();
        app.appLabel =appLabelString;
        app.appid = appId;
        return app;
    }

    public SelectApplication selectApplication(Application app)  throws IOException  {
        addTextSeparation();
        log("[Step 4] Now that we know the AID, select the application");
        addText("[Step 4] Now that we know the AID, select the application");
        byte[] recv = transceive("00 A4 04 00 07 " +  NumUtil.toHexString(app.appid));
        addText(NumUtil.toHexString(recv));

        HashMap<RecvTag, byte[]> parsedRecv = TLVParser.parseTVL(recv);
        byte[] fciTemplate = TLVParser.getTlvValue(parsedRecv, "6F");
        log("FCI Template : " + NumUtil.toHexString(fciTemplate));
        //addText("FCI Template" , NumUtil.toHexString(fciTemplate));

        // Parse FCI Template
        // -------------------
        // addText("Parse FCI Template");
        log("Parse FCI Template");
        HashMap<RecvTag, byte[]> parsedFciTemplate = TLVParser.parseTVL(fciTemplate);
        log(parsedFciTemplate);
        //addText(parsedFciTemplate);

        // DF Name
        byte[] dfName = TLVParser.getTlvValue(parsedFciTemplate, "84");
         String dfNameString = null;//AscciHelper.toAscciString(dfName);
        log("DF Name : " + NumUtil.toHexString(dfName) + " ==> " + dfNameString);
        //DF Name : A0 00 00 00 42 10 10 ==> null
        //addText("DF Name", dfNameString);

        // Parse FCI Proprietary
        byte[] fciProprietary = TLVParser.getTlvValue(parsedFciTemplate, "A5");
        log("FCI Proprietary : " + NumUtil.toHexString(fciProprietary));
     //   addText("FCI Proprietary", NumUtil.toHexString(fciProprietary));

        // Parse FCI Proprietary
        // -------------------
        HashMap<RecvTag, byte[]> parsedFciProprietary = TLVParser.parseTVL(fciProprietary);
        log(parsedFciProprietary);
        byte[] appLabel = TLVParser.getTlvValue(parsedFciProprietary, "50");
        byte[] appPriorityIndicator = TLVParser.getTlvValue(parsedFciProprietary, "87");
          byte[] lang = TLVParser.getTlvValue(parsedFciProprietary, "5F2D");
        byte[] appPrefName= TLVParser.getTlvValue(parsedFciProprietary, "9F12");
        String appPrefNameString =  AscciHelper.toAscciString(appPrefName);
        addText("App Pref Name" , appPrefNameString);
        byte[] iserDicretionnarayData= TLVParser.getTlvValue(parsedFciProprietary, "BF0C");

        // Parse PDOL
        // -------------------
        byte[] pdol = TLVParser.getTlvValue(parsedFciProprietary, "9F38");
        log("PDOL : " + NumUtil.toHexString(pdol));
        addText("PDOL" , NumUtil.toHexString(pdol));

        SelectApplication selectApp = new SelectApplication();
        selectApp.pdol = pdol;
        if (pdol !=null) {
            //http://www.openscdp.org/scripts/tutorial/emv/initiateapplicationprocess.html
            ArrayList<RecvTag> parsedPdol = TLVParser.parseDataObjectList(pdol);
            int pdolSize = 0;
            for (RecvTag recvTag : parsedPdol) {
                pdolSize += recvTag.valueSize;
                addText("PDOL" ,recvTag.toString());
                log("PDOL : " + recvTag.toString());
            }
          //  addText("PDOL Size " ,pdolSize);
            log("PDOL Size : "  + pdolSize);
        }
        return selectApp;
    }

    public GetAFL getGetProcessingOptions(SelectApplication selectApp)  throws IOException  {
        addTextSeparation();
        //http://stackoverflow.com/questions/15059580/reading-emv-card-using-ppse-and-not-pse
        // http://www.acbm.com/inedits/cartes-bancaires-sans-contact.html
        log("[Step 5] Send GET PROCESSING OPTIONS command");
        addText("[Step 5] Send GET PROCESSING OPTIONS command");
        String pdol = "83 00";
        if (selectApp!=null && selectApp.pdol !=null) {
            ArrayList<RecvTag> parsedPdol = TLVParser.parseDataObjectList(selectApp.pdol);
            int pdolSize = 0;
            for (RecvTag recvTag : parsedPdol) {
                pdolSize += recvTag.valueSize;
            }

            pdol = NumUtil.toHexString(new byte[] {(byte)pdolSize}) + " "  + NumUtil.toHexString(selectApp.pdol);
        }
        byte[] recv = transceive("80 A8 00 00 02 " +  pdol);
        addText(NumUtil.toHexString(recv));

        GetAFL afl =  ApplicationFileLocatorParser.parseAFL(recv);
        return afl;
        //
    }

    public void getRecordInformation(GetAFL afl)  throws IOException  {
        addTextSeparation();
        log("[Step 6] Send READ RECORD with 0 to find out where the record is");
        addText("[Step 6] Send READ RECORD with 0 to find out where the record is");

        for (AflRecord record : afl.records) {
            int sfi = record.sfi;
            int begin = record.recordNumberBegin;
            byte[] cmd = Iso7816Commands.readRecord(begin, sfi);
            byte[] recv = transceive(cmd);
        }

    }

    private void log(HashMap<RecvTag, byte[]> parsed) {
        for (Map.Entry<RecvTag, byte[]> entry : parsed.entrySet()) {
            log("" + NumUtil.hex2String(entry.getKey().key) + " = " + NumUtil.toHexString(entry.getValue()));
        }
    }

    private void addText(HashMap<RecvTag, byte[]> parsed) {
        for (Map.Entry<RecvTag, byte[]> entry : parsed.entrySet()) {
            addText(NumUtil.hex2String(entry.getKey().key), NumUtil.toHexString(entry.getValue()));
        }
    }


    private void readMasterFileByIdentifier() {
        log("Select Master File ");
        try {
            byte[] cmd = Iso7816Commands.selectMasterFileByIdentifier();
            byte[] recv = transceive(cmd);
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
                byte[] recv = transceive(cmd);
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
        CardResponse res = new CardResponse();
        try {
            byte[] resp = transceive(command.getBytes());
            byte[] data = new byte[resp.length - 2];
            for (int i = 0; i < data.length; i++) {
                data[i] = resp[i];
            }
            res.setData(data);
            StatusWord sw = new StatusWord(resp[resp.length - 2], resp[resp.length - 1]);
            res.setStatusWord(sw);
        } catch (IOException e) {
            throw new CardException(e);
        }
        return res;
    }

    protected byte[] transceive(String hexstr) throws IOException {
        String[] hexbytes = hexstr.split("\\s");
        byte[] bytes = new byte[hexbytes.length];
        for (int i = 0; i < hexbytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hexbytes[i], 16);
        }
        return transceive(bytes);
    }

    protected byte[] transceive(byte[] bytes) throws IOException {
        log("Send: " + SharedUtils.byte2Hex(bytes));
        byte[] recv = tagcomm.transceive(bytes);
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
        return recv;
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
                    byte[] recv = transceive(String.format("00 B2 %02x EC 00", i));
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