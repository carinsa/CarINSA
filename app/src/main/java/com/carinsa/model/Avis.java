package com.carinsa.model;

public class Avis {
    private int complet=0;
    private int libre=0;
    private int ferme=0;
    private int ouvert=0;

    private boolean avisComplet=false;
    private boolean avisLibre=false;
    private boolean avisFerme=false;
    private boolean avisOuvert=false;

    public Avis(){

    }

    public Avis(int complet,int libre,int ferme,int ouvert,boolean avisComplet,boolean avisLibre,boolean avisFerme,boolean avisOuvert){
        this.complet=complet;
        this.libre=libre;
        this.ferme=ferme;
        this.ouvert=ouvert;
        this.avisComplet=avisComplet;
        this.avisLibre=avisLibre;
        this.avisFerme=avisFerme;
        this.avisOuvert=avisOuvert;
    }

    public void setOuvert(int ouvert) {
        this.ouvert = ouvert;
    }

    public void setFerme(int ferme) {
        this.ferme = ferme;
    }

    public void setLibre(int libre) {
        this.libre = libre;
    }

    public void setComplet(int complet) {
        this.complet = complet;
    }

    public void setAvisOuvert(boolean avisOuvert) {
        this.avisOuvert = avisOuvert;
    }

    public void setAvisFerme(boolean avisFerme) {
        this.avisFerme = avisFerme;
    }

    public void setAvisLibre(boolean avisLibre) {
        this.avisLibre = avisLibre;
    }

    public void setAvisComplet(boolean avisComplet) {
        this.avisComplet = avisComplet;
    }

    public int getComplet() {
        return complet;
    }

    public int getLibre() {
        return libre;
    }

    public int getOuvert() {
        return ouvert;
    }

    public int getFerme() {
        return ferme;
    }

    public boolean isAvisOuvert() {
        return avisOuvert;
    }

    public boolean isAvisLibre() {
        return avisLibre;
    }

    public boolean isAvisFerme() {
        return avisFerme;
    }

    public boolean isAvisComplet() {
        return avisComplet;
    }
}
