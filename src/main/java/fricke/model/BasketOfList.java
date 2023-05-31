package fricke.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
@Getter
public class BasketOfList {
    private List<String> OLORDT = new ArrayList<>();
    private List<String> OLORDS = new ArrayList<>();
    private List<String> OLCUNO = new ArrayList<>();
    private List<String> OLORNO = new ArrayList<>();
    private List<String> OLPRDC = new ArrayList<>();
    private List<String> OLDESC = new ArrayList<>();
    private List<String> OLOQTY = new ArrayList<>();

    private List<String> OLSALP = new ArrayList<>();
    private List<String> OLSCPR = new ArrayList<>();
    private List<String> OLITET = new ArrayList<>();
    private List<String> OLCOSP = new ArrayList<>();
    private List<String> OLFOCC = new ArrayList<>();
    private List<String> NACOUN = new ArrayList<>();

    private List<String> NANAME = new ArrayList<>();
    private List<String> OHEXR3 = new ArrayList<>();
    private List<String> OHPCUR = new ArrayList<>();
    private List<String> OHODAT = new ArrayList<>();
    private List<String> PGPGRP = new ArrayList<>();
    private List<String> CTOTIC = new ArrayList<>();

    public void add(String OLORDT, String OLORDS, String OLCUNO, String OLORNO, String OLPRDC, String OLDESC, String OLOQTY) {
        this.OLORDT.add(OLORDT);
        this.OLORDS.add(OLORDS);
        this.OLCUNO.add(OLCUNO);
        this.OLORNO.add(OLORNO);
        this.OLPRDC.add(OLPRDC);
        this.OLDESC.add(OLDESC);
        this.OLOQTY.add(OLOQTY);
    }

    public void add(String OLSALP, String OLSCPR, String OLITET, String OLCOSP, String OLFOCC, String NACOUN) {
        this.OLSALP.add(OLSALP);
        this.OLSCPR.add(OLSCPR);
        this.OLITET.add(OLITET);
        this.OLCOSP.add(OLCOSP);
        this.OLFOCC.add(OLFOCC);
        this.NACOUN.add(NACOUN);
    }

    public void add(String NANAME, String OHEXR3, String OHPCUR, String OHODAT, String PGPGRP){
        this.NANAME.add(NANAME);
        this.OHEXR3.add(OHEXR3);
        this.OHPCUR.add(OHPCUR);
        this.OHODAT.add(OHODAT);
        this.PGPGRP.add(PGPGRP);
    }
}
