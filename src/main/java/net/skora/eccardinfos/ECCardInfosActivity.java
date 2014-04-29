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
import android.widget.Button;
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

public class ECCardInfosActivity extends Activity {
    // Dialogs
    private static final int DIALOG_NFC_NOT_AVAIL = 0;

    private static final String LOGTAG = "ECCardInfosActivity";

    private NfcAdapter nfc;
    private Tag tag;
    private IsoDep tagcomm;
    //private String[][] nfctechfilter = new String[][]{new String[]{NfcA.class.getName()}};
    private String[][] nfctechfilter = new String[][]{new String[]{IsoDep.class.getName()}};
    private PendingIntent nfcintent;

    private TextView nfcid;
    private TextView cardtype;
    private TextView blz;
    private TextView accountTextView;
    private TextView amountTextView;
    private TextView cardNumberTextView;
    private TextView kknr;
    private TextView activatedText;
    private TextView expirationTextView;
    private TableLayout aidtable;
    private Button transactions;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        nfcid = (TextView) findViewById(R.id.display_nfcid);
        cardtype = (TextView) findViewById(R.id.display_cardtype);
        blz = (TextView) findViewById(R.id.display_blz);
        accountTextView = (TextView) findViewById(R.id.display_account);
        amountTextView = (TextView) findViewById(R.id.display_amount);
        cardNumberTextView = (TextView) findViewById(R.id.display_cardnumber);
        kknr = (TextView) findViewById(R.id.display_kknr);
        activatedText = (TextView) findViewById(R.id.display_activation);
        expirationTextView = (TextView) findViewById(R.id.display_expiration);
        aidtable = (TableLayout) findViewById(R.id.table_features);
        transactions = (Button) findViewById(R.id.button_transactions);

        transactions.setOnClickListener(transactions_click);

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

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        log("Tag detected!");

        nfcid.setText("-");
        cardtype.setText("-");
        blz.setText("-");
        accountTextView.setText("-");
        amountTextView.setText("-");
        cardNumberTextView.setText("-");
        kknr.setText("-");
        activatedText.setText("-");
        expirationTextView.setText("-");
        aidtable.removeAllViews();
        transactions.setVisibility(View.GONE);

        byte[] id = tag.getId();
        nfcid.setText(SharedUtils.byte2Hex(id));

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
            // Switch to DF_BOERSE
            byte[] recv = transceive("00 A4 04 0C 09 D2 76 00 00 25 45 50 02 00");
            if (recv.length >= 2 && recv[0] == (byte) 0x90 && recv[1] == 0) {
                cardtype.setText("GeldKarte");
                readGeldKarte();
                return;
            } else if (new String(transceive("00 A4 04 0C 07 A0 00 00 00 04 10 10"), "ISO-8859-1").contains("MasterCard")) {    // MasterCard
                cardtype.setText("MasterCard");
                readCreditCard();

                // Now following: AIDs I never tried until now - perhaps they work, possibly not
            } else if (transceive("00 A4 04 0C 07 A0 00 00 00 03 10 10").length > 2) {
                cardtype.setText("Visa");
                readCreditCard();
            } else if (transceive("00 A4 04 0C 07 A0 00 00 00 04 99 99").length > 2) {
                cardtype.setText("MasterCard");
                readCreditCard();
            } else if (transceive("00 A4 04 0C 07 A0 00 00 00 04 30 60").length > 2) {
                cardtype.setText("Maestro");
                readCreditCard();
            } else if (transceive("00 A4 04 0C 07 A0 00 00 00 04 60 00").length > 2) {
                cardtype.setText("Cirrus");
                readCreditCard();
            } else if (transceive("00 A4 04 0C 07 A0 00 00 00 03 20 10").length > 2) {
                cardtype.setText("Visa Electron");
                readCreditCard();
            } else if (transceive("00 A4 04 0C 07 A0 00 00 00 03 20 20").length > 2) {
                cardtype.setText("Visa V Pay");
                readCreditCard();
            } else if (transceive("00 A4 04 0C 07 A0 00 00 00 03 80 10").length > 2) {
                cardtype.setText("Visa V Pay");
                readCreditCard();
            } else {
                toastError(getResources().getText(R.string.error_card_unknown));
                // readCreditCard();
                // readMasterFile();
                // readMasterFileByIdentifier();

               selectCommand();
                //readAllRecord();
                readVisa();

                log("Byte 04 => " + SharedUtils.convertByteAsBitString( (byte)0x04));
                log("Byte 0C => " + SharedUtils.convertByteAsBitString( (byte)0x0C));
            }

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


    private void selectCommand() {
        log("Select Commande File ");
        try {
            byte[] cmd = {0x00, (byte) 0xA4, 0x04, 0x00, 0x07, (byte) 0xA0, 0x00, 0x00, 0x00, 0x42, 0x10, 0x10};
            byte[] recv = transceive(cmd);
            log(" ==> " + SharedUtils.getDataAsString(recv));

          //  readAllRecord(0);
        } catch (IOException e) {
            Log.e(LOGTAG, "Error transmiting : " + e.getMessage());
        }

    }

    private void readVisa() {
        try {
            log("Read Visa ");
            // http://blog.saush.com/2006/09/08/getting-information-from-an-emv-chip-card/
            byte[] cmd = new byte[]{0x00, (byte) 0xA4, 0x04, 0x00, (byte) 0xA0, 0x00, 0x00, 0x00, 0x04};
            byte[] recv = transceive(cmd);
           // byte[] recv =transceive("00 A4 04 00 A0 00 00 00 04");

        } catch (IOException e) {
            Log.e(LOGTAG, "Error transmiting : " + e.getMessage());
        }
        log("Read Visa Payload ");
        byte[] readPayLogVisa = {0x00, (byte) 0xB2, 0x01, (byte) 0x8C, 0x00};
        for (int i = 1; i <= 20; i++) {
            readPayLogVisa[2] = (byte) i;
            try {
                byte[] recvPayLoad = transceive(readPayLogVisa);

            } catch (IOException e) {
                Log.e(LOGTAG, "Error transmiting : " + e.getMessage());
            }
        }

    }

    private void readMasterCard() {
        try {
            log("Read Master Card ");
            byte[] cmd = {0x00, (byte) 0xB2, 0x01, 0x14, 0x00};
            byte[] recv = transceive(cmd);
        } catch (IOException e) {
            Log.e(LOGTAG, "Error transmiting : " + e.getMessage());
        }
        log("Read Master Card Payload ");
        byte readPayLogMC[] = {0x00, (byte) 0xB2, 0x01, 0x5C, 0x00};
        for (int i = 1; i <= 20; i++) {
            readPayLogMC[2] = (byte) i;
            try {
                byte[] recvPayLoad = transceive(readPayLogMC);

            } catch (IOException e) {
                Log.e(LOGTAG, "Error transmiting : " + e.getMessage());
            }
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

    private void readGeldKarte() {
        try {
            // Read EF_MONTANT
            byte[] recv = transceive("00 B2 01 C4 00");
            amountTextView.setText(SharedUtils.formatBCDAmount(recv));

            // Read EF_ID
            recv = transceive("00 B2 01 BC 00");
            // Kartennr.
            cardNumberTextView.setText(SharedUtils.byte2Hex(Arrays.copyOfRange(recv, 4, 9)).replace(" ", ""));
            //Aktiviert am
            activatedText.setText(SharedUtils.byte2Hex(Arrays.copyOfRange(recv, 14, 15)).replace(" ", "") + "." + SharedUtils.byte2Hex(Arrays.copyOfRange(recv, 13, 14)).replace(" ", "") + ".20" + SharedUtils.byte2Hex(Arrays.copyOfRange(recv, 12, 13)).replace(" ", ""));
            //Verfällt am
            expirationTextView.setText(SharedUtils.byte2Hex(Arrays.copyOfRange(recv, 11, 12)).replace(" ", "") + "/" + SharedUtils.byte2Hex(Arrays.copyOfRange(recv, 10, 11)).replace(" ", ""));

            // EF_BOURSE
            recv = transceive("00 B2 01 CC 00");
            // BLZ
            blz.setText(SharedUtils.byte2Hex(Arrays.copyOfRange(recv, 1, 5)).replace(" ", ""));
            // N ° de compte.
            accountTextView.setText(SharedUtils.byte2Hex(Arrays.copyOfRange(recv, 5, 10)).replace(" ", ""));

            transactions.setVisibility(View.VISIBLE);

            //		recv = transceive("00 A4 04 00 0E 31 50 41 59 2E 53 59 53 2E 44 44 46 30 31 00");
            //		int len = recv.length;
            //		if (len >= 2 && recv[len - 2] == 0x90 && recv[len - 1] == 0) {
            //			// PSE supported
            //			addAIDRow(getResources().getText(R.string.ui_pse), getResources().getText(R.string.text_yes));
            //		} else {
            //			// no PSE
            //			addAIDRow(getResources().getText(R.string.ui_pse), getResources().getText(R.string.text_no));
            //		}
            //		recv = transceive("00 A4 04 0C 07 F0 00 00 01 57 10 21");	// Lastschrift AID
            //		recv = transceive("00 A4 04 0C 0A A0 00 00 03 59 10 10 02 80 01");	// EC AID
        } catch (IOException e) {
            toastError(getResources().getText(R.string.error_nfc_comm_cont) + (e.getMessage() != null ? e.getMessage() : "-"));
        }
    }

    private void readCreditCard() {
        try {
            byte[] recv = transceive("00 B2 01 0C 00");
            kknr.setText(new String(Arrays.copyOfRange(recv, 29, 45), "ISO-8859-1"));
            expirationTextView.setText(
                    new String(Arrays.copyOfRange(recv, 75, 77), "ISO-8859-1")
                            .concat("/")
                            .concat(new String(Arrays.copyOfRange(recv, 73, 75), "ISO-8859-1"))
            );
        } catch (IOException e) {
            toastError(getResources().getText(R.string.error_nfc_comm_cont) + (e.getMessage() != null ? e.getMessage() : "-"));
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
                                ECCardInfosActivity.this.finish();
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
            Intent intent = new Intent(ECCardInfosActivity.this, TransactionsActivity.class);
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

    private void addAIDRow(CharSequence left, CharSequence right) {
        TextView t1 = new TextView(this);
        t1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        t1.setPadding(0, 0, (int) (getResources().getDisplayMetrics().density * 10 + 0.5f), 0);
        t1.setTextAppearance(this, android.R.attr.textAppearanceMedium);
        t1.setText(left);

        TextView t2 = new TextView(this);
        t2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        t2.setText(right);

        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        tr.addView(t1);
        tr.addView(t2);

        TableLayout t = (TableLayout) findViewById(R.id.table_features);
        t.addView(tr, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    }

    protected void log(String msg) {
        Log.d(LOGTAG, msg);
    }

    protected void toastError(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}