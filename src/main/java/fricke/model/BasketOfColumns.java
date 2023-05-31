package fricke.model;

import lombok.Getter;

@Getter
public class BasketOfColumns {

    private String OLORDT;
    private String OLORDS;
    private String OLCUNO;
    private String OLORNO;
    private String OLPRDC;
    private String OLDESC;
    private String OLOQTY;

    private String OLSALP;
    private String OLSCPR;
    private String OLITET;
    private String OLCOSP;
    private String OLFOCC;
    private String NACOUN;

    private String NANAME;
    private String OHEXR3;
    private String OHPCUR;
    private String OHODAT;
    private String PGPGRP;

    public BasketOfColumns(String OLORDT, String OLORDS, String OLCUNO, String OLORNO, String OLPRDC, String OLDESC, String OLOQTY) {
        this.OLORDT = OLORDT;
        this.OLORDS = OLORDS;
        this.OLCUNO = OLCUNO;
        this.OLORNO = OLORNO;
        this.OLPRDC = OLPRDC;
        this.OLDESC = OLDESC;
        this.OLOQTY = OLOQTY;
    }

    public void setBasketOfColumns(String OLSALP, String OLSCPR, String OLITET, String OLCOSP, String OLFOCC, String NACOUN) {
        this.OLSALP = OLSALP;
        this.OLSCPR = OLSCPR;
        this.OLITET = OLITET;
        this.OLCOSP = OLCOSP;
        this.OLFOCC = OLFOCC;
        this.NACOUN = NACOUN;
    }

    public void setBasketOfColumns(String NANAME, String OHEXR3, String OHPCUR, String OHODAT, String PGPGRP) {
        this.NANAME = NANAME;
        this.OHEXR3 = OHEXR3;
        this.OHPCUR = OHPCUR;
        this.OHODAT = OHODAT;
        this.PGPGRP = PGPGRP;
    }
}
