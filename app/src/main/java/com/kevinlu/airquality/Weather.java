
package com.kevinlu.airquality;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("ts")
    @Expose
    private String ts;
    @SerializedName("hu")
    @Expose
    private Integer hu;
    @SerializedName("ic")
    @Expose
    private String ic;
    @SerializedName("pr")
    @Expose
    private Integer pr;
    @SerializedName("tp")
    @Expose
    private Integer tp;
    @SerializedName("wd")
    @Expose
    private Integer wd;
    @SerializedName("ws")
    @Expose
    private Double ws;

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public Integer getHu() {
        return hu;
    }

    public void setHu(Integer hu) {
        this.hu = hu;
    }

    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    public Integer getPr() {
        return pr;
    }

    public void setPr(Integer pr) {
        this.pr = pr;
    }

    public Integer getTp() {
        return tp;
    }

    public void setTp(Integer tp) {
        this.tp = tp;
    }

    public Integer getWd() {
        return wd;
    }

    public void setWd(Integer wd) {
        this.wd = wd;
    }

    public Double getWs() {
        return ws;
    }

    public void setWs(Double ws) {
        this.ws = ws;
    }

}
