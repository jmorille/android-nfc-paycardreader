package eu.ttbox.ecard.model;

public class AflRecord {

    public int sfi;

    public int recordNumberBegin;

    public int recordNumberEnd;

    public int recordNumberForAuthentification;


    public boolean isAuthentifcation(){
        return recordNumberForAuthentification>0;
    }

    @Override
    public String toString() {
        return "AflRecord{" +
                "sfi=" + sfi +
                ", record[ " + recordNumberBegin +
                "-->  " + recordNumberEnd + "]" +
                ", record authentification=" + recordNumberForAuthentification +
                '}';
    }



}
