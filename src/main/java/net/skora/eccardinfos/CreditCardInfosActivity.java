/*  This file is part of EC/Kreditkarten-Infos.

    EC/Kreditkarten-Infos is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    EC/Kreditkarten-Infos is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with EC/Kreditkarten-Infos.  If not, see <http://www.gnu.org/licenses/>.
*/
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import net.skora.eccardinfos.error.Err;
import net.skora.eccardinfos.error.Errors;
import net.skora.eccardinfos.iso7816.Iso7816Commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * http://blog.saush.com/2006/09/08/getting-information-from-an-emv-chip-card/
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
        adpater.add(key + " : " + value);
    }
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        log("Tag detected!");
        adpater.clear();


        byte[] id = tag.getId();
        addText("Tag Id" ,SharedUtils.byte2Hex(id));

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

            // [Step 1] Select 1PAY.SYS.DDF01 to get the PSE directory
            addText("[Step 1] Select 1PAY.SYS.DDF01 to get the PSE directory");
            byte[] recv = transceive("00 A4 04 00 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31");

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