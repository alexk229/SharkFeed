
package com.kong.alex.sharkfeed;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("owner")
    @Expose
    private String owner;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("url_t")
    @Expose
    private String urlT;
    @SerializedName("height_t")
    @Expose
    private String heightT;
    @SerializedName("width_t")
    @Expose
    private String widthT;
    @SerializedName("url_m")
    @Expose
    private String urlM;
    @SerializedName("height_m")
    @Expose
    private String heightM;
    @SerializedName("width_m")
    @Expose
    private String widthM;
    @SerializedName("url_l")
    @Expose
    private String urlL;
    @SerializedName("height_l")
    @Expose
    private String heightL;
    @SerializedName("width_l")
    @Expose
    private String widthL;
    @SerializedName("url_o")
    @Expose
    private String urlO;
    @SerializedName("height_o")
    @Expose
    private String heightO;
    @SerializedName("width_o")
    @Expose
    private String widthO;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Photo() {
    }

    /**
     * 
     * @param urlO
     * @param widthL
     * @param heightL
     * @param widthM
     * @param heightM
     * @param urlT
     * @param widthT
     * @param heightT
     * @param heightO
     * @param widthO
     * @param id
     * @param farm
     * @param title
     * @param owner
     * @param urlM
     * @param secret
     * @param urlL
     * @param server
     */
    public Photo(String id, String owner, String secret, String server, Integer farm, String title, Integer ispublic, Integer isfriend, Integer isfamily, String urlT, String heightT, String widthT, String urlM, String heightM, String widthM, String urlL, String heightL, String widthL, String urlO, String heightO, String widthO) {
        super();
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.urlT = urlT;
        this.heightT = heightT;
        this.widthT = widthT;
        this.urlM = urlM;
        this.heightM = heightM;
        this.widthM = widthM;
        this.urlL = urlL;
        this.heightL = heightL;
        this.widthL = widthL;
        this.urlO = urlO;
        this.heightO = heightO;
        this.widthO = widthO;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlT() {
        return urlT;
    }

    public void setUrlT(String urlT) {
        this.urlT = urlT;
    }

    public String getHeightT() {
        return heightT;
    }

    public void setHeightT(String heightT) {
        this.heightT = heightT;
    }

    public String getWidthT() {
        return widthT;
    }

    public void setWidthT(String widthT) {
        this.widthT = widthT;
    }

    public String getUrlM() {
        return urlM;
    }

    public void setUrlM(String urlM) {
        this.urlM = urlM;
    }

    public String getHeightM() {
        return heightM;
    }

    public void setHeightM(String heightM) {
        this.heightM = heightM;
    }

    public String getWidthM() {
        return widthM;
    }

    public void setWidthM(String widthM) {
        this.widthM = widthM;
    }

    public String getUrlL() {
        return urlL;
    }

    public void setUrlL(String urlL) {
        this.urlL = urlL;
    }

    public String getHeightL() {
        return heightL;
    }

    public void setHeightL(String heightL) {
        this.heightL = heightL;
    }

    public String getWidthL() {
        return widthL;
    }

    public void setWidthL(String widthL) {
        this.widthL = widthL;
    }

    public String getUrlO() {
        return urlO;
    }

    public void setUrlO(String urlO) {
        this.urlO = urlO;
    }

    public String getHeightO() {
        return heightO;
    }

    public void setHeightO(String heightO) {
        this.heightO = heightO;
    }

    public String getWidthO() {
        return widthO;
    }

    public void setWidthO(String widthO) {
        this.widthO = widthO;
    }

}
