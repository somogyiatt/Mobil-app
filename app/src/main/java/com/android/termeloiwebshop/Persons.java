package com.android.termeloiwebshop;

import java.util.ArrayList;

public class Persons {

    private String nev;
    private String szuletesiDatum;
    private String iranyitoszam;
    private String varos;
    private String hazszam;
    private String email;
    private String tipus;
    private ArrayList<ShoppingItem> kosar;

    public Persons() {
    }

    public Persons(String nev, String szuletesiDatum, String iranyitoszam, String varos, String hazszam, String email, String tipus) {
        this.nev = nev;
        this.szuletesiDatum = szuletesiDatum;
        this.iranyitoszam = iranyitoszam;
        this.varos = varos;
        this.hazszam = hazszam;
        this.email = email;
        this.tipus = tipus;
        this.kosar = new ArrayList<ShoppingItem>();
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getSzuletesiDatum() {
        return szuletesiDatum;
    }

    public void setSzuletesiDatum(String szuletesiDatum) {
        this.szuletesiDatum = szuletesiDatum;
    }

    public String getIranyitoszam() {
        return iranyitoszam;
    }

    public void setIranyitoszam(String iranyitoszam) {
        this.iranyitoszam = iranyitoszam;
    }

    public String getVaros() {
        return varos;
    }

    public void setVaros(String varos) {
        this.varos = varos;
    }

    public String getHazszam() {
        return hazszam;
    }

    public void setHazszam(String hazszam) {
        this.hazszam = hazszam;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }


    public ArrayList<ShoppingItem> getKosar() {
        return kosar;
    }

    public void setKosar(ArrayList<ShoppingItem> kosar) {
        this.kosar = kosar;
    }
}
